package org.healthapps.birthdefects.utils;

import junit.framework.TestCase;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.util.encoders.Hex;

public class EncryptorTest extends TestCase {
    private static final String text = "this is a test";

    public void testShouldEncrypt() throws InvalidCipherTextException {
        final Encryptor encryptor = new Encryptor("somekey8".getBytes());
        String encrypted = encryptor.encrypt(text);
        final String decrytedValue = encryptor.decrypt(Hex.decode(encrypted));
        assertEquals(text, decrytedValue.trim());
    }
}
