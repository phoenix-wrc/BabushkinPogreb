package site.ph0en1x.service;

import site.ph0en1x.entity.AppDocument;
import site.ph0en1x.entity.AppPhoto;
import site.ph0en1x.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
}
