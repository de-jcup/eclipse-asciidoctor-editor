package de.jcup.asciidoctor.editor.plantuml;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

/**
 * Just a simple java code generator for plantuml keywords.
 * See http://plantuml.com/developers for more information
 * @author albert
 *
 */
public class PlantUMLKeywordsGenerator {

	public static void main(String[] args) throws IOException {
		new PlantUMLKeywordsGenerator().generate();
	}

	private void generate() throws IOException {
		String template = FileUtils.readFileToString(new File("./src/main/resources/PlantUML__ID__DocumentKeywords.template"));
		Map<String, Set<String>> map = readPlantumlLanguageToMap();
		
		for(String key: map.keySet()){
			generateFile(template, key,map.get(key));
		}
		
	}

	private void generateFile(String template, String key, Set<String> list) throws IOException {
		String content = new StringBuilder().append(template).toString();
		
		String firstUpper = key.substring(0, 1).toUpperCase()+key.substring(1);
		String filename = "PlantUML"+firstUpper+"DocumentKeywords.java";
		content = content.replaceAll("__ID__", firstUpper);
		
		StringBuilder replacement = new StringBuilder(); 
		for (String line: list){
			replacement.append("\n          ");
			replacement.append(createJavaEnumIdentifier(line));
			replacement.append("(\"");
			replacement.append(line);
			replacement.append("\"),");
					
		}
		content = content.replaceAll("__GENERATED_CONTENT__", replacement.toString());
		content=content.replaceAll("__GENERATED_TOOLTIP__", "This is a keyword representing a '"+key+"' in plantuml. Please refer to online documentation for more information");
		String parentFolder ="./../asciidoctor-editor-plugin/src/main/java/";
		String thePackage="de/jcup/asciidoctoreditor/document/keywords";
		String path = parentFolder+thePackage;
		
		File file = new File(path,filename);
		FileUtils.writeStringToFile(file,content);
//		System.out.println(content);
		System.out.println("generated:"+file);
	}

	private String createJavaEnumIdentifier(String line) {
		String enumname = line.toUpperCase();
		enumname=enumname.replaceAll("!", "NOT_");
		enumname=enumname.replaceAll("@", "ATSIGN_");
		enumname=enumname.replaceAll(" ", "_");
		return enumname;
	}

	private Map<String,Set<String>> readPlantumlLanguageToMap() throws IOException {
		String text = FileUtils.readFileToString(new File("./src/main/resources/plantuml.language.txt"));
		String[] parts = text.split("\n");
		
		Map<String,Set<String>> map= new TreeMap<>(); 
		for (Iterator<String> it= Arrays.asList(parts).iterator();it.hasNext();){
			String line = it.next();
			if (line.equals(";EOF")){
				continue;
			}
			if(line.startsWith(";")){
				String key = line.substring(1); // e.g. ;color
				String expectedSizeStr = it.next().substring(1); // e.g ;47
				int expectedSize = Integer.parseInt(expectedSizeStr);
				Set<String> list = new TreeSet<>();
				
				while (it.hasNext()){
					String next = it.next();
					if (next.isEmpty()){
						break;
					}
					list.add(next);
				}
				if (expectedSize!=list.size()){
					throw new IllegalStateException("not expected size:"+expectedSize+", but "+list.size()+"\ncontent:\n"+list);
				}
				map.put(key, list);
			}
		}
		System.out.println("found:");	
		System.out.println(map.keySet());
		return map;
	}
}
