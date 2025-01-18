// DOMが完全に読み込まれた後に年の初期値を設定
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
}