import api from "/common/js/API.js"

document.getElementById("verify").addEventListener("click", () => {
  const params = new URLSearchParams(location.search);
  const code = params.get("code");

  console.log(code);

  api.get("/mail/verification", {
    params:{
      code: code
    }
  })
    .then(response => {
      console.log(response);
      if (response.data){
        alert("인증이 성공적으로 완료되었습니다.")
        window.close();
      }
    })
})