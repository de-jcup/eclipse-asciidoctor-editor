/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.asp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import de.jcup.asp.client.AspClientProgressMonitor;

public class AspCompatibleProgressMonitorAdapter implements AspClientProgressMonitor{
    private static final IProgressMonitor NULL_PROGRESS = new NullProgressMonitor();
    private IProgressMonitor monitor;
    
    public AspCompatibleProgressMonitorAdapter(IProgressMonitor monitor) {
        if (monitor==null) {
            monitor=NULL_PROGRESS;
        }
        this.monitor=monitor;
    }
    
    @Override
    public boolean isCanceled() {
        return monitor.isCanceled();
    }

}
