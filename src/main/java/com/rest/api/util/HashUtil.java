package com.rest.api.util;

import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class HashUtil {
    
    public String calcularHash(byte[] contenido) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contenido);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al calcular hash SHA-256", e);
        }
    }
}