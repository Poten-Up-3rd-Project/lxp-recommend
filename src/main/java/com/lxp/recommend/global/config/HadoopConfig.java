package com.lxp.recommend.global.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Configuration
public class HadoopConfig {

    @PostConstruct
    public void setupHadoopHome() {
        if (!System.getProperty("os.name", "").toLowerCase().contains("windows")) {
            return;
        }

        if (System.getProperty("hadoop.home.dir") != null) {
            return;
        }

        try {
            Path hadoopHome = Files.createTempDirectory("hadoop");
            Path binDir = hadoopHome.resolve("bin");
            Files.createDirectories(binDir);
            Files.createFile(binDir.resolve("winutils.exe"));

            System.setProperty("hadoop.home.dir", hadoopHome.toString());
            log.info("Set hadoop.home.dir to: {}", hadoopHome);
        } catch (IOException e) {
            log.warn("Failed to create temporary hadoop home directory", e);
        }
    }
}
