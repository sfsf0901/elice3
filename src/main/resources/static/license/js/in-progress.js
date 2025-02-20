import api from "/common/js/API.js"

async function complete() {
  const request = {
    callbackId: localStorage.getItem("callbackId"),
    callbackType: localStorage.getItem("callbackType")
  };

  const response = await (await fetch("https://datahub-dev.scraping.co.kr/scrap/captcha", {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "74d7823acb384f5db0b54ab9e40839aec2a13b46"
    },
    body: JSON.stringify(request)
  })).json();

  // 인증 안하고 버튼 눌렀을 때 에러코드 2090

  alert(response.data.ERRMSG)
  console.log(response)

  if(response.data.ECODE !== "ERR_MLCOM_MSG50255") {
    localStorage.removeItem("callbackId");
    localStorage.removeItem("callbackType");

    await fetch("/api/v1/members/role", {
      method: "POST"
    });

    window.close();
  }

}