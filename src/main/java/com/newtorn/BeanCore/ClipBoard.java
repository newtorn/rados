package com.newtorn.BeanCore;

/**
 * 剪切板
 */
public final class ClipBoard {
    private static FolderEntry fe = null;
    private static boolean isCopy = true;
    private static boolean isFile = true;
    private static int blockNum = 0;

    public static FolderEntry getFe() {
        return fe;
    }

    public static void setFe(FolderEntry fe) {
        ClipBoard.fe = fe;
    }

    public static boolean isCopy() {
        return isCopy;
    }

    public static void setCopy(boolean isCopy) {
        ClipBoard.isCopy = isCopy;
    }

    public static boolean isFile() {
        return isFile;
    }

    public static void setFile(boolean isFile) {
        ClipBoard.isFile = isFile;
    }

    public static int getBlockNum() {
        return blockNum;
    }

    public static void setBlockNum(int blockNum) {
        ClipBoard.blockNum = blockNum;
    }
}