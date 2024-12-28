<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="card mt-4">
    <div class="card-header">
        <h5 class="mb-0">리스트 데이터 관리</h5>
    </div>
    <div class="card-body">
        <form id="listForm">
            <div class="mb-3">
                <label for="listKey" class="form-label">리스트 키</label>
                <input type="text" class="form-control" id="listKey" required>
            </div>
            <div class="mb-3">
                <label for="listValue" class="form-label">추가할 값</label>
                <input type="text" class="form-control" id="listValue" required>
            </div>
            <button type="submit" class="btn btn-primary">리스트에 추가</button>
            <button type="button" class="btn btn-secondary" onclick="getList()">리스트 조회</button>
        </form>

        <div class="mt-3">
            <h6>리스트 내용</h6>
            <ul id="listResult" class="list-group">
            </ul>
        </div>
    </div>
</div>