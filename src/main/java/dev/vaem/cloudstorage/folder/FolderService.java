package dev.vaem.cloudstorage.folder;

import java.io.IOException;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dev.vaem.cloudstorage.file.FileInfo;
import dev.vaem.cloudstorage.file.FileInfoRepository;
import dev.vaem.cloudstorage.filesystem.StorageService;

@Service
public class FolderService {

    @Autowired
    private FolderInfoRepository folderInfoRepository;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private StorageService storageService;

    public FolderInfo getFolderInfo(UUID folderId) {
        return folderInfoRepository.findById(folderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public FolderInfo createFolder(UUID parentId, String name) {
        var path = (parentId == null) ? Path.of(name).toString() // tostr
                : Path.of(folderInfoRepository.findById(parentId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                        .getPath()).resolve(name).toString(); // tostr
        var folderInfo = FolderInfo.builder()
                .name(name)
                .path(path)
                .createDate(OffsetDateTime.now())
                .build();
        folderInfoRepository.save(folderInfo);
        return folderInfo;
    }

    public void deleteFolder(UUID folderId) {
        if (!folderInfoRepository.existsById(folderId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        folderInfoRepository.deleteById(folderId);
    }

    public FileInfo zipFolder(UUID folderId) throws IOException {
        var folderInfo = folderInfoRepository.findById(folderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var fileInfo = storageService.zipFolder(Path.of(folderInfo.getPath()));
        fileInfoRepository.save(fileInfo);
        return fileInfo;
    }

}
