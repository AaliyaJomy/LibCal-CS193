import com.google.gson.Gson;
import java.util.ArrayList;

public class LibCalParser {
    // matches the outer "slots" wrapper that the json returns
    private static class GridResponse {
        ArrayList<TimeSlot> slots;
    }

    // takes raw json and returns an ArrayList<TimeSlot> of TimeSlot Objects
    public ArrayList<TimeSlot> parseSlots(String json) {
        Gson gson = new Gson();
        // moves the json text into GridResponse and each item inside "slots" into a Timeslot
        GridResponse response = gson.fromJson(json, GridResponse.class);
        if (response == null || response.slots == null) {
            return new ArrayList<>();
        }
        return response.slots;
    }
}