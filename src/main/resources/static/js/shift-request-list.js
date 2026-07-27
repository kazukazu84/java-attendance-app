/**
 * サイドバーを表示し、非同期で詳細データを取得して表示する
 */
function openSidebar(buttonElement) {
    const userId = buttonElement.getAttribute('data-userid');
    const userName = buttonElement.getAttribute('data-username');
    const eventSelect = document.getElementById('eventSelect');
    const eventId = eventSelect ? eventSelect.value : null;

    if (!userId || !eventId) return;

    // ユーザー名のセット
    document.getElementById('sidebarUserName').innerText = userName;

    // 非同期データ取得 (fetch)
    fetch(`/admin/shift-request-list/detail?userId=${encodeURIComponent(userId)}&eventId=${encodeURIComponent(eventId)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('ネットワークエラーが発生しました。');
            }
            return response.json();
        })
        .then(data => {
            const tbody = document.getElementById('sidebarDetailBody');
            tbody.innerHTML = '';

            data.forEach(item => {
                const tr = document.createElement('tr');

                const tdDate = document.createElement('td');
                tdDate.textContent = item.formattedWorkDate;

                const tdAvail = document.createElement('td');
                tdAvail.textContent = item.availabilityText;

                const tdStart = document.createElement('td');
                tdStart.textContent = item.startTimeText;

                const tdEnd = document.createElement('td');
                tdEnd.textContent = item.endTimeText;

                // 可否が「×」の場合のハイライト処理
                if (item.availabilityText === "×") {
                    tdDate.classList.add('sidebar-unavailable');
                    tdAvail.classList.add('sidebar-unavailable');
                    tdStart.classList.add('sidebar-unavailable');
                    tdEnd.classList.add('sidebar-unavailable');
                }

                // 日またぎ判定：終了時刻 < 開始時刻 の場合、4セルすべてに専用クラスを付与
                const startTime = item.startTimeText;
                const endTime = item.endTimeText;
                if (startTime && endTime && endTime < startTime) {
                    tdDate.classList.add('overnight-cell');
                    tdAvail.classList.add('overnight-cell');
                    tdStart.classList.add('overnight-cell');
                    tdEnd.classList.add('overnight-cell');
                }

                tr.appendChild(tdDate);
                tr.appendChild(tdAvail);
                tr.appendChild(tdStart);
                tr.appendChild(tdEnd);

                tbody.appendChild(tr);
            });

            // サイドバー表示
            document.getElementById('sidebar').classList.add('active');
        })
        .catch(error => {
            console.error('Error fetching detail:', error);
            alert('詳細データの取得に失敗しました。');
        });
}

/**
 * サイドバーを閉じる
 */
function closeSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
        sidebar.classList.remove('active');
    }
}