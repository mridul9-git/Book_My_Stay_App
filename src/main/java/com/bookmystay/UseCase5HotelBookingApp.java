import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UseCase5HotelBookingApp {

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

        public Map<String, Room> getInventory() {
            return inventory;
        }

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

    // ── BookingRequest class ───────────────────────
    static class BookingRequest {
        private String guestName;
        private String roomNumber;
        private int numberOfNights;
        private double totalCost;

        public BookingRequest(String guestName, String roomNumber, int numberOfNights, double pricePerNight) {
            this.guestName      = guestName;
            this.roomNumber     = roomNumber;
            this.numberOfNights = numberOfNights;
            this.totalCost      = numberOfNights * pricePerNight;
        }

        public String getGuestName()    { return guestName; }
        public String getRoomNumber()   { return roomNumber; }
        public int getNumberOfNights()  { return numberOfNights; }
        public double getTotalCost()    { return totalCost; }

        @Override
        public String toString() {
            return String.format(
                    "  Guest      : %s%n" +
                            "  Room       : %s%n" +
                            "  Nights     : %d%n" +
                            "  Total Cost : Rs.%.0f",
                    guestName, roomNumber, numberOfNights, totalCost);
        }
    }

    // ── BookingProcessor class ─────────────────────
    static class BookingProcessor {
        private RoomInventory inventory;

        public BookingProcessor(RoomInventory inventory) {
            this.inventory = inventory;
        }

        public BookingRequest processBooking(String guestName, String roomNumber, int nights) {
            Room room = inventory.getRoom(roomNumber);

            if (room == null) {
                System.out.println("  ERROR: Room " + roomNumber + " does not exist.");
                return null;
            }

            if (!room.isAvailable()) {
                System.out.println("  ERROR: Room " + roomNumber + " is not available.");
                return null;
            }

            if (guestName == null || guestName.trim().isEmpty()) {
                System.out.println("  ERROR: Guest name cannot be empty.");
                return null;
            }

            if (nights <= 0) {
                System.out.println("  ERROR: Number of nights must be greater than 0.");
                return null;
            }

            // Mark room as not available
            room.setAvailable(false);

            BookingRequest request = new BookingRequest(guestName, roomNumber, nights, room.getPricePerNight());
            return request;
        }
    }

    // ── Main Method ────────────────────────────────
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("============================================");
        System.out.println("   Book My Stay                             ");
        System.out.println("   UC5 - Booking Request (Flow-Cross-Film)  ");
        System.out.println("============================================");
        System.out.println();

        RoomInventory inventory        = new RoomInventory();
        BookingProcessor processor     = new BookingProcessor(inventory);

        boolean running = true;
        while (running) {
            System.out.println("\n--- Booking Menu ---");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Make a Booking");
            System.out.println("3. Exit");
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

                    System.out.println("\n--- Processing Booking ---");
                    BookingRequest request = processor.processBooking(guestName, roomNumber, nights);

                    if (request != null) {
                        System.out.println("  Booking Successful!");
                        System.out.println("--------------------------------------------");
                        System.out.println(request);
                        System.out.println("--------------------------------------------");
                    }
                    break;

                case 3:
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