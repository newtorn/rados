package com.newtorn.ToolkitsCore;

import java.beans.PropertyVetoException;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

public abstract class AppFrame {
    protected String appName;
    protected String appIcon;
    protected MFrame frame = null;
    protected AppDock appDock = null;
    protected boolean isRunning = false;
    protected TransparentButton runBtn = new TransparentButton(false);
    protected TransparentButton fixedBtn = new TransparentButton(false);

    protected AppFrame() {
    }

    public void setAppInfo(String appName, String appIcon) {
        this.appName = appName;
        this.appIcon = appIcon;
    }

    public void setAppDock(AppDock appDock) {
        this.appDock = appDock;
    }
    
    public void setToolTip(MWFrame parent) {
        runBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                parent.showTooltip(appName, runBtn.getLocationOnScreen().x,
                        runBtn.getLocationOnScreen().y - runBtn.getHeight() / 2);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                parent.hideTooltip();
                super.mouseExited(e);
            }
        });
        fixedBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                parent.showTooltip(appName, fixedBtn.getLocationOnScreen().x,
                        fixedBtn.getLocationOnScreen().y - fixedBtn.getHeight() / 2);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                parent.hideTooltip();
                super.mouseExited(e);
            }
        });
    }

    public String getAppIcon() {
        return appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void create(MWFrame parent) {
        isRunning = true;
        frame = new MFrame(appName, parent);
        frame.setVisible(true);
        frame.toFront();
    }

    public void dispose() {
        try {
            if (frame.getOnceCacelClose()) {
                frame.setOnceCancelClose(false);
            } else {
                frame.outMax();
                frame.setClosed(true);
                isRunning = false;
                if (appDock != null) {
                    appDock.disposeRunApp(this);
                }
            }
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public void display() {
        frame.outMin();
    }

    public TransparentButton getRunBtn() {
        return runBtn;
    }

    public TransparentButton getFixedBtn() {
        return fixedBtn;
    }

    public MFrame getFrame() {
        return frame;
    }
}
