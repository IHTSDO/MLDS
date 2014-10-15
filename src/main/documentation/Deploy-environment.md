## Environmental Dependencies
The core application requires:

- a recent Java 7 JVM 
- a Postgres database and user with ddl permissions for connecting and configuring storage. See [database docs](./database.md) for more.
- an outbound email connection (normally SMTP or ESMTP)
- the IHTSDO user management application

Optionally, the application can provide SSL termination.  This requires a keyfile and password.

The .deb packaged application uses SupervisorD for daemon services, so supervisord is a required package.

## Environmental Configuration
These external resources are configured first by [internal defaults](../resources/application.yml), then further configured in an installation via /etc/opt/mlds/config.properies.  A default [config.properties](../deb/config.properities) is included in the .deb package.

