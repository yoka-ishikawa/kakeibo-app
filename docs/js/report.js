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

  reportButton.addEventListener("click", function () {
    document.getElementById("report-area").style.display = "block"; // レポートを表示

    // ここでレポートのデータを取得して表示する処理を追加
    // 例: fetch('/api/report')
    // プルダウン選択肢を基に年間のデータ、または月間のデータを取得
    // 例: fetch(`/api/report?period=${periodSelect.value}`)
    // .then(response => response.json())
    // .then(data => {
    //   // レポートデータを表示する処理
    // });
    // 例: document.getElementById("report-content").innerHTML = data;
    // ここではダミーデータを表示
    // const reportContent = document.getElementById("report-area");
    // if (periodSelect.value === "monthly") {
    //   reportContent.innerHTML = "<h2>月間レポート</h2><p>ここに月間レポートの内容が表示されます。</p>";
    //} else if (periodSelect.value === "yearly") {
    //  reportContent.innerHTML = "<h2>年間レポート</h2><p>ここに年間レポートの内容が表示されます。</p>";
    //}
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
