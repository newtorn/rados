package com.newtorn.ServiceCore;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.newtorn.BeanCore.Memory;
import com.newtorn.BeanCore.Task;
import com.newtorn.BeanCore.TaskQueue;
import com.newtorn.ToolkitsCore.FrameUtil;
import com.newtorn.ToolkitsCore.MFrame;

public class TaskService {
    public static Task next() {
        Task t = TaskQueue.next();
        return t;
    }

    public static Task getNullTask() {
        return TaskQueue.getNullTask();
    }

    public static void toTail() {
        TaskQueue.toTail();
    }

    public static void create(MFrame frame, String id, ArrayList<String> text) {
        int index = TaskQueue.apply();
        if (index != -1) {
            int start = Memory.apply(text.size());
            if (start != -1) {
                Task p = new Task(id, start);
                TaskQueue.put(p, index);
                Memory.put(text, start);
            } else {
                JOptionPane.showMessageDialog(frame.getParentFrame(), "No enough memory", "Allocate memroy failed",
                        JOptionPane.ERROR_MESSAGE, FrameUtil.ErrorDialogImage);
            }
        } else {
            JOptionPane.showMessageDialog(frame.getParentFrame(), "Max of create process numbers is 10", "Allocate PCB memory failed",
                    JOptionPane.ERROR_MESSAGE, FrameUtil.ErrorDialogImage);
        }
    }

    public static void destory(Task p) {
        if (p != null && p.getStart() != 0)
        {
            Memory.free(p.getStart());
            TaskQueue.free();
        }
    }

    public static void block(Task p) {
        if (p != null && p.getStart() != 0)
            TaskQueue.block();
    }

    public static void awake(Task p) {
        TaskQueue.awake(p);
        p.setPSW(Task.Ready);
    }

    public static void awake(char ch) {
        Task p = TaskQueue.search(ch);
        if (p != null) {
            TaskQueue.awake(p);
            p.setPSW(Task.Ready);
        }
    }
}
