# 网易云音乐歌单导出系统 - 环境安装与配置指南

## 1. Java JDK 8 安装

### 1.1 下载 JDK 8

1. 访问 [Oracle JDK 8 下载页面](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html)（需要Oracle账号登录）
   或者
   访问 [AdoptOpenJDK 8 下载页面](https://adoptium.net/temurin/releases/?version=8)（无需登录）

2. 选择适合您操作系统的版本：
   - Windows: 选择 `x64 Installer` 或 `x86 Installer`（根据您的系统架构）
   - macOS: 选择 `x64 DMG Installer`
   - Linux: 根据您的发行版选择相应的包

### 1.2 安装 JDK 8

#### Windows 安装步骤：
1. 双击下载的 `.exe` 文件开始安装
2. 点击「下一步」，保留默认安装路径（通常为 `C:\Program Files\Java\jdk1.8.x_xxx`）
3. 完成安装

#### macOS 安装步骤：
1. 双击下载的 `.dmg` 文件
2. 按照提示完成安装

#### Linux 安装步骤（以 Ubuntu 为例）：
```bash
sudo apt update
sudo apt install openjdk-8-jdk
```

### 1.3 配置环境变量

#### Windows 配置步骤：
1. 右键点击「此电脑」→ 「属性」→ 「高级系统设置」→ 「环境变量」
2. 在「系统变量」部分，点击「新建」
3. 变量名：`JAVA_HOME`
   变量值：JDK 安装路径（例如 `C:\Program Files\Java\jdk1.8.0_301`）
4. 编辑「Path」变量，添加 `%JAVA_HOME%\bin`
5. 点击「确定」保存所有更改

#### macOS 配置步骤：
1. 打开终端
2. 编辑或创建 `~/.bash_profile` 或 `~/.zshrc` 文件：
   ```bash
   nano ~/.bash_profile  # 或 nano ~/.zshrc
   ```
3. 添加以下内容：
   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
   export PATH=$JAVA_HOME/bin:$PATH
   ```
4. 保存并退出：`Ctrl+O` → `Enter` → `Ctrl+X`
5. 执行：`source ~/.bash_profile` 或 `source ~/.zshrc`

#### Linux 配置步骤：
1. 打开终端
2. 编辑 `/etc/profile` 文件：
   ```bash
   sudo nano /etc/profile
   ```
3. 添加以下内容：
   ```bash
   export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64  # 路径可能因发行版而异
   export PATH=$JAVA_HOME/bin:$PATH
   ```
4. 保存并退出
5. 执行：`source /etc/profile`

### 1.4 验证安装

打开命令提示符或终端，执行以下命令：
```bash
java -version
```

如果安装成功，将显示 Java 版本信息，类似于：
```
java version "1.8.0_301"
Java(TM) SE Runtime Environment (build 1.8.0_301-b09)
Java HotSpot(TM) 64-Bit Server VM (build 25.301-b09, mixed mode)
```

## 2. Maven 安装

### 2.1 下载 Maven

访问 [Apache Maven 官网下载页面](https://maven.apache.org/download.cgi)

下载最新的二进制 zip 文件（例如 `apache-maven-3.8.6-bin.zip`）

### 2.2 安装 Maven

#### Windows 安装步骤：
1. 解压下载的 zip 文件到您选择的目录（例如 `C:\Program Files\Apache Maven`）
2. 重命名解压后的文件夹为 `apache-maven-3.8.6`（或其他版本号）

#### macOS/Linux 安装步骤：
```bash
# 创建 Maven 目录
sudo mkdir -p /opt/maven
# 解压下载的文件
sudo tar -xf apache-maven-3.8.6-bin.tar.gz -C /opt/maven
# 创建软链接
sudo ln -s /opt/maven/apache-maven-3.8.6 /opt/maven/latest
```

### 2.3 配置环境变量

#### Windows 配置步骤：
1. 右键点击「此电脑」→ 「属性」→ 「高级系统设置」→ 「环境变量」
2. 在「系统变量」部分，点击「新建」
3. 变量名：`MAVEN_HOME`
   变量值：Maven 安装路径（例如 `C:\Program Files\Apache Maven\apache-maven-3.8.6`）
4. 编辑「Path」变量，添加 `%MAVEN_HOME%\bin`
5. 点击「确定」保存所有更改

#### macOS 配置步骤：
1. 打开终端
2. 编辑或创建 `~/.bash_profile` 或 `~/.zshrc` 文件：
   ```bash
   nano ~/.bash_profile  # 或 nano ~/.zshrc
   ```
3. 添加以下内容：
   ```bash
   export M2_HOME=/opt/maven/latest
   export MAVEN_HOME=/opt/maven/latest
   export PATH=${M2_HOME}/bin:${PATH}
   ```
4. 保存并退出：`Ctrl+O` → `Enter` → `Ctrl+X`
5. 执行：`source ~/.bash_profile` 或 `source ~/.zshrc`

#### Linux 配置步骤：
1. 打开终端
2. 编辑 `/etc/profile.d/maven.sh` 文件：
   ```bash
   sudo nano /etc/profile.d/maven.sh
   ```
3. 添加以下内容：
   ```bash
   export M2_HOME=/opt/maven/latest
   export MAVEN_HOME=/opt/maven/latest
   export PATH=${M2_HOME}/bin:${PATH}
   ```
4. 保存并退出
5. 设置可执行权限：
   ```bash
   sudo chmod +x /etc/profile.d/maven.sh
   ```
6. 执行：`source /etc/profile.d/maven.sh`

### 2.4 验证安装

打开命令提示符或终端，执行以下命令：
```bash
mvn -version
```

如果安装成功，将显示 Maven 版本信息，类似于：
```
Apache Maven 3.8.6 (84538c9988a25aec085021c365c560670ad80f63)
Maven home: C:\Program Files\Apache Maven\apache-maven-3.8.6
Java version: 1.8.0_301, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk1.8.0_301\jre
Default locale: zh_CN, platform encoding: GBK
OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"
```

## 3. MySQL 安装

### 3.1 下载 MySQL

访问 [MySQL 下载页面](https://dev.mysql.com/downloads/mysql/)

选择适合您操作系统的版本并下载

### 3.2 安装 MySQL

#### Windows 安装步骤：
1. 双击下载的 `.msi` 文件开始安装
2. 选择「Developer Default」安装类型
3. 按照向导完成安装
4. 在配置过程中，设置 root 用户密码
5. 确保 MySQL 服务设置为自动启动

#### macOS 安装步骤：
1. 下载 MySQL Community Server DMG 安装包
2. 双击安装包并按照提示完成安装
3. 在系统偏好设置中配置 MySQL 服务

#### Linux 安装步骤（以 Ubuntu 为例）：
```bash
# 更新包列表
sudo apt update
# 安装 MySQL 服务器
sudo apt install mysql-server
# 运行安全配置脚本
sudo mysql_secure_installation
```

### 3.3 启动 MySQL 服务

#### Windows：
MySQL 服务通常在安装后自动启动。如果需要手动启动：
1. 按下 `Win + R`，输入 `services.msc`
2. 找到「MySQL80」服务（或您安装的版本）
3. 右键点击并选择「启动」

#### macOS：
1. 打开系统偏好设置
2. 点击 MySQL
3. 点击「Start MySQL Server」

#### Linux：
```bash
sudo systemctl start mysql
# 设置开机自启
sudo systemctl enable mysql
```

### 3.4 创建数据库和用户

1. 登录 MySQL：

```bash
# Windows
mysql -u root -p

# Linux/macOS
sudo mysql -u root -p
```

2. 输入之前设置的 root 密码

3. 创建数据库：

```sql
CREATE DATABASE netease_music_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

4. 创建用户并授权：

```sql
CREATE USER 'music_user'@'localhost' IDENTIFIED BY 'music_password';
GRANT ALL PRIVILEGES ON netease_music_db.* TO 'music_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

## 4. 项目配置

### 4.1 配置数据库连接

1. 打开项目中的 `application.properties` 文件：

```
src/main/resources/application.properties
```

2. 如果该文件不存在，则创建它

3. 添加以下配置（确保替换为您的数据库信息）：

```properties
# 数据库连接信息
spring.datasource.url=jdbc:mysql://localhost:3306/netease_music_db?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=music_user
spring.datasource.password=music_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect

# 服务器配置
server.port=8080
server.servlet.context-path=/netease-music

# 日志配置
logging.level.root=INFO
logging.level.com.musicapp=DEBUG

# 网易云音乐API配置（根据实际情况修改）
netease.api.base-url=https://api.music.163.com
```

### 4.2 创建必要的目录结构

确保项目具有以下目录结构：

```
src/main/java/com/musicapp/neteasemusic/
src/main/resources/static/js/
src/main/resources/templates/
```

如果某些目录不存在，请创建它们。

## 5. 构建和运行项目

### 5.1 构建项目

打开命令提示符或终端，导航到项目根目录，执行以下命令：

```bash
cd c:\Users\BCXW\Documents\trae_projects\java-finalweb
mvn clean package -DskipTests
```

如果构建成功，将在 `target` 目录下生成一个 JAR 文件。

### 5.2 运行项目

#### 方法一：使用 Maven 运行

在项目根目录执行：

```bash
mvn spring-boot:run
```

#### 方法二：使用 Java 运行 JAR 文件

构建成功后，执行：

```bash
java -jar target/netease-music-export-1.0-SNAPSHOT.jar
```

### 5.3 验证项目运行

1. 打开浏览器，访问：
   ```
   http://localhost:8080/netease-music/
   ```

2. 如果一切正常，您应该能看到项目的登录页面。

## 6. 常见问题与解决方案

### 6.1 数据库连接问题

**错误：** `Access denied for user 'music_user'@'localhost'`

**解决方案：**
- 检查数据库用户名和密码是否正确
- 确保用户有权限访问数据库
- 尝试重新创建用户并授予权限

### 6.2 端口占用问题

**错误：** `Address already in use`

**解决方案：**
- 修改 `application.properties` 中的 `server.port` 值
- 或者关闭占用该端口的其他应用

### 6.3 Maven 依赖问题

**错误：** `Could not resolve dependencies`

**解决方案：**
- 确保网络连接正常
- 清理 Maven 缓存：`mvn dependency:purge-local-repository`
- 重新下载依赖：`mvn clean install`

### 6.4 Java 版本问题

**错误：** 不兼容的 Java 版本

**解决方案：**
- 确保使用的是 Java 8
- 检查 `JAVA_HOME` 环境变量是否指向正确的 JDK 8 安装目录

## 7. 开发工具推荐

### 7.1 IDE 推荐

- [IntelliJ IDEA](https://www.jetbrains.com/idea/) - 功能强大的 Java IDE
- [Eclipse](https://www.eclipse.org/downloads/) - 免费开源的 Java IDE
- [Visual Studio Code](https://code.visualstudio.com/) + Java 扩展包

### 7.2 数据库工具

- [MySQL Workbench](https://www.mysql.com/products/workbench/) - 官方 MySQL 图形界面工具
- [DBeaver](https://dbeaver.io/) - 通用数据库管理工具

## 8. 额外资源

- [Spring Boot 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [MySQL 官方文档](https://dev.mysql.com/doc/)
- [Maven 官方文档](https://maven.apache.org/guides/)

---

按照以上步骤配置完成后，您应该能够成功运行网易云音乐歌单导出系统。如果遇到任何问题，请参考「常见问题与解决方案」部分，或查阅相关官方文档获取更多帮助。