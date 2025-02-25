import api from "/common/js/API.js";
import startChat from "/chat/js/create-chat-room.js";

api.get("counsels/detail", {
  params: {id: location.pathname.split("/").pop()}
}).then(response => {
  renderContent(response.data)
});

api.get("comments", {
  params: {counselId: location.pathname.split("/").pop()}
}).then(response => {
  renderComment(response.data);
  renderPagination(response.data);
})

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

async function renderContent(data) {
  document.getElementById("title").textContent = data.title;
  document.getElementById("content").textContent = data.content;
  document.getElementById("name").textContent = data.name;
  document.getElementById("time").textContent = time(data.createdDate);
  document.getElementById("category").textContent = data.category;
}

async function renderComment(data) {
  const commentList = document.getElementById("comment-list");
  commentList.innerHTML = "";
  const commentData = data.content;
  commentData.forEach(item => {

    const commentDiv = document.createElement("div");
    commentDiv.className = "comment";

    commentDiv.innerHTML = `
      <div class="comment">
        <div class="comment-meta">${item.name} | ${time(item.createdDate)}
            <button id="check-chat-room-btn"
                class="btn btn-outline-secondary btn-sm bi bi-chat-fill"
            >
            상담
            </button>
        </div>
        <div class="comment-item">${item.content}</div>
      </div>
    `

    commentList.appendChild(commentDiv);

    const chatButton = document.getElementById(`check-chat-room-btn`);
    if (chatButton) {
      chatButton.addEventListener("click", (e) =>
        startChat(item.memberId)
      );
    }
  })
}

function renderPagination(data) {
  const pagination = document.getElementById("pagination");
  pagination.innerHTML = "";
  for (let i = 0; i < data.totalPages; i++) {
    const pageItem = document.createElement("li");
    pageItem.className = "page-item"
    pageItem.innerHTML = `<span class="page-link">${i + 1}</span>`
    if (i === 0)
      pageItem.classList.add("active");

    pageItem.addEventListener("click", () => {
      const activePage = document.querySelector(".active");
      if (activePage !== null) {
        activePage.classList.remove("active");
      }
      pageItem.classList.add("active");

      api.get("comments", {
        params: {
          counselId: location.pathname.split("/").pop(),
          page: i
        }
      })
        .then(response => {
          renderComment(response.data);
        })
    });

    pagination.appendChild(pageItem)
  }
}

const commentBtn = document.getElementById("comment-btn");
const deleteBtn = document.getElementById("delete");
const updateBtn = document.getElementById("update");

if (commentBtn !== null) {
  commentBtn.addEventListener("click", () => {
    commentBtn.style.display = "none";
    document.getElementById("comment-content").style.display = "block";
    document.getElementById("confirm-btn").style.display = "block";
  });
}

if (deleteBtn !== null) {
  deleteBtn.addEventListener("click", () => {
    if (confirm("글을 삭제하시겠습니까?")) {
      api.delete("counsels", {
        params: {
          id: location.pathname.split("/").pop()
        }
      }).then(response => {
        location.href = response.headers.location;
      })
    }
  })
}

if (updateBtn !== null) {
  updateBtn.addEventListener("click", () => {
    location.href = `/counsels/update/${location.pathname.split("/").pop()}`
  });
}

document.getElementById("confirm-btn").addEventListener("click", () => {
  api.post("comments", {
    counselId: location.pathname.split("/").pop(),
    content: document.getElementById("comment-content").value
  })
    .then(response => {
      location.href = location.pathname;
    })
});