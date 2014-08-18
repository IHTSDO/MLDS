package ca.intelliware.ihtsdo.mlds.web.rest;

import java.sql.Blob;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.LicenseFile;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.LicenseFileRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
public class MemberResource {
    private final Logger log = LoggerFactory.getLogger(MemberResource.class);
    
	@Resource MemberRepository memberRepository;
	@Resource LicenseFileRepository licenseFileRepository;
	@Resource SessionService sessionService;
	
    @RequestMapping(value = Routes.MEMBERS,
            method = RequestMethod.GET,
            produces = "application/json")
    @PermitAll
    public List<Member> getMembers() {
    	return memberRepository.findAll();
    }
    
    @RequestMapping(value = Routes.MEMBER_LICENSE,
            method = RequestMethod.GET,
            produces = "application/json")
    @PermitAll
    public LicenseFile getMemberLicense(@PathVariable String memberKey) {
    	return memberRepository.findOneByKey(memberKey).getLicense();
    }
    
    @RequestMapping(value = Routes.MEMBER_LICENSE,
            method = RequestMethod.PUT,
            produces = "application/json")
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	@Transactional
    public ResponseEntity<?> updateMemberLicense(@PathVariable String memberKey, @RequestBody ObjectNode requestBody) {
    	Member member = memberRepository.findOneByKey(memberKey);
    	
		updateMemberLicense(member, requestBody);
    	
    	return new ResponseEntity<Member>(member, HttpStatus.OK);
    }

	private void updateMemberLicense(Member member, ObjectNode requestBody) {
		File file = (member.getLicense() != null) ? member.getLicense() : new File();
		file.setCreator(sessionService.getUsernameOrNull());
		file.setLastUpdated(Instant.now());
		//file.setBlob((Blob) requestBody.get("blob"));

		fileRepository.save(file);
		member.setLicense(file);
		memberRepository.save(member);
	}
    
}
