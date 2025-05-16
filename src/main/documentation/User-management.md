# User Management

MLDS versions up to and including 6.x have a two-tiered authentication architecture, with multiple hierarchical access roles.

Anonymous visitors are presented with the MLDS Public views.

Visitors may register directly within MLDS, with account details saved in the t_user table of the MLDS database, and their email address as their login identity.

Registered user accounts may then also be approved as an Affiliate, giving them access to the MLDS Affiliate features.

An alternative authentication route uses Confluence/Crowd accounts which are associated with an existing MLDS account by having the same email address, but which use a separate username for login credentials.

If a registered user logs into MLDS using their registered email address, they will be authenticated through their MLDS database account and presented with the MLDS Affiliate views (which extend the Public views).

If a registered user logs into MLDS using their Confluence/Crowd credentials, and their account matches an MLDS user email address, they will be authorized as an MLDS Member, NRC Staff or Administrator and presented with relevant views/features.

Note that logging in with email/password (MLDS database account) credentials will only present the Affiliate / Public views, even if the site visitor has appropriate Confluence/Crowd credentials. To see the Members/Staff/Administrator features/views, visitors must authenticate using their Confluence/Crowd username/password credentials.

In increasing features/functionality order, the authentication/roles patterns are:
- Unauthenticated
  - **Public** (anonymous, not authenticated)
- Authenticated with MLDS credentials
  - **Affiliate (Pending)** (authorized access, but pending account approval as an Affiliate by an Administrator)
  - **Affiliate** (authorized access and account approved as an Affiliate by an Administrator)
- Authenticated with Confluence/Crowd credentials
  - **Member** (authorized if the account is in the MLDS Members access group, and account email matches an MLDS DB user account)
  - **Staff** (authorized if the account is in a relevant MLDS NRC Staff access group, and account email matches an MLDS DB user account)
  - **Administrator** (authorized if the account is in a relevant MLDS Admin access group, and account email matches an MLDS DB user account)

A notable aspect of Confluence/Crowd user authorization is that an MLDS user should only be placed in a single named MLDS access group. If placed in multiple groups, MLDS access may be problematic.

MLDS user accounts can be inactivated by Administrators, after which login attempts using their email addresses will fail to authenticate.
