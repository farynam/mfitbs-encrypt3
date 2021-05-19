package com.mfitbs.encrypt;

import com.mfitbs.encrypt.OutFile;
import com.mfitbs.encrypt.io.ChunkedFileOutputStream;
import com.mfitbs.encrypt.util.IOUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestChunkedOutput {
    final int numWrites = 10;
    final int bufSize = 1024;

    @Test
    public void test() throws IOException {

        OutFile outFile = new OutFile("test/out/",
                "file",
                1024l
        );

        File outFolder = new File(outFile.getOutFileFolder());

        try (ChunkedFileOutputStream chunkedFileOutputStream =
                     new ChunkedFileOutputStream(outFile)) {

            int n = numWrites;
            while (n > 0) {
                chunkedFileOutputStream.write(new byte[bufSize], 0, bufSize);
                n--;
            }
        }

        assertEquals(numWrites + 1, outFolder.listFiles().length);
        checkFilesSize(outFolder, outFile.getChunkSize());
        checkLastFileSize(outFolder);
    }

    private void checkFilesSize(File dir, long limit) {
        List<File> files = getSorted(dir);
        File last = files.get(files.size() - 1);

        files.remove(files.size() - 1);

        files.forEach((f) -> {
            assertEquals(limit, f.length(), "File length differ");
        });

        assertNotEquals(last.length(), 0, "List size was empty");
    }

    private List<File> getSorted(File dir) {
        List<File> files = new ArrayList<>(Arrays.asList(dir.listFiles()));
        files.sort(Comparator.comparingLong(File::length).reversed());
        return files;
    }

    private File getLast(File dir) {
        List<File> files = getSorted(dir);
        return files.get(files.size() -1);
    }

    private void checkLastFileSize(File outFolder) {
        long totalData = bufSize * numWrites;
        long totalSize = totalData + numWrites * IOUtil.UUID_LENGTH_IN_BYTES * 2;
        long lastSize = totalSize % bufSize + IOUtil.UUID_LENGTH_IN_BYTES * 2;

        assertEquals(lastSize , getLast(outFolder).length());
    }
}
