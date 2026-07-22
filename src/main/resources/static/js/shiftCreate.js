/**
 * シフト作成画面用 JavaScript
 */
document.addEventListener('DOMContentLoaded', () => {
    const restCheckbox = document.getElementById('restCheckbox');
    const startTimeInput = document.getElementById('startTime');
    const endTimeInput = document.getElementById('endTime');
    const btnCancel = document.getElementById('btnCancel');

    /**
     * 「休み」チェックボックスの状態に応じて出勤・退勤時間を制御
     */
    const toggleTimeInputs = () => {
        if (!restCheckbox || !startTimeInput || !endTimeInput) return;

        const isRest = restCheckbox.checked;
        // 休みチェックONでdisabled、OFFでenabled（値は消さない）
        startTimeInput.disabled = isRest;
        endTimeInput.disabled = isRest;
    };

    // 初期状態の設定とイベントリスナー登録
    if (restCheckbox) {
        toggleTimeInputs();
        restCheckbox.addEventListener('change', toggleTimeInputs);
    }

    // キャンセルボタン押下処理（GETリクエストで自画面へ遷移しモーダルを閉じる）
    if (btnCancel) {
        btnCancel.addEventListener('click', () => {
            const selectedEventId = document.getElementById('eventSelect')?.value;
            if (selectedEventId) {
                window.location.href = `/shift/shiftCreate?selectedEventId=${selectedEventId}`;
            } else {
                window.location.href = '/shift/shiftCreate';
            }
        });
    }
});