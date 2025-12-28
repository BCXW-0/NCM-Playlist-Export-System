// API交互相关功能
const api = {
    // 基础API URL
    baseUrl: '/netease-music/api',

    // 登录请求
    login: async function(username, password) {
        try {
            const response = await axios.post(`${this.baseUrl}/auth/signin`, {
                username,
                password
            });
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    },

    // 注册请求
    register: async function(username, email, password) {
        try {
            const response = await axios.post(`${this.baseUrl}/auth/signup`, {
                username,
                email,
                password
            });
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    },

    // 获取用户歌单列表
    getUserPlaylists: async function() {
        try {
            const response = await axios.get(`${this.baseUrl}/playlists`);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    },

    // 获取歌单详情
    getPlaylistDetails: async function(playlistId) {
        try {
            const response = await axios.get(`${this.baseUrl}/playlists/${playlistId}`);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    },

    // 获取歌单歌曲列表
    getPlaylistSongs: async function(playlistId) {
        try {
            const response = await axios.get(`${this.baseUrl}/playlists/${playlistId}/songs`);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    },

    // 从网易云音乐导入歌单
    importNeteasePlaylist: async function(playlistId) {
        try {
            const response = await axios.post(`${this.baseUrl}/playlists/import/netease/${playlistId}`);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    },

    // 搜索网易云歌单
    searchNeteasePlaylists: async function(keyword) {
        try {
            const response = await axios.get(`${this.baseUrl}/playlists/search?keyword=${encodeURIComponent(keyword)}`);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    },

    // 删除歌单
    deletePlaylist: async function(playlistId) {
        try {
            const response = await axios.delete(`${this.baseUrl}/playlists/${playlistId}`);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    },

    // 处理错误响应
    handleError: function(error) {
        let errorMessage = '操作失败，请稍后重试';
        
        if (error.response) {
            // 服务器返回了错误状态码
            if (error.response.data) {
                // 如果响应体中有错误信息
                if (typeof error.response.data === 'string') {
                    errorMessage = error.response.data;
                } else if (error.response.data.message) {
                    errorMessage = error.response.data.message;
                } else if (error.response.data.error) {
                    errorMessage = error.response.data.error;
                }
            }
            
            // 处理特定的错误状态码
            if (error.response.status === 401) {
                // 未授权，清除认证信息
                auth.clearToken();
                window.location.href = '/login';
            }
        } else if (error.request) {
            // 请求已发出但没有收到响应
            errorMessage = '网络错误，请检查您的网络连接';
        }
        
        console.error('API错误:', error);
        return new Error(errorMessage);
    }
};

// 处理登录表单提交
if (document.getElementById('loginForm')) {
    document.getElementById('loginForm').addEventListener('submit', async function(event) {
        event.preventDefault();
        
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const errorMessage = document.getElementById('errorMessage');
        
        // 重置错误消息
        errorMessage.style.display = 'none';
        
        try {
            // 显示加载状态
            const submitBtn = this.querySelector('button[type="submit"]');
            const originalText = submitBtn.textContent;
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> 登录中...';
            
            // 调用登录API
            const response = await api.login(username, password);
            
            // 保存令牌和用户信息
            auth.saveToken(response.token);
            auth.saveUserInfo({
                username: response.username,
                email: response.email
            });
            
            // 配置axios默认请求头
            auth.configureAxios();
            
            // 跳转到仪表盘
            window.location.href = '/dashboard';
        } catch (error) {
            // 显示错误消息
            errorMessage.textContent = error.message;
            errorMessage.style.display = 'block';
        } finally {
            // 恢复按钮状态
            const submitBtn = this.querySelector('button[type="submit"]');
            submitBtn.disabled = false;
            submitBtn.innerHTML = '登录';
        }
    });
}

// 处理注册表单提交
if (document.getElementById('registerForm')) {
    document.getElementById('registerForm').addEventListener('submit', async function(event) {
        event.preventDefault();
        
        const username = document.getElementById('username').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const errorMessage = document.getElementById('errorMessage');
        
        // 验证密码匹配
        if (password !== confirmPassword) {
            errorMessage.textContent = '两次输入的密码不一致';
            errorMessage.style.display = 'block';
            return;
        }
        
        // 重置错误消息
        errorMessage.style.display = 'none';
        
        try {
            // 显示加载状态
            const submitBtn = this.querySelector('button[type="submit"]');
            const originalText = submitBtn.textContent;
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> 注册中...';
            
            // 调用注册API
            await api.register(username, email, password);
            
            // 注册成功后跳转到登录页面
            alert('注册成功，请登录');
            window.location.href = '/login';
        } catch (error) {
            // 显示错误消息
            errorMessage.textContent = error.message;
            errorMessage.style.display = 'block';
        } finally {
            // 恢复按钮状态
            const submitBtn = this.querySelector('button[type="submit"]');
            submitBtn.disabled = false;
            submitBtn.innerHTML = '注册';
        }
    });
}