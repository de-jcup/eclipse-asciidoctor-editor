package de.jcup.asciidoctoreditor;

public enum PreviewLayout {

	VERTICAL("vertical"),
	HORIZONTAL("horizontal"),
	EXTERNAL_BROWSER("external");
	
	private String id;
	
	private PreviewLayout(String mode){
		this.id=mode;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean isVertical(){
		return VERTICAL.equals(this);
	}
	
	public boolean isHorizontal(){
		return HORIZONTAL.equals(this);
	}
	
	public boolean isExternal(){
		return EXTERNAL_BROWSER.equals(this);
	}
	
	/**
	 * Returns corresponding mode or <code>null</code>
	 * @param id
	 * @return corresponding mode or <code>null</code>
	 */
	public static PreviewLayout fromId(String id){
		for (PreviewLayout mode: PreviewLayout.values()){
			if (mode.getId().equals(id)){
				return mode;
			}
		}
		return null;
	}
}
