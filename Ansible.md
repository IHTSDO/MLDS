Assuming:

- Java 7
- Ubuntu
- Tomcat 7
- Postgres 9.1
- Angular 1.2.16

- -K or ansible_sudo_password or ansible hangs on sudo password prompt!!!

iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
iptables -t nat -A PREROUTING -p tcp --dport 443 -j REDIRECT --to-port 8443
Update server.xml to identify redirect ports

repositories
Nexus as deb

try to get 
[14-05-29 10:42:58 AM] Matt Willsher: [snomed_release_service_api]
uat-release.ihtsdotools.org snomed_release_service_api_version=latest ihtsdo_repository=snapshot

[snomed_release_service_web]
uat-release.ihtsdotools.org snomed_release_service_web_version=latest ihtsdo_repository=snapshot

[snomed_release_service_builder]
uat-release.ihtsdotools.org snomed_release_service_builder_version=latest ihtsdo_repository=snapshot[14-05-29 10:44:17 AM] Matt Willsher: group_vars/snomed_release_service_web[14-05-29 10:44:18 AM] Matt Willsher: ---
snomed_release_service_web_nginx_servername: "{{ inventory_hostname }}"[14-05-29 10:45:56 AM] Matt Willsher: hosts_vars/uat-release.ihtsdotool.org[14-05-29 10:46:10 AM] Matt Willsher: ---
snomed_release_service_api_aws_privateKey: poiewfjowiefjwe[14-05-29 10:46:15 AM] Matt Willsher: snomed_release_service_api_aws_key: foiwjefjwef[14-05-29 10:51:50 AM] Matt Willsher: https://galaxy.ansible.com/[14-05-29 10:56:06 AM] Matt Willsher: https://github.com/IHTSDO/snomed-release-service/tree/master/api


Added apt repository to /etc/apt/
> cat /etc/apt/sources.list.d/ihtsdo.snapshot.list 
deb https://maven.ihtsdotools.org/content/repositories/snapshots/ ./

Added gpg key:
> wget https://maven.ihtsdotools.org/service/local/repositories/releases/content/apt-key.gpg.key
> apt-key add apt-key.gpg.key 

Installed the package via apt
> sudo apt-get update
> sudo apt-get install mlds

Jenkins job runs 'mvm clean deploy' at midnight.  It takes ~1hr to upload to Nexus.

Added cron job -- see /mlds/src/main/ansible/mlds_nightly_update.cron
> cat /etc/cron.d/mlds_nightly_update.cron 
#/etc/cron.d/mlds_nightly_update.cron
# Do a 3am apt update of the app
0 3 * * * root ( apt-get update && apt-get upgrade mlds )
