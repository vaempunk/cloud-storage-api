package dev.vaem.cloudstorage.folder;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dev.vaem.cloudstorage.file.FileInfo;
import dev.vaem.cloudstorage.track.FolderTrack;
import dev.vaem.cloudstorage.track.TrackService;

@RestController
public class FolderController {

    @Autowired
    private FolderService folderService;

    @Autowired
    private TrackService trackService;

    @GetMapping(path = "/folders/{folderId}/info")
    public FolderInfo getFolderInfo(@PathVariable("folderId") UUID folderId) {
        return folderService.getFolderInfo(folderId);
    }

    @PostMapping(path = { "/folders{folderId}/subfolders", "/folders" })
    public FolderInfo createFolder(
            @PathVariable(name = "folderId", required = false) UUID parentId,
            @RequestBody FolderCreate folderCreate) {
        return folderService.createFolder(parentId, folderCreate.name());
    }

    @PostMapping(path = "/folders/{folderId}/zip")
    public FileInfo zipFolder(@PathVariable("folderId") UUID folderId) throws IOException {
        return folderService.zipFolder(folderId);
    }

    @PostMapping(path = "/folders/{folderId}/track")
    public FolderTrack trackFolder(@PathVariable("folderId") UUID folderId) throws IOException {
        return trackService.trackFolder(folderId);
    }

    @DeleteMapping(path = "/folders/{folderId}")
    public void deleteFolder(@PathVariable("folderId") UUID folderId) {
        folderService.deleteFolder(folderId);
    }

}
