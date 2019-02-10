package de.jcup.asciidoctoreditor;

public abstract class AbstractContentTransformer implements ContentTransformer{

	@Override
	public final String transform(ContentTransformerData data) {
		if (data==null) {
			return null;
		}
		return saveTransform(data);
	}
	
	/**
	 * At this point data is never null
	 * @param data
	 * @return
	 */
	protected abstract String saveTransform(ContentTransformerData data) ;


}
