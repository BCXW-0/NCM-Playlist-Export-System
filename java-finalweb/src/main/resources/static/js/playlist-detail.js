// 歌单详情页面的功能
const playlistDetail = {
    // 初始化页面
    init: function() {
        // 获取URL中的歌单ID
        const playlistId = this.getPlaylistIdFromUrl();
        if (playlistId) {
            // 加载歌单信息
            this.loadPlaylistDetail(playlistId);
            // 加载歌曲列表
            this.loadSongs(playlistId);
            // 设置导出按钮事件
            this.setupExportButton(playlistId);
        } else {
            this.showError('无效的歌单ID');
        }
    },

    // 从URL获取歌单ID
    getPlaylistIdFromUrl: function() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('id');
    },

    // 加载歌单信息
    loadPlaylistDetail: function(playlistId) {
        // 显示加载状态
        this.showLoading();

        api.getPlaylistDetail(playlistId)
            .then(playlist => {
                this.updatePlaylistInfo(playlist);
                this.hideLoading();
            })
            .catch(error => {
                console.error('加载歌单信息失败:', error);
                this.showError(error.message);
                this.hideLoading();
            });
    },

    // 更新歌单信息显示
    updatePlaylistInfo: function(playlist) {
        document.getElementById('playlistName').textContent = playlist.name;
        document.getElementById('playlistDescription').textContent = playlist.description || '暂无描述';
        document.getElementById('neteasePlaylistId').textContent = playlist.neteasePlaylistId || '自定义歌单';
        document.getElementById('songCount').textContent = playlist.songCount || 0;
    },

    // 加载歌曲列表
    loadSongs: function(playlistId) {
        axios.get(`/api/playlists/${playlistId}/songs`)
            .then(response => {
                const songs = response.data;
                this.renderSongs(songs);
                // 隐藏加载状态，显示内容
                document.getElementById('loading').classList.add('d-none');
                document.getElementById('content').classList.remove('d-none');
            })
            .catch(error => {
                console.error('加载歌曲列表失败:', error);
                this.showError('加载歌曲列表失败');
            });
    },

    // 渲染歌曲列表
    renderSongs: function(songs) {
        const songListElement = document.getElementById('songList');
        const emptySongsElement = document.getElementById('emptySongs');

        // 清空现有列表
        songListElement.innerHTML = '';

        if (songs && songs.length > 0) {
            // 有歌曲，隐藏空状态提示
            emptySongsElement.classList.add('d-none');

            // 渲染歌曲
            songs.forEach((song, index) => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <th scope="row">${index + 1}</th>
                    <td>${song.name}</td>
                    <td>${song.artist}</td>
                    <td>${song.album}</td>
                    <td>${this.formatDuration(song.duration)}</td>
                `;
                songListElement.appendChild(row);
            });
        } else {
            // 没有歌曲，显示空状态提示
            emptySongsElement.classList.remove('d-none');
        }
    },

    // 设置导出按钮事件
    setupExportButton: function(playlistId) {
        const exportBtn = document.getElementById('exportBtn');
        if (exportBtn) {
            exportBtn.addEventListener('click', () => {
                this.exportPlaylist(playlistId);
            });
        }
    },

    // 导出歌单
    exportPlaylist: function(playlistId) {
        // 显示加载状态
        const exportBtn = document.getElementById('exportBtn');
        const originalText = exportBtn.innerHTML;
        exportBtn.disabled = true;
        exportBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> 导出中...';

        api.exportPlaylist(playlistId)
        .then(() => {
            // 导出已处理在API函数中
        })
        .catch(error => {
            console.error('导出歌单失败:', error);
            alert(error.message);
        })
        .finally(() => {
            // 恢复按钮状态
            exportBtn.disabled = false;
            exportBtn.innerHTML = originalText;
        });
    },

    // 显示错误信息
    showError: function(message) {
        const errorElement = document.getElementById('error');
        errorElement.textContent = message;
        errorElement.classList.remove('d-none');
        
        // 隐藏加载状态
        document.getElementById('loading').classList.add('d-none');
    },
    
    // 显示加载状态
    showLoading: function() {
        document.getElementById('loading').classList.remove('d-none');
        document.getElementById('content').classList.add('d-none');
    },
    
    // 隐藏加载状态
    hideLoading: function() {
        document.getElementById('loading').classList.add('d-none');
        document.getElementById('content').classList.remove('d-none');
    },

    // 格式化时长
    formatDuration: function(durationInSeconds) {
        if (!durationInSeconds && durationInSeconds !== 0) return '-';
        
        const minutes = Math.floor(durationInSeconds / 60);
        const seconds = Math.floor(durationInSeconds % 60);
        return `${minutes}:${seconds.toString().padStart(2, '0')}`;
    }
};

// 页面加载完成后初始化
window.addEventListener('load', () => {
    if (auth.isAuthenticated()) {
        playlistDetail.init();
    }
});