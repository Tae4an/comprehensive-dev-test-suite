<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Log Dashboard</title>
    <link href="/webjars/bootstrap/5.3.2/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
<div class="container mt-5">
    <h2>Log Dashboard</h2>
    <div class="row">
        <div class="col-md-6">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Log Levels Distribution</h5>
                    <canvas id="logLevelChart"></canvas>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const ctx = document.getElementById('logLevelChart').getContext('2d');
        const data = {
            labels: [<c:forEach items="${logCounts}" var="entry">'${entry.key}', </c:forEach>],
            datasets: [{
                data: [<c:forEach items="${logCounts}" var="entry">${entry.value}, </c:forEach>],
                backgroundColor: [
                    '#dc3545', // ERROR
                    '#ffc107', // WARN
                    '#0dcaf0', // INFO
                    '#198754', // DEBUG
                    '#6c757d'  // TRACE
                ]
            }]
        };

        new Chart(ctx, {
            type: 'pie',
            data: data
        });
    });
</script>
</body>
</html>