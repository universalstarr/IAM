package hospital;

public class PatientVisit {
    private String visitID = null;
    private String patientID = null;
    private String doctorID = null;
    private String date = null;
    private String description = null;

    public PatientVisit(String visitID,String patientID,String doctorID,String date,String description){
        this.visitID = visitID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.date = date;
        this.description = description;

    }

    public String getVisitID() {
        return visitID;
    }

    @Override
    public String toString() {
        return "PatientVisit{" +
                "visitID='" + visitID + '\'' +
                ", patientID='" + patientID + '\'' +
                ", doctorID='" + doctorID + '\'' +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
