package ca.intelliware.ihtsdo.mlds.domain;

import java.io.IOException;

import javax.persistence.EntityManager;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class MemberJsonDeserializer extends
		JsonDeserializer<Member> {
	private final EntityManager em;

	public MemberJsonDeserializer(EntityManager em) {
		this.em = em;
	}

	@Override
	public Member deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);
		String key = node.get("key").textValue();
		Member result = (Member) em
				.createQuery("from Member where key = :key")
				.setParameter("key", key)
				.getSingleResult();
		return result;
	}
}