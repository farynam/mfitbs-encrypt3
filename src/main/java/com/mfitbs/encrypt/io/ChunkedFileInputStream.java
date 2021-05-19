package com.mfitbs.encrypt.io;

import com.mfitbs.encrypt.util.IOUtil;
import org.apache.commons.lang3.NotImplementedException;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ChunkedFileInputStream extends InputStream {

    private List<File> files;
    private InputStream current;
    //private UUID currentUUID;
    private UUID end;
    private long currentRead;
    private long currentSize;
    private BiConsumer<File, UUID> nextFileOpenedConsumer;

    private long totalDataRead;
    private long totalMetaRead;

    public void init(String file) throws IOException {
        init(new File(file));
    }

    public void init(File file) throws IOException {
        files = Arrays.stream(file.getParentFile().listFiles())
                .collect(Collectors.toList());
        files.remove(file);
        current = new FileInputStream(file);
        end = IOUtil.readID(current);
        invokeEvent(file, end);
        updateCurrentFileInfo(file);
        updateTotalMetaRead();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int left = len;

        while (left > 0) {
            if (isAtTheEndOfFile()) {
                UUID next = IOUtil.readRef(current);
                updateTotalMetaRead();
                if (isTheEnd(next)) {
                    return -1;
                }
                openNew(next);
            }

            long allowed = getAllowedToRead();
            long toRead = Math.min(left, allowed);
            int read = current.read(b, off, (int) toRead);

            if (read == -1) {
                throw new IllegalStateException();
            }

            left -= read;
            off += read;
            currentRead += read;
            totalDataRead += read;
        }

        return len;
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public long skip(long n) throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public int available() throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public void close() throws IOException {
        System.out.println(this.getClass().toString());
        System.out.printf("total data read:%d\n", totalDataRead);
        System.out.printf("total meta data read:%d\n", totalMetaRead);
        current.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        throw new NotImplementedException();
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public boolean markSupported() {
        throw new NotImplementedException();
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public int read() throws IOException {
        throw new NotImplementedException();
    }

    public void setNextFileOpenedConsumer(BiConsumer<File, UUID> nextFileOpenedConsumer) {
        this.nextFileOpenedConsumer = nextFileOpenedConsumer;
    }

    private long getAllowedToRead() {
        return currentSize - IOUtil.UUID_LENGTH_IN_BYTES - currentRead;
    }

    private boolean isAtTheEndOfFile() {
        return (currentSize - currentRead) == IOUtil.UUID_LENGTH_IN_BYTES;
    }

    private boolean isTheEnd(UUID id) {
        return id.equals(end);
    }

    private void openNew(UUID next) throws IOException {
        Iterator<File> it = files.iterator();
        while (it.hasNext()) {
            File f = it.next();
            InputStream is = new FileInputStream(f);
            UUID nextFileID = IOUtil.readID(is);
            if (nextFileID.equals(next) && !nextFileID.equals(end)) {
                it.remove();
                current.close();
                current = is;
                updateCurrentFileInfo(f);
                updateTotalMetaRead();
                invokeEvent(f, next);
                return;
            }
            is.close();
        }
        throw new IllegalStateException("missing file with UUID" + next);
    }

    private void updateCurrentFileInfo(File file) {
        currentRead = IOUtil.UUID_LENGTH_IN_BYTES;
        currentSize = file.length();
    }

    private void invokeEvent(File file, UUID uuid) {
        if (nextFileOpenedConsumer != null) {
            nextFileOpenedConsumer.accept(file, uuid);
        }
    }

    private void updateTotalMetaRead() {
        totalMetaRead += IOUtil.UUID_LENGTH_IN_BYTES;
    }

    public long getTotalDataRead() {
        return totalDataRead;
    }

    public long getTotalMetaRead() {
        return totalMetaRead;
    }
}
