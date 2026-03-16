import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UseCase6HotelBookingApp {

    // ── RoomType enum ──────────────────────────────
    enum RoomType {
        SINGLE, DOUBLE, SUITE, DELUXE, FAMILY
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
        private double totalCost;

        public Reservation(String guestName, Room room, int numberOfNights) {
            this.reservationId   = "RES" + (++counter);
            this.guestName       = guestName;
            this.assignedRoom    = room;
            this.numberOfNights  = numberOfNights;
            this.totalCost       = numberOfNights * room.getPricePerNight();
        }

        public String getReservationId() { return reservationId; }
        public String getGuestName()     { return guestName; }
        public Room getAssignedRoom()    { return assignedRoom; }
        public int getNumberOfNights()   { return numberOfNights; }
        public double getTotalCost()     { return totalCost; }

        @Override
        public String toString() {
            return String.format(
                    "  Reservation ID : %s%n" +
                            "  Guest Name     : %s%n" +
                            "  Room Number    : %s%n" +
                            "  Room Type      : %s%n" +
                            "  Nights         : %d%n" +
                            "  Price/Night    : Rs.%.0f%n" +
                            "  Total Cost     : Rs.%.0f",
                    reservationId,
                    guestName,
                    assignedRoom.getRoomNumber(),
                    assignedRoom.getRoomType(),
                    numberOfNights,
                    assignedRoom.getPricePerNight(),
                    totalCost);
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

            // Validate guest name
            if (guestName == null || guestName.trim().isEmpty()) {
                System.out.println("  ERROR: Guest name cannot be empty.");
                return null;
            }

            // Validate nights
            if (nights <= 0) {
                System.out.println("  ERROR: Number of nights must be greater than 0.");
                return null;
            }

            // Check room exists
            Room room = inventory.getRoom(roomNumber);
            if (room == null) {
                System.out.println("  ERROR: Room " + roomNumber + " does not exist.");
                return null;
            }

            // Check room availability
            if (!room.isAvailable()) {
                System.out.println("  ERROR: Room " + roomNumber + " is not available.");
                return null;
            }

            // Allocate room
            room.setAvailable(false);

            // Create reservation
            Reservation reservation = new Reservation(guestName, room, nights);
            reservations.put(reservation.getReservationId(), reservation);

            return reservation;
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

    // ── Main Method ────────────────────────────────
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("============================================");
        System.out.println("   Book My Stay                             ");
        System.out.println("   UC6 - Reservation Confirmation &         ");
        System.out.println("         Room Allocation                    ");
        System.out.println("============================================");
        System.out.println();

        RoomInventory inventory   = new RoomInventory();
        BookingService service    = new BookingService(inventory);

        boolean running = true;
        while (running) {
            System.out.println("\n--- Booking Menu ---");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Confirm Booking & Allocate Room");
            System.out.println("3. View All Reservations");
            System.out.println("4. Exit");
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

                    System.out.println("\n--- Processing Reservation ---");
                    Reservation reservation = service.confirmBooking(guestName, roomNumber, nights);

                    if (reservation != null) {
                        System.out.println("\n  Reservation Confirmed!");
                        System.out.println("--------------------------------------------");
                        System.out.println(reservation);
                        System.out.println("--------------------------------------------");
                    }
                    break;

                case 3:
                    System.out.println("\n--- All Reservations ---");
                    service.displayAllReservations();
                    break;

                case 4:
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