The project is built with Maven, producing a war file.

	mvn clean package

The war file is modified during the build to embed the Tomcat web server, and turns the war into an self-contained executable jar file by the spring-boot-maven-plugin (see [pom.xml](../../../pom.xml) )

Releases are produced using the jgitflow plugin, implementing the [Gitflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) process.

	mvn jgitflow:release-start
	mvn jgitflow:release-finish
	git push

Once the application is packaged as an executable war file, we then generate a Debian installable .deb file using the jdeb plugin.  The contents and config for this are in the pom.xml xml and in the [src/main/deb](../deb) folder.

Releases are published to the [IHTSDO Maven Nexus Repository](https://maven.ihtsdotools.org) using the normal deploy processes.  If you wish to publish a snapshot version (e.g for a nightly deploy), run

	mvn clean deploy

Once published to Nexus, the project is available for installation.  There is an [Ansible playbook](https://github.com/IHTSDO/ihtsdo-ansible/blob/master/otf_mlds.yml) available.

You can install the software manually usin apt-get.  The IHTSDO Nexus server acts as an Apt repository.  To subscribe to the repository, add a file to /etc/apt/sources.list.d/ containing the single line

	deb https://maven.ihtsdotools.org/content/repositories/snapshots/ ./
	
for snapshots, or 

	deb https://maven.ihtsdotools.org/content/repositories/releases/ ./

for releases.


Then, update the Apt indexes with

	apt-get update

The install the software with

	apt-get install mlds

Note: when installing manually, you will need to configure a Postgres database.  By default, the db name is mlds, connecting via a user named mlds.

The Intelliware [Jenkins server](http://ihtsdo-hg.intware.com:8080/job/MLDS%20develop%20branch%20nightly/) publishes a nightly build, which is pulled by a nightly cron job

	0 3 * * * root ( apt-get update && apt-get install --reinstall mlds )
Note: config customization may will block automated updates when config.properties has been changed, or when the version number has changed.

The application provides default configuration values in [config.properties](../deb/config.properties).  Comments inline.

