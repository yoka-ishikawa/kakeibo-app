// DOMが完全に読み込まれた後に年月の初期値を設定
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
  
    // YYYY-MM形式（年月）
    const inputstartmonth = document.getElementById("startmonth");
    if (inputstartmonth) {
      var startmonth = `${year}-${month}`;
      inputstartmonth.value = startmonth;
    }
}