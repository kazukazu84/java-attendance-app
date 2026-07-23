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
// キャンセルボタン処理
// -------------------------------------------------------------
function cancelModal() {
    // 1. モーダル内の hidden 項目、または select から selectedEventId を取得
    // (disabled な select からではなく、hidden の eventId を優先参照する)
    const hiddenEventId = document.querySelector('form input[name="eventId"]');
    const eventSelectEl = document.getElementById('eventSelect');

    let selectedEventId = null;
    if (hiddenEventId && hiddenEventId.value) {
        selectedEventId = hiddenEventId.value;
    } else if (eventSelectEl && eventSelectEl.value) {
        selectedEventId = eventSelectEl.value;
    }

    // 2. モーダルを即座に非表示
    const modalOverlay = document.getElementById('modalOverlay');
    if (modalOverlay) {
        modalOverlay.style.display = 'none';
    }

    // 3. GETで初期表示URLへ遷移
    if (selectedEventId) {
        window.location.href = '/shift/shiftCreate?selectedEventId=' + encodeURIComponent(selectedEventId);
    } else {
        window.location.href = '/shift/shiftCreate';
    }
}