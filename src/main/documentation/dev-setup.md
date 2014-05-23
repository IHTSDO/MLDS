
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

DB -
Install Postgres
Create mlds user password=password
Create mlds database, owned by mlds user.
Create Datasource in tomcat context.xml - Add
	<!-- IHTSDO MLDS datasource -->  
    <Resource 
    	name="jdbc/mlds" 
    	type="javax.sql.DataSource" 
    	url="jdbc:postgresql:mlds" 
    	driverClassName="org.postgresql.Driver" 
    	auth="Container" 
    	username="mlds"
    	password="password" 
    	maxActive="20" maxIdle="4" 
    	/>
to the end of the <Context> section.
"Publish" the changes to the server.

Download a driver from http://jdbc.postgresql.org/download/postgresql-9.3-1101.jdbc41.jar
and copy to the Tomcat /lib folder


