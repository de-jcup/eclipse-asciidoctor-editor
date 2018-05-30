package de.jcup.asciidoctoreditor;

public class PlantUMLContentTransformer implements ContentTransformer{

	@Override
	public String transform(String origin) {
		StringBuilder sb = new StringBuilder();
		if (origin!=null){
			sb.append("[plantuml]\n----\n");
			sb.append(origin);
			sb.append("\n----\n");
		}
		return sb.toString();
	}

	@Override
	public boolean isTransforming(Object data) {
		return true;
	}

}
