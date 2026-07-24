// static/js/cat-runner.js
document.addEventListener('DOMContentLoaded', () => {
    const BLACK_CAT = '🐈‍⬛';

    // --- 1. 外周を走る黒猫（40秒ごとに後ろに1匹追加・最大6匹） ---
    const maxOrbitCount = 6;
    let currentOrbitCount = 0;

	function spawnOrbitCat() {
	        if (currentOrbitCount >= maxOrbitCount) return;

	        const cat = document.createElement('div');
	        cat.className = 'orbit-cat';
	        cat.innerText = BLACK_CAT;

			// ディレイ値も少し大きめに調整（秒数指定）
			        let delay = 0;
			        if (currentOrbitCount === 1) {
			            delay = -5; // 2匹目はしっかり離す
			        } else if (currentOrbitCount > 1) {
			            delay = -5 - ((currentOrbitCount - 1) * 1.8); // 3匹目以降はトコトコ詰める
			        }

	        cat.style.animationDelay = `${delay}s`;

	        document.body.appendChild(cat);
	        currentOrbitCount++;
	    }

    // 1匹目は即時スタート
    spawnOrbitCat();

    // 1周（40秒）ごとに1匹ずつ増殖
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