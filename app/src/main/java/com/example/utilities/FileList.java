package com.example.utilities;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

class FileItem {
    public String absolutePath = null;
    public String parentDir = null;
    public String fileName = null;

    private Path filePath = null;

    public FileItem(String path){
        filePath = Paths.get(path);
        absolutePath = filePath.toAbsolutePath().toString();
        parentDir = filePath.getParent().toString();
        fileName = filePath.getFileName().toString();
    }

    public long getModifiedDate(){
        return filePath.toFile().lastModified();
    }

    public long size(){
        return filePath.toFile().length();
    }

    @Override
    public String toString() {
        return filePath.toAbsolutePath().toString();
    }
}

public class FileList {

    private FileItem[] fileArray = null;

    public FileList(String[] fileList){
        fileArray = new FileItem[fileList.length];
        for(int i = 0; i < fileList.length; i++){
            fileArray[i] = new FileItem(fileList[i]);
        }
    }

    public FileList(FileItem[] fileList){
        fileArray = new FileItem[fileList.length];
        for(int i = 0; i < fileList.length; i++){
            fileArray[i] = fileList[i];
        }
    }

    public int count(){
        return fileArray.length;
    }

    public FileItem[] getFileArray() {
        return fileArray;
    }

    public FileItem[] getFileArray(int limit, int offset) {
        FileItem[] files = new FileItem[limit];
        for(int i = offset; i < offset+limit || i < fileArray.length; i++){
            files[i] = fileArray[i];
        }
        return files;
    }

    public FileList sortList(String by){
        Arrays.sort(fileArray, new Comparator<FileItem>() {
            @Override
            public int compare(FileItem f1, FileItem f2) {
                switch(by){
                    case "date": {
                        return (int)(f1.getModifiedDate() - f2.getModifiedDate());
                    }
                    case "size": {
                        return (int)(f1.size() - f2.size());
                    }
                    default : {
                        return f1.fileName.compareToIgnoreCase(f2.fileName);
                    }
                }
            }
        });
        return this;
    }
}
