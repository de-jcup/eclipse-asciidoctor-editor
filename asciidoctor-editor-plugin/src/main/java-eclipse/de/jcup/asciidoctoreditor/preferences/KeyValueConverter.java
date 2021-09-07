package de.jcup.asciidoctoreditor.preferences;

import de.jcup.eclipse.commons.preferences.AbstractPreferenceValueConverter;

public class KeyValueConverter extends AbstractPreferenceValueConverter<KeyValue>{
    
    public static final KeyValueConverter DEFAULT = new KeyValueConverter();

	@Override
	protected void write(KeyValue oneEntry,
			de.jcup.eclipse.commons.preferences.PreferenceDataWriter writer) {
		writer.writeString(oneEntry.getKey());
		writer.writeString(oneEntry.getValue());
	}

	@Override
	protected KeyValue read(
			de.jcup.eclipse.commons.preferences.PreferenceDataReader reader) {
	    KeyValue definition = new KeyValue(reader.readString(),reader.readString());
		return definition;
	}
	

}