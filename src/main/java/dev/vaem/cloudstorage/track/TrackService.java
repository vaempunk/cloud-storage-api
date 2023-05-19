package dev.vaem.cloudstorage.track;

import java.io.IOException;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.vaem.cloudstorage.filesystem.StorageService;
import dev.vaem.cloudstorage.folder.FolderInfoRepository;

@Service
public class TrackService {

    @Autowired
    private FolderTrackRepository folderTrackRepository;

    @Autowired
    private FolderInfoRepository folderInfoRepository;

    @Autowired
    private StorageService storageService;

    @Transactional(rollbackFor = IOException.class)
    public FolderTrack trackFolder(UUID folderId) throws IOException {
        var folderInfo = folderInfoRepository.findById(folderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var folderTrack = FolderTrack.builder()
                .folderId(folderId)
                .date(OffsetDateTime.now())
                .build();
        folderTrackRepository.save(folderTrack);
        storageService.createZipTrack(Path.of(folderInfo.getPath()), folderTrack.getId());

        return folderTrack;
    }

}
