package ca.intelliware.ihtsdo.mlds.domain;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class MemberSerializer extends JsonSerializer<Member> {
	@Override
	public void serialize(Member value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeStartObject();
		jgen.writeObjectField("key", value.getKey());
		jgen.writeEndObject();
	}
}