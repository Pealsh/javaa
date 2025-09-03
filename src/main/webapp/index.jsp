<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<!DOCTYPE html> 
<html lang="ja"> 
<head> 
    <meta charset="UTF-8"> 
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>医療クリニック予約システム | MediReserve</title> 
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css"> 
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <meta name="description" content="医療クリニック専用の予約システム。診療科選択、医師指名、症状入力で安心の医療予約を。">
    <script src="${pageContext.request.contextPath}/js/modern-ui.js" defer></script>
</head> 
<body> 
    <div class="container"> 
        <h1>診療予約申込み</h1> 
        <form action="reservation" method="post"> 
            <input type="hidden" name="action" value="add"> 
            <p class="error-message"><c:out value="${errorMessage}"/></p>
            
            <div class="form-section">
                <h3>患者様情報</h3>
                <p> 
                    <label for="name">お名前 *</label> 
                    <input type="text" id="name" name="name" value="<c:out value="${param.name}"/>" required placeholder="山田 太郎"> 
                </p> 
                <p> 
                    <label for="phoneNumber">電話番号 *</label> 
                    <input type="tel" id="phoneNumber" name="phoneNumber" value="<c:out value="${param.phoneNumber}"/>" required placeholder="090-1234-5678"> 
                </p>
                <p> 
                    <label for="insuranceNumber">保険証番号 *</label> 
                    <input type="text" id="insuranceNumber" name="insuranceNumber" value="<c:out value="${param.insuranceNumber}"/>" required placeholder="12345678"> 
                </p>
            </div>
            
            <div class="form-section">
                <h3>診療情報</h3>
                <p> 
                    <label for="department">診療科 *</label> 
                    <select id="department" name="department" required>
                        <option value="">診療科を選択してください</option>
                        <option value="内科" <c:if test="${param.department == '内科'}">selected</c:if>>内科</option>
                        <option value="外科" <c:if test="${param.department == '外科'}">selected</c:if>>外科</option>
                        <option value="皮膚科" <c:if test="${param.department == '皮膚科'}">selected</c:if>>皮膚科</option>
                        <option value="眼科" <c:if test="${param.department == '眼科'}">selected</c:if>>眼科</option>
                        <option value="耳鼻咽喉科" <c:if test="${param.department == '耳鼻咽喉科'}">selected</c:if>>耳鼻咽喉科</option>
                        <option value="整形外科" <c:if test="${param.department == '整形外科'}">selected</c:if>>整形外科</option>
                        <option value="小児科" <c:if test="${param.department == '小児科'}">selected</c:if>>小児科</option>
                        <option value="婦人科" <c:if test="${param.department == '婦人科'}">selected</c:if>>婦人科</option>
                    </select>
                </p>
                <p> 
                    <label for="doctor">担当医師希望</label> 
                    <select id="doctor" name="doctor">
                        <option value="">指定なし</option>
                        <option value="田中医師" <c:if test="${param.doctor == '田中医師'}">selected</c:if>>田中医師</option>
                        <option value="佐藤医師" <c:if test="${param.doctor == '佐藤医師'}">selected</c:if>>佐藤医師</option>
                        <option value="鈴木医師" <c:if test="${param.doctor == '鈴木医師'}">selected</c:if>>鈴木医師</option>
                        <option value="高橋医師" <c:if test="${param.doctor == '高橋医師'}">selected</c:if>>高橋医師</option>
                    </select>
                </p>
                <p> 
                    <label for="reservation_time">希望診療日時 *</label> 
                    <input type="datetime-local" id="reservation_time" name="reservation_time" value="<c:out 
value="${param.reservation_time}"/>" required> 
                </p>
            </div>
            
            <div class="form-section">
                <h3>症状・医療情報</h3>
                <p> 
                    <label for="symptoms">症状・主訴 *</label> 
                    <textarea id="symptoms" name="symptoms" rows="3" required placeholder="どのような症状でご来院されますか？"><c:out value="${param.symptoms}"/></textarea>
                </p>
                <p> 
                    <label for="allergies">アレルギー</label> 
                    <textarea id="allergies" name="allergies" rows="2" placeholder="薬剤アレルギー、食物アレルギーなど"><c:out value="${param.allergies}"/></textarea>
                </p>
                <p> 
                    <label for="medications">現在服用中の薬</label> 
                    <textarea id="medications" name="medications" rows="2" placeholder="服用中のお薬があれば記入してください"><c:out value="${param.medications}"/></textarea>
                </p>
                <p> 
                    <label for="emergencyContact">緊急連絡先</label> 
                    <input type="tel" id="emergencyContact" name="emergencyContact" value="<c:out value="${param.emergencyContact}"/>" placeholder="080-9876-5432"> 
                </p>
                <p> 
                    <label for="notes">その他・備考</label> 
                    <textarea id="notes" name="notes" rows="2" placeholder="その他ご要望やご質問があれば記入してください"><c:out value="${param.notes}"/></textarea>
                </p>
            </div> 
            <div class="button-group"> 
                <input type="submit" value="診療予約を申し込む"> 
            </div> 
        </form> 
 
        <hr> 
 
        <h2>予約データ一括インポート</h2> 
        <div class="import-info">
            <p>CSV形式のファイルから複数の予約データを一括登録できます。</p>
            <small>※CSVフォーマット：名前,電話番号,診療科,医師,症状,日時,保険証番号,緊急連絡先,アレルギー,服薬,備考</small>
        </div>
        <form action="reservation" method="post" enctype="multipart/form-data"> 
            <input type="hidden" name="action" value="import_csv"> 
            <p> 
                <label for="csvFile">CSV ファイルを選択:</label> 
                <input type="file" id="csvFile" name="csvFile" accept=".csv" required> 
            </p> 
            <div class="button-group"> 
                <input type="submit" value="データをインポート"> 
            </div>
        </form> 
        <p class="success-message"><c:out value="${successMessage}"/></p> 
 
        <div class="button-group"> 
            <a href="reservation?action=list" class="button secondary">予約一覧を確認</a> 
        </div> 
    </div> 

    <script>
        // ページロード時にボタンの状態をリセット
        window.addEventListener('beforeunload', function() {
            const submitBtns = document.querySelectorAll('input[type="submit"]');
            submitBtns.forEach(btn => {
                btn.disabled = false;
                if (btn.form && btn.form.querySelector('input[name="action"][value="add"]')) {
                    btn.value = '診療予約を申し込む';
                } else if (btn.form && btn.form.querySelector('input[name="action"][value="import_csv"]')) {
                    btn.value = 'データをインポート';
                }
            });
        });

        // 予約フォームの検証とエラーハンドリング
        document.addEventListener('DOMContentLoaded', function() {
            // ページロード時にボタン状態をリセット
            const submitBtns = document.querySelectorAll('input[type="submit"]');
            submitBtns.forEach(btn => {
                btn.disabled = false;
                if (btn.form && btn.form.querySelector('input[name="action"][value="add"]')) {
                    btn.value = '診療予約を申し込む';
                } else if (btn.form && btn.form.querySelector('input[name="action"][value="import_csv"]')) {
                    btn.value = 'データをインポート';
                }
            });

            // ページロード時にフォームの入力内容をリセット
            const reservationForm = document.querySelector('form[action="reservation"]');
            if (reservationForm && reservationForm.querySelector('input[name="action"][value="add"]')) {
                // より確実なリセット方法を使用
                setTimeout(function() {
                    // フォーム全体をリセット
                    reservationForm.reset();
                    
                    // 個別にクリア（reset()で対応できない場合の保険）
                    document.getElementById('name').value = '';
                    document.getElementById('phoneNumber').value = '';
                    document.getElementById('insuranceNumber').value = '';
                    document.getElementById('department').selectedIndex = 0;
                    document.getElementById('doctor').selectedIndex = 0;
                    document.getElementById('reservation_time').value = '';
                    document.getElementById('symptoms').value = '';
                    const allergies = document.getElementById('allergies');
                    if (allergies) allergies.value = '';
                    const medications = document.getElementById('medications');
                    if (medications) medications.value = '';
                    const emergencyContact = document.getElementById('emergencyContact');
                    if (emergencyContact) emergencyContact.value = '';
                    const notes = document.getElementById('notes');
                    if (notes) notes.value = '';
                    
                    // エラースタイルを削除
                    const allFields = reservationForm.querySelectorAll('input, select, textarea');
                    allFields.forEach(field => {
                        field.classList.remove('error-field');
                    });
                    
                    // エラーメッセージをクリア
                    const errorContainer = document.querySelector('.error-message');
                    if (errorContainer) {
                        errorContainer.textContent = '';
                        errorContainer.style.display = 'none';
                    }
                }, 100); // 少し遅延させてDOM完全読み込み後に実行
            }

            // フォーム検証の設定
            if (reservationForm && reservationForm.querySelector('input[name="action"][value="add"]')) {
                reservationForm.addEventListener('submit', function(e) {
                    e.preventDefault(); // デフォルトの送信を防ぐ
                    
                    // 必須フィールドのチェック
                    const requiredFields = [
                        { id: 'name', name: 'お名前' },
                        { id: 'phoneNumber', name: '電話番号' },
                        { id: 'insuranceNumber', name: '保険証番号' },
                        { id: 'department', name: '診療科' },
                        { id: 'reservation_time', name: '希望診療日時' },
                        { id: 'symptoms', name: '症状・主訴' }
                    ];
                    
                    let firstEmptyField = null;
                    let errorMessages = [];
                    
                    // 各必須フィールドをチェック
                    requiredFields.forEach(field => {
                        const element = document.getElementById(field.id);
                        if (element && (!element.value || element.value.trim() === '')) {
                            if (!firstEmptyField) {
                                firstEmptyField = element;
                            }
                            errorMessages.push(field.name + 'が入力されていません。');
                            element.classList.add('error-field');
                        } else if (element) {
                            element.classList.remove('error-field');
                        }
                    });
                    
                    // エラーがある場合
                    if (errorMessages.length > 0) {
                        // エラーメッセージを表示
                        const errorContainer = document.querySelector('.error-message');
                        if (errorContainer) {
                            errorContainer.textContent = errorMessages[0]; // 最初のエラーのみ表示
                            errorContainer.style.display = 'block';
                        }
                        
                        // 最初の空フィールドにフォーカス
                        if (firstEmptyField) {
                            firstEmptyField.focus();
                            firstEmptyField.scrollIntoView({ behavior: 'smooth', block: 'center' });
                        }
                        
                        return false;
                    }
                    
                    // エラーがない場合、送信ボタンを無効化してフォームを送信
                    const submitBtn = this.querySelector('input[type="submit"]');
                    if (submitBtn) {
                        submitBtn.disabled = true;
                        submitBtn.value = '処理中...';
                    }
                    
                    // エラーメッセージをクリア
                    const errorContainer = document.querySelector('.error-message');
                    if (errorContainer) {
                        errorContainer.textContent = '';
                        errorContainer.style.display = 'none';
                    }
                    
                    // フォームを送信
                    this.submit();
                });
                
                // フィールドに入力があったらエラースタイルを削除
                const allFields = document.querySelectorAll('#name, #phoneNumber, #insuranceNumber, #department, #reservation_time, #symptoms');
                allFields.forEach(field => {
                    field.addEventListener('input', function() {
                        this.classList.remove('error-field');
                    });
                });
            }
        });
    </script>
</body> 
</html> 