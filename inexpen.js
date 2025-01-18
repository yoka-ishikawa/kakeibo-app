// DOMが完全に読み込まれた後に年月日の初期値を設定
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
  
    // YYYY-MM-DD形式（年月日）
    const inputstartdate = document.getElementById("date");
    if (inputstartdate) {
      var date = `${year}-${month}-${day}`;
      inputstartdate.value = date;
    }
  }

  // プルダウンの選択肢データ
const options = {
  income: ["選択してください",  "給料", "おこづかい", "賞与", "臨時収入", "その他"],
  expenditure: ["選択してください", "食事", "日用品", "衣服", "美容", "医療費", "光熱費", "住居費", "通信費", "その他"]
};

// プルダウンリストを更新する関数
function updateDropdown(selectedValue) {
  const dropdown = document.getElementById("category");

  // プルダウンの中身をクリア
  dropdown.innerHTML = "";

  // 選択された値に応じたオプションを追加
  options[selectedValue].forEach(item => {
    const option = document.createElement("option");
    option.value = item;
    option.textContent = item;
    dropdown.appendChild(option);
  });
}

// 初期化
document.addEventListener("DOMContentLoaded", () => {
  updateDropdown("income"); // デフォルトは"収入"
});

// フォームの内容を取得
document
  .getElementById("expenseForm")
  .addEventListener("submit", async function (e) {
    e.preventDefault();

    const date = document.getElementById("date").value; // 開始日
    const selectedOption = document.querySelector('input[name="type"]:checked'); // 収入か支出か
    const incomeOrExpenditure = selectedOption
    ? selectedOption.value === "income"
      ? "収入"
      : "支出"
    : null; // "収入" または "支出" を取得
    const categorySelect = document.getElementById("category"); // カテゴリ
    const category = categorySelect.options[categorySelect.selectedIndex].text; // カテゴリ
    const amount = document.getElementById("amount").value; // 金額

    // 画面に表示
    const expensesList = document.getElementById("expenses");
    const listItem = document.createElement("li");
    listItem.textContent = `${date} ${incomeOrExpenditure} ${category} ${amount}円`;
    expensesList.appendChild(listItem);

    // フォームをクリア
    setDateValues(); // 日付を今日の日付にリセット
    document.querySelector('input[name="type"]:checked').checked = false; // ラジオボタンの選択解除
    document.getElementById("category").value = ""; // カテゴリを未選択に
    document.getElementById("amount").value = ""; // 金額をクリア

    /*   // データベースに登録
  try {
    const response = await fetch('/api/expenses', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        startdate: startdate, // 開始日
        income: income, // 収入
        expenditure: expenditure, // 支出 
        category: category, // カテゴリ
        tentacles: tentacles // 金額 
      })
    });
    
    if (response.ok) {
      // 画面に表示
      const expensesList = document.getElementById('expenses');
      const listItem = document.createElement('li');
      listItem.textContent = `${startdate} ${income} ${expenditure} ${category} ${tentacles}円`;
      expensesList.appendChild(listItem);
  
    }
  } catch (error) {
    console.error('登録エラー:', error);
    alert('登録に失敗しました');
  } */
  });
