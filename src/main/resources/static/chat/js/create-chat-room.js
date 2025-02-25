document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("checkChatRoomBtn").addEventListener("click", (e) => {
        const doctorUserId = e.target.getAttribute("data-doctor-user-id");

        const isConfirmed = confirm("상담을 시작하시겠습니까?");

        if (!isConfirmed) {
            return;
        }

        let requestPayload = {
            memberIds: [doctorUserId]
        };

        axios.post('/api/chat/check-chat-room', requestPayload)
            .then(response => {
                if (response.data) {
                    const chatRoomId = response.data.chatRoomId;
                    const memberId = response.data.memberId;
                    window.location.href = "/chat/chat-room/"  + chatRoomId + "/" + memberId;
                } else {
                    alert("상담을 연결하는데 오류가 발생하였습니다. 다시 시도해 주세요");
                }
            })
            .catch(error =>
                console.error("Error: ", error)
            );
    });
});