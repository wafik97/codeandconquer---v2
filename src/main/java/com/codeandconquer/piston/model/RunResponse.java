package com.codeandconquer.piston.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RunResponse {
    public String language;
    public String version;
    public RunResult run;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RunResult {
        public String stdout;
        public String stderr;
        public int code;
        public String output;
        public String signal;
        public String message;
        public String reason;
        public double time;
    }
}
