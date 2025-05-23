package ca.intelliware.ihtsdo.mlds.web;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Service
public class RouteLinkBuilder {

	public URI toURLWithKeyValues(String route, Object... keyValueList) {
		Validate.isTrue(keyValueList.length %2 ==0, "key/value list has even numebr of elements");
	 	Map<String,Object> variables = Maps.newHashMap();
		for (int i = 0; i < keyValueList.length; i+=2) {
			String key = (String) keyValueList[i];
			Object value = keyValueList[i+1];
			variables.put(key, value);
		}
		return toUrl(route, variables);
	}

	private URI toUrl(String urlTemplate, Map<String, Object> variables) {
		try {
			String result = urlTemplate;
			Set<Entry<String, Object>> entrySet = variables.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				String encodedValue = URLEncoder.encode(entry.getValue().toString(), "UTF-8");
				result = result.replace("{" + entry.getKey() + "}", encodedValue);
			}
			return new URI(result);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
