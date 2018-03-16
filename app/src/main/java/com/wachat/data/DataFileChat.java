package com.wachat.data;//package com.wachat.data;
//
///**
// * Created by Ashish on 16-12-2015.
// */
//public class DataFileChat extends DataTextChat {
//
//    private String filePath = "";
//    private String fileUrl = "";
//    private String thumbBase64 = "";
//    private String thumbFilePath = "";
//    private int uploadProgress = 0;
//    private String uploadStatus  = UploadStatus.NOT_UPLOADED;
//    private String downloadStatus  = DownloadStatus.NOT_DOWNLOADED;
//
//    public String getFilePath() {
//        return filePath;
//    }
//
//    public void setFilePath(String filePath) {
//        this.filePath = filePath;
//    }
//
//    public String getFileUrl() {
//        return fileUrl;
//    }
//
//    public void setFileUrl(String fileUrl) {
//        this.fileUrl = fileUrl;
//    }
//
//    public int getUploadProgress() {
//        return uploadProgress;
//    }
//
//    public void setUploadProgress(int uploadProgress) {
//        this.uploadProgress = uploadProgress;
//    }
//
//    public String getUploadStatus() {
//        return uploadStatus;
//    }
//
//    public void setUploadStatus(String uploadStatus) {
//        this.uploadStatus = uploadStatus;
//    }
//    public String getThumbBase64() {
//        return thumbBase64;
//    }
//
//    public void setThumbBase64(String thumbBase64) {
//        this.thumbBase64 = thumbBase64;
//    }
//
//    public String getThumbFilePath() {
//        return thumbFilePath;
//    }
//
//    public void setThumbFilePath(String thumbFilePath) {
//        this.thumbFilePath = thumbFilePath;
//    }
//
//    public String getDownloadStatus() {
//        return downloadStatus;
//    }
//
//    public void setDownloadStatus(String downloadStatus) {
//        this.downloadStatus = downloadStatus;
//    }
//
//    public interface UploadStatus{
//        String UPLOADING = "1";
//        String UPLOADED = "2";
//        String NOT_UPLOADED = "0";
//    }
//
//    public interface DownloadStatus{
//        String DOWNLOADING = "1";
//        String DOWNLOADED = "2";
//        String NOT_DOWNLOADED = "0";
//    }
//}
