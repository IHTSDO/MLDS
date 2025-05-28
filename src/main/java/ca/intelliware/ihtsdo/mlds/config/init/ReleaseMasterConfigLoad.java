package ca.intelliware.ihtsdo.mlds.config.init;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePackageConfig;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageConfigRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReleaseMasterConfigLoad {

    private final ReleasePackageConfigRepository repository;

    public ReleaseMasterConfigLoad(ReleasePackageConfigRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void loadDefaultsIfEmpty() {
        if (repository.count() == 0) {
            repository.saveAll(List.of(
                create("ONLINE"),
                create("ALPHA/BETA"),
                create("OFFLINE"),
                create("ALL")
            ));
        }
    }

    private ReleasePackageConfig create(String releaseType) {
        ReleasePackageConfig config = new ReleasePackageConfig();
        config.setReleaseType(releaseType);
        config.setReleasePackageAccess("ALL");
        config.setReleasePermissionType("NOT_SELECTED");
        config.setUserList("[]");
        config.setActive(false);
        return config;
    }
}
