package com.codeandconquer.piston;//package com.example.piston;
//
//import com.google.gson.Gson;
//import okhttp3.*;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class PistonClient {
//    private static final String PISTON_URL = "https://emkc.org/api/v2/piston/execute";
//    private final OkHttpClient client;
//    private final Gson gson;
//
//    public PistonClient() {
//        this.client = new OkHttpClient();
//        this.gson = new Gson();
//    }
//
//    public String runJavaCode(String code) throws IOException {
//        Map<String, Object> body = new HashMap<>();
//        body.put("language", "java");
//        body.put("version", "15.0.2");
//
//        List<Map<String, String>> files = new ArrayList<>();
//        Map<String, String> file = new HashMap<>();
//        file.put("name", "Main.java");
//        file.put("content", code);
//        files.add(file);
//
//        body.put("files", files);
//
//        RequestBody requestBody = RequestBody.create(
//                gson.toJson(body),
//                MediaType.parse("application/json")
//        );
//
//        Request request = new Request.Builder()
//                .url(PISTON_URL)
//                .post(requestBody)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                return "Error: " + response.code() + " -> " + response.body().string();
//            }
//            return response.body().string();
//        }
//    }
//
//
//
//    public static void main(String[] args) {
//        PistonClient piston = new PistonClient();
//
//        String code = """
//                public class Main {
//                    public static void main(String[] args) {
//                        System.out.println("Hello from Piston!");
//                    }
//                }
//                """;
//
//        try {
//            String result = piston.runJavaCode(code);
//            System.out.println(result);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
