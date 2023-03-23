package tools;

import org.mindrot.jbcrypt.BCrypt;


public class PasswordDemo {


    public static void main(String[] args) {
        String ps = "A1b2C3d4E5";
        String ps2 = "A1b2C3d4E6";

        String salt = BCrypt.gensalt();
        String hashed = BCrypt.hashpw(ps, salt);

        System.out.println(salt);
        System.out.println(hashed);

        if (BCrypt.checkpw(ps, hashed))
            System.out.println("It matches");
        else
            System.out.println("It does not match");

        if (BCrypt.checkpw(ps2, hashed))
            System.out.println("It matches");
        else
            System.out.println("It does not match");

    }
}
