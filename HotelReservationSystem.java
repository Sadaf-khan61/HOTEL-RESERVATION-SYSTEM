import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;

public class HotelReservationSystem{
    private static final String url="jdbc:mysql://localhost:3306/hotel_db2";
    private static final String username="root";
    private static final String password="KLsa@3333";

    public static void main(String[] args) throws ClassNotFoundException, SQLException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View reservations");
                System.out.println("3. Get room number");
                System.out.println("4. Update reservation");
                System.out.println("5. Delete reservation");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(connection, sc);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, sc);
                        break;
                    case 4:
                        updateReservation(connection, sc);
                        break;
                    case 5:
                        deleteReservation(connection, sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice.Try again.");
                }
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection connection, Scanner sc){
        try {
            System.out.print("Enter guest name: ");
            String guestName = sc.next();
            sc.nextLine();
            System.out.print("Enter the room number: ");
            int roomNumber = sc.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = sc.next();

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number)" +
                     "VALUES ('" + guestName + "'," + roomNumber + ",'" + contactNumber + "')";

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0) {
                    System.out.println("Reservation successful");
                } else {
                    System.out.println("Reservation failed");
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection connection) throws SQLException {

        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("Current Reservations:");

            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.println("Reservation ID: " + reservationId);
                System.out.println("Guest Name    : " + guestName);
                System.out.println("Room Number   : " + roomNumber);
                System.out.println("Contact Number: " + contactNumber);
                System.out.println("Date          : " + reservationDate);
                System.out.println("-----------------------------------------");

            }
        }
    }

    private static void getRoomNumber(Connection connection, Scanner sc) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = sc.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = sc.next();

            String sql = "SELECT room_number FROM reservations " +
                    "WHERE reservation_id= " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for reservation ID:" + reservationId +
                            " and guest " + guestName + " is:" + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection, Scanner sc) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = sc.nextInt();
            sc.nextLine();
            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }
            System.out.print("Enter new guest name: ");
            String newGuestName = sc.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = sc.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = sc.next();

            String sql = "UPDATE reservations SET guest_name='" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner sc) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = sc.nextInt();
            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully");
                } else {
                    System.out.println("Reservation deletion failed");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId){
        try{

            String sql="SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try(Statement statement=connection.createStatement();
                ResultSet resultSet=statement.executeQuery(sql)) {
                return resultSet.next();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i=5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thank you for using Hotel Reservation System");
    }
}