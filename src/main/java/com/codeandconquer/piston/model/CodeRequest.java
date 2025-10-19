package com.codeandconquer.piston.model;

import java.util.List;

public class CodeRequest {
    public String language;
    public String version;
    public List<File> files;
    public String[] args = new String[0];

    public CodeRequest(String language, String version, String content) {
        this.language = language;
        this.version = version;
        this.files = List.of(new File(content));
    }

    public static class File {
        public String name = "Main"; // default name required by Piston
        public String content;

        public File(String content) {
            this.content = content;
        }
    }
}
