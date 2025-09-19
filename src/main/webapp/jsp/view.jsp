<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!DOCTYPE html> 
<html lang="ja"> 
<head> 
    <meta charset="UTF-8"> 
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>診療予約詳細 | MediReserve</title> 
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css"> 
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <meta name="description" content="診療予約詳細情報を表示する医療クリニック用システム。">
    <script src="${pageContext.request.contextPath}/js/modern-ui.js" defer></script>
</head> 
<body> 
    <div class="container"> 
        <h1>診療予約詳細情報</h1> 
        
        <div class="form-section">
            <h3>患者様情報</h3>
            <div class="detail-item">
                <label>患者ID:</label>
                <span class="patient-id">${reservation.id}</span>
            </div>
            <div class="detail-item">
                <label>お名前:</label>
                <span><c:out value="${reservation.name}"/></span>
            </div>
            <div class="detail-item">
                <label>電話番号:</label>
                <span><c:out value="${reservation.phoneNumber}"/></span>
            </div>
            <div class="detail-item">
                <label>保険証番号:</label>
                <span><c:out value="${reservation.insuranceNumber}"/></span>
            </div>
        </div>
        
        <div class="form-section">
            <h3>診療情報</h3>
            <div class="detail-item">
                <label>診療科:</label>
                <span class="department-badge">
                    <c:choose>
                        <c:when test="${not empty reservation.department}">
                            ${reservation.department}
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted">一般診療</span>
                        </c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="detail-item">
                <label>担当医師:</label>
                <span>
                    <c:choose>
                        <c:when test="${not empty reservation.doctor}">
                            ${reservation.doctor}
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted">指定なし</span>
                        </c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="detail-item">
                <label>希望診療日時:</label>
                <span class="datetime">${reservation.reservationTime}</span>
            </div>
        </div>
        
        <div class="form-section">
            <h3>症状・医療情報</h3>
            <div class="detail-item">
                <label>症状・主訴:</label>
                <div class="detail-text">
                    <c:choose>
                        <c:when test="${not empty reservation.symptoms}">
                            <c:out value="${reservation.symptoms}"/>
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted">症状未記載</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="detail-item">
                <label>アレルギー:</label>
                <div class="detail-text">
                    <c:choose>
                        <c:when test="${not empty reservation.allergies}">
                            <c:out value="${reservation.allergies}"/>
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted">なし</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="detail-item">
                <label>現在服用中の薬:</label>
                <div class="detail-text">
                    <c:choose>
                        <c:when test="${not empty reservation.medications}">
                            <c:out value="${reservation.medications}"/>
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted">なし</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="detail-item">
                <label>緊急連絡先:</label>
                <span>
                    <c:choose>
                        <c:when test="${not empty reservation.emergencyContact}">
                            <c:out value="${reservation.emergencyContact}"/>
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted">未登録</span>
                        </c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="detail-item">
                <label>その他・備考:</label>
                <div class="detail-text">
                    <c:choose>
                        <c:when test="${not empty reservation.notes}">
                            <c:out value="${reservation.notes}"/>
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted">なし</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        
        <div class="form-section">
            <h3>予約状態</h3>
            <div class="detail-item">
                <label>現在の状態:</label>
                <span>
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
                        <c:otherwise>
                            <span class="status-badge">${reservation.status}</span>
                        </c:otherwise>
                    </c:choose>
                </span>
            </div>
        </div>
        
        <div class="button-group">
            <a href="reservation?action=edit&id=${reservation.id}" class="button">編集する</a>
            <a href="reservation?action=list" class="button secondary">予約一覧に戻る</a>
        </div>
    </div>

    <footer style="margin-top: 2rem; padding: 1rem 0; border-top: 1px solid #e0e0e0; text-align: center; color: #666; font-size: 0.9rem;">
        <p>&copy; 2025 MediReserve. All rights reserved.</p>
    </footer>
</body>
</html> 