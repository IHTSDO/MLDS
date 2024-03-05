package ca.intelliware.ihtsdo.mlds.web.rest;


import ca.intelliware.ihtsdo.mlds.service.AtomFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = Routes.FEED_URL,
    method= RequestMethod.GET,
    produces = MediaType.APPLICATION_ATOM_XML_VALUE)
public class AtomFeedResource {

    @Autowired
    private AtomFeedService atomFeedService;

    @GetMapping(produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    public String getAtomFeed() {
        return atomFeedService.generateAtomFeed();
    }
}
