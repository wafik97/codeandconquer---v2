package com.codeandconquer.piston;

import com.codeandconquer.piston.model.CodeRequest;
import com.codeandconquer.piston.model.RunResponse;
import com.codeandconquer.piston.model.RunResponse.RunResult;
import com.codeandconquer.piston.service.PistonService;

public class Main {
    public static void main(String[] args) throws Exception {
        PistonService pistonService = new PistonService();

        String pythonCode = "print(\"hi im piston\")";
        CodeRequest request = new CodeRequest("python", "3", pythonCode); // Try Python 3



        RunResponse response = pistonService.execute(request);
        printRunResponse(response);
    }

    private static void printRunResponse(RunResponse response) {


        printR( response);


        if (response == null || response.run == null) {
            System.out.println("No output or error occurred.");
            return;
        }

        RunResult run = response.run;

        System.out.println("Exit Code: " + run.code);

        if (hasText(run.signal)) {
            System.out.println("Signal: " + run.signal);
        }

        if (hasText(run.message)) {
            System.out.println("Message: " + run.message);
        }

        if (hasText(run.reason)) {
            System.out.println("Reason: " + run.reason);
        }

        if (run.time > 0) {
            System.out.println("Execution Time: " + run.time + "s");
        }

        if (hasText(run.stdout)) {
            System.out.println("Standard Output:\n" + run.stdout);
        }

        if (hasText(run.stderr)) {
            System.out.println("Standard Error:\n" + run.stderr);
        }

        if (hasText(run.output)) {
            System.out.println("Combined Output:\n" + run.output);
        }

        if (!hasText(run.stdout) && !hasText(run.stderr) &&
                !hasText(run.message) && !hasText(run.signal)) {
            System.out.println("No output or error from the program.");
        }
    }


    private static void printR(RunResponse response) {
        try {
            System.out.println(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response == null || response.run == null) {
            System.out.println("No output or error occurred.");
            return;
        }
    }




    private static boolean hasText(String text) {
        return text != null && !text.trim().isEmpty();
    }
}
