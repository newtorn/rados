package com.newtorn.BeanCore;

import java.util.ArrayList;

/**
 * 进程控制块队列
 */
public final class TaskQueue {
    static QueueItem[] queue = new QueueItem[10];
    static Task nullTask;

    /**
     * 就绪队列索引
     */
    static int top;
    static int tail;

    /**
     * 空白FCB索引
     */
    static int blankIndex;

    /**
     * 阻塞队列索引
     */
    static int blockIndex;

    static {
        top = -1;
        tail = -1;
        blankIndex = 0;
        blockIndex = -1;
        for (int i = 0; i < 10; i++) {
            QueueItem item = new QueueItem();
            queue[i] = item;
            if (i != 9) {
                item.next = i + 1;
            } else {
                item.next = -1;
            }
        }
    }

    static {
        int start = Memory.apply(1);
        nullTask = new Task("NULL TASK", start);
        ArrayList<String> array = new ArrayList<String>();
        array.add("end;");
        Memory.put(array, start);
    }

    public TaskQueue() {

    }

    public static Task getNullTask() {
        return nullTask;
    }

    public static boolean isEmpty() {
        if (top == -1 && tail == -1) {
            return true;
        } else
            return false;
    }

    public static int apply() {
        int index = blankIndex;
        if (index != -1)
            blankIndex = queue[blankIndex].next;
        return index;
    }

    public static void put(Task t, int index) {
        // 当就绪队列为空时
        if (TaskQueue.isEmpty() == true) {
            top = index;
            tail = index;
            queue[index].next = -1;
            queue[tail].task = t;
        } else {
            queue[tail].next = index;
            tail = index;
            queue[tail].next = -1;
            queue[tail].task = t;
        }
    }

    public static void awake(Task t) {
        int preIndex = -1;

        // 要阻塞的PCB，在表中的索引
        int index = -1;

        if (queue[blockIndex].task == t) {
            index = blockIndex;
            blockIndex = queue[blockIndex].next;
        } else {
            preIndex = blockIndex;
            index = queue[blockIndex].next;
            for (; queue[index].next != -1; index = queue[index].next, preIndex = queue[preIndex].next) {
                if (queue[index].task == t) {
                    break;
                }
            }
            queue[preIndex].next = queue[index].next;
        }
        TaskQueue.put(t, index);
    }

    /**
     * 只有位于就绪队列之首的进程才有运行权，只有它才会被阻塞
     */
    public static void block() {
        int index = top;
        if (top == tail) {
            top = -1;
            tail = -1;
        } else
            top = queue[top].next;

        queue[index].next = blockIndex;
        blockIndex = index;
    }

    /**
     * 要释放的pcb也肯定在队首，这个很重要
     */
    public static void free() {
        int index = top;
        if (top == tail) {
            top = -1;
            tail = -1;
        } else
            top = queue[top].next;

        queue[index].next = blankIndex;
        blankIndex = index;
    }

    public static Task search(char ch) {
        int reason = 0;
        int index = blockIndex;
        Task task = null;
        if (ch == 'A') {
            reason = Task.LACK_A;
        }
        if (ch == 'B') {
            reason = Task.LACK_B;
        }
        if (ch == 'C') {
            reason = Task.LACK_C;
        }

        outer: while (index != -1) {
            Task t = queue[index].task;
            if (t.getPSW() == Task.IO_INTERRUPT) {
                if (t.getDetail() == reason) {
                    task = t;
                    break outer;
                }
            }
            index = queue[index].next;
        }
        return task;
    }

    /**
     * 得到就绪队列中的首进程，注意，这里是得到就绪队列首元素，并没有把它从中移除，也没有必要移除
     */
    public static Task next() {
        Task t = null;
        if (TaskQueue.isEmpty() == false) {
            t = queue[top].task;
        }
        return t;
    }

    /**
     * 把时间片用完的是进程，放到就绪队列的尾部
     */
    public static void toTail() {
        int topBuffer = top;

        // 要明白，调用这个函数的时候，就绪队列中是有PCB的，空闲队列不可能调用到这个函数，因为时间片最小为1。
        if (top != tail) {
            top = queue[top].next;
            queue[tail].next = topBuffer;
            tail = queue[tail].next;
            queue[topBuffer].next = -1;
        }
    }

    public static String getReadyText() {
        int index = top;
        StringBuffer buffer = new StringBuffer();
        while (index != -1) {
            int s = queue[index].task.getStart();
            if (index == top)
                Memory.display(s, 0);
            else
                Memory.display(s, 1);
            buffer.append(queue[index].task.getPID());
            buffer.append("\n");
            index = queue[index].next;
        }
        return new String(buffer);
    }

    public static String getBlockText() {
        int index = blockIndex;
        StringBuffer buffer = new StringBuffer();
        while (index != -1) {
            int s = queue[index].task.getStart();
            Memory.display(s, -1);
            buffer.append(queue[index].task.getPID());
            buffer.append("\n");
            if (queue[index].task.getPSW() == Task.RUN_INTERRUPT) {
                if (queue[index].task.getRun() == Task.RUN_A) {
                    buffer.append("Run device A");
                    buffer.append("\n");
                }
                if (queue[index].task.getRun() == Task.RUN_B) {
                    buffer.append("Run device B");
                    buffer.append("\n");
                }
                if (queue[index].task.getRun() == Task.RUN_C) {
                    buffer.append("Run device C");
                    buffer.append("\n");
                }
            }
            if (queue[index].task.getPSW() == Task.IO_INTERRUPT) {
                if (queue[index].task.getDetail() == Task.LACK_A) {
                    buffer.append("Lost device A");
                    buffer.append("\n");
                }
                if (queue[index].task.getDetail() == Task.LACK_B) {
                    buffer.append("Lost device B");
                    buffer.append("\n");
                }
                if (queue[index].task.getDetail() == Task.LACK_C) {
                    buffer.append("Lost device C");
                    buffer.append("\n");
                }
            }
            index = queue[index].next;
        }
        return new String(buffer);
    }
}

/**
 * 队列项
 */
class QueueItem {
    Task task;
    int next;

    public QueueItem() {
        task = null;
        next = -1;
    }
}