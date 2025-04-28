document.addEventListener("DOMContentLoaded", function () {
  setDateValues(); // DOMが完全に読み込まれた後に初期値を設定
  // 初期表示の状態に基づいて表示内容を更新
  var periodSelect = document.getElementById("period");
  periodSelect.dispatchEvent(new Event('change')); // 初期表示の状態に基づいて「月間」または「年間」を切り替え
});

// 年月と年月日の初期値を設定する関数
function setDateValues() {
  var now = new Date();

  // 年、月を取得
  var year = now.getFullYear();
  var month = String(now.getMonth() + 1).padStart(2, "0");

  // 初期値として現在の日付を設定する
  const inputstartmonth = document.getElementById("startmonth");
  if (inputstartmonth) {
    var startmonth = `${year}-${month}`;
    inputstartmonth.value = startmonth;  // 年月（YYYY-MM）形式
  }

  // 初期値の設定後に年選択肢を設定する
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
}

// 期間選択時に表示を切り替える処理
document.getElementById("period").addEventListener("change", function() {
  var period = this.value;

  if (period === "monthly") {
    document.getElementById("calendar-container").style.display = "block";  // 月間選択時にカレンダー表示
    document.getElementById("year-container").style.display = "none";     // 年間選択肢を非表示
  } else if (period === "yearly") {
    document.getElementById("calendar-container").style.display = "none";  // 月間選択肢を非表示
    document.getElementById("year-container").style.display = "block";     // 年間選択肢を表示
  }
});
