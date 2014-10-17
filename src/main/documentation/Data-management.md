# Data Management

## Database
The system uses a Postgres database.  On system startup, the application connects via the configured database/username/password parameters.  It then launches the Liquibase databsae configuration tool configured by the [master changelog](../resources/config/liquibase/master.xml).  See [DatabaseConfiguration.java](../java/ca/intelliware/ihtsdo/mlds/config/DatabaseConfiguration.java)

Liquibase runs from an empty database and:

- creates internal book-keeping tables: databasechangelog and databasechangeloglock 
- runs each <changeset> defined, in order

Liquibase runs every time the application starts, doing nothing of there are no new entries defined.

## Hibernate
The application accesses the database via the Hiberate JPA provider.  The provider uses the standard Spring Boot configuration, using a persistence unit named "default".  We use the [Jadira Usertypes](http://jadira.sourceforge.net/usertype-userguide.html) to map fields defined with [Joda Time](http://www.joda.org/joda-time/) types.  This is turned on in [persistence.xml](../resources/META-INF/persistence.xml).

## Key tables
The system has a few important roots:

- Table "member" defines IHTSDO members using the ISO 2 character country code as a key.  The table also include an "IHTSDO" pseudo-member to own the international releases, and administer non-member country affiliates
- Table "release_package" is the root for the various release versions and files.
- Table "affiliate" is the root object for all affiliate data
- "t_user"" and the other t_* tables come from a base framework and implement users, permissions, and event logging.  The t_user table has an "active" flag for disabling locally defined users.

## Data Administration - New Member Country
Most administrative activities can be accomplished via the user interface.  One exception is the (infrequent) onboarding of a new member country.  To do this:

- Insert a new entry into the "member" table, using the Iso 2 character country code for the "key" column.
- Update the "country" table, changing the "member_id" to match the new member entry.

E.g. To update the system to recognize Andora as a member:

	INSERT INTO member(
            member_id, key, created_at, licence_file)
    VALUES (nextval('hibernate_sequence'), 'AD', now(), null);
    UPDATE country
    SET member_id = (SELECT member_id from member where key = 'AD)
    WHERE iso_code_2 = 'AD';
    
Separately, the administrative interface is used to configure the member's participation in the MLDS system, use the administrative UI.  See the "National Registration List", and the "License T&C" tabs.

The system also has translations for the member name in two places.  Each must be updated:

- [The web front end translations](../../../src/main/webapp/i18n/en.json)
- [The server-side translations for email](../../../src/main/resources/mails/messages/messages_en.properties)
