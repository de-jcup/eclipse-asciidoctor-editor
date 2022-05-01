/*
 * Copyright 2018 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.asciidoctoreditor;

public class EndlessLoopPreventer {

    private int endlessLoopCounter = 0;
    private int maximumLoops;

    public EndlessLoopPreventer(int maximumAllowedLoops) {
        this.maximumLoops = maximumAllowedLoops;
    }

    /**
     * @throws EndlessLoopException when endless loop detected
     */
    public void assertNoEndlessLoop() {
        endlessLoopCounter++;
        if (endlessLoopCounter > maximumLoops) {
            throw new EndlessLoopException(endlessLoopCounter);
        }

    }

    public static class EndlessLoopException extends RuntimeException {

        private static final long serialVersionUID = -6521801245956422758L;

        private EndlessLoopException(int amount) {
            super("Endless loop detected, loop count:" + amount);
        }
    }

}
