package dev.vaem.cloudstorage.file;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/files/{fileId}/info")
    public FileInfo getFileInfo(@PathVariable("fileId") UUID fileId) {
        return fileService.getFileInfo(fileId);
    }

    @GetMapping(path = "/files/{fileId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Resource getChunk(
            @PathVariable("fileId") UUID fileId,
            @RequestParam(name = "offset", required = false) Optional<Long> offset,
            @RequestParam(name = "length", required = false) Optional<Long> length)
            throws IOException {
        return fileService.getChunk(fileId, offset, length);
    }

    @PostMapping(path = { "/files", "/folders/{folderId}/files" })
    @ResponseStatus(HttpStatus.CREATED)
    public FileInfo createFile(
            @PathVariable(name = "folderId", required = false) UUID folderId,
            @RequestBody FileCreate file)
            throws IOException {
        return fileService.createFile(folderId, file.name());
    }

    @PatchMapping(path = "/files/{fileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uploadChunk(
            @PathVariable("fileId") UUID fileId,
            @RequestParam("chunk") MultipartFile chunk,
            @RequestParam(name = "offset", required = false) Optional<Long> offset)
            throws IOException {
        fileService.addChunk(fileId, chunk.getBytes(), offset);
    }
    
    // @Deprecated
    // @DeleteMapping("/files/{fileId}")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    // public void deleteChunk(
    //         @PathVariable("fileId") UUID fileId,
    //         @RequestParam(name = "offset", required = false) Optional<Long> offset,
    //         @RequestParam(name = "length", required = false) Optional<Long> length) throws IOException {
    //     fileService.deleteChunk(fileId, offset, length);
    // }

}
