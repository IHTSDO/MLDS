Deployment
Packaging (binary)
build
tests
- client vs server
startup and config
json serialization
persistence
client authentication - session vs token
Angular services (stateful vs stateless)
- session
Sonar
Security
- permission to make changes
- permission to see objects
- permission to see fields within objects
Multiple ways to configure jackson serialization
- dto
- module
- filter
Copying data - clone, detach, serialize
Angular router and the frame
Postgres blobs
Migrations
- deleting database
- developer migrations?
E2E tests
Lots of duplication in Angular forms
- validation
Continuous save
Audit logs
- ad-hoc history
Affiliate/application ad-hoc history
Half-backed localization
- separate messages bundles for server (email) vs client (en.json)
Pages often render before their data is in - can show some odd states.
partial implementation of OData $filters.  Ad-hoc and limited
Paging works
Full-text works
- not sure that child dirty events flow up
Client caching is a pain.  Why isn't dev build set with no caching?
Too many ways of doing things - client server
refactoring client side is frightening - lack of tests and tools
Miss Webstorm
Jhipster hot-reload never spread
Mixed strategy
MVC controllers are too wide
- Tests as often narrower
Angular: http vs resource

3rd party gaps
- angular forms
- angular data
- rest architecture - 


TODO
navigability
- client js directory structure
- rest controllers - split, sub-packages
- sonar against js?
- Sonar gardening
- prod build

