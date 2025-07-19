// DOMが読み込まれた後に実行
document.addEventListener('DOMContentLoaded', function () {
  loadDataList();
});

// データ一覧を読み込む関数
async function loadDataList() {
  try {
    const response = await fetch('/api/infokanri');

    if (!response.ok) {
      throw new Error(`データ取得に失敗しました。ステータス: ${response.status}`);
    }

    const data = await response.json();

    // 登録年月日の昇順（古い順）でソート
    data.sort((a, b) => {
      const dateA = new Date(a.registedAt);
      const dateB = new Date(b.registedAt);
      return dateA - dateB;
    });

    const tbody = document.querySelector('tbody');
    tbody.innerHTML = ''; // 既存のデータをクリア

    if (data.length === 0) {
      tbody.innerHTML = '<tr><td colspan="5">データがありません</td></tr>';
      return;
    }
    data.forEach((item) => {
      const row = document.createElement('tr');
      row.dataset.id = item.id;

      const typeText = item.type;
      const amountText = item.amount.toLocaleString() + '円';

      row.innerHTML = `
        <td class="date-cell">${item.registedAt}</td>
        <td class="type-cell">${typeText}</td>
        <td class="category-cell">${item.category}</td>
        <td class="amount-cell">${amountText}</td>
        <td>
          <div class="button-wrapper">
            <button class="edit-button">編集</button>
            <button class="delete-button">削除</button>
          </div>
        </td>
      `;

      tbody.appendChild(row);
    });

    // イベントリスナーを再設定
    setupEventListeners();
  } catch (error) {
    console.error('データ取得エラー:', error);
    alert('データの取得に失敗しました。');
  }
}

// イベントリスナーを設定する関数
function setupEventListeners() {
  // 編集ボタンの処理
  document.querySelectorAll('.edit-button').forEach((button) => {
    button.addEventListener('click', function () {
      const row = this.closest('tr');
      const id = row.dataset.id;
      const date = row.querySelector('.date-cell').textContent.trim();
      const type =
        row.querySelector('.type-cell').textContent === '収入' ? 'income' : 'expenditure';
      const category = row.querySelector('.category-cell').textContent.trim();
      const amount = row
        .querySelector('.amount-cell')
        .textContent.trim()
        .replace('円', '')
        .replace(/,/g, '');

      const params = new URLSearchParams({
        id,
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
  document.querySelectorAll('.delete-button').forEach((button) => {
    button.addEventListener('click', async function () {
      const row = this.closest('tr');
      const id = row.dataset.id;

      const isConfirmed = confirm('削除してもよろしいですか？');
      if (isConfirmed) {
        try {
          const response = await fetch(`/api/infokanri/${id}`, {
            method: 'DELETE',
          });

          if (!response.ok) {
            throw new Error(`削除に失敗しました。ステータス: ${response.status}`);
          }

          // 削除成功時のみ実行
          row.remove();
          alert('削除が完了しました。');
        } catch (error) {
          console.error('削除エラー:', error);
          alert('削除中にエラーが発生しました: ' + error.message);
        }
      } else {
        alert('削除がキャンセルされました。');
      }
    });
  });
}
