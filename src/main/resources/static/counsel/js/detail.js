import api from "/common/js/API.js"

async function renderContent() {
  const data = (await api.get(`counsels/detail`, {
    params: {id: location.pathname.split("/").pop()}
  })).data
  document.getElementById("title").textContent = data.title;
  document.getElementById("content").textContent = data.content;
  document.getElementById("name").textContent = data.email;
  document.getElementById("time").textContent = data.createdDate;

  console.log(data);
}

renderContent();