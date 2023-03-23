package hospital;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HospitalData {
    private Connection con = null;

    public HospitalData(String databaseUsername, String databasePassword) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hospital", databaseUsername, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PatientVisit> listAllPatientVisits() {
        ArrayList<PatientVisit> allPatientVisits= new ArrayList<>();
        try {
            String sql = "SELECT VisitID, PatientID, DoctorID, Date, Description FROM patientvisit";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String visitID = rs.getString("VisitID");
                String patientId = rs.getString("PatientID");
                String doctorID = rs.getString("DoctorID");
                String date = rs.getString("Date");
                String description = rs.getString("Description");
                allPatientVisits.add(new PatientVisit(visitID,patientId,doctorID,date,description));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return allPatientVisits;
    }
}
