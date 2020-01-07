package com.newtorn.BeanCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 磁盘
 */
public final class Disk {
    private static Disk disk = new Disk();
    private static byte[][] block;
    private static File dataFile;

    /**
     * 获取磁盘
     * @return
     * @throws IllegalAccessError
     */
    public static Disk getDisk() throws IllegalAccessError {
        dataFile = new File(Config.HARD_DISK_PATH);
        if (dataFile.exists()) {
            try {
                init();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalAccessError("File not exist");
        }
        return disk;
    }

    /**
     * @throws IOException
     */
    private static void init() throws IOException {
        block = new byte[Config.MAX_DISK_BLOCK_ENTRIES][Config.MAX_BLOCK_ENTRIES];
        FileInputStream input = new FileInputStream(dataFile);
        for (int i = 0; i < Config.MAX_DISK_BLOCK_ENTRIES; i++) {
            input.read(block[i], 0, Config.MAX_BLOCK_ENTRIES);
        }
        input.close();
    }

    public byte[][] getBlock() {
        return block;
    }
}
