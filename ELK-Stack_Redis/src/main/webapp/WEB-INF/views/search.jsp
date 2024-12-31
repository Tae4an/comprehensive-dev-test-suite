<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Log Search</title>
    <link href="/webjars/bootstrap/5.3.2/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2>Log Search</h2>
    <form action="/logs" method="get" class="mt-4">
        <div class="row mb-3">
            <div class="col">
                <label>Start Time:</label>
                <input type="datetime-local" name="startTime" class="form-control">
            </div>
            <div class="col">
                <label>End Time:</label>
                <input type="datetime-local" name="endTime" class="form-control">
            </div>
        </div>
        <div class="mb-3">
            <label>Log Levels:</label>
            <div class="form-check">
                <input type="checkbox" name="levels" value="ERROR" class="form-check-input">
                <label class="form-check-label">ERROR</label>
            </div>
            <div class="form-check">
                <input type="checkbox" name="levels" value="WARN" class="form-check-input">
                <label class="form-check-label">WARN</label>
            </div>
            <div class="form-check">
                <input type="checkbox" name="levels" value="INFO" class="form-check-input">
                <label class="form-check-label">INFO</label>
            </div>
        </div>
        <div class="mb-3">
            <label>Keyword:</label>
            <input type="text" name="keyword" class="form-control">
        </div>
        <button type="submit" class="btn btn-primary">Search</button>
    </form>
</div>
</body>
</html>