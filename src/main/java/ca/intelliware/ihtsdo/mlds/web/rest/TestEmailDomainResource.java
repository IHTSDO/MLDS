package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.domain.TestEmailDomain;
import ca.intelliware.ihtsdo.mlds.repository.TestEmailDomainRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import com.codahale.metrics.annotation.Timed;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TestEmailDomainResource {

    @Value("${test.email.max.count}")
    private int maxCount;


    private TestEmailDomainRepository testEmailDomainRepository;

    public TestEmailDomainResource(TestEmailDomainRepository testEmailDomainRepository) {
        this.testEmailDomainRepository = testEmailDomainRepository;
    }

    // CREATE

    @PostMapping(
        value = Routes.TEST_EMAIL_DOMAINS,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public ResponseEntity<TestEmailDomain> createDomain(
        @RequestBody TestEmailDomain request) {

        TestEmailDomain saved =
            testEmailDomainRepository.save(request);

        return ResponseEntity.ok(saved);
    }

    // GET ALL

    @GetMapping(
        value =Routes.TEST_EMAIL_DOMAINS,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public ResponseEntity<List<TestEmailDomain>> getAllDomains() {

        return ResponseEntity.ok(
            testEmailDomainRepository.findAll()
        );
    }

    // GET BY ID

    @GetMapping(
        value = Routes.GET_TEST_EMAIL_DOMAIN_BY_ID,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public ResponseEntity<TestEmailDomain> getDomainById(
        @PathVariable Long id) {

        return testEmailDomainRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE

    @PutMapping(
        value = Routes.GET_TEST_EMAIL_DOMAIN_BY_ID,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public ResponseEntity<TestEmailDomain> updateDomain(
        @PathVariable Long id,
        @RequestBody TestEmailDomain request) {

        return testEmailDomainRepository.findById(id)
            .map(existing -> {

                existing.setDomainName(
                    request.getDomainName()
                );

                TestEmailDomain updated =
                    testEmailDomainRepository.save(existing);

                return ResponseEntity.ok(updated);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // DELETE

    @DeleteMapping(
        value = Routes.GET_TEST_EMAIL_DOMAIN_BY_ID,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public ResponseEntity<Void> deleteDomain(
        @PathVariable Long id) {

        if (!testEmailDomainRepository.existsById(id)) {

            return ResponseEntity.notFound().build();
        }

        testEmailDomainRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }
    @GetMapping(value = Routes.GET_COUNT)
    @RolesAllowed({
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    public ResponseEntity<Integer> getMaxTestEmailCount() {

        return ResponseEntity.ok(maxCount);
    }
}
