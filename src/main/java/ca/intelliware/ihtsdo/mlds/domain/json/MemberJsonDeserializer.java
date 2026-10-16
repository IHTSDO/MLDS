package ca.intelliware.ihtsdo.mlds.domain.json;


import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class MemberJsonDeserializer extends ValueDeserializer<Member> {

    private final MemberRepository memberRepository;

    public MemberJsonDeserializer(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Member deserialize(
        JsonParser jp,
        DeserializationContext ctxt) throws JacksonException {

        JsonNode node = jp.readValueAsTree();

        String key = node.get("key").asString();

        return memberRepository.findOneByKey(key);
    }
}
