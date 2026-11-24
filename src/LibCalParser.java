import java.util.ArrayList;

// Parses LibCal JSON manually using String methods and ArrayList (no external libraries).
public class LibCalParser {

    public ArrayList<TimeSlot> parseSlots(String json) {
        ArrayList<TimeSlot> slots = new ArrayList<>();
        if (json == null || json.isBlank()) {
            return slots;
        }

        // Split the JSON string at each slot object start so we handle one slot at a time.
        String[] blocks = json.split("\\{\"start\":");

        // Index 0 is the outer {"slots":[ part before the first slot, so start at 1.
        for (int i = 1; i < blocks.length; i++) {
            String block = "{\"start\":" + blocks[i];

            String start = readTextAfterKey(block, "\"start\":\"");
            String end = readTextAfterKey(block, "\"end\":\"");
            int itemId = readNumberAfterKey(block, "\"itemId\":");
            String className = readTextAfterKey(block, "\"className\":\"");

            // Only add if we found a start time and room itemId.
            if (start != null && itemId != 0) {
                slots.add(new TimeSlot(start, end, itemId, className));
            }
        }

        return slots;
    }

    // Finds a text value in quotes right after a key like "\"start\":\""
    private String readTextAfterKey(String text, String key) {
        int keyIndex = text.indexOf(key);
        if (keyIndex == -1) {
            return null;
        }

        int valueStart = keyIndex + key.length();
        int valueEnd = text.indexOf("\"", valueStart);
        if (valueEnd == -1) {
            return null;
        }

        return text.substring(valueStart, valueEnd);
    }

    // Finds an integer value right after a key like "\"itemId\":"
    private int readNumberAfterKey(String text, String key) {
        int keyIndex = text.indexOf(key);
        if (keyIndex == -1) {
            return 0;
        }

        int valueStart = keyIndex + key.length();
        int valueEnd = valueStart;

        // Move forward while characters are digits (0-9).
        while (valueEnd < text.length() && Character.isDigit(text.charAt(valueEnd))) {
            valueEnd++;
        }

        if (valueStart == valueEnd) {
            return 0;
        }

        return Integer.parseInt(text.substring(valueStart, valueEnd));
    }
}