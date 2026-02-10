package com.lxp.recommend.application.port.out;

import java.io.InputStream;
import java.nio.file.Path;

public interface StoragePort {

    void upload(String key, Path filePath);

    void upload(String key, byte[] data);

    InputStream download(String key);

    byte[] downloadAsBytes(String key);

    boolean exists(String key);

    void delete(String key);
}
