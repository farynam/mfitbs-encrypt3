package com.mfitbs.encrypt;

import com.mfitbs.encrypt.io.ChunkedFileInputStream;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestChunkedInput {


    @Test
    public void testReading() throws IOException {
        try (ChunkedFileInputStream chunkedFileInputStream = new ChunkedFileInputStream()) {

            chunkedFileInputStream.setNextFileOpenedConsumer((f, uuid) -> {
                System.out.printf("next file opened:%s\n", uuid.toString());
            });

            chunkedFileInputStream.init("test/out/file_1.encr");

            byte [] buffer = new byte[1024];

            int read = 0;
            do {
                read = chunkedFileInputStream.read(buffer);
            } while (read > 0);
        }
    }

}
