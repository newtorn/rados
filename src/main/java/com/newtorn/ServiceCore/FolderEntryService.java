package com.newtorn.ServiceCore;

import java.util.ArrayList;
import java.util.List;

import com.newtorn.BeanCore.FolderEntry;
import com.newtorn.BeanCore.Config;
import com.newtorn.BeanCore.Disk;
import com.newtorn.BeanCore.Finder;
import com.newtorn.BeanCore.Attribute;
import com.newtorn.ToolkitsCore.ByteUtil;
import com.newtorn.ToolkitsCore.AttributeUtil;

public class FolderEntryService {

    private BlockService bs;
    private DiskService ds;

    public byte[] FolderEntryToByte(FolderEntry de) {

        byte[] b = new byte[Config.MAX_FOLDER_ENTRY_LEN];
        byte[] temp;
        temp = new byte[Config.MAX_PRENAME_LEN];
        temp = ByteUtil.stringToBytes(de.getName(), Config.MAX_PRENAME_LEN);
        for (int i = 0; i < Config.MAX_PRENAME_LEN; i++) {
            b[i] = temp[i];
        }

        temp = new byte[Config.MAX_EXTNAME_LEN];
        temp = ByteUtil.stringToBytes(de.getExtName(), Config.MAX_EXTNAME_LEN);
        for (int i = 0; i < Config.MAX_EXTNAME_LEN; i++) {
            b[Config.MAX_PRENAME_LEN + i] = temp[i];
        }

        b[Config.MAX_FILENAME_LEN] = de.getAttribute();

        temp = new byte[2];
        temp = ByteUtil.shortToTowBytes(de.getLength());
        b[Config.MAX_FILENAME_LEN + 1] = temp[0];
        b[Config.MAX_FILENAME_LEN + 2] = temp[1];

        temp = ByteUtil.shortToTowBytes(de.getFolderAddress());
        b[Config.MAX_FILENAME_LEN + 3] = temp[0];
        b[Config.MAX_FILENAME_LEN + 4] = temp[1];

        temp = ByteUtil.shortToTowBytes(de.getIndexAddress());
        b[Config.MAX_FILENAME_LEN + 5] = temp[0];
        b[Config.MAX_FILENAME_LEN + 6] = temp[1];
        return b;
    }

    public FolderEntry byteToFolderEntry(byte[] data) {
        FolderEntry de = new FolderEntry();
        if (data.length != Config.MAX_FOLDER_ENTRY_LEN) {
            throw new IllegalArgumentException(
                    "File floder item size is " + Config.MAX_FOLDER_ENTRY_LEN + " bytes, Param data.length != 6");
        }

        int s = 0, r = Config.MAX_PRENAME_LEN;
        String name = ByteUtil.BytesToString(ByteUtil.getByteArray(data, s, r));

        s += r;
        r = 3;
        String exName = ByteUtil.BytesToString(ByteUtil.getByteArray(data, s, r));

        s += r;
        r = 1;
        byte property = data[s];

        s += r;
        r = 2;
        short length = ByteUtil.twoBytesToShort(ByteUtil.getByteArray(data, s, r));

        s += r;
        r = 2;
        short folderAddress = ByteUtil.twoBytesToShort(ByteUtil.getByteArray(data, s, r));

        s += r;
        r = 2;
        short indexAddress = ByteUtil.twoBytesToShort(ByteUtil.getByteArray(data, s, r));

        de.setName(name);
        de.setFolderAddress(folderAddress);
        de.setExtName(exName);
        de.setIndexAddress(indexAddress);
        de.setLength(length);
        de.setProperty(property);
        return de;
    }

    public int addFileEntry(String name) {
        byte[] b = name.getBytes();
        int length = name.length();
        int i;
        for (i = 0; i < length; i++) {
            if (name.charAt(i) == '.')
                break;
        }
        String fileName = new String(b, 0, i);
        String exName = "";
        if (i + 1 >= length) {
            exName = "txt";
        } else {
            exName = new String(b, i + 1, length - i - 1);
        }
        int isRename = this.isRenameFile(name);
        if (isRename == 1)
            return -1;
        FolderEntry de = new FolderEntry();
        Attribute attr = new Attribute();
        attr.setFile(true);
        attr.setHidden(false);
        attr.setOnlyRead(false);
        attr.setShare(true);
        attr.setUsed(true);
        de.setName(fileName);
        de.setExtName(exName);
        de.setProperty(AttributeUtil.attributeToByte(attr));
        int result = this.addDir(de, Finder.getCFB());
        return result;
    }

    public int addFolderEntry(String name) {
        int isRename = this.isRenameDirectory(name);
        if (isRename == 1)
            return -1;
        FolderEntry de = new FolderEntry();
        Attribute attr = new Attribute();
        attr.setFile(false);
        attr.setHidden(false);
        attr.setOnlyRead(false);
        attr.setShare(true);
        attr.setUsed(true);
        de.setName(name);
        de.setProperty(AttributeUtil.attributeToByte(attr));
        int result = this.addDir(de, Finder.getCFB());
        return result;
    }

    public void deleteFolderEntry(FolderEntry de, int CDB) {
        bs = new BlockService();
        int[] position = bs.getFolderEntryPosition(de, CDB);
        deleteNextFolderEntry(de);
        deleteDir(position[1], position[0]);
    }

    private void deleteNextFolderEntry(FolderEntry de) {
        BlockService bs = new BlockService();
        if (de.getFolderAddress() != 0) {
            List<FolderEntry> list = bs.searchBlock(de.getFolderAddress());
            bs.freeBlock(de.getFolderAddress());
            for (int i = 0; i < list.size(); i++) {
                FolderEntry d = list.get(i);
                if (d.getAttribute() > 0)
                    bs.freeBlock(d);
                if (d.getAttribute() < 0)
                    deleteNextFolderEntry(d);
            }
        }
        if (de.getIndexAddress() != 0) {
            List<FolderEntry> list = bs.searchBlock(-de.getIndexAddress());
            for (int i = 0; i < list.size(); i++) {
                FolderEntry d = list.get(i);
                if (d.getAttribute() > 0)
                    bs.freeBlock(d);
                if (d.getAttribute() < 0)
                    deleteNextFolderEntry(d);
            }
        }

    }

    public void deleteDir(int position, int blockNum) {
        if (position >= Config.MAX_BLOCK_SEP) {
            throw new IllegalAccessError("Overflow floder item position");
        }

        byte[][] block = Disk.getDisk().getBlock();
        block[blockNum][position * Config.MAX_FOLDER_ENTRY_LEN] = '#';
        new DiskService().writeToDisk(block);
    }

    public int isRenameDirectory(String name) {
        int isRename = 0;
        bs = new BlockService();
        int CDB = Finder.getCFB();
        List<FolderEntry> FEList = new ArrayList<FolderEntry>();
        List<Integer> index = new ArrayList<Integer>();
        if (CDB == 0) {
            FEList = bs.getAllFolderEntry(1);
            List<FolderEntry> deList = bs.getAllFolderEntry(2);
            for (int j = 0; j < deList.size(); j++) {
                FEList.add(deList.get(j));
            }
        } else {
            if (CDB < 0) {
                index = bs.getBlocks(-CDB);
                for (int i = 0; i < index.size(); i++) {
                    List<FolderEntry> temp = bs.getAllFolderEntry(index.get(i));
                    for (int j = 0; j < temp.size(); j++) {
                        FEList.add(temp.get(j));
                    }
                }
            } else {
                FEList = bs.getAllFolderEntry(CDB);
            }

        }
        for (int i = 0; i < FEList.size(); i++) {
            FolderEntry de = FEList.get(i);
            if (name.equals(de.getName()))
                isRename = 1;
        }
        return isRename;
    }

    public int isRenameFile(String name) {
        int isRename = 0;
        bs = new BlockService();
        int CDB = Finder.getCFB();
        List<FolderEntry> DEList = new ArrayList<FolderEntry>();
        List<Integer> index = new ArrayList<Integer>();
        if (CDB == 0) {
            DEList = bs.getAllFile(1);
            List<FolderEntry> deList = bs.getAllFile(2);
            for (int j = 0; j < deList.size(); j++) {
                DEList.add(deList.get(j));
            }
        } else {
            if (CDB < 0) {
                index = bs.getBlocks(-CDB);
                for (int i = 0; i < index.size(); i++) {
                    List<FolderEntry> temp = bs.getAllFile(index.get(i));
                    for (int j = 0; j < temp.size(); j++) {
                        DEList.add(temp.get(j));
                    }
                }
            } else {
                DEList = bs.getAllFile(CDB);
            }

        }
        for (int i = 0; i < DEList.size(); i++) {
            FolderEntry de = DEList.get(i);
            if (name.equals(de.getName()) && name.equals(de.getExtName()))
                isRename = 1;
        }
        return isRename;
    }

    public int addDir(FolderEntry DE, int CDB) {
        bs = new BlockService();
        ds = new DiskService();
        int blockNum = 0;
        int position = 0;
        if (CDB == 0) {
            position = bs.getfreeFolderEntry(1);
            if (position == Config.MAX_BLOCK_SEP) {
                position = bs.getfreeFolderEntry(2);
                if (position == Config.MAX_BLOCK_SEP) {
                    throw new IllegalAccessError("Root floder items is over than 16");
                }
                blockNum = 2;
            } else {
                blockNum = 1;
            }
        } else {
            if (CDB < 0) {
                List<Integer> index = bs.getBlocks(-CDB);
                for (int i = 0; i < index.size(); i++) {
                    position = bs.getfreeFolderEntry(index.get(i));
                    if (position != Config.MAX_BLOCK_SEP) {
                        blockNum = index.get(i);
                        break;
                    }
                }
                if (position == Config.MAX_BLOCK_SEP) {
                    int newBlock = bs.getFreeBlock();
                    if (newBlock == 0)
                        return 0;
                    bs.addBlockToIndex(newBlock, -CDB);
                    blockNum = newBlock;
                    position = 0;
                }
            } else {
                position = bs.getfreeFolderEntry(CDB);
                if (position == Config.MAX_BLOCK_SEP) {

                    int indexBlock = bs.getFreeBlock();
                    if (indexBlock == 0)
                        return 0;
                    else {
                        int PDB = Finder.getPFB();
                        int[] result = bs.getCDBPositionInPDB(CDB, PDB);
                        FolderEntry d = bs.getFolderEntryByPosition(result[0], result[1]);
                        d.setFolderAddress((short) 0);
                        d.setIndexAddress((short) indexBlock);
                        new DiskService().writeToDisk(d, result[0], result[1]);
                        bs.addBlockToIndex(CDB, indexBlock);
                        Finder.popPFB();
                        Finder.setCFB(-indexBlock);
                        int b = bs.getFreeBlock();
                        bs.addBlockToIndex(b, indexBlock);
                        if (b == 0)
                            return 0;
                        else {
                            blockNum = b;
                            position = 0;
                        }
                    }
                } else {
                    blockNum = CDB;
                }

            }
        }

        ds.writeToDisk(DE, blockNum, position);
        return 1;
    }

}
