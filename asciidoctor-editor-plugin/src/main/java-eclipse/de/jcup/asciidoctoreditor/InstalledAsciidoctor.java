package de.jcup.asciidoctoreditor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.DirectoryWalker;
import org.asciidoctor.Options;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.DocumentHeader;
import org.asciidoctor.ast.StructuredDocument;
import org.asciidoctor.converter.JavaConverterRegistry;
import org.asciidoctor.extension.ExtensionGroup;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.asciidoctor.extension.RubyExtensionRegistry;

import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;

/**
 * Special variant of an Asciidoctor instance - uses native installation. But it
 * works ony with the editor, because not all parts are implemented...
 * 
 * @author Albert Tregnaghi
 *
 */
public class InstalledAsciidoctor implements Asciidoctor {

	
	/*
	 * -------------------------------------------------------------------------
	 * +++++++++++++++++++++++++++++++ used++ ++++++++++++++++++++++++++++++++++
	 * -------------------------------------------------------------------------
	 */

	@Override
	public DocumentHeader readDocumentHeader(File filename) {
		return AsciiDoctorOSGIWrapper.INSTANCE.getAsciidoctor().readDocumentHeader(filename);
	}

	@Override
	public String convertFile(File filename, Map<String, Object> options) {

		List<String> commands = buildCommands(filename, options);
		String commandLineString = createCommandLineString(commands);
		
		ProcessBuilder pb = new ProcessBuilder(commands);
		AsciiDoctorConsoleUtil.output(">> rendering:"+filename.getName());
		try {
			StringBuffer lineStringBuffer=null;
			Process process = pb.start();
			try (InputStream is = process.getErrorStream();) {
				int c;
				lineStringBuffer = new StringBuffer();
				while ((c = is.read()) != -1) {
					lineStringBuffer.append((char) c);
				}
				String line = lineStringBuffer.toString();
				if (line.isEmpty()){
					AsciiDoctorConsoleUtil.output(line);
				}else{
					AsciiDoctorConsoleUtil.error(line);
				}
			}
			boolean exitdone = process.waitFor(2, TimeUnit.MINUTES);
			int exitCode =-1;
			if (exitdone){
				exitCode=process.exitValue();
			}
			if (EclipseDevelopmentSettings.DEBUG_LOGGING_ENABLED){
				AsciiDoctorConsoleUtil.output("Called:" + commandLineString);
				AsciiDoctorConsoleUtil.output("Exitcode:"+exitCode);
			}
			if (exitCode>0){
				AsciiDoctorEclipseLogAdapter.INSTANCE.logWarn("Installed Asciidoctor rendering failed for '"+filename.getName()+"'\n\nCommandLine was:\n"+commandLineString+"\n\nResulted in exitcode:"+exitCode+", \nLast output:"+lineStringBuffer);
				throw new InstalledAsciidoctorException("FAILED - Asciidoctor exitcode:"+exitCode+" - last output:"+lineStringBuffer);
				
			}
		} catch (Exception e) {
			if (e instanceof InstalledAsciidoctorException){
				InstalledAsciidoctorException iae = (InstalledAsciidoctorException) e;
				throw iae; // already an exception from installed asciidoctor so just re-throw
			}else{
				AsciiDoctorEditorUtil.logError("Cannot execute installed asciidoctor\n\nCommandline was:\n"+commandLineString, e);
				throw new InstalledAsciidoctorException("FAILED - Installed Asciidoctor instance was not executable, reason:"+e.getMessage());
			}
		}

		return null;
	}

	protected String createCommandLineString(List<String> commands) {
		StringBuilder commandLine = new StringBuilder();
		for (String command : commands) {
			commandLine.append(command);
			commandLine.append(" ");
		}
		String commandLineString = commandLine.toString();
		return commandLineString;
	}

	protected List<String> buildCommands(File filename, Map<String, Object> options) {
		List<String> commands = new ArrayList<String>();
		if (OSUtil.isWindows()) {
			commands.add("cmd.exe");
			commands.add("/C");
		}
		String asciidoctorCall = createAsciidoctorCall();
		commands.add(asciidoctorCall);

		String outDir = null;

		@SuppressWarnings("unchecked")
		Map<String, String> attributes = (Map<String, String>) options.get("attributes");
		for (String key : attributes.keySet()) {
			Object value = attributes.get(key);
			if (value == null) {
				continue;
			}
			String v = value.toString();
			String attrib = key;
			if (v.isEmpty()) {
				continue;
			}
			commands.add("-a");
			String safeValue = toWindowsSafeVariant(value);
			if (key.equals("outdir")) {
				outDir = safeValue;
			}
			attrib += "=" + safeValue;
			commands.add(attrib);
		}

		String argumentsForInstalledAsciidoctor = AsciiDoctorEditorPreferences.getInstance()
				.getArgumentsForInstalledAsciidoctor();
		List<String> preferenceCLICommands = CLITextUtil.convertToList(argumentsForInstalledAsciidoctor);
		commands.addAll(preferenceCLICommands);
		if (outDir != null) {
			commands.add("-D");
			commands.add(outDir);
		}

		commands.add(toWindowsSafeVariant(filename.getAbsolutePath()));
		return commands;
	}

	protected String createAsciidoctorCall() {
		StringBuilder sb = new StringBuilder();
		String path =AsciiDoctorEditorPreferences.getInstance().getPathToInstalledAsciidoctor();
		if (path!=null && !path.trim().isEmpty()){
			sb.append(path);
			if (! path.endsWith(File.separator)){
				sb.append(File.separator);
			}
		}
		sb.append("asciidoctor");
		String callPath = sb.toString();
		return callPath;
	}
	
	private String toWindowsSafeVariant(Object obj) {
		String command = "" + obj;
		boolean windowsPath = command.indexOf('\\') != -1;
		if (!windowsPath) {
			return command;
		}
		return "\"" + command + "\"";
	}

	/*
	 * -------------------------------------------------------------------------
	 * +++++++++++++++++++++++++++++++ unused ++++++++++++++++++++++++++++++++++
	 * -------------------------------------------------------------------------
	 */

	@Override
	public String render(String content, Map<String, Object> options) {
		throw new NotImplementedException();

	}

	@Override
	public String render(String content, Options options) {
		throw new NotImplementedException();

	}

	@Override
	public String render(String content, OptionsBuilder options) {
		throw new NotImplementedException();

	}

	@Override
	public void render(Reader contentReader, Writer rendererWriter, Map<String, Object> options) throws IOException {
		throw new NotImplementedException();

	}

	@Override
	public void render(Reader contentReader, Writer rendererWriter, Options options) throws IOException {
		throw new NotImplementedException();

	}

	@Override
	public void render(Reader contentReader, Writer rendererWriter, OptionsBuilder options) throws IOException {
		throw new NotImplementedException();

	}

	@Override
	public String renderFile(File filename, Map<String, Object> options) {
		throw new NotImplementedException();

	}

	@Override
	public String renderFile(File filename, Options options) {
		throw new NotImplementedException();

	}

	@Override
	public String renderFile(File filename, OptionsBuilder options) {
		throw new NotImplementedException();

	}

	@Override
	public String[] renderDirectory(DirectoryWalker directoryWalker, Map<String, Object> options) {
		throw new NotImplementedException();

	}

	@Override
	public String[] renderDirectory(DirectoryWalker directoryWalker, Options options) {
		throw new NotImplementedException();

	}

	@Override
	public String[] renderDirectory(DirectoryWalker directoryWalker, OptionsBuilder options) {
		throw new NotImplementedException();

	}

	@Override
	public String[] renderFiles(Collection<File> asciidoctorFiles, Map<String, Object> options) {
		throw new NotImplementedException();

	}

	@Override
	public String[] renderFiles(Collection<File> asciidoctorFiles, Options options) {
		throw new NotImplementedException();

	}

	@Override
	public String[] renderFiles(Collection<File> asciidoctorFiles, OptionsBuilder options) {
		throw new NotImplementedException();

	}

	@Override
	public String convert(String content, Map<String, Object> options) {
		throw new NotImplementedException();

	}

	@Override
	public String convert(String content, Options options) {
		throw new NotImplementedException();

	}

	@Override
	public String convert(String content, OptionsBuilder options) {
		throw new NotImplementedException();

	}

	@Override
	public void convert(Reader contentReader, Writer rendererWriter, Map<String, Object> options) throws IOException {
		throw new NotImplementedException();

	}

	@Override
	public void convert(Reader contentReader, Writer rendererWriter, Options options) throws IOException {
		throw new NotImplementedException();

	}

	@Override
	public void convert(Reader contentReader, Writer rendererWriter, OptionsBuilder options) throws IOException {
		throw new NotImplementedException();

	}

	@Override
	public String convertFile(File filename, Options options) {
		throw new NotImplementedException();

	}

	@Override
	public String convertFile(File filename, OptionsBuilder options) {
		throw new NotImplementedException();

	}

	@Override
	public String[] convertDirectory(DirectoryWalker directoryWalker, Map<String, Object> options) {
		throw new NotImplementedException();

	}

	@Override
	public String[] convertDirectory(DirectoryWalker directoryWalker, Options options) {
		throw new NotImplementedException();

	}

	@Override
	public String[] convertDirectory(DirectoryWalker directoryWalker, OptionsBuilder options) {
		throw new NotImplementedException();

	}

	@Override
	public String[] convertFiles(Collection<File> asciidoctorFiles, Map<String, Object> options) {
		throw new NotImplementedException();

	}

	@Override
	public String[] convertFiles(Collection<File> asciidoctorFiles, Options options) {
		throw new NotImplementedException();

	}

	@Override
	public String[] convertFiles(Collection<File> asciidoctorFiles, OptionsBuilder options) {
		throw new NotImplementedException();

	}

	@Override
	public StructuredDocument readDocumentStructure(File filename, Map<String, Object> options) {
		throw new NotImplementedException();

	}

	@Override
	public StructuredDocument readDocumentStructure(String content, Map<String, Object> options) {
		throw new NotImplementedException();

	}

	@Override
	public StructuredDocument readDocumentStructure(Reader contentReader, Map<String, Object> options) {
		throw new NotImplementedException();

	}

	@Override
	public DocumentHeader readDocumentHeader(String content) {
		throw new NotImplementedException();

	}

	@Override
	public DocumentHeader readDocumentHeader(Reader contentReader) {
		throw new NotImplementedException();

	}

	@Override
	public void requireLibrary(String... requiredLibraries) {
		throw new NotImplementedException();

	}

	@Override
	public void requireLibraries(Collection<String> requiredLibraries) {
		throw new NotImplementedException();

	}

	@Override
	public JavaExtensionRegistry javaExtensionRegistry() {
		throw new NotImplementedException();

	}

	@Override
	public RubyExtensionRegistry rubyExtensionRegistry() {
		throw new NotImplementedException();

	}

	@Override
	public JavaConverterRegistry javaConverterRegistry() {
		throw new NotImplementedException();

	}

	@Override
	public ExtensionGroup createGroup() {
		throw new NotImplementedException();

	}

	@Override
	public ExtensionGroup createGroup(String groupName) {
		throw new NotImplementedException();

	}

	@Override
	public void unregisterAllExtensions() {
		throw new NotImplementedException();

	}

	@Override
	public void shutdown() {
		throw new NotImplementedException();

	}

	@Override
	public String asciidoctorVersion() {
		return "installed version";
	}

	@Override
	public Document load(String content, Map<String, Object> options) {
		throw new NotImplementedException();
	}

	@Override
	public Document loadFile(File file, Map<String, Object> options) {
		throw new NotImplementedException();
	}

	public static class NotImplementedException extends RuntimeException {

		private static final long serialVersionUID = 1L;

	}
}
