package ca.intelliware.ihtsdo.mlds.registration;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:applicationContext.xml", 
		"classpath:applicationContext-integrationTest.xml"})
public class UserRegistrationIntegrationTest {
	@Resource
	UserRegistrationService controller;
	
	@Resource
	UserRegistrationRepository userRegistrationRepository;

	@Test
	public void createRegistrationViaAPIAndFindIt() throws Exception {
		String email = "email" + System.currentTimeMillis() + "@example.com";
		controller.createRegistration(email);
		
		UserRegistration found = userRegistrationRepository.findByEmail(email);
		Assert.assertNotNull("found some", found);
		
	}
}
