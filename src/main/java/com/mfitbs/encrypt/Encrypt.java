package com.mfitbs.encrypt;

import com.mfitbs.encrypt.util.IOUtil;
import org.bouncycastle.util.encoders.Base64;

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
        System.out.println(new String(Base64.encode(sKey)));
        encryptSymetric.init(sKey);
        encryptSymetric.decrypt(in, out);
    }


    private byte [] getSKey(InputStream in, byte [] privKey) throws IOException {
        byte [] sKeyEncrypted = IOUtil.readBytesCount(in, 256);
        System.out.println("TO DECRYPT:" + new String(Base64.encode(sKeyEncrypted)));
        PrivateKey privateKey = PublicPrivateKeyGenerator.createPrivate(privKey);
        byte [] sKeyDecrypted = encryptAsimetric.decryptBytes(sKeyEncrypted, privateKey);
        System.out.println("DECRYPTED:" + new String(Base64.encode(sKeyDecrypted)));
        return sKeyDecrypted;
    }

    private void attachSKey(OutputStream out, byte [] pubKey, byte [] sKey) throws IOException {
        PublicKey mPublicKey = PublicPrivateKeyGenerator.createPublic(pubKey);
        System.out.println("BEFORE ENCRYPT:" + new String(Base64.encode(sKey)));
        byte [] result = encryptAsimetric.encryptBytes(sKey, mPublicKey);
        System.out.println("TO ENCRYPT:" + new String(Base64.encode(result)));
        out.write(result);
        out.flush();
    }
}
