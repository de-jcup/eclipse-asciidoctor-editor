package de.jcup.asciidoctoreditor;

public class NotChangingContentTransformer implements ContentTransformer{

	public static NotChangingContentTransformer INSTANCE = new NotChangingContentTransformer();
	
	private NotChangingContentTransformer(){
		
	}

	@Override
	public String transform(String origin) {
		return origin;
	}

	@Override
	public boolean isTransforming(Object data) {
		return false;
	}
	
}
