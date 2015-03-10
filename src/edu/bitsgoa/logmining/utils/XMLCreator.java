package edu.bitsgoa.logmining.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

public class XMLCreator {

	private String parent;
	private String child;
	private Integer ctr = 0;
	private HashMap<Integer, String> xml;
	private String root;
	private HashMap<Integer, String> roots;
	private StringBuilder sB;

	private HashMap<Integer, String> getRoot() {
		this.roots.put(1, "<" + root + ">");
		this.roots.put(2, "</" + root + ">");
		return roots;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public XMLCreator() {
		if (this.xml == null) {
			this.xml = new HashMap<Integer, String>();
		}
		if (this.roots == null) {
			this.roots = new HashMap<Integer, String>();
		}
		if (this.sB == null) {
			this.sB = new StringBuilder();
		}
	}


	public void startParentField(String parent) { this.parent = "<" + parent
			+ ">"; this.ctr++; addToXML(getParentField()); }

	public void endParentField(String parent) { this.parent = "</" + parent
			+ ">"; this.ctr++; addToXML(getParentField()); }

	public void setParent(String parent) { startParentField(parent);
	endParentField(parent); }


	public void addChildField(String child) {
		this.child = "<" + child + ">" + "</" + child + ">";
		this.ctr++;
		addToXML(getChildField());

	}

	@SuppressWarnings("deprecation")
	public void addChildField(String child, String value) {
		
		System.out.println("debugging the error");
		System.out.println("this.child"+this.child);
		System.out.println("encoder value"+URLEncoder.encode(value));
		
		
		this.child = "<" + child + ">" + URLEncoder.encode(value) + "</" + child + ">";
		this.ctr++;
		addToXML(getChildField());
	}

	private String getParentField() {
		return parent;
	}

	private String getChildField() {
		return child;
	}

	private void addToXML(String element) {
		xml.put(ctr, element);
	}

	public String buildXMLPreview() {

		// Set<Integer> keys = xml.keySet();
		getRoot();
		sB.append(roots.get(1));
		for (int x = 1; x <= xml.size(); x++) {
			sB.append(xml.get(x) + "\n");
		}
		sB.append(roots.get(2));
		String finalString = sB.toString();
		System.out.println(sB.toString());
		sB.delete(0, sB.length());
		return finalString;
	}

	public void buildXMLToFile() throws IOException {

		FileWriter writer = new FileWriter("C:/Users/kavish_406541/Documents/workspace/iCirrusUI/WebContent/iCirrusUI-debug/files.xml");
		BufferedWriter out = new BufferedWriter(writer);
		getRoot();
		out.append(roots.get(1));
		out.newLine();
		for (int x = 1; x <= xml.size(); x++) {
			out.append(xml.get(x));
			out.newLine();
		}
		out.append(roots.get(2));
		out.flush();
		out.close();
	}

}