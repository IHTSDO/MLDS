package ca.intelliware.ihtsdo.mlds.web.rest;

import javax.annotation.security.RolesAllowed;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AnnouncementDTO;

import com.codahale.metrics.annotation.Timed;

@RestController
public class AnnouncementResource {

	@RequestMapping(value = Routes.RELEASE_FILE,
    		method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@Timed
    public @ResponseBody ResponseEntity<AnnouncementDTO> postAnnouncement(@RequestBody AnnouncementDTO body) {
    	
		//FIXME security check can send announcement to specified member
		
		//FIXME send email to all member users
		
    	//FIXME log announcement - against user?
		
    	return new ResponseEntity<AnnouncementDTO>(body, HttpStatus.OK);
    }

}
