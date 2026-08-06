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

/*
 * 選択日一括時間反映
 */
const bulkButton =
    document.getElementById(
        "applyBulkTime"
    );


if (bulkButton) {


    bulkButton.addEventListener(
        "click",
        function () {


            const bulkStart =
                document.getElementById(
                    "bulkStartTime"
                ).value;


            const bulkEnd =
                document.getElementById(
                    "bulkEndTime"
                ).value;



            /*
             * 時間未入力チェック
             */
            if (!bulkStart || !bulkEnd) {

                alert(
                    "開始時間と終了時間を入力してください"
                );

                return;
            }



            const checks =
                document.querySelectorAll(
                    ".shift-check:checked"
                );


            /*
             * 選択チェック
             */
            if (checks.length === 0) {

                alert(
                    "反映する日を選択してください"
                );

                return;
            }



            checks.forEach(
                function(check){


                    const row =
                        check.closest("tr");


                    const radios =
                        row.querySelectorAll(
                            'input[type="radio"]'
                        );


                    /*
                     * ×の日には反映しない
                     */
                    let available = null;


                    radios.forEach(
                        function(radio){

                            if(radio.checked){

                                available =
                                    radio.value;

                            }

                        }
                    );


                    if(available !== "○"){
                        return;
                    }



                    const startInput =
                        row.querySelector(
                            'input[type="time"][name*="startTime"]'
                        );


                    const endInput =
                        row.querySelector(
                            'input[type="time"][name*="endTime"]'
                        );


                    startInput.value =
                        bulkStart;


                    endInput.value =
                        bulkEnd;

                }
            );

        }
    );

}