import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a date (YYYY-MM-DD) or date + time (YYYY-MM-DD HH:mm), or press Enter for Current Availability:");
        String userInput = scanner.nextLine().trim();

        if (userInput.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            userInput = LocalDateTime.now().format(formatter);
        }

        // Extract YYYY-MM-DD from the first 10 characters to query that day from LibCal
        if (userInput.length() < 10) {
            System.out.println("Invalid format. Please use YYYY-MM-DD or YYYY-MM-DD HH:mm.");
            return;
        }
        String queryDate = userInput.substring(0, 10);
        String nextDate = LocalDate.parse(queryDate).plusDays(1).toString();
        boolean isDateOnly = (userInput.length() == 10);

        String url = "https://calendar.lib.purdue.edu/spaces/availability/grid";
        String formBody = "lid=13748&gid=28429&eid=-1&seat=0&seatId=0&zone=0"
                + "&start=" + queryDate + "&end=" + nextDate + "&pageIndex=0&pageSize=18";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Referer", "https://calendar.lib.purdue.edu/spaces")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + response.statusCode());

            if (response.statusCode() == 200) {
                LibCalParser parser = new LibCalParser();
                ArrayList<TimeSlot> allSlots = parser.parseSlots(response.body());
                System.out.println("Parsed time slots: " + allSlots.size());
                System.out.println();

                RoomList rooms = new RoomList();
                FreeRoomFinder finder = new FreeRoomFinder();

                ArrayList<TimeSlot> matches = finder.getFreeSlotsByUserInput(allSlots, rooms, userInput);

                ArrayList<TimeSlot> rowsToPrint = new ArrayList<>();
                if (isDateOnly) {
                    rowsToPrint = matches;
                } else {
                    for (int i = 0; i < matches.size(); i++) {
                        TimeSlot slot = matches.get(i);
                        if (!finder.alreadyAddedRoom(rowsToPrint, slot.getItemId())) {
                            rowsToPrint.add(slot);
                        }
                    }
                }

                System.out.printf("%-8s | %-7s | %-19s | %-19s | %-6s%n",
                        "Building", "Room ID", "Start", "End", "Status");
                System.out.println("---------+---------+---------------------+---------------------+-------");

                for (int i = 0; i < rowsToPrint.size(); i++) {
                    TimeSlot slot = rowsToPrint.get(i);
                    int idx = rooms.findIndexByItemId(slot.getItemId());
                    String building = (idx >= 0) ? rooms.getBuilding(idx) : "UNKNOWN";
                    String status = slot.isBooked() ? "BOOKED" : "FREE";

                    System.out.printf("%-8s | %-7d | %-19s | %-19s | %-6s%n",
                            building,
                            slot.getItemId(),
                            slot.getStart(),
                            slot.getEnd(),
                            status);
                }
            }
        } catch (Exception e) {
            System.out.println("Could not fetch data: " + e);
        }
    }
}