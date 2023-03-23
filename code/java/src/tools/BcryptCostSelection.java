package tools;

import org.mindrot.jbcrypt.BCrypt;

public class BcryptCostSelection {
    public static void main(String[] args) {
        String ps = "A1b2C3d4E5";

        for(int i = 4;i<=30;i++){
            long startTime= System.nanoTime();
            String hashed = BCrypt.hashpw(ps, BCrypt.gensalt(i));
            long endTime = System.nanoTime();
            double takenTimes = (endTime-startTime)/1.0e6;
            System.out.println(i+" "+takenTimes);
        }
    }
}
