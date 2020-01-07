package com.newtorn.BeanCore;

import java.util.List;
import java.util.Stack;

/**
 * 访达 动态记录树形目录的磁盘块变化，方便上级目录操作
 */
public final class Finder {
    /**
     * 父目录的磁盘块号
     */
    private static Stack<Integer> PFB = new Stack<Integer>();

    /**
     * 当前目录的磁盘块号
     */
    private static int CFB = 0;

    /**
     * 获取父目录的磁盘块号
     * 
     * @return
     */
    public static int getPFB() {
        return PFB.isEmpty() ? 0 : PFB.lastElement();
    }

    /**
     * 初始化访达
     */
    public static void init() {
        CFB = 0;
        PFB.clear();
    }

    /**
     * 设置当前目录的磁盘号
     * 
     * @param CFB
     */
    public static void setCFB(int cfb) {
        CFB = cfb;
    }

    /**
     * 获取当前目录的磁盘号
     */
    public static int getCFB() {
        return CFB;
    }

    /**
     * 父目录磁盘号出栈
     */
    public static int popPFB() {
        return PFB.isEmpty() ? 0 : PFB.pop();
    }

    /**
     * 当前目录磁盘号放入父目录磁盘号栈
     */
    public static void setPFB() {
        PFB.push(CFB);
    }

    public static void setPFB(Stack<Integer> list) {
        PFB = list;
    }

    public static void setPFB(List<Integer> list) {
        PFB = new Stack<>();
        for (Integer i : list) {
            PFB.push(i);
        }
    }
}
