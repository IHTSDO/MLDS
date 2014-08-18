package ca.intelliware.ihtsdo.mlds.web.rest;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.repository.FileRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

public class MemberResourceTest {

	@Mock private MemberRepository memberRepository;
	@Mock private FileRepository fileRepository;
	@Mock private SessionService sessionService;
	
	private MockMvc restUserMockMvc;
	
	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MemberResource memberResource = new MemberResource();
        
        memberResource.memberRepository = memberRepository;
        memberResource.fileRepository = fileRepository;
        memberResource.sessionService = sessionService;
        
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(memberResource).build();
	}
}
