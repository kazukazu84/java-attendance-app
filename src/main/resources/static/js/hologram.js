// ★ ホログラム背景の揺らぎ演出（軽量）

document.addEventListener("mousemove", (e) => {
    const x = (e.clientX / window.innerWidth - 0.5) * 10;
    const y = (e.clientY / window.innerHeight - 0.5) * 10;

    const bg = document.querySelector(".bg-animation");
    bg.style.transform = `translate(${x}px, ${y}px)`;
});
