// 搜索页面的功能
const search = {
    // 初始化页面
    init: function() {
        this.setupSearchForm();
    },

    // 设置搜索表单提交事件
    setupSearchForm: function() {
        const searchForm = document.getElementById('searchForm');
        if (searchForm) {
            searchForm.addEventListener('submit', (event) => {
                event.preventDefault();
                this.performSearch();
            });
        }
    },

    // 执行搜索
    performSearch: function() {
        const keywordInput = document.getElementById('keyword');
        const keyword = keywordInput.value.trim();

        if (!keyword) {
            alert('请输入搜索关键词');
            return;
        }

        // 显示加载状态
        this.showLoading();
        
        // 使用API模块执行搜索请求
        api.searchPlaylists(keyword)
            .then(playlists => {
                this.renderSearchResults(playlists);
                this.hideLoading();
            })
            .catch(error => {
                console.error('搜索失败:', error);
                this.showError(error.message || '搜索失败，请稍后重试');
                this.hideLoading();
            });
    },

    // 渲染搜索结果
    renderSearchResults: function(playlists) {
        const resultsContainer = document.getElementById('searchResults');
        const noResultsElement = document.getElementById('noResults');

        // 清空现有结果
        resultsContainer.innerHTML = '';

        if (playlists && playlists.length > 0) {
            // 有搜索结果，隐藏无结果提示
            noResultsElement.classList.add('d-none');

            // 渲染歌单列表
            playlists.forEach(playlist => {
                const card = document.createElement('div');
                card.className = 'col-md-4 mb-4';
                card.innerHTML = `
                    <div class="card h-100">
                        <div class="card-body">
                            <h5 class="card-title">${playlist.name}</h5>
                            <p class="card-text">${playlist.description ? playlist.description.substring(0, 100) + '...' : '暂无描述'}</p>
                            <p class="card-text">
                                <small class="text-muted">歌曲数量: ${playlist.trackCount || 0}</small>
                            </p>
                        </div>
                        <div class="card-footer">
                            <button class="btn btn-sm btn-primary import-btn" data-id="${playlist.id}">导入歌单</button>
                        </div>
                    </div>
                `;
                resultsContainer.appendChild(card);
            });

            // 添加导入按钮事件监听
            this.setupImportButtons();
        } else {
            // 没有搜索结果，显示无结果提示
            noResultsElement.classList.remove('d-none');
        }
    },

    // 设置导入按钮事件监听
    setupImportButtons: function() {
        const importButtons = document.querySelectorAll('.import-btn');
        importButtons.forEach(button => {
            button.addEventListener('click', function() {
                const playlistId = this.getAttribute('data-id');
                search.importPlaylist(playlistId);
            });
        });
    },

    // 导入歌单
    importPlaylist: function(playlistId) {
        axios.post(`/api/playlists/import/${playlistId}`)
            .then(response => {
                alert('歌单导入成功！');
                // 跳转到仪表盘
                window.location.href = '/dashboard';
            })
            .catch(error => {
                console.error('导入歌单失败:', error);
                alert('导入歌单失败，请稍后重试');
            });
    },

    // 显示加载状态
    showLoading: function() {
        const loadingElement = document.getElementById('loading');
        if (loadingElement) {
            loadingElement.classList.remove('d-none');
        }
    },

    // 隐藏加载状态
    hideLoading: function() {
        const loadingElement = document.getElementById('loading');
        if (loadingElement) {
            loadingElement.classList.add('d-none');
        }
    },

    // 显示错误信息
    showError: function(message) {
        const errorElement = document.getElementById('errorMessage');
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.classList.remove('d-none');
            
            // 3秒后隐藏错误信息
            setTimeout(() => {
                errorElement.classList.add('d-none');
            }, 3000);
        }
    }
};

// 页面加载完成后初始化
window.addEventListener('load', () => {
    if (auth.isAuthenticated()) {
        search.init();
    }
});