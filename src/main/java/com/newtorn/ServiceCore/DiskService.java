package com.newtorn.ServiceCore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.newtorn.BeanCore.FolderEntry;
import com.newtorn.BeanCore.Disk;
import com.newtorn.BeanCore.Config;
import com.newtorn.BeanCore.SpecialBlock;
import com.newtorn.ToolkitsCore.ByteUtil;

public class DiskService {
    private static byte[][] block = new byte[Config.MAX_DISK_BLOCK_ENTRIES][Config.MAX_BLOCK_ENTRIES];

    public void writeToDisk(byte[][] block) {
        File hardDisk = new File(Config.HARD_DISK_PATH);
        if (!hardDisk.exists()) {
            throw new IllegalAccessError("File not exist");
        }
        FileOutputStream output;
        try {
            output = new FileOutputStream(hardDisk);
            for (int i = 0; i < Config.MAX_DISK_BLOCK_ENTRIES; ++i)
                output.write(block[i], 0, Config.MAX_BLOCK_ENTRIES);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeSpecialBlockTOBlock(SpecialBlock sb, int blockNum) {
        block = Disk.getDisk().getBlock();
        byte[] b = ByteUtil.intToBytes(sb.getNum(), Config.MAX_SPECIAL_DISK_BLOCK_NUM);
        for (int i = 0; i < Config.MAX_SPECIAL_DISK_BLOCK_NUM; i++) {
            block[blockNum][i] = b[i];
        }
        for (int i = 1; i <= sb.getNum(); i++) {
            byte[] temp = ByteUtil.intToBytes(sb.getEntryBlock().get(i - 1), Config.MAX_SPECIAL_DISK_BLOCK_NUM);
            for (int j = 0; j < Config.MAX_SPECIAL_DISK_BLOCK_NUM; j++) {
                block[blockNum][i * Config.MAX_SPECIAL_DISK_BLOCK_NUM + j] = temp[j];
            }
        }
        writeToDisk(block);
    }

    public void initBlock(int blockNum) {
        byte[][] block = Disk.getDisk().getBlock();
        for (int i = 0; i < Config.MAX_BLOCK_ENTRIES; i++) {
            block[blockNum][i] = 0;
        }
        writeToDisk(block);
    }

    public void writeToDisk(FolderEntry de, int blockNum, int position) {
        if (position >= Config.MAX_BLOCK_SEP)
            return;
        byte[][] block = Disk.getDisk().getBlock();
        byte[] b = new FolderEntryService().FolderEntryToByte(de);
        for (int i = 0; i < Config.MAX_FOLDER_ENTRY_LEN; i++) {
            block[blockNum][position * Config.MAX_FOLDER_ENTRY_LEN + i] = b[i];
        }
        writeToDisk(block);
    }

    public void initData() throws IOException {
        File hardDisk = new File(Config.HARD_DISK_PATH);
        if (!hardDisk.exists()) {
            throw new IOException("Can not create file");
        }

        convert(0, Config.MAX_SPECIAL_DISK_BLOCK_NUM * 0, 3);
        convert(0, Config.MAX_SPECIAL_DISK_BLOCK_NUM * 1, 5);
        convert(0, Config.MAX_SPECIAL_DISK_BLOCK_NUM * 2, 4);
        convert(0, Config.MAX_SPECIAL_DISK_BLOCK_NUM * 3, 3);
        for (int i = Config.MAX_SPECIAL_DISK_BLOCK_NUM + 1; i < Config.MAX_DISK_BLOCK_ENTRIES
                - Config.MAX_BLOCK_NUM; i = i + Config.MAX_BLOCK_NUM) {
            for (int j = 0; j < (Config.MAX_FILENAME_LEN + 2); j++) {
                if (j == 0) {
                    convert(i, 0, Config.MAX_BLOCK_NUM);
                } else {
                    convert(i, j * Config.MAX_SPECIAL_DISK_BLOCK_NUM, i + (Config.MAX_FILENAME_LEN + 2) - j);
                }
            }
        }
        convert(Config.MAX_DISK_BLOCK_ENTRIES - Config.MAX_BLOCK_NUM - 1, Config.MAX_SPECIAL_DISK_BLOCK_NUM, 0);
        FileOutputStream output = new FileOutputStream(hardDisk);
        for (int i = 0; i < Config.MAX_DISK_BLOCK_ENTRIES; ++i)
            output.write(block[i], 0, Config.MAX_BLOCK_ENTRIES);
        output.close();
    }

    private void convert(int blockNum, int start, int num) {
        byte[] b = new byte[Config.MAX_SPECIAL_DISK_BLOCK_NUM];
        b = ByteUtil.intToBytes(num, Config.MAX_SPECIAL_DISK_BLOCK_NUM);
        for (int i = 0; i < Config.MAX_SPECIAL_DISK_BLOCK_NUM; i++) {
            block[blockNum][start + i] = b[i];
        }
    }
}
