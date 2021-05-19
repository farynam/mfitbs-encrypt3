package com.mfitbs.encrypt;

import com.mfitbs.encrypt.io.ChunkedFileInputStream;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.StringTokenizer;

import static org.junit.jupiter.api.Assertions.*;

public class TestChunkedInput {
    int count = 0;

    @Test
    public void testReading() throws IOException {
        final String input = "test/testInput/in/file_1.encr";
        final String outFile = "test/testInput/out/result.bin";
        final int estimatedOutputSize = 10240;
        final int filesCount = 11;
        final int bufferSize = 1024;

        try (ChunkedFileInputStream chunkedFileInputStream = new ChunkedFileInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStream fout = new FileOutputStream(new File(outFile))
        ) {

            chunkedFileInputStream.setNextFileOpenedConsumer((f, uuid) -> {
                count++;
                System.out.printf("%d next file opened:%s\n", count, uuid.toString());
            });

            chunkedFileInputStream.init(input);

            byte [] buffer = new byte[bufferSize];

            int read = 0;
            do {
                read = chunkedFileInputStream.read(buffer);
                if (read < 0) {
                    break;
                }
                out.write(buffer, 0, read);
                fout.write(buffer, 0, read);
            } while (true);

            byte [] outLen = out.toByteArray();
            checkIntegrity(outLen);

            assertEquals(filesCount, count);
            assertTrue(outLen.length > 0);
            assertEquals(estimatedOutputSize, outLen.length);
        }

    }

    private void checkIntegrity(byte [] out) {
        String data = new String(out);

        StringTokenizer strtok = new StringTokenizer(data);

        long last = -1;
        while (strtok.hasMoreTokens()) {
            String token = strtok.nextToken(",");
            if (token.startsWith("#") && token.endsWith("#")) {
                continue;
            }


            long num = Long.parseLong(token.replaceAll("#", ""));

            assertFalse((last + 1) != num,
                    "Wrong integrity before:" + last + "next:" + num);
            last = num;
        }
    }
}
