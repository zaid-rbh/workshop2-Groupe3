package com.devsecops.project.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cryptography helpers.
 * Le chiffrement utilise AES-GCM avec un IV alÃ©atoire, et la clÃ© vient de la variable
 * d'environnement APP_AES_KEY (Base64, 16 ou 32 octets), jamais du code source.
 */
@Service
public class CryptoService {

    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_BITS = 128;

    private final SecureRandom secureRandom = new SecureRandom();
    private final SecretKeySpec aesKey;

    public CryptoService() {
        String base64Key = System.getenv("APP_AES_KEY");
        if (base64Key == null || base64Key.isBlank()) {
            throw new IllegalStateException(
                    "Variable d'environnement APP_AES_KEY manquante (clÃ© AES en Base64, 16 ou 32 octets).");
        }
        byte[] keyBytes = Base64.getDecoder().decode(base64Key.trim());
        if (keyBytes.length != 16 && keyBytes.length != 32) {
            throw new IllegalStateException("APP_AES_KEY doit contenir 16 ou 32 octets une fois dÃ©codÃ©e.");
        }
        this.aesKey = new SecretKeySpec(keyBytes, "AES");
    }

    /** Chiffre en AES-GCM. Le rÃ©sultat (Base64) contient l'IV suivi du texte chiffrÃ©. */
    public String encrypt(String plainText) {
        if (plainText == null) {
            return null;
        }
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            ByteBuffer buffer = ByteBuffer.allocate(iv.length + encrypted.length);
            buffer.put(iv);
            buffer.put(encrypted);
            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    public String decrypt(String cipherText) {
        if (cipherText == null) {
            return null;
        }
        try {
            byte[] data = Base64.getDecoder().decode(cipherText);
            ByteBuffer buffer = ByteBuffer.wrap(data);
            byte[] iv = new byte[GCM_IV_LENGTH];
            buffer.get(iv);
            byte[] encrypted = new byte[buffer.remaining()];
            buffer.get(encrypted);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}