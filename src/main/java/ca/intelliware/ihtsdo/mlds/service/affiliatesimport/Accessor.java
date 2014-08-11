package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ReflectionUtils;

public class Accessor {
	Class rootClazz;
	String attributePath;
	List<String> attributes = new ArrayList<String>();
	List<Field> fields = new ArrayList<Field>();
	public Accessor(Class rootClazz, String attributePath) {
		this.rootClazz = rootClazz;
		this.attributePath = attributePath;
		populateFields();
	}
	private void populateFields() {
		Class currentClazz = rootClazz;
		String[] split = attributePath.split("\\.");
		for (int i = 0; i < split.length; i++) {
			String attribute = split[i];
			Field field = ReflectionUtils.findField(currentClazz, attribute);
			attributes.add(attribute);
			fields.add(field);
			ReflectionUtils.makeAccessible(field);
			currentClazz = field.getType();
		}
	}
	public Class getAttributeClass() {
		return fields.get(fields.size() - 1).getType();
	}
	public Object getValue(Object rootObject) {
		Object value = rootObject;
		for (int i = 0; i < attributes.size(); i++) {
			if (value == null) {
				break;
			}
			value = ReflectionUtils.getField(fields.get(i), value);
		}
		return value;
	}
	public void setValue(Object rootObject, Object setValue) throws InstantiationException, IllegalAccessException {
		Object target = rootObject;
		for (int i = 0; i < attributes.size() - 1; i++) {
			Field field = fields.get(i);
			Object value = ReflectionUtils.getField(field, target);
			if (value == null) {
				value = field.getType().newInstance();
				ReflectionUtils.setField(field, target, value);
			}
			target = value;
		}
		ReflectionUtils.setField(fields.get(fields.size()-1), target, setValue);
	}
}