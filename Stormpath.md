#Stormpath integration

Stormpath is the online directory service that we use to manage users and authentication.  Populations of users are stored in objects called a "Directory", and an "Application" is associated with one or more Directories (or subgroups there-of) to provide user login, etc. services.

Required Jackson upgrade to 1.9

#### Questions
How many Tenants to create?  1 for prod, 1 for dev.  How many more?

There are three user populations -- Staff, members, and licensing users.  Recommend licensing users are a separate store.  Cloud only?

Does IHTSDO have an LDAP server alread?  e.g. Exchange?

Does IHTSDO want to manage members via Exchange?

Recommend Staff directory, Member directory, and Affiliate user directory.  If members and staff are managed together, then we can merge those two directories.  This way staff and members can be shared between apps.  Assumes that Affiliates aren't as shared.

### Stormpath dev account
https://api.stormpath.com/ui/dashboard
michael.buckley@intelliware.com / [iwdpass]SP1
Tenant name: arid-fury

***REMOVED***

