import api from "/common/js/API.js"

function renderList(data){
  const items = data.content;
  const listContainer = document.getElementById("list-container");
  listContainer.innerHTML = "";

  items.forEach(item => {
    const itemDiv = document.createElement("div");
    itemDiv.className = "counsel-item"
    itemDiv.innerHTML = `
      <h3 class="counsel-title">${item.title}</h3>
      <p class="counsel-summary">${item.content}</p>
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
        params: {page: i}
      })
        .then(response => {
          renderList(response.data);
        })
    });

    pagination.appendChild(pageItem)
  }
}

api.get("members/counsels")
  .then(response => {
    console.log(response);
    renderList(response.data);
    renderPagination(response.data);
  });