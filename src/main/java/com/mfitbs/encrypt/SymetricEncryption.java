package com.mfitbs.encrypt;

import lombok.SneakyThrows;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SymetricEncryption {

    void init(byte [] key);

    void encrypt(InputStream in, OutputStream out);

    void decrypt(InputStream in, OutputStream out) throws IOException;
}
