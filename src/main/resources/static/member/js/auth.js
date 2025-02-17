const authHandler = {
    kakao: () => {
        alert("카카오 인증 페이지로 이동합니다.");
        window.location.href = "https://kauth.kakao.com/oauth/authorize";
    },
    naver: () => {
        alert("네이버 인증 페이지로 이동합니다.");
        window.location.href = "https://nid.naver.com/oauth2.0/authorize";
    },
    pass: () => {
        alert("PASS 인증 페이지로 이동합니다.");
        window.location.href = "https://www.pass.co.kr/";
    },
    certificate: () => {
        alert("공동인증서 페이지로 이동합니다.");
        window.location.href = "https://www.yessign.or.kr/";
    }
};