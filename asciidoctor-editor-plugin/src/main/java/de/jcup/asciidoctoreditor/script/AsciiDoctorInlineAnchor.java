package de.jcup.asciidoctoreditor.script;

public class AsciiDoctorInlineAnchor {

	private int end;
	private int position;
	private String label;
	private String id;

	public AsciiDoctorInlineAnchor(String text, int position, int end) {
		this.label = text;
		this.end = end;
		this.position = position;
		
		this.id = createIDByLabel();
	}

	private String createIDByLabel() {
		if (label==null){
			return null;
		}
		if (!label.startsWith("[[")){
			return "illegal-nostart-"+System.nanoTime();
		}
		if (!label.endsWith("]]")){
			return "illegal-noend-"+System.nanoTime();
		}
		return label.substring(2,label.length()-2);
	}


	public String getLabel() {
		return label;
	}

	public int getPosition() {
		return position;
	}

	public int getEnd() {
		return end;
	}

	public String getId() {
		return id;
	}

}
