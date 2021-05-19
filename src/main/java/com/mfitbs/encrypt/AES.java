package com.mfitbs.encrypt;

import com.mfitbs.encrypt.util.IOUtil;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AES implements SymetricEncryption {

    private SecretKeySpec aeskeySpec;

    @SneakyThrows
    @Override
    public void init(byte [] key) {
        aeskeySpec = new SecretKeySpec(key, "AES");
    }

    @SneakyThrows
    @Override
    public void encrypt(InputStream in, OutputStream out) {
        Cipher aesCipher = getAESCipher(Cipher.ENCRYPT_MODE);
        try (CipherOutputStream os = new CipherOutputStream(out, aesCipher)) {
            IOUtil.copy(in, os);
        }
    }

    @SneakyThrows
    private Cipher getAESCipher(int mode) {
        Cipher aesCipher =  Cipher.getInstance("AES/ECB/NoPadding");
        aesCipher.init(mode, aeskeySpec);
        return aesCipher;
    }

    @Override
    public void decrypt(InputStream in, OutputStream out) throws IOException {
        Cipher cipher = getAESCipher(Cipher.DECRYPT_MODE);
        CipherInputStream is = new CipherInputStream(in, cipher);
        IOUtil.copy(is, out);
    }

}
