package com.example.passwordmanager;

import android.security.keystore.KeyProperties;

import javax.crypto.Cipher;

public class AES {

    private byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        cipher.init(Cipher.ENCRYPT_MODE, KeyUtils.getKey());
        return cipher.doFinal(data);
    }

    private byte[] decrypt(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        cipher.init(Cipher.DECRYPT_MODE, KeyUtils.getKey());
        return cipher.doFinal(encryptedData);
    }
}
