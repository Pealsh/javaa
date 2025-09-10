<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理者ログイン | MediReserve</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <script src="${pageContext.request.contextPath}/js/modern-ui.js" defer></script>
</head>
<body>
    <div class="container">
        <h1>管理者ログイン</h1>
        
        <form action="login" method="post" style="max-width: 500px; margin: 0 auto; padding: var(--space-6); background-color: var(--gray-50); border-radius: var(--radius-lg); border: 1px solid var(--gray-200);">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
            
            <div class="form-section">
                <h3 style="text-align: center;">認証</h3>
                <p style="text-align: center;">
                    <label for="password" style="display: block; text-align: center; margin-bottom: var(--space-3);">パスワード *</label>
                    <input type="password" id="password" name="password" required placeholder="パスワードを入力してください" style="width: 100%; max-width: 400px; margin: 0 auto; display: block;">
                </p>
            </div>
            
            <div class="button-group">
                <input type="submit" value="ログイン">
                <a href="${pageContext.request.contextPath}/" class="button secondary">戻る</a>
            </div>
        </form>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const form = document.querySelector('form');
            const passwordField = document.getElementById('password');
            
            // フォーカスをパスワードフィールドに設定
            passwordField.focus();
            
            // エンターキーでの送信をサポート
            passwordField.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    form.submit();
                }
            });
            
            // フォーム送信時の処理
            form.addEventListener('submit', function(e) {
                const submitBtn = this.querySelector('input[type="submit"]');
                
                if (submitBtn) {
                    submitBtn.disabled = true;
                    submitBtn.value = '認証中...';
                }
            });
        });
    </script>
</body>
</html>