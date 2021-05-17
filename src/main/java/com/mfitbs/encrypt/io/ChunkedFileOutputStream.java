package com.mfitbs.encrypt.io;

import com.mfitbs.encrypt.OutFile;
import lombok.RequiredArgsConstructor;
import java.io.*;
import java.util.UUID;


@RequiredArgsConstructor
public class ChunkedFileOutputStream extends OutputStream {

    final static int UUID_LENGTH_IN_BYTES = 36;

    private FileOutputStream last;
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
            byte [] uuid = generateUUID();
            writeUUID(current, uuid);//end of the last file
            current.close();
            last = current;
            current = generateFile();
            writeUUID(current, uuid);//begining of the new file
        }
    }

    private FileOutputStream generateFile() throws FileNotFoundException {
        filesCount++;
        String nameFileName = outFile.createEncryptedFileNameBase(filesCount);
        return new FileOutputStream(nameFileName);
    }

    private long getBufferSize(boolean singleUUID) {
        int n = singleUUID ? 1 : 2;
        return outFile.getChunkSize() - n * UUID_LENGTH_IN_BYTES;
    }

    private boolean nextFile() {
       return written % getBufferSize(isFirst()) == 0;
    }

    private int placeLeft() {
        long bufferSize = getBufferSize(isFirst());
        return (int) (bufferSize - (written % bufferSize));
    }

    private byte [] generateUUID() {
        return UUID.randomUUID().toString().getBytes();
    }

    private void writeUUID(OutputStream out, byte [] uuid) throws IOException {
        out.write(uuid);
        written += UUID_LENGTH_IN_BYTES;
    }

    private boolean isFirst() {
        return last == null;
    }
 }
