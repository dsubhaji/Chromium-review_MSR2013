package edu.bitsgoa.logmining.dataimport;

public class DefaultLogFilter {
	@SuppressWarnings("unused")
	private String _exclusionRegEx=null;
	public DefaultLogFilter() {
		
	}
	public DefaultLogFilter(String regex) {
		_exclusionRegEx= regex;
	}
	public boolean shouldBeImported() {
		return true;
	}
}
