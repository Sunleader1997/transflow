package org.sunyaxing.transflow.plubinnettydemoinput;

import com.alibaba.fastjson2.JSONObject;
import io.netty.handler.codec.http.multipart.HttpData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestData {

    private String uri;
    private JSONObject header;
    private List<FileData> fileData;

    public HttpRequestData() {
        this.uri = null;
        this.header = new JSONObject();
        this.fileData = new ArrayList<>();
    }

    public static class FileData {
        private String fileName;
        private String location;
        private Long dataSize;

        public FileData(File file) {
            this.fileName = file.getName();
            this.location = file.getAbsolutePath();
            this.dataSize = file.length();
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Long getDataSize() {
            return dataSize;
        }

        public void setDataSize(Long dataSize) {
            this.dataSize = dataSize;
        }
    }

    public JSONObject getHeader() {
        return header;
    }

    public void setHeader(JSONObject header) {
        this.header = header;
    }

    public List<FileData> getFileData() {
        return fileData;
    }

    public void setFileData(List<FileData> fileData) {
        this.fileData = fileData;
    }

    public void addFileData(FileData fileData) {
        this.fileData.add(fileData);
    }
    public void addHttpData(HttpData httpData) {
        try{
            FileData fileData = new FileData(httpData.getFile());
            addFileData(fileData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void addFileData(File file) {
        FileData fileData = new FileData(file);
        addFileData(fileData);
    }


    public void addHeader(String key, String value) {
        this.header.put(key, value);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
