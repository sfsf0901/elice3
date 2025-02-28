import api from "/common/js/API.js"

function renderList(data){
  const items = data.content;
  const listContainer = document.getElementById("list-container");
  listContainer.innerHTML = "";

  if(items.length === 0) {
    listContainer.innerHTML = `
    <div class="alert alert-light my-5" role="alert">
      <div class="d-flex flex-column align-items-center justify-content-center text-center" style="height: 20vh;">
        <i class="fa-solid fa-triangle-exclamation h2"></i>
        <h5 class="mt-2">상담내역이 존재하지 않습니다.</h5>
      </div>
    </div>`;
    return;
  }

  items.forEach(item => {
    const itemDiv = document.createElement("div");
    let content = item.content;
    if(content.length > 50)
      content = content.substring(0, 50) + "...";

    itemDiv.className = "counsel-item"
    itemDiv.innerHTML = `
      <div class="d-flex">
      <h3 class="counsel-title">${item.title}</h3>&nbsp
      <h6><span class="badge text-bg-secondary">${item.category}</span></h6>
      </div>
      <p class="counsel-summary">${content}</p>
      <span class="counsel-date">${item.createdDate.split("T")[0]}</span>
    `;

    itemDiv.addEventListener("click", () => {
      location.href = `/counsels/${item.counselId}`;
    });
    listContainer.appendChild(itemDiv);
  });
}

function renderPagination(data) {
  const pagination = document.getElementById("pagination");
  pagination.innerHTML = "";
  for(let i = 0; i < data.totalPages; i++) {
    const pageItem = document.createElement("li");
    pageItem.className = "page-item"
    pageItem.innerHTML = `<span class="page-link">${i + 1}</span>`
    if(i === 0)
      pageItem.classList.add("active");

    pageItem.addEventListener("click", () => {
      const activePage = document.querySelector(".active");
      if(activePage !== null){
        activePage.classList.remove("active");
      }
      pageItem.classList.add("active");

      api.get("counsels", {
        params: {
          keyword: document.getElementById("search-input").value,
          page: i
        }
      })
        .then(response => {
          renderList(response.data);
        })
    });

    pagination.appendChild(pageItem)
  }
}

function search(){
  api.get("counsels", {
    params: {
      keyword: document.getElementById("search-input").value
    }
  })
    .then(response => {
      console.log(response);
      renderList(response.data);
      renderPagination(response.data);
    });
}

document.getElementById("search").addEventListener("click", () => {
  search();
})

document.getElementById("search-input").addEventListener("keydown", (event) => {
  if (event.key === "Enter") {
    search();
  }
});

api.get("counsels")
  .then(response => {
    console.log(response);
    renderList(response.data);
    renderPagination(response.data);
  });