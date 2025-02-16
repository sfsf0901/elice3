document.getElementById("licenseForm").addEventListener("submit", async function (event) {
    event.preventDefault(); // 기본 제출 방지

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

    const response = await (await fetch('https://datahub-dev.scraping.co.kr/scrap/common/mohw/MedicalLicenseInquirySimple', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            "Authorization": "74d7823acb384f5db0b54ab9e40839aec2a13b46"
        },
        body: JSON.stringify(formData)
    })).json();

    console.log(response);

    localStorage.setItem("callbackId", response.data.callbackId);
    localStorage.setItem("callbackType", response.data.callbackType);

    location.href = "/license/progress";
});