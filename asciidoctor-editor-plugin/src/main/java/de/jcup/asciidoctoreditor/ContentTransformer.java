package de.jcup.asciidoctoreditor;

public interface ContentTransformer {
	public String transform(String origin);

	public boolean isTransforming(Object data);
}
