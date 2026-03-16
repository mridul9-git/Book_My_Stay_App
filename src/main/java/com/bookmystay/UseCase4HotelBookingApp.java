import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UseCase4HotelBookingApp {

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

    // ── RoomSearchService class ────────────────────
    static class RoomSearchService {
        private RoomInventory inventory;

        public RoomSearchService(RoomInventory inventory) {
            this.inventory = inventory;
        }

        public void searchByRoomType(String typeInput) {
            boolean found = false;
            System.out.println("\n--- Search Results for Type: " + typeInput.toUpperCase() + " ---");
            System.out.println("------------------------------------------------------------");
            for (Room room : inventory.getInventory().values()) {
                if (room.getRoomType().toString().equalsIgnoreCase(typeInput)
                        && room.isAvailable()) {
                    System.out.println(room);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("  No available rooms found for type: " + typeInput);
            }
            System.out.println("------------------------------------------------------------");
        }

        public void searchByMaxPrice(double maxPrice) {
            boolean found = false;
            System.out.println("\n--- Search Results for Max Price: Rs." + maxPrice + " ---");
            System.out.println("------------------------------------------------------------");
            for (Room room : inventory.getInventory().values()) {
                if (room.getPricePerNight() <= maxPrice && room.isAvailable()) {
                    System.out.println(room);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("  No available rooms found under Rs." + maxPrice);
            }
            System.out.println("------------------------------------------------------------");
        }

        public void checkRoomAvailability(String roomNumber) {
            Room room = inventory.getInventory().get(roomNumber);
            System.out.println("\n--- Availability Check for Room: " + roomNumber + " ---");
            if (room != null) {
                System.out.println(room);
            } else {
                System.out.println("  Room " + roomNumber + " does not exist.");
            }
            System.out.println("------------------------------------------------------------");
        }
    }

    // ── Main Method ────────────────────────────────
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("============================================");
        System.out.println("   Book My Stay                             ");
        System.out.println("   UC4 - Room Search & Availability Check   ");
        System.out.println("============================================");
        System.out.println();

        RoomInventory inventory       = new RoomInventory();
        RoomSearchService searchService = new RoomSearchService(inventory);

        boolean running = true;
        while (running) {
            System.out.println("\n--- Room Search Menu ---");
            System.out.println("1. View All Available Rooms");
            System.out.println("2. Search by Room Type");
            System.out.println("3. Search by Max Price");
            System.out.println("4. Check Room Availability by Room Number");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\nAll Available Rooms:");
                    inventory.displayAvailableRooms();
                    break;

                case 2:
                    System.out.print("Enter Room Type (SINGLE/DOUBLE/SUITE/DELUXE/FAMILY): ");
                    String type = scanner.nextLine();
                    searchService.searchByRoomType(type);
                    break;

                case 3:
                    System.out.print("Enter Maximum Price per Night (Rs.): ");
                    double maxPrice = scanner.nextDouble();
                    searchService.searchByMaxPrice(maxPrice);
                    break;

                case 4:
                    System.out.print("Enter Room Number: ");
                    String roomNum = scanner.nextLine();
                    searchService.checkRoomAvailability(roomNum);
                    break;

                case 5:
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