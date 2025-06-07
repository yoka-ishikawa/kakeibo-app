// 編集ボタンの処理
const editButtons = document.querySelectorAll(".edit-button");
editButtons.forEach((button) => {
  button.addEventListener("click", function () {
    const row = this.closest("tr");
    const expenseId = row.dataset.expenseId;
    const date = row.querySelector(".date-cell").textContent.trim();
    const type =
      row.querySelector(".type-cell").textContent === "収入"
        ? "income"
        : "expenditure";
    const category = row.querySelector(".category-cell").textContent.trim();
    const amount = row
      .querySelector(".amount-cell")
      .textContent.trim()
      .replace("円", "")
      .replace(/,/g, ""); // 金額から「円」とカンマを削除

    const params = new URLSearchParams({
      expenseId,
      date,
      type,
      category,
      amount,
    });

    const editUrl = `inexpen.html?${params.toString()}`;

    window.location.href = editUrl;
  });
});
// 削除ボタンの処理
document.querySelectorAll(".delete-button").forEach((button) => {
  button.addEventListener("click", function () {
    // 削除ボタンがクリックされたときの処理
    const row = this.closest("tr"); // ボタンが属する行を取得
    const expenseId = row.dataset.expenseId; // 行のデータ属性からexpenseIdを取得

    // 確認ダイアログを表示
    const isConfirmed = confirm("削除してもよろしいですか？");
    if (isConfirmed) {
      row.remove(); // 見た目だけ削除実行
      alert("削除が完了しました。");

      // 削除処理を実行（DB連携後にコメントアウトを解除）
      /*
      fetch('/api/expense/${expenseId}', {
        method: 'DELETE'
      })
      .then(Response => {
        if (Response.ok) {
          // 削除成功時、行をテーブルから削除
          row.remove();
          alert("削除が完了しました。");
        } else {
          alert("削除に失敗しました。");
        }
      })
      .catch(error => {
        console.error('削除エラー:', error);
        alert("削除中にエラーが発生しました。");
      });
      */
    } else {
      alert("削除がキャンセルされました。");
    }
  });
});
