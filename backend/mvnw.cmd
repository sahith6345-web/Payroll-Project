@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------

@IF "%JAVA_HOME%" == "" (
  SET "JAVACMD=java"
) ELSE (
  SET "JAVACMD=%JAVA_HOME%\bin\java"
)

@SET MAVEN_PROJECTBASEDIR=%~dp0
@SET WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar
@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@IF EXIST "%WRAPPER_JAR%" GOTO run

@powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $dir = [System.IO.Path]::GetDirectoryName('%WRAPPER_JAR%'); if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir | Out-Null }; (New-Object Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar', '%WRAPPER_JAR%')"

:run
"%JAVACMD%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%\" -classpath "%WRAPPER_JAR%" %WRAPPER_LAUNCHER% %*
