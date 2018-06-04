package de.jcup.asciidoctoreditor.provider;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.asciidoctor.AsciiDocDirectoryWalker;
import org.asciidoctor.DirectoryWalker;
import org.asciidoctor.ast.DocumentHeader;

public class AsciiDoctorAttributesProvider {
	
	private Map<String, Object> cachedAttributes;
	private AsciiDoctorProviderContext context;

	AsciiDoctorAttributesProvider(AsciiDoctorProviderContext context){
		if (context==null ){
			throw new IllegalArgumentException("context may never be null!");
		}
		this.context=context;
	}

	protected Map<String, Object> getCachedAttributes() {
		if (cachedAttributes == null) {
			cachedAttributes = resolveAttributes(context.baseDir);
		}
		return cachedAttributes;
	}

	protected String resolveImagesDirPath(File baseDir) {

		Object imagesDir = getCachedAttributes().get("imagesdir");

		String imagesDirPath = null;
		if (imagesDir != null) {
			imagesDirPath = imagesDir.toString();
			if (imagesDirPath.startsWith("./")) {
				File imagePathNew = new File(baseDir, imagesDirPath.substring(2));
				imagesDirPath = imagePathNew.getAbsolutePath();
			}
		} else {
			imagesDirPath = baseDir.getAbsolutePath();
		}
		return imagesDirPath;
	}

	protected Map<String, Object> resolveAttributes(File baseDir) {
		Map<String, Object> map = new HashMap<>();
		Set<DocumentHeader> documentIndex = new HashSet<DocumentHeader>();
		DirectoryWalker directoryWalker = new AsciiDocDirectoryWalker(baseDir.getAbsolutePath());

		for (File file : directoryWalker.scan()) {
			documentIndex.add(context.asciidoctor.readDocumentHeader(file));
		}
		for (DocumentHeader header : documentIndex) {
			map.putAll(header.getAttributes());
		}
		return map;
	}

	public void reset() {
		cachedAttributes=null;
	}

}
