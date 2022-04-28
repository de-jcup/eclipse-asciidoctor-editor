package de.jcup.asciidoctoreditor;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Support {
    
    public static Sha256Support SHARED_INSTANCE = new Sha256Support();
    
    /**
     * Creates a SHA256 checksum for given string
     *
     * @param filepath
     * @return checksum or <code>null</code> when file is not existing
     * @throws IOException
     */
    public String createChecksum(String content) {
        if (content == null) {
            throw new IllegalArgumentException("content may not be null");
        }
        
        MessageDigest md;
        String algorithm = "SHA-256";
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algorithm not supported:" + algorithm);
        }
        md.update(content.getBytes());

        // bytes to hex
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();

    }
}
