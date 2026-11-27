import java.util.ArrayList;

public class FreeRoomFinder {

public ArrayList<TimeSlot> getFreeSlotsByUserInput(            ArrayList<TimeSlot> allSlots,
            RoomList rooms,
            String userInput) {

        ArrayList<TimeSlot> result = new ArrayList<>();
        if (userInput == null || userInput.isBlank()) {
            return result;
        }

        String cleaned = normalize(userInput);
        boolean isDateOnly = (cleaned.length() == 10); // "YYYY-MM-DD"

        // If user typed "YYYY-MM-DD HH:mm", append ":00" so string comparison matches "YYYY-MM-DD HH:mm:ss"
        if (!isDateOnly && cleaned.length() == 16) {
            cleaned = cleaned + ":00";
        }

        for (int i = 0; i < allSlots.size(); i++) {
            TimeSlot slot = allSlots.get(i);

            // 1. Must not be booked
            if (slot.isBooked()) {
                continue;
            }

            // 2. Must be in WALC, Hicks, or HSSE
            int roomIndex = rooms.findIndexByItemId(slot.getItemId());
            if (!rooms.isTargetBuilding(roomIndex)) {
                continue;
            }

            String start = normalize(slot.getStart());
            String end = normalize(slot.getEnd());
            if (start == null || end == null) {
                continue;
            }

            // 3A. Date-only mode: any open slot starting on that YYYY-MM-DD date
            if (isDateOnly) {
                if (start.startsWith(cleaned)) {
                    result.add(slot);
                }
            }
            // 3B. Date + time mode: open slot covering that exact timestamp (start <= target < end)
            else {
                if (start.compareTo(cleaned) <= 0 && cleaned.compareTo(end) < 0) {
                    result.add(slot);
                }
            }
        }

        return result;
    }

    public boolean alreadyAddedRoom(ArrayList<TimeSlot> chosenSlots, int itemId) {
        for (int i = 0; i < chosenSlots.size(); i++) {
            if (chosenSlots.get(i).getItemId() == itemId) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String data) {
        if (data == null) {
            return null;
        }
        return data.trim().replace('T', ' ');
    }

    private String padSeconds(String data) {
        if (data != null && data.length() == 16) {
            return data + ":00";
        }
        return data;
    }
}