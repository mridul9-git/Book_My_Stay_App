public class UseCase2HotelBookingApp {

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

        @Override
        public String toString() {
            return String.format("Room %-6s | Type: %-8s | Price: Rs.%-8.0f | Status: %s",
                    roomNumber,
                    roomType,
                    pricePerNight,
                    available ? "Available" : "Not Available");
        }
    }

    // ── RoomCatalogue class ────────────────────────
    static class RoomCatalogue {
        private Room[] rooms;

        public RoomCatalogue() {
            rooms = new Room[] {
                    new Room("101", RoomType.SINGLE,  1500, true),
                    new Room("102", RoomType.SINGLE,  1500, false),
                    new Room("201", RoomType.DOUBLE,  2500, true),
                    new Room("202", RoomType.DOUBLE,  2500, true),
                    new Room("301", RoomType.SUITE,   5000, false),
                    new Room("401", RoomType.DELUXE,  3500, true),
                    new Room("501", RoomType.FAMILY,  4000, false)
            };
        }

        public void displayAllRooms() {
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-8s %-10s %-15s %-15s%n", "Room No", "Type", "Price/Night", "Status");
            System.out.println("------------------------------------------------------------");
            for (Room room : rooms) {
                System.out.println(room);
            }
            System.out.println("------------------------------------------------------------");
        }

        public void displayAvailableRooms() {
            System.out.println("\n--- Available Rooms ---");
            System.out.println("------------------------------------------------------------");
            for (Room room : rooms) {
                if (room.isAvailable()) {
                    System.out.println(room);
                }
            }
            System.out.println("------------------------------------------------------------");
        }
    }

    // ── Main Method ────────────────────────────────
    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("   Book My Stay                             ");
        System.out.println("   UC2 - Room Types & Static Availability   ");
        System.out.println("============================================");
        System.out.println();

        RoomCatalogue catalogue = new RoomCatalogue();

        System.out.println("All Rooms:");
        catalogue.displayAllRooms();

        catalogue.displayAvailableRooms();

        System.out.println();
        System.out.println("  Room catalogue loaded successfully.");
        System.out.println("============================================");
    }
}