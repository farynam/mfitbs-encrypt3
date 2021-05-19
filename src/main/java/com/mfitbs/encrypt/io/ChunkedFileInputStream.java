package com.mfitbs.encrypt.io;

import com.mfitbs.encrypt.util.IOUtil;
import org.apache.commons.lang3.NotImplementedException;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ChunkedFileInputStream extends InputStream {

    private List<File> files;
    private InputStream current;
    private UUID end;
    private File currentFile;
    private long currentRead;
    private long currentSize;
    private Optional<BiConsumer<File, UUID>> nextFileOpenedConsumer;


    public void init(String file) throws IOException {
        init(new File(file));
    }

    public void init(File file) throws IOException {
        files = Arrays.stream(file.getParentFile().listFiles())
                .collect(Collectors.toList());
        current = new FileInputStream(file);
        end = IOUtil.readID(current);
        updateCurrentFileInfo(file);
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
        return 0;
    }

    public void setNextFileOpenedConsumer(BiConsumer<File, UUID> nextFileOpenedConsumer) {
        this.nextFileOpenedConsumer = Optional.ofNullable(nextFileOpenedConsumer);
    }

    private long getCurrentSize() {
        return currentFile.length();
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
            if (IOUtil.readID(is).equals(next)) {
                it.remove();
                current.close();
                current = is;
                updateCurrentFileInfo(f);

                nextFileOpenedConsumer.ifPresent(c -> c.accept(f, next));
                return;
            }
            is.close();
        }
        throw new IllegalStateException("missing file with UUID" + next);
    }

    private void updateCurrentFileInfo(File file) {
        currentRead = IOUtil.UUID_LENGTH_IN_BYTES;
        currentFile = file;
        currentSize = file.length();
    }
}
