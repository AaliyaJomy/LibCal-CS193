import java.util.ArrayList;

public class RoomList {
    private ArrayList<Integer> itemIds = new ArrayList<>();
    private ArrayList<String> buildings = new ArrayList<>();
    private ArrayList<String> roomNames = new ArrayList<>();

    public RoomList() {
        addRoom(107398, "HSSE", "HSSE 145");
        addRoom(107410, "WALC", "WALC 2141");
        addRoom(107409, "WALC", "WALC 2020");
        addRoom(107411, "WALC", "WALC 3060");
        addRoom(107412, "WALC", "WALC 3068");
        addRoom(107413, "WALC", "WALC 3076");
        addRoom(219883, "WALC", "WALC 1134");
        addRoom(107397, "HSSE", "HSSE 142");
    }

    public void addRoom(int itemId, String building, String roomName) {
        itemIds.add(itemId);
        buildings.add(building);
        roomNames.add(roomName);
    }

    public int findIndexByItemId(int itemId) {
        for (int i = 0; i < itemIds.size(); i++) {
            if (itemIds.get(i) == itemId) {
                return i;
            }
        }
        return -1;
    }

    public boolean isTargetBuilding(int index) {
        if (index < 0 || index >= itemIds.size()) {
            return false;
        }
        String b = buildings.get(index);
        return "WALC".equalsIgnoreCase(b)
                || "Hicks".equalsIgnoreCase(b)
                || "HSSE".equalsIgnoreCase(b);
    }

    public String getBuilding(int index) {
        return buildings.get(index);
    }

    public String getRoomName(int index) {
        return roomNames.get(index);
    }
}