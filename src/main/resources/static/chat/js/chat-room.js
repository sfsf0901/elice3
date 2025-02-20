// 채팅방 ID와 사용자 ID 설정.
const chatRoomId = document.getElementById("messages").getAttribute("data-chat-room-id");
const memberId = document.getElementById("messages").getAttribute("data-member-id");

window.onload = function() {
    // 초기 메시지 로드를 위해 SSE(EventSource) 연결 설정
    const eventSource = new EventSource(`/api/chat/${chatRoomId}/${memberId}`);

    // SSE 연결 성공 - 메시지 처리
    eventSource.onmessage = (e) => {
        const message = JSON.parse(e.data);
        console.log("받은 메시지:", message);
        displayMessage(message);

        // 첫 번째 메시지 수신 후 WebSocket 연결
        if (!stompClient || !stompClient.connected) {
            connectWebSocket();
        }
    };

    // SSE 에러 처리
    eventSource.onerror = function(e) {
        console.error("SSE 연결 오류", e);
        console.error("이벤트 타입:", e.type);
        console.error("현재 상태:", e.target.readyState);
        eventSource.close();
    };
};
// 초기 메시지 표시
function displayMessage(message) {
    const messageBox = document.getElementById("messages");

    // 메시지 요소 생성
    const messageElement = document.createElement("div");
    messageElement.classList.add("message");

    // 본인의 메시지인지 구분하여 클래스 추가
    if (message.senderId === memberId) {
        messageElement.classList.add("my-message");
    } else {
        messageElement.classList.add("other-message");
    }

    messageElement.textContent = message.message;
    messageBox.appendChild(messageElement);

    messageBox.scrollTop = messageBox.scrollHeight; // 자동 스크롤
}

// WebSocket 연결을 설정하여 실시간 채팅 처리
let stompClient = null;

function connectWebSocket() {
    const socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('WebSocket 연결 성공: ' + frame);

        // WebSocket을 통해 채팅방에 대한 메시지 수신
        stompClient.subscribe(`/topic/${chatRoomId}`, (response) => {
            const message = JSON.parse(response.body);
            displayMessage(message);
        });
    }, (error) =>
        console.error('WebSocket 연결 실패', error)
    );
}

// 메시지 전송
function sendMessage() {
    const messageContent = document.getElementById("messageInput").value;
    if (messageContent.trim() !== "") {
        const message = {
            message: messageContent,
            senderId: memberId,
            chatRoomId: chatRoomId
        };

        if (stompClient && stompClient.connected) {
            stompClient.send(`/app/send-message/${chatRoomId}`, {}, JSON.stringify(message));
        }
        document.getElementById("messageInput").value = '';  // 입력 필드 초기화
    }
}

// 전송 버튼 클릭 시 메시지 전송
document.getElementById("sendButton").onclick = sendMessage;