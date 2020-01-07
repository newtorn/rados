package com.newtorn.ViewCore;

import com.newtorn.ToolkitsCore.AppFrame;
import com.newtorn.ToolkitsCore.MWFrame;

public class MusicApp extends AppFrame {
    
    private static AppFrame instance = null;

    private MusicApp() {
        instance = this;
        setAppInfo("Music", "Music");
    }

    @Override
    public void create(MWFrame parent) {
        super.create(parent);
        frame.setSize(300, 400);
        frame.toCenterPosition();
    }

    public static MusicApp getInstance() {
        if (instance == null) {
            instance = new MusicApp();
        }
        return (MusicApp)instance;
    }
}