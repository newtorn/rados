package com.newtorn.ToolkitsCore;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

/**
 * 自定义屏幕管理
 */
public final class MScreen {
    /**
     * 显示器
     */
    private GraphicsDevice device;

    public MScreen() {
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }

    /**
     * 进入全屏模式
     * 
     * @param dm  显示模式
     * @param mwf 桌面窗体
     */
    public void setFullScreen(DisplayMode dm, MWFrame mwf) {
        mwf.setResizable(false);
        mwf.setBarVisible(false);
        mwf.setFrameBorder(false);
        device.setFullScreenWindow(mwf);
        if (dm != null && device.isDisplayChangeSupported()) {
            try {
                device.setDisplayMode(dm);
            } catch (IllegalArgumentException ex) {
            }
        }
    }

    /**
     * 获取全屏窗体
     * 
     * @return
     */
    public Window getFullScreenWindow() {
        return device.getFullScreenWindow();
    }

    /**
     * 退出全屏模式
     */
    public void outFullScreen() {
        MWFrame w = (MWFrame)device.getFullScreenWindow();
        device.setFullScreenWindow(null);
        if (w != null) {
            w.setResizable(true);
            w.setBarVisible(true);
            w.setFrameBorder(true);
            w.setLocationToCenter();
        }
    }
}