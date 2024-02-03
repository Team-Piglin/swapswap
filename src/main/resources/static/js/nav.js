document.addEventListener('DOMContentLoaded', function () {
  var logo = document.querySelector('.header-logo a');
  var searchInput = document.querySelector('.swapswap-search-input');
  var navCityInput = document.getElementById('navCityInput');
  var categoryLinks = document.querySelectorAll('.category-bar a');

  // URL에서 검색어와 카테고리 추출
  var urlParams = new URLSearchParams(window.location.search);
  var currentSearchValue = urlParams.get('title');
  var currentCategory = urlParams.get('category');
  var selectedCity = localStorage.getItem('selectedCity');

  if (selectedCity) {
    navCityInput.value = selectedCity;
  }

  // 검색란에 파라미터 title 추가
  if (currentSearchValue) {
    searchInput.value = currentSearchValue;
  }

  if (logo) {
    logo.addEventListener('click', function () {
      localStorage.removeItem('searchValue');
      localStorage.removeItem('selectedCategory');
      localStorage.removeItem('selectedCity');
    });
  }

  categoryLinks.forEach(function (link) {

    if (link.getAttribute('href').slice(1) === currentCategory) {
      link.classList.add('active');
    }

    link.addEventListener('click', function (event) {
      event.preventDefault();
      localStorage.setItem('selectedCategory',
          this.getAttribute('href').slice(1));
      updateSearch();
    });
  });

  // 검색 입력 필드와 버튼
  var searchInput = document.querySelector('.swapswap-search-input');
  var searchButton = document.querySelector('.swapswap-search-button');

  if (searchInput && searchButton) {
    // 제목란에 입력할 때 카테고리 초기화
    searchInput.addEventListener('input', function () {
      localStorage.removeItem('selectedCategory');
    });

    // 검색 버튼 클릭 이벤트
    searchButton.addEventListener('click', function () {
      localStorage.setItem('searchValue', searchInput.value);
      updateSearch();
    });

    // 검색 입력 필드의 엔터 키 이벤트
    searchInput.addEventListener('keyup', function (event) {
      if (event.keyCode === 13) {
        localStorage.setItem('searchValue', searchInput.value);
        updateSearch();
      }
    });
  }

  // 검색 실행 함수
  function updateSearch() {
    var searchValue = localStorage.getItem('searchValue') || '';
    var selectedCategory = localStorage.getItem('selectedCategory') || '';
    var selectedCity = navCityInput.value;

    var searchQuery = '/search/posts?';
    if (searchValue) {
      searchQuery += 'title=' + encodeURIComponent(searchValue);
    }
    if (selectedCategory) {
      searchQuery += (searchValue ? '&' : '') + 'category='
          + encodeURIComponent(
              selectedCategory);
    }
    if (selectedCity) {
      searchQuery += (searchValue || selectedCategory ? '&' : '') + 'city='
          + encodeURIComponent(
              selectedCity);
    }
    window.location.href = searchQuery;
  }

  navCityInput.addEventListener('change', function () {
    localStorage.setItem('selectedCity', this.value);
    updateSearch()
  });

  const rainbowColors = ['red', 'orange', 'green', 'blue', 'indigo', 'violet'];
  let currentColorIndex = 0;

  function updateRainbowColor() {
    const element = document.querySelector('.header-nav .coupon-event-link');
    if (element) {
      element.style.color = rainbowColors[currentColorIndex];
      currentColorIndex = (currentColorIndex + 1) % rainbowColors.length;
    }
  }

  setInterval(updateRainbowColor, 1000); // 매 1초마다 색상을 업데이트합니다.

});

function csCenter() {

  Swal.fire({
    title: '서비스 준비중입니다',
    confirmButtonColor: '#00AADC'
  })
}

// ----modal 및 notification---
document.addEventListener('DOMContentLoaded', function () {
  const modal = document.getElementById('notificationModal');
  const link = document.getElementById('notificationLink');
  const closeBtn = modal.querySelector('.close');
  const deleteAllBtn = modal.querySelector('#deleteAllNotificationsBtn');

  closeBtn.addEventListener('click', function () {
    modal.style.display = 'none';
  });

  window.addEventListener('click', function (event) {
    if (event.target == modal) {
      modal.style.display = 'none';
    }
  });
  if (getToken()) {

    link.addEventListener('click', function (event) {
      event.preventDefault();
      modal.style.display = 'block';
      fetchNotifications();
      displayNotifications();
    });

    subscribeToNotifications();
    getCountOfUnreadNotifications();
  }
  deleteAllBtn.addEventListener('click', function () {
    deleteAllNotifications();
  });
});

function getToken() {
  const auth = Cookies.get('Authorization');
  return auth ? auth : '';
}

function subscribeToNotifications() {
  const eventSource = new EventSource("/subscribe");

  eventSource.onmessage = function (event) {
    const notificationData = JSON.parse(event.data);

    checkNotificationPermission().then(granted => {
      if (granted) {
        console.log('알림 권한이 부여되었습니다');
        showNotification(notificationData);
      } else {
        console.log('알림 권한이 거부되었습니다');
      }
    });
    fetchNotifications();
  };

  eventSource.onerror = function (event) {
    console.error("EventSource error:", event);
    eventSource.close();
  };
}

function checkNotificationPermission() {
  return new Promise((resolve) => {
    if (Notification.permission === 'granted') {
      resolve(true);
    } else {
      Notification.requestPermission().then(permission => {
        resolve(permission === 'granted');
      });
    }
  });
}

function showNotification(data) {
  const notification = new Notification('', {
    body: data.content
  });

  setTimeout(() => {
    notification.close();
  }, 10 * 1000);

  notification.onclick = () => {
    window.open(data.url);
  };
}

function fetchNotifications() {
  $.ajax({
    type: 'GET',
    url: '/notifications',
    success: function (response) {
      console.log('알림:', response);
      displayNotifications(response);
      getCountOfUnreadNotifications();
    },
    error: function (xhr, status, error) {
      console.error('알림 불러오기 실패:', error);
    }
  });
}

function displayNotifications(notifications) {
  var notificationList = document.getElementById('notification-list');
  notificationList.innerHTML = '';

  if (notifications.length === 0) {
    var noNotificationMessage = document.createElement('li');
    noNotificationMessage.textContent = '새로운 알림이 없습니다!';
    noNotificationMessage.classList.add('no-notification');
    notificationList.appendChild(noNotificationMessage);
    return;
  }

  notifications.forEach(function (notification) {
    var listItem = document.createElement('li');

    var notificationText = document.createElement('span');
    notificationText.textContent = notification.content;

    if (notification.status) {
      notificationText.classList.add('read-notification-text');
    } else {
      notificationText.classList.add('unread-notification-text');
    }
    listItem.appendChild(notificationText);

    var deleteButton = document.createElement('button');
    deleteButton.textContent = '삭제';
    deleteButton.classList.add('delete-notification-btn');
    deleteButton.addEventListener('click', function (event) {
      event.stopPropagation();
      deleteNotification(notification.id);
      listItem.remove();
    });
    listItem.appendChild(deleteButton);

    listItem.addEventListener('click', function () {
      markNotificationAsRead(notification.id);
      window.open(notification.url);
    });

    var deleteAllNotificationsBtn = document.getElementById(
        'deleteAllNotificationsBtn');

    deleteAllNotificationsBtn.addEventListener('mouseover', function () {
      this.src = '/images/trash-open.jpg';
    });

    deleteAllNotificationsBtn.addEventListener('mouseout', function () {
      this.src = '/images/trash-closed.jpg';
    });

    notificationList.appendChild(listItem);
  });
}

function markNotificationAsRead(notificationId) {
  $.ajax({
    url: '/notification/' + notificationId,
    type: 'POST',
    success: function (response) {
      console.log(response);
      fetchNotifications();
    },
    error: function (xhr, status, error) {
      console.error('알림 읽기 실패:', error);
    }
  });
}

function deleteNotification(notificationId) {
  $.ajax({
    url: '/notifications/' + notificationId,
    type: 'DELETE',
    success: function (response) {
      console.log('알림 삭제 성공:', response);
      fetchNotifications();
    },
    error: function (xhr, status, error) {
      console.error('알림 삭제 실패:', error);
    }
  });
}

function deleteAllNotifications() {
  $.ajax({
    url: '/notifications',
    type: 'DELETE',
    success: function (response) {
      console.log('All notifications deleted:', response);
      fetchNotifications();
    },
    error: function (xhr, status, error) {
      console.error('Failed to delete all notifications:', error);
    }
  });
}

function getCountOfUnreadNotifications() {
  $.ajax({
    type: 'GET',
    url: '/notifications/count',
    success: function (response) {
      console.log('읽지 않은 알림 불러오기 성공:', response);
      updateUnreadNotificationCount(response.count);

    },
    error: function (xhr, status, error) {
      console.error('읽지 않은 알림 불러오기 실패:', error);
    }
  });
}

function updateUnreadNotificationCount(count) {
  var notificationIcon = document.getElementById('notificationLink');
  var countSpan = notificationIcon.querySelector('.unread-notification-count');

  if (!countSpan) {
    countSpan = document.createElement('span');
    countSpan.classList.add('unread-notification-count');
    notificationIcon.appendChild(countSpan);
  }

  if (count === 0) {
    countSpan.style.display = 'none';
  } else {
    countSpan.style.display = 'inline-block';
    countSpan.textContent = count;
  }
}
