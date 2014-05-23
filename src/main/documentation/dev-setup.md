
#[IHTSDO](http://www.ihtsdo.org "IHTSDO") Development Environment Setup

###Create project directory
- /projects/IHTSDO


###Install Java 7
//iwdbdc/iwd/iwd_apps/Java/1.7/


###Install Eclipse Kepler JEE
- Grab Kepler from //iwdbdc/iwd/iwd_apps/Eclipse/4.3-Kepler
- Unzip in /projects/IHTSDO/eclipse-kepler-j2ee


###Configure Eclipse

####Runnning Eclipse on on the jdk7
Edit eclipse.ini - beside eclipse.exe on Windows, in the .app bundle under /Contents/MacOS/
- Add two lines just before the -vmargs line
	-vm
	PATH_TO_JAVAW (e.g. C:\Java\JDK\1.6\bin\javaw.exe)

####Workspace
Start eclipse and create Eclipse workspace: /projects/IHTSDO/workspace

####Check the project out of GIT
1. Install m2e egit connector 
	- Help --> Eclipse Marketplace --> search for "m2e egit"
	- Install
2. File-->Import --> Check out existing Maven project from SCM
	- Go to the m2e Marketplace and search for 'egit'
	- Install the plugin and restart Eclipse
3. File-->Import --> Check out existing Maven project from SCM
	- Pick "git" and enter repo url: git@github.com:IHTSDO/MLDS.git
	- next,next,finish.  Nothing special


###Install Tomcat 7

- //iwdbdc/iwd/iwd_apps/Apache Group/tomcat/Tomcat7/
- Unzip Tomcat into /projects/IHTSDO/tomcat7/

Create "Server" definition inside eclipse using Servers view and deploy the app

Start the server and verify that the app has started at http://localhost:8080/ihtsdo-mlds/
