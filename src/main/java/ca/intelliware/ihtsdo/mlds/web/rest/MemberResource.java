package ca.intelliware.ihtsdo.mlds.web.rest;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;

import ca.intelliware.ihtsdo.mlds.domain.File;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.BlobHelper;
import ca.intelliware.ihtsdo.mlds.repository.FileRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.web.SessionService;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.MemberDTO;

@RestController
public class MemberResource {
    private final Logger log = LoggerFactory.getLogger(MemberResource.class);
    
	@Resource MemberRepository memberRepository;
	@Resource FileRepository fileRepository;
	@Resource SessionService sessionService;
	@Resource BlobHelper blobHelper;
	@Resource EntityManager entityManager;
	
    @RequestMapping(value = Routes.MEMBERS,
            method = RequestMethod.GET,
            produces = "application/json")
    @PermitAll
    @Transactional
    @Timed
    public List<MemberDTO> getMembers() {
    	List<MemberDTO> memberDTOs = new ArrayList<MemberDTO>();
    	
    	for (Member member : memberRepository.findAll()) {
			memberDTOs.add(new MemberDTO(member));
		}
    	return memberDTOs;
    }
    
    @RequestMapping(value = Routes.MEMBER_LICENCE,
            method = RequestMethod.GET)
    @PermitAll
    @Transactional
    @Timed
    public ResponseEntity<?> getMemberLicense(@PathVariable String memberKey) throws SQLException, IOException {
    	HttpHeaders httpHeaders = new HttpHeaders();
    	File license = memberRepository.findOneByKey(memberKey).getLicense();
    	
    	if(license == null) {
    		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	}
    	
    	httpHeaders.setContentType(MediaType.valueOf(license.getMimetype()));
    	httpHeaders.setContentLength(license.getContent().length());
    	httpHeaders.setContentDispositionFormData("file", license.getFilename());
    	
    	byte[] byteArray = IOUtils.toByteArray(license.getContent().getBinaryStream());
    	org.springframework.core.io.Resource contents = new ByteArrayResource(byteArray);
		return new ResponseEntity<org.springframework.core.io.Resource>(contents, httpHeaders, HttpStatus.OK);
    }
    
    @RequestMapping(value = Routes.MEMBER_LICENCE,
            method = RequestMethod.POST,
    		headers = "content-type=multipart/*",
            produces = "application/json")
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	@Transactional
	@Timed
    public ResponseEntity<?> updateMemberLicense(@PathVariable String memberKey, @RequestParam(value="file", required = false) MultipartFile multipartFile, @RequestParam("licenceName") String licenceName, @RequestParam("licenceVersion") String licenceVersion) throws IOException {
		Member member = memberRepository.findOneByKey(memberKey);

		if (multipartFile != null && !multipartFile.isEmpty()) {
			updateLicenceFile(multipartFile, member);
		}

		member.setLicenceName(licenceName);
		member.setLicenceVersion(licenceVersion);
		
		memberRepository.save(member);

		return new ResponseEntity<MemberDTO>(new MemberDTO(member), HttpStatus.OK);
	}

	private void updateLicenceFile(MultipartFile multipartFile, Member member) throws IOException {
		File licenseFile = new File();

		if (member.getLicense() != null) {
			entityManager.detach(member.getLicense());
		}

		Blob blob = blobHelper.createBlobFrom(multipartFile);
		licenseFile.setContent(blob);
		licenseFile.setCreator(sessionService.getUsernameOrNull());
		licenseFile.setFilename(multipartFile.getOriginalFilename());
		licenseFile.setMimetype(multipartFile.getContentType());
		licenseFile.setLastUpdated(Instant.now());

		fileRepository.save(licenseFile);
		member.setLicense(licenseFile);
	}

}
