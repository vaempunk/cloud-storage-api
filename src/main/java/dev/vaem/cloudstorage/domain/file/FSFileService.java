package dev.vaem.cloudstorage.domain.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import dev.vaem.cloudstorage.config.AppProperties;
import dev.vaem.cloudstorage.domain.user.UserService;

@Service
class FSFileService {
    
    @Autowired
    private UserService userService;

    private Path basePath;

    public FSFileService(AppProperties applicationProperties) {
        this.basePath = Path.of(applicationProperties.getBasePath());
    }

    private Path makeUserBasePath() {
        return basePath.resolve(userService.userAccount().getId());
    }

    public void saveFile(Path filePath, byte[] body) throws IOException {
        var userBasePath = makeUserBasePath();
        var absPath = userBasePath.resolve(filePath);
        Files.createDirectories(absPath.getParent());
        Files.createFile(absPath);
        Files.write(absPath, body);
    }

    public void moveFile(Path filePath, Path newPath) throws IOException {
        var userBasePath = makeUserBasePath();
        var absPath = userBasePath.resolve(filePath);
        var absNewPath = userBasePath.resolve(newPath);
        Files.move(absPath, absNewPath);
    }

    public void copyFile(Path filePath, Path newPath) throws IOException {
        var userBasePath = makeUserBasePath();
        var absPath = userBasePath.resolve(filePath);
        var absNewPath = userBasePath.resolve(newPath);
        Files.copy(absPath, absNewPath);
    }

    public void deleteFile(Path filePath) throws IOException {
        var userBasePath = makeUserBasePath();
        var absPath = userBasePath.resolve(filePath);
        Files.delete(absPath);
    }

    public Resource getChunk(Path filePath, long offset, Optional<Long> lengthOpt) throws IOException {
        var userBasePath = makeUserBasePath();
        ByteArrayResource chunk = null;
        var absPath = userBasePath.resolve(filePath);

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
        var userBasePath = makeUserBasePath();
        var absPath = userBasePath.resolve(filePath);
        var oldSize = Files.size(absPath);

        try (var fileChan = Files.newByteChannel(absPath, StandardOpenOption.WRITE)) {
            fileChan.position(offset);
            fileChan.write(ByteBuffer.wrap(chunk));
        }

        var newSize = Files.size(absPath);
        return oldSize - newSize;
    }

}
