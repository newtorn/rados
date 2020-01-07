package com.newtorn.ViewCore;

import com.newtorn.ToolkitsCore.AppFrame;
import com.newtorn.ToolkitsCore.FrameUtil;
import com.newtorn.ToolkitsCore.MWFrame;
import com.newtorn.ToolkitsCore.TransparentButton;

public class TrashApp extends AppFrame {
    
    private static AppFrame instance = null;
    
    private int appNum = 0;

    private TrashApp() {
        instance = this;
        fixedBtn = runBtn;
        setAppInfo("Trash", "Trash");
    }

    @Override
    public void create(MWFrame parent) {
        super.create(parent);
        frame.setSize(800, 600);
        frame.toCenterPosition();
    }

    public static TrashApp getInstance() {
        if (instance == null) {
            instance = new TrashApp();
        }
        return (TrashApp) instance;
    }

    public boolean isEmpty() {
        return appNum == 0;
    }
    
    private String getIconFileName() {
        return "Trash" + (isEmpty() ? "Empty" : "Full");
    }

    public void setIcon(int theme, int w, int h) {
        getRunBtn().setIcon(FrameUtil.getThemeIcon(getIconFileName(), theme, w, h));
    }

    @Override
    public TransparentButton getFixedBtn(){
        return getRunBtn();
    }
}