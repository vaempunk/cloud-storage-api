package dev.vaem.cloudstorage.domain.file;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/files/{fileId}/info")
    public FileInfo getFileInfo(@PathVariable("fileId") String fileId) {
        return fileService.getFileInfo(fileId);
    }

    @GetMapping(path = { "/files", "/folders/{folderId}/files" })
    public Page<FileInfo> getAllFiles(@PathVariable(name = "folderId", required = false) String folderId,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        return fileService.getAllByFolderId(folderId, page);
    }

    @GetMapping(path = "/files/{fileId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Resource getChunk(
            @PathVariable("fileId") String fileId,
            @RequestParam(name = "offset", required = false) Optional<Long> offset,
            @RequestParam(name = "length", required = false) Optional<Long> length)
            throws IOException {
        return fileService.getChunk(fileId, offset, length);
    }

    @PostMapping(path = { "/files", "/folders/{folderId}/files" }, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FileInfo createFile(
            @PathVariable(name = "folderId", required = false) String folderId,
            @RequestParam MultipartFile file)
            throws IOException {
        return fileService.uploadFile(folderId, file);
    }

    @PatchMapping(path = "/files/{fileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uploadChunk(
            @PathVariable("fileId") String fileId,
            @RequestParam("chunk") MultipartFile chunk,
            @RequestParam(name = "offset", required = false) Optional<Long> offset)
            throws IOException {
        fileService.addChunk(fileId, chunk.getBytes(), offset);
    }

    // @Deprecated
    @DeleteMapping("/files/{fileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(
            @PathVariable("fileId") String fileId) throws IOException {
        fileService.deleteFile(fileId);
    }

}
