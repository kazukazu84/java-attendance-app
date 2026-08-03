// static/js/cat-runner.js
document.addEventListener('DOMContentLoaded', () => {
    const BLACK_CAT = '🐈‍⬛';

    // --- 1. 外周を走る黒猫（40秒ごとに後ろに1匹追加・最大10匹） ---
    const maxOrbitCount = 10;
    let currentOrbitCount = 0;

	function spawnOrbitCat() {
	        if (currentOrbitCount >= maxOrbitCount) return;

	        const cat = document.createElement('div');
	        cat.className = 'orbit-cat';
	        cat.innerText = BLACK_CAT;

	        // 💡 スタート地点（画面端）は揃えつつ、1周のスピード（50秒〜70秒）に個体差をつける
	        // 足の速い猫が前の猫に追いついて追い越すことでワチャワチャする！
	        const randomDuration = 60 + (Math.random() * 20 - 10); 
	        cat.style.animationDuration = `${randomDuration}s`;

	        // 💡 delay は設定しない（または 0s）ことで、必ずスタート地点から走り出す
	        cat.style.animationDelay = '0s';

	        document.body.appendChild(cat);
	        currentOrbitCount++;
	    }

    // 1匹目は即時スタート
    spawnOrbitCat();

    // 1周（60秒）ごとに1匹ずつ増殖
    const orbitInterval = setInterval(() => {
        if (currentOrbitCount < maxOrbitCount) {
            spawnOrbitCat();
        } else {
            clearInterval(orbitInterval);
        }
    }, 60000);


    // --- 2. 浮遊する黒猫（30秒後に1匹目、以降6秒ごとに1匹・最大10匹） ---
    const maxFloatingCount = 10;
    let currentFloatingCount = 0;

    function createFloatingCat() {
        if (currentFloatingCount >= maxFloatingCount) return;

        const cat = document.createElement('div');
        cat.innerText = BLACK_CAT;
        
        cat.style.position = 'fixed';
        cat.style.fontSize = '3rem';
        cat.style.pointerEvents = 'none';
        cat.style.zIndex = '9999';
        
        let x = Math.random() * (window.innerWidth - 60);
        let y = Math.random() * (window.innerHeight - 60);
        
        // 低速移動（0.4〜1.2px/frame）
        const speed = 0.4 + Math.random() * 0.8;
        let dx = (Math.random() < 0.5 ? 1 : -1) * speed;
        let dy = (Math.random() < 0.5 ? 1 : -1) * speed;

        // ゆっくり回転
        let angle = Math.random() * 360;
        const rotateSpeed = (Math.random() - 0.5) * 1.5;

        document.body.appendChild(cat);
        currentFloatingCount++;

        function move() {
            x += dx;
            y += dy;

            // 壁バウンド
            if (x <= 0 || x >= window.innerWidth - 60) dx *= -1;
            if (y <= 0 || y >= window.innerHeight - 60) dy *= -1;

            angle += rotateSpeed;

            cat.style.left = `${x}px`;
            cat.style.top = `${y}px`;

            const scaleX = dx < 0 ? -1 : 1;
            cat.style.transform = `scaleX(${scaleX}) rotate(${angle}deg)`;

            requestAnimationFrame(move);
        }
        move();
    }

    // 30秒放置で浮遊スタート
    setTimeout(() => {
        createFloatingCat();

        const floatingInterval = setInterval(() => {
            if (currentFloatingCount < maxFloatingCount) {
                createFloatingCat();
            } else {
                clearInterval(floatingInterval);
            }
        }, 6000);
    }, 30000);
});