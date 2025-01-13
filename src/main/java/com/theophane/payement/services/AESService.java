package com.theophane.payement.services;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

@Service
public class AESService {

    private static final String ALGORITHM = "AES";
    private final SecretKey secretKey;
    private final ObjectMapper objectMapper;

    public AESService() throws Exception {
        this.secretKey = generateSecretKey();
        this.objectMapper = new ObjectMapper();
    }

    // Générer une clé secrète
    private SecretKey generateSecretKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // Clé de 128 bits
        return keyGen.generateKey();
    }

    // Chiffrer un objet JSON
    public String encrypt(Object data) throws Exception {
        // Convertir l'objet en chaîne JSON
        String jsonData = objectMapper.writeValueAsString(data);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(jsonData.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    // Déchiffrer une chaîne en objet JSON
    public <T> T decrypt(String encryptedData, Class<T> valueType) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] originalData = cipher.doFinal(decodedData);

        // Convertir la chaîne JSON déchiffrée en objet
        String jsonString = new String(originalData);
        return objectMapper.readValue(jsonString, valueType);
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }
}
