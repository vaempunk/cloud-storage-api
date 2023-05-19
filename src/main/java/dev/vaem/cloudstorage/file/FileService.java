package dev.vaem.cloudstorage.file;

import java.io.IOException;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.vaem.cloudstorage.filesystem.StorageService;
import dev.vaem.cloudstorage.folder.FolderInfoRepository;

@Service
public class FileService {

    @Autowired
    private StorageService storageService;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private FolderInfoRepository folderInfoRepository;

    public FileInfo getFileInfo(UUID fileId) {
        return fileInfoRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Resource getChunk(UUID fileId, Optional<Long> offsetOpt, Optional<Long> length) throws IOException {
        var offset = offsetOpt.isPresent() ? offsetOpt.get() : 0;
        var fileInfo = fileInfoRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return storageService.getChunk(Path.of(fileInfo.getPath()), offset, length);
    }

    @Transactional(rollbackFor = IOException.class)
    public FileInfo createFile(UUID folderId, String name) throws IOException {
        String filePath = null;
        if (folderId != null) {
            var parentFolderPath = folderInfoRepository.findById(folderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                    .getPath();
            filePath = Path.of(parentFolderPath).resolve(name).toString(); //tostr
        } else {
            filePath = Path.of(name).toString();
        }
        var fileInfo = FileInfo.builder()
                .name(name)
                .path(filePath)
                .size((long) 0)
                .dateCreated(OffsetDateTime.now())
                .dateUpdated(OffsetDateTime.now())
                .build();
        fileInfoRepository.save(fileInfo);
        storageService.createFile(Path.of(fileInfo.getPath()));
        return fileInfo;
    }

    @Transactional(rollbackFor = IOException.class)
    public void addChunk(UUID fileId, byte[] chunk, Optional<Long> offsetOpt) throws IOException {
        var fileInfo = fileInfoRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var offset = offsetOpt.isPresent() ? offsetOpt.get() : 0;
        var sizeChange = storageService.addChunk(Path.of(fileInfo.getPath()), chunk, offset);

        fileInfo.setSize(fileInfo.getSize() + sizeChange);
        fileInfo.setDateUpdated(OffsetDateTime.now());
        fileInfoRepository.save(fileInfo);
    }

    // @Deprecated
    // @Transactional(rollbackFor = IOException.class)
    // public void deleteChunk(UUID fileId, Optional<Long> offsetOpt, Optional<Long> lengthOpt) throws IOException {
    //     var fileInfo = fileInfoRepository.findById(fileId)
    //             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    //     var offset = offsetOpt.isPresent() ? offsetOpt.get() : 0;
    //     var sizeChange = storageService.deleteChunk(Path.of(fileInfo.getPath()), offset, lengthOpt);

    //     if (offset == 0 && lengthOpt.isEmpty()) {
    //         fileInfoRepository.delete(fileInfo);
    //     } else {
    //         fileInfo.setSize(fileInfo.getSize() + sizeChange);
    //         fileInfo.setDateUpdated(OffsetDateTime.now());
    //         fileInfoRepository.save(fileInfo);
    //     }
    // }

}
