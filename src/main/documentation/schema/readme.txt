This directory contains an abbreviated E-R diagram in a variety of formats (er.*), and a ddl dump (mlds.ddl).

Some notes:

* These represent a snapshot-in-time during active development.  They do not represent a stable API

* Tables affiliate and member are important roots.

* user, authority, user_authority, persistent_audit_event, persistent_audit_event_data and persistent_token tables come from a base framework and implement users, permissions, and event logging.  authority and user_authority will be obsolete once we finish integrating with the staff authentication service.

* Tables "databasechangelog" and "databasechangeloglock" are bookkeeping for Liquibase which manages our db changes.

* "commercial_usage" et. al. should be more properly named "usage", etc.

* "affiliate_details" is the implementation to allow applications to capture snapshots of the affiliate state at points-in-time.


To generate a new ddl dump:

    mysqldump -u mlds -p --no-data mlds > mlds.ddl
