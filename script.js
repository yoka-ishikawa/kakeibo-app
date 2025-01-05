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

  for (var inputstartyear = year; inputstartyear >= year - 20; inputstartyear--) {
    var option = document.createElement("option");
    option.value = inputstartyear;
    option.textContent = inputstartyear;
    select.appendChild(option);
  }
  if (select) {
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
