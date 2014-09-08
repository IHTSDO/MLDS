package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import ca.intelliware.ihtsdo.mlds.web.RouteLinkBuilder;

public class RouteLinkBuilderTest {
	RouteLinkBuilder routeLinkBuilder = new RouteLinkBuilder();

	@Test
	public void routeWithNoTokensIsUnmolested() throws URISyntaxException {
		URI uri = routeLinkBuilder.toURLWithKeyValues("/aRoute/");
		
		assertEquals(new URI("/aRoute/"), uri);
	}
	
	@Test
	public void simpleTokenIsReplaced() throws URISyntaxException {
		URI uri = routeLinkBuilder.toURLWithKeyValues("/aRoute/{pathVariable}", "pathVariable", 55);
		
		assertEquals(new URI("/aRoute/55"), uri);
	}
	
	@Test
	public void tokenReplacementIsURLEscaped() throws URISyntaxException {
		URI uri = routeLinkBuilder.toURLWithKeyValues("/aRoute/{pathVariable}", "pathVariable", "escape?");
		
		assertEquals(new URI("/aRoute/escape%3F"), uri);
	}

}
