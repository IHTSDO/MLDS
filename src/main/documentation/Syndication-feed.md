# Syndication Feed

The [MLDS Syndication Feed](https://mlds.ihtsdotools.org/api/feed) is a public XML feed which extends the [Atom Syndication Format](https://tools.ietf.org/html/rfc4287) as described in the [IHTSDO
termserver-syndication](https://github.com/IHTSDO/termserver-syndication) github repository documentation.

The feed has been available since MLDS 4.0.3 was deployed on 20 September 2023.

Related JIRA MLDS project tickets include:

* [MLDS-987](https://jira.ihtsdotools.org/browse/MLDS-987) New: Atom syndication feed for MLDS
* [MLDS-1021](https://jira.ihtsdotools.org/browse/MLDS-1021) Syndication feed refinement
* [MLDS-1028](https://jira.ihtsdotools.org/browse/MLDS-1028) Syndication feed refinements
* [MLDS-1031](https://jira.ihtsdotools.org/browse/MLDS-1031) MLDS Syndication Feed: Improve consistency of alternate links length attribute value formatting
* [MLDS-1043](https://jira.ihtsdotools.org/browse/MLDS-1043) Missing dependencies in MLDS Syndication Feed
* [MLDS-1047](https://jira.ihtsdotools.org/browse/MLDS-1047) MLDS \[update\] : add Access-Control headers to Syndication feed
* [MLDS-1051](https://jira.ihtsdotools.org/browse/MLDS-1051) Syndication dependencies are not highlighted when about to be removed
* [MLDS-1131](https://jira.ihtsdotools.org/browse/MLDS-1131) MLDS Feed Authentication Breaking Change
* [MLDS-1190](https://jira.ihtsdotools.org/browse/MLDS-1190) Clarification: how are Archived releases handled in the syndication feeds?
* [MLDS-1197](https://jira.ihtsdotools.org/browse/MLDS-1197) Syndication Feed: data validation refinement

## Specification

The [MLDS-987 description](https://jira.ihtsdotools.org/browse/MLDS-987#descriptionmodule) referred directly to the [IHTSDO
termserver-syndication](https://github.com/IHTSDO/termserver-syndication) documentation as the requirements specification.

## Testing

Feed compliance verification in Dev and UAT has used the [W3C Feed Validation Service](https://validator.w3.org/feed/).

## Additional information

### Impact of [MLDS-1049](https://jira.ihtsdotools.org/browse/MLDS-1049) archiving features on syndication feed

[MLDS-1049](https://jira.ihtsdotools.org/browse/MLDS-1049) added release archiving features to MLDS. The impact of these on syndication feed contents was queried in [MLDS-1190](https://jira.ihtsdotools.org/browse/MLDS-1190), and the response also summarised here:

>The MLDS syndication feed only includes releases with published status, so the impact of archiving changes for [MLDS-1049](https://jira.ihtsdotools.org/browse/MLDS-1049) is to remove a previously published release from the syndication feed if it gets archived. This is also the impact of changing it to any other offline status.
