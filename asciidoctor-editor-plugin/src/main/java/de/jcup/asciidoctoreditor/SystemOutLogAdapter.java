/*
 * Copyright 2022 Albert Tregnaghi
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

public class SystemOutLogAdapter implements LogAdapter {

    @Override
    public void logInfo(String message) {
        System.out.println("info:" + message);
    }

    @Override
    public void logWarn(String message) {
        System.out.println("warn:" + message);
    }

    @Override
    public void logError(String message, Throwable t) {
        System.err.println("error:" + message);
        if (t != null) {
            t.printStackTrace();
        }
    }

    private long time;

    @Override
    public void resetTimeDiff() {
        time = System.currentTimeMillis();
    }

    @Override
    public void logTimeDiff(String info) {
        System.out.println("timediff:" + info + " - " + (System.currentTimeMillis() - time) + " ms");
    }

}
