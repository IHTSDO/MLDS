package ca.intelliware.ihtsdo.mlds.web.rest;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ca.intelliware.ihtsdo.mlds.domain.File;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.BlobHelper;
import ca.intelliware.ihtsdo.mlds.repository.FileRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

@RestController
public class MemberResource {
    private final Logger log = LoggerFactory.getLogger(MemberResource.class);
    
	@Resource MemberRepository memberRepository;
	@Resource FileRepository fileRepository;
	@Resource SessionService sessionService;
	@Resource BlobHelper blobHelper;
	
    @RequestMapping(value = Routes.MEMBERS,
            method = RequestMethod.GET,
            produces = "application/json")
    @PermitAll
    public List<Member> getMembers() {
    	return memberRepository.findAll();
    }
    
    @RequestMapping(value = Routes.MEMBER_LICENSE,
            method = RequestMethod.GET)
    @PermitAll
    @Transactional
    public ResponseEntity<org.springframework.core.io.Resource> getMemberLicense(@PathVariable String memberKey) throws SQLException, IOException {
    	HttpHeaders httpHeaders = new HttpHeaders();
    	File license = memberRepository.findOneByKey(memberKey).getLicense();
    	httpHeaders.setContentType(MediaType.valueOf(license.getMimetype()));
    	httpHeaders.setContentLength(license.getContent().length());
    	httpHeaders.setContentDispositionFormData("file", license.getFilename());
    	
    	byte[] byteArray = IOUtils.toByteArray(license.getContent().getBinaryStream());
    	org.springframework.core.io.Resource contents = new ByteArrayResource(byteArray);
		return new ResponseEntity<org.springframework.core.io.Resource>(contents, httpHeaders, HttpStatus.OK);
    }
    
    @RequestMapping(value = Routes.MEMBER_LICENSE,
            method = RequestMethod.POST,
    		headers = "content-type=multipart/*",
            produces = "application/json")
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	@Transactional
    public ResponseEntity<?> updateMemberLicense(@PathVariable String memberKey, @RequestParam("file") MultipartFile multipartFile) throws IOException {
		Member member = memberRepository.findOneByKey(memberKey);

		File licenseFile = (member.getLicense() != null) ? member.getLicense(): new File();

		if (!multipartFile.isEmpty() && multipartFile != null) {
			Blob blob = blobHelper.createBlobFrom(multipartFile);
			licenseFile.setContent(blob);
			licenseFile.setCreator(sessionService.getUsernameOrNull());
			licenseFile.setFilename(multipartFile.getOriginalFilename());
			licenseFile.setMimetype(multipartFile.getContentType());
			licenseFile.setLastUpdated(Instant.now());
		}

		fileRepository.save(licenseFile);
		member.setLicense(licenseFile);
		memberRepository.save(member);

		return new ResponseEntity<Member>(member, HttpStatus.OK);
	}

}
