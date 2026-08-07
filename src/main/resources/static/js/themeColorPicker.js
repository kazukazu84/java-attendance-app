document.addEventListener("DOMContentLoaded", () => {
    const select = document.getElementById("themeSelect");
    const picker = document.getElementById("themePicker");
    const hidden = document.getElementById("themeColorHidden");

    if (!select || !picker || !hidden) return;

    // ▼ 初期値を hidden に反映
    hidden.value = select.value;

    // ▼ 選択式の変更時
    select.addEventListener("change", () => {
        if (select.value === "custom") {
            picker.style.display = "block";
        } else {
            picker.style.display = "none";
            picker.value = select.value;

            // hidden に反映（POST 用）
            hidden.value = select.value;

            // UI に即反映
            document.body.style.setProperty("--themeColor", select.value);
        }
    });

    // ▼ カラーピッカーの変更時
    picker.addEventListener("input", () => {
        // hidden に反映（POST 用）
        hidden.value = picker.value;

        // UI に即反映
        document.body.style.setProperty("--themeColor", picker.value);
    });
});
