package com.mfitbs.encrypt.io;

import java.io.*;
import java.nio.channels.FileChannel;

public class AuditedFileInputStream extends FileInputStream {
    private long totalDataRead;
    private long skipped;

    public AuditedFileInputStream(String name) throws FileNotFoundException {
        super(name);
    }

    public AuditedFileInputStream(File file) throws FileNotFoundException {
        super(file);
    }

    public AuditedFileInputStream(FileDescriptor fdObj) {
        super(fdObj);
    }

    @Override
    public int read() throws IOException {
        return saveRead(super.read());
    }

    @Override
    public int read(byte[] b) throws IOException {
        return saveRead(super.read(b));
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return saveRead(super.read(b, off, len));
    }

    @Override
    public long skip(long n) throws IOException {
        return saveSkipped(super.skip(n));
    }

    @Override
    public int available() throws IOException {
        return super.available();
    }

    @Override
    public void close() throws IOException {
        System.out.println(this.getClass().toString());
        System.out.printf("Total data read:%d\n", totalDataRead);
        System.out.printf("Total data skipped:%d\n", skipped);

        super.close();
    }

    @Override
    public FileChannel getChannel() {
        return super.getChannel();
    }

    private int saveRead(int read) {
        totalDataRead += Math.max(read, 0);
        return read;
    }

    private long saveSkipped(long skipped) {
        this.skipped += skipped;
        return skipped;
    }
}
