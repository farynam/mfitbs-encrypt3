package com.mfitbs.encrypt;

import java.io.*;

public class EncryptFile {

    private final Encrypt encrypt;

    public EncryptFile(Encrypt encrypt) {
        this.encrypt = encrypt;
    }

    public void encrypt(String in, String out, byte [] pubKey) throws IOException {
        encrypt(new File(in), new File(out), pubKey);
    }

    public void decrypt(String in, String out, byte [] privKey) throws IOException {
        decrypt(new File(in), new File(out), privKey);
    }

    public void encrypt(File in, File out, byte [] pubKey) throws IOException {
        try (FileInputStream fis = new FileInputStream(in);
             FileOutputStream fout = new FileOutputStream(out)) {
            encrypt.encrypt(fis, fout, pubKey);
        }
    }

    public void decrypt(File in, File out, byte [] privKey) throws IOException {
        try (FileInputStream fis = new FileInputStream(in);
             FileOutputStream fout = new FileOutputStream(out)) {
            encrypt.decrypt(fis, fout, privKey);
        }
    }
}
