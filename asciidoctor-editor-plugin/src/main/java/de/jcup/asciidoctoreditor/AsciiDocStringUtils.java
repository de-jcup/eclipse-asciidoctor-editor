package de.jcup.asciidoctoreditor;

public class AsciiDocStringUtils {

	/**
	 * Resolves filenames from fullstrings of an potential include.<br><br>
	 * Example:<br>
	 * <code>include::src/xyz/filenamexyz.adoc[]</code><br>
	 * will be resolved to <br>
	 * <code>src/xyz/filenamexyz.adoc</code>
	 * @param potentialInclude
	 * @return resolved filename of include or <code>null</code>
	 */
	public static String resolveFilenameOfIncludeOrNull(String potentialInclude) {
		if (potentialInclude==null){
			return null;
		}
		if (potentialInclude.startsWith("include::")){
			if (potentialInclude.endsWith(".adoc[]")){
				String fileName = potentialInclude.substring("include::".length());
				fileName=fileName.substring(0,fileName.length()-2);
				return fileName;
			}
		}
		return null;
	}
}
