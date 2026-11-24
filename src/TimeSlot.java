public class TimeSlot {
    private String start;
    private String end;
    private int itemId;
    private String className;

    // Constructor used when creating a TimeSlot manually in code
    public TimeSlot(String start, String end, int itemId, String className) {
        this.start = start;
        this.end = end;
        this.itemId = itemId;
        this.className = className;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public int getItemId() {
        return itemId;
    }

    public boolean isBooked() {
        return className != null && !className.isBlank();
    }

    @Override
    public String toString() {
        String status = isBooked() ? "BOOKED" : "FREE";
        return "Room " + itemId + " | " + start + " to " + end + " (" + status + ")";
    }
}