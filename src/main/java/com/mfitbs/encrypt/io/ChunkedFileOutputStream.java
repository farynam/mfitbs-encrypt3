package com.mfitbs.encrypt.io;

import com.mfitbs.encrypt.OutFile;
import com.mfitbs.encrypt.util.IOUtil;
import lombok.RequiredArgsConstructor;
import java.io.*;
import java.util.UUID;


@RequiredArgsConstructor
public class ChunkedFileOutputStream extends OutputStream {

    private FileOutputStream current;
    private long currentWritten;
    private int filesCount;
    private final OutFile outFile;
    private byte [] currentUUID;
    private byte [] lastUUID;
    private boolean closed;
    private long bufferSize;

    @Override
    public void write(int b) throws IOException {
        handleFileCreation();
        current.write(b);
        currentWritten++;
    }

    @Override
    public void write(byte[] b) throws IOException {
        current.write(b, 0, b.length);
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
            currentWritten += writeCount;
        }
    }

    @Override
    public void flush() throws IOException {
        current.flush();
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            writeUUID(current, lastUUID);//write UUID for the last
        }
        current.close();
        closed = true;
    }

    private void handleFileCreation() throws IOException {
        if (current == null) {
            current = generateFile();
            lastUUID = generateUUID();
            writeUUID(current, lastUUID);
            bufferSize = getBufferSize();
        } else if (nextFile()) {
            currentUUID = generateUUID();
            writeUUID(current, currentUUID);//end of the last file
            current.close();
            current = generateFile();
            writeUUID(current, currentUUID);//begining of the new file
        }
    }

    private FileOutputStream generateFile() throws FileNotFoundException {
        currentWritten = 0;
        filesCount++;
        String nameFileName = outFile.createFileNameBase(filesCount);
        return new FileOutputStream(nameFileName);
    }

    private long getBufferSize() {
        return outFile.getChunkSize();
    }

    private boolean nextFile() {
       return placeLeft() == 0;
    }

    private int placeLeft() {
        return (int) (bufferSize - currentWritten - IOUtil.UUID_LENGTH_IN_BYTES);
    }

    private byte [] generateUUID() {
        return UUID.randomUUID().toString().getBytes();
    }

    private void writeUUID(OutputStream out, byte [] uuid) throws IOException {
        out.write(uuid);
        currentWritten += IOUtil.UUID_LENGTH_IN_BYTES;
    }

 }
