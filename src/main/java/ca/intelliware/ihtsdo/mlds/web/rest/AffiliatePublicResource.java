package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AffiliateCheckDTO;
import com.codahale.metrics.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class AffiliatePublicResource {

    private static final int ZERO_ONE_MANY_RESULTS_LENGTH = 3;
    private static final int MINIMUM_MATCH_LENGTH = 3;


    private final AffiliateRepository affiliateRepository;


    private final MemberRepository memberRepository;

    public AffiliatePublicResource(AffiliateRepository affiliateRepository, MemberRepository memberRepository) {
        this.affiliateRepository = affiliateRepository;
        this.memberRepository = memberRepository;
    }

    @Operation(
        summary = "Check that an affiliate is in good standing with IHTSDO.",
        description = """
            Where possible the affiliateId with confirming matching data from
            the application can be used, otherwise a single match from all
            affiliates of a member can be used.

            The check API requires a unique match to be found from the database
            of Affiliates. Affiliates are filtered by the member, optionally by
            affiliate id, and a keyword text match against the identifying
            fields of the affiliate.

            Once a single affiliate has been identified the affiliate's
            application must have been approved for the specified member and
            for the affiliate's account to be considered in good standing.

            If successful then the API will return with a matched: true.

            If there is no match then the API will return with matched: false.

            No additional information is provided to diagnose a failed match.
            """
    )
    @ApiResponse(
        responseCode = "200",
        description = "Affiliate check completed successfully.",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = AffiliateCheckDTO.class),
            examples = {
                @ExampleObject(
                    name = "Successful match",
                    value = """
                    {
                      "matched": true
                    }
                    """
                ),
                @ExampleObject(
                    name = "Unsuccessful match",
                    value = """
                    {
                      "matched": false
                    }
                    """
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Client error. The request parameters are invalid.",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = AffiliateCheckDTO.class),
            examples = @ExampleObject(
                name = "Invalid member",
                value = """
                {
                  "error": "Bad Request",
                  "status": 400,
                  "message": "Unknown member: 'xz'. Example options: AU BE BN CA CL CZ DK EE ES GB HK IHTSDO IL IN IS LT MT MY NL NZ PL PT SE SG SI SK US UY"
                }
                """
            )
        )
    )
    @GetMapping(
        value = Routes.AFFILIATES_CHECK,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Timed
    @RolesAllowed({ AuthoritiesConstants.ADMIN })
    public ResponseEntity<AffiliateCheckDTO> getAffiliates(

        @Parameter(
            description = """
            Identification of the member country that the affiliate has
            been accepted by. It is either a two-letter country code or
            IHTSDO to indicate IHTSDO international.

            The country codes are a subset of the ISO 3166-1 alpha-2 codes.

            Example options:
            AU BE BN CA CL CZ DK EE ES GB HK IHTSDO IL IN IS LT MT MY
            NL NZ PL PT SE SG SI SK US UY.
            """,
            required = true,
            example = "SE"
        )
        @RequestParam(
            value = "member",
            defaultValue = ""
        )
        String memberKey,

        @Parameter(
            description = "Limit the search to a specific Affiliate record.",
            required = false,
            example = "123"
        )
        @RequestParam(
            value = "affiliateId",
            defaultValue = "",
            required = false
        )
        String affiliateId,

        @Parameter(
            description = """
            Search keyword within the affiliate record. This will match
            against a number of identifying fields of the affiliate:
            organization name, first name, last name, street address,
            email, alternative email, and third email.
            """,
            required = true,
            example = "abc@test.com"
        )
        @RequestParam(
            value = "match",
            defaultValue = ""
        )
        String match) {

        AffiliateCheckDTO response = new AffiliateCheckDTO();

        if (StringUtils.isBlank(memberKey)) {
            return badRequest(response, "Missing mandatory parameter: member");
        }

        if (StringUtils.isBlank(match)) {
            return badRequest(response, "Missing mandatory parameter: match");
        }

        if (match.trim().length() < MINIMUM_MATCH_LENGTH) {
            return badRequest(
                response,
                "Match parameter value: '" + match
                    + "' was shorter than the minimum length: "
                    + MINIMUM_MATCH_LENGTH
            );
        }

        Member member = memberRepository.findOneByKey(memberKey);

        if (member == null) {
            return badRequest(
                response,
                "Unknown member: '" + memberKey
                    + "'. Example options: " + memberOptions()
            );
        }

        long affilateIdOptional =
            AffiliateRepository.AFFILIATE_ID_OPTIONAL_VALUE;

        if (StringUtils.isNotBlank(affiliateId)) {
            try {
                // Do not try to check affiliateId to not leak valid ids
                affilateIdOptional = Long.valueOf(affiliateId);
            } catch (NumberFormatException _) {
                response.setMatched(false);
                return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
                );
            }
        }

        Page<Affiliate> matchingAffiliates =
            affiliateRepository.findForCheck(
                affilateIdOptional,
                member,
                match,
                createZeroOneManyPageRequest()
            );

        response.setMatched(
            isSingleAffiliateMatch(matchingAffiliates)
        );

        return new ResponseEntity<>(
            response,
            HttpStatus.OK
        );
    }

    private boolean isSingleAffiliateMatch(Page<Affiliate> resultPage) {
        return resultPage.getNumberOfElements() == 1;
    }

    private Pageable createZeroOneManyPageRequest() {
        // Limit number of results to tell difference between 0, 1, many results
        return PageRequest.of(0, ZERO_ONE_MANY_RESULTS_LENGTH);
    }

    private ResponseEntity<AffiliateCheckDTO> badRequest(
        AffiliateCheckDTO response,
        String errorMessage) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        response.setError(status.getReasonPhrase());
        response.setStatus(status.value());
        response.setMessage(errorMessage);

        return new ResponseEntity<>(
            response,
            status
        );
    }

    private String memberOptions() {
        List<String> options = new ArrayList<>();

        for (Member member : memberRepository.findAll()) {
            options.add(member.getKey());
        }

        Collections.sort(options);

        StringBuilder builder = new StringBuilder();

        for (String memberKey : options) {
            builder.append(memberKey);
            builder.append(' ');
        }

        return builder.toString();
    }
}
