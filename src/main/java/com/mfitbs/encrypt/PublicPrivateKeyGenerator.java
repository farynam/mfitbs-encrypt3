package com.mfitbs.encrypt;

import lombok.SneakyThrows;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


public class PublicPrivateKeyGenerator {

    public static final String DEFAULT = RSA.DEFAULT_ENCRYPTION_ALGORITHM;

    @SneakyThrows
    public KeyPair generate(String mEncryptionAlgorithm) {
        mEncryptionAlgorithm = mEncryptionAlgorithm == null ? DEFAULT : mEncryptionAlgorithm;

        KeyPairGenerator kpg = KeyPairGenerator.getInstance(mEncryptionAlgorithm);

        KeyPair keyPair = kpg.generateKeyPair();

        return keyPair;
    }

    @SneakyThrows
    public static PublicKey createPublic(byte [] pubBytes) {
        return KeyFactory.getInstance("RSA")
                        .generatePublic(new X509EncodedKeySpec(pubBytes));
    }

    @SneakyThrows
    public static PrivateKey createPrivate(byte [] privBytes) {
        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(privBytes));
    }


    public KeyPair generate() {
        return generate(null);
    }

    public byte[] getPublicKeyAsByteArray(KeyPair keyPair) {
        return keyPair.getPublic().getEncoded();
    }


    public byte[] getPrivateKeyAsByteArray(KeyPair keyPair)
    {
        return keyPair.getPrivate().getEncoded();
    }

}
