package com.mfitbs.encrypt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OutFile {

    final String outFileFolder;
    final String outFileBase;
    final Long chunkSize;

    public String createFileNameBase(Integer index) {
        String fileNumber = "";

        if (isChunked()) {
            fileNumber = "_" + index;
        }
        return String.format("%s/%s%s.encr", outFileFolder, outFileBase, fileNumber);
    }

    public String createFileNameBase() {
        return createFileNameBase(null);
    }

    public boolean isChunked() {
        return chunkSize > 1;
    }
}
