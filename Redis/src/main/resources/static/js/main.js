// 문자열 저장
$('#stringForm').submit(function(e) {
    e.preventDefault();

    $.ajax({
        url: '/api/redis/string',
        type: 'POST',
        data: {
            key: $('#stringKey').val(),
            value: $('#stringValue').val()
        },
        success: function(response) {
            alert('저장되었습니다.');
        },
        error: function(xhr) {
            alert('저장 실패: ' + xhr.responseText);
        }
    });
});

// 문자열 조회
function getString() {
    const key = $('#stringKey').val();
    if (!key) {
        alert('키를 입력하세요.');
        return;
    }

    $.ajax({
        url: '/api/redis/string',
        type: 'GET',
        data: { key: key },
        success: function(response) {
            $('#stringResult').text(JSON.stringify(response, null, 2));
        },
        error: function(xhr) {
            alert('조회 실패: ' + xhr.responseText);
        }
    });
}

// 리스트 관리
$('#listForm').submit(function(e) {
    e.preventDefault();

    $.ajax({
        url: '/api/redis/list',
        type: 'POST',
        data: {
            key: $('#listKey').val(),
            value: $('#listValue').val()
        },
        success: function(response) {
            alert('리스트에 추가되었습니다.');
            getList();
        },
        error: function(xhr) {
            alert('추가 실패: ' + xhr.responseText);
        }
    });
});

function getList() {
    const key = $('#listKey').val();
    if (!key) {
        alert('키를 입력하세요.');
        return;
    }

    $.ajax({
        url: '/api/redis/list',
        type: 'GET',
        data: { key: key },
        success: function(response) {
            const $listResult = $('#listResult');
            $listResult.empty();
            response.forEach(item => {
                $listResult.append(`<li class="list-group-item">${item}</li>`);
            });
        },
        error: function(xhr) {
            alert('조회 실패: ' + xhr.responseText);
        }
    });
}

// 이미지 관리
$('#imageForm').submit(function(e) {
    e.preventDefault();

    const formData = new FormData();
    formData.append('key', $('#imageKey').val());
    formData.append('file', $('#imageFile')[0].files[0]);

    const expiration = $('#expirationTime').val();
    if (expiration) {
        formData.append('timeout', expiration);
    }

    $.ajax({
        url: expiration ? '/api/redis/image/expire' : '/api/redis/image',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            alert('이미지가 캐시되었습니다.');
            getCachedImage();
        },
        error: function(xhr) {
            alert('캐시 실패: ' + xhr.responseText);
        }
    });
});

function getCachedImage() {
    const key = $('#imageKey').val();
    if (!key) {
        alert('키를 입력하세요.');
        return;
    }

    $('#cachedImage')
        .attr('src', `/api/redis/image?key=${key}`)
        .removeClass('d-none');
}

function deleteImage() {
    const key = $('#imageKey').val();
    if (!key) {
        alert('키를 입력하세요.');
        return;
    }

    $.ajax({
        url: '/api/redis/image',
        type: 'DELETE',
        data: { key: key },
        success: function(response) {
            alert('이미지가 삭제되었습니다.');
            $('#cachedImage').addClass('d-none');
        },
        error: function(xhr) {
            alert('삭제 실패: ' + xhr.responseText);
        }
    });
}

// 이미지 미리보기
$('#imageFile').change(function() {
    const file = this.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            $('#imagePreview')
                .attr('src', e.target.result)
                .removeClass('d-none');
        }
        reader.readAsDataURL(file);
    }
});