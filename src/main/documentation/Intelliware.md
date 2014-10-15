# Intelliware environment
In addition to developer workstations, the Intelliware environment has a build server, and two deployed VMs.

The build server resides at http://ihtsdo-hg.intware.com:8080 and provides:

- a [developer build](http://ihtsdo-hg.intware.com:8080/job/MLDS%20develops%20branch/) which polls git for changes,
- and a scheduled [nightly build](http://ihtsdo-hg.intware.com:8080/job/MLDS%20develop%20branch%20nightly/) which builds and publishes a SNAPSHOT build to the Nexus repository.

The system is deployed to ihtsdo-dev.intware.com and ihtsdo-demo.intware.com.  

The ihtsdo-dev server has a cron fragment 

	0 3 * * * root ( apt-get update && apt-get install --reinstall mlds )
That runs after the Jenkins nightly and updates to the most recent published snapshot.
Note: config customization may will block automated updates when config.properties has been changed, or when the version number has changed.

The ihtsdo-demo server is manually updated after full releases via a similar commandline. (See [Build-deploy.md](Build-deploy.md))

