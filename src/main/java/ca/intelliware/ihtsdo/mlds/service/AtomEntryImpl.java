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

        for (Map.Entry<String, List<String>> entry : versionLinks.entrySet()) {
            String versionId = entry.getKey();
            List<String> links = entry.getValue();
            for (String fileId : links) {
                String downloadUrl = feedBaseUrl + "api/releasePackages/" + packageId + "/releaseVersions/" + versionId + "/releaseFiles/" + fileId + "/download";
                boolean checkPrimaryFile = fileIdToPrimaryFileMap.get(fileId);
                String fileUrl = fileIdToDownloadUrlMap.get(fileId);
                String checkFileSize = fileIdToFileSizeMap.get(fileId);
                String fileHash = fileIdToFileHash.get(fileId);
                String fileExtension = getFileExtension(fileUrl);

                if (!"null".equals(checkFileSize) && !"null".equals(fileHash) && !checkFileSize.isEmpty() && !fileHash.isEmpty()) {
                    if (checkPrimaryFile) {
                        entryXml.append("        <link rel=\"alternate\" type=\"application/").append(fileExtension).append("\" href=\"").append(downloadUrl).append("\" length=\"").append(checkFileSize).append("\" sct:md5Hash=\"").append(fileHash).append("\" />\n");
                    } else {
                        entryXml.append("        <link rel=\"related\" type=\"application/").append(fileExtension).append("\" href=\"").append(downloadUrl).append("\" length=\"").append(checkFileSize).append("\" sct:md5Hash=\"").append(fileHash).append("\" />\n");
                    }
                }

                else {
                    if(checkPrimaryFile){
                        entryXml.append("        <link rel=\"alternate\" type=\"application/").append(fileExtension).append("\" href=\"").append(downloadUrl).append("\" />\n");
                    }
                    else{
                        entryXml.append("        <link rel=\"related\" type=\"application/").append(fileExtension).append("\" href=\"").append(downloadUrl).append("\" />\n");
                    }
                }
            }
        }

        //for category
        if(packageType.equals("SCT_RF2_SNAPSHOT")) {
            entryXml.append("        <category term=\"").append(packageType).append("\" label=\"SNOMED CT RF2 Snapshot Package\" scheme=\"http://ns.electronichealth.net.au/ncts/syndication/asf/scheme/1.0.0\" />\n");
        }
        if(packageType.equals("SCT_RF2_FULL")) {
            entryXml.append("        <category term=\"").append(packageType).append("\" label=\"SNOMED CT RF2 Full Package\" scheme=\"http://ns.electronichealth.net.au/ncts/syndication/asf/scheme/1.0.0\" />\n");
        }
        if(!packageType.equals("SCT_RF2_SNAPSHOT") && !packageType.equals("SCT_RF2_FULL")) {
            entryXml.append("        <category term=\"OTHER\" label=\"Other Package\" scheme=\"http://ns.electronichealth.net.au/ncts/syndication/asf/scheme/1.0.0\" />\n");
        }

        entryXml.append("        <author>\n");
        entryXml.append("            <name>").append(memberOrgName).append("</name>\n");
        entryXml.append("            <uri>").append(memberOrgURL).append("</uri>\n");
        entryXml.append("            <email>").append(contactEmail).append("</email>\n");
        entryXml.append("        </author>\n");
        entryXml.append("        <id>urn:uuid:").append(id).append("</id>\n");
        entryXml.append("        <rights>").append(copyrights).append("</rights>\n");
        entryXml.append("        <updated>").append(updated).append("</updated>\n");
        entryXml.append("        <published>").append(publishedAt).append("T00:00:00Z</published>\n");
        entryXml.append("        <summary>").append(summary).append("</summary>\n");
        entryXml.append("        <ncts:contentItemIdentifier>").append(releasePackageURI).append("</ncts:contentItemIdentifier>\n");

        entryXml.append("        <ncts:contentItemVersion>").append(versionURI).append("</ncts:contentItemVersion>\n");
        if(versionDependentURI != null && !versionDependentURI.isEmpty() && !Objects.equals(versionDependentURI, "null")){
            entryXml.append("        <sct:packageDependency>\n");
            entryXml.append("            <sct:editionDependency>").append(versionDependentURI).append("</sct:editionDependency>\n");
            if(versionDependentDerivativeURI != null && !versionDependentDerivativeURI.isEmpty() && !Objects.equals(versionDependentDerivativeURI, "null")){
                entryXml.append("            <sct:derivativeDependency>").append(versionDependentDerivativeURI).append("</sct:derivativeDependency>\n");
            }
            entryXml.append("        </sct:packageDependency>\n");
        }

        entryXml.append("    </entry>\n");

        return entryXml.toString();
    }

    private String getFileExtension(String fileUrl) {
        String fileExtension = fileUrl.substring(fileUrl.lastIndexOf('.') + 1);
        return fileExtension;
    }

}
