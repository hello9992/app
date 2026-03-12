@echo off
echo 正在构建APK...
gradlew.bat assembleRelease
echo.
echo APK文件位置: app\build\outputs\apk\release\app-release-unsigned.apk
pause
