import api from "/common/js/API.js";

// console.log(memberAPI.REGISTER)

const code = Math.random().toString(36).substring(2, 11);

document.getElementById("submit-button").addEventListener("click", userRegister);

async function userRegister() {
  api.post("/mail", {
    address: document.getElementById("email").value,
    code
  })
  alert("인증 메일이 발송되었습니다.")

  document.querySelectorAll("input").forEach(item => {
    item.disabled = true;
  });

  document.getElementById("submit-button").style.display = "none";
  document.getElementById("verify-button").style.display = "block"
}

document.getElementById("verify-button").addEventListener("click", verifySuccess)

function verifySuccess(){
  api.get("/mail/verification-success", {
    params: {
      code: code
    }
  })
    .then(response => {
      console.log(response);
      if(response.data) {
        api.post("members", {
          email: document.getElementById('email').value,
          name: document.getElementById('name').value,
          password: document.getElementById('password').value,
          contact: document.getElementById('contact').value
        })
          .then(res => {
            console.log(res);
            alert("회원가입이 완료되었습니다.");
            location.href = "/login";
          })
      } else
        alert("이메일 인증을 완료해주세요.");
    })
}

window.addEventListener("beforeunload", event => {
  event.preventDefault();
})
