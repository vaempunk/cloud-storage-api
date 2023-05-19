package dev.vaem.cloudstorage.filesystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dev.vaem.cloudstorage.config.AppProperties;
import dev.vaem.cloudstorage.file.FileInfo;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

@Service
public class StorageService {

    private Path basePath;
    private Path historyPath;

    public StorageService(AppProperties applicationProperties) {
        this.basePath = Path.of(applicationProperties.getBasePath());
        this.historyPath = Path.of(applicationProperties.getHistoryPath());
    }

    public void createFile(Path filePath) throws IOException {
        var absPath = basePath.resolve(filePath);
        Files.createDirectories(absPath.getParent());
        Files.createFile(absPath);
    }

    public Resource getChunk(Path filePath, long offset, Optional<Long> lengthOpt) throws IOException {
        ByteArrayResource chunk = null;
        var absPath = basePath.resolve(filePath);

        var length = lengthOpt.isPresent() ? lengthOpt.get() : Files.size(absPath) - offset;

        try (var fileChan = Files.newByteChannel(absPath)) {
            fileChan.position(offset);
            var buffer = ByteBuffer.allocate((int) length);
            fileChan.read(buffer);
            chunk = new ByteArrayResource(buffer.array());
        }

        return chunk;
    }

    public long addChunk(Path filePath, byte[] chunk, long offset) throws IOException {
        var absPath = basePath.resolve(filePath);
        var oldSize = Files.size(absPath);

        try (var fileChan = Files.newByteChannel(absPath, StandardOpenOption.WRITE)) {
            fileChan.position(offset);
            fileChan.write(ByteBuffer.wrap(chunk));
        }

        var newSize = Files.size(absPath);
        return oldSize - newSize;
    }

    public FileInfo zipFolder(Path folderPath) throws IOException {
        var zipPath = Path.of(folderPath.toString() + ".zip");
        var zipAbsPath = basePath.resolve(zipPath);

        try (var zipFile = new ZipFile(zipAbsPath.toString())) {
            zipFile.addFolder(basePath.resolve(folderPath).toFile());
        } catch (ZipException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
        
        var fileInfo = FileInfo.builder()
                .name(zipPath.getFileName().toString())
                .path(zipPath.toString())
                .size(Files.size(zipAbsPath))
                .dateCreated(OffsetDateTime.now())
                .dateUpdated(OffsetDateTime.now())
                .build();

        return fileInfo;
    }

    public void createZipTrack(Path folderPath, UUID trackId) throws IOException {
        Files.createDirectories(historyPath.resolve(folderPath));
        var zipPath = Path.of(folderPath.resolve(trackId.toString()) + ".zip");
        var zipAbsPath = historyPath.resolve(zipPath);

        try (var zipFile = new ZipFile(zipAbsPath.toString())) {
            zipFile.addFolder(basePath.resolve(folderPath).toFile());
        } catch (ZipException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    // @Deprecated
    // public long deleteChunk(Path filePath, long offset, Optional<Long> opLength) throws IOException {
    //     var absPath = basePath.resolve(filePath);
    //     var oldSize = Files.size(absPath);

    //     if (offset == 0 && opLength.isEmpty()) {
    //         Files.delete(absPath);
    //         return -oldSize;
    //     }
    //     var length = opLength.isPresent() ? opLength.get() : Files.size(absPath) - offset;

    //     try (var fileChan = Files.newByteChannel(absPath, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
    //         var buffer = ByteBuffer.allocate(1024*1024);
    //         var remaining = fileChan.size() - offset - length;
    //         var donePosition = offset;
    //         while (remaining > 0) {
    //             buffer.clear();
    //             if (remaining < 1024*1024) {
    //                 buffer.limit((int) remaining);
    //             }
    //             fileChan.position(donePosition + length);
    //             fileChan.read(buffer);
    //             fileChan.position(donePosition);
    //             buffer.position(0);
    //             fileChan.write(buffer);

    //             donePosition += 1024*1024;
    //             remaining -= 1024*1024;
    //         }
    //         fileChan.truncate(fileChan.size() - length);
    //     }

    //     var newSize = Files.size(absPath);
    //     return oldSize - newSize;
    // }

}