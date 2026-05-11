package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.Member;

import java.util.List;

public class ApplicationFilter {

    private Member member;
    private List<ApprovalState> approvalStates;
    private String countryCode;

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public List<ApprovalState> getApprovalStates() {
        return approvalStates;
    }

    public void setApprovalStates(List<ApprovalState> approvalStates) {
        this.approvalStates = approvalStates;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
