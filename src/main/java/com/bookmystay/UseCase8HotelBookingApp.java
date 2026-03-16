import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UseCase8HotelBookingApp {

    // ── RoomType enum ──────────────────────────────
    enum RoomType {
        SINGLE, DOUBLE, SUITE, DELUXE, FAMILY
    }

    // ── AddOnService enum ──────────────────────────
    enum AddOnService {
        BREAKFAST(300, "Breakfast"),
        AIRPORT_TRANSFER(500, "Airport Transfer"),
        SPA(800, "Spa"),
        LAUNDRY(200, "Laundry"),
        PARKING(150, "Parking");

        private final double price;
        private final String displayName;

        AddOnService(double price, String displayName) {
            this.price       = price;
            this.displayName = displayName;
        }

        public double getPrice()       { return price; }
        public String getDisplayName() { return displayName; }
    }

    // ── Room class ─────────────────────────────────
    static class Room {
        private String roomNumber;
        private RoomType roomType;
        private double pricePerNight;
        private boolean available;

        public Room(String roomNumber, RoomType roomType, double pricePerNight, boolean available) {
            this.roomNumber    = roomNumber;
            this.roomType      = roomType;
            this.pricePerNight = pricePerNight;
            this.available     = available;
        }

        public String getRoomNumber()    { return roomNumber; }
        public RoomType getRoomType()    { return roomType; }
        public double getPricePerNight() { return pricePerNight; }
        public boolean isAvailable()     { return available; }
        public void setAvailable(boolean available) { this.available = available; }

        @Override
        public String toString() {
            return String.format("Room %-6s | Type: %-8s | Price: Rs.%-8.0f | Status: %s",
                    roomNumber, roomType, pricePerNight,
                    available ? "Available" : "Not Available");
        }
    }

    // ── RoomInventory class ────────────────────────
    static class RoomInventory {
        private Map<String, Room> inventory;

        public RoomInventory() {
            inventory = new HashMap<>();
            addRoom(new Room("101", RoomType.SINGLE,  1500, true));
            addRoom(new Room("102", RoomType.SINGLE,  1500, false));
            addRoom(new Room("201", RoomType.DOUBLE,  2500, true));
            addRoom(new Room("202", RoomType.DOUBLE,  2500, true));
            addRoom(new Room("301", RoomType.SUITE,   5000, false));
            addRoom(new Room("401", RoomType.DELUXE,  3500, true));
            addRoom(new Room("501", RoomType.FAMILY,  4000, false));
        }

        public void addRoom(Room room) {
            inventory.put(room.getRoomNumber(), room);
        }

        public Map<String, Room> getInventory() { return inventory; }

        public Room getRoom(String roomNumber) {
            return inventory.get(roomNumber);
        }

        public void displayAvailableRooms() {
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-8s %-10s %-15s %-15s%n",
                    "Room No", "Type", "Price/Night", "Status");
            System.out.println("------------------------------------------------------------");
            for (Room room : inventory.values()) {
                if (room.isAvailable()) {
                    System.out.println(room);
                }
            }
            System.out.println("------------------------------------------------------------");
        }
    }

    // ── Reservation class ──────────────────────────
    static class Reservation {
        private static int counter = 1000;
        private String reservationId;
        private String guestName;
        private Room assignedRoom;
        private int numberOfNights;
        private List<AddOnService> addOns;
        private double totalCost;
        private String status; // CONFIRMED, CANCELLED

        public Reservation(String guestName, Room room, int numberOfNights) {
            this.reservationId  = "RES" + (++counter);
            this.guestName      = guestName;
            this.assignedRoom   = room;
            this.numberOfNights = numberOfNights;
            this.addOns         = new ArrayList<>();
            this.totalCost      = numberOfNights * room.getPricePerNight();
            this.status         = "CONFIRMED";
        }

        public String getReservationId() { return reservationId; }
        public String getGuestName()     { return guestName; }
        public Room getAssignedRoom()    { return assignedRoom; }
        public int getNumberOfNights()   { return numberOfNights; }
        public String getStatus()        { return status; }
        public void setStatus(String status) { this.status = status; }

        public void addService(AddOnService service) {
            addOns.add(service);
            totalCost += service.getPrice();
        }

        public double getTotalCost() { return totalCost; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(
                    "  Reservation ID : %s%n" +
                            "  Guest Name     : %s%n" +
                            "  Room Number    : %s%n" +
                            "  Room Type      : %s%n" +
                            "  Nights         : %d%n" +
                            "  Price/Night    : Rs.%.0f%n",
                    reservationId, guestName,
                    assignedRoom.getRoomNumber(),
                    assignedRoom.getRoomType(),
                    numberOfNights,
                    assignedRoom.getPricePerNight()));

            if (!addOns.isEmpty()) {
                sb.append("  Add-On Services:\n");
                for (AddOnService s : addOns) {
                    sb.append(String.format("    - %-20s Rs.%.0f%n",
                            s.getDisplayName(), s.getPrice()));
                }
            } else {
                sb.append("  Add-On Services : None\n");
            }

            sb.append(String.format(
                    "  Total Cost     : Rs.%.0f%n" +
                            "  Status         : %s",
                    totalCost, status));
            return sb.toString();
        }
    }

    // ── BookingService class ───────────────────────
    static class BookingService {
        private RoomInventory inventory;
        private Map<String, Reservation> reservations;

        public BookingService(RoomInventory inventory) {
            this.inventory    = inventory;
            this.reservations = new HashMap<>();
        }

        public Reservation confirmBooking(String guestName, String roomNumber, int nights) {
            if (guestName == null || guestName.trim().isEmpty()) {
                System.out.println("  ERROR: Guest name cannot be empty.");
                return null;
            }
            if (nights <= 0) {
                System.out.println("  ERROR: Number of nights must be greater than 0.");
                return null;
            }
            Room room = inventory.getRoom(roomNumber);
            if (room == null) {
                System.out.println("  ERROR: Room " + roomNumber + " does not exist.");
                return null;
            }
            if (!room.isAvailable()) {
                System.out.println("  ERROR: Room " + roomNumber + " is not available.");
                return null;
            }
            room.setAvailable(false);
            Reservation reservation = new Reservation(guestName, room, nights);
            reservations.put(reservation.getReservationId(), reservation);
            return reservation;
        }

        public Reservation getReservation(String reservationId) {
            return reservations.get(reservationId);
        }

        public Map<String, Reservation> getAllReservations() {
            return reservations;
        }

        public void displayAllReservations() {
            if (reservations.isEmpty()) {
                System.out.println("  No reservations found.");
                return;
            }
            System.out.println("------------------------------------------------------------");
            for (Reservation r : reservations.values()) {
                System.out.println(r);
                System.out.println("------------------------------------------------------------");
            }
        }
    }

    // ── BookingReportService class ─────────────────
    static class BookingReportService {
        private BookingService bookingService;

        public BookingReportService(BookingService bookingService) {
            this.bookingService = bookingService;
        }

        public void generateFullReport() {
            Map<String, Reservation> reservations = bookingService.getAllReservations();

            System.out.println("============================================");
            System.out.println("         BOOKING HISTORY & REPORT          ");
            System.out.println("============================================");

            if (reservations.isEmpty()) {
                System.out.println("  No booking records found.");
                return;
            }

            int totalBookings    = 0;
            int confirmed        = 0;
            int cancelled        = 0;
            double totalRevenue  = 0;

            for (Reservation r : reservations.values()) {
                totalBookings++;
                if (r.getStatus().equals("CONFIRMED")) {
                    confirmed++;
                    totalRevenue += r.getTotalCost();
                } else {
                    cancelled++;
                }
            }

            System.out.println("------------------------------------------------------------");
            System.out.printf("  %-25s : %d%n", "Total Bookings",   totalBookings);
            System.out.printf("  %-25s : %d%n", "Confirmed",        confirmed);
            System.out.printf("  %-25s : %d%n", "Cancelled",        cancelled);
            System.out.printf("  %-25s : Rs.%.0f%n", "Total Revenue", totalRevenue);
            System.out.println("------------------------------------------------------------");

            System.out.println("\n  Booking Details:");
            System.out.println("------------------------------------------------------------");
            for (Reservation r : reservations.values()) {
                System.out.println(r);
                System.out.println("------------------------------------------------------------");
            }
        }

        public void searchByGuestName(String name) {
            System.out.println("\n--- Search Results for Guest: " + name + " ---");
            System.out.println("------------------------------------------------------------");
            boolean found = false;
            for (Reservation r : bookingService.getAllReservations().values()) {
                if (r.getGuestName().equalsIgnoreCase(name)) {
                    System.out.println(r);
                    System.out.println("------------------------------------------------------------");
                    found = true;
                }
            }
            if (!found) {
                System.out.println("  No bookings found for guest: " + name);
                System.out.println("------------------------------------------------------------");
            }
        }
    }

    // ── Main Method ────────────────────────────────
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("============================================");
        System.out.println("   Book My Stay                             ");
        System.out.println("   UC8 - Booking History & Reporting        ");
        System.out.println("============================================");
        System.out.println();

        RoomInventory inventory          = new RoomInventory();
        BookingService bookingService    = new BookingService(inventory);
        BookingReportService reportService = new BookingReportService(bookingService);

        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Make a Booking");
            System.out.println("3. View All Reservations");
            System.out.println("4. Generate Booking Report");
            System.out.println("5. Search Booking by Guest Name");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\nAvailable Rooms:");
                    inventory.displayAvailableRooms();
                    break;

                case 2:
                    System.out.print("Enter Guest Name: ");
                    String guestName = scanner.nextLine();

                    System.out.print("Enter Room Number: ");
                    String roomNumber = scanner.nextLine();

                    System.out.print("Enter Number of Nights: ");
                    int nights = scanner.nextInt();
                    scanner.nextLine();

                    Reservation reservation = bookingService.confirmBooking(guestName, roomNumber, nights);
                    if (reservation != null) {
                        System.out.println("\n  Booking Confirmed!");
                        System.out.println("--------------------------------------------");
                        System.out.println(reservation);
                        System.out.println("--------------------------------------------");
                    }
                    break;

                case 3:
                    System.out.println("\n--- All Reservations ---");
                    bookingService.displayAllReservations();
                    break;

                case 4:
                    reportService.generateFullReport();
                    break;

                case 5:
                    System.out.print("Enter Guest Name to Search: ");
                    String searchName = scanner.nextLine();
                    reportService.searchByGuestName(searchName);
                    break;

                case 6:
                    System.out.println("\n  Thank you for using Book My Stay!");
                    System.out.println("============================================");
                    running = false;
                    break;

                default:
                    System.out.println("  Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
}