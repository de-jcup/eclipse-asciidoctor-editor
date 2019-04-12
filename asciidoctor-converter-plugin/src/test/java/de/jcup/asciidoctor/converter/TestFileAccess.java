package de.jcup.asciidoctor.converter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

public class TestFileAccess {

	static File projectRootFolder;
	
	static File testResources;
	
	static {
		testResources=new File("src/test/resources");
		if (! testResources.exists()) {
			testResources=new File("asciidoctor-converter-plugin/src/test/resources");
		}
		if (! testResources.exists()) {
			throw new IllegalStateException();
		}
		projectRootFolder=testResources.getParentFile().getParentFile();
	}
	
	public static Path createTempFolder() throws IOException {
	    return Files.createTempDirectory("asciidoceditor");
	}

	public static Path write(Path tempFolder, String relativePathToFile, String code) throws IOException{
	    if (tempFolder==null) {
	        throw new IllegalArgumentException("path may not be null");
	    }
	    Path path = tempFolder.resolve(relativePathToFile);
	    Files.createDirectories(path.getParent());
	    
	    Files.newBufferedWriter(path).write(code);
	    return path;
	    
	}
	
	public static File getTestResource(String path) {
		File file = new File(testResources,path);
		if (! file.exists()) {
			throw new IllegalStateException();
		}
		return file;
	}
	
	public static String getTestResourceAsString(String path) {
	    File file = getTestResource(path);
	    try  {
            List<String> lines = Files.readAllLines(file.toPath());
            StringBuilder sb= new StringBuilder();
            for (Iterator<String> it = lines.iterator();it.hasNext();) {
                sb.append(it.next());
                if (it.hasNext()) {
                    sb.append("\n");
                }
            }
            return sb.toString();
            
        } catch (IOException e) {
            throw new IllegalStateException("Should not happen", e);
        }
	}

	

}
