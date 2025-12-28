// 认证相关的功能
const auth = {
    // 获取存储的令牌
    getToken: function() {
        return localStorage.getItem('token');
    },

    // 保存令牌
    saveToken: function(token) {
        localStorage.setItem('token', token);
    },

    // 清除令牌
    clearToken: function() {
        localStorage.removeItem('token');
    },

    // 检查用户是否已登录
    isAuthenticated: function() {
        return this.getToken() !== null;
    },

    // 配置api默认请求头
    configureApi: function() {
        const token = this.getToken();
        if (token) {
            api.setAuthToken(token);
        }
    },

    // 退出登录
    logout: function() {
        this.clearToken();
        api.clearAuthToken();
        window.location.href = '/login';
    },

    // 初始化认证状态
    init: function() {
        // 配置api默认请求头
        this.configureApi();

        // 检查用户是否已登录，如果未登录则跳转到登录页面
        if (!this.isAuthenticated()) {
            // 排除登录和注册页面
            const currentPath = window.location.pathname;
            if (currentPath !== '/login' && currentPath !== '/register') {
                window.location.href = '/login';
                return false;
            }
        } else {
            // 如果已登录，显示用户名
            this.displayUsername();
            
            // 添加退出登录事件监听
            const logoutBtn = document.getElementById('logoutBtn');
            if (logoutBtn) {
                logoutBtn.addEventListener('click', () => this.logout());
            }
        }
        return true;
    },

    // 显示当前登录的用户名
    displayUsername: function() {
        const usernameElement = document.getElementById('username');
        if (usernameElement) {
            // 从本地存储获取用户名或发送请求获取用户信息
            const username = localStorage.getItem('username') || '用户';
            usernameElement.textContent = username;
        }
    },

    // 保存用户信息到本地存储
    saveUserInfo: function(user) {
        if (user && user.username) {
            localStorage.setItem('username', user.username);
        }
    }
};

// 页面加载时初始化认证
window.addEventListener('load', () => {
    auth.init();
});

// 设置API错误处理
api.setErrorHandler(function(error) {
    if (error.response && error.response.status === 401) {
        // 未授权，清除令牌并重定向到登录页面
        auth.clearToken();
        window.location.href = '/login';
    }
    return Promise.reject(error);
});

// 导入api模块
if (typeof api === 'undefined') {
    console.error('API模块未定义，请确保api.js已正确加载');
}