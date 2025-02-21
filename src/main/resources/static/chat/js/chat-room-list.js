const memberId = document.getElementById("chatRoomsContainer").getAttribute("data-member-id");

function fetchChatRooms() {
    axios.get(`/api/chat/chat-rooms/${memberId}`)
        .then((response) => {
            const chatRooms = response.data;
            const chatRoomsContainer = document.getElementById("chatRoomsContainer");

            if (chatRooms.length === 0) {
                chatRoomsContainer.innerHTML = "<p>참여하고 있는 채팅방이 없습니다.</p>";
            } else {
                chatRoomsContainer.innerHTML = chatRooms.map(room =>
                `
                    <div>
                        <a href="/chat/chat-room/${room.chatRoomId}/${memberId}">
                            <h5>${room.opponentName}</h5>
                            <p>마지막 채팅: ${room.message || "없음"}</p>
                            <p>마지막 채팅 날짜: ${room.lastModifiedDate || "알 수 없음"}</p>
                            <p>안 읽은 메시지: ${room.unreadMessagesCount || 0}</p>
                        </a>
                        <button class="leaveBtn" data-chat-room-id="${room.chatRoomId}">나가기</button>
                    </div>
                `
                ).join("");
            }
            document.querySelectorAll(".leaveBtn").forEach(button => {
                button.addEventListener("click", (e) => {
                    const chatRoomId = e.target.getAttribute("data-chat-room-id");
                    const isConfirmed = confirm("채팅방을 나가시겠습니까?");
                    if (!isConfirmed) {
                        return;
                    }
                    leaveChatRoom(chatRoomId);
                });
            });
        })
        .catch((error) => {
            console.error("Error fetching chat rooms:", error);
        });
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