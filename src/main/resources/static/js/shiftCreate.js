/**
 * シフト作成画面用 JavaScript
 */
document.addEventListener('DOMContentLoaded', () => {
    const restCheckbox = document.getElementById('restCheckbox');
    const startTimeInput = document.getElementById('startTime');
    const endTimeInput = document.getElementById('endTime');

    /**
     * 「休み」チェックボックスの状態に応じて出勤・退勤時間を制御
     */
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

    // --- 修正箇所: イベント委譲でキャンセルボタンのクリックを検知 ---
	document.addEventListener('DOMContentLoaded', () => {
	    // ... 前半の処理 ...

	    const btnCancel = document.getElementById('btnCancel');

	    if (btnCancel) {
	        btnCancel.addEventListener('click', (e) => {
	            // フォームの送信動作を確実に防止
	            e.preventDefault();
	            
	            // デバッグ用（ブラウザのコンソールでクリックされたか確認できます）
	            console.log('キャンセルボタンが押されました');

	            const eventSelectEl = document.getElementById('eventSelect');
	            const selectedEventId = eventSelectEl ? eventSelectEl.value : null;

	            if (selectedEventId) {
	                window.location.href = `/shift/shiftCreate?selectedEventId=${encodeURIComponent(selectedEventId)}`;
	            } else {
	                window.location.href = '/shift/shiftCreate';
	            }
	        });
	    }
	});