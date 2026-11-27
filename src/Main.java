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
        System.out.println("Enter a date (YYYY-MM-DD) or date + time (YYYY-MM-DD HH:mm), or press Enter for now:");
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

                RoomList rooms = new RoomList();
                FreeRoomFinder finder = new FreeRoomFinder();

                ArrayList<TimeSlot> matches = finder.getFreeSlotsByUserInput(allSlots, rooms, userInput);

                if (isDateOnly) {
                    System.out.println("Open slots on " + userInput + ": " + matches.size());
                    for (int i = 0; i < matches.size(); i++) {
                        TimeSlot slot = matches.get(i);
                        int idx = rooms.findIndexByItemId(slot.getItemId());
                        System.out.println("- [" + rooms.getBuilding(idx) + "] "
                                + rooms.getRoomName(idx)
                                + " (ID " + slot.getItemId() + "): "
                                + slot.getStart() + " to " + slot.getEnd());
                    }
                } else {
                    ArrayList<TimeSlot> uniqueFreeRooms = new ArrayList<>();
                    for (int i = 0; i < matches.size(); i++) {
                        TimeSlot slot = matches.get(i);
                        if (!finder.alreadyAddedRoom(uniqueFreeRooms, slot.getItemId())) {
                            uniqueFreeRooms.add(slot);
                        }
                    }

                    System.out.println("Free rooms at " + userInput + ": " + uniqueFreeRooms.size());
                    for (int i = 0; i < uniqueFreeRooms.size(); i++) {
                        TimeSlot slot = uniqueFreeRooms.get(i);
                        int idx = rooms.findIndexByItemId(slot.getItemId());
                        System.out.println("- [" + rooms.getBuilding(idx) + "] "
                                + rooms.getRoomName(idx)
                                + " (ID " + slot.getItemId() + ") free until " + slot.getEnd());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Could not fetch data: " + e.getMessage());
        }
    }
}