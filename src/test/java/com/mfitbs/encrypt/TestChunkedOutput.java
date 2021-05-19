package com.mfitbs.encrypt;

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
    final String testData = "Ala ma kota";

    @Test
    public void test() throws IOException {

        OutFile outFile = new OutFile("test/testOutput/out/",
                "file",
                1024l
        );

        File outFolder = new File(outFile.getOutFileFolder());

        try (ChunkedFileOutputStream chunkedFileOutputStream =
                     new ChunkedFileOutputStream(outFile)) {

            int n = numWrites;
            long counter = 0;
            while (n > 0) {
                byte [] buf = new byte[bufSize];
                counter = countRecords(counter, buf);
                chunkedFileOutputStream.write(buf, 0, bufSize);
                n--;
            }
        }

        assertEquals(numWrites + 1, outFolder.listFiles().length);
        checkFilesSize(outFolder, outFile.getChunkSize());
        checkLastFileSize(outFolder);
        //checkFilesContainsTestData(outFolder, testData);
    }

    private void checkFilesContainsTestData(File dir, String testData) {
        List<File> files = getSorted(dir);

        files.forEach((f) -> {
            try {
                String data = new String(IOUtil.readFile(f));
                assertTrue(data.contains(testData), String.format("contents differ:%s", f.getName()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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

    private long countRecords(long counter, byte [] buffer) {
        int index = 0;
        while (index < buffer.length) {
            String num = "" + counter + ",";
            int numLen = num.getBytes().length;
            if ((index + numLen) > buffer.length) {
                num = "#";
            } else {
                counter++;
            }

            numLen = num.getBytes().length;

            System.arraycopy(num.getBytes(), 0, buffer, index, numLen);
            index += num.getBytes().length;
        }
        return counter;
    }
}
