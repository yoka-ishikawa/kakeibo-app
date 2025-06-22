document.addEventListener("DOMContentLoaded", function () {
  setDateValues(); // DOMが完全に読み込まれた後に初期値を設定

  // 初期表示の状態に基づいて表示内容を更新
  let periodSelect = document.getElementById("period");
  if (periodSelect) {
    periodSelect.addEventListener("change", function () {
      let period = this.value;

      if (period === "monthly") {
        document.getElementById("month-container").style.display = "block"; // 月間選択時にカレンダー表示
        document.getElementById("year-container").style.display = "none"; // 年間選択肢を非表示
        document.getElementById("report-area").style.display = "none"; // レポートエリアを非表示
      } else if (period === "yearly") {
        document.getElementById("month-container").style.display = "none"; // 月間選択肢を非表示
        document.getElementById("year-container").style.display = "block"; // 年間選択肢を表示
        document.getElementById("report-area").style.display = "none"; // レポートエリアを非表示
      }
    });

    // 初期状態を反映
    periodSelect.dispatchEvent(new Event("change"));
  }
  // レポートを表示ボタンをクリックした時の処理
  const reportButton = document.getElementById("displayButton");

  reportButton.addEventListener("click", async function () {
    try {
      // レポートデータを取得
      const response = await fetch("/api/infokanri/report");
      const data = await response.json();

      // レポートエリアを表示
      document.getElementById("report-area").style.display = "block";

      // 収支を計算
      let totalIncome = 0;
      let totalExpenditure = 0;      data.forEach((item) => {
        if (item.syubetu === "収入") {      // type → syubetu, "income" → "収入"
          totalIncome += item.kingaku;      // amount → kingaku
        } else if (item.syubetu === "支出") { // type → syubetu, "expenditure" → "支出"
          totalExpenditure += item.kingaku; // amount → kingaku
        }
      });

      const balance = totalIncome - totalExpenditure;

      // レポート表示を更新
      document.querySelector(".income").textContent = `収入: ¥ ${totalIncome.toLocaleString()}`;
      document.querySelector(".expenditure").textContent = `支出: ¥ ${totalExpenditure.toLocaleString()}`;
      document.querySelector(".income-expenditure").textContent = `収支: ¥ ${balance.toLocaleString()}`;

      // テーブルを更新
      const tbody = document.querySelector("tbody");
      tbody.innerHTML = "";

      if (data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4">データがありません</td></tr>';
        return;
      }

      data.forEach((item) => {
        const row = document.createElement("tr");
        const typeText = item.type === "income" ? "収入" : "支出";
        const amountText = item.amount.toLocaleString() + "円";

        row.innerHTML = `
          <td>${item.registeredAt}</td>
          <td>${typeText}</td>
          <td>${item.category}</td>
          <td>${amountText}</td>
        `;

        tbody.appendChild(row);
      });

    } catch (error) {
      console.error("レポートデータ取得エラー:", error);
      alert("レポートデータの取得に失敗しました。");
    }
  });
});

// 年月と年月日の初期値を設定する関数
function setDateValues() {
  let now = new Date();

  // 年、月を取得
  let year = now.getFullYear();
  let month = String(now.getMonth() + 1).padStart(2, "0");

  // 初期値として現在の日付を設定する
  const inputstartmonth = document.getElementById("startmonth");
  if (inputstartmonth) {
    let startmonth = `${year}-${month}`;
    inputstartmonth.value = startmonth; // 年月（YYYY-MM）形式
  }

  // 初期値の設定後に年選択肢を設定する
  let select = document.getElementById("startyear");
  if (select) {
    for (
      let inputstartyear = year - 20;
      inputstartyear <= year;
      inputstartyear++
    ) {
      let option = document.createElement("option");
      option.value = inputstartyear;
      option.textContent = inputstartyear;
      select.appendChild(option);
    }
    // 初期値を今年に設定
    select.value = year;
  }
}
