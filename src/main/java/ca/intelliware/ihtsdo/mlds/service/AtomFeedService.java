package ca.intelliware.ihtsdo.mlds.service;


import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import com.rometools.rome.feed.atom.*;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import org.hibernate.boot.model.relational.QualifiedName;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

@Configuration
@Transactional
@Service

public class AtomFeedService extends AbstractAtomFeedView {

    private ReleaseVersionRepository releaseVersionRepository;

    private String feedTitle;

    private String feedLink;

    private String feedGenerator;

    private String feedProfile;

    private String feedBaseUrl;


    public AtomFeedService(ReleaseVersionRepository releaseVersionRepository, Environment environment) {
        this.releaseVersionRepository = releaseVersionRepository;
        this.feedTitle = environment.getProperty("feedTitle");
        this.feedLink = environment.getProperty("feedLink");
        this.feedGenerator = environment.getProperty("feedGenerator");
        this.feedProfile = environment.getProperty("feedProfile");
        this.feedBaseUrl = environment.getProperty("feedBaseUrl");
    }

    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Feed feed, HttpServletRequest request) {
        super.buildFeedMetadata(model, feed, request);
        SyndFeed syndFeed = (SyndFeed) model.get("feed");

        feed.setTitle(feedTitle); //for title
        List<Link> links = new ArrayList<>(); //for link
        Link link = new Link();
        link.setHref(feedLink);
        link.setType("application/atom+xml");
        links.add(link);
        feed.setAlternateLinks(links);

        String uuid = "urn:uuid:" + UUID.randomUUID().toString(); //for id
        feed.setId(uuid);

        Generator generator = new Generator(); //for generator
        generator.setValue(feedGenerator);
        feed.setGenerator(generator);


        Date generationDate = new Date(); //for updateddate
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = dateFormat.format(generationDate);
        try {
            Date updatedDate = dateFormat.parse(formattedDate);
            feed.setUpdated(updatedDate);
        } catch (ParseException e) {

        }


        Namespace nctsNamespace = Namespace.getNamespace("ncts", "http://ns.electronichealth.net.au/ncts/syndication/asf/extensions/1.0.0");
        Element atomSyndicationFormatProfile = new Element("atomSyndicationFormatProfile", nctsNamespace);
        atomSyndicationFormatProfile.setNamespace(nctsNamespace);

        atomSyndicationFormatProfile.setText(feedProfile);

        List<Element> foreignMarkup = feed.getForeignMarkup();
        foreignMarkup.add(atomSyndicationFormatProfile);

    }

    @Override
    public List<Entry> buildFeedEntries(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
        SyndFeed syndFeed = (SyndFeed) model.get("feed");
        List<SyndEntry> syndEntries = syndFeed.getEntries();
   Collection<Object[]> atomFeed = releaseVersionRepository.listAtomFeed();
        List<Entry> entries = new ArrayList<>();
        for (Object[] entry : atomFeed) {
            String title = (String) entry[0];
            String downloadUrl = (String) entry[1];
            String memberOrgName = (String) entry[2];
            String memberOrgURL = (String) entry[3];
            String contactEmail = (String) entry[4];
            String id = (String) entry[5];
            String copyrights = (String) entry[6];
            Date updated = (Date) entry[7];
            Date publisedAt = (Date) entry[8];
            String summaryValue = (String) entry[9];
            String releasePackageURI = (String) entry[10];
            String versionURI = (String) entry[11];
            String versionDependentURI = (String) entry[12];
            String versionDependentDerivativeURI = (String) entry[13];
            BigInteger packageId = (BigInteger) entry[14];
            BigInteger versionId = (BigInteger) entry[15];
            BigInteger fileId = (BigInteger) entry[16];


            Entry atomFeedEntry = new Entry();
            atomFeedEntry.setTitle(title);

            List<Link> finalLink = new ArrayList<>(); //for link
            String finalUrl = feedBaseUrl + "api/releasePackages/" + packageId + "/releaseVersions/" + versionId + "/releaseFiles/" + fileId + "/download";
            String fileExtension = downloadUrl.substring(downloadUrl.lastIndexOf('.') + 1);
            Link link = new Link();
            link.setHref(finalUrl);
            link.setType("application/"+fileExtension);
            long fileSize = getFileSize(finalUrl);
            link.setLength(fileSize);
            finalLink.add(link);
            atomFeedEntry.setAlternateLinks(finalLink);


            List<Category> finalCategory = new ArrayList<>();
            Category category = new Category();
            category.setTerm("SCT_RF2_SNAPSHOT");
            category.setLabel("SNOMED CT RF2 Snapshot");
            category.setScheme("http://ns.electronichealth.net.au/ncts/syndication/asf/scheme/1.0.0");
            finalCategory.add(category);
            atomFeedEntry.setCategories(finalCategory);

            Person author = new Person();
            author.setName(memberOrgName);
            author.setUri(memberOrgURL);
            author.setEmail(contactEmail);
            atomFeedEntry.setAuthors(Collections.singletonList(author));

            atomFeedEntry.setId(id);
            atomFeedEntry.setRights(copyrights);
            atomFeedEntry.setUpdated(updated);
            atomFeedEntry.setPublished(publisedAt);
            Content summary = new Content();
            summary.setValue(summaryValue);
            atomFeedEntry.setSummary(summary);
            Namespace nctsNamespace = Namespace.getNamespace("ncts", "http://ns.electronichealth.net.au/ncts/syndication/asf/extensions/1.0.0");
            Element contentItemIdentifierElement = new Element("contentItemIdentifier", nctsNamespace);
            contentItemIdentifierElement.setText(releasePackageURI);
            atomFeedEntry.getForeignMarkup().add(contentItemIdentifierElement);
            Namespace nctsNamespaceVersion = Namespace.getNamespace("ncts", "http://ns.electronichealth.net.au/ncts/syndication/asf/extensions/1.0.0");
            Element contentItemVersionElement = new Element("contentItemVersion", nctsNamespaceVersion);
            contentItemVersionElement.setText(versionURI);
            atomFeedEntry.getForeignMarkup().add(contentItemVersionElement);

            if ((versionDependentURI != null && !versionDependentURI.isEmpty())
                || (versionDependentDerivativeURI != null && !versionDependentDerivativeURI.isEmpty())){
                Namespace sctNamespaceVersion = Namespace.getNamespace("sct", "http://snomed.info/syndication/sct-extension/1.0.0");
                Element packageDependencyElement = new Element("packageDependency", sctNamespaceVersion);
                if (versionDependentURI != null && !versionDependentURI.isEmpty()) {
                    Namespace nctsNamespaceVersionDURI = Namespace.getNamespace("sct", "http://snomed.info/syndication/sct-extension/1.0.0");
                    Element editionDependencyElement = new Element("editionDependency", nctsNamespaceVersionDURI);
                    editionDependencyElement.setText(versionDependentURI);
                    packageDependencyElement.addContent(editionDependencyElement);

                }
                if (versionDependentDerivativeURI != null && !versionDependentDerivativeURI.isEmpty()) {
                    Namespace nctsNamespaceVersionDDURI = Namespace.getNamespace("sct", "http://snomed.info/syndication/sct-extension/1.0.0");
                    Element derivativeDependencyElement = new Element("derivativeDependency", nctsNamespaceVersionDDURI);
                    derivativeDependencyElement.setText(versionDependentDerivativeURI);
                    packageDependencyElement.addContent(derivativeDependencyElement);
                }
                atomFeedEntry.getForeignMarkup().add(packageDependencyElement);
            }
            entries.add(atomFeedEntry);
        }
        model.put("entries", entries);
        return entries;
    }

    private long getFileSize(String finalUrl) {
        try {
            URL url = new URL(finalUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            int contentLength = connection.getContentLength();
            connection.disconnect();

            return contentLength;
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Return -1 if file size calculation fails
        }
    }
}







