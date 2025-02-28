import api from "/common/js/API.js";

document.getElementById("licenseForm").addEventListener("submit", async function (event) {
  event.preventDefault(); // 기본 제출 방지
  document.getElementById("licenseForm").style.pointerEvents = "none";
  document.getElementById("licenseForm").style.cursor = "wait";

  const JUMIN = document.getElementById("jumin1").value + document.getElementById("jumin2").value;
  const encryptedJumin = await (await fetch("/api/v1/licenses/encrypt", {
    method: "POST",
    headers: {"Content-Type": "application/json"},
    body: JUMIN
  })).text();

  const formData = {
    LOGINOPTION: document.querySelector("input[name=\"loginOption\"]:checked").value,
    JUMIN: encryptedJumin,
    DSNM: document.getElementById("dsn").value,
    PHONENUM: document.getElementById("phoneNum").value,
    TELECOMGUBUN: 1
  };

  console.log("폼 데이터:", formData);

  const data = (await api.post('https://datahub-dev.scraping.co.kr/scrap/common/mohw/MedicalLicenseInquirySimple',
    formData, // 본문 데이터
    { headers: { "Authorization": "74d7823acb384f5db0b54ab9e40839aec2a13b46" } }
  )).data;

  console.log(data);

  localStorage.setItem("callbackId", data.callbackId);
  localStorage.setItem("callbackType", data.callbackType);

  location.href = "/license/progress";
});