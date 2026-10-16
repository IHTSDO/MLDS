package ca.intelliware.ihtsdo.mlds.domain.json;

import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.web.RouteLinkBuilder;
import ca.intelliware.ihtsdo.mlds.web.rest.Routes;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.net.URI;

public class ReleaseFileSerializer extends ValueSerializer<ReleaseFile> {

    @Override
    public void serialize(
        ReleaseFile value,
        JsonGenerator jgen,
        SerializationContext provider) {

        jgen.writeStartObject();

        jgen.writePOJOProperty(
            "releaseFileId",
            value.getReleaseFileId()
        );

        jgen.writePOJOProperty(
            "label",
            value.getLabel()
        );

        jgen.writePOJOProperty(
            "createdAt",
            value.getCreatedAt()
        );

        jgen.writePOJOProperty(
            "clientDownloadUrl",
            calculateClientDownloadUrl(value)
        );

        jgen.writePOJOProperty(
            "md5Hash",
            value.getMd5Hash()
        );

        jgen.writePOJOProperty(
            "fileSize",
            value.getFileSize()
        );

        jgen.writePOJOProperty(
            "primaryFile",
            value.getPrimaryFile()
        );

        if (isToSeeRawDownloadUrl()) {
            jgen.writePOJOProperty(
                "downloadUrl",
                value.getDownloadUrl()
            );
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
            "releasePackageId",
            value.getReleaseVersion()
                .getReleasePackage()
                .getReleasePackageId(),
            "releaseVersionId",
            value.getReleaseVersion()
                .getReleaseVersionId(),
            "releaseFileId",
            value.getReleaseFileId()
        );
    }
}
