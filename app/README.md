# Android应用 - 蓝牙耳机电量监控

这是一个监控蓝牙耳机电量的Android应用。

## 功能
- 连接蓝牙耳机
- 实时显示电量变化
- 二维坐标图：X轴为时间（分钟），Y轴为电量（0-100）

## 构建APK（无需安装任何软件）

### 使用GitHub自动构建（最简单）
1. 在GitHub创建新仓库
2. 将 `g:\app` 文件夹的所有文件上传到仓库
3. 点击仓库的 "Actions" 标签
4. 等待自动构建完成（约5分钟）
5. 点击构建任务，下载 "app-debug" 文件
6. 解压得到 `app-debug.apk`，传到手机安装

### 上传到GitHub的步骤
```bash
cd g:\app
git init
git add .
git commit -m "初始提交"
git branch -M main
git remote add origin https://github.com/你的用户名/你的仓库名.git
git push -u origin main
```

### 其他方法
- 使用Android Studio：需要下载安装
- 命令行构建：需要安装Java JDK和Android SDK

## 安装到手机
1. 将APK文件传到手机（通过USB、微信、QQ等）
2. 在手机上打开APK文件
3. 允许安装未知来源应用
4. 安装完成后打开应用
5. 授予蓝牙权限
6. 连接蓝牙耳机即可看到电量曲线

## 权限
应用需要蓝牙权限来监控耳机电量。
