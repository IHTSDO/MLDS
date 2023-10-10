package ca.intelliware.ihtsdo.mlds.service;


import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
@Transactional
@Service
public class AtomFeedService {

    @Autowired
    private ReleaseVersionRepository releaseVersionRepository;

    @Value("${atom.feed.baseUrl}")
    private String feedBaseUrl;

    @Value("${atom.feed.title}")
    private String feedTitle;

    @Value("${atom.feed.link}")
    private String feedLink;

    @Value("${atom.feed.UUID}")
    private String feedUUID;

    @Value("${atom.feed.generator}")
    private String feedGenerator;

    @Value("${atom.feed.profile}")
    private String feedProfile;



    public String generateAtomFeed() {

        StringBuilder atomFeedXml = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        atomFeedXml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        atomFeedXml.append("<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:ncts=\"http://ns.electronichealth.net.au/ncts/syndication/asf/extensions/1.0.0\" xmlns:sct=\"http://snomed.info/syndication/sct-extension/1.0.0\">\n");
        atomFeedXml.append("    <title>").append(feedTitle).append("</title>\n");
        atomFeedXml.append("    <link rel=\"alternate\" type=\"application/atom+xml\" href=\"").append(feedProfile).append("\" />\n");
        atomFeedXml.append("    <id>urn:uuid:").append(feedUUID).append("</id>\n");
        atomFeedXml.append("    <generator>").append(feedGenerator).append("</generator>\n");
        atomFeedXml.append("    <updated>").append(dateFormat.format(new Date())).append("</updated>\n");
        atomFeedXml.append("    <ncts:atomSyndicationFormatProfile>http://ns.electronichealth.net.au/ncts/syndication/asf/profile/1.0.0</ncts:atomSyndicationFormatProfile>\n");

        Collection<Object[]> atomFeed = releaseVersionRepository.listAtomFeed();
        List<AtomEntryImpl> entries = createEntries(atomFeed);
        for (AtomEntryImpl entry : entries) {
            atomFeedXml.append(entry.toXml());
        }

        atomFeedXml.append("</feed>");

        return atomFeedXml.toString();
    }

    private List<AtomEntryImpl> createEntries(Collection<Object[]> atomFeed) {

        Map<String, AtomEntryImpl> entryMap = new HashMap<>();
        for (Object[] entryData : atomFeed) {
            if (entryData.length >= 5) {
                String versionId = String.valueOf(entryData[15]);
                AtomEntryImpl entry;

                if (entryMap.containsKey(versionId)) {
                    entry = entryMap.get(versionId);
                } else {
                    entry = new AtomEntryImpl();
                    entry.setFeedBaseUrl(feedBaseUrl);
                    entry.setTitle(String.valueOf(entryData[0]));
                    entry.setDownloadUrl(String.valueOf(entryData[1]));
                    entry.setMemberOrgName(String.valueOf(entryData[2]));
                    entry.setMemberOrgURL(String.valueOf(entryData[3]));
                    entry.setContactEmail(String.valueOf(entryData[4]));
                    entry.setId(String.valueOf(entryData[5]));
                    entry.setCopyrights(String.valueOf(entryData[6]));
                    entry.setUpdated(String.valueOf(entryData[7]));
                    entry.setPublishedAt(String.valueOf(entryData[8]));
                    entry.setSummary(String.valueOf(entryData[9]));
                    entry.setReleasePackageURI(String.valueOf(entryData[10]));
                    entry.setVersionURI(String.valueOf(entryData[11]));
                    entry.setVersionDependentURI(String.valueOf(entryData[12]));
                    entry.setVersionDependentDerivativeURI(String.valueOf(entryData[13]));
                    entry.setPackageId(String.valueOf(entryData[14]));
                    entry.setVersionId(versionId);
                    entry.setFileId(String.valueOf(entryData[16]));
                    entry.setPrimaryFile((Boolean) entryData[17]);
                    entry.setMd5Hash(String.valueOf(entryData[18]));
                    entry.setFileSize(String.valueOf(entryData[19]));
                }

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                Date date;
                try {
                    date = inputFormat.parse(String.valueOf(entryData[7]));
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    String formattedDate = outputFormat.format(date);
                    entry.setUpdated(formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                entry.addLink(versionId, String.valueOf(entryData[16]),
                    (Boolean) entryData[17],String.valueOf(entryData[1]),String.valueOf(entryData[18]),String.valueOf(entryData[19]));
                entryMap.put(versionId, entry);
            }
        }
        return new ArrayList<>(entryMap.values());
    }


}







