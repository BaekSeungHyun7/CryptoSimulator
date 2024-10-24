// 서버에서 가상화폐 데이터를 가져오는 함수
async function fetchCryptocurrencies() {
    try {
        const response = await fetch('http://localhost:8080/api/cryptocurrencies/list'); // 서버 API 호출
        const data = await response.json();
        renderTable(data); // 데이터 렌더링
    } catch (error) {
        console.error('Error fetching cryptocurrency data:', error);
    }
}

// 데이터를 테이블에 렌더링하는 함수
function renderTable(cryptos) {
    const tableBody = document.querySelector('#crypto-table tbody'); // 테이블 본문 선택
    tableBody.innerHTML = ''; // 기존 테이블 초기화

    cryptos.forEach((crypto, index) => {
        const row = document.createElement('tr'); // 행 생성

        // 변동량 계산 및 색상 적용
        const changeRate = (crypto.signed_change_rate * 100).toFixed(2);
        const changeText = changeRate > 0 ? `+${changeRate}%` : `${changeRate}%`; // 백틱 사용
        let changeColor = 'black'; // 기본 색상
        if (changeRate > 0) changeColor = 'red'; // 양수인 경우 빨간색
        else if (changeRate < 0) changeColor = 'blue'; // 음수인 경우 파란색

        // 각 데이터 셀 생성
        row.innerHTML = `
            <td>${index + 1}</td>  
            <td>${crypto.koreanName}</td>
            <td>${crypto.symbol}</td>
            <td>${crypto.trade_price.toLocaleString()}</td>
            <td>${crypto.acc_trade_price.toLocaleString()}</td>
            <td>${crypto.acc_trade_volume_24h.toLocaleString()}</td>
            <td style="color: ${changeColor};">${changeText}</td>
        `;

        tableBody.appendChild(row); // 테이블에 행 추가
    });
}

// 페이지 로드 시 데이터 가져오기
document.addEventListener('DOMContentLoaded', fetchCryptocurrencies);


