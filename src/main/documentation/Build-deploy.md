The project is built with Maven, producing a war file.

	mvn clean package
	
##Build dependencies:
To run Maven, we need:

- a recent JDK (>=17)
- Maven
- NodeJS: apt-get install nodejs (or nodejs-legacy, depending on os version).  Make sure that "node --version" returns something > 0.10.10
- ruby: apt-get install ruby
- compass: apt-get install ruby-compass
- bower: npm install -g bower
- grunt: npm install -g grunt-cli

Note:  It requires at least JDK 17 and you think you have that already, then check what's actually being used 
by typing 
	mvn -version
If maven is using the wrong version of java, then you might want to set the JAVA_HOME environmental variable in your ~/.bash_profile file.

The war file is modified during the build by the spring-boot-maven-plugin to embed the Tomcat web server.  This turns the war into an self-contained executable jar file  (see [pom.xml](../../../pom.xml) )

Releases are produced using the jgitflow plugin, implementing the [Gitflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) process.

	mvn jgitflow:release-start
	mvn jgitflow:release-finish
	git push

Note that the release-finish step takes a while to upload.

Once the application is packaged as an executable war file, we then generate a Debian installable .deb file using the jdeb plugin.  The contents and config for this are in the pom.xml xml and in the [src/main/deb](../deb) folder.

Releases are published to the [IHTSDO Maven Nexus Repository](https://maven.ihtsdotools.org) using the normal deploy processes.  If you wish to publish a snapshot version (e.g for a nightly deploy), run

	mvn clean deploy

Note: Publishing builds requires credentials in settings.xml for the Nexus server.  E.g.

	<settings>
		<servers>
			<server>
				<id>ihtsdo-public-nexus</id>
				<username>buckleym</username>
				<password>******</password>
			</server>
		</servers>
	</settings>

Once published to Nexus, the project is available for installation.  There is an [Ansible playbook](https://github.com/IHTSDO/ihtsdo-ansible/blob/master/otf_mlds.yml) available.

You can also install the software manually using apt-get.  The IHTSDO Nexus server acts as an Apt repository.  To subscribe to the repository, add a file to /etc/apt/sources.list.d/ containing the single line

	deb https://maven.ihtsdotools.org/content/repositories/snapshots/ ./
	
for snapshots, or 

	deb https://maven.ihtsdotools.org/content/repositories/releases/ ./

for releases.


Then, update the Apt indexes with

	apt-get update

The install the software with

	apt-get install mlds

Note: when installing manually, you will need to configure a MySQL database.  By default, the db name is mlds, connecting via a user named mlds.

The application provides default configuration values in [config.properties](../deb/config.properties).  Comments inline.

