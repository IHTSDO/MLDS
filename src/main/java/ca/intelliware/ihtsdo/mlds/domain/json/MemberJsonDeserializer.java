package ca.intelliware.ihtsdo.mlds.domain.json;

import java.io.IOException;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class MemberJsonDeserializer extends
		JsonDeserializer<Member> {
	MemberRepository memberRepository;

	public MemberJsonDeserializer(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public Member deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);
		String key = node.get("key").textValue();
		Member result = memberRepository.findOneByKey(key);
		return result;
	}
}