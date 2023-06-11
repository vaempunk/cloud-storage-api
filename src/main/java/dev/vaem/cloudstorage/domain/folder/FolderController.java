package dev.vaem.cloudstorage.domain.folder;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.vaem.cloudstorage.domain.file.FileInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@Validated
public class FolderController {

    @Autowired
    private FolderService folderService;

    @GetMapping(path = "/folders/{folderId}/info")
    public FolderInfo getFolderInfo(@PathVariable("folderId") String folderId) {
        return folderService.getFolderInfo(folderId);
    }

    @GetMapping(path = { "/folders", "/folders/{folderId}/subfolders" })
    public Page<FolderInfo> getAllByParentId(@PathVariable(name = "folderId", required = false) String folderId,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page) {
        return folderService.getAllByParentId(folderId, page);
    }

    @PostMapping(path = { "/folders{folderId}/subfolders", "/folders" })
    public FolderInfo createFolder(
            @PathVariable(name = "folderId", required = false) String parentId,
            @RequestBody @Valid FolderCreateRequest folderCreate) {
        return folderService.createFolder(parentId, folderCreate.name());
    }

    @PostMapping(path = "/folders/{folderId}/zip")
    public FileInfo zipFolder(@PathVariable("folderId") String folderId) throws IOException {
        return folderService.zipFolder(folderId);
    }

    @DeleteMapping(path = "/folders/{folderId}")
    public void deleteFolder(@PathVariable("folderId") String folderId) {
        folderService.deleteFolder(folderId);
    }

}
