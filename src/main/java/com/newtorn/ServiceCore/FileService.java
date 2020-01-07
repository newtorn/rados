package com.newtorn.ServiceCore;

import java.util.ArrayList;
import java.util.List;

import com.newtorn.BeanCore.ClipBoard;
import com.newtorn.BeanCore.Config;
import com.newtorn.BeanCore.FolderEntry;
import com.newtorn.BeanCore.Disk;
import com.newtorn.BeanCore.Finder;

public class FileService {
    public String openFile(FolderEntry fe) {
        String text = "";
        BlockService bs = new BlockService();
        if (fe.getFolderAddress() == 0 && fe.getIndexAddress() == 0) {
            int temp = bs.getFreeBlock();
            if (temp == 0) {
                throw new IllegalAccessError("No enough memory");
            } else {
                int[] position = bs.getFolderEntryPosition(fe, Finder.getCFB());
                fe.setFolderAddress((short) temp);
                new DiskService().writeToDisk(fe, position[0], position[1]);
            }
        } else {
            if (fe.getFolderAddress() == 0) {
                List<Integer> list = bs.getBlocks(fe.getIndexAddress());
                for (int i = 0; i < list.size(); i++) {
                    text += readFile(list.get(i));
                }
            } else {
                int blockNum = fe.getFolderAddress();
                text = readFile(blockNum);
            }
        }
        return text;
    }

    public String readFile(int blockNum) {
        String text = "";
        byte[][] block = Disk.getDisk().getBlock();
        byte[] b = block[blockNum];
        text = new String(b);
        return text.trim();
    }

    public void saveFile(String text, FolderEntry fe) {
        DiskService ds = new DiskService();
        BlockService bs = new BlockService();
        bs.freeBlock(fe);
        byte[] b = text.getBytes();
        int num = b.length / Config.MAX_BLOCK_ENTRIES + 1;
        if (num > 1) {

            int index = bs.getFreeBlock();
            int[] position = bs.getFolderEntryPosition(fe, Finder.getCFB());
            fe.setIndexAddress((short) index);
            fe.setFolderAddress((short) 0);
            ds.writeToDisk(fe, position[0], position[1]);
            for (int i = 0; i < num - 1; i++) {
                int blockNum = bs.getFreeBlock();
                bs.addBlockToIndex(blockNum, index);
                byte[][] block = Disk.getDisk().getBlock();
                for (int j = 0; j < Config.MAX_BLOCK_ENTRIES; j++) {
                    block[blockNum][j] = b[i * Config.MAX_BLOCK_ENTRIES + j];
                }
                ds.writeToDisk(block);
            }
            int blockNum = bs.getFreeBlock();
            bs.addBlockToIndex(blockNum, index);
            byte[][] block = Disk.getDisk().getBlock();
            for (int i = 0; i < (b.length) % Config.MAX_BLOCK_ENTRIES; i++) {
                block[blockNum][i] = b[i + (num - 1) * Config.MAX_BLOCK_ENTRIES];
            }
            ds.writeToDisk(block);
        } else {
            int blockNum = bs.getFreeBlock();
            int[] position = bs.getFolderEntryPosition(fe, Finder.getCFB());
            fe.setFolderAddress((short) blockNum);
            fe.setIndexAddress((short) 0);
            ds.writeToDisk(fe, position[0], position[1]);
            byte[][] block = Disk.getDisk().getBlock();
            for (int i = 0; i < b.length; i++) {
                block[blockNum][i] = b[i];
            }
            ds.writeToDisk(block);
        }
    }

    public void deleteFile(FolderEntry fe, int CDB) {
        BlockService bs = new BlockService();
        bs.freeBlock(fe);
        int[] position = bs.getFolderEntryPosition(fe, CDB);
        new FolderEntryService().deleteDir(position[1], position[0]);
    }

    public boolean existsFile(FolderEntry fe, int CDB) {
        BlockService bs = new BlockService();
        int[] position = bs.getFolderEntryPosition(fe, CDB);
        return position[1] < Config.MAX_BLOCK_SEP;
    }

    public void copyFile(FolderEntry fe) {
        ClipBoard.setFe(fe);
        ClipBoard.setCopy(true);
        ClipBoard.setFile(true);
        ClipBoard.setBlockNum(Finder.getCFB());
    }

    public void cutFile(FolderEntry fe) {
        ClipBoard.setFe(fe);
        ClipBoard.setCopy(false);
        ClipBoard.setFile(true);
        ClipBoard.setBlockNum(Finder.getCFB());
    }

    public int postFile() {
        FolderEntry de = ClipBoard.getFe();
        if (de == null)
            return 0;
        FolderEntry d = new FolderEntry();
        d.setFolderAddress(de.getFolderAddress());
        d.setExtName(de.getExtName());
        d.setIndexAddress(de.getIndexAddress());
        d.setLength(de.getLength());
        d.setName(de.getName());
        d.setProperty(de.getAttribute());
        FolderEntryService des = new FolderEntryService();
        BlockService bs = new BlockService();
        if (d.getFolderAddress() != 0) {
            int blockNum = de.getFolderAddress();
            int temp = bs.getFreeBlock();
            d.setFolderAddress((short) temp);
            bs.copyBlock(blockNum, temp);
        }
        if (d.getIndexAddress() != 0) {
            int blockNum = d.getIndexAddress();
            int index = bs.getFreeBlock();
            d.setIndexAddress((short) index);
            List<Integer> list = bs.getBlocks(blockNum);
            for (int i = 0; i < list.size(); i++) {
                int temp = bs.getFreeBlock();
                bs.addBlockToIndex(temp, index);
                bs.copyBlock(list.get(i), temp);
            }
        }
        if (des.isRenameFile(d.getName()) == 1)
            return -1;
        des.addDir(d, Finder.getCFB());
        if (ClipBoard.isCopy() == false) {
            deleteFile(de, ClipBoard.getBlockNum());
        }
        return 1;
    }

    public void copyFolder(FolderEntry fe) {
        ClipBoard.setFe(fe);
        ClipBoard.setCopy(true);
        ClipBoard.setFile(false);
        ClipBoard.setBlockNum(Finder.getCFB());
    }

    public void cutFolder(FolderEntry fe) {
        ClipBoard.setFe(fe);
        ClipBoard.setCopy(false);
        ClipBoard.setFile(false);
        ClipBoard.setBlockNum(Finder.getCFB());
    }

    public int postDir() {
        FolderEntry de = ClipBoard.getFe();
        if (de == null)
            return 0;
        FolderEntry d = new FolderEntry();
        d.setFolderAddress(de.getFolderAddress());
        d.setExtName(de.getExtName());
        d.setIndexAddress(de.getIndexAddress());
        d.setLength(de.getLength());
        d.setName(de.getName());
        d.setProperty(de.getAttribute());
        if (new FolderEntryService().isRenameDirectory(d.getName()) == 1)
            return -1;
        copyNextFolderEntry(d, Finder.getCFB());
        if (ClipBoard.isCopy() == false) {
            new FolderEntryService().deleteFolderEntry(de, ClipBoard.getBlockNum());
        }
        return 1;

    }

    private void copyNextFolderEntry(FolderEntry fe, int CDB) {
        BlockService bs = new BlockService();
        FolderEntryService des = new FolderEntryService();
        if (fe.getFolderAddress() != 0) {
            int dirAddress = fe.getFolderAddress();
            int newDirAddress = bs.getFreeBlock();
            fe.setFolderAddress((short) newDirAddress);
            List<FolderEntry> deList = bs.searchBlock(dirAddress);
            for (int i = 0; i < deList.size(); i++) {
                FolderEntry d = deList.get(i);
                if (d.getAttribute() > 0) {

                    if (d.getFolderAddress() != 0) {
                        int blockNum = fe.getFolderAddress();
                        int temp = bs.getFreeBlock();
                        d.setFolderAddress((short) temp);
                        bs.copyBlock(blockNum, temp);
                    }
                    if (d.getIndexAddress() != 0) {
                        int blockNum = d.getIndexAddress();
                        int index = bs.getFreeBlock();
                        d.setIndexAddress((short) index);
                        List<Integer> list = bs.getBlocks(blockNum);
                        for (int j = 0; j < list.size(); j++) {
                            int temp = bs.getFreeBlock();
                            bs.addBlockToIndex(temp, index);
                            bs.copyBlock(list.get(j), temp);
                        }
                    }
                    des.addDir(d, newDirAddress);
                } else {

                    copyNextFolderEntry(d, newDirAddress);
                }
            }
        }
        if (fe.getIndexAddress() != 0) {
            int indexAddress = fe.getIndexAddress();
            int newIndexAddress = bs.getFreeBlock();
            fe.setFolderAddress((short) newIndexAddress);
            List<FolderEntry> deList = bs.searchBlock(indexAddress);
            for (int i = 0; i < deList.size(); i++) {
                FolderEntry d = deList.get(i);
                if (d.getAttribute() > 0) {

                    if (d.getFolderAddress() != 0) {
                        int blockNum = fe.getFolderAddress();
                        int temp = bs.getFreeBlock();
                        d.setFolderAddress((short) temp);
                        bs.copyBlock(blockNum, temp);
                    }
                    if (d.getIndexAddress() != 0) {
                        int blockNum = d.getIndexAddress();
                        int index = bs.getFreeBlock();
                        d.setIndexAddress((short) index);
                        List<Integer> list = bs.getBlocks(blockNum);
                        for (int j = 0; j < list.size(); j++) {
                            int temp = bs.getFreeBlock();
                            bs.addBlockToIndex(temp, index);
                            bs.copyBlock(list.get(j), temp);
                        }
                    }
                    des.addDir(d, newIndexAddress);
                } else {

                    copyNextFolderEntry(d, newIndexAddress);
                }
            }
        }
        des.addDir(fe, CDB);
    }

    public static boolean existFile(String file, BlockService bs) {
        return (boolean) searchFile(file, bs)[0];
    }

    public static Object[] searchFile(String file, BlockService bs) {
        Object[] result = new Object[3];
        List<Integer> list = new ArrayList<Integer>();
        boolean flag = true;
        String str = file;
        String[] name = str.split(Config.FILE_SEPARATOR);
        int CDB = 0;
        FolderEntry d = new FolderEntry();
        for (int i = 0; i < name.length; i++) {
            d = bs.getFolderEntryByName(name[i], CDB);
            if (d != null) {
                list.add(CDB);
                if (list.size() == name.length) {
                    flag = true;
                }
                if (d.getFolderAddress() == 0 && d.getIndexAddress() == 0) {
                    int blockNum = bs.getFreeBlock();
                    CDB = blockNum;
                } else if (d.getFolderAddress() == 0)
                    CDB = d.getIndexAddress();
                else
                    CDB = d.getFolderAddress();
            } else {
                flag = false;
                break;
            }
        }
        result[0] = Boolean.valueOf(flag);
        result[1] = list;
        result[2] = Integer.valueOf(CDB);

        return result;
    }
}
