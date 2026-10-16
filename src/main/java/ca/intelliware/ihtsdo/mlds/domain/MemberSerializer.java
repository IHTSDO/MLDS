package ca.intelliware.ihtsdo.mlds.domain;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class MemberSerializer extends ValueSerializer<Member> {

    @Override
    public void serialize(
        Member value,
        JsonGenerator jgen,
        SerializationContext provider) {

        jgen.writeStartObject();
        jgen.writePOJOProperty("key", value.getKey());
        jgen.writeEndObject();
    }
}
