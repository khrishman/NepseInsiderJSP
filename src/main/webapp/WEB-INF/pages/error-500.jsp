<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Server Error | NepseInsider</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container" style="text-align:center; margin-top:80px;">
    <div class="card">
        <h1 style="font-size:72px; color:#e74c3c; margin-bottom:8px;">500</h1>
        <h2>Something Went Wrong</h2>
        <p style="margin-bottom:20px;">An unexpected error occurred. Please try again later.</p>
        <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">Go to Home</a>
    </div>
</div>
</body>
</html>
