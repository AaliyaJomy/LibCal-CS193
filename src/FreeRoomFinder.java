import java.util.ArrayList;

public class FreeRoomFinder {

    public ArrayList<TimeSlot> getFreeSlotsNow(
            ArrayList<TimeSlot> allSlots,
            RoomList rooms,
            String currentDateTime) {

        ArrayList<TimeSlot> result = new ArrayList<>();
        String normalizedNow = normalize(currentDateTime);

        for (int i = 0; i < allSlots.size(); i++) {
            TimeSlot slot = allSlots.get(i);

            // 1. Must not be booked
            if (slot.isBooked()) {
                continue;
            }

            // 2. Must be a collaborative room in WALC, Hicks, or HSSE
            int roomIndex = rooms.findIndexByItemId(slot.getItemId());
            if (!rooms.isTargetBuilding(roomIndex)) {
                continue;
            }

            // 3. Must cover current time: start <= now < end
            String start = normalize(slot.getStart());
            String end = normalize(slot.getEnd());
            if (start != null && end != null
                    && start.compareTo(normalizedNow) <= 0
                    && normalizedNow.compareTo(end) < 0) {
                result.add(slot);
            }
        }

        return result;
    }

    // Avoid printing the same room twice if multiple overlapping slots match
    public boolean alreadyAddedRoom(ArrayList<TimeSlot> chosenSlots, int itemId) {
        for (int i = 0; i < chosenSlots.size(); i++) {
            if (chosenSlots.get(i).getItemId() == itemId) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String dt) {
        if (dt == null) {
            return null;
        }
        return dt.trim().replace('T', ' ');
    }
}