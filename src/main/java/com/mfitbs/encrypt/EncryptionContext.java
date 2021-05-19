package com.mfitbs.encrypt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EncryptionContext {

    final String inFile;
    final OutFile outFile;
    final byte [] key;
    final EncryptFile encryptFile;
}
