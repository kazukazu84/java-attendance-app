document.addEventListener("DOMContentLoaded", function () {

    /*
     * 全行取得
     */
    const rows = document.querySelectorAll(".shift-table tbody tr");


    rows.forEach(function (row) {

        const radios =
            row.querySelectorAll(
                'input[type="radio"]'
            );

        const startTime =
            row.querySelector(
                'input[type="time"][name*="startTime"]'
            );

        const endTime =
            row.querySelector(
                'input[type="time"][name*="endTime"]'
            );


        /*
         * 時間入力欄の状態変更
         */
        function updateTimeInput() {

            let selectedValue = null;


            radios.forEach(function (radio) {

                if (radio.checked) {
                    selectedValue = radio.value;
                }

            });


            /*
             * ○の場合
             */
            if (selectedValue === "○") {

                startTime.disabled = false;
                endTime.disabled = false;

                startTime.classList.remove("disabled-input");
                endTime.classList.remove("disabled-input");

            }


            /*
             * ×の場合
             */
            else if (selectedValue === "×") {

                startTime.value = "";
                endTime.value = "";

                startTime.disabled = true;
                endTime.disabled = true;

                startTime.classList.add("disabled-input");
                endTime.classList.add("disabled-input");

            }

        }


        /*
         * 初期表示時
         */
        updateTimeInput();


        /*
         * ラジオ変更時
         */
        radios.forEach(function (radio) {

            radio.addEventListener(
                "change",
                updateTimeInput
            );

        });

    });

});