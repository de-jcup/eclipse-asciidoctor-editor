/*
 * Copyright 2021 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.preferences;

import de.jcup.asciidoctoreditor.KeyValue;
import de.jcup.eclipse.commons.preferences.AbstractPreferenceValueConverter;

public class KeyValueConverter extends AbstractPreferenceValueConverter<KeyValue> {

    public static final KeyValueConverter DEFAULT = new KeyValueConverter();

    @Override
    protected void write(KeyValue oneEntry, de.jcup.eclipse.commons.preferences.PreferenceDataWriter writer) {
        writer.writeString(oneEntry.getKey());
        writer.writeString(oneEntry.getValue());
    }

    @Override
    protected KeyValue read(de.jcup.eclipse.commons.preferences.PreferenceDataReader reader) {
        KeyValue definition = new KeyValue(reader.readString(), reader.readString());
        return definition;
    }

}