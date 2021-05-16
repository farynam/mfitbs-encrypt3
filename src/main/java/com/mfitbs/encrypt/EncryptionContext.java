package com.mfitbs.encrypt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EncryptionContext {

    final String inFile;
    final String outFile;
    final byte [] key;
    final EncryptFile encryptFile;
}
