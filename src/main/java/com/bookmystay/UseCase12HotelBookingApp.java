import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.*;

public class UseCase12HotelBookingApp {

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
    static class Room implements Serializable {
        private static final long serialVersionUID = 1L;
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

    // ── Reservation class ──────────────────────────
    static class Reservation implements Serializable {
        private static final long serialVersionUID = 2L;
        private static int counter = 1000;
        private String reservationId;
        private String guestName;
        private Room assignedRoom;
        private int numberOfNights;
        private List<AddOnService> addOns;
        private double totalCost;
        private String status;

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
        public String getStatus()        { return status; }
        public void setStatus(String s)  { this.status = s; }
        public double getTotalCost()     { return totalCost; }

        @Override
        public String toString() {
            return String.format(
                    "  Reservation ID : %s%n" +
                            "  Guest Name     : %s%n" +
                            "  Room Number    : %s%n" +
                            "  Room Type      : %s%n" +
                            "  Nights         : %d%n" +
                            "  Total Cost     : Rs.%.0f%n" +
                            "  Status         : %s",
                    reservationId, guestName,
                    assignedRoom.getRoomNumber(),
                    assignedRoom.getRoomType(),
                    numberOfNights,
                    totalCost, status);
        }
    }

    // ── RoomInventory class ────────────────────────
    static class RoomInventory implements Serializable {
        private static final long serialVersionUID = 3L;
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

        public void restoreRoom(String roomNumber) {
            Room room = inventory.get(roomNumber);
            if (room != null) room.setAvailable(true);
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
    }

    // ── SystemState class ──────────────────────────
    static class SystemState implements Serializable {
        private static final long serialVersionUID = 4L;
        private RoomInventory inventory;
        private Map<String, Reservation> reservations;

        public SystemState(RoomInventory inventory, Map<String, Reservation> reservations) {
            this.inventory    = inventory;
            this.reservations = reservations;
        }

        public RoomInventory getInventory()               { return inventory; }
        public Map<String, Reservation> getReservations() { return reservations; }
    }

    // ── PersistenceService class ───────────────────
    static class PersistenceService {
        private static final String FILE_PATH = "system_state.dat";

        public void saveState(RoomInventory inventory, Map<String, Reservation> reservations) {
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(FILE_PATH))) {
                SystemState state = new SystemState(inventory, reservations);
                oos.writeObject(state);
                System.out.println("  State saved successfully to " + FILE_PATH);
            } catch (IOException e) {
                System.out.println("  ERROR saving state: " + e.getMessage());
            }
        }

        public SystemState loadState() {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                System.out.println("  No saved state found. Starting fresh.");
                return null;
            }
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(FILE_PATH))) {
                SystemState state = (SystemState) ois.readObject();
                System.out.println("  State loaded successfully from " + FILE_PATH);
                return state;
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("  ERROR loading state: " + e.getMessage());
                return null;
            }
        }

        public void deleteState() {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                file.delete();
                System.out.println("  Saved state deleted.");
            } else {
                System.out.println("  No saved state to delete.");
            }
        }
    }

    // ── BookingService class ───────────────────────
    static class BookingService {
        private RoomInventory inventory;
        private Map<String, Reservation> reservations;

        public BookingService(RoomInventory inventory, Map<String, Reservation> reservations) {
            this.inventory    = inventory;
            this.reservations = reservations;
        }

        public Reservation confirmBooking(String guestName, String roomNumber, int nights)
                throws IllegalArgumentException {
            if (guestName == null || guestName.trim().isEmpty()) {
                throw new IllegalArgumentException("Guest name cannot be empty.");
            }
            if (nights <= 0) {
                throw new IllegalArgumentException("Number of nights must be greater than 0.");
            }

            Room room = inventory.getRoom(roomNumber);
            if (room == null) {
                throw new IllegalArgumentException("Room " + roomNumber + " does not exist.");
            }
            if (!room.isAvailable()) {
                throw new IllegalArgumentException("Room " + roomNumber + " is not available.");
            }

            room.setAvailable(false);
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

        public Map<String, Reservation> getReservations() { return reservations; }
    }

    // ── FileAfterPersistenceService class ─────────────
    static class FilePersistenceService {
        private static final String TEXT_FILE = "booking_history.txt";

        public void writeToFile(Map<String, Reservation> reservations,
                                RoomInventory inventory) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(TEXT_FILE))) {
                writer.println("===== BOOK MY STAY - SYSTEM SNAPSHOT =====");
                writer.println("--- Reservations ---");
                if (reservations.isEmpty()) {
                    writer.println("No reservations.");
                } else {
                    for (Reservation r : reservations.values()) {
                        writer.println(r.toString().replaceAll("  ", ""));
                        writer.println("---");
                    }
                }
                writer.println("\n--- Room Inventory ---");
                for (Room room : inventory.getInventory().values()) {
                    writer.println(room.toString());
                }
                writer.println("==========================================");
                System.out.println("  Booking history written to " + TEXT_FILE);
            } catch (IOException e) {
                System.out.println("  ERROR writing file: " + e.getMessage());
            }
        }

        public void readFromFile() {
            File file = new File(TEXT_FILE);
            if (!file.exists()) {
                System.out.println("  No history file found.");
                return;
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(TEXT_FILE))) {
                String line;
                System.out.println("------------------------------------------------------------");
                while ((line = reader.readLine()) != null) {
                    System.out.println("  " + line);
                }
                System.out.println("------------------------------------------------------------");
            } catch (IOException e) {
                System.out.println("  ERROR reading file: " + e.getMessage());
            }
        }
    }

    // ── Main Method ────────────────────────────────
    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("   Book My Stay                             ");
        System.out.println("   UC12 - Data Persistence & System Recovery");
        System.out.println("============================================");
        System.out.println();

        PersistenceService persistenceService   = new PersistenceService();
        FilePersistenceService fileService      = new FilePersistenceService();

        // Try to load saved state
        RoomInventory inventory;
        Map<String, Reservation> reservations;

        System.out.println("--- Loading Saved State ---");
        SystemState savedState = persistenceService.loadState();

        if (savedState != null) {
            inventory    = savedState.getInventory();
            reservations = savedState.getReservations();
            System.out.println("  System restored from saved state.");
        } else {
            inventory    = new RoomInventory();
            reservations = new HashMap<>();
            System.out.println("  Starting with fresh inventory.");
        }

        BookingService bookingService = new BookingService(inventory, reservations);

        System.out.println("\n--- Current Inventory ---");
        inventory.displayAllRooms();

        // Make some bookings
        System.out.println("\n--- Making Bookings ---");
        try {
            Reservation r1 = bookingService.confirmBooking("Alice", "101", 3);
            System.out.println("  Booked: " + r1.getReservationId() + " for Alice");

            Reservation r2 = bookingService.confirmBooking("Bob", "201", 2);
            System.out.println("  Booked: " + r2.getReservationId() + " for Bob");

            Reservation r3 = bookingService.confirmBooking("Charlie", "401", 5);
            System.out.println("  Booked: " + r3.getReservationId() + " for Charlie");
        } catch (IllegalArgumentException e) {
            System.out.println("  INFO: " + e.getMessage());
        }

        // Save state
        System.out.println("\n--- Saving State ---");
        persistenceService.saveState(inventory, bookingService.getReservations());

        // Write to text file
        System.out.println("\n--- Writing Booking History to File ---");
        fileService.writeToFile(bookingService.getReservations(), inventory);

        // Display all reservations
        System.out.println("\n--- All Reservations ---");
        bookingService.displayAllReservations();

        // Simulate recovery
        System.out.println("\n--- Simulating System Recovery ---");
        SystemState recoveredState = persistenceService.loadState();
        if (recoveredState != null) {
            System.out.println("  Recovery successful!");
            System.out.println("\n--- Recovered Inventory ---");
            recoveredState.getInventory().displayAllRooms();
        }

        // Read history from text file
        System.out.println("\n--- Reading Booking History from File ---");
        fileService.readFromFile();

        System.out.println("\n  System operations completed successfully.");
        System.out.println("============================================");
    }
}