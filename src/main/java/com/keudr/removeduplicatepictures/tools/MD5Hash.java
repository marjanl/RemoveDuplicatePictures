package com.keudr.removeduplicatepictures.tools;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hash {

    public String getChecksum(Path imagePath) throws FileNotFoundException {

        return generateMD5(new FileInputStream(imagePath.toFile()));

        /*byte[] hash = new byte[0];
        try {
            byte[] data = Files.readAllBytes(imagePath);
            hash = MessageDigest.getInstance("MD5").digest(data);
            return new BigInteger(1, hash).toString(16);
        } catch (IOException | NoSuchAlgorithmException  e) {
            throw new RuntimeException(e);
        }*/

    }

    private static String generateMD5(FileInputStream inputStream){
        if(inputStream==null){

            return null;
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            FileChannel channel = inputStream.getChannel();
            ByteBuffer buff = ByteBuffer.allocate(2048);
            while(channel.read(buff) != -1)
            {
                buff.flip();
                md.update(buff);
                buff.clear();
            }
            byte[] hashValue = md.digest();
            return new String(hashValue);
        }
        catch (NoSuchAlgorithmException e)
        {
            return null;
        }
        catch (IOException e)
        {
            return null;
        }
        finally
        {
            try {
                if(inputStream!=null)inputStream.close();
            } catch (IOException e) {

            }
        }
    }

}
