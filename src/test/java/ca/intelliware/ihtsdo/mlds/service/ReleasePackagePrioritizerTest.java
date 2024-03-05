package ca.intelliware.ihtsdo.mlds.service;

import static org.hamcrest.CoreMatchers.equalTo;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReleasePackagePrioritizerTest {

	@Mock ReleasePackageRepository releasePackageRepository;
	
	private ReleasePackagePrioritizer releasePackagePrioritizer;

	private Member ihtsdo;

	private ArrayList<ReleasePackage> ihtsdoPackages;
	
	@Before
    public void setup() {
		releasePackagePrioritizer = new ReleasePackagePrioritizer();
		
		releasePackagePrioritizer.releasePackageRepository = releasePackageRepository;
		
		ihtsdo = new Member("IHTSDO", 1L);
		
		ihtsdoPackages = new ArrayList<ReleasePackage>();
		Mockito.when(releasePackageRepository.findByMemberOrderByPriorityDesc(ihtsdo)).thenReturn(ihtsdoPackages);
    }
	
	@Test
	public void shouldInitializeFirstPackage() {
		ReleasePackage releasePackage = new ReleasePackage(99L);
		releasePackage.setMember(ihtsdo);
		
		releasePackagePrioritizer.prioritize(releasePackage, null);
		
		Assert.assertThat(releasePackage.getPriority(), equalTo(1));
	}

	@Test
	public void shouldPutNewPackageWithLowestPriority() {
		ReleasePackage package1 = withIhtsdoPackage(102L, 1);
		
		ReleasePackage releasePackage = new ReleasePackage(99L);
		releasePackage.setMember(ihtsdo);
		
		releasePackagePrioritizer.prioritize(releasePackage, null);
		
		Assert.assertThat(package1.getPriority(), equalTo(2));
		Assert.assertThat(releasePackage.getPriority(), equalTo(1));
	}

	@Test
	public void shouldBeAbleToPromotePackageToTop() {
		ReleasePackage package3 = withIhtsdoPackage(100L, 3);
		ReleasePackage package2 = withIhtsdoPackage(101L, 2);
		ReleasePackage package1 = withIhtsdoPackage(102L, 1);
		
		releasePackagePrioritizer.prioritize(package1, 3);
		
		Assert.assertThat(package1.getPriority(), equalTo(3));
		Assert.assertThat(package3.getPriority(), equalTo(2));
		Assert.assertThat(package2.getPriority(), equalTo(1));
	}

	@Test
	public void shouldContrainPromoteToTopValue() {
		ReleasePackage package3 = withIhtsdoPackage(100L, 3);
		ReleasePackage package2 = withIhtsdoPackage(101L, 2);
		ReleasePackage package1 = withIhtsdoPackage(102L, 1);
		
		releasePackagePrioritizer.prioritize(package1, 99);
		
		Assert.assertThat(package1.getPriority(), equalTo(3));
		Assert.assertThat(package3.getPriority(), equalTo(2));
		Assert.assertThat(package2.getPriority(), equalTo(1));
	}

	@Test
	public void shouldBeAbleToDemote() {
		ReleasePackage package3 = withIhtsdoPackage(100L, 3);
		ReleasePackage package2 = withIhtsdoPackage(101L, 2);
		ReleasePackage package1 = withIhtsdoPackage(102L, 1);
		
		releasePackagePrioritizer.prioritize(package3, 1);
		
		Assert.assertThat(package2.getPriority(), equalTo(3));
		Assert.assertThat(package3.getPriority(), equalTo(2));
		Assert.assertThat(package1.getPriority(), equalTo(1));
	}

	@Test
	public void shouldBeAbleToDemoteToBottom() {
		ReleasePackage package3 = withIhtsdoPackage(100L, 3);
		ReleasePackage package2 = withIhtsdoPackage(101L, 2);
		ReleasePackage package1 = withIhtsdoPackage(102L, 1);
		
		releasePackagePrioritizer.prioritize(package3, 0);
		
		Assert.assertThat(package2.getPriority(), equalTo(3));
		Assert.assertThat(package1.getPriority(), equalTo(2));
		Assert.assertThat(package3.getPriority(), equalTo(1));
	}

	private ReleasePackage withIhtsdoPackage(long id, Integer priority) {
		ReleasePackage releasePackage = new ReleasePackage(id);
		releasePackage.setMember(ihtsdo);
		releasePackage.setPriority(priority);
		ihtsdoPackages.add(releasePackage);
		return releasePackage;
	}

}


