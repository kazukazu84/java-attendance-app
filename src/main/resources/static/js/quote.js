function toggleQuoteList(postId) {
    const list = document.getElementById(`quote-list-${postId}`);
    const preview = document.querySelector(`.quote-preview[data-post-id="${postId}"]`);

    const isHidden = list.style.display === "none" || list.style.display === "";

    // 一覧を表示する時はプレビューを消す
    if (isHidden) {
        list.style.display = "block";
        if (preview) preview.style.display = "none";
    } else {
        list.style.display = "none";
        if (preview) preview.style.display = "block";
    }
}
