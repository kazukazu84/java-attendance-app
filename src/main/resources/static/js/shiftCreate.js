/**
 * シフト作成画面用 JavaScript
 */
document.addEventListener('DOMContentLoaded', () => {
    
    // -------------------------------------------------------------
    // 「休み」チェックボックスによる時間入力欄の制御
    // -------------------------------------------------------------
    const restCheckbox = document.getElementById('restCheckbox');
    const startTimeInput = document.getElementById('startTime');
    const endTimeInput = document.getElementById('endTime');

    const toggleTimeInputs = () => {
        if (!restCheckbox || !startTimeInput || !endTimeInput) return;
        const isRest = restCheckbox.checked;
        startTimeInput.disabled = isRest;
        endTimeInput.disabled = isRest;
    };

    if (restCheckbox) {
        toggleTimeInputs();
        restCheckbox.addEventListener('change', toggleTimeInputs);
    }

});

// -------------------------------------------------------------
// キャンセルボタン処理（HTMLのonclickから呼び出される）
// -------------------------------------------------------------
function cancelModal() {
    // イベント選択コンテナから現在選ばれているイベントIDを取得
    const eventSelectEl = document.getElementById('eventSelect');
    const selectedEventId = eventSelectEl ? eventSelectEl.value : null;

    // 選択されたイベントIDを引き継いだまま画面を再読み込み（モーダルを閉じる）
    if (selectedEventId) {
        window.location.href = `/shift/shiftCreate?selectedEventId=${encodeURIComponent(selectedEventId)}`;
    } else {
        window.location.href = '/shift/shiftCreate';
    }
}