import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class UseCase11HotelBookingApp {

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

    // ── InputValidator class ───────────────────────
    static class InputValidator {
        public static void validateGuestName(String name) throws IllegalArgumentException {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Guest name cannot be empty.");
            }
            if (name.trim().length() < 2) {
                throw new IllegalArgumentException("Guest name must be at least 2 characters.");
            }
            if (!name.trim().matches("[a-zA-Z ]+")) {
                throw new IllegalArgumentException("Guest name must contain letters only.");
            }
        }

        public static void validateNights(int nights) throws IllegalArgumentException {
            if (nights <= 0) {
                throw new IllegalArgumentException("Number of nights must be greater than 0.");
            }
            if (nights > 365) {
                throw new IllegalArgumentException("Number of nights cannot exceed 365.");
            }
        }

        public static void validateRoomNumber(String roomNumber) throws IllegalArgumentException {
            if (roomNumber == null || roomNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Room number cannot be empty.");
            }
        }

        public static void validateMenuChoice(int choice, int min, int max) throws IllegalArgumentException {
            if (choice < min || choice > max) {
                throw new IllegalArgumentException("Invalid choice. Please enter between " + min + " and " + max + ".");
            }
        }
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

        public void restoreRoom(String roomNumber) {
            Room room = inventory.get(roomNumber);
            if (room != null) room.setAvailable(true);
        }

        public void displayAvailableRooms() {
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-8s %-10s %-15s %-15s%n",
                    "Room No", "Type", "Price/Night", "Status");
            System.out.println("------------------------------------------------------------");
            for (Room room : inventory.values()) {
                if (room.isAvailable()) System.out.println(room);
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
        public int getNumberOfNights()   { return numberOfNights; }
        public String getStatus()        { return status; }
        public void setStatus(String s)  { this.status = s; }
        public double getTotalCost()     { return totalCost; }
        public List<AddOnService> getAddOns() { return addOns; }

        public void addService(AddOnService service) {
            addOns.add(service);
            totalCost += service.getPrice();
        }

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

        public Reservation confirmBooking(String guestName, String roomNumber, int nights)
                throws IllegalArgumentException {
            InputValidator.validateGuestName(guestName);
            InputValidator.validateNights(nights);
            InputValidator.validateRoomNumber(roomNumber);

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

    // ── CancellationService class ──────────────────
    static class CancellationService {
        private BookingService bookingService;
        private RoomInventory inventory;
        private List<String> cancellationLog;

        public CancellationService(BookingService bookingService, RoomInventory inventory) {
            this.bookingService  = bookingService;
            this.inventory       = inventory;
            this.cancellationLog = new ArrayList<>();
        }

        public void cancelReservation(String reservationId) throws IllegalArgumentException {
            Reservation reservation = bookingService.getReservation(reservationId);

            if (reservation == null) {
                throw new IllegalArgumentException("Reservation " + reservationId + " not found.");
            }
            if (reservation.getStatus().equals("CANCELLED")) {
                throw new IllegalArgumentException("Reservation " + reservationId + " is already cancelled.");
            }

            reservation.setStatus("CANCELLED");
            inventory.restoreRoom(reservation.getAssignedRoom().getRoomNumber());

            String log = "Cancelled: " + reservationId +
                    " | Guest: " + reservation.getGuestName() +
                    " | Room: " + reservation.getAssignedRoom().getRoomNumber();
            cancellationLog.add(log);

            System.out.println("\n  Cancellation Successful!");
            System.out.println("--------------------------------------------");
            System.out.println(reservation);
            System.out.println("--------------------------------------------");
            System.out.println("  Room " + reservation.getAssignedRoom().getRoomNumber() +
                    " is now available again.");
        }
    }

    // ── BookingSimulator (Thread) class ───────────────
    static class BookingSimulator extends Thread {
        private BookingService bookingService;
        private String guestName;
        private String roomNumber;
        private int nights;
        private static AtomicInteger successCount = new AtomicInteger(0);
        private static AtomicInteger failCount    = new AtomicInteger(0);

        public BookingSimulator(BookingService bookingService,
                                String guestName, String roomNumber, int nights) {
            this.bookingService = bookingService;
            this.guestName      = guestName;
            this.roomNumber     = roomNumber;
            this.nights         = nights;
        }

        @Override
        public void run() {
            try {
                synchronized (bookingService) {
                    Reservation reservation = bookingService.confirmBooking(guestName, roomNumber, nights);
                    if (reservation != null) {
                        successCount.incrementAndGet();
                        System.out.println("  [SUCCESS] Booking confirmed for " +
                                guestName + " | " + reservation.getReservationId());
                    }
                }
            } catch (IllegalArgumentException e) {
                failCount.incrementAndGet();
                System.out.println("  [FAILED]  Booking failed for " +
                        guestName + " - " + e.getMessage());
            }
        }

        public static int getSuccessCount() { return successCount.get(); }
        public static int getFailCount()    { return failCount.get(); }
    }

    // ── Main Method ────────────────────────────────
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("============================================");
        System.out.println("   Book My Stay                             ");
        System.out.println("   UC11 - Concurrent Booking Simulation     ");
        System.out.println("============================================");
        System.out.println();

        RoomInventory inventory           = new RoomInventory();
        BookingService bookingService     = new BookingService(inventory);
        CancellationService cancelService = new CancellationService(bookingService, inventory);

        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Make a Booking");
            System.out.println("3. Cancel a Booking");
            System.out.println("4. View All Reservations");
            System.out.println("5. Run Concurrent Booking Simulation");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                InputValidator.validateMenuChoice(choice, 1, 6);

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
                        int nights;
                        try {
                            nights = Integer.parseInt(scanner.nextLine().trim());
                        } catch (NumberFormatException e) {
                            System.out.println("  ERROR: Please enter a valid number for nights.");
                            break;
                        }

                        try {
                            Reservation reservation = bookingService.confirmBooking(
                                    guestName, roomNumber, nights);
                            System.out.println("\n  Booking Confirmed!");
                            System.out.println("--------------------------------------------");
                            System.out.println(reservation);
                            System.out.println("--------------------------------------------");
                        } catch (IllegalArgumentException e) {
                            System.out.println("  ERROR: " + e.getMessage());
                        }
                        break;

                    case 3:
                        System.out.print("Enter Reservation ID to Cancel: ");
                        String resId = scanner.nextLine();
                        try {
                            cancelService.cancelReservation(resId);
                        } catch (IllegalArgumentException e) {
                            System.out.println("  ERROR: " + e.getMessage());
                        }
                        break;

                    case 4:
                        System.out.println("\n--- All Reservations ---");
                        bookingService.displayAllReservations();
                        break;

                    case 5:
                        System.out.println("\n--- Running Concurrent Booking Simulation ---");
                        System.out.println("  Simulating 5 guests booking Room 201 simultaneously...");
                        System.out.println("------------------------------------------------------------");

                        List<Thread> threads = new ArrayList<>();
                        String[] guests = {"Alice", "Bob", "Charlie", "Diana", "Eve"};

                        for (String guest : guests) {
                            Thread t = new BookingSimulator(bookingService, guest, "201", 2);
                            threads.add(t);
                        }

                        for (Thread t : threads) t.start();
                        for (Thread t : threads) t.join();

                        System.out.println("------------------------------------------------------------");
                        System.out.println("  Simulation Complete!");
                        System.out.printf("  Successful Bookings : %d%n", BookingSimulator.getSuccessCount());
                        System.out.printf("  Failed Bookings     : %d%n", BookingSimulator.getFailCount());
                        System.out.println("------------------------------------------------------------");
                        break;

                    case 6:
                        System.out.println("\n  Thank you for using Book My Stay!");
                        System.out.println("============================================");
                        running = false;
                        break;
                }

            } catch (NumberFormatException e) {
                System.out.println("  ERROR: Please enter a valid number.");
            } catch (IllegalArgumentException e) {
                System.out.println("  ERROR: " + e.getMessage());
            }
        }
        scanner.close();
    }
}