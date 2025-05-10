// DOMが完全に読み込まれた後に初期値を設定
document.addEventListener("DOMContentLoaded", function () {
  setDateValues(); // 年月日の初期値を設定
  updateDropdown("income"); // デフォルトは"収入"
  setupButtonControl(); // 登録ボタンの制御を設定

  // 収支登録一覧画面で編集ボタンを押したときの処理
  const params = new URLSearchParams(window.location.search);

  const date = params.get("date");
  const type = params.get("type");
  const category = params.get("category");
  const amount = params.get("amount");

  if(date) {
    const dateInput = document.getElementById("date");
    if (dateInput) dateInput.value = date; // 日付を設定
  }

  if(type) {
    const typeInput = document.querySelector(`input[name="type"][value="${type}"]`);
    if (typeInput) {
      typeInput.checked = true; // 収支タイプを設定
      if (typeof updateDropdown === 'function') {
        updateDropdown(type); // 選択されたタイプに応じてプルダウンを更新
      }
    }
  }

  if(category) {
    setTimeout(() => {
      const categorySelect = document.getElementById("category");
      if (categorySelect) categorySelect.value = category; // カテゴリを設定
    }, 100); // DOMの更新を待つために少し遅延させる
  }

  if(amount) {
    const amountInput = document.getElementById("amount");
    if (amountInput) amountInput.value = amount; // 金額を設定
  }
});

// ボタンを制御する関数
function setupButtonControl() {
  const inputs = document.querySelectorAll('#category, #amount');
  const button = document.getElementById("registButton");

  function checkInputs() {
    let allFilled = true;
    inputs.forEach(input => {
      if (input.value.trim() === '') {
        allFilled = false;
      }
    });

    console.log("ボタンの状態:", allFilled ? "有効" : "無効"); // ボタンの状態をログに出力

    if (allFilled) {
      button.disabled = false;
      button.classList.add('active');
    } else {
      button.disabled = true;
      button.classList.remove('active');
    }
  }

  checkInputs();

  // 入力・選択変更を監視
  inputs.forEach(input => {
    input.addEventListener('input', checkInputs);
    input.addEventListener('change', checkInputs);
  });
}

// 年月と年月日の初期値を設定する関数
function setDateValues() {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");

  // YYYY-MM-DD形式（年月日）
  const inputstartdate = document.getElementById("date");
  if (inputstartdate) {
      const date = `${year}-${month}-${day}`;
      inputstartdate.value = date;
  }
}

// プルダウンの選択肢データ
const options = {
  income: ["給料", "おこづかい", "賞与", "臨時収入", "その他"],
  expenditure: ["食事", "日用品", "衣服", "美容", "医療費", "光熱費", "住居費", "通信費", "その他"]
};

// プルダウンリストを更新する関数
function updateDropdown(selectedValue) {
  const dropdown = document.getElementById("category");
  dropdown.innerHTML = ""; // プルダウンの中身をクリア

  // プレースホルダー
  const placeholder = document.createElement("option");
  placeholder.textContent = "選択してください";
  placeholder.disabled = true;
  placeholder.selected = true;
  placeholder.value = "";
  dropdown.appendChild(placeholder);

  // 選択された値に応じたオプションを追加
  options[selectedValue].forEach(item => {
      const option = document.createElement("option");
      option.value = item;
      option.textContent = item;
      dropdown.appendChild(option);
  });
}

// フォームの内容を取得
document.getElementById("expenseForm").addEventListener("submit", async function (e) {
  e.preventDefault(); // フォームのデフォルトの送信を防止

  // ユーザに確認メッセージを表示
  const isConfirmed = confirm("登録してもよろしいですか？");

  console.log("confirm結果:", isConfirmed);  // 確認結果をログに出力

  if (isConfirmed) {
    // OKが押された場合、登録処理を実行
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

    // フォームをクリア
    setDateValues(); // 日付を今日の日付にリセット
    document.getElementById("category").value = ""; // カテゴリを未選択に
    document.getElementById("amount").value = ""; // 金額をクリア
    setupButtonControl(); // 登録ボタンの制御を設定

    // 登録完了ポップアップを表示
    alert("登録が完了しました！");

  } else {
    // キャンセルが押された場合、何もしない
    alert("登録がキャンセルされました");
  }
});
