<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="card mt-4">
    <div class="card-header">
        <h5 class="mb-0">문자열 데이터 관리</h5>
    </div>
    <div class="card-body">
        <form id="stringForm">
            <div class="mb-3">
                <label for="stringKey" class="form-label">키</label>
                <input type="text" class="form-control" id="stringKey" required>
            </div>
            <div class="mb-3">
                <label for="stringValue" class="form-label">값</label>
                <input type="text" class="form-control" id="stringValue" required>
            </div>
            <button type="submit" class="btn btn-primary">저장</button>
            <button type="button" class="btn btn-secondary" onclick="getString()">조회</button>
        </form>

        <div class="mt-3">
            <h6>조회 결과</h6>
            <pre id="stringResult" class="bg-light p-2 rounded"></pre>
        </div>
    </div>
</div>