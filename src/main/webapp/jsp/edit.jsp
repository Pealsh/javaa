<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!DOCTYPE html> 
<html lang="ja"> 
<head> 
    <meta charset="UTF-8"> 
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>診療予約編集 | MediReserve</title> 
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css"> 
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <meta name="description" content="診療予約情報を編集・更新できる医療クリニック用システム。">
    <script src="${pageContext.request.contextPath}/js/modern-ui.js" defer></script>
</head> 
<body> 
    <div class="container"> 
        <h1>診療予約情報編集</h1> 
        <form action="reservation" method="post"> 
            <input type="hidden" name="action" value="update"> 
            <input type="hidden" name="id" value="${reservation.id}"> 
            
            <p class="error-message"><c:out value="${errorMessage}"/></p>
            
            <div class="form-section">
                <h3>患者様情報</h3>
                <p> 
                    <label for="name">お名前 *</label> 
                    <input type="text" id="name" name="name" value="<c:out value="${reservation.name}"/>" required placeholder="山田 太郎"> 
                </p> 
                <p> 
                    <label for="phoneNumber">電話番号 *</label> 
                    <input type="tel" id="phoneNumber" name="phoneNumber" value="<c:out value="${reservation.phoneNumber}"/>" required placeholder="090-1234-5678"> 
                </p>
                <p> 
                    <label for="insuranceNumber">保険証番号 *</label> 
                    <input type="text" id="insuranceNumber" name="insuranceNumber" value="<c:out value="${reservation.insuranceNumber}"/>" required placeholder="12345678"> 
                </p>
            </div>
            
            <div class="form-section">
                <h3>診療情報</h3>
                <p> 
                    <label for="department">診療科 *</label> 
                    <select id="department" name="department" required>
                        <option value="">診療科を選択してください</option>
                        <option value="内科" <c:if test="${reservation.department == '内科'}">selected</c:if>>内科</option>
                        <option value="外科" <c:if test="${reservation.department == '外科'}">selected</c:if>>外科</option>
                        <option value="皮膚科" <c:if test="${reservation.department == '皮膚科'}">selected</c:if>>皮膚科</option>
                        <option value="眼科" <c:if test="${reservation.department == '眼科'}">selected</c:if>>眼科</option>
                        <option value="耳鼻咽喉科" <c:if test="${reservation.department == '耳鼻咽喉科'}">selected</c:if>>耳鼻咽喉科</option>
                        <option value="整形外科" <c:if test="${reservation.department == '整形外科'}">selected</c:if>>整形外科</option>
                        <option value="小児科" <c:if test="${reservation.department == '小児科'}">selected</c:if>>小児科</option>
                        <option value="婦人科" <c:if test="${reservation.department == '婦人科'}">selected</c:if>>婦人科</option>
                    </select>
                </p>
                <p> 
                    <label for="doctor">担当医師希望</label> 
                    <select id="doctor" name="doctor">
                        <option value="">指定なし</option>
                        <option value="田中医師" <c:if test="${reservation.doctor == '田中医師'}">selected</c:if>>田中医師</option>
                        <option value="佐藤医師" <c:if test="${reservation.doctor == '佐藤医師'}">selected</c:if>>佐藤医師</option>
                        <option value="鈴木医師" <c:if test="${reservation.doctor == '鈴木医師'}">selected</c:if>>鈴木医師</option>
                        <option value="高橋医師" <c:if test="${reservation.doctor == '高橋医師'}">selected</c:if>>高橋医師</option>
                    </select>
                </p>
                <p> 
                    <label for="reservation_time">希望診療日時 *</label> 
                    <input type="datetime-local" id="reservation_time" name="reservation_time" value="<c:out 
value="${reservation.reservationTime}"/>" required> 
                </p>
                <p>
                    <label for="status">予約状態</label>
                    <select id="status" name="status">
                        <option value="PENDING" <c:if test="${reservation.status == 'PENDING'}">selected</c:if>>予約申込中</option>
                        <option value="CONFIRMED" <c:if test="${reservation.status == 'CONFIRMED'}">selected</c:if>>予約確定</option>
                        <option value="CANCELLED" <c:if test="${reservation.status == 'CANCELLED'}">selected</c:if>>キャンセル</option>
                        <option value="COMPLETED" <c:if test="${reservation.status == 'COMPLETED'}">selected</c:if>>診療完了</option>
                        <option value="NO_SHOW" <c:if test="${reservation.status == 'NO_SHOW'}">selected</c:if>>未来院</option>
                    </select>
                </p>
            </div>
            
            <div class="form-section">
                <h3>症状・医療情報</h3>
                <p> 
                    <label for="symptoms">症状・主訴 *</label> 
                    <textarea id="symptoms" name="symptoms" rows="3" required placeholder="どのような症状でご来院されますか？"><c:out value="${reservation.symptoms}"/></textarea>
                </p>
                <p> 
                    <label for="allergies">アレルギー</label> 
                    <textarea id="allergies" name="allergies" rows="2" placeholder="薬剤アレルギー、食物アレルギーなど"><c:out value="${reservation.allergies}"/></textarea>
                </p>
                <p> 
                    <label for="medications">現在服用中の薬</label> 
                    <textarea id="medications" name="medications" rows="2" placeholder="服用中のお薬があれば記入してください"><c:out value="${reservation.medications}"/></textarea>
                </p>
                <p> 
                    <label for="emergencyContact">緊急連絡先</label> 
                    <input type="tel" id="emergencyContact" name="emergencyContact" value="<c:out value="${reservation.emergencyContact}"/>" placeholder="080-9876-5432"> 
                </p>
                <p> 
                    <label for="notes">その他・備考</label> 
                    <textarea id="notes" name="notes" rows="2" placeholder="その他ご要望やご質問があれば記入してください"><c:out value="${reservation.notes}"/></textarea>
                </p>
            </div>
            
            <div class="button-group"> 
                <input type="submit" value="診療予約情報を更新"> 
                <a href="reservation?action=list" class="button secondary">予約一覧に戻る</a> 
            </div> 
        </form> 
    </div> 
</body> 
</html>