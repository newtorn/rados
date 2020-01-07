package com.newtorn.BeanCore;

public final class FolderEntry {
    /**
     * Config.MAX_PRENAME_LEN个字节 文件名或目录名
     */
    private String name = "";

    /**
     * Config.MAX_EXTNAME个字节 文件扩展名
     */
    private String extName = "";

    /**
     * 1个字节 属性 1:只读文件 2:读写文件 3：目录
     */
    private byte attribute = 0;

    /**
     * 2个字节 文件长度
     */
    private short length = 0;

    /**
     * 2个字节 直接地址项
     */
    private short folderAddress = 0;

    /**
     * 2个字节 一级索引项 （在最后一个磁盘块中占的字节数）或 0（目录目录项）
     */
    private short indexAddress = 0;

    public Boolean equal(FolderEntry fe) {
        return (fe.name.equals(name) && fe.extName.equals(extName) && fe.attribute == attribute && fe.length == length
                && fe.folderAddress == folderAddress && fe.indexAddress == indexAddress);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtName() {
        return extName;
    }

    public void setExtName(String extName) {
        this.extName = extName;
    }

    public byte getAttribute() {
        return attribute;
    }

    public void setProperty(byte property) {
        this.attribute = property;
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public short getFolderAddress() {
        return folderAddress;
    }

    public void setFolderAddress(short folderAddress) {
        this.folderAddress = folderAddress;
    }

    public short getIndexAddress() {
        return indexAddress;
    }

    public void setIndexAddress(short indexAddress) {
        this.indexAddress = indexAddress;
    }
}