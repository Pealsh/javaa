<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>Test Page</title>
</head>
<body>
    <h1>テストページ</h1>
    <p>Eclipse Tomcat 接続テスト</p>
    <p>現在時刻: <%= new java.util.Date() %></p>
    <p>コンテキストパス: ${pageContext.request.contextPath}</p>
</body>
</html>