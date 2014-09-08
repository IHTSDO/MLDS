package ca.intelliware.ihtsdo.mlds.domain.json;

import java.io.IOException;
import java.net.URI;

import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.service.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.web.RouteLinkBuilder;
import ca.intelliware.ihtsdo.mlds.web.rest.Routes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ReleaseFileSerializer extends JsonSerializer<ReleaseFile> {
	@Override
	public void serialize(ReleaseFile value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeStartObject();
		jgen.writeObjectField("releaseFileId", value.getReleaseFileId());
		jgen.writeObjectField("label", value.getLabel());
		jgen.writeObjectField("createdAt", value.getCreatedAt());
		jgen.writeObjectField("clientDownloadUrl", calculateClientDownloadUrl(value));
		if (isToSeeRawDownloadUrl()) {
			jgen.writeObjectField("downloadUrl", value.getDownloadUrl());
		}
		jgen.writeEndObject();
	}

	private boolean isToSeeRawDownloadUrl() {
		return new CurrentSecurityContext().isStaffOrAdmin();
	}

	private URI calculateClientDownloadUrl(ReleaseFile value) {
		RouteLinkBuilder routeLinkBuilder = new RouteLinkBuilder();
		return routeLinkBuilder.toURLWithKeyValues(
				Routes.RELEASE_FILE_DOWNLOAD, 
				"releasePackageId", value.getReleaseVersion().getReleasePackage().getReleasePackageId(),
				"releaseVersionId", value.getReleaseVersion().getReleaseVersionId(),
				"releaseFileId", value.getReleaseFileId()
				);
	}
}