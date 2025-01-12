// JSONファイルを読み込む
fetch('https://yoka-ishikawa.github.io/kakeibo-app/expenses.json')
  .then(response => response.json()) // JSONデータをパース
  .then(data => {
    // データをテーブルに追加
    const expenseList = document.getElementById('expenseList');
    
    data.forEach(expense => {
      // 新しい行を作成
      const row = document.createElement('tr');
      
      // 日付セル
      const dateCell = document.createElement('td');
      dateCell.textContent = expense.date;
      row.appendChild(dateCell);
      
      // 収支タイプセル
      const typeCell = document.createElement('td');
      typeCell.textContent = expense.type === 'income' ? '収入' : '支出';
      row.appendChild(typeCell);
      
      // カテゴリセル
      const categoryCell = document.createElement('td');
      categoryCell.textContent = expense.category;
      row.appendChild(categoryCell);
      
      // 金額セル
      const amountCell = document.createElement('td');
      amountCell.textContent = `${expense.amount}円`;
      row.appendChild(amountCell);
      
      // テーブルに行を追加
      expenseList.appendChild(row);
    });
  })
  .catch(error => {
    console.error('JSON読み込みエラー:', error);
  });
// 共通のjsファイル、以下の処理はinexpen.jsで記載しているので削除する
// DOMが完全に読み込まれた後に年月と年月日の初期値を設定
document.addEventListener("DOMContentLoaded", function () {
  setDateValues();
});

// 年月と年月日の初期値を設定する関数
function setDateValues() {
  var now = new Date();

  // 年、月、日を取得
  var year = now.getFullYear();
  var month = String(now.getMonth() + 1).padStart(2, "0");
  var day = String(now.getDate()).padStart(2, "0");

  // YYYY形式（年）
  var select = document.getElementById("startyear");

  if (select) {
      for (var inputstartyear = year - 20; inputstartyear <= year; inputstartyear++) {
        var option = document.createElement("option");
        option.value = inputstartyear;
        option.textContent = inputstartyear;
        select.appendChild(option);
      }
    // 初期値を今年に設定
    select.value = year;
  }

  // YYYY-MM形式（年月）
  const inputstartmonth = document.getElementById("startmonth");
  if (inputstartmonth) {
    var startmonth = `${year}-${month}`;
    inputstartmonth.value = startmonth;
  }

  // YYYY-MM-DD形式（年月日）
  const inputstartdate = document.getElementById("startdate");
  if (inputstartdate) {
    var startdate = `${year}-${month}-${day}`;
    inputstartdate.value = startdate;
  }
}
