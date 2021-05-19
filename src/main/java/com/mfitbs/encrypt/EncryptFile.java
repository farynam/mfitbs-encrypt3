package com.mfitbs.encrypt;

import com.mfitbs.encrypt.io.AuditedFileInputStream;
import com.mfitbs.encrypt.io.ChunkedFileInputStream;
import com.mfitbs.encrypt.io.ChunkedFileOutputStream;
import lombok.RequiredArgsConstructor;

import java.io.*;

@RequiredArgsConstructor
public class EncryptFile {

    private final Encrypt encrypt;
    private final OutFile outFile;
    private final boolean chunked;

    public void encrypt(String in, byte [] pubKey) throws IOException {
        encrypt(new File(in), pubKey);
    }

    public void decrypt(String in, byte [] privKey) throws IOException {
        decrypt(new File(in), privKey);
    }

    public void encrypt(File in, byte [] pubKey) throws IOException {
        try (FileInputStream fis = new AuditedFileInputStream(in);
             OutputStream out = getOutputStream(outFile)) {
            encrypt.encrypt(fis, out, pubKey);
        }
    }

    public void decrypt(File in, byte [] privKey) throws IOException {
        try (InputStream fis = getInputStream(in);
             OutputStream out = new FileOutputStream(outFile.createFileNameBase())) {
            encrypt.decrypt(fis, out, privKey);
        }
    }

    private OutputStream getOutputStream(OutFile outFile) throws FileNotFoundException {
        if (chunked) {
            return new ChunkedFileOutputStream(outFile);
        }
        return new FileOutputStream(outFile.createFileNameBase());
    }

    private InputStream getInputStream(File fileIn) throws IOException {
        if (chunked) {
            ChunkedFileInputStream is = new ChunkedFileInputStream();
            is.init(fileIn);
            return is;
        }
        return new FileInputStream(fileIn);
    }
}
