package com.catchingnow.sharetodelete;

/**
 * Created by heruoxin on 15/3/23.
 */
public class MyFileInfo {
    String fileName;
    long fileSize;
    String filePath;

    public MyFileInfo(String fileName, long fileSize, String filePath) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
    }
}
