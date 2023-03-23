package hospital;

import authentication.AuthenticationModule;
import authentication.BcryptAuthenticationModule;
import authorization.AuthorizationModule;
import authorization.CompositeAuthorizationModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        AuthenticationModule authenticationModule = new BcryptAuthenticationModule("root", "123456");
        AuthorizationModule authorizationModule = new CompositeAuthorizationModule("root", "123456");
        System.out.print("User Name: ");
        Scanner sc = new Scanner(System.in);
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        if (!authenticationModule.login(username, password)) {
            System.out.println("Incorrect User Name or Password");
            return;
        }
        HospitalData hd = new HospitalData("root", "123456");
        List<PatientVisit> patientVisitLists = hd.listAllPatientVisits();
//       System.out.println(patientVisitLists);
        List<PatientVisit> accessiblePatientVisits = new ArrayList<>();
        for (PatientVisit patientVisit : patientVisitLists) {
            if (authorizationModule.checkAccess(username, "read", patientVisit.getVisitID())) {
                accessiblePatientVisits.add(patientVisit);
            }
        }
        System.out.println(accessiblePatientVisits);
    }
}
