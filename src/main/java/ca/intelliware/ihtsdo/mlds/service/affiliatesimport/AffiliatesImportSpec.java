package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.util.ArrayList;
import java.util.List;

public class AffiliatesImportSpec {

	private List<ColumnSpec> columns = new ArrayList<ColumnSpec>();
	
	public List<ColumnSpec> getColumns() {
		return columns;
	}
	
	
	public static class ColumnSpec {
		String columnName;
		String attributeClass;
		boolean required;
		String example;
		List<String> options;
		public String getColumnName() {
			return columnName;
		}
		public String getAttributeClass() {
			return attributeClass;
		}
		public boolean isRequired() {
			return required;
		}
		public String getExample() {
			return example;
		}
		public List<String> getOptions() {
			return options;
		}
	}
}
