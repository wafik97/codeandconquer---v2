package com.codeandconquer.piston.service;

import com.codeandconquer.piston.model.CodeRequest;
import com.codeandconquer.piston.model.RunResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PistonService {

    private static final String API_URL = "https://emkc.org/api/v2/piston/execute";
    private final ObjectMapper mapper = new ObjectMapper();

    public RunResponse execute(CodeRequest request) throws Exception {
        // Prepare connection
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Write request body
        String json = mapper.writeValueAsString(request);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }

        if (conn.getResponseCode() == 200) {
            return mapper.readValue(conn.getInputStream(), RunResponse.class);
        } else {
            System.out.println("Error: " + conn.getResponseCode());
            String errorJson = new String(conn.getErrorStream().readAllBytes());
            System.out.println("Error response body: " + errorJson);
            return null;
        }

    }
}
