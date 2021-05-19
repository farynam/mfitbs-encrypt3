package com.mfitbs.encrypt;

import lombok.SneakyThrows;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class AESKeyGenerator implements  SimetricKeyGenerator {

    public static final int AES_Key_Size = 256;//bites


    @SneakyThrows
    @Override
    public byte [] generate() {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(AES_Key_Size);
        SecretKey key = kgen.generateKey();
        return key.getEncoded();
    }

    @Override
    public int size() {
        return 32; //bytes
    }
}
