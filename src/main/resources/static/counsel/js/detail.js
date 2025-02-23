import api from "/common/js/API.js"

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

async function renderContent(data) {
  document.getElementById("title").textContent = data.title;
  document.getElementById("content").textContent = data.content;
  document.getElementById("name").textContent = data.email;
  document.getElementById("time").textContent = data.createdDate;
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
        <div class="comment-meta">${item.email} | ${item.createdDate}</div>
        <div class="comment-item">${item.content}</div>
      </div>
    `

    commentList.appendChild(commentDiv);
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