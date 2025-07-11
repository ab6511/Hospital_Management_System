package HospitalManagmentSystem;

import com.sun.security.jgss.GSSUtil;

import java.sql.*;
import java.util.Scanner;

public class HospitalMangmentSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Admin@123";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);
            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. view Patient");
                System.out.println("3. view doctor");
                System.out.println("4. Book appointment");
                System.out.println("5. Exit");

                System.out.println("Enter your choice :");
                int choice = scanner.nextInt();

                switch (choice){

                    case 1 :
                        //Add patient
                        patient.addPatient();
                        System.out.println("");
                        break;
                    case 2 :
                        //View patient
                        patient.viewPatient();
                        System.out.println("");

                        break;
                    case 3 :
                        //view doctor
                        doctor.viewDoctors();
                        System.out.println("");

                        break;
                    case 4:
                        // book appointment
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println("");

                        break;
                    case 5:
                        return;

                    default:
                        System.out.println("Enter the vaild choice !!");

                }

            }

        }catch (SQLException e ){
            e.printStackTrace();
        }

    }
    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection, Scanner scanner){
        System.out.println("Enter the Patient ID: ");
        int patientId = scanner.nextInt();
        System.out.println("Enter the Doctor ID: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD)");
        String appointmentDate = scanner.next();

        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            if(checkDoctorAvailabillity(doctorId,appointmentDate,connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id,doctor_id,appointment_date)values(?,?,?)";

                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appointmentDate);
                    int rowAffected = preparedStatement.executeUpdate();
                    if(rowAffected>0){
                        System.out.println("Appointment Booked");
                    }
                    else {
                        System.out.println("Failed to book Appointment !!!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("Doctor not available on this date!!");
            }
        }
        else {
            System.out.println("Either Doctor or patient doesn't exist !!!");
        }
    }
    public static boolean checkDoctorAvailabillity(int doctorId , String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE Doctor_id = ? AND appointment_date = ? ";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int count = resultSet.getInt(1);
                if(count ==0 ){
                    return true;
                }
                else{
                    return false;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
