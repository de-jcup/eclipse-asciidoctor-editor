package de.jcup.asciidoctoreditor.preferences;

import java.util.Objects;

public class KeyValue {

    private String key;
    private String value;

    public KeyValue(String key, String value) {
        setKey(key);
        setValue(value);
    }

    public void setKey(String key) {
        if (key == null) {
            key = "";
        }
        this.key = key;
    }

    public void setValue(String value) {
        if (value == null) {
            value = "";
        }
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof KeyValue)) {
            return false;
        }
        KeyValue other = (KeyValue) obj;
        return Objects.equals(key, other.key) && Objects.equals(value, other.value);
    }
}
