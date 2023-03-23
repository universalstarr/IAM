package tools;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Md5ShaDemo {
    public static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] message = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, message);
            String hashed = number.toString(16);
            while (hashed.length() < 32) {
                hashed = "0" + hashed;
            }
            return hashed;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA256");
            byte[] message = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, message);
            String hashed = number.toString(16);

            return hashed;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        String ps = "A1b2C3d4E5";
        String ps2 = "A1b2C3d4E6";
        String hashed = getMd5(ps);
        System.out.println(hashed);

        String hashed2 = getMd5(ps);
        if(hashed.equals(hashed2)){
            System.out.println("matched");
        }else {
            System.out.println("not matched");
        }


        String hashed3 = getMd5(ps2);
        if(hashed.equals(hashed3)){
            System.out.println("matched");
        }else {
            System.out.println("not matched");
        }



        String hashed4 =getSHA256(ps);
        System.out.println(hashed4);

        String hashed5 = getSHA256(ps);
        if(hashed4.equals(hashed5)){
            System.out.println("matched");
        }else {
            System.out.println("not matched");
        }


        String hashed6 = getSHA256(ps2);
        if(hashed4.equals(hashed6)){
            System.out.println("matched");
        }else {
            System.out.println("not matched");
        }
    }
}
