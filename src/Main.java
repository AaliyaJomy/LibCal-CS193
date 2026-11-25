import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        String url = "https://calendar.lib.purdue.edu/spaces/availability/grid";
        String formBody = "lid=13748&gid=28429&eid=-1&seat=0&seatId=0&zone=0"
                + "&start=2026-11-24&end=2026-12-01&pageIndex=0&pageSize=18";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Referer", "https://calendar.lib.purdue.edu/spaces")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();
                
        try {
            /** 
            * Sends a request to Purdue server and decodes the response and reads as a Java String
            * shows a code depending on the status (200 = OK; 404 = Not Found; 500 = Server error)
            */
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + response.statusCode());
            
            if (response.statusCode() == 200) {
                LibCalParser parser = new LibCalParser();
                ArrayList<TimeSlot> allSlots = parser.parseSlots(response.body());

                RoomList rooms = new RoomList();
                FreeRoomFinder finder = new FreeRoomFinder();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String nowStr = LocalDateTime.now().format(formatter);
                ArrayList<TimeSlot> rawMatches = finder.getFreeSlotsNow(allSlots, rooms, nowStr);
                ArrayList<TimeSlot> uniqueFreeRooms = new ArrayList<>();

                for (int i = 0; i < rawMatches.size(); i++) {
                    TimeSlot slot = rawMatches.get(i);
                    if (!finder.alreadyAddedRoom(uniqueFreeRooms, slot.getItemId())) {
                        uniqueFreeRooms.add(slot);
                    }
                }

                System.out.println("Free rooms at " + nowStr + ": " + uniqueFreeRooms.size());
                for (int i = 0; i < uniqueFreeRooms.size(); i++) {
                    TimeSlot slot = uniqueFreeRooms.get(i);
                    int idx = rooms.findIndexByItemId(slot.getItemId());
                    System.out.println("- [" + rooms.getBuilding(idx) + "] "
                            + rooms.getRoomName(idx)
                            + " (ID " + slot.getItemId() + ") free until " + slot.getEnd());
                }
            }

        } catch (Exception e) {
            System.out.println("Could not fetch data: " + e.getMessage());
        }
    }
}