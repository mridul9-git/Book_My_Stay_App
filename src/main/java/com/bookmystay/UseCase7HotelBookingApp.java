import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UseCase7HotelBookingApp {

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

        public Reservation(String guestName, Room room, int numberOfNights) {
            this.reservationId  = "RES" + (++counter);
            this.guestName      = guestName;
            this.assignedRoom   = room;
            this.numberOfNights = numberOfNights;
            this.addOns         = new ArrayList<>();
            this.totalCost      = numberOfNights * room.getPricePerNight();
        }

        public String getReservationId() { return reservationId; }
        public String getGuestName()     { return guestName; }
        public Room getAssignedRoom()    { return assignedRoom; }

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
                    reservationId,
                    guestName,
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

            sb.append(String.format("  Total Cost     : Rs.%.0f", totalCost));
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

    // ── AddOnServiceManager class ──────────────────
    static class AddOnServiceManager {

        public void displayAvailableServices() {
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-5s %-25s %-10s%n", "No.", "Service", "Price");
            System.out.println("------------------------------------------------------------");
            int i = 1;
            for (AddOnService s : AddOnService.values()) {
                System.out.printf("%-5d %-25s Rs.%.0f%n", i++, s.getDisplayName(), s.getPrice());
            }
            System.out.println("------------------------------------------------------------");
        }

        public void addServiceToReservation(Reservation reservation, int choice) {
            AddOnService[] services = AddOnService.values();
            if (choice < 1 || choice > services.length) {
                System.out.println("  ERROR: Invalid service choice.");
                return;
            }
            AddOnService selected = services[choice - 1];
            reservation.addService(selected);
            System.out.println("  Added: " + selected.getDisplayName() +
                    " (Rs." + selected.getPrice() + ") to " +
                    reservation.getReservationId());
        }
    }

    // ── Main Method ────────────────────────────────
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("============================================");
        System.out.println("   Book My Stay                             ");
        System.out.println("   UC7 - Add-On Service Selection           ");
        System.out.println("============================================");
        System.out.println();

        RoomInventory inventory       = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);
        AddOnServiceManager addOnMgr  = new AddOnServiceManager();

        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Make a Booking");
            System.out.println("3. Add Services to Reservation");
            System.out.println("4. View All Reservations");
            System.out.println("5. Exit");
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
                    System.out.print("Enter Reservation ID: ");
                    String resId = scanner.nextLine();

                    Reservation res = bookingService.getReservation(resId);
                    if (res == null) {
                        System.out.println("  ERROR: Reservation not found.");
                        break;
                    }

                    boolean addingServices = true;
                    while (addingServices) {
                        System.out.println("\nAvailable Add-On Services:");
                        addOnMgr.displayAvailableServices();
                        System.out.println("0. Done adding services");
                        System.out.print("Select service number: ");
                        int serviceChoice = scanner.nextInt();
                        scanner.nextLine();

                        if (serviceChoice == 0) {
                            addingServices = false;
                        } else {
                            addOnMgr.addServiceToReservation(res, serviceChoice);
                        }
                    }

                    System.out.println("\nUpdated Reservation:");
                    System.out.println("--------------------------------------------");
                    System.out.println(res);
                    System.out.println("--------------------------------------------");
                    break;

                case 4:
                    System.out.println("\n--- All Reservations ---");
                    bookingService.displayAllReservations();
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