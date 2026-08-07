document.addEventListener("DOMContentLoaded", () => {

    // ===============================
    // ラジオボタン：再クリックで解除（未配置扱い）
    // ===============================
    document.querySelectorAll("input[type='radio']").forEach(radio => {

        radio.addEventListener("click", () => {

            if (radio.dataset.wasChecked === "true") {

                radio.checked = false;
                radio.dataset.wasChecked = "false";

                if (radio.name.startsWith("slotItemId_")) {
                    const slot = radio.name.replace("slotItemId_", "");
                    const preview = document.querySelector(".room-preview");
                    const slotDiv = preview.querySelector(`[data-slot='${slot}']`);
                    const old = slotDiv.querySelector("div");
                    if (old) {
                        old.classList.add("furniture-anim-leave");
                        setTimeout(() => old.remove(), 400);
                    }
                }

                const hidden = document.createElement("input");
                hidden.type = "hidden";
                hidden.name = radio.name;
                hidden.value = 0;
                radio.closest("label").appendChild(hidden);

            } else {
                radio.dataset.wasChecked = "true";

                document.querySelectorAll(`input[name='${radio.name}']`).forEach(other => {
                    if (other !== radio) {
                        other.dataset.wasChecked = "false";
                    }
                });
            }
        });
    });


    // ===============================
    // テーマ変更（フェード付き）
    // ===============================
    document.querySelectorAll("input[name='themeItemId']").forEach(radio => {
        radio.addEventListener("change", () => {
            const label = radio.closest("label");
            if (!label) return;

            const css = label.dataset.cssClass;
            if (!css) return;

            const preview = document.querySelector(".room-preview");
            if (!preview) return;

            preview.classList.add("theme-changing");

            const removeList = [];
            preview.classList.forEach(cls => {
                if (cls.startsWith("theme-")) removeList.push(cls);
            });
            removeList.forEach(cls => preview.classList.remove(cls));

            preview.classList.add(css);

            setTimeout(() => {
                preview.classList.remove("theme-changing");
            }, 400);
        });
    });


    // ===============================
    // 家具変更（6スロット方式）
    // ===============================
    document.querySelectorAll("input[type='radio'][name^='slotItemId_']")
        .forEach(radio => {
            radio.addEventListener("change", () => {

                const label = radio.closest("label");
                if (!label) return;

                const css = label.dataset.cssClass;
                if (!css) return;

                const id = radio.value;
                const preview = document.querySelector(".room-preview");
                if (!preview) return;

                const slot = radio.name.replace("slotItemId_", "");
                const slotDiv = preview.querySelector(`[data-slot='${slot}']`);
                if (!slotDiv) return;

                const old = slotDiv.querySelector("div");
                if (old) {
                    old.classList.add("furniture-anim-leave");
                    setTimeout(() => old.remove(), 400);
                }

                const div = document.createElement("div");
                div.classList.add(css);
                div.dataset.itemId = id;
                div.classList.add("furniture-anim-enter");

                slotDiv.appendChild(div);
            });
        });


    // ===============================
    // アバター表示 ON/OFF
    // ===============================
    const avatarCheckbox = document.querySelector("input[name='avatarVisible']");
    const avatar = document.querySelector("#roomAvatar");

    if (avatarCheckbox && avatar) {
        avatarCheckbox.addEventListener("change", () => {
            if (avatarCheckbox.checked) {
                avatar.classList.remove("hidden");
            } else {
                avatar.classList.add("hidden");
            }
        });
    }


    // ===============================
    // ★ 家具配置パターン（DEFAULT / LEFT / CENTER / RIGHT / SCATTER）
    // ===============================
    const layoutSelect = document.querySelector("select[name='layoutPattern']");
    const preview = document.querySelector(".room-preview");

    if (layoutSelect && preview) {

        applyLayout(preview, layoutSelect.value);

        layoutSelect.addEventListener("change", () => {
            applyLayout(preview, layoutSelect.value);
        });
    }

    function applyLayout(preview, pattern) {

        // ▼ 既存 layout-* を削除
        const removeList = [];
        preview.classList.forEach(cls => {
            if (cls.startsWith("layout-")) removeList.push(cls);
        });
        removeList.forEach(cls => preview.classList.remove(cls));

        // ▼ DEFAULT → 何もしない（CSS の初期配置）
        if (pattern === "DEFAULT") {
            resetSlotPosition();
            return;
        }

        // ▼ 新しいレイアウトを付与
        const cls = `layout-${pattern.toLowerCase()}`;
        preview.classList.add(cls);

        if (pattern === "SCATTER") {
            applyScatter();
        }

        if (pattern === "LEFT" || pattern === "CENTER" || pattern === "RIGHT") {
            fixOverlap();
        }
    }

    // ▼ DEFAULT のとき座標を初期化
    function resetSlotPosition() {
        document.querySelectorAll(".furniture-slot").forEach(slot => {
            slot.style.left = "";
            slot.style.top = "";
            slot.style.transform = "";
        });
    }

    // ▼ SCATTER：ランダム配置
    function applyScatter() {
        document.querySelectorAll(".furniture-slot").forEach(slot => {
            slot.style.setProperty("--rand-x", Math.random());
            slot.style.setProperty("--rand-y", Math.random());
        });
    }

    // ▼ LEFT / CENTER / RIGHT の重なり防止（縦方向にずらす）
    function fixOverlap() {
        const slots = document.querySelectorAll(".furniture-slot");

        slots.forEach((slot, index) => {
            const offset = index * 12;
            slot.style.top = `${20 + offset}%`;
        });
    }

});
