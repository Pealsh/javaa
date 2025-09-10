<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!DOCTYPE html> 
<html lang="ja"> 
<head> 
    <meta charset="UTF-8"> 
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>診療予約管理 | MediReserve</title> 
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css"> 
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <meta name="description" content="診療予約の管理・検索・確認ができる医療クリニック専用システム。">
    <script src="${pageContext.request.contextPath}/js/modern-ui.js" defer></script>
</head> 
<body> 
    <div class="container"> 
        <h1>診療予約管理</h1> 
 
        <form action="reservation" method="post" class="search-sort-form"> 
            <input type="hidden" name="action" value="list"> 
            <div> 
                <label for="search">患者検索:</label> 
                <input type="text" id="search" name="search" value="<c:out value="${searchTerm}"/>" placeholder="患者名・診療科・医師名で検索"> 
            </div> 
            <div> 
                <label for="sortBy">ソート基準:</label> 
                <select id="sortBy" name="sortBy"> 
                    <option value="" <c:if test="${sortBy == null || sortBy == ''}">selected</c:if>>並び順を選択
</option> 
                    <option value="name" <c:if test="${sortBy == 'name'}">selected</c:if>>患者名</option> 
                    <option value="time" <c:if test="${sortBy == 'time'}">selected</c:if>>診療日時</option> 
                    <option value="department" <c:if test="${sortBy == 'department'}">selected</c:if>>診療科</option> 
                    <option value="doctor" <c:if test="${sortBy == 'doctor'}">selected</c:if>>担当医</option> 
                </select> 
            </div> 
            <div> 
                <label for="sortOrder">ソート順:</label> 
                <select id="sortOrder" name="sortOrder"> 
                    <option value="asc" <c:if test="${sortOrder == 'asc'}">selected</c:if>>昇順</option> 
                    <option value="desc" <c:if test="${sortOrder == 'desc'}">selected</c:if>>降順</option> 
                </select> 
            </div> 
            <button type="submit" class="search-button">検索・ソート実行</button> 
        </form>
        <p class="error-message"><c:out value="${errorMessage}"/></p> 
        <p class="success-message"><c:out value="${successMessage}"/></p> 
 
        <div class="list-button-group"> 
            <a href="reservation?action=export_csv" class="list-button list-button-success no-transition" target="_blank">診療データエクスポート</a> 
            <form action="reservation" method="get"> 
                <input type="hidden" name="action" value="clean_up"> 
                <input type="submit" value="クリーンアップ" class="list-button list-button-secondary" onclick="return confirm('本当に全ての診療予約を削除しますか？')"> 
            </form> 
        </div> 
 
        <table> 
            <thead> 
                <tr> 
                    <th>患者ID</th> 
                    <th>患者名</th> 
                    <th>電話番号</th> 
                    <th>診療科</th> 
                    <th>担当医</th> 
                    <th>診療日時</th> 
                    <th>症状</th> 
                    <th>状態</th>
                    <th>操作</th> 
                </tr> 
            </thead> 
            <tbody> 
                <c:forEach var="reservation" items="${reservations}"> 
                    <tr> 
                        <td><span class="patient-id">${reservation.id}</span></td> 
                        <td><strong>${reservation.name}</strong></td> 
                        <td>
                            <c:choose>
                                <c:when test="${not empty reservation.phoneNumber}">
                                    ${reservation.phoneNumber}
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">未登録</span>
                                </c:otherwise>
                            </c:choose>
                        </td> 
                        <td>
                            <c:choose>
                                <c:when test="${not empty reservation.department}">
                                    <span class="department-badge">${reservation.department}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">一般診療</span>
                                </c:otherwise>
                            </c:choose>
                        </td> 
                        <td>
                            <c:choose>
                                <c:when test="${not empty reservation.doctor}">
                                    ${reservation.doctor}
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">指定なし</span>
                                </c:otherwise>
                            </c:choose>
                        </td> 
                        <td><time class="datetime">${reservation.reservationTime}</time></td> 
                        <td>
                            <c:choose>
                                <c:when test="${not empty reservation.symptoms}">
                                    <div class="symptoms-preview" title="${reservation.symptoms}">
                                        <c:choose>
                                            <c:when test="${reservation.symptoms.length() > 20}">
                                                ${reservation.symptoms.substring(0, 20)}...
                                            </c:when>
                                            <c:otherwise>
                                                ${reservation.symptoms}
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">症状未記載</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${reservation.status == 'PENDING'}">
                                    <span class="status-badge status-pending">予約申込中</span>
                                </c:when>
                                <c:when test="${reservation.status == 'CONFIRMED'}">
                                    <span class="status-badge status-confirmed">予約確定</span>
                                </c:when>
                                <c:when test="${reservation.status == 'CANCELLED'}">
                                    <span class="status-badge status-cancelled">キャンセル</span>
                                </c:when>
                                <c:when test="${reservation.status == 'COMPLETED'}">
                                    <span class="status-badge status-completed">診療完了</span>
                                </c:when>
                                <c:when test="${reservation.status == 'NO_SHOW'}">
                                    <span class="status-badge status-no-show">未来院</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge status-pending">予約申込中</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="table-actions"> 
                            <a href="reservation?action=edit&id=${reservation.id}" class="table-button" title="診療予約情報を編集">編集</a>
                            
                            <c:if test="${reservation.status == 'PENDING'}">
                                <a href="reservation?action=confirm&id=${reservation.id}" class="table-button table-button-success" 
                                   onclick="return confirm('予約を確定しますか？\n患者: ${reservation.name}');" title="予約を確定する">予約確定</a>
                            </c:if>
                            
                            <c:if test="${reservation.status == 'CONFIRMED'}">
                                <a href="reservation?action=complete&id=${reservation.id}" class="table-button table-button-primary" 
                                   onclick="return confirm('診療を完了しますか？\n患者: ${reservation.name}');" title="診療完了にする">診療完了</a>
                            </c:if>
                            
                            <c:if test="${reservation.status == 'PENDING' || reservation.status == 'CONFIRMED'}">
                                <a href="reservation?action=cancel&id=${reservation.id}" class="table-button table-button-danger" 
                                   onclick="return confirm('本当に予約をキャンセルしますか？\n患者: ${reservation.name}\n日時: ${reservation.reservationTime}');" title="予約をキャンセルする">キャンセル</a>
                            </c:if>
                            
                            <c:if test="${reservation.status == 'CANCELLED' || reservation.status == 'COMPLETED'}">
                                <form action="reservation" method="post" style="display:inline;"> 
                                    <input type="hidden" name="action" value="delete"> 
                                    <input type="hidden" name="id" value="${reservation.id}"> 
                                    <input type="submit" value="削除" class="table-button table-button-secondary" onclick="return 
confirm('本当に予約データを削除しますか？\n患者: ${reservation.name}\n日時: ${reservation.reservationTime}');" title="予約データを削除する"> 
                                </form>
                            </c:if>
                        </td> 
                    </tr> 
                </c:forEach> 
                <c:if test="${empty reservations}"> 
                    <tr><td colspan="9" class="no-data">現在、診療予約がありません。</td></tr> 
                </c:if> 
            </tbody> 
        </table> 
 
        <div class="pagination"> 
            <c:if test="${currentPage != 1}"> 
                <a href="reservation?action=list&page=${currentPage - 
1}&search=${searchTerm}&sortBy=${sortBy}&sortOrder=${sortOrder}">前へ</a> 
            </c:if> 
            <c:forEach begin="1" end="${noOfPages}" var="i"> 
                <c:choose> 
                    <c:when test="${currentPage eq i}"> 
                        <span class="current">${i}</span> 
                    </c:when> 
                    <c:otherwise> 
                        <a 
href="reservation?action=list&page=${i}&search=${searchTerm}&sortBy=${sortBy}&sortOrder=${sortOrder}">${i}</a> 
                    </c:otherwise> 
                </c:choose> 
            </c:forEach> 
            <c:if test="${currentPage lt noOfPages}"> 
                <a href="reservation?action=list&page=${currentPage + 
1}&search=${searchTerm}&sortBy=${sortBy}&sortOrder=${sortOrder}">次へ</a> 
            </c:if> 
        </div> 
 
        <div class="list-button-group"> 
            <a href="index.jsp" class="list-button list-button-secondary">診療予約申込み画面に戻る</a> 
        </div> 
    </div> 

    <script>
        // 成功メッセージを10秒後に自動で消す
        document.addEventListener('DOMContentLoaded', function() {
            const successMessage = document.querySelector('.success-message');
            if (successMessage && successMessage.textContent.trim() !== '') {
                setTimeout(function() {
                    successMessage.classList.add('fade-out');
                    setTimeout(function() {
                        successMessage.style.display = 'none';
                    }, 500); // フェードアウトアニメーション完了後に非表示
                }, 10000); // 10秒後
            }
        });
    </script>
</body> 
</html> 