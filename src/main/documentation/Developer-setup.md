
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
- Add two lines just before the `-vmargs` line

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


###Install Database
- Install Postgres (http://www.postgresql.org/download/)
- Create mlds user password=password
- Create mlds database, owned by mlds user.

###Running the application
- Run the "Application" class at the root of the Java package hierarchy directly
- Access the site at: http://localhost:8080

### Populating developer database
- The first run of "Application" will build the schema using liquibase.
- On a private developer box you may want to load in a minimal set of user accounts from src/main/ad-hoc/minimal-dev-db.sql
 - sudo -u postgres psql --set ON_ERROR_STOP=on mlds  < minimal-dev-db.sql
 - DO NOT do this on a public accessable machine
 - You can then use the following accounts: admin, staff, sweden, user with matching passwords


####[JHipster Development Docs](http://jhipster.github.io/development.html "JHipster Development Docs")


