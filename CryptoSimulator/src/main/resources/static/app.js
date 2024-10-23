// API로부터 가상화폐 데이터를 가져와 표에 표시하는 함수
async function loadCryptocurrencies() {
    try {
        const response = await fetch('/api/cryptocurrencies/list'); // API 호출
        const data = await response.json(); // JSON 데이터 파싱

        const tableBody = document.querySelector('#crypto-table tbody');
        tableBody.innerHTML = ''; // 기존 데이터 초기화

        // 각 가상화폐 데이터를 표에 추가
        data.forEach(crypto => {
            const row = document.createElement('tr');

            // 변동량이 양수면 + 붙이기
            const change24h = crypto.change_24h >= 0 
                ? `+${(crypto.change_24h * 100).toFixed(2)}%` 
                : `${(crypto.change_24h * 100).toFixed(2)}%`;

            row.innerHTML = `
                <td>${crypto.rank}</td>
                <td>${crypto.name_korean}</td>
                <td>${crypto.symbol}</td>
                <td>${crypto.price_krw.toLocaleString()}</td>
                <td>${crypto.market_cap.toLocaleString()}</td>
                <td>${crypto.volume_24h.toLocaleString()}</td>
                <td>${change24h}</td>
            `;

            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error fetching cryptocurrency data:', error);
    }
}

// 페이지가 로드되면 데이터 불러오기
window.onload = loadCryptocurrencies;

