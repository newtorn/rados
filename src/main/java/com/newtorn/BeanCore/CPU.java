package com.newtorn.BeanCore;

import java.util.regex.Pattern;

import com.newtorn.ServiceCore.TaskService;
import com.newtorn.ViewCore.PreferenceApp.Display;
import com.newtorn.ViewCore.ProcessManagerApp.ProcessPanel;

/**
 * 中央处理单元
 */
public final class CPU {
    /**
     * 程序状态字
     */
    private int psw;

    /**
     * 原因
     */
    private int detail;

    /**
     * 指令翻译寄存器
     */
    private byte[] IR = new byte[4];

    /**
     * 内存开始地址
     */
    private int start;

    /**
     * 指令指针
     */
    private int IP;

    /**
     * 数据缓冲寄存器
     */
    static byte[][] DR = new byte[2][2];

    /**
     * 进程控制块
     */
    private Task p;

    public CPU() {
        psw = Task.END_INTERRUPT;
        detail = 0;
        start = 0;
        IP = 0;
        p = null;
        DR[0][0] = 0;
        DR[0][1] = 0;
        DR[1][0] = 0;
        DR[1][1] = 0;
    }

    public void run() {
        // 如果有中断，则处理中断
        if (psw > Task.Ready) {
            if (psw == Task.TIMEOUT) {
                p.setIP(IP);
                p.setDR(DR);
                TaskService.toTail();
                // 释放寄存器资源
                this.freeDR();
                ProcessPanel.setPCBCenter("");

                p = TaskService.next();

                // 把PCB寄存器中的各种信息，存到CPU的各种寄存器中
                start = p.getStart();
                IP = p.getIP();

                // 初始化数据寄存器。
                p.getDR();
                psw = Task.Ready;
                p.setTimeCell(Display.value);

                // 正在运行的进程名发到界面显示
                ProcessPanel.setPCBName(p.getPID());
            }

            if (psw == Task.IO_INTERRUPT || psw == Task.RUN_INTERRUPT) {
                {
                    p.setPSW(psw);
                    p.setDetail(detail);
                    p.setIP(IP);
                    p.setDR(DR);
                }
                TaskService.block(p);

                // 释放寄存器资源
                this.freeDR();
                ProcessPanel.setPCBCenter("");
                if (!TaskQueue.isEmpty()) {
                    p = TaskService.next();
                } else {
                    p = TaskService.getNullTask();
                }

                // 把PCB寄存器中的各种信息，存到CPU的各种寄存器中
                start = p.getStart();
                IP = p.getIP();
                p.getDR();
                psw = Task.Ready;
                p.setTimeCell(Display.value);

                // 正在运行的进程名发到界面显示
                ProcessPanel.setPCBName(p.getPID());
            }

            if (psw == Task.END_INTERRUPT) {
                // destroy方法，允许销毁空闲进程（内容为：null）
                TaskService.destory(p);

                // 释放寄存器资源
                this.freeDR();
                ProcessPanel.setPCBCenter("");
                if (!TaskQueue.isEmpty()) {
                    p = TaskService.next();
                } else {
                    p = TaskService.getNullTask();
                }
                // 把PCB寄存器中的各种信息，存到CPU的各种寄存器中
                start = p.getStart();
                IP = p.getIP();
                p.getDR();
                psw = Task.Ready;
                p.setTimeCell(Display.value);
                // 正在运行的进程名发到界面显示
                ProcessPanel.setPCBName(p.getPID());
            }
        }

        IR = Memory.get(start, IP);

        explain(IR);
        IP++;
        ProcessPanel.setPCBTime(p.getTime());
        p.decrease();
        if (p.isTimeOut() && psw == Task.Ready) {
            psw = Task.TIMEOUT;
        }
    }

    /**
     * 解析每一条指令
     */
    public void explain(byte[] program) {
        String r1 = "[a-zA-Z]=[0-9];";
        String r2 = "[a-zA-Z][+-][+-];";
        String r3 = "![A|B|C][0-9];";
        String r4 = "[a-zA-Z][+][+];";
        String r5 = "[a-zA-Z][-][-];";
        String r6 = "end;";

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 4; i++)
            buffer.append((char) program[i]);
        String text = new String(buffer);

        // 执行语句发送到图形界面显示
        ProcessPanel.setPCBExeSentence(text);

        if (Pattern.matches(r1, text)) {
            // 第一个寄存器空
            if (DR[0][0] == 0) {
                DR[0][0] = program[0];
                DR[0][1] = (byte) (program[2] - '0');
            } else if (DR[1][0] == 0) {
                // 第一个寄存器不空，第二个寄存器空

                // 一不空，二空，和第一个不同
                if (DR[0][0] != program[0]) {
                    DR[1][0] = program[0];
                    DR[1][1] = (byte) (program[3] - '0');
                } else {
                    // 一不空，二空，和第一个不同
                    DR[0][1] = (byte) (program[3] - '0');
                }
            } else {
                // 两个都不空

                if (DR[0][0] == program[0]) {
                    DR[0][1] = (byte) (program[3] - '0');
                }
                if (DR[1][0] == program[0]) {
                    DR[1][1] = (byte) (program[3] - '0');
                }
            }
        }

        if (Pattern.matches(r2, text)) {
            // 如果是++操作
            if (Pattern.matches(r4, text)) {
                // 如果是第一个变量++
                if (program[0] == DR[0][0]) {
                    DR[0][1]++;
                    ProcessPanel.setPCBCenter("" + (char) DR[0][0] + "=" + DR[0][1]);
                }

                // 如果是第二个变量++
                if (program[0] == DR[1][0]) {
                    DR[1][1]++;
                    ProcessPanel.setPCBCenter("" + (char) DR[1][0] + "=" + DR[1][1]);
                }
            }

            // 如果是--操作
            if (Pattern.matches(r5, text)) {
                // 如果是第一个变量--
                if (program[0] == DR[0][0]) {
                    DR[0][1]--;
                    ProcessPanel.setPCBCenter("" + (char) DR[0][0] + "=" + DR[0][1]);
                }
                // 如果是第二个变量--
                if (program[0] == DR[1][0]) {
                    DR[1][1]--;
                    ProcessPanel.setPCBCenter("" + (char) DR[1][0] + "=" + DR[1][1]);
                }
            }
        }

        if (Pattern.matches(r3, text)) {
            DeviceEntry e = Device.applyDevice((char) program[1], program[2] - '0');
            if (e == null) {
                psw = Task.IO_INTERRUPT;
                if((char)program[1]=='A' )
				{
					detail = Task.LACK_A;
					IP--;
				}
				if((char)program[1]=='B' )
				{
					detail = Task.LACK_B;
					IP--;
				}
				if((char)program[1]=='C' )
				{
					detail = Task.LACK_C;
					IP--;
				}

            } else {
                psw = Task.RUN_INTERRUPT;
                if ((char)program[1] == 'A') {
                    p.setRun(Task.RUN_A);
                }
                if ((char)program[1] == 'B') {
                    p.setRun(Task.RUN_B);
                }
                if ((char)program[1] == 'C') {
                    p.setRun(Task.RUN_C);
                }
                e.use(p);
            }
        }

        if (Pattern.matches(r6, text)) {
            psw = Task.END_INTERRUPT;
        }
    }

    public void freeDR() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                DR[i][j] = 0;
            }
        }
    }
}
