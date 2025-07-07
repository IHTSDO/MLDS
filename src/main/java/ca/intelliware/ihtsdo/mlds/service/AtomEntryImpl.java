package ca.intelliware.ihtsdo.mlds.service;

import java.util.*;

public class AtomEntryImpl {

    private String title;
    private String downloadUrl;
    private String memberOrgName;
    private String memberOrgURL;
    private String contactEmail;
    private String id;
    private String copyrights;
    private String updated;
    private String publishedAt;
    private String summary;
    private String releasePackageURI;
    private String versionURI;
    private String versionDependentURI;
    private String versionDependentDerivativeURI;
    private String packageId;
    private String versionId;
    private String fileId;
    private boolean primaryFile;
    private String md5Hash;
    private String fileSize;
    private String packageType;
    private String feedBaseUrl;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getMemberOrgName() {
        return memberOrgName;
    }

    public void setMemberOrgName(String memberOrgName) {
        this.memberOrgName = memberOrgName;
    }

    public String getMemberOrgURL() {
        return memberOrgURL;
    }

    public void setMemberOrgURL(String memberOrgURL) {
        this.memberOrgURL = memberOrgURL;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getReleasePackageURI() {
        return releasePackageURI;
    }

    public void setReleasePackageURI(String releasePackageURI) {
        this.releasePackageURI = releasePackageURI;
    }

    public String getVersionURI() {
        return versionURI;
    }

    public void setVersionURI(String versionURI) {
        this.versionURI = versionURI;
    }

    public String getVersionDependentURI() {
        return versionDependentURI;
    }

    public void setVersionDependentURI(String versionDependentURI) {
        this.versionDependentURI = versionDependentURI;
    }

    public String getVersionDependentDerivativeURI() {
        return versionDependentDerivativeURI;
    }

    public void setVersionDependentDerivativeURI(String versionDependentDerivativeURI) {
        this.versionDependentDerivativeURI = versionDependentDerivativeURI;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public boolean isPrimaryFile() {
        return primaryFile;
    }

    public void setPrimaryFile(boolean primaryFile) {
        this.primaryFile = primaryFile;
    }

    public String getMd5Hash() {
        return md5Hash;
    }

    public void setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getFeedBaseUrl() {
        return feedBaseUrl;
    }

    public void setFeedBaseUrl(String feedBaseUrl) {
        this.feedBaseUrl = feedBaseUrl;
    }

    private Map<String, List<String>> versionLinks = new HashMap<>();
    private Map<String, Boolean> fileIdToPrimaryFileMap = new HashMap<>();

    private Map<String, String> fileIdToDownloadUrlMap = new HashMap<>();

    private Map<String, String> fileIdToFileSizeMap = new HashMap<>();

    private Map<String, String> fileIdToFileHash = new HashMap<>();

    private static final Map<String, String> EXTENSION_TO_MIME = Map.ofEntries(
        Map.entry("xls", "application/vnd.ms-excel"),
        Map.entry("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        Map.entry("doc", "application/msword"),
        Map.entry("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        Map.entry("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
        Map.entry("xml", "application/xml"),
        Map.entry("ods", "application/vnd.oasis.opendocument.spreadsheet"),
        Map.entry("csv", "text/csv"),
        Map.entry("pdf", "application/pdf"),
        Map.entry("php", "application/x-httpd-php"),
        Map.entry("jpg", "image/jpeg"),
        Map.entry("png", "image/png"),
        Map.entry("gif", "image/gif"),
        Map.entry("bmp", "image/bmp"),
        Map.entry("txt", "text/plain"),
        Map.entry("js", "application/javascript"),
        Map.entry("swf", "application/x-shockwave-flash"),
        Map.entry("mp3", "audio/mpeg"),
        Map.entry("zip", "application/zip"),
        Map.entry("rar", "application/vnd.rar"),
        Map.entry("tar", "application/x-tar"),
        Map.entry("html", "text/html"),
        Map.entry("htm", "text/html")
    );


    public void addLink(String versionId, String fileId, boolean primaryFile, String downloadUrl,
                        String md5Hash, String fileSize) {
        versionLinks.computeIfAbsent(versionId, k -> new ArrayList<>()).add(fileId);
        fileIdToPrimaryFileMap.put(fileId, primaryFile);
        fileIdToDownloadUrlMap.put(fileId, downloadUrl);
        fileIdToFileHash.put(fileId, md5Hash);
        fileIdToFileSizeMap.put(fileId, fileSize);
    }

    public String toXml() {
        StringBuilder entryXml = new StringBuilder();
        entryXml.append("    <entry>\n");

        entryXml.append("        <title>").append(title).append("</title>\n");
        // for links
        entryXml.append(buildLinksXml());
        // for category
        entryXml.append(buildCategoryXml(packageType));
        // for author
        entryXml.append(buildAuthorXml(memberOrgName, memberOrgURL, contactEmail));

        entryXml.append("        <id>urn:uuid:").append(id).append("</id>\n");

        if (!"null".equals(copyrights) && !copyrights.isEmpty()) {
        entryXml.append("        <rights>").append(copyrights).append("</rights>\n");
        }

        entryXml.append("        <updated>").append(updated).append("</updated>\n");

        if (!"null".equals(publishedAt) && !publishedAt.isEmpty()) {
            entryXml.append("        <published>").append(publishedAt).append("T00:00:00Z</published>\n");
        }

        if (!"null".equals(summary) && !summary.isEmpty()) {
            entryXml.append("        <summary>").append(summary).append("</summary>\n");
        }

        // for content xml
        entryXml.append(buildContentItemXml(releasePackageURI, versionURI, versionDependentURI, versionDependentDerivativeURI));

        entryXml.append("    </entry>\n");

        return entryXml.toString();
    }

    private String buildLinksXml() {
        StringBuilder linksXml = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : versionLinks.entrySet()) {
            String releaseVersionId = entry.getKey();
            List<String> links = entry.getValue();
            for (String releaseFileId : links) {
                String fileDownloadUrl = feedBaseUrl + "api/releasePackages/" + packageId
                    + "/releaseVersions/" + releaseVersionId + "/releaseFiles/" + releaseFileId + "/download";
                boolean checkPrimaryFile = fileIdToPrimaryFileMap.get(releaseFileId);
                String fileUrl = fileIdToDownloadUrlMap.get(releaseFileId);
                String checkFileSize = fileIdToFileSizeMap.get(releaseFileId);
                String fileHash = fileIdToFileHash.get(releaseFileId);
                String fileExtension = getFileExtension(fileUrl);
                String mimeType = getMimeTypeFromExtension(fileExtension);

                appendLinkXml(linksXml, fileDownloadUrl, checkPrimaryFile, mimeType, checkFileSize, fileHash);
            }
        }
        return linksXml.toString();
    }

    private void appendLinkXml(StringBuilder xml, String downloadUrl, boolean isPrimaryFile, String mimeType, String fileSize, String fileHash) {
        String rel = isPrimaryFile ? "alternate" : "related";
        xml.append("        <link rel=\"").append(rel).append("\" type=\"")
            .append(mimeType).append("\" href=\"").append(downloadUrl).append("\"");

        boolean hasSizeAndHash = !"null".equals(fileSize) && !"null".equals(fileHash) && !fileSize.isEmpty() && !fileHash.isEmpty();

        if (hasSizeAndHash) {
            String fileSizeDigitsOnly = fileSize.replaceAll("\\D", "");
            xml.append(" length=\"").append(fileSizeDigitsOnly).append("\" sct:md5Hash=\"").append(fileHash).append("\"");
        }
        xml.append(" />\n");
    }

    private String buildAuthorXml(String memberOrgName, String memberOrgURL, String contactEmail) {
        StringBuilder authorXml = new StringBuilder();
        authorXml.append("        <author>\n");
        authorXml.append("            <name>").append(memberOrgName).append("</name>\n");
        if (!"null".equals(memberOrgURL) && !memberOrgURL.isEmpty()) {
            authorXml.append("            <uri>").append(memberOrgURL).append("</uri>\n");
        }
        if (!"null".equals(contactEmail) && !contactEmail.isEmpty()) {
            authorXml.append("            <email>").append(contactEmail).append("</email>\n");
        }
        authorXml.append("        </author>\n");
        return authorXml.toString();
    }

    private String buildCategoryXml(String packageType) {
        StringBuilder categoryXml = new StringBuilder();
        String scheme = "http://ns.electronichealth.net.au/ncts/syndication/asf/scheme/1.0.0";
        String label = switch (packageType) {
            case "SCT_RF2_SNAPSHOT" -> "SNOMED CT RF2 Snapshot";
            case "SCT_RF2_FULL" -> "SNOMED CT RF2 Full";
            case "SCT_RF2_ALL" -> "SNOMED CT RF2 All";
            default -> {
                packageType = "OTHER";
                yield "Other Package";
            }
        };

        categoryXml.append("        <category term=\"").append(packageType).append("\" label=\"").append(label).append("\" scheme=\"").append(scheme).append("\" />\n");
        return categoryXml.toString();
    }


    private String buildContentItemXml(String releasePackageURI, String versionURI, String versionDependentURI, String versionDependentDerivativeURI) {
        StringBuilder contentXml = new StringBuilder();

        contentXml.append("        <ncts:contentItemIdentifier>").append(releasePackageURI).append("</ncts:contentItemIdentifier>\n");
        contentXml.append("        <ncts:contentItemVersion>").append(versionURI).append("</ncts:contentItemVersion>\n");

        if(versionDependentURI != null && !versionDependentURI.isEmpty() && !Objects.equals(versionDependentURI, "null")){
            contentXml.append("        <sct:packageDependency>\n");
            contentXml.append("            <sct:editionDependency>").append(versionDependentURI).append("</sct:editionDependency>\n");
            if(versionDependentDerivativeURI != null && !versionDependentDerivativeURI.isEmpty() && !Objects.equals(versionDependentDerivativeURI, "null")){
                contentXml.append("            <sct:derivativeDependency>").append(versionDependentDerivativeURI).append("</sct:derivativeDependency>\n");
            }
            contentXml.append("        </sct:packageDependency>\n");
        }

        return contentXml.toString();
    }

    private String getFileExtension(String fileUrl) {
        if (fileUrl == null || !fileUrl.contains(".")) {
            return "";
        }

        // Remove query params or fragments if any
        int queryIndex = fileUrl.indexOf('?');
        int hashIndex = fileUrl.indexOf('#');

        int endIndex = fileUrl.length();
        if (queryIndex != -1 && hashIndex != -1) {
            endIndex = Math.min(queryIndex, hashIndex);
        } else if (queryIndex != -1) {
            endIndex = queryIndex;
        } else if (hashIndex != -1) {
            endIndex = hashIndex;
        }

        String cleanUrl = fileUrl.substring(0, endIndex);
        int lastDotIndex = cleanUrl.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == cleanUrl.length() - 1) {
            return "";
        }

        return cleanUrl.substring(lastDotIndex + 1);
    }

    private String getMimeTypeFromExtension(String extension) {
        if (extension == null || extension.isBlank()) {
            return "application/octet-stream";
        }
        return EXTENSION_TO_MIME.getOrDefault(extension.toLowerCase(), "application/octet-stream");
    }



}
