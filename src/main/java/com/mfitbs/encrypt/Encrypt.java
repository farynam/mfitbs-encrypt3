package com.mfitbs.encrypt;

import com.mfitbs.encrypt.util.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Encrypt {

    private final SymetricEncryption encryptSymetric;
    private final RSA encryptAsimetric;
    private final SimetricKeyGenerator simetricKeyGenerator;


    public Encrypt(SymetricEncryption encryptSymetric,
                   RSA encryptAsimetric,
                   SimetricKeyGenerator simetricKeyGenerator) {
        this.encryptSymetric = encryptSymetric;
        this.encryptAsimetric = encryptAsimetric;
        this.simetricKeyGenerator = simetricKeyGenerator;
    }




    public void encrypt(InputStream in, OutputStream out, byte [] pubKey) throws IOException {
        byte [] sKey = simetricKeyGenerator.generate();
        encryptSymetric.init(sKey);
        attachSKey(out, pubKey, sKey);
        encryptSymetric.encrypt(in, out);
    }

    public void decrypt(InputStream in, OutputStream out, byte [] privKey) throws IOException {
        byte [] sKey = getSKey(in, privKey);
        encryptSymetric.init(sKey);
        encryptSymetric.decrypt(in, out);
    }

    private byte [] getSKey(InputStream in, byte [] privKey) throws IOException {
        byte [] sKeyEncrypted = IOUtil.readBytesCount(in, simetricKeyGenerator.size());
        PrivateKey privateKey = PublicPrivateKeyGenerator.createPrivate(privKey);
        return encryptAsimetric.decryptBytes(sKeyEncrypted, privateKey);
    }

    private void attachSKey(OutputStream out, byte [] pubKey, byte [] sKey) throws IOException {
        PublicKey mPublicKey = PublicPrivateKeyGenerator.createPublic(pubKey);
        byte [] result = encryptAsimetric.encryptBytes(sKey, mPublicKey);
        try (ByteArrayInputStream keyStream = new ByteArrayInputStream(result)) {
            IOUtil.copy(keyStream, out);
            out.flush();
        }
    }
}
