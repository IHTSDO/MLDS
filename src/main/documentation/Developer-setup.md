
#[IHTSDO](http://www.ihtsdo.org "IHTSDO") Development Environment Setup

###Create project directory
- /projects/IHTSDO

###Install Java 17
//iwdbdc/iwd/iwd_apps/Java/17/


###Install Eclipse Kepler JEE
- Grab Kepler from //iwdbdc/iwd/iwd_apps/Eclipse/4.3-Kepler
  - Unzip in /projects/IHTSDO/eclipse-kepler-j2ee


(**or**)
  
###Install Intellij JEE
- Grab IntelliJ IDEA from JetBrains website (choose the appropriate version for your OS).
  - Unzip/Install in /projects/IHTSDO/intellij.


###Configure Eclipse

####Runnning Eclipse on on the jdk17
Edit eclipse.ini - beside eclipse.exe on Windows, in the .app bundle under /Contents/MacOS/
- Add two lines just before the `-vmargs` line

	-vm
	PATH_TO_JAVAW (e.g. C:\Java\JDK\17\bin\javaw.exe)
	
####Workspace
Start eclipse and create Eclipse workspace: /projects/IHTSDO/workspace

   (**or**)

####Runnning Intellij on on the jdk17
Edit IntelliJ IDEA Configuration - beside intellij.exe on Windows, in the .app bundle under /Contents/MacOS/
- Add two lines just before the `-vmargs` line

  -vm
  PATH_TO_JAVAW (e.g. C:\Java\JDK\17\bin\javaw.exe)

####Workspace
Start intellij and create intellij workspace: /projects/IHTSDO/workspace

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

###Change below values in application.properties in the application
- Replace value for database  
  - (**spring.datasource.url=jdbc:mysql://localhost:3306/mlds**).
  - (**spring.datasource.serverName=localhost**)
  - (**spring.datasource.username=mlds**)
  - (**spring.datasource.password=(Replace with Database Password)**)

###Install Database
- Install MySQL (https://dev.mysql.com/downloads)
- Create mlds user password=password
- Create mlds schema, owned by mlds user.

###Before Running the application
- To run the code locally we need to set the region for S3 Configuration.
- For Windows machine:Set this as an Environment variable

  **setx AWS_REGION us-east-1**
    
  After The Environment Variable is Declared Restart the machine.

###Running the application
- Run the "Application" class at the root of the Java package hierarchy directly
- Access the site at: http://localhost:8080

### Populating developer database
- The first run of "Application" will build the schema using liquibase.
- On a private developer box you may want to load in a minimal set of user accounts from src/main/ad-hoc/minimal-dev-db.sql
  sudo mysql -u mlds -p mlds < minimal-dev-db.sql
 - DO NOT do this on a public accessable machine
 - You can then use the following accounts: admin, staff, sweden, user with matching passwords





