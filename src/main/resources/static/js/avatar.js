document.addEventListener("DOMContentLoaded", () => {

    const preview = document.querySelector(".avatar-preview");

    // ===============================
    // アバター変更（差し替え方式）
    // ===============================
    const radios = document.querySelectorAll(".item-list input[type='radio']");

    radios.forEach(radio => {
        radio.addEventListener("change", () => {

            // ▼ パーツ行（label）を取得
            const label = radio.closest("label");

            // ▼ パーツの cssClass（最新仕様）
            const cssClass = label.dataset.cssClass || radio.dataset.cssClass;

            // ▼ 部位タイプ（BASE / EAR / EYE / FACE / BODY / ACCESSORY）
            const partType = radio.name.replace("ItemId", "").toUpperCase();

            // ▼ 対象パーツの既存DOMを取得
            const partEl = preview.querySelector(`[data-part='${partType}']`);
            if (!partEl) return;

            // ▼ フェードアウト
            partEl.classList.add("avatar-anim-leave");

            setTimeout(() => {

                // ▼ クラス差し替え（DOMは消さない）
                partEl.className = cssClass;
                partEl.dataset.part = partType;

                // ▼ フェードイン
                partEl.classList.remove("avatar-anim-leave");
                partEl.classList.add("avatar-anim-enter");

                setTimeout(() => {
                    partEl.classList.remove("avatar-anim-enter");
                }, 300);

            }, 200);
        });
    });
});
