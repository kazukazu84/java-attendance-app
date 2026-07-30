// ▼ 今日の遅延証明書チェック
const btnCheck = document.getElementById("btn-check-certificate");
const boxCheck = document.getElementById("certificate-check");
const resultCheck = document.getElementById("certificate-result");

if (btnCheck) {
    btnCheck.addEventListener("click", async () => {

        const isHidden = boxCheck.style.display === "none" || boxCheck.style.display === "";

        if (isHidden) {
            boxCheck.style.display = "block";
            resultCheck.textContent = "確認中...";

            try {
                // Spring側の HEAD チェック API
                const res = await fetch("/rail/certificate/checkToday");
                const json = await res.json();

                if (json.certificateAvailable) {
                    // ★ 今日の遅延証明書が発行されている場合のみリンクを表示
                    resultCheck.innerHTML =
                        `<a href="${json.todayCertificateUrl}" target="_blank">今日の遅延証明書を見る</a>`;
                } else {
                    // ★ 発行されていない場合はメッセージのみ
                    resultCheck.textContent = "本日の遅延証明書は発行されていません。";
                }

            } catch (e) {
                resultCheck.textContent = "証明書の確認に失敗しました。";
            }

        } else {
            boxCheck.style.display = "none";
        }
    });
}

// ▼ 他社リンクの折りたたみ
const btnCompany = document.getElementById("btn-company-links");
const boxCompany = document.getElementById("company-links");

if (btnCompany) {
    btnCompany.addEventListener("click", () => {
        const isHidden = boxCompany.style.display === "none" || boxCompany.style.display === "";
        boxCompany.style.display = isHidden ? "block" : "none";
    });
}
