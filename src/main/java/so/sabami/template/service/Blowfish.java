/*-
 * Copyright (c) 2004-2015 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import so.sabami.template.exception.DecryptException;
import so.sabami.template.exception.EncryptException;

/**
 * @author usr160056
 * @since 2015/02/16
 */
public class Blowfish {

    public static final String KEY = "0C7u9y9L";

    /**
     * 暗号化するメソッドです。
     * @param key  鍵です
     * @param text 暗号化したい文字列です
     * @throws EncryptException 
     */
    public static String encrypt(String key, String text) throws EncryptException  {
        String encryptedString;
        try {
            SecretKeySpec sksSpec = new SecretKeySpec(key.getBytes(), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, sksSpec);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            encryptedString = new String(Hex.encodeHex(encrypted));
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptException(e);
        }
        return encryptedString;
    }

    /**
     * 複合化するメソッドです。
     * @param key  鍵です
     * @param text 複合化したい文字列です
     * @throws DecryptException 
     */
    public static String decrypt(String key, String text) throws DecryptException {
        String decryptedString;
        try {
            byte[] encrypted = null;
            encrypted = Hex.decodeHex(text.toCharArray());
            SecretKeySpec sksSpec = new SecretKeySpec(key.getBytes(), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, sksSpec);
            byte[] decrypted = cipher.doFinal(encrypted);
            decryptedString = new String(decrypted);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | DecoderException e) {
            throw new DecryptException(e);
        }
        return decryptedString;

    }

}
