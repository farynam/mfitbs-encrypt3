package com.mfitbs.encrypt.io;

import com.mfitbs.encrypt.OutFile;
import lombok.RequiredArgsConstructor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

@RequiredArgsConstructor
public class FileOutputStreamFactory {

    private final OutFile outFile;


    public OutputStream create() throws FileNotFoundException {
        if (outFile.isChunked()) {
            return new ChunkedFileOutputStream(outFile);
        }

        return new FileOutputStream(outFile.createFileNameBase());
    }



}
