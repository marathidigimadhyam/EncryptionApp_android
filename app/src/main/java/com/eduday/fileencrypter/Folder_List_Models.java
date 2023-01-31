package com.eduday.fileencrypter;

public class Folder_List_Models {

    public String folder_title;
    public String folder_path;
    public int folder_notes_files_count;
    public int folder_video_files_count;


    public String getFolder_title() {
        return folder_title;
    }

    public void setFolder_title(String folder_title) {
        this.folder_title = folder_title;
    }

    public int getFolder_notes_files_count() {
        return folder_notes_files_count;
    }

    public void setFolder_notes_files_count(int folder_notes_files_count) {
        this.folder_notes_files_count = folder_notes_files_count;
    }

    public int getFolder_video_files_count() {
        return folder_video_files_count;
    }

    public void setFolder_video_files_count(int folder_video_files_count) {
        this.folder_video_files_count = folder_video_files_count;
    }

    public String getFolder_path() {
        return folder_path;
    }

    public void setFolder_path(String folder_path) {
        this.folder_path = folder_path;
    }

    public Folder_List_Models(String Folder_title, String Folder_path, int Folder_notes_files_count, int Folder_video_files_count) {
        this.folder_path = Folder_path;
        this.folder_title = Folder_title;
        this.folder_notes_files_count = Folder_notes_files_count;
        this.folder_video_files_count = Folder_video_files_count;

    }


}
