package de.jcup.asciidoctoreditor;

public class EndlessLoopPreventer {

	private int endlessLoopCounter =0;
	private int maximumLoops;
	
	public EndlessLoopPreventer(int maximumAllowedLoops){
		this.maximumLoops=maximumAllowedLoops;
	}

	/**
	 * @throws EndlessLoopException when endless loop detected
	 */
	public void assertNoEndlessLoop() {
		endlessLoopCounter++;
		if (endlessLoopCounter>maximumLoops){
			throw new EndlessLoopException(endlessLoopCounter);
		}
		
	}
	
	public static class EndlessLoopException extends RuntimeException{
		
		private static final long serialVersionUID = -6521801245956422758L;

		private EndlessLoopException(int amount){
			super("Endless loop detected, loop count:"+amount);
		}
	}
	
	
}
