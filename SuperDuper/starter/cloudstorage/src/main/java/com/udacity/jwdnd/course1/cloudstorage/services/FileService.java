package com.udacity.jwdnd.course1.cloudstorage.services;
import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {
    private final FileMapper fileMapper;

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }
    public int insertFile(File file) {
        return fileMapper.insertFile(file);
    }
    public int deleteFile(File file){
        return fileMapper.deleteFile(file.getFileId());
    }
    public List<File> getFilesForCurrentUser(Integer userId) {
        return fileMapper.getFiles(userId);
    }
    public File getFile(int fileId){
        return fileMapper.getFile(fileId);
    }
    public boolean fileExist( Integer userId,String filename){
        if (filename.isEmpty())
            return true;
        File file=getFileByName(userId, filename);

        return file!=null;

    }
    public File getFileByName(Integer userId,String filename) {
        return fileMapper.existFile(userId, filename);
    }
}

