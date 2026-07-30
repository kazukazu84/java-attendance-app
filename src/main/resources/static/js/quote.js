function toggleQuoteList(postId) {
    const list = document.getElementById(`quote-list-${postId}`);
    const preview = document.querySelector(`.quote-preview[data-post-id="${postId}"]`);
    const moreBtn = document.querySelector(`.quote-more[onclick="toggleQuoteList(${postId})"]`);

    const isHidden = list.style.display === "none" || list.style.display === "";

    if (isHidden) {
        // ▼ 全件表示を開く
        list.style.display = "block";
        if (preview) preview.style.display = "none";

        // ▼ 「すべての引用を見る」ボタンを消す
        if (moreBtn) moreBtn.style.display = "none";

    } else {
        // ▼ 最新3件に戻す
        list.style.display = "none";
        if (preview) preview.style.display = "block";

        // ▼ 「すべての引用を見る」ボタンを再表示
        if (moreBtn) moreBtn.style.display = "block";
    }
}
