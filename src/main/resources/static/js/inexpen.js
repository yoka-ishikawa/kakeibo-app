/**
 * 収支登録画面の初期化処理
 * - フォーム要素の初期値設定
 * - ユーザー認証トークンの取得・管理
 * - 編集モード時のデータ復元
 */
document.addEventListener('DOMContentLoaded', function () {
  // フォーム初期化
  setDateValues();
  updateDropdown('income');
  setupButtonControl();

  // ユーザー認証トークンの初期化
  initializeUserToken();

  // 編集モード時のデータ復元処理
  // URLパラメータから既存データを取得してフォームに設定
  restoreFormDataFromURL();
});

/**
 * ユーザー認証トークンの初期化
 * localStorageにトークンがない場合、サーバーから取得
 */
function initializeUserToken() {
  if (!localStorage.getItem('userToken')) {
    fetch('/api/user/token')
      .then((response) => response.json())
      .then((data) => {
        localStorage.setItem('userToken', data.userToken);
      })
      .catch(() => {
        // サーバーからの取得に失敗した場合のフォールバック
        localStorage.setItem('userToken', 'fallback_token_' + Date.now());
      });
  }
}

/**
 * URLパラメータからフォームデータを復元
 * 編集モード時に使用
 */
function restoreFormDataFromURL() {
  const params = new URLSearchParams(window.location.search);

  const id = params.get('id');
  const date = params.get('date');
  const type = params.get('type');
  const category = params.get('category');
  const amount = params.get('amount');

  if (date) {
    const dateInput = document.getElementById('date');
    if (dateInput) dateInput.value = date; // 日付を設定
  }

  if (type) {
    const typeInput = document.querySelector(`input[name="type"][value="${type}"]`);
    if (typeInput) {
      typeInput.checked = true; // 収支タイプを設定
      if (typeof updateDropdown === 'function') {
        updateDropdown(type); // 選択されたタイプに応じてプルダウンを更新
      }
    }
  }

  if (category) {
    setTimeout(() => {
      const categorySelect = document.getElementById('category');
      if (categorySelect) categorySelect.value = category; // カテゴリを設定
    }, 100); // DOMの更新を待つために少し遅延させる
  }

  if (amount) {
    const amountInput = document.getElementById('amount');
    if (amountInput) amountInput.value = amount; // 金額を設定
  }
}

// ボタンを制御する関数
function setupButtonControl() {
  const inputs = document.querySelectorAll('#category, #amount');
  const button = document.getElementById('registButton');

  function checkInputs() {
    let allFilled = true;
    inputs.forEach((input) => {
      if (input.value.trim() === '') {
        allFilled = false;
      }
    });

    console.log('ボタンの状態:', allFilled ? '有効' : '無効'); // ボタンの状態をログに出力

    if (allFilled) {
      button.disabled = false;
      button.classList.add('active');
    } else {
      button.disabled = true;
      button.classList.remove('active');
    }
  }

  // 初期状態をチェック
  checkInputs();

  // 入力値変更時にボタン状態を再チェック
  inputs.forEach((input) => {
    input.addEventListener('input', checkInputs);
    input.addEventListener('change', checkInputs);
  });
}

/**
 * 日付入力フィールドに今日の日付を設定
 */
function setDateValues() {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const todayFormatted = `${year}-${month}-${day}`;

  const dateInput = document.getElementById('date');
  if (dateInput) {
    dateInput.value = todayFormatted;
  }
}

// プルダウンの選択肢データ
const options = {
  income: ['給料', 'おこづかい', '賞与', '臨時収入', 'その他'],
  expenditure: ['食事', '日用品', '衣服', '美容', '医療費', '光熱費', '住居費', '通信費', 'その他'],
};

// プルダウンリストを更新する関数
function updateDropdown(selectedValue) {
  const dropdown = document.getElementById('category');
  dropdown.innerHTML = ''; // プルダウンの中身をクリア

  // プレースホルダー
  const placeholder = document.createElement('option');
  placeholder.textContent = '選択してください';
  placeholder.disabled = true;
  placeholder.selected = true;
  placeholder.value = '';
  dropdown.appendChild(placeholder);

  // 選択された値に応じたオプションを追加
  options[selectedValue].forEach((item) => {
    const option = document.createElement('option');
    option.value = item;
    option.textContent = item;
    dropdown.appendChild(option);
  });
}

// フォームの内容を取得
document.getElementById('expenseForm').addEventListener('submit', async function (e) {
  e.preventDefault(); // フォームのデフォルトの送信を防止

  // ユーザに確認メッセージを表示
  const isConfirmed = confirm('登録してもよろしいですか？');

  console.log('confirm結果:', isConfirmed); // 確認結果をログに出力

  if (isConfirmed) {
    // OKが押された場合、登録処理を実行
    const date = document.getElementById('date').value; // 開始日
    const selectedOption = document.querySelector('input[name="type"]:checked'); // 収入か支出か
    const incomeOrExpenditure = selectedOption ? selectedOption.value : null; // "income" または "expenditure" を取得

    const categorySelect = document.getElementById('category'); // カテゴリ
    const category = categorySelect.options[categorySelect.selectedIndex].text; // カテゴリ
    const amount = document.getElementById('amount').value; // 金額

    // 編集モードの判定（tryブロックの外で定義）
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    const isEditMode = id !== null;
    // 収支登録またはAPIにリクエストを送信
    try {
      // リクエストペイロードを作成（新しいテーブル構造に対応）
      const payload = {
        hiduke: date, // registeredAt → hiduke
        syubetu: incomeOrExpenditure === 'income' ? '収入' : '支出', // type → syubetu (日本語)
        naisyo: category, // category → naisyo
        kingaku: parseInt(amount, 10), // amount → kingaku
      };
      console.log('送信するデータ:', payload); // 送信するデータをログに出力

      // ローカルストレージからユーザーIDを取得（userTokenからuserIdに変更）
      let userId = localStorage.getItem('userId');
      if (!userId) {
        userId = 'anonymous_user_' + Date.now();
        localStorage.setItem('userId', userId);
      }
      // リクエストメソッドとURLを設定
      const method = isEditMode ? 'PUT' : 'POST';
      const url = isEditMode ? `/api/infokanri/${id}` : '/api/infokanri';

      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json',
          'X-User-Id': userId, // X-User-Token → X-User-Id
        },
        body: JSON.stringify(payload),
      });
      if (!response.ok) {
        const errorText = await response.text();
        console.error('登録に失敗:', {
          status: response.status,
          statusText: response.statusText,
          errorText: errorText,
        });
        throw new Error(
          `登録に失敗しました。ステータス: ${response.status}, メッセージ: ${errorText}`
        );
      }

      // レスポンスの詳細情報をログ出力
      console.log('レスポンス詳細:', {
        status: response.status,
        statusText: response.statusText,
        headers: response.headers,
        ok: response.ok,
      });

      // レスポンスの生テキストを確認
      const responseClone = response.clone();
      const responseText = await responseClone.text();
      console.log('レスポンス生テキスト:', responseText);
      console.log('レスポンステキスト長:', responseText.length);

      // 登録/更新成功時の処理
      let result;
      try {
        result = await response.json();
        console.log('サーバーレスポンス:', result);

        // 成功レスポンスの検証
        if (!result || !result.id) {
          throw new Error('サーバーからの応答が不正です');
        }

        console.log(isEditMode ? '更新成功:' : '登録成功:', result);
      } catch (parseError) {
        console.error('レスポンス解析エラー:', parseError);
        console.error('レスポンステキスト:', responseText);
        throw new Error((isEditMode ? '更新' : '登録') + 'の処理中に問題が発生しました');
      }
    } catch (error) {
      console.error(isEditMode ? '更新エラー:' : '登録エラー:', error);
      alert((isEditMode ? '更新' : '登録') + '中にエラーが発生しました: ' + error.message);
      return; // エラーが発生した場合は処理を中断
    }

    // ここまで到達した場合のみ成功処理を実行
    // フォームをクリア（新規登録の場合のみ）
    if (!isEditMode) {
      setDateValues(); // 日付を今日の日付にリセット
      document.getElementById('category').value = ''; // カテゴリを未選択に
      document.getElementById('amount').value = ''; // 金額をクリア
      setupButtonControl(); // 登録ボタンの制御を設定
    }

    // 成功ポップアップを表示（成功が確認された場合のみ）
    alert((isEditMode ? '更新' : '登録') + 'が完了しました！');

    // 編集モードの場合は一覧画面に戻る
    if (isEditMode) {
      window.location.href = 'list.html';
    }
  } else {
    // キャンセルが押された場合、何もしない
    alert('登録がキャンセルされました');
  }
});
