#[IHTSDO](http://www.ihtsdo.org "IHTSDO") Development Environment Setup

### Create project directory
- /projects/IHTSDO

### Install Java 25
- Install JDK 25 and set `JAVA_HOME`.

### Install IDE
- Install IntelliJ IDEA, Eclipse IDE, or VS Code (with Java Extension Pack).

#### Check the project out of GIT
- Clone the repository: `git clone git@github.com:IHTSDO/MLDS.git`
- Import as an Existing Maven Project into your IDE.

### Change below values in application.properties in the application
- Replace values for database:
  - **`spring.datasource.url=jdbc:mysql://localhost:3306/mlds`**
  - **`spring.datasource.username=mlds`**
  - **`spring.datasource.password=(Replace with Database Password)`**

### Install Database
- Install MySQL (https://dev.mysql.com/downloads)
- Create `mlds` user with password `password`.
- Create `mlds` schema, owned by `mlds` user.

### Before Running the application
- To run the code locally, set the region for S3 Configuration.
- For Windows (PowerShell):
  ```powershell
  setx AWS_REGION us-east-1
  ```
- For Linux / macOS:
  ```bash
  export AWS_REGION=us-east-1
  ```

### Running the application
- Run the `Application` class at the root of the Java package hierarchy directly (`ca.intelliware.ihtsdo.mlds.Application`), or run `mvn spring-boot:run`.
- The REST API will be accessible at: `http://localhost:8080`

### Populating developer database
- The first run of `Application` will build the schema using Liquibase.
- On a private developer box you may load minimal test accounts:
  ```bash
  sudo mysql -u mlds -p mlds < src/main/ad-hoc/minimal-dev-db.sql
  ```
  *(Do NOT run this on a public or production machine).*
- You can then use the following accounts: `admin`, `staff`, `sweden`, `user` with matching passwords.
