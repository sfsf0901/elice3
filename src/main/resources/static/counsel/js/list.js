import api from "/common/js/API.js"

function renderList(items){
  const listContainer = document.getElementById("list-container");
  listContainer.innerHTML = "";

  items.forEach(item => {
    const itemDiv = document.createElement("div");
    itemDiv.innerHTML = `
      <h3>${item.title}</h3>
      <p>${item.content}</p>
    `;

    listContainer.appendChild(itemDiv);
  });
}

function renderPageButton(pageNumber) {

}

api.get("counsels")
  .then(response => {
    console.log(response);
    renderList(response.data.content);
  });