import com.mfitbs.encrypt.*;
import com.mfitbs.encrypt.util.IOUtil;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestEncryption {

    @Test
    public void testSimetric() throws IOException {
        SimetricKeyGenerator simetricKeyGenerator = new AESKeyGenerator();
        SymetricEncryption symetric = new AES();
        symetric.init(simetricKeyGenerator.generate());

        String text = "ala ma kota";

        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();

        symetric.encrypt(getAsInputStream(text), byteArrayOutputStream);
        byteArrayOutputStream.close();
        assertTrue(byteArrayOutputStream.toByteArray().length > 0);


        ByteArrayInputStream toDecrypt = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        symetric.decrypt(toDecrypt, result);


        assertEquals(result.toString(), text);
    }

    @Test
    public void testAsimtric() {
        RSA rsa = new RSA();
        PublicPrivateKeyGenerator publicPrivateKeyGenerator = new PublicPrivateKeyGenerator();
        KeyPair keyPair = publicPrivateKeyGenerator.generate();

        String text = "Ala ma kota";

        byte [] encrypted = rsa.encryptText(text, keyPair.getPublic());

        assertTrue(encrypted.length > 0);

        String decrypted = rsa.decryptText(encrypted, keyPair.getPrivate());

        assertEquals(text, decrypted);
    }

    @Test
    public void testWithAttachedKey() throws IOException {
        SymetricEncryption encryptSymetric = new AES();
        SimetricKeyGenerator simetricKeyGenerator = new AESKeyGenerator();
        RSA encryptAsimetric = new RSA();
        PublicPrivateKeyGenerator keyGenerator = new PublicPrivateKeyGenerator();

        KeyPair keyPair = keyGenerator.generate();

        Encrypt encryptFile = new Encrypt(encryptSymetric,
                encryptAsimetric,
                simetricKeyGenerator
                );
        String text = "Ala ma kota";

        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();

        encryptFile.encrypt(getAsInputStream(text),
                byteArrayOutputStream,
                keyPair.getPublic().getEncoded()
                );

        assertTrue(byteArrayOutputStream.size() > 0);


        ByteArrayOutputStream decrypted = new ByteArrayOutputStream();

        encryptFile.decrypt(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()),
                decrypted,
                keyPair.getPrivate().getEncoded()
                );
        decrypted.close();

        assertEquals(decrypted.toString(), text);
    }


    @Test
    public void testKeyWriting() throws IOException {
        PublicPrivateKeyGenerator keyGenerator = new PublicPrivateKeyGenerator();
        KeyPair keyPair = keyGenerator.generate();

        String path = "test";
        String privateFile = path + "/private.bin";
        String publicFile = path + "/public.bin";

        IOUtil.writetofile(privateFile,
                new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded()).getEncoded());
        IOUtil.writetofile(publicFile,
                new X509EncodedKeySpec(keyPair.getPublic().getEncoded()).getEncoded());

        byte[] priv = IOUtil.readFile(privateFile);
        byte[] pub = IOUtil.readFile(publicFile);

        PublicPrivateKeyGenerator.createPrivate(priv);
        PublicPrivateKeyGenerator.createPublic(pub);
    }

    private InputStream getAsInputStream(String text) {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        return byteArrayInputStream;
    }
}
