import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class FreeRoomFinder {

    private static final String FORMAT_ERROR_MESSAGE =
            "Invalid format. Please use YYYY-MM-DD or YYYY-MM-DD HH:mm.";

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_MINUTE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_TIME_SECOND_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ArrayList<TimeSlot> getFreeSlotsByUserInput(
            ArrayList<TimeSlot> allSlots,
            RoomList rooms,
            String userInput) {

        ArrayList<TimeSlot> result = new ArrayList<>();
        if (userInput == null || userInput.isBlank()) {
            throw new IllegalArgumentException(FORMAT_ERROR_MESSAGE);
        }

        String cleaned = normalize(userInput);
        boolean isDateOnly = (cleaned.length() == 10); // "YYYY-MM-DD"

        if (isDateOnly) {
            validateDate(cleaned);
        } else if (cleaned.length() == 16) {
            validateDateTimeMinute(cleaned);
            cleaned = cleaned + ":00";
        } else if (cleaned.length() == 19) {
            validateDateTimeSecond(cleaned);
        } else {
            throw new IllegalArgumentException(FORMAT_ERROR_MESSAGE);
        }

        for (int i = 0; i < allSlots.size(); i++) {
            TimeSlot slot = allSlots.get(i);

            // 1. Must not be booked
            if (slot.isBooked()) {
                continue;
            }

            // 2. Must be in WALC or HSSE
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
            if (chosenSlots.get(i) != null && chosenSlots.get(i).getItemId() == itemId) {
                return true;
            }
        }
        return false;
    }

    private void validateDate(String value) {
        try {
            LocalDate.parse(value, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(FORMAT_ERROR_MESSAGE);
        }
    }

    private void validateDateTimeMinute(String value) {
        try {
            LocalDateTime.parse(value, DATE_TIME_MINUTE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(FORMAT_ERROR_MESSAGE);
        }
    }

    private void validateDateTimeSecond(String value) {
        try {
            LocalDateTime.parse(value, DATE_TIME_SECOND_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(FORMAT_ERROR_MESSAGE);
        }
    }

    private String normalize(String data) {
        if (data == null) {
            return null;
        }
        return data.trim().replace('T', ' ');
    }
}