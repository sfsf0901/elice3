let unreadCount = 0;
let notificationList, notificationBadge;

document.addEventListener("DOMContentLoaded", () => {
    checkLogin();
});

function checkLogin() {
    axios.get('/api/notification/is-login')
        .then(response => {
            if (response.data.isLogin) {
                console.log("User is logged in, establishing SSE connection...");
                notificationList = document.getElementById("notification-list");
                notificationBadge = document.getElementById("notification-badge");
                fetchUnreadNotifications();
                connectSSE();
            } else {
                console.log("User is not logged in, SSE connection not established.");
            }
        })
        .catch(error => {
            console.error('Error checking login status:');
        });
}

function fetchUnreadNotifications() {
    axios.get('/api/notification/unread')
        .then(response => {
            if (response.data.length === 0) {
                displayEmptyNotificationMessage();
            }
            response.data.forEach(notification => {
                displayNotification(notification);
                unreadCount++;
            });
            updateBadge();
        })
        .catch(error => {
            console.error("'Error fetching unread notifications:");
        });
}

function connectSSE() {
    const eventSource = new EventSource("/api/notification");

    eventSource.onopen = (e) => {
        console.log("'Connection established");
    };

    eventSource.onmessage = (e) => {
        const notification = JSON.parse(e.data);
        console.log("Received notification:", notification);
        displayNotification(notification)
        unreadCount++;
        updateBadge();
    };

    eventSource.onerror = (e) => {
        console.error("Error occurred while receiving SSE events:");
        setTimeout(() => {
            eventSource.close();
            connectSSE();
        }, 5000);
    };
}

function displayNotification(notification) {
    const notificationItem = document.createElement("li");
    notificationItem.classList.add("dropdown-item");

    const notificationContent = document.createElement("div");
    notificationContent.classList.add("dropdown-content");

    const contentMessage = document.createElement("div");
    contentMessage.classList.add("content-massage");

    const maxLength = 10;
    let message = notification.message;
    if (message.length > maxLength) {
        message = message.substring(0, maxLength) + '...';
    }
    contentMessage.textContent = message;

    const sideContent = document.createElement("div");
    sideContent.classList.add("dropdown-side-content");

    const contentSender = document.createElement("span");
    contentSender.classList.add("content-sender");
    contentSender.textContent = notification.senderName;

    const contentTime = document.createElement("span");
    contentTime.classList.add("content-time");
    contentTime.textContent = time(notification.createdDate);

    notificationItem.onclick = () => {
        window.location.href = `/chat/chat-room/${notification.chatRoomId}/${notification.receiverId}`;
    };

    const readButton = document.createElement("button");
    readButton.classList.add("btn", "'btn-sm", "bi-check-lg");
    readButton.style.visibility  = "hidden";
    readButton.onclick = function(e) {
        e.stopPropagation();
        markAsRead(notification.notificationId, notificationItem);
    };

    sideContent.appendChild(contentSender);
    sideContent.appendChild(contentTime);

    notificationContent.appendChild(contentMessage);
    notificationContent.appendChild(sideContent);

    notificationItem.appendChild(notificationContent);
    notificationItem.appendChild(readButton);

    notificationItem.onmouseover = function() {
        readButton.style.visibility = "visible";
    };
    notificationItem.onmouseout = function() {
        readButton.style.visibility = "hidden";
    };

    notificationList.insertBefore(notificationItem, notificationList.firstChild);

    if (notificationList.children.length > 4) {
        notificationList.removeChild(notificationList.lastChild);
    }
}

function time(date){
  const start = new Date(date);
  const end = new Date();

  const seconds = Math.floor((end.getTime() - start.getTime()) / 1000);
  if (seconds < 60) return '방금 전';

  const minutes = seconds / 60;
  if (minutes < 60) return `${Math.floor(minutes)}분 전`;

  const hours = minutes / 60;
  if (hours < 24) return `${Math.floor(hours)}시간 전`;

  const days = hours / 24;
  if (days < 7) return `${Math.floor(days)}일 전`;

  return `${start.toLocaleDateString()}`;
}

function displayEmptyNotificationMessage() {
    const emptyMessage = document.createElement("div");
    emptyMessage.classList.add("empty-message");
    emptyMessage.style.minHeight = "20vh";
    emptyMessage.classList.add("d-flex", "align-items-center", "justify-content-center", "text-center");
    emptyMessage.innerHTML = `
        <p class="mt-2" style="font-size: 0.8rem;">새로운 알림이 존재하지 않습니다.</p>
    `;

    notificationList.innerHTML = '';
    notificationList.appendChild(emptyMessage);
}

function markAsRead(notificationId, notificationItem) {
    axios.put(`/api/notification/read/${notificationId}`)
        .then(response => {
            unreadCount--;
            if (unreadCount <= 0) {
                notificationBadge.style.display = "none";
                displayEmptyNotificationMessage();
            } else {
                updateBadge();
            }
            notificationList.removeChild(notificationItem);
        })
        .catch(error => {
            console.error("Error marking notification as read");
        });
}

function updateBadge() {
    if (unreadCount > 0) {
        notificationBadge.style.display = "inline-block";
        notificationBadge.textContent = unreadCount;
    } else {
        notificationBadge.style.display = "none";
    }
}