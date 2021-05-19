package com.mfitbs.encrypt.util;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.*;
import java.util.Arrays;
import java.util.UUID;

public abstract class IOUtil {

    final static String PUB_KEY = "PUBLIC KEY";
    final static String PRIV_KEY = "PRIVATE KEY";

    public final static int UUID_LENGTH_IN_BYTES = 36;

    public static void copy(InputStream is, OutputStream os) throws IOException {
        int i;
        byte[] b = new byte[1024];
        while ((i = is.read(b)) != -1) {
            os.write(b, 0, i);
        }
        os.flush();
    }

    public static byte [] readBytesCount(InputStream is, int count) throws IOException {

        byte [] buf = new byte[count];
        int read = is.read(buf, 0, count);
        if (read != count) {
            throw new IOException("cannot read " + count + " bytes");
        }

        return buf;
    }

    public static byte [] readLastBytesCount(InputStream is, int count) throws IOException {
        byte [] all = is.readAllBytes();
        return  Arrays.copyOfRange(all, all.length - count, count);
    }

    public static void writetofile(String fileName, byte [] bytes) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(bytes);
            fos.flush();
        }
    }


    public static byte [] readFile(String fileName) throws IOException {
        try (FileInputStream fin = new FileInputStream(new File(fileName));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            copy(fin, out);
            out.flush();
            return out.toByteArray();
        }
    }

    public static byte [] readPEM(String fileName) throws IOException {
        try (FileInputStream fis = new FileInputStream(fileName);
             InputStreamReader isr = new InputStreamReader(fis);
             PemReader pemReader = new PemReader(isr)
        ) {
            PemObject pem = pemReader.readPemObject();
            return pem.getContent();
        }
    }

    public static void writePEM(String fileName, byte [] bytes, String type) throws IOException {
        try (FileOutputStream out = new FileOutputStream(fileName)) {
            PemWriter pemWriter = new PemWriter(new OutputStreamWriter(out));
            pemWriter.writeObject(new PemObject(type, bytes));
            pemWriter.flush();
        }
    }

    public static UUID readID(InputStream is) throws IOException {
        return UUID.nameUUIDFromBytes(readBytesCount(is, UUID_LENGTH_IN_BYTES));
    }

    public static UUID readID(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return readID(fileInputStream);
        }
    }

    public static UUID readRef(InputStream is) throws IOException {
        return UUID.nameUUIDFromBytes(readBytesCount(is, UUID_LENGTH_IN_BYTES));
    }

    public static UUID readRef(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return readRef(fileInputStream);
        }
    }
}
