<jsp:include page="fragments/header.jsp" />
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="container-fluid">
    <div class="row">
        <!-- 좌측 네비게이션 -->
        <nav class="col-md-3 col-lg-2 d-md-block bg-light sidebar">
            <div class="position-sticky pt-3">
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link active" href="#string-section">
                            문자열 관리
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#list-section">
                            리스트 관리
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#image-section">
                            이미지 캐시 관리
                        </a>
                    </li>
                </ul>
            </div>
        </nav>

        <!-- 메인 컨텐츠 영역 -->
        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
            <!-- 문자열 섹션 -->
            <div id="string-section" class="section">
                <jsp:include page="sections/string.jsp" />
            </div>

            <!-- 리스트 섹션 -->
            <div id="list-section" class="section">
                <jsp:include page="sections/list.jsp" />
            </div>

            <!-- 이미지 섹션 -->
            <div id="image-section" class="section">
                <jsp:include page="sections/image.jsp" />
            </div>
        </main>
    </div>
</div>

<jsp:include page="fragments/footer.jsp" />