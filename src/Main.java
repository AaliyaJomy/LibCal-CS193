import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
            * and then returns the actual text sent by the server (JSON room data)
            */
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + response.statusCode());
            System.out.println(response.body());
        } catch (Exception e) {
            System.out.println("Could not fetch data: " + e.getMessage());
        }
    }
}