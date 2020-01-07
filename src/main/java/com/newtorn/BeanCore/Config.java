package com.newtorn.BeanCore;

import com.newtorn.ToolkitsCore.FrameUtil;

/**
 * 常量配置
 */
public class Config {
    public static final int[] ROOT_FOLDER_BLOCK = new int[] { 1, 2 };
    public static final String HARD_DISK_PATH = FrameUtil.getFilePath("HARD_DISK");
    public static final String FILE_SEPARATOR = "/";
    public static final int MAX_BLOCK_NUM = 10;
    public static final int MAX_MEMROY_ENTRY_REMAIN = Integer.MAX_VALUE;
    public static final int MAX_PRENAME_LEN = 6;
    public static final int MAX_EXTNAME_LEN = 3;
    public static final int MAX_FILENAME_LEN = MAX_PRENAME_LEN + MAX_EXTNAME_LEN;
    public static final int MAX_FOLDER_ENTRY_LEN = MAX_FILENAME_LEN + 7;
    public static final int MAX_BLOCK_SEP = 8;
    public static final int MAX_BLOCK_ENTRIES = MAX_BLOCK_SEP * MAX_FOLDER_ENTRY_LEN;
    public static final int MAX_DISK_BLOCK_ENTRIES = MAX_BLOCK_ENTRIES * 2;
    public static final int MAX_SPECIAL_DISK_BLOCK_NUM = 4;
    public static final int MAX_SPECIAL_DISK_BLOCK_AVG = MAX_BLOCK_ENTRIES / MAX_SPECIAL_DISK_BLOCK_NUM;
}
