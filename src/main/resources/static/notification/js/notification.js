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

    const maxLength = 10;
    let message = notification.message;
    if (message.length > maxLength) {
        message = message.substring(0, maxLength) + '...';
    }
    notificationItem.textContent = message;

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

    notificationItem.appendChild(readButton);

    notificationItem.onmouseover = function() {
        readButton.style.visibility = "visible";
    };
    notificationItem.onmouseout = function() {
        readButton.style.visibility = "hidden";
    };

    notificationList.insertBefore(notificationItem, notificationList.firstChild);

    if (notificationList.children.length > 10) {
        notificationList.removeChild(notificationList.lastChild);
    }
}

function displayEmptyNotificationMessage() {
    const emptyMessage = document.createElement("div");
    emptyMessage.classList.add("empty-message");
    emptyMessage.style.minHeight = "20vh";
    emptyMessage.classList.add("d-flex", "align-items-center", "justify-content-center", "text-center");
    emptyMessage.innerHTML = `
        <p class="mt-2" style="font-size: 14px;">새로운 알림이 존재하지 않습니다.</p>
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