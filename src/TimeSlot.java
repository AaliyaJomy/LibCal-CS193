public class TimeSlot {
    private int itemId;
    private String start;
    private String end;
    private String className;

    public int getItemId() {
        return itemId;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getClassName() {
        return className;
    }

    public boolean isAvailable() {
        // In LibCal's grid JSON, an empty or null className indicates an unbooked slot.
        return className == null || className.isEmpty();
    }

    @Override
    public String toString() {
        return "RoomID=" + itemId + " | " + start + " -> " + end + " | free=" + isAvailable();
    }
}