package com.eduday.fileencrypter;

public class File_List_Models {

    public String file_title;
    public String file_path;
    public String file_size;
    public String file_type,folderFilelength,folder_location;
    public boolean doneOrNor;

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getFile_type() {
        return file_type;
    }

    public String getFile_title() {
        return file_title;
    }

    public void setFile_title(String file_title) {
        this.file_title = file_title;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }



    public String getFolderFilelength() {
        return folderFilelength;
    }

    public void setFolderFilelength(String folderFilelength) {
        this.folderFilelength = folderFilelength;
    }

    public String getFolder_location() {
        return folder_location;
    }

    public void setFolder_location(String folder_location) {
        this.folder_location = folder_location;
    }

    public File_List_Models(String File_title, String File_path, String File_size, String File_type, boolean doneOrNor, String folderFilelength, String folder_location) {
        this.file_path = File_path;
        this.file_title = File_title;
        this.file_size = File_size;
        this.file_type = File_type;
        this.doneOrNor = doneOrNor;
        this.folderFilelength = folderFilelength;
        this.folder_location = folder_location;
    }

    public boolean isDoneOrNor() {
        return doneOrNor;
    }

    public void setDoneOrNor(boolean doneOrNor) {
        this.doneOrNor = doneOrNor;
    }
}