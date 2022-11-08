package com.example.utilities;

public class Schema {
    public static String tagToFiles = "tag_to_files(" +
            "tag_uid INTEGER PRIMARY KEY," +
            "file_list TEXT)";

    public static String tagList = "tags(" +
            "tag_uid INTEGER PRIMARY KEY," +
            "tag_name TEXT UNIQUE); " +
            "CREATE INDEX tag_shortener ON tags(tag_name)";

    public static String fileToTags = "file_to_tags(" +
            "absolute_path TEXT PRIMARY KEY," +
            "tags TEXT)";

    public static String tableTags = "tags";
    public static String tableTagToFiles = "tag_to_files";
    public static String tableFileToTags = "file_to_tags";

    public static class Tags {
        public static final String tag_uid = "tag_uid";
        public static final String tagName = "tag_name";
    }

    public static class TagToFiles {
        public static final String tag_uid = "tag_uid";
        public static final String fileList = "file_list";
    }

    public static class FileToTags {
        public static final String filePath = "absolute_path";
        public static final String tags = "tags";
    }
}
