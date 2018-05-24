package crdhn.dis.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author namdv
 */
public class AsymmetricCryptography {

    private static AsymmetricCryptography _instance;
    private Cipher cipher;
    private Cipher aesCipher;
    private static String algorithm = "RSA";//RSA

    public AsymmetricCryptography() {
        try {
            this.cipher = Cipher.getInstance(algorithm);
            //for encrypt data
            this.aesCipher = Cipher.getInstance("AES");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static AsymmetricCryptography getInstance() {
        if (_instance == null) {
            _instance = new AsymmetricCryptography();
        }

        return _instance;
    }

    public DataResponse encryptTextData(String content, PublicKey publicKey) {
        try {
            Cipher enCipher = Cipher.getInstance(algorithm);
            String key_random = "DI" + System.nanoTime();
            enCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            String encryptedKey = Base64.getUrlEncoder().encodeToString(enCipher.doFinal(key_random.getBytes("UTF-8")));

            byte[] salt;
            salt = Arrays.copyOf(key_random.getBytes(), 16);
            SecretKey secretKey = new SecretKeySpec(salt, 0, salt.length, "AES");
            Cipher enaesCipher = Cipher.getInstance("AES");
            enaesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            String encryptedData = Base64.getUrlEncoder().encodeToString(enaesCipher.doFinal(content.getBytes("UTF-8")));
            HashMap<String, Object> obj = new HashMap();
            obj.put("k", encryptedKey);
            obj.put("data", encryptedData);
            return new DataResponse(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return new DataResponse(-1, e.getMessage());
        }
    }

    public String DecryptTextData(String encryptedKey, String encryptedData, PrivateKey key) {
        try {
            Cipher deCipher = Cipher.getInstance(algorithm);
            deCipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedKey = deCipher.doFinal(Base64.getUrlDecoder().decode(encryptedKey));
            byte[] salt;
            salt = Arrays.copyOf(decryptedKey, 16);
            SecretKey originalKey = new SecretKeySpec(salt, 0, salt.length, "AES");
//            SecretKey originalKey = new SecretKeySpec(decryptedKey, "AES");
            Cipher deaesCipher = Cipher.getInstance("AES");
            deaesCipher.init(Cipher.DECRYPT_MODE, originalKey);
            return new String(deaesCipher.doFinal(Base64.getUrlDecoder().decode(encryptedData.getBytes("UTF-8"))), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // https://docs.oracle.com/javase/8/docs/api/java/security/spec/PKCS8EncodedKeySpec.html
    public PrivateKey getPrivate(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePrivate(spec);
    }

    public PrivateKey getPrivateFromString(String keyContent) throws Exception {
        byte[] keyBytes = keyContent.getBytes();
        byte[] keydata = java.util.Base64.getUrlDecoder().decode(keyBytes);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keydata);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePrivate(spec);
    }

    // https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
    public PublicKey getPublic(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(spec);
    }

    public PublicKey getPublicFromString(String keyContent) throws Exception {
        byte[] keyBytes = keyContent.getBytes();
        byte[] keydata = java.util.Base64.getUrlDecoder().decode(keyBytes);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keydata);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(spec);
    }

    private void encryptFile(byte[] input, File output, PrivateKey key)
            throws IOException, GeneralSecurityException {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        writeToFile(output, this.cipher.doFinal(input));
    }

    private void decryptFile(byte[] input, File output, PublicKey key)
            throws IOException, GeneralSecurityException {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        writeToFile(output, this.cipher.doFinal(input));
    }

    private void writeToFile(File output, byte[] toWrite)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        FileOutputStream fos = new FileOutputStream(output);
        fos.write(toWrite);
        fos.flush();
        fos.close();
    }

    private String encryptText(String msg, PublicKey key) {
        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getUrlEncoder().encodeToString(cipher.doFinal(msg.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String decryptText(String msg, PrivateKey key) {
        try {
            this.cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getUrlDecoder().decode(msg)), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getFileInBytes(File f) throws IOException {
        byte[] fbytes;
        try (FileInputStream fis = new FileInputStream(f)) {
            fbytes = new byte[(int) f.length()];
            fis.read(fbytes);
        }
        return fbytes;
    }

    public static void main(String[] args) throws Exception {
        AsymmetricCryptography ac = new AsymmetricCryptography();
        PrivateKey privateKey = ac.getPrivate("KeyPair/privateKey");
        PublicKey publicKey = ac.getPublic("KeyPair/publicKey");

        String msg = "Đặng Văn Nam 2017@ Hôm nay là ngày 05/10/2017!";
        String encrypted_msg = ac.encryptText(msg, publicKey);
        String decrypted_msg = ac.decryptText(encrypted_msg, privateKey);
        System.out.println("Original Message: " + msg
                + "\nEncrypted Message: " + encrypted_msg
                + "\nDecrypted Message: " + decrypted_msg);

//        if (new File("KeyPair/text.txt").exists()) {
//            ac.encryptFile(ac.getFileInBytes(new File("KeyPair/text.txt")),
//                    new File("KeyPair/text_encrypted.txt"), privateKey);
//            ac.decryptFile(ac.getFileInBytes(new File("KeyPair/text_encrypted.txt")),
//                    new File("KeyPair/text_decrypted.txt"), publicKey);
//        } else {
//            System.out.println("Create a file text.txt under folder KeyPair");
//        }
    }
}
