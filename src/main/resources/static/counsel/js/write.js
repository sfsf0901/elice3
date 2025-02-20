import api from "/common/js/API.js"

function renderSubject(data){
  const subjectList = document.getElementById("category");
  data.forEach(item => {
    const subjectOption = document.createElement("option");
    subjectOption.value = item.name;
    subjectOption.textContent = item.name;

    subjectList.appendChild(subjectOption);
  })
}

api.get("/counsels/category")
  .then(response => {
    console.log(response);
    renderSubject(response.data)
  })

document.getElementById("submit-button").addEventListener("click", () => {
  const request = {
    title: document.getElementById("title").value,
    content: document.getElementById("content").value,
    category: document.getElementById("category").value
  }

  api.post("/counsels", request)
    .then(response => {
      console.log(response);
      location.href = response.headers.location;
    })
})