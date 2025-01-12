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

document
  .getElementById("expenseForm")
  .addEventListener("submit", async function (e) {
    e.preventDefault();

    const startdate = document.getElementById("date").value; // 開始日
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
    listItem.textContent = `${startdate} ${incomeOrExpenditure} ${category} ${amount}円`;
    expensesList.appendChild(listItem);

/*     // フォームをクリア
    document.getElementById("startdate").value = "";
    document.getElementById("income").value = "";
    document.getElementById("expenditure").value = "";
    document.getElementById("category-select").value = "";
    document.getElementById("tentacles").value = ""; */

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
      
      // フォームをクリア
      document.getElementById('startdate').value = '';
      document.getElementById('income').value = '';
      document.getElementById('expenditure').value = '';
      document.getElementById('category-select').value = '';
      document.getElementById('tentacles').value = '';
    }
  } catch (error) {
    console.error('登録エラー:', error);
    alert('登録に失敗しました');
  } */
  });
