package ca.intelliware.ihtsdo.mlds.web.rest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.ExtensionApplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class PartialCopyTest {

	@Test
	public void applySomePropertiesToAnObject() throws JsonProcessingException, IOException {
		ExtensionApplication extensionApplication = new ExtensionApplication();
		extensionApplication.setAffiliateDetails(new AffiliateDetails());
		extensionApplication.setNotesInternal("internalNotes");
		
		String incomingJSON = "{ \"notesInternal\": \"new value\"}";
		
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectReader readerForUpdating = objectMapper.readerForUpdating(extensionApplication);
		
		JsonNode tree = objectMapper.readTree(incomingJSON);
		readerForUpdating.readValue(tree);
		
		Assert.assertEquals("new value", extensionApplication.getNotesInternal());
	}
}
