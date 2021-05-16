package com.mfitbs.encrypt;

import com.mfitbs.encrypt.util.IOUtil;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.security.KeyPair;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Main {

    public static final String GENERATE_KEYS = "g";
    public static final String ENCRYPT = "e";
    public static final String DECRYPT = "d";
    public static final String OUT = "o";
    public static final String KEY_FILE = "k";
    public static final String SPLIT = "s";

    public static void main(String[] args) throws ParseException, IOException {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();

        options.addOption(Option.builder(GENERATE_KEYS)
                .hasArg()
                .desc("generate keys")
                .build())
                .addOption(Option.builder(ENCRYPT)
                        .hasArg()
                        .desc("encrypt")
                        .build())
                .addOption(Option.builder(DECRYPT)
                        .hasArg()
                        .desc("decrypt")
                        .build())
                .addOption(Option.builder(OUT)
                        .hasArg()
                        .desc("out file")
                        .build())
                .addOption(Option.builder(KEY_FILE)
                        .hasArg()
                        .desc("key file")
                        .build());


        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption(GENERATE_KEYS)) {
            String path = commandLine.getOptionValue(GENERATE_KEYS);
            generateKeys(path);
            return;
        }

        final String outFile = getOut(commandLine);
        final String keyFile = getKeyFile(commandLine);

        if (commandLine.hasOption(ENCRYPT)) {
            String toEncrypt = commandLine.getOptionValue(ENCRYPT);
            encrypt(toEncrypt, outFile, keyFile);
            return;
        }

        if (commandLine.hasOption(DECRYPT)) {
            String toDecrypt = commandLine.getOptionValue(DECRYPT);
            decrypt(toDecrypt, outFile, keyFile);
            return;
        }
    }

    private static void generateKeys(String path) throws IOException {
        PublicPrivateKeyGenerator publicPrivateKeyGenerator = new PublicPrivateKeyGenerator();
        KeyPair keyPair = publicPrivateKeyGenerator.generate();

        String privateFile = path + "/private.bin";
        String publicFile = path + "/public.bin";

        //IOUtil.writePEM(privateFile, keyPair.getPrivate().getEncoded(), IOUtil.PRIV_KEY);
        //IOUtil.writePEM(publicFile, keyPair.getPublic().getEncoded(), IOUtil.PUB_KEY);

        IOUtil.writetofile(privateFile,
                new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded()).getEncoded());
        IOUtil.writetofile(publicFile,
                new X509EncodedKeySpec(keyPair.getPublic().getEncoded()).getEncoded());

        System.out.println("Generated");
        System.out.println("privateFile:" + privateFile);
        System.out.println("publicFile:" + publicFile);
    }

    private static void encrypt(final String infile, final String outFile, String pubKeyFile)
            throws IOException {
        perform(infile, outFile, pubKeyFile, (ctx) -> {
            ctx.getEncryptFile().encrypt(
                    ctx.getInFile(),
                    ctx.getOutFile(),
                    ctx.getKey());
        });
    }

    private static void decrypt(final String infile, final String outFile, String privKeyFile)
            throws IOException {
        perform(infile, outFile, privKeyFile, (ctx) -> {
            ctx.getEncryptFile().decrypt(
                    ctx.getInFile(),
                    ctx.getOutFile(),
                    ctx.getKey());
        });
    }

    private static String getOut(CommandLine commandLine) {
        if (commandLine.hasOption(OUT)) {
            return commandLine.getOptionValue(OUT);
        }
        return null;
    }

    private static void perform(
            final String inFile,
            String outFile,
            final String someKey,
            MessHandler<EncryptionContext> consumer
    ) throws IOException {
        if (StringUtils.isEmpty(inFile)) {
            throw new IllegalArgumentException("Infile cannot be empty");
        }

        if (StringUtils.isEmpty(someKey)) {
            throw new IllegalArgumentException("key file cannot be empty");
        }

        if (StringUtils.isEmpty(outFile)) {
            outFile = inFile + ".encr";
        }

        System.out.println("Using");
        System.out.println("inFile:" + inFile);
        System.out.println("outFile:" + outFile);
        System.out.println("someKey:" + someKey);


        SymetricEncryption encryptSymetric = new AES();
        RSA encryptAsimetric = new RSA();
        SimetricKeyGenerator keyGenerator = new AESKeyGenerator();

        Encrypt encrypt = new Encrypt(encryptSymetric, encryptAsimetric, keyGenerator);

        EncryptFile encryptFile = new EncryptFile(encrypt);

        byte[] key = IOUtil.readFile(someKey);

        try {
            consumer.exec(new EncryptionContext(inFile, outFile, key, encryptFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getKeyFile(CommandLine commandLine) {
        if (commandLine.hasOption(KEY_FILE)) {
            return commandLine.getOptionValue(KEY_FILE);
        }
        return null;
    }
}
