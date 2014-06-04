Assuming:

- Java 7
- Ubuntu
- Tomcat 7
- Postgres 9.1
- Angular 1.2.16

- -K or ansible_sudo_password or ansible hangs on sudo password prompt!!!


repositories
Nexus as deb

try to get 
Ansible galaxy
[14-05-29 10:20:15 AM] Michael Buckley: Please add me as a contact. Michael Buckley[14-05-29 10:20:30 AM] Matt Willsher: Matt Willsher has shared contact details with Michael Buckley.[14-05-29 10:20:38 AM] Matt Willsher: Hi Michael[14-05-29 10:20:41 AM] Michael Buckley: Hi Matt[14-05-29 10:21:07 AM] Michael Buckley: I'm new to Ansible, so I'm hoping to find an example to start from.[14-05-29 10:21:28 AM] Michael Buckley: Can we do this by audio?[14-05-29 10:22:03 AM] Matt Willsher: sure[14-05-29 10:22:12 AM] Matt Willsher: Call started, 37 minutes 16 seconds[14-05-29 10:41:33 AM] Matt Willsher: ihtsdo-ansible/[14-05-29 10:41:40 AM] Matt Willsher:  - files[14-05-29 10:41:44 AM] Matt Willsher:   - inventory[14-05-29 10:42:46 AM] Matt Willsher:  inventory/
  - group_vars/
  - host_vars/
  development.ini[14-05-29 10:42:58 AM] Matt Willsher: [snomed_release_service_api]
uat-release.ihtsdotools.org snomed_release_service_api_version=latest ihtsdo_repository=snapshot

[snomed_release_service_web]
uat-release.ihtsdotools.org snomed_release_service_web_version=latest ihtsdo_repository=snapshot

[snomed_release_service_builder]
uat-release.ihtsdotools.org snomed_release_service_builder_version=latest ihtsdo_repository=snapshot[14-05-29 10:44:17 AM] Matt Willsher: group_vars/snomed_release_service_web[14-05-29 10:44:18 AM] Matt Willsher: ---
snomed_release_service_web_nginx_servername: "{{ inventory_hostname }}"[14-05-29 10:45:56 AM] Matt Willsher: hosts_vars/uat-release.ihtsdotool.org[14-05-29 10:46:10 AM] Matt Willsher: ---
snomed_release_service_api_aws_privateKey: poiewfjowiefjwe[14-05-29 10:46:15 AM] Matt Willsher: snomed_release_service_api_aws_key: foiwjefjwef[14-05-29 10:51:50 AM] Matt Willsher: https://galaxy.ansible.com/[14-05-29 10:56:06 AM] Matt Willsher: https://github.com/IHTSDO/snomed-release-service/tree/master/api[14-05-29 10:59:28 AM] Michael Buckley: Call ended, 37 minutes 16 seconds[14-05-29 10:59:29 AM] Matt Willsher:       Matt Willsher    2236        Michael Buckley    2236  