package com.newtorn.BeanCore;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import com.newtorn.ToolkitsCore.FrameUtil;
import com.newtorn.ViewCore.ProcessManagerApp.MemoryPanel;

/**
 * 内存类
 * BF(BestFit)算法分配
 */
public final class Memory {
    /**
     * 内存的数组表现形式
     */
    static byte[][] memory;

    /**
     * 已分配空间的容器
     */
    static LinkedList<MemoryEntry> busyGrid;

    /**
     * 未分配空间的容器
     */
    static LinkedList<MemoryEntry> freeGrid;

    static {
        memory = new byte[32][16];
        busyGrid = new LinkedList<MemoryEntry>();
        freeGrid = new LinkedList<MemoryEntry>();
        freeGrid.add(new MemoryEntry(0, 32));
    }

    /**
     * 申请成功返回申请到的内容在主存中的首地址，申请失败则返回-1
     */
    public static int apply(int size) {
        // 记录从空闲表中分出来的空间的信息，MemoryEntry我这里理解为一个容器中的项，getFreeEntry,就是从空闲表中得到一空闲项
        MemoryEntry getFreeEntry = null;

        // new一个Entry，接下来的代码中填充分配给的空间的首地址和长度
        MemoryEntry insertEntry = new MemoryEntry();

        // 定义一个插入索引，这里的insertIndex必须为0，不可以改动，这与接下来的代码有关
        int insertIndex = 0;

        // 磁盘剩余空间是多少，之所以定义那么大，是因为在循环中要比较，以得到最小的。
        int remain = Config.MAX_MEMROY_ENTRY_REMAIN;

        // 如果申请大小不为0
        if (size != 0) {
            for (MemoryEntry fe : freeGrid) {
                int ts = fe.getLength() - size;
                if ( ts < remain && ts >= 0) {
                    getFreeEntry = fe;
                    // 在空闲分区表中寻找合适的分区来分配给要保存的文本
                    remain = ts;
                }
            }

            if (remain != Config.MAX_MEMROY_ENTRY_REMAIN) {
                if (remain == 0) {
                    // 如果分配给的空间正好等于空闲表中对应的项,则全部分出次空间

                    insertEntry.setStart(getFreeEntry.getStart());
                    insertEntry.setLength(getFreeEntry.getLength());
                    
                    freeGrid.remove(getFreeEntry);
                } else {
                    // 否则，把剩下的割下来

                    insertEntry.setStart(getFreeEntry.getStart());
                    insertEntry.setLength(size);

                    getFreeEntry.setStart(getFreeEntry.getStart() + size);
                    getFreeEntry.setLength(getFreeEntry.getLength() - size);
                }

                // 插入算法，注意这里有两种情况，插入队列中，或者没有找到合适点，直接加在队尾
                for (int i = 0, flag = 1; i < busyGrid.size() && flag == 1;) {
                    if (insertEntry.getStart() > busyGrid.get(i).getStart()) {
                        // 这里的if语句很重要，考虑一下为什么不把if里面的语句写到for循环的控制条件中？
                        i++;
                    } else {
                        flag = 0;
                    }
                    insertIndex = i;
                }

                // 文本保存了，已分配表中该多一项了
                busyGrid.add(insertIndex, insertEntry);

                return insertEntry.getStart();
            } else {
                JOptionPane.showMessageDialog(null, "suitable memory zone not found", "allocate memory failed",
                        JOptionPane.ERROR_MESSAGE, FrameUtil.ErrorDialogImage);
                return -1;
            }
        } else
            return -1;
    }

    /**
     * 申请成功才会调用这个函数，把内容写进去，因此这个函数不用考虑申请失败的问题
     * 
     * @param text
     * @param start
     */
    public static void put(ArrayList<String> text, int start) {
        int size = text.size();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < 4; j++) {
                memory[i + start][j] = (byte) text.get(i).charAt(j);
            }
        }
    }

    /**
     * 实际上应该叫做read函数，更合适一些，但是鉴于read和write理解上的混，所以，这里命名为put,get
     * 
     * @param start
     * @param PC
     * @return
     */
    public static byte[] get(int start, int PC) {
        byte[] b = new byte[4];
        for (int j = 0; j < 4; j++) {
            b[j] = memory[start + PC][j];
        }
        return b;
    }

    /**
     * PCB只记录内容在内存中的首地址
     * 
     * @param start
     */
    public static void free(int start) {
        MemoryEntry transfer = null;
        for (int i = 0; i < busyGrid.size(); i++) {
            if (busyGrid.get(i).getStart() == start) {
                transfer = busyGrid.get(i);
                busyGrid.remove(i);
            }
        }

        for (int i = transfer.getStart(); i < transfer.getStart() + transfer.getLength(); i++) {
            MemoryPanel.setFreeBackColor(i);
        }

        // 插入空闲表中的项
        MemoryEntry insertEntry = new MemoryEntry(transfer.getStart(), transfer.getLength());
        int insertIndex = 0;

        if (freeGrid.size() == 0) {
            // 如果空闲表是空的，直接加进入就可以了。
            freeGrid.add(insertEntry);
        } else {
            for (int i = 0, flag = 1; i < freeGrid.size() && flag == 1;) {
                if (insertEntry.getStart() > freeGrid.get(i).getStart()) {
                    i++;
                } else {
                    flag = 0;
                }
                // 在空闲表中
                insertIndex = i;
            }

            // 记录插入点前趋结点
            MemoryEntry preEntry = new MemoryEntry();
            // 记录出入点后趋结点
            MemoryEntry nextEntry = new MemoryEntry();

            // 标记，如果为1，表示前趋可以和插入项合为一，如果为0表示前趋不存在或者前趋不能和插入项合
            int preFlag = 0;
            int nextFlag = 0;

            // 如果插入点不再开始，也不再末尾，而在中间
            if (insertIndex != 0 && insertIndex != freeGrid.size()) {
                preEntry = freeGrid.get(insertIndex - 1);
                nextEntry = freeGrid.get(insertIndex);
            } else if (insertIndex == 0) {
                // 如果插入点在开始
                nextEntry = freeGrid.get(insertIndex);
                preFlag = 0;
            } else if (insertIndex == freeGrid.size()) {
                // 如果插入点在末尾
                preEntry = freeGrid.get(insertIndex - 1);
                nextFlag = 0;
            } // 判断插入点所在位置，并得到插入点前趋和后趋，如果前趋或后趋不存在，则相应的索引为0

            if (preEntry.getStart() + preEntry.getLength() == insertEntry.getStart()) {
                preFlag = 1;
            }

            if (insertEntry.getStart() + insertEntry.getLength() == nextEntry.getStart()) {
                nextFlag = 1;
            } // 判断前趋和后趋能否和插入点结合

            // 如果前面的项可以结合
            if (preFlag == 1 && nextFlag != 1) {
                preEntry.setLength(preEntry.getLength() + insertEntry.getLength());
            }

            // 如果后面的可以结合
            if (preFlag != 1 && nextFlag == 1) {
                nextEntry.setStart(insertEntry.getStart());
                nextEntry.setLength(insertEntry.getLength() + nextEntry.getLength());
            }

            // 如果前后的都可以结合
            if (preFlag == 1 && nextFlag == 1) {
                preEntry.setLength(preEntry.getLength() + insertEntry.getLength() + nextEntry.getLength());
                freeGrid.remove(nextEntry);
            }

            // 如果前面后面都不能结合
            if (preFlag != 1 && nextFlag != 1) {
                freeGrid.add(insertIndex, insertEntry);
            }
        }
    }

    public static void display(int start, int color) {
        for (int i = 0; i < busyGrid.size(); i++) {
            if (busyGrid.get(i).getStart() == start) {
                for (int j = start; j < start + busyGrid.get(i).getLength(); j++) {
                    MemoryPanel.setApplyBackColor(j, color);
                }
            }
        }
    }
}

/**
 * 内存区
 * 内存容器由若干项构成
 */
class MemoryEntry {
    /**
     * 分区地址
     */
    private int start;

    /**
     * 占用长度 注意：这里没有索引
     */
    private int length;

    public MemoryEntry() {
        start = -1;
        length = -1;
    }

    public MemoryEntry(int start, int len) {
        this.start = start;
        this.length = len;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }
}
