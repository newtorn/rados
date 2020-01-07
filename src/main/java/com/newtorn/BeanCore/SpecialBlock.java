package com.newtorn.BeanCore;

import java.util.ArrayList;
import java.util.List;

import com.newtorn.ToolkitsCore.ByteUtil;

/**
 * 特殊块
 */
public class SpecialBlock {
    private static int num;
    private static List<Integer> entryBlock = new ArrayList<Integer>();
    private static SpecialBlock specialBlock = new SpecialBlock();

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        SpecialBlock.num = num;
    }

    public List<Integer> getEntryBlock() {
        return entryBlock;
    }

    public void setEntryBlock(List<Integer> entryBlock) {
        SpecialBlock.entryBlock = entryBlock;
    }

    public static SpecialBlock getSpecialBlock() {
        byte[][] block = Disk.getDisk().getBlock();
        num = ByteUtil.BytesToInt(ByteUtil.getByteArray(block[0], 0, Config.MAX_SPECIAL_DISK_BLOCK_NUM));
        entryBlock.clear();
        for (int i = 1; i <= num; i++) {
            int temp = ByteUtil.BytesToInt(ByteUtil.getByteArray(block[0], Config.MAX_SPECIAL_DISK_BLOCK_NUM * i,
                    Config.MAX_SPECIAL_DISK_BLOCK_NUM));
            entryBlock.add(temp);
        }
        specialBlock.setNum(num);
        specialBlock.setEntryBlock(entryBlock);
        return specialBlock;
    }
}
