package com.mfitbs.encrypt.io;

import com.mfitbs.encrypt.OutFile;
import lombok.RequiredArgsConstructor;
import java.io.*;


@RequiredArgsConstructor
public class ChunkedFileOutputStream extends OutputStream {

    private FileOutputStream current;
    private long written;
    private int filesCount;
    private final OutFile outFile;


    @Override
    public void write(int b) throws IOException {
        handleFileCreation();
        current.write(b);
        written++;
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        long writeLeft = len;
        int offset = off;

        while (writeLeft > 0) {
            handleFileCreation();
            int left = placeLeft();
            int writeCount = (int) Math.min(left, writeLeft);
            current.write(b, offset, writeCount);
            offset += writeCount;
            writeLeft -= writeCount;
            written += writeCount;
        }
    }

    @Override
    public void flush() throws IOException {
        current.flush();
    }

    @Override
    public void close() throws IOException {
        current.close();
    }

    private void handleFileCreation() throws IOException {
        if (current == null) {
            current = generateFile();
        } else if (nextFile()) {
            current.close();
            current = generateFile();
        }
    }

    private FileOutputStream generateFile() throws FileNotFoundException {
        filesCount++;
        String nameFileName = outFile.createEncryptedFileNameBase(filesCount);
        return new FileOutputStream(nameFileName);
    }

    private boolean nextFile() {
       return (written % outFile.getChunkSize()) == 0;
    }

    private int placeLeft() {
        return (int) (outFile.getChunkSize() - (written % outFile.getChunkSize()));
    }
}
