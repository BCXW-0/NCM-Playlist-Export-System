// 仪表盘页面的功能
const dashboard = {
    // 初始化页面
    init: function() {
        // 加载用户歌单列表
        this.loadPlaylists();
        // 设置导入歌单按钮事件
        this.setupImportPlaylistForm();
    },

    // 加载用户歌单列表
    loadPlaylists: function() {
        // 显示加载状态
        this.showLoading();

        api.getUserPlaylists()
            .then(playlists => {
                this.renderPlaylists(playlists);
                this.hideLoading();
            })
            .catch(error => {
                console.error('加载歌单失败:', error);
                this.showError(error.message);
                this.hideLoading();
            });
    },

    // 渲染歌单列表
    renderPlaylists: function(playlists) {
        const playlistListElement = document.getElementById('playlistList');
        const emptyPlaylistsElement = document.getElementById('emptyPlaylists');

        // 清空现有列表
        playlistListElement.innerHTML = '';

        if (playlists && playlists.length > 0) {
            // 有歌单，隐藏空状态提示
            emptyPlaylistsElement.classList.add('d-none');

            // 渲染歌单列表
            playlists.forEach(playlist => {
                const card = document.createElement('div');
                card.className = 'col-md-4 mb-4';
                card.innerHTML = `
                    <div class="card h-100">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-start">
                                <h5 class="card-title">${playlist.name}</h5>
                                <div class="dropdown">
                                    <button class="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" id="dropdownMenuButton${playlist.id}" data-bs-toggle="dropdown" aria-expanded="false">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-three-dots-vertical" viewBox="0 0 16 16">
                                            <path d="M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zm0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zm0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z"/>
                                        </svg>
                                    </button>
                                    <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton${playlist.id}">
                                        <li><a class="dropdown-item delete-playlist" href="#" data-id="${playlist.id}">删除歌单</a></li>
                                    </ul>
                                </div>
                            </div>
                            <p class="card-text">${playlist.description ? playlist.description.substring(0, 100) + '...' : '暂无描述'}</p>
                            <p class="card-text">
                                <small class="text-muted">歌曲数量: ${playlist.songCount || 0}</small>
                            </p>
                            <p class="card-text">
                                <small class="text-muted">创建时间: ${this.formatDate(playlist.createdAt)}</small>
                            </p>
                        </div>
                        <div class="card-footer">
                            <a href="/playlist-detail?id=${playlist.id}" class="btn btn-sm btn-primary">查看详情</a>
                        </div>
                    </div>
                `;
                playlistListElement.appendChild(card);
            });

            // 添加删除歌单事件监听
            this.setupDeletePlaylistButtons();
        } else {
            // 没有歌单，显示空状态提示
            emptyPlaylistsElement.classList.remove('d-none');
        }
    },

    // 设置导入歌单表单
    setupImportPlaylistForm: function() {
        const importForm = document.getElementById('importPlaylistForm');
        if (importForm) {
            importForm.addEventListener('submit', (event) => {
                event.preventDefault();
                this.importPlaylistFromNetease();
            });
        }
    },

    // 从网易云音乐导入歌单
    importPlaylistFromNetease: function() {
        const playlistIdInput = document.getElementById('neteasePlaylistId');
        const neteasePlaylistId = playlistIdInput.value.trim();

        if (!neteasePlaylistId) {
            alert('请输入网易云歌单ID');
            return;
        }

        // 显示导入中状态
        const importBtn = document.getElementById('importPlaylistForm').querySelector('button[type="submit"]');
        const originalText = importBtn.innerHTML;
        importBtn.disabled = true;
        importBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> 导入中...';

        api.importNeteasePlaylist(neteasePlaylistId)
            .then(() => {
                alert('歌单导入成功！');
                // 重新加载歌单列表
                this.loadPlaylists();
                // 清空输入框
                playlistIdInput.value = '';
            })
            .catch(error => {
                console.error('导入歌单失败:', error);
                alert(error.message);
            })
            .finally(() => {
                // 恢复按钮状态
                importBtn.disabled = false;
                importBtn.innerHTML = originalText;
            });
    },

    // 设置删除歌单按钮事件监听
    setupDeletePlaylistButtons: function() {
        const deleteButtons = document.querySelectorAll('.delete-playlist');
        deleteButtons.forEach(button => {
            button.addEventListener('click', function(event) {
                event.preventDefault();
                const playlistId = this.getAttribute('data-id');
                if (confirm('确定要删除这个歌单吗？此操作不可撤销。')) {
                    dashboard.deletePlaylist(playlistId);
                }
            });
        });
    },

    // 删除歌单
    deletePlaylist: function(playlistId) {
        api.deletePlaylist(playlistId)
            .then(() => {
                // 重新加载歌单列表
                this.loadPlaylists();
            })
            .catch(error => {
                console.error('删除歌单失败:', error);
                alert(error.message);
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
    },

    // 格式化日期
    formatDate: function(dateString) {
        if (!dateString) return '-';
        
        const date = new Date(dateString);
        if (isNaN(date.getTime())) return '-';
        
        return date.toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }
};

// 页面加载完成后初始化
window.addEventListener('load', () => {
    if (auth.isAuthenticated()) {
        dashboard.init();
    }
});