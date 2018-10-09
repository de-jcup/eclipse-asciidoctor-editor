package de.jcup.asciidoctoreditor;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class CLITextUtilTest {

	
	@Test
	public void alpha_newline_omega_results_in_two_entries() {
		List<String> commands = CLITextUtil.convertToList("alpha\nomega");
		assertContains(commands, "alpha","omega");
	}
	@Test
	public void newline_alpha_newline_omega_results_in_two_entries() {
		List<String> commands = CLITextUtil.convertToList("\nalpha\nomega");
		assertContains(commands, "alpha","omega");
	}
	
	@Test
	public void alpha_newline_omega_newline_results_in_two_entries() {
		List<String> commands = CLITextUtil.convertToList("alpha\nomega\n");
		assertContains(commands, "alpha","omega");
	}
	@Test
	public void alpha_newline_newline_omega_results_in_two_entries() {
		List<String> commands = CLITextUtil.convertToList("alpha\n\nomega");
		assertContains(commands, "alpha","omega");
	}
	
	@Test
	public void alpha_gamma_newline_newline_omega_results_in_three_entries() {
		List<String> commands = CLITextUtil.convertToList("alpha gamma\n\nomega");
		assertContains(commands, "alpha","gamma","omega");
	}
	
	@Test
	public void _5spaces_alpha_gamma_newline_newline_omega_results_in_three_entries() {
		List<String> commands = CLITextUtil.convertToList("    alpha gamma\n\nomega");
		assertContains(commands, "alpha","gamma","omega");
	}
	@Test
	public void _5spaces_alpha_2tabs_gamma_newline_newline_omega_results_in_three_entries() {
		List<String> commands = CLITextUtil.convertToList("    alpha		gamma\n\nomega");
		assertContains(commands, "alpha","gamma","omega");
	}
	
	@Test
	public void realWorld_example() {
		List<String> commands = CLITextUtil.convertToList("-r asciidoctor-diagram -r asciidoctor-pdf\n-r asciidoctor-rouge -a source-highlighter=rouge");
		assertContains(commands, "-r","asciidoctor-diagram","-r","asciidoctor-pdf","-r","asciidoctor-rouge","-a","source-highlighter=rouge");
	}
	

	@Test
	public void null_is_empty_list() {
		List<String> commands = CLITextUtil.convertToList(null);
		
		assertNotNull(commands);
		assertTrue(commands.isEmpty());
	}
	
	@Test
	public void spaces_string_is_empty_list() {
		List<String> commands = CLITextUtil.convertToList("    ");
		
		assertNotNull(commands);
		assertTrue(commands.isEmpty());
	}
	
	@Test
	public void emty_string_is_empty_list() {
		List<String> commands = CLITextUtil.convertToList("");
		
		assertNotNull(commands);
		assertTrue(commands.isEmpty());
	}
	
	private void assertContains(List<String> commands, String ...expected){
		assertEquals(Arrays.asList(expected),commands);
	}

}
