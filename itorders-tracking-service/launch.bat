@echo off

set mavenInput="%*"

if "%*" == "" (
	echo No Maven arguments skipping maven build
) else (
	echo Running with user input: %mavenInput%
	echo Running maven build on available project

	call mvn -v >con

	cd ..

	for %%s in ("-model" "itorders-tracking-service") do (

			cd *%%s
			echo ===============================================================================
            for %%I in (.) do echo %%~nxI
            echo ===============================================================================

			if exist "%M3_HOME%\bin\mvn.bat" (
				call %M3_HOME%\bin\mvn.bat %* >con
			) else (
				call mvn %* >con
			)

			cd ..

	)
)

goto :startapp

:startapp
	if not x%mavenInput:docker=%==x%mavenInput% (
		echo Launching the application as docker container...
		call docker run -d -p 8090:8090 --name itorders-tracking-service apps/itorders-tracking-service:1.0-SNAPSHOT
	) else if not x%mavenInput:openshift=%==x%mavenInput% (
		echo Launching the application on OpenShift...
		call oc new-app itorders-tracking-service:1.0-SNAPSHOT
		call oc expose svc/itorders-tracking-service
	) else (
		echo "Launching the application locally..."
		cd itorders-tracking-service
		cd target
		for /f "delims=" %%x in ('dir /od /b *-fat.jar') do set latestjar=%%x
		cd ..
		call java -jar target\%latestjar%
	)


:end
