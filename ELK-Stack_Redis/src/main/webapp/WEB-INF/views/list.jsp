<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Log List</title>
    <link href="/webjars/bootstrap/5.3.2/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2>Log List</h2>
    <table class="table">
        <thead>
        <tr>
            <th>Timestamp</th>
            <th>Level</th>
            <th>Logger</th>
            <th>Message</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${logs.content}" var="log">
            <tr>
                <td>${log.timestamp}</td>
                <td>${log.level}</td>
                <td>${log.logger}</td>
                <td>${log.message}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <div>
        <c:if test="${logs.totalPages > 0}">
            <nav>
                <ul class="pagination">
                    <c:forEach begin="0" end="${logs.totalPages-1}" var="page">
                        <li class="page-item">
                            <a class="page-link" href="?page=${page}">${page + 1}</a>
                        </li>
                    </c:forEach>
                </ul>
            </nav>
        </c:if>
    </div>
    <a href="/" class="btn btn-secondary">Back</a>
</div>
</body>
</html>