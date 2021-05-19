package com.mfitbs.encrypt;

import com.mfitbs.encrypt.io.FileOutputStreamFactory;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@RequiredArgsConstructor
public class EncryptFile {

    private final Encrypt encrypt;
    private final FileOutputStreamFactory fileOutputStreamFactory;

    public void encrypt(String in, byte [] pubKey) throws IOException {
        encrypt(new File(in), pubKey);
    }

    public void decrypt(String in, byte [] privKey) throws IOException {
        decrypt(new File(in), privKey);
    }

    public void encrypt(File in, byte [] pubKey) throws IOException {
        try (FileInputStream fis = new FileInputStream(in);
             OutputStream out = fileOutputStreamFactory.create()) {
            encrypt.encrypt(fis, out, pubKey);
        }
    }

    public void decrypt(File in, byte [] privKey) throws IOException {
        try (FileInputStream fis = new FileInputStream(in);
             OutputStream out = fileOutputStreamFactory.create()) {
            encrypt.decrypt(fis, out, privKey);
        }
    }

}
