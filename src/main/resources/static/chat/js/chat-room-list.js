const memberId = document.getElementById("chat-room-container").getAttribute("data-member-id");

function fetchChatRooms() {
    axios.get(`/api/chat/chat-rooms`)
        .then((response) => {
            const chatRooms = response.data;
            const chatRoomsContainer = document.getElementById("chat-room-container");

            if (chatRooms.length === 0) {
                chatRoomsContainer.innerHTML = "<p>참여하고 있는 채팅방이 없습니다.</p>";
            } else {
                chatRooms.forEach(room => {
                    displayChatRoom(room);
                });
            }
        })
        .catch((error) => {
            console.error("Error fetching chat rooms:", error);
        });
}

function displayChatRoom(room) {
    const chatRoomsContainer = document.getElementById("chat-room-container");

    const chatRoomElement = document.createElement("div");
    chatRoomElement.classList.add("chat-room");

    chatRoomElement.onclick = () => {
        window.location.href = `/chat/chat-room/${room.chatRoomId}/${memberId}`;
    };

    const chatRoomContentElement = document.createElement("div");
    chatRoomContentElement.classList.add("chat-content");

    const opponentNameElement = document.createElement("h5");
    opponentNameElement.textContent = room.opponentName;

    const lastMessageElement = document.createElement("p");
    if (room.lastModifiedDate) {
        lastMessageElement.textContent = room.message;
    } else {
        lastMessageElement.style.visibility = "hidden";
    }

    const chatRoomSideContentElement = document.createElement("div");
    chatRoomSideContentElement.classList.add("chat-side-content");

    const chatRoomSideContentDetailElement = document.createElement("div");
    chatRoomSideContentDetailElement.classList.add("chat-side-content-detail");

    const unreadMessagesElement = document.createElement("span");
    unreadMessagesElement.classList.add("badge");
    if (room.unreadMessagesCount > 0) {
        unreadMessagesElement.textContent = room.unreadMessagesCount;
    } else {
        unreadMessagesElement.style.display = "none";
    }

    const lastModifiedDateElement = document.createElement("p");
    if (room.lastModifiedDate) {
        lastModifiedDateElement.textContent = formatDate(room.lastModifiedDate);
    } else {
        lastModifiedDateElement.style.visibility = "hidden";
    }

    const leaveButton = document.createElement("button");
    leaveButton.classList.add("leaveBtn", "bi-box-arrow-right");

    leaveButton.addEventListener("click", (e) => {
        e.stopPropagation();
        const isConfirmed = confirm("채팅방을 나가시겠습니까?");
        if (isConfirmed) {
            leaveChatRoom(room.chatRoomId);
        }
    });

    chatRoomContentElement.appendChild(opponentNameElement);
    chatRoomContentElement.appendChild(lastMessageElement);

    chatRoomSideContentDetailElement.appendChild(unreadMessagesElement);
    chatRoomSideContentDetailElement.appendChild(lastModifiedDateElement);

    chatRoomSideContentElement.appendChild(chatRoomSideContentDetailElement);
    chatRoomSideContentElement.appendChild(leaveButton);

    chatRoomElement.appendChild(chatRoomContentElement);
    chatRoomElement.appendChild(chatRoomSideContentElement);

    chatRoomsContainer.appendChild(chatRoomElement);
}

function formatDate(dateString) {
    if (!dateString) {
        return "";
    }

    const date = new Date(dateString);
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');

    return `${month}-${day} ${hours}:${minutes}`;
}

function leaveChatRoom(chatRoomId) {
    axios.post(`/api/chat/leave-room`, { chatRoomId, memberId })
        .then(response => {
            alert("채팅방을 나갔습니다.");
            fetchChatRooms();
        })
        .catch(error => {
            console.error("Error: ", error)
            alert("채팅방을 나가는 데 실패했습니다. 다시 시도해 주세요");
        });
}

document.addEventListener("DOMContentLoaded", fetchChatRooms);