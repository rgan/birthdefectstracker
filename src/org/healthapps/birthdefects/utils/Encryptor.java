package org.healthapps.birthdefects.utils;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.modes.PaddedBlockCipher;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;
import org.apache.commons.lang.StringUtils;

public class Encryptor {
    private BufferedBlockCipher cipher;
    private KeyParameter keyParam;
    private static byte[] KEY;

    public Encryptor() {
       this(KEY);
    }

    public Encryptor(byte[] key) {
        cipher = new PaddedBufferedBlockCipher(new DESEngine());
        keyParam = new KeyParameter(key);
    }

    public String encrypt(String value) throws InvalidCipherTextException {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        cipher.init(true, keyParam);
        byte[] input = value.getBytes();
        byte[] out = new byte[cipher.getOutputSize(input.length)];
        int len1 = cipher.processBytes(input, 0, input.length, out, 0);
        cipher.doFinal(out, len1);
        return new String(Hex.encode(out));
    }

    public String decrypt(byte[] input) throws InvalidCipherTextException {
        byte[] out = new byte[input.length];
        cipher.init(false, keyParam);
        int len2 = cipher.processBytes(input, 0, input.length, out, 0);
        cipher.doFinal(out, len2);
        return new String(out);
    }

    public static void setKey(String encryptionKey) {
        KEY = encryptionKey.getBytes();
    }
}
