package com.newtorn.BeanCore;

/**
 * 进程任务控制
 */
public final class Task {
    /**
     * 进程ID
     */
    private String pid;
    
    /**
     * 内存开始地址
     */
    private int address;

    /**
     * 程序状态字
     */
    private int PSW;

    /**
     * 时间间隔
     */
    private int timeCell;
    
    /**
     * 指令指针
     */
    private int IP;

    /**
     * 数据缓冲寄存器
     */
    private byte[][] DR = new byte[2][2];

    /**
     * 阻塞原因
     */
    private int detail;

    private int run;

    public static final int Ready = 0;

    /**
     * IO中断
     */
    static final int IO_INTERRUPT = 1;

    /**
     * 软中断
     */
    static final int END_INTERRUPT = 2;

    /**
     * 设备运行中断
     */
    static final int RUN_INTERRUPT = 3;

    static final int TIMEOUT = 4;

    /**
     * 缺少设备A
     */
    static final int LACK_A = 1;

    /**
     * 缺少设备B
     */
    static final int LACK_B = 2;

    /**
     * 缺少设备C
     */
    static final int LACK_C = 3;

    static final int RUN_A = 1;
    static final int RUN_B = 2;
    static final int RUN_C = 3;

    public Task(String id, int start) {
        this.pid = id;
        this.address = start;
        PSW = Ready;
        detail = -1;
        run = -1;
        timeCell = 0;
        IP = 0;
    }

    public void setRun(int r) {
        run = r;
    }

    public int getRun() {
        return run;
    }

    public String getPID() {
        return pid;
    }

    public int getStart() {
        return address;
    }

    public void setPSW(int psw) {
        PSW = psw;
    }

    public int getPSW() {
        return PSW;
    }

    public void setTimeCell(int time) {
        timeCell = time;
    }

    public void decrease() {
        timeCell--;
    }

    public int getTime() {
        return timeCell;
    }

    public boolean isTimeOut() {
        if (timeCell == 0) {
            return true;
        } else
            return false;
    }

    public void setIP(int ip) {
        IP = ip;
    }

    public int getIP() {
        return IP;
    }

    public void setDetail(int d) {
        this.detail = d;
    }

    public int getDetail() {
        return detail;
    }

    public void getDR() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                CPU.DR[i][j] = DR[i][j];
            }
        }
    }

    public void setDR(byte[][] data) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                DR[i][j] = data[i][j];
            }
        }
    }
}
