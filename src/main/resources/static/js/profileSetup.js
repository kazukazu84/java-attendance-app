document.addEventListener("DOMContentLoaded", () => {

    const headers = document.querySelectorAll(".company-header");

    headers.forEach(header => {
        const box = header.nextElementSibling;

        // ▼ 初期状態は閉じる
        box.style.display = "none";

        // ▼ ラジオボタンが選択済みなら開く
        const selectedRadio = box.querySelector("input[type='radio']:checked");
        if (selectedRadio) {
            box.style.display = "block";
        }

        // ▼ クリックで開閉
        header.addEventListener("click", () => {
            const isOpen = box.style.display === "block";
            box.style.display = isOpen ? "none" : "block";
        });
    });

});
