// 채팅방 ID와 사용자 ID 설정.
const chatRoomId = document.getElementById("messages").getAttribute("data-chat-room-id");
const memberId = document.getElementById("messages").getAttribute("data-member-id");

window.onload = function() {
    // 서버에서 초기 메시지 로드
    axios.get(`/api/chat/${chatRoomId}/${memberId}`)
        .then(response => {
            // 응답 받은 메시지를 모두 화면에 표시
            response.data.forEach(message => {
                displayMessage(message);
            });
        })
        .catch(error => {
            console.error('초기 메시지 로드 실패', error);
        });

    // WebSocket 연결
    connectWebSocket();
};

// 초기 메시지 표시
function displayMessage(message) {
    const messageBox = document.getElementById("messages");

    const messageElement = document.createElement("div");
    messageElement.classList.add("message");

    if (message.senderId == memberId) {
        messageElement.classList.add("my-message");

        const senderNameElement = document.createElement("span");
        senderNameElement.classList.add("sender-name");
        senderNameElement.textContent = "You";
        senderNameElement.style.display = "none";
        messageElement.appendChild(senderNameElement);

        const messageTextElement = document.createElement("div");
        messageTextElement.classList.add("message-text");
        messageTextElement.textContent = message.message;

        const createdDateElement = document.createElement("span");
        createdDateElement.classList.add("message-time");
        createdDateElement.textContent = formatDate(message.createdDate);

        const messageContentElement = document.createElement("div");
        messageContentElement.classList.add("message-content");
        messageContentElement.appendChild(createdDateElement);
        messageContentElement.appendChild(messageTextElement);

        const deleteButton = document.createElement("button");
        deleteButton.textContent = "삭제";
        deleteButton.classList.add("delete-button");
        deleteButton.addEventListener("click", function () {
            deleteMessage(message.chatMessageId, messageElement);
        });

        messageContentElement.appendChild(deleteButton);
        messageElement.appendChild(messageContentElement);
    } else {
        messageElement.classList.add("other-message");

        const senderNameElement = document.createElement("span");
        senderNameElement.classList.add("sender-name");
        senderNameElement.textContent = `${message.senderId}`;
        messageElement.appendChild(senderNameElement);

        const messageTextElement = document.createElement("div");
        messageTextElement.classList.add("message-text");
        messageTextElement.textContent = message.message;

        const createdDateElement = document.createElement("span");
        createdDateElement.classList.add("message-time");
        createdDateElement.textContent = formatDate(message.createdDate);

        const messageContentElement = document.createElement("div");
        messageContentElement.classList.add("message-content");
        messageContentElement.appendChild(messageTextElement);
        messageContentElement.appendChild(createdDateElement);

        messageElement.appendChild(messageContentElement);
    }

    messageBox.appendChild(messageElement);

    // 자동 스크롤
    messageBox.scrollTop = messageBox.scrollHeight;
}

// 날짜 포맷 함수 (예: "2025-02-21 15:30")
function formatDate(dateString) {
    const date = new Date(dateString);
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');

    return `${month}-${day} ${hours}:${minutes}`;
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

    }, (error) => {
         console.error('WebSocket 연결 실패', error);
         setTimeout(connectWebSocket, 5000);  // 5초 후 재연결 시도
    });
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

// Enter 키로 메시지 전송
document.getElementById("messageInput").addEventListener("keypress", (e) => {
    if (e.key === "Enter") {
        sendMessage();
    }
});

function deleteMessage(chatMessageId, messageElement) {
    axios.delete(`/api/chat/${chatMessageId}`)
        .then(response => {
            if (response.status === 204) {
                messageElement.remove();
                console.log("메시지 삭제 성공!");
            }
        })
        .catch(error => {
            console.error("메시지 삭제 실패", error);
            alert("메시지 삭제에 실패했습니다. 다시 시도해 주세요.");
        });
}

function leaveChatRoom(chatRoomId) {
    axios.post(`/api/chat/leave-room`, { chatRoomId, memberId })
        .then(response => {
            alert("채팅방을 나갔습니다.");
            window.location.href = "/chat/chat-rooms";
        })
        .catch(error => {
            console.error("Error: ", error)
            alert("채팅방을 나가는 데 실패했습니다. 다시 시도해 주세요");
        });
}

document.getElementById("leaveBtn").addEventListener("click", (e) => {
    const chatRoomId = e.target.getAttribute("data-chat-room-id");
    const isConfirmed = confirm("채팅방을 나가시겠습니까?");
    if (!isConfirmed) {
        return;
    }
    leaveChatRoom(chatRoomId);
});

document.getElementById("cancelBtn").addEventListener("click", (e) => {
    window.location.href = "/chat/chat-rooms";
});
