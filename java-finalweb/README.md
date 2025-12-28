# 网易云音乐歌单导出系统

## 项目简介
这是一个基于Spring Boot开发的网易云音乐歌单导出系统，允许用户登录、导入网易云音乐歌单、查看歌单详情并导出为M3U格式。

## 技术栈
- 后端：Spring Boot 2.7.12、Spring Security、Spring Data JPA、MySQL
- 前端：HTML5、Bootstrap 5、JavaScript
- 认证：JWT
- 构建工具：Maven

## 系统功能
1. 用户认证：登录、注册
2. 歌单管理：导入网易云音乐歌单、查看所有歌单、查看歌单详情
3. 歌单搜索：搜索歌单
4. 歌单导出：将歌单导出为M3U格式

## 环境要求
- JDK 11+
- Maven 3.6+
- MySQL 8.0+

## 安装和运行

### 1. 数据库配置
- 创建MySQL数据库：`netease_music_export`
- 修改`src/main/resources/application.properties`中的数据库连接信息：
  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/netease_music_export?useSSL=false&serverTimezone=UTC
  spring.datasource.username=your_username
  spring.datasource.password=your_password
  ```

### 2. 构建项目
```bash
mvn clean package
```

### 3. 运行应用
```bash
java -jar target/netease-music-export-0.0.1-SNAPSHOT.jar
```

### 4. 访问系统
应用启动后，可通过以下地址访问：
- 首页：http://localhost:8080/netease-music/
- 登录页：http://localhost:8080/netease-music/login

## API文档

### 认证相关
- POST `/api/auth/signin` - 用户登录
- POST `/api/auth/signup` - 用户注册

### 歌单相关
- GET `/api/playlists` - 获取用户歌单列表
- GET `/api/playlists/{id}` - 获取歌单详情
- POST `/api/playlists/import/netease/{playlistId}` - 导入网易云音乐歌单
- DELETE `/api/playlists/{id}` - 删除歌单
- GET `/api/playlists/{id}/export?format=m3u` - 导出歌单

### 搜索相关
- GET `/api/search?keyword={keyword}&type={type}` - 搜索歌单

## 功能测试说明

### 测试用户注册和登录
1. 访问 http://localhost:8080/netease-music/register
2. 填写注册信息，点击注册
3. 注册成功后将自动登录并重定向到仪表盘

### 测试导入歌单
1. 登录后，在仪表盘页面输入网易云音乐歌单ID
2. 点击导入按钮
3. 导入成功后，歌单将显示在歌单列表中

### 测试查看歌单详情
1. 在歌单列表中点击任意歌单的详情按钮
2. 页面将显示歌单信息和歌曲列表

### 测试导出歌单
1. 在歌单详情页面，点击导出按钮
2. 系统将生成并下载M3U格式的歌单文件

### 测试搜索歌单
1. 点击顶部导航栏的搜索链接
2. 输入关键词，选择搜索类型，点击搜索
3. 搜索结果将显示在页面上

## 注意事项
1. 请确保已正确配置MySQL数据库
2. 确保已配置正确的网易云音乐API信息
3. 系统需要网络连接以访问网易云音乐API
4. 部分功能可能需要登录后才能使用