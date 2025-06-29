document.addEventListener('DOMContentLoaded', function () {
  setDateValues(); // DOMが完全に読み込まれた後に初期値を設定

  // 初期表示の状態に基づいて表示内容を更新
  let periodSelect = document.getElementById('period');
  if (periodSelect) {
    periodSelect.addEventListener('change', function () {
      let period = this.value;

      if (period === 'monthly') {
        document.getElementById('month-container').style.display = 'block'; // 月間選択時にカレンダー表示
        document.getElementById('year-container').style.display = 'none'; // 年間選択肢を非表示
        document.getElementById('report-area').style.display = 'none'; // レポートエリアを非表示
      } else if (period === 'yearly') {
        document.getElementById('month-container').style.display = 'none'; // 月間選択肢を非表示
        document.getElementById('year-container').style.display = 'block'; // 年間選択肢を表示
        document.getElementById('report-area').style.display = 'none'; // レポートエリアを非表示
      }
    });

    // 初期状態を反映
    periodSelect.dispatchEvent(new Event('change'));
  }
  // レポートを表示ボタンをクリックした時の処理
  const reportButton = document.getElementById('displayButton');
  reportButton.addEventListener('click', async function () {
    try {
      // 期間タイプを取得
      const periodType = document.getElementById('period').value;
      let startDate, endDate, selectedMonth, selectedYear;

      if (periodType === 'monthly') {
        // 月間の場合: 年月から期間を計算
        selectedMonth = document.getElementById('startmonth').value; // YYYY-MM形式
        if (!selectedMonth) {
          alert('年月を選択してください。');
          return;
        }

        const [year, month] = selectedMonth.split('-');
        startDate = `${year}-${month}-01`;

        // 月末日を計算
        const nextMonth = new Date(parseInt(year), parseInt(month), 0);
        const lastDay = nextMonth.getDate();
        endDate = `${year}-${month}-${String(lastDay).padStart(2, '0')}`;

      } else if (periodType === 'yearly') {
        // 年間の場合: 年から期間を計算
        selectedYear = document.getElementById('startyear').value;
        if (!selectedYear) {
          alert('年を選択してください。');
          return;
        }

        startDate = `${selectedYear}-01-01`;
        endDate = `${selectedYear}-12-31`;
      }

      // クエリパラメータを構築
      const params = new URLSearchParams({
        period: periodType,
        startDate: startDate,
        endDate: endDate
      });

      // レポートデータを取得（期間指定付き）
      console.log('リクエストURL:', `/api/infokanri/report?${params.toString()}`);
      const response = await fetch(`/api/infokanri/report?${params.toString()}`);

      console.log('レスポンス詳細:', {
        status: response.status,
        statusText: response.statusText,
        ok: response.ok
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('APIエラー詳細:', errorText);
        throw new Error(`レポートデータ取得に失敗しました。ステータス: ${response.status}, エラー: ${errorText}`);
      }

      const data = await response.json();
      console.log('取得データ:', data);

      // 指定期間内のデータのみフィルタリング
      const filteredData = data.filter((item) => {
        const itemDate = item.hiduke; // YYYY-MM-DD形式
        return itemDate >= startDate && itemDate <= endDate;
      });

      // 登録年月日の昇順でソート（古い順）
      filteredData.sort((a, b) => {
        const dateA = new Date(a.hiduke);
        const dateB = new Date(b.hiduke);
        return dateA - dateB;
      });

      // レポートエリアを表示
      document.getElementById('report-area').style.display = 'block';

      // 収支を計算（フィルタリング済みデータで計算）
      let totalIncome = 0;
      let totalExpenditure = 0;
      filteredData.forEach((item) => {
        if (item.syubetu === '収入') {
          totalIncome += item.kingaku;
        } else if (item.syubetu === '支出') {
          totalExpenditure += item.kingaku;
        }
      });

      const balance = totalIncome - totalExpenditure;

      // レポート表示を更新
      document.querySelector('.income').textContent = `収入: ¥ ${totalIncome.toLocaleString()}`;
      document.querySelector('.expenditure').textContent =
        `支出: ¥ ${totalExpenditure.toLocaleString()}`;
      document.querySelector('.income-expenditure').textContent =
        `収支: ¥ ${balance.toLocaleString()}`;

      // テーブルを更新
      const tbody = document.querySelector('tbody');
      tbody.innerHTML = '';

      if (filteredData.length === 0) {
        let periodText = '';
        if (periodType === 'monthly') {
          const month = document.getElementById('startmonth').value;
          if (month) {
            const [year, monthNum] = month.split('-');
            periodText = `${year}年${monthNum}月`;
          } else {
            periodText = '指定月';
          }
        } else {
          const year = document.getElementById('startyear').value;
          periodText = year ? `${year}年` : '指定年';
        }
        tbody.innerHTML = `<tr><td colspan="4">${periodText}のデータがありません</td></tr>`;
        return;
      }

      filteredData.forEach((item) => {
        const row = document.createElement('tr');
        const typeText = item.syubetu;
        const amountText = item.kingaku.toLocaleString() + '円';

        row.innerHTML = `
          <td>${item.hiduke}</td>
          <td>${typeText}</td>
          <td>${item.naisyo}</td>
          <td>${amountText}</td>
        `;

        tbody.appendChild(row);
      });

      console.log(`レポート表示完了: ${periodType}期間, ${filteredData.length}件のデータ`);

    } catch (error) {
      console.error('レポートデータ取得エラー:', error);
      alert('レポートデータの取得に失敗しました。');
    }
  });
});

// 年月と年月日の初期値を設定する関数
function setDateValues() {
  let now = new Date();

  // 年、月を取得
  let year = now.getFullYear();
  let month = String(now.getMonth() + 1).padStart(2, '0');

  // 初期値として現在の日付を設定する
  const inputstartmonth = document.getElementById('startmonth');
  if (inputstartmonth) {
    let startmonth = `${year}-${month}`;
    inputstartmonth.value = startmonth; // 年月（YYYY-MM）形式
  }

  // 初期値の設定後に年選択肢を設定する
  let select = document.getElementById('startyear');
  if (select) {
    for (let inputstartyear = year - 20; inputstartyear <= year; inputstartyear++) {
      let option = document.createElement('option');
      option.value = inputstartyear;
      option.textContent = inputstartyear;
      select.appendChild(option);
    }
    // 初期値を今年に設定
    select.value = year;
  }
}
