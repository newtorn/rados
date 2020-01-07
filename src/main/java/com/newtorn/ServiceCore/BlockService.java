package com.newtorn.ServiceCore;

import java.util.ArrayList;
import java.util.List;

import com.newtorn.BeanCore.FolderEntry;
import com.newtorn.BeanCore.Config;
import com.newtorn.BeanCore.Disk;
import com.newtorn.BeanCore.Finder;
import com.newtorn.BeanCore.SpecialBlock;
import com.newtorn.ToolkitsCore.ByteUtil;

public class BlockService {
    private FolderEntryService fes;
    private DiskService ds;

    private List<FolderEntry> getAllFolder(int CFB) {
        List<FolderEntry> FEList1 = this.getAllFolderEntry(CFB);
        List<FolderEntry> FEList2 = this.getAllFile(CFB);
        for (int i = 0; i < FEList2.size(); i++)
            FEList1.add(FEList2.get(i));

        return FEList1;
    }

    public List<FolderEntry> readBlock(int CFB) {
        List<Integer> index = new ArrayList<Integer>();
        List<FolderEntry> DEList = new ArrayList<FolderEntry>();
        if (CFB == 0) {
            DEList = getAllFolder(1);
            List<FolderEntry> deList = getAllFolder(2);
            for (int j = 0; j < deList.size(); j++) {
                DEList.add(deList.get(j));
            }
        } else {
            if (CFB < 0) {
                Finder.setPFB();
                Finder.setCFB(CFB);
                index = getBlocks(-CFB);
                for (int i = 0; i < index.size(); i++) {
                    List<FolderEntry> temp = getAllFolder(index.get(i));
                    for (int j = 0; j < temp.size(); j++) {
                        DEList.add(temp.get(j));
                    }
                }
            } else {
                Finder.setPFB();
                Finder.setCFB(CFB);
                DEList = getAllFolder(CFB);
            }

        }
        return DEList;
    }

    public List<FolderEntry> searchBlock(int CFB) {
        List<Integer> index = new ArrayList<Integer>();
        List<FolderEntry> DEList = new ArrayList<FolderEntry>();

        if (CFB == 0) {
            DEList = getAllFolder(1);
            List<FolderEntry> deList = getAllFolder(2);
            for (int j = 0; j < deList.size(); j++) {
                DEList.add(deList.get(j));
            }
        } else {
            if (CFB < 0) {
                index = getBlocks(-CFB);
                for (int i = 0; i < index.size(); i++) {
                    List<FolderEntry> temp = getAllFolder(index.get(i));
                    for (int j = 0; j < temp.size(); j++) {
                        DEList.add(temp.get(j));
                    }
                }
            } else {
                DEList = getAllFolder(CFB);
            }
        }
        return DEList;
    }

    public List<FolderEntry> getAllFolderEntry(int CFB) {
        fes = new FolderEntryService();
        List<FolderEntry> DEList = new ArrayList<FolderEntry>();
        Disk d = Disk.getDisk();
        byte[][] block = d.getBlock();
        for (int i = 0; i < Config.MAX_BLOCK_SEP; i++) {
            if (block[CFB][i * Config.MAX_FOLDER_ENTRY_LEN + Config.MAX_FILENAME_LEN] < 0
                    && block[CFB][i * Config.MAX_FOLDER_ENTRY_LEN] != '#') {
                byte[] temp = ByteUtil.getByteArray(block[CFB], i * Config.MAX_FOLDER_ENTRY_LEN,
                        Config.MAX_FOLDER_ENTRY_LEN);
                FolderEntry de = fes.byteToFolderEntry(temp);
                DEList.add(de);
            }
        }
        return DEList;
    }

    public List<FolderEntry> getAllFile(int CFB) {
        fes = new FolderEntryService();
        List<FolderEntry> DEList = new ArrayList<FolderEntry>();
        Disk d = Disk.getDisk();
        byte[][] block = d.getBlock();
        for (int i = 0; i < Config.MAX_BLOCK_SEP; i++) {
            if (block[CFB][i * Config.MAX_FOLDER_ENTRY_LEN + Config.MAX_FILENAME_LEN] > 0
                    && block[CFB][i * Config.MAX_FOLDER_ENTRY_LEN] != '#') {
                byte[] temp = ByteUtil.getByteArray(block[CFB], i * Config.MAX_FOLDER_ENTRY_LEN,
                        Config.MAX_FOLDER_ENTRY_LEN);
                FolderEntry de = fes.byteToFolderEntry(temp);
                DEList.add(de);
            }
        }
        return DEList;
    }

    public int getfreeFolderEntry(int CFB) {
        byte[][] block = Disk.getDisk().getBlock();
        for (int i = 0; i < Config.MAX_BLOCK_SEP; ++i) {
            if (block[CFB][i * Config.MAX_FOLDER_ENTRY_LEN] == '#'
                    || block[CFB][i * Config.MAX_FOLDER_ENTRY_LEN + Config.MAX_FILENAME_LEN] == 0) {
                return i;
            }
        }
        return Config.MAX_BLOCK_SEP;
    }

    private int getFolderEntryPositionInBlock(FolderEntry fe, int blockNum) {
        fes = new FolderEntryService();
        byte[][] block = Disk.getDisk().getBlock();
        for (int i = 0; i < Config.MAX_BLOCK_SEP; i++) {
            FolderEntry d = fes.byteToFolderEntry(ByteUtil.getByteArray(block[blockNum],
                    i * Config.MAX_FOLDER_ENTRY_LEN, Config.MAX_FOLDER_ENTRY_LEN));
            if (d.equal(fe)) {
                return i;
            }
        }
        return Config.MAX_BLOCK_SEP;
    }

    public int[] getCDBPositionInPDB(int CFB, int PFB) {
        int[] result = new int[2];
        int blockNum = 0;
        int position = Config.MAX_BLOCK_SEP;
        if (PFB == 0) {
            position = getFolderEntryPositionInBlock(CFB, 1);
            if (position == Config.MAX_BLOCK_SEP) {
                position = getFolderEntryPositionInBlock(CFB, 2);
                if (position != Config.MAX_BLOCK_SEP) {
                    blockNum = 2;
                    result[0] = blockNum;
                    result[1] = position;
                    return result;
                }
            } else {
                blockNum = 1;
                result[0] = blockNum;
                result[1] = position;
                return result;
            }
        } else {
            if (PFB < 0) {
                List<Integer> index = new BlockService().getBlocks(-PFB);
                for (int i = 0; i < index.size(); i++) {
                    position = getFolderEntryPositionInBlock(CFB, index.get(i));
                    if (position != Config.MAX_BLOCK_SEP) {
                        blockNum = index.get(i);
                        result[0] = blockNum;
                        result[1] = position;
                        return result;
                    }
                }
            } else {
                position = getFolderEntryPositionInBlock(CFB, PFB);
                if (position != Config.MAX_BLOCK_SEP) {
                    blockNum = PFB;
                    result[0] = blockNum;
                    result[1] = position;
                    return result;
                }
            }

        }
        return result;
    }

    private int getFolderEntryPositionInBlock(int nextBlock, int blockNum) {
        fes = new FolderEntryService();
        byte[][] block = Disk.getDisk().getBlock();
        for (int i = 0; i < Config.MAX_BLOCK_SEP; i++) {
            FolderEntry de = fes.byteToFolderEntry(ByteUtil.getByteArray(block[blockNum],
                    i * Config.MAX_FOLDER_ENTRY_LEN, Config.MAX_FOLDER_ENTRY_LEN));
            if (de.getFolderAddress() == nextBlock) {
                return i;
            }
        }
        return Config.MAX_BLOCK_SEP;
    }

    public int[] getFolderEntryPosition(FolderEntry fe, int CFB) {

        int[] result = new int[] { 0, Config.MAX_BLOCK_SEP };
        int position = Config.MAX_BLOCK_SEP;
        int blockNum = 0;
        if (CFB == 0) {
            position = getFolderEntryPositionInBlock(fe, 1);
            if (position == Config.MAX_BLOCK_SEP) {
                position = getFolderEntryPositionInBlock(fe, 2);
                if (position != Config.MAX_BLOCK_SEP) {
                    blockNum = 2;
                    result[0] = blockNum;
                    result[1] = position;
                    return result;
                }
            } else {
                blockNum = 1;
                result[0] = blockNum;
                result[1] = position;
                return result;
            }
        } else {
            if (CFB < 0) {
                List<Integer> index = new BlockService().getBlocks(-CFB);
                for (int i = 0; i < index.size(); i++) {
                    position = getFolderEntryPositionInBlock(fe, index.get(i));
                    if (position != Config.MAX_BLOCK_SEP) {
                        blockNum = index.get(i);
                        result[0] = blockNum;
                        result[1] = position;
                        return result;
                    }
                }
            } else {
                position = getFolderEntryPositionInBlock(fe, CFB);
                if (position != Config.MAX_BLOCK_SEP) {
                    blockNum = CFB;
                    result[0] = blockNum;
                    result[1] = position;
                    return result;
                }
            }

        }
        return result;
    }

    public FolderEntry getFolderEntryByName(String name, int CFB) {
        FolderEntry de = null;
        if (CFB == 0) {
            de = getFolderEntryByNameInBlock(name, 1);
            if (de == null) {
                de = getFolderEntryByNameInBlock(name, 2);
            }
        } else {
            if (CFB < 0) {
                List<Integer> index = new BlockService().getBlocks(-CFB);
                for (int i = 0; i < index.size(); i++) {
                    de = getFolderEntryByNameInBlock(name, index.get(i));
                    if (de != null) {
                        return de;
                    }
                }
            } else {
                de = getFolderEntryByNameInBlock(name, CFB);
            }

        }
        return de;
    }

    private FolderEntry getFolderEntryByNameInBlock(String name, int blockNum) {
        fes = new FolderEntryService();
        List<FolderEntry> list = getAllFolderEntry(blockNum);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equals(name))
                return list.get(i);
        }
        return null;
    }

    private void initFreeBlock(SpecialBlock sb) {
        ds = new DiskService();
        Disk d = Disk.getDisk();
        byte[][] block = d.getBlock();
        int num = sb.getNum();
        if (num == 1) {
            int b = sb.getEntryBlock().get(num - 1);
            if (b == 0) {
                num--;
            } else {
                for (int i = 0; i < (Config.MAX_FILENAME_LEN + 2) * Config.MAX_SPECIAL_DISK_BLOCK_NUM; i++) {
                    byte temp = block[b][i];
                    block[b][i] = 0;
                    block[0][i] = temp;
                }
            }
            ds.writeToDisk(block);
        } else {
            num--;
            byte[] temp = ByteUtil.intToBytes(num, Config.MAX_SPECIAL_DISK_BLOCK_NUM);
            for (int i = 0; i < Config.MAX_SPECIAL_DISK_BLOCK_NUM; i++) {
                block[0][i] = temp[i];
            }
            ds.writeToDisk(block);
        }
    }

    public int getFreeBlock() {
        SpecialBlock sb = SpecialBlock.getSpecialBlock();
        int num = sb.getNum();
        if (num == 0) {
            return 0;
        } else {
            int freeBlock = sb.getEntryBlock().get(num - 1);
            initFreeBlock(sb);
            return freeBlock;
        }

    }

    public void freeBlock(FolderEntry fe) {
        if (fe.getFolderAddress() != 0) {
            freeBlock(fe.getFolderAddress());
        }
        if (fe.getIndexAddress() != 0) {
            List<Integer> list = getBlocks(fe.getIndexAddress());
            freeBlock(fe.getIndexAddress());
            for (int i = 0; i < list.size(); i++) {
                freeBlock(list.get(i));
            }
        }
    }

    public void freeBlock(int blockNum) {
        DiskService ds = new DiskService();
        ds.initBlock(blockNum);
        SpecialBlock sb = SpecialBlock.getSpecialBlock();
        int num = sb.getNum();
        List<Integer> list = sb.getEntryBlock();
        if (num == Config.MAX_BLOCK_NUM) {
            ds.writeSpecialBlockTOBlock(sb, blockNum);
            sb.setNum(1);
            list.clear();
            list.add(blockNum);
            sb.setEntryBlock(list);
            ds.writeSpecialBlockTOBlock(sb, 0);
        } else {
            num++;
            list.add(blockNum);
            sb.setNum(num);
            sb.setEntryBlock(list);
            ds.writeSpecialBlockTOBlock(sb, 0);
        }
    }

    public List<Integer> getBlocks(int index) {
        byte[][] block = Disk.getDisk().getBlock();
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < Config.MAX_SPECIAL_DISK_BLOCK_AVG; i++) {
            int b = ByteUtil.BytesToInt((ByteUtil.getByteArray(block[index], i * Config.MAX_SPECIAL_DISK_BLOCK_NUM,
                    Config.MAX_SPECIAL_DISK_BLOCK_NUM)));
            if (b > 0) {
                list.add(b);
            }
        }
        return list;
    }

    public void copyBlock(int a, int b) {
        byte[][] block = Disk.getDisk().getBlock();
        for (int i = 0; i < Config.MAX_BLOCK_ENTRIES; i++) {
            block[b][i] = block[a][i];
        }
        new DiskService().writeToDisk(block);
    }

    public void addBlockToIndex(int blockNum, int index) {
        byte[][] block = Disk.getDisk().getBlock();
        int i;
        for (i = 0; i < Config.MAX_SPECIAL_DISK_BLOCK_AVG; i++) {
            int b = ByteUtil.BytesToInt((ByteUtil.getByteArray(block[index], i * Config.MAX_SPECIAL_DISK_BLOCK_NUM,
                    Config.MAX_SPECIAL_DISK_BLOCK_NUM)));
            if (b == 0) {
                byte[] temp = ByteUtil.intToBytes(blockNum, Config.MAX_SPECIAL_DISK_BLOCK_NUM);
                for (int j = 0; j < Config.MAX_SPECIAL_DISK_BLOCK_NUM; j++) {
                    block[index][i * Config.MAX_SPECIAL_DISK_BLOCK_NUM + j] = temp[j];
                }
                break;
            }
        }
        if (i == Config.MAX_SPECIAL_DISK_BLOCK_AVG) {
            throw new IllegalAccessError("index block overflow exception");
        }
        new DiskService().writeToDisk(block);
    }

    public FolderEntry getFolderEntryByPosition(int blockNum, int position) {
        FolderEntry de = new FolderEntry();
        byte[][] block = Disk.getDisk().getBlock();
        byte[] temp = ByteUtil.getByteArray(block[blockNum], position * Config.MAX_FOLDER_ENTRY_LEN,
                Config.MAX_FOLDER_ENTRY_LEN);
        de = new FolderEntryService().byteToFolderEntry(temp);
        return de;
    }

    public List<Integer> getAllFreeBLock() {
        List<Integer> list = new ArrayList<Integer>();
        SpecialBlock sb = SpecialBlock.getSpecialBlock();
        List<Integer> entryBlock = sb.getEntryBlock();
        if (sb.getNum() == 0)
            return list;
        int next = entryBlock.get(0);
        if (next == 0) {
            list.add(Config.MAX_DISK_BLOCK_ENTRIES-1);
            return list;
        } else {
            for (int i = 0; i < entryBlock.size(); i++) {
                list.add(entryBlock.get(i));
            }
        }
        byte[][] block = Disk.getDisk().getBlock();
        while (next != 0) {
            List<Integer> temp = new ArrayList<Integer>();
            int tempNext = 0;
            for (int i = 1; i <= Config.MAX_BLOCK_NUM; i++) {
                int b = ByteUtil.BytesToInt((ByteUtil.getByteArray(block[next], i * Config.MAX_SPECIAL_DISK_BLOCK_NUM,
                        Config.MAX_SPECIAL_DISK_BLOCK_NUM)));
                if (i == 1)
                    tempNext = b;
                if (b == 0) {
                    b = Config.MAX_DISK_BLOCK_ENTRIES-1;
                }
                temp.add(b);
            }
            for (int i = 0; i < temp.size(); i++) {
                list.add(temp.get(i));
            }
            next = tempNext;
        }
        return list;
    }
}
