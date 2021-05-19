package com.mfitbs.encrypt;

import com.mfitbs.encrypt.io.ChunkedFileInputStream;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestChunkedInput {
    int count = 0;

    @Test
    public void testReading() throws IOException {
        final String input = "test/testInput/in/file_1.encr";
        final int estimatedOutputSize = 10240;
        final int filesCount = 11;
        final int bufferSize = 1024;

        try (ChunkedFileInputStream chunkedFileInputStream = new ChunkedFileInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()
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
            } while (true);

            byte [] outLen = out.toByteArray();

            assertEquals(filesCount, count);
            assertTrue(outLen.length > 0);
            assertEquals(estimatedOutputSize, outLen.length);
        }
    }
}
