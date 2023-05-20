package dev.vaem.cloudstorage.domain.folder;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dev.vaem.cloudstorage.domain.file.FileInfo;
import dev.vaem.cloudstorage.domain.file.FileInfoRepository;
import dev.vaem.cloudstorage.domain.filesystem.StorageService;

@Service
public class FolderService {

    @Autowired
    private FolderInfoRepository folderInfoRepository;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private StorageService storageService;

    public FolderInfo getFolderInfo(String folderId) {
        return folderInfoRepository.findById(folderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Page<FolderInfo> getAllByParentId(String parentId, int page) {
        return folderInfoRepository.findAllByParentId(parentId, PageRequest.of(page, 20));
    }

    public FolderInfo createFolder(String parentId, String name) {
        var path = (parentId == null) ? Path.of(name).toString() // tostr
                : Path.of(folderInfoRepository.findById(parentId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                        .getPath()).resolve(name).toString(); // tostr
        var folderInfo = FolderInfo.builder()
                .name(name)
                .path(path)
                .parentId(parentId)
                .isActive(true)
                .dateCreated(Instant.now())
                .build();
        folderInfoRepository.save(folderInfo);
        return folderInfo;
    }

    public void deleteFolder(String folderId) {
        if (!folderInfoRepository.existsById(folderId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        folderInfoRepository.deleteById(folderId);
    }

    public FileInfo zipFolder(String folderId) throws IOException {
        var folderInfo = folderInfoRepository.findById(folderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var fileInfo = FileInfo.builder()
                .name(folderInfo.getName() + ".zip")
                .path(folderInfo.getPath() + ".zip")
                .folderId(folderInfo.getParentId())
                .isActive(true)
                .dateCreated(Instant.now())
                .dateUpdated(Instant.now())
                .build();
        fileInfoRepository.save(fileInfo);

        storageService.zipFolder(Path.of(folderInfo.getPath()));
        return fileInfo;
    }

}
