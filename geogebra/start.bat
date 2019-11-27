@cd /d %~dp0
@IF %1=="two" start "GeoGebra Gradle Process" cmd.exe /k "timeout 10 >nul & cls & gradlew.bat :desktop:run & pause >nul & exit"
@java.exe "-Dorg.gradle.appname=gradlew" -classpath "%~dp0\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain :desktop:run