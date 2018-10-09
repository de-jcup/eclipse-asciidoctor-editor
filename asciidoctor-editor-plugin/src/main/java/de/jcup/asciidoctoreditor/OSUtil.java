package de.jcup.asciidoctoreditor;

public class OSUtil {

	private static Boolean isWindows;
	static{
		String os = System.getProperty("os.name");
		if (os==null || os.toLowerCase().indexOf("windows")!=-1){
			isWindows=Boolean.TRUE;
		}else{
			isWindows=Boolean.FALSE;
		}
	}
	public static boolean isWindows() {
		return isWindows.booleanValue();
	}
}
