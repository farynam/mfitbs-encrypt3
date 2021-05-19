package com.mfitbs.encrypt;

import com.mfitbs.encrypt.util.IOUtil;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
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
                        .desc("out directory")
                        .build())
                .addOption(Option.builder(KEY_FILE)
                        .hasArg()
                        .desc("key file")
                        .build())
                .addOption(Option.builder(SPLIT)
                    .hasArg()
                    .desc("split size")
                    .build());


        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption(GENERATE_KEYS)) {
            String path = commandLine.getOptionValue(GENERATE_KEYS);
            generateKeys(path);
            return;
        }

        final String keyFile = getKeyFile(commandLine);
        final boolean split = getSplit(commandLine);

        if (commandLine.hasOption(ENCRYPT)) {
            String toEncrypt = commandLine.getOptionValue(ENCRYPT);
            encrypt(toEncrypt,
                    getOutFile(commandLine, toEncrypt),
                    keyFile,
                    split
            );
            return;
        }

        if (commandLine.hasOption(DECRYPT)) {
            String toDecrypt = commandLine.getOptionValue(DECRYPT);
            decrypt(toDecrypt,
                    getOutFile(commandLine, toDecrypt),
                    keyFile,
                    split);
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

    private static void encrypt(final String infile,
                                final OutFile outFile,
                                final String pubKeyFile,
                                final boolean split)
            throws IOException {
        perform(infile,
                outFile,
                pubKeyFile,
                split,
                (ctx) -> {
            ctx.getEncryptFile().encrypt(ctx.getInFile(), ctx.getKey());
        });
    }

    private static void decrypt(final String infile,
                                final OutFile outFile,
                                String privKeyFile,
                                final boolean split)
            throws IOException {
        perform(infile,
                outFile,
                privKeyFile,
                split,
                (ctx) -> {
            ctx.getEncryptFile().decrypt(ctx.getInFile(),
                    ctx.getKey());
        });
    }

    private static void perform(
            final String inFile,
            final OutFile outFile,
            final String someKey,
            final boolean split,
            MessHandler<EncryptionContext> consumer
    ) throws IOException {
        if (StringUtils.isEmpty(inFile)) {
            throw new IllegalArgumentException("Infile cannot be empty");
        }

        if (StringUtils.isEmpty(someKey)) {
            throw new IllegalArgumentException("key file cannot be empty");
        }

        System.out.println("Using");
        System.out.println("inFile:" + inFile);
        System.out.println("outFileFolder:" + outFile.getOutFileFolder());
        System.out.println("outFileBase:" + outFile.getOutFileBase());
        System.out.println("someKey:" + someKey);


        SymetricEncryption encryptSymetric = new AES();
        RSA encryptAsimetric = new RSA();
        SimetricKeyGenerator keyGenerator = new AESKeyGenerator();
        Encrypt encrypt = new Encrypt(encryptSymetric, encryptAsimetric, keyGenerator);

        EncryptFile encryptFile = new EncryptFile(encrypt, outFile, split);

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

    private static Long getChunksizeInBytes(final CommandLine commandLine) {
        final String value = commandLine.getOptionValue(SPLIT);
        if (value == null) {
            return null;
        }

        final int numberPart = Integer.parseInt(value.substring(0, value.length() - 1));
        final String measurePart = value.substring(value.length() - 1);

        return numberPart * DataMeasure.create(measurePart).orElseThrow(() -> {
            throw new IllegalArgumentException(
                    String.format("measure: %s not found", value));
        }).getBytes();
    }

    private static OutFile getOutFile(CommandLine commandLine, String inFileName) {
        String outFolder = commandLine.getOptionValue(OUT);
        Long chunkSize = getChunksizeInBytes(commandLine);

        OutFile outFile = new OutFile(
                outFolder,
                new File(inFileName).getName(),
                chunkSize);

        return outFile;
    }

    private static boolean getSplit(CommandLine commandLine) {
        return commandLine.hasOption(SPLIT);
    }
}
