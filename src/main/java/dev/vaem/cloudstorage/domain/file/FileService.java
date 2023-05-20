package dev.vaem.cloudstorage.domain.file;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import dev.vaem.cloudstorage.domain.filesystem.StorageService;
import dev.vaem.cloudstorage.domain.folder.FolderInfoRepository;

@Service
public class FileService {

    @Autowired
    private StorageService storageService;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private FolderInfoRepository folderInfoRepository;

    public FileInfo getFileInfo(String fileId) {
        return fileInfoRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Page<FileInfo> getAllByFolderId(String folderId, int page) {
        return fileInfoRepository.findAllByFolderId(folderId, PageRequest.of(page, 20));
    }

    public Resource getChunk(String fileId, Optional<Long> offsetOpt, Optional<Long> length) throws IOException {
        var offset = offsetOpt.isPresent() ? offsetOpt.get() : 0;
        var fileInfo = fileInfoRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return storageService.getChunk(Path.of(fileInfo.getPath()), offset, length);
    }

    @Transactional(rollbackFor = IOException.class)
    public FileInfo uploadFile(String folderId, MultipartFile file) throws IOException {
        var name = file.getOriginalFilename();
        String filePath = null;
        if (folderId != null) {
            var parentFolderPath = folderInfoRepository.findById(folderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                    .getPath();
            filePath = Path.of(parentFolderPath).resolve(name).toString(); // tostr
        } else {
            filePath = Path.of(name).toString();
        }
        var fileInfo = FileInfo.builder()
                .name(name)
                .path(filePath)
                .folderId(folderId)
                .isActive(true)
                .dateCreated(Instant.now())
                .dateUpdated(Instant.now())
                .build();
        fileInfoRepository.save(fileInfo);
        storageService.uploadFile(Path.of(fileInfo.getPath()), file.getBytes());
        return fileInfo;
    }

    @Transactional(rollbackFor = IOException.class)
    public void addChunk(String fileId, byte[] chunk, Optional<Long> offsetOpt) throws IOException {
        var fileInfo = fileInfoRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        fileInfo.setDateUpdated(Instant.now());
        fileInfoRepository.save(fileInfo);

        var offset = offsetOpt.isPresent() ? offsetOpt.get() : 0;
        storageService.addChunk(Path.of(fileInfo.getPath()), chunk, offset);
    }

    @Transactional(rollbackFor = IOException.class)
    public void deleteFile(String fileId) throws IOException {
        var fileInfo = fileInfoRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        storageService.deleteFile(Path.of(fileInfo.getPath()));

        if (fileInfo.getTracks().isEmpty()) {
            fileInfoRepository.delete(fileInfo);
        } else {
            fileInfo.setActive(false);
            fileInfoRepository.save(fileInfo);
        }
    }

    // @Transactional(rollbackFor = IOException.class)
    // public void deleteFile(String fileId, Optional<Long> offsetOpt, Optional<Long> lengthOpt) throws IOException {
    //     var fileInfo = fileInfoRepository.findById(fileId)
    //             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    //     var offset = offsetOpt.isPresent() ? offsetOpt.get() : 0;
    //     storageService.deleteChunk(Path.of(fileInfo.getPath()),
    //             offset, lengthOpt);

    //     if (offset == 0 && lengthOpt.isEmpty()) {
    //         fileInfoRepository.delete(fileInfo);
    //     } else {
    //         fileInfo.setDateUpdated(Instant.now());
    //         fileInfoRepository.save(fileInfo);
    //     }
    // }

}
