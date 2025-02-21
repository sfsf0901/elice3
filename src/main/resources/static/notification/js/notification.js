// 채팅방 ID와 사용자 ID 설정
// const chatRoomId = document.getElementById("messages").getAttribute("data-chat-room-id");
// const memberId = document.getElementById("messages").getAttribute("data-member-id");
const chatRoomId = 3;
const memberId = 1;

let unreadCount = 0;

window.onload = function() {
    loadUnreadNotifications(memberId);
};

// 서버에서 확인하지 않은 알림 메세지 로드
function loadUnreadNotifications(memberId) {
    axios.get(`/api/notification/unread/${memberId}`)
        .then(response => {
            unreadCount = 0;
            response.data.forEach(notification => {
                showNotification(notification);
            });

            // 읽지 않은 알림이 있을 경우 뱃지 표시
            const notificationBadge = document.getElementById('notification-badge');
            if (unreadCount > 0) {
                notificationBadge.style.display = 'inline';
                notificationBadge.textContent = unreadCount;
            } else {
                notificationBadge.style.display = 'none';
            }
        })
        .catch(error => {
            console.error('알림 로드 실패', error);
        });

    // WebSocket 연결
    connectWebSocket();
};

// 알림 읽음 처리 API 호출
function markNotificationAsRead(notificationId) {
    axios.put(`/api/notification/read/${notificationId}`)
        .then(response => {
            if (response.status === 200) {
                console.log('Notification marked as read');
                loadUnreadNotifications(memberId);
            }
        })
        .catch(error => console.error('Error marking notification as read:', error));
}

// 알림 항목 표시
function showNotification(notification) {
    const notificationList = document.getElementById('notification-list');

    // 알림 아이템 생성
    const listItem = document.createElement('li');
    listItem.classList.add('dropdown-item');
    listItem.textContent = notification.message;

    // 알림 클릭 시 해당 채팅방으로 이동
    listItem.addEventListener('click', function() {
        window.location.href = `/chat/chat-room/${notification.chatRoomId}/${notification.receiverId}`;
    });

    // 알림 읽음 처리 버튼 추가
    const markAsReadButton = document.createElement('button');
    markAsReadButton.textContent = '읽음 처리';
    markAsReadButton.classList.add('btn', 'btn-sm', 'btn-secondary');
    markAsReadButton.addEventListener('click', (e) => {
        e.stopPropagation();
        markNotificationAsRead(notification.notificationId);
    });

    listItem.appendChild(markAsReadButton);

    notificationList.appendChild(listItem);

    unreadCount++;
}

// WebSocket 연결
let stompClient = null;

function connectWebSocket() {
    const socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('WebSocket 연결 성공: ' + frame);

        stompClient.subscribe(`/topic/${chatRoomId}`, (response) => {
            const message = JSON.parse(response.body);
            showNotification(message);
        });

    }, (error) => {
        console.error('WebSocket 연결 실패', error);
        setTimeout(connectWebSocket, 5000);  // 5초 후 재연결 시도
    });
}

// 메시지 전송
function sendMessage() {
    const notification = {
        message: message,
        receiverId: memberId,
        chatRoomId: chatRoomId
    };

    if (stompClient && stompClient.connected) {
        stompClient.send(`/app/send-notification/${chatRoomId}`, {}, JSON.stringify(notification));
    }
}
