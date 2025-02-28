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
  const title = document.getElementById("title").value;
  const content = document.getElementById("content").value;

  const request = {
    title,
    content,
    category: document.getElementById("category").value
  }

  if(title.length < 2 || content.length < 2){
    alert("양식에 맞게 입력해주세요");
    return;
  }

  api.post("/counsels", request)
    .then(response => {
      console.log(response);
      location.href = response.headers.location;
    })
})