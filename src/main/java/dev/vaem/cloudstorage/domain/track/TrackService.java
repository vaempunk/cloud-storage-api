package dev.vaem.cloudstorage.domain.track;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.vaem.cloudstorage.domain.file.FileInfoRepository;
import dev.vaem.cloudstorage.domain.filesystem.StorageService;
import dev.vaem.cloudstorage.domain.folder.FolderInfoRepository;

@Service
public class TrackService {

    @Autowired
    private TrackRepository folderTrackRepository;

    @Autowired
    private FolderInfoRepository folderInfoRepository;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private StorageService storageService;

    public Track getFolderTrack(String trackId) {
        return folderTrackRepository.findById(trackId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Page<Track> getAllByFolderId(String folderId, int page) {
        return folderTrackRepository.findAllByFolderId(folderId, PageRequest.of(page, 20));
    }

    @Transactional(rollbackFor = IOException.class)
    public Track trackFolder(String folderId) throws IOException {
        var folderInfo = folderInfoRepository.findById(folderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var folderTrack = Track.builder()
                .folderId(folderId)
                .folderPath(folderInfo.getPath())
                .date(Instant.now())
                .build();
        folderTrackRepository.save(folderTrack);

        folderInfoRepository.addTrackByPathStartingWith(folderInfo.getPath(), folderTrack.getId());
        fileInfoRepository.addTrackByPathStartingWith(folderInfo.getPath(), folderTrack.getId());

        storageService.createZipTrack(Path.of(folderTrack.getFolderPath()), folderTrack.getId());

        return folderTrack;
    }

    @Transactional(rollbackFor = IOException.class)
    public void restoreTrack(String trackId) throws IOException {
        var folderTrack = folderTrackRepository.findById(trackId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        folderInfoRepository.deleteByPathStartingWithAndTracksIsEmpty(folderTrack.getFolderPath());
        fileInfoRepository.deleteByPathStartingWithAndTracksIsEmpty(folderTrack.getFolderPath());

        folderInfoRepository.streamByPathStartingWithAndTracksContaining(folderTrack.getFolderPath(), trackId).forEach(
                subfolderInfo -> {
                    subfolderInfo.setActive(true);
                    folderInfoRepository.save(subfolderInfo);
                });
        fileInfoRepository.streamByPathStartingWithAndTracksContaining(folderTrack.getFolderPath(), trackId).forEach(
                fileInfo -> {
                    fileInfo.setActive(true);
                    fileInfo.setDateCreated(Instant.now());
                    fileInfo.setDateUpdated(Instant.now());
                    fileInfoRepository.save(fileInfo);
                });

        storageService.restoreZipTrack(Path.of(folderTrack.getFolderPath()), trackId);
    }

    @Transactional(rollbackFor = IOException.class)
    public void deleteTrack(String trackId) throws IOException {
        var folderTrack = folderTrackRepository.findById(trackId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        folderTrackRepository.delete(folderTrack);
        folderInfoRepository.streamByPathStartingWithAndTracksContaining(folderTrack.getFolderPath(), trackId).forEach(
                subfolderInfo -> {
                    subfolderInfo.getTracks().remove(trackId);
                    folderInfoRepository.save(subfolderInfo);
                });
        fileInfoRepository.streamByPathStartingWithAndTracksContaining(folderTrack.getFolderPath(), trackId).forEach(
                fileInfo -> {
                    fileInfo.getTracks().remove(trackId);
                    fileInfoRepository.save(fileInfo);
                });
        storageService.deleteZipTrack(Path.of(folderTrack.getFolderPath()), trackId);
    }

}
