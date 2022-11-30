package com.keudr.removeduplicatepictures.tools;


import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hash {

    public String getChecksum(Path imagePath)  {
        byte[] hash = new byte[0];
        try {
            byte[] data = Files.readAllBytes(imagePath);
            hash = MessageDigest.getInstance("MD5").digest(data);
            return new BigInteger(1, hash).toString(16);
        } catch (IOException | NoSuchAlgorithmException  e) {
            throw new RuntimeException(e);
        }

    }
}
