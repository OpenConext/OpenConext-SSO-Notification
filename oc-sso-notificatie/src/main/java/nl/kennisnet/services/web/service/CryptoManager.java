/*
 * Copyright 2021, Stichting Kennisnet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.kennisnet.services.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Encrypts data using algorithm and secured key from properties
 */
@Component
public class CryptoManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoManager.class);

    private static final int IV_SIZE = 16;
    private static final int KEY_SIZE = 256;
    private static final int ITERATION_COUNT = 1000;

    @Value("${crypto.encrypt.algorithm}")
    private String algorithm;

    @Value("${crypto.secure.key}")
    private String key;

    @Value("${crypto.secure.key.type}")
    private String keyType;

    @Value("${crypto.secure.key.algorithm}")
    private String keyAlgorithm;

    @Value("${crypto.secure.salt}")
    private String salt;

    /**
     * Encrypts the supplied data.
     *
     * @param data Decrypted data.
     * @return Encrypted data.
     */
    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            IvParameterSpec iv = generateIv();
            cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey(), iv);

            // Encrypt data
            byte[] enc = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Prepend generated iv
            byte[] cipherText = ByteBuffer.allocate(iv.getIV().length + enc.length)
                    .put(iv.getIV())
                    .put(enc)
                    .array();

            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception ex) {
            LOGGER.error("Encryption error: {}", ex.getMessage() , ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Key generateSecretKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(keyType);
        KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_SIZE);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), keyAlgorithm);
    }

    private IvParameterSpec generateIv() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}
