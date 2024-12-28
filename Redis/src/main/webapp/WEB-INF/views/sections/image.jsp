<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="card mt-4">
    <div class="card-header">
        <h5 class="mb-0">이미지 캐시 관리</h5>
    </div>
    <div class="card-body">
        <form id="imageForm" enctype="multipart/form-data">
            <div class="mb-3">
                <label for="imageKey" class="form-label">이미지 키</label>
                <input type="text" class="form-control" id="imageKey" required>
            </div>
            <div class="mb-3">
                <label for="imageFile" class="form-label">이미지 파일</label>
                <input type="file" class="form-control" id="imageFile" accept="image/*" required>
            </div>
            <div class="mb-3">
                <label for="expirationTime" class="form-label">만료 시간 (초)</label>
                <input type="number" class="form-control" id="expirationTime" min="0">
            </div>
            <div class="mb-3">
                <img id="imagePreview" class="img-fluid d-none" alt="이미지 미리보기">
            </div>
            <button type="submit" class="btn btn-primary">이미지 캐시</button>
            <button type="button" class="btn btn-secondary" onclick="getCachedImage()">이미지 조회</button>
            <button type="button" class="btn btn-danger" onclick="deleteImage()">이미지 삭제</button>
        </form>

        <div class="mt-3">
            <h6>캐시된 이미지</h6>
            <img id="cachedImage" class="img-fluid d-none" alt="캐시된 이미지">
        </div>
    </div>
</div>