const chatRoomId = document.getElementById("messages").getAttribute("data-chat-room-id");
const memberId = document.getElementById("messages").getAttribute("data-member-id");
const memberName = document.getElementById("messages").getAttribute("data-member-name");

function fetchChat() {
    axios.get(`/api/chat/${chatRoomId}`)
        .then(response => {
            console.log(response.data);
            response.data.forEach(message => {
                displayChatMessage(message);
            });
        })
        .catch(error => {
            console.log(error.response)
            console.error("Error load chat messages");
        });

    connectWebSocket();
};

function displayChatMessage(message) {
    const messageBox = document.getElementById("messages");

    const messageElement = document.createElement("div");
    messageElement.classList.add("message");

    if (message.senderId == memberId) {
        messageElement.classList.add("my-message");

        const messageTextElement = document.createElement("div");
        messageTextElement.classList.add("message-text");
        messageTextElement.textContent = message.message;

        const TimeElement = document.createElement("div");
        TimeElement.classList.add("time");

        const createdDateElement = document.createElement("span");
        createdDateElement.classList.add("message-time");
        createdDateElement.textContent = formatDate(message.createdDate);

        const messageContentElement = document.createElement("div");
        messageContentElement.classList.add("message-content");

        const deleteButton = document.createElement("button");
        deleteButton.classList.add("delete-button","bi-trash");
        deleteButton.style.visibility = "hidden";

        messageContentElement.addEventListener("click", () => {
            if (deleteButton.style.visibility === "hidden") {
                deleteButton.style.visibility = "visible";
            } else {
                deleteButton.style.visibility = "hidden";
            }
        });

        deleteButton.addEventListener("click", (e) => {
            e.stopPropagation();
            const isConfirmed = confirm("메세지를 삭제하시겠습니까?");
            if (!isConfirmed) {
                return;
            }
            deleteMessage(message.chatMessageId, messageElement);
        });

        TimeElement.appendChild(createdDateElement);

        messageContentElement.appendChild(TimeElement);
        messageContentElement.appendChild(messageTextElement);
        messageContentElement.appendChild(deleteButton);

        messageElement.appendChild(messageContentElement);
    } else {
        messageElement.classList.add("other-message");

        const senderNameElement = document.createElement("span");
        senderNameElement.classList.add("sender-name");
        senderNameElement.textContent = `${message.senderName}`;
        messageElement.appendChild(senderNameElement);

        const messageTextElement = document.createElement("div");
        messageTextElement.classList.add("message-text");
        messageTextElement.textContent = message.message;

        const TimeElement = document.createElement("div");
        TimeElement.classList.add("time");

        const createdDateElement = document.createElement("span");
        createdDateElement.classList.add("message-time");
        createdDateElement.textContent = formatDate(message.createdDate);

        const messageContentElement = document.createElement("div");
        messageContentElement.classList.add("message-content");

        TimeElement.appendChild(createdDateElement);
        messageContentElement.appendChild(messageTextElement);
        messageContentElement.appendChild(TimeElement);

        messageElement.appendChild(messageContentElement);
    }

    messageBox.appendChild(messageElement);

    messageBox.scrollTop = messageBox.scrollHeight;
}

function formatDate(dateString) {
    const date = new Date(dateString);
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');

    return `${month}-${day} ${hours}:${minutes}`;
}

let stompClient = null;
let isWebSocketConnected = false;

function connectWebSocket() {
    const socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);

    stompClient.connect({}, (frame) => {
        console.log("WebSocket Connection Successful: " + frame);
        isWebSocketConnected = true;

        stompClient.subscribe(`/topic/${chatRoomId}`, (response) => {
            const message = JSON.parse(response.body);
            displayChatMessage(message);
        });

        enableMessageActions();

    }, (error) => {
         console.error("WebSocket Connection Failed: ", error);
         setTimeout(connectWebSocket, 5000);  // 5초 후 재연결 시도
    });
}

function sendMessage() {
    const messageContent = document.getElementById("messageInput").value;
    if (messageContent.trim() !== "" && isWebSocketConnected) {
        const message = {
            message: messageContent,
            senderId: memberId,
            senderName: memberName,
            chatRoomId: chatRoomId
        };

        if (stompClient && stompClient.connected) {
            stompClient.send(`/app/send-message/${chatRoomId}`, {}, JSON.stringify(message));
        }
        document.getElementById("messageInput").value = '';
    }
}

document.getElementById("sendButton").onclick = sendMessage;

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
            }
        })
        .catch(error => {
            console.error("Error delete chat message");
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
            console.error("Error leave chat room")
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

function disableMessageActions() {
    document.getElementById("sendButton").disabled = true;
    document.getElementById("messageInput").disabled = true;
}

function enableMessageActions() {
    document.getElementById("sendButton").disabled = false;
    document.getElementById("messageInput").disabled = false;
}

document.addEventListener("DOMContentLoaded", () => {
    fetchChat();
    disableMessageActions();
});