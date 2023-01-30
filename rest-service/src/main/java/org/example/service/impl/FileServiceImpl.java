package org.example.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;
import org.example.dao.AppDocumentDAO;
import org.example.dao.AppPhotoDAO;
import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;
import org.example.entity.BinaryContent;
import org.example.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;

    @Autowired
    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
    }

    @Override
    public AppDocument getDocument(String docId) {
        // TODO Добавить дешифровку ИД
        var id = Long.parseLong(docId);
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String photoId) {
        // TODO Добавить дешифровку ИД
        var id = Long.parseLong(photoId);
        return appPhotoDAO.findById(id).orElse(null);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            //TODO Добавить генерацию имени файла
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
