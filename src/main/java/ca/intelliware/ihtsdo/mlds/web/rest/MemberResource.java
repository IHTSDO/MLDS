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
import javax.servlet.http.HttpServletRequest;
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

import ca.intelliware.ihtsdo.mlds.domain.File;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.BlobHelper;
import ca.intelliware.ihtsdo.mlds.repository.FileRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.web.SessionService;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.MemberDTO;

import com.codahale.metrics.annotation.Timed;

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
    
    @RequestMapping(value = Routes.MEMBER_LICENSE,
            method = RequestMethod.GET)
    @PermitAll
    @Transactional
    @Timed
    public ResponseEntity<?> getMemberLicense(@PathVariable String memberKey, HttpServletRequest request) throws SQLException, IOException {
    	File license = memberRepository.findOneByKey(memberKey).getLicense();
    	
    	return downloadFile(request, license);
    }

	private ResponseEntity<?> downloadFile(HttpServletRequest request, File file) throws SQLException, IOException {
		if (file == null) {
    		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	} else if (file.getLastUpdated() != null) {
    		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
    		long lastUpdatedSecondsFloor = file.getLastUpdated().getMillis() / 1000 * 1000;
			if (ifModifiedSince != -1 && lastUpdatedSecondsFloor <= ifModifiedSince) {
				return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    		}
    	}
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.valueOf(file.getMimetype()));
		httpHeaders.setContentLength(file.getContent().length());
		httpHeaders.setContentDispositionFormData("file", file.getFilename());
		if (file.getLastUpdated() != null) {
			httpHeaders.setLastModified(file.getLastUpdated().getMillis());
		}
    	
    	byte[] byteArray = IOUtils.toByteArray(file.getContent().getBinaryStream());
    	org.springframework.core.io.Resource contents = new ByteArrayResource(byteArray);
		return new ResponseEntity<org.springframework.core.io.Resource>(contents, httpHeaders, HttpStatus.OK);
	}
    
    @RequestMapping(value = Routes.MEMBER_LICENSE,
            method = RequestMethod.POST,
    		headers = "content-type=multipart/*",
            produces = "application/json")
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	@Transactional
	@Timed
    public ResponseEntity<?> updateMemberLicense(@PathVariable String memberKey, @RequestParam(value="file", required = false) MultipartFile multipartFile, @RequestParam("licenseName") String licenseName, @RequestParam("licenseVersion") String licenseVersion) throws IOException {
		Member member = memberRepository.findOneByKey(memberKey);

		if (multipartFile != null && !multipartFile.isEmpty()) {
			File licenseFile = updateFile(multipartFile, member.getLicense());
			member.setLicense(licenseFile);
		}

		member.setLicenseName(licenseName);
		member.setLicenseVersion(licenseVersion);
		
		memberRepository.save(member);

		return new ResponseEntity<MemberDTO>(new MemberDTO(member), HttpStatus.OK);
	}

    @RequestMapping(value = Routes.MEMBER_LOGO,
            method = RequestMethod.GET)
    @PermitAll
    @Transactional
    @Timed
    public ResponseEntity<?> getMemberLogo(@PathVariable String memberKey, HttpServletRequest request) throws SQLException, IOException {
    	File logo = memberRepository.findOneByKey(memberKey).getLogo();
    	
    	return downloadFile(request, logo);
    }

    @RequestMapping(value = Routes.MEMBER_BRAND,
            method = RequestMethod.POST,
    		headers = "content-type=multipart/*",
            produces = "application/json")
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	@Transactional
	@Timed
    public ResponseEntity<?> updateMemberBrand(@PathVariable String memberKey, @RequestParam(value="file", required = false) MultipartFile multipartFile, @RequestParam("name") String name) throws IOException {
		Member member = memberRepository.findOneByKey(memberKey);

		if (multipartFile != null && !multipartFile.isEmpty()) {
			File licenseFile = updateFile(multipartFile, member.getLogo());
			member.setLogo(licenseFile);
		}

		member.setName(name);
		
		memberRepository.save(member);

		return new ResponseEntity<MemberDTO>(new MemberDTO(member), HttpStatus.OK);
	}

	private File updateFile(MultipartFile multipartFile, File existingFile) throws IOException {
		File newFile = new File();

		if (existingFile != null) {
			entityManager.detach(existingFile);
		}

		Blob blob = blobHelper.createBlobFrom(multipartFile);
		newFile.setContent(blob);
		newFile.setCreator(sessionService.getUsernameOrNull());
		newFile.setFilename(multipartFile.getOriginalFilename());
		newFile.setMimetype(multipartFile.getContentType());
		newFile.setLastUpdated(Instant.now());

		fileRepository.save(newFile);
		return newFile;
	}

}
