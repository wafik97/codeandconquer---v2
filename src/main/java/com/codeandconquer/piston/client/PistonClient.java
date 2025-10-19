package com.codeandconquer.piston.client;//package com.example.piston.client;
//
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//
//public class PistonClient {
//    private static final String BASE_URL = "https://emkc.org/api/v2/piston";
//    private final HttpClient httpClient;
//
//    public PistonClient() {
//        this.httpClient = HttpClient.newHttpClient();
//    }
//
//    public String runCode(String jsonPayload) throws Exception {
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(BASE_URL + "/execute"))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
//                .build();
//
//        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//        return response.body();
//    }
//}
