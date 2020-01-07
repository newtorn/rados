package com.newtorn.BeanCore;

import java.util.ArrayList;

import com.newtorn.ServiceCore.TaskService;
import com.newtorn.ViewCore.PreferenceApp.DevicePanel;

/**
 * 设备
 */
public final class Device {
    static ArrayList<DeviceEntry> DeviceGrid;
    static {
        DeviceGrid = new ArrayList<DeviceEntry>();
        DeviceGrid.add(new DeviceEntry('A'));
        DeviceGrid.add(new DeviceEntry('A'));
        DeviceGrid.add(new DeviceEntry('B'));
        DeviceGrid.add(new DeviceEntry('B'));
        DeviceGrid.add(new DeviceEntry('C'));
        DeviceGrid.add(new DeviceEntry('C'));
    }

    public static DeviceEntry applyDevice(char name, int t) {
        for (DeviceEntry e : DeviceGrid) {
            if (e.getName() == name && e.isFree() == true) {
                e.setFree(false);
                e.setTime(t);
                return e;
            }
        }
        return null;
    }

    public static void run() {
        for (int i = 0; i < DeviceGrid.size(); ++i) {
            DeviceEntry e = DeviceGrid.get(i);
            if (!e.isFree()) {
                e.decrease();
                if (e.isTimeOver()) {
                    e.setFree(true);
                    TaskService.awake(e.getPCB());
                    e.setPCB(null);
                    TaskService.awake(e.getName());
                    DevicePanel.setText(i, "Free");
                } else {
                    DevicePanel.setText(i, e.getPCB().getPID(), e.getTime());
                }
            }
        }
    }
}

/**
 * 设备项
 */
class DeviceEntry {
    /**
     * 使用的进程
     */
    private Task pcb;

    /**
     * 设备名称
     */
    private char name;

    /**
     * 使用时间
     */
    private int time;

    /**
     * true表示空闲 false表示正在使用
     */
    private boolean isFree;

    public DeviceEntry(char name) {
        this.name = name;
        pcb = null;
        time = 0;
        isFree = true;
    }

    public void use(Task p) {
        pcb = p;
        isFree = false;
    }

    public void setPCB(Task p) {
        pcb = p;
    }

    public Task getPCB() {
        return pcb;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean b) {
        isFree = b;
    }

    public char getName() {
        return name;
    }

    public void decrease() {
        time--;
    }

    public void setTime(int t) {
        time = t;
    }

    public int getTime() {
        return time;
    }

    public boolean isTimeOver() {
        return time == 0;
    }
}