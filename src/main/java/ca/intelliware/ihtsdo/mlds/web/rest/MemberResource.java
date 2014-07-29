package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;

@RestController
public class MemberResource {
    private final Logger log = LoggerFactory.getLogger(MemberResource.class);
    
	@Resource
	MemberRepository memberRepository;
	
    @RequestMapping(value = Routes.MEMBERS,
            method = RequestMethod.GET,
            produces = "application/json")
    @PermitAll
    public List<Member> getMembers() {
    	return memberRepository.findAll();
    }
}
