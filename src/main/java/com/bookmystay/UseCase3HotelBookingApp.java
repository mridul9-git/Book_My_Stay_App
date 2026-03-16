import java.util.HashMap;
import java.util.Map;

public class UseCase3HotelBookingApp {

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

        public Room getRoom(String roomNumber) {
            return inventory.get(roomNumber);
        }

        public void updateAvailability(String roomNumber, boolean available) {
            Room room = inventory.get(roomNumber);
            if (room != null) {
                room.setAvailable(available);
                System.out.println("  Updated: Room " + roomNumber +
                        " is now " + (available ? "Available" : "Not Available"));
            } else {
                System.out.println("  Room " + roomNumber + " not found.");
            }
        }

        public void displayAllRooms() {
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-8s %-10s %-15s %-15s%n",
                    "Room No", "Type", "Price/Night", "Status");
            System.out.println("------------------------------------------------------------");
            for (Room room : inventory.values()) {
                System.out.println(room);
            }
            System.out.println("------------------------------------------------------------");
        }

        public void displayAvailableRooms() {
            System.out.println("\n--- Available Rooms ---");
            System.out.println("------------------------------------------------------------");
            for (Room room : inventory.values()) {
                if (room.isAvailable()) {
                    System.out.println(room);
                }
            }
            System.out.println("------------------------------------------------------------");
        }

        public int getTotalRooms() {
            return inventory.size();
        }

        public int getAvailableCount() {
            int count = 0;
            for (Room room : inventory.values()) {
                if (room.isAvailable()) count++;
            }
            return count;
        }
    }

    // ── Main Method ────────────────────────────────
    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("   Book My Stay                             ");
        System.out.println("   UC3 - Centralized Room Inventory Mgmt    ");
        System.out.println("============================================");
        System.out.println();

        RoomInventory inventory = new RoomInventory();

        System.out.println("All Rooms in Inventory:");
        inventory.displayAllRooms();

        inventory.displayAvailableRooms();

        System.out.println("\n--- Updating Availability ---");
        inventory.updateAvailability("301", true);
        inventory.updateAvailability("501", true);
        inventory.updateAvailability("201", false);

        System.out.println("\nUpdated Inventory:");
        inventory.displayAllRooms();

        System.out.println();
        System.out.println("  Total Rooms   : " + inventory.getTotalRooms());
        System.out.println("  Available Now : " + inventory.getAvailableCount());
        System.out.println();
        System.out.println("  Inventory management loaded successfully.");
        System.out.println("============================================");
    }
}