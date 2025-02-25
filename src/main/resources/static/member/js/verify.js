import api from "/common/js/API.js"

api.get("/mail/verification", {
  params: {
    code: new URLSearchParams(location.search).get("code")
  }
}).then(response => {
  console.log(response);
  if (response.data) {
    alert("인증이 성공적으로 완료되었습니다.");
    window.close();
  } else {
    alert("인증이 실패했습니다. 다시 시도해주세요.");
    window.close();
  }
}).catch(error => {
  console.log(error);
  alert("인증 기간이 만료되었습니다. 다시 시도해주세요.");
  window.close();
})

