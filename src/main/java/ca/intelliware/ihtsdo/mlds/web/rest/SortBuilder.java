package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

public class SortBuilder {

	public static final String ORDER_BY = "([\\w/]+) ?(asc|desc)?";

	public Sort createSort(String orderby, Map<String,List<String>> fieldMappings, Sort defaultSort) {
		Sort sort = null;
		if (StringUtils.isNotBlank(orderby)) {
	    	Matcher matcher = Pattern.compile(ORDER_BY).matcher(orderby);
	    	if (matcher.matches()) {
	    		String matched = matcher.group(1);
	    		List<String> fields = fieldMappings.get(matched);
	    		if (fields.isEmpty()) {
	    			//FIXME should ensure 400 bad request 
	    			throw new IllegalArgumentException("Unknown orderby field");
	    		}
	    		Direction direction = Direction.ASC;
	    		String directionString = matcher.group(2);
	    		if ("desc".equalsIgnoreCase(directionString)) {
	    			direction = Direction.DESC;
	    		}
	    		List<Order> orders = new ArrayList<Order>();
	    		for (String field : fields) {
					orders.add(new Order(direction, field));
				}
	    		sort = new Sort(orders);
	    			
	    	} else {
	    		//FIXME should ensure 400 bad request 
	    		throw new IllegalArgumentException("Could not parse orderby");
	    	}
		}
		if (sort != null) {
			sort = sort.and(defaultSort);
		} else {
			sort = defaultSort;
		}
		return sort;
	}

}
