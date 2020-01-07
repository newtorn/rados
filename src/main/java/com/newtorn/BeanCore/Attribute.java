package com.newtorn.BeanCore;

/**
 * 文件属性
 */
public final class Attribute {
    /**
     * 0位表示是否为文件
     */
    private boolean isFile;

    /**
     * 1位表示是否隐藏
     */
    private boolean isHidden;

    /**
     * 2位表示是否只读
     */
    private boolean isOnlyRead;

    /**
     * 3位表示是否共享
     */
    private boolean isShare;

    /**
     * 该目录项是否被占用
     */
    private boolean isUsed;

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean isFile) {
        this.isFile = isFile;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean isOnlyRead() {
        return isOnlyRead;
    }

    public void setOnlyRead(boolean isOnlyRead) {
        this.isOnlyRead = isOnlyRead;
    }

    public boolean isShare() {
        return isShare;
    }

    public void setShare(boolean isShare) {
        this.isShare = isShare;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }
}