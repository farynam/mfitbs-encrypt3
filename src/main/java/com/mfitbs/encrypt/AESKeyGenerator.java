package com.mfitbs.encrypt;

import lombok.SneakyThrows;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESKeyGenerator implements  SimetricKeyGenerator {

    public static final int AES_Key_Size = 256;


    @SneakyThrows
    @Override
    public byte [] generate() {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(AES_Key_Size);
        SecretKey key = kgen.generateKey();
        byte [] aesKey = key.getEncoded();
        return aesKey;
    }

    @Override
    public int size() {
        return AES_Key_Size;
    }
}
