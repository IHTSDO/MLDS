package ca.intelliware.ihtsdo.mlds.service;


import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import com.google.common.base.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class ReleasePackagePrioritizer {

	public static final int END_PRIORITY = 0;

	@Autowired ReleasePackageRepository releasePackageRepository;

	public void prioritize(ReleasePackage releasePackage, Integer priority) {
		if (priority != null && Objects.equal(releasePackage.getPriority(), priority)) {
			// no change - so do nothing - under the assumption that existing priority is valid
		} else {
			setPriorityTo(releasePackage, priority);
		}
	}

	private void setPriorityTo(ReleasePackage releasePackage, Integer priority) {
		List<ReleasePackage> originals = releasePackageRepository.findByMemberOrderByPriorityDesc(releasePackage.getMember());
		List<ReleasePackage> existing = new ArrayList<ReleasePackage>(originals);
		boolean removed = existing.remove(releasePackage);
		int matchedIndex = existing.size();
		if (removed) {
			for (int i = 0; i < existing.size(); i++) {
				ReleasePackage existingPackage = existing.get(i);
				if (existingPackage.getPriority() == null) {
					matchedIndex = i;
					break;
				}
				if (existingPackage.getPriority() != null && priority != null && priority >= existingPackage.getPriority()) {
					matchedIndex = i;
					break;
				}
			}
		}
		existing.add(matchedIndex, releasePackage);

		updateAllPriorities(existing);
	}

	private void updateAllPriorities(List<ReleasePackage> existing) {
		for (int i = 0; i < existing.size(); i++) {
			ReleasePackage existingPackage = existing.get(i);
			int newPriority = existing.size() - i;
			if (! Objects.equal(existingPackage.getPriority(), newPriority)) {
				existingPackage.setPriority(newPriority);
				releasePackageRepository.save(existingPackage);
			}
		}
	}
}
