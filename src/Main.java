import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) {
        String url = "https://calendar.lib.purdue.edu/spaces";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
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