package com.mfitbs.encrypt;

import lombok.SneakyThrows;

import java.security.*;

import javax.crypto.Cipher;

public class RSA {
    // key encryption algorithms supported - RSA, Diffie-Hellman, DSA
// key pair generator - RSA: keyword - RSA, key size: 1024, 2048
// key pair generator - Diffie-Hellman: keyword i DiffieHellman, key size - 1024
// key pair generator - DSA: keyword - DSA, key size: 1024
// NOTE: using asymmetric algorithms other than RSA needs to be worked out
    protected static String DEFAULT_ENCRYPTION_ALGORITHM = "RSA";
    protected static int DEFAULT_ENCRYPTION_KEY_LENGTH = 1024;
    protected static String DEFAULT_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    protected String mEncryptionAlgorithm, mTransformation;
    protected int mEncryptionKeyLength;

    public RSA() {
        mEncryptionAlgorithm = RSA.DEFAULT_ENCRYPTION_ALGORITHM;
        mEncryptionKeyLength = RSA.DEFAULT_ENCRYPTION_KEY_LENGTH;
        mTransformation = RSA.DEFAULT_TRANSFORMATION;
    }

    public byte[] encryptText(String text, PublicKey mPublicKey) {
        return this.encryptBytes(text.getBytes(), mPublicKey);
    }

    @SneakyThrows
    public byte[] encryptBytes(byte[] bytes, PublicKey mPublicKey) {
        byte[] encryptedText = null;

        KeyPairGenerator kpg = KeyPairGenerator.getInstance(mEncryptionAlgorithm);
        kpg.initialize(mEncryptionKeyLength);

        Cipher cipher = Cipher.getInstance(mTransformation);
        cipher.init(Cipher.PUBLIC_KEY, mPublicKey);

        encryptedText = cipher.doFinal(bytes);

        return encryptedText;
    }

    public String decryptText(byte [] encrypted, PrivateKey mPrivateKey) {
        byte [] decrypted = this.decryptBytes(encrypted, mPrivateKey);
        return new String(decrypted);
    }

    @SneakyThrows
    public byte[] decryptBytes(byte[] encrypted, PrivateKey mPrivateKey) {
        byte[] decrypted = null;

        Cipher cipher = Cipher.getInstance(mTransformation);
        cipher.init(Cipher.PRIVATE_KEY, mPrivateKey);

        decrypted = cipher.doFinal(encrypted);

        return decrypted;
    }
}
