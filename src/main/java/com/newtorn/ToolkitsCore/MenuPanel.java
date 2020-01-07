package com.newtorn.ToolkitsCore;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class MenuPanel extends MFrame {
    private static final long serialVersionUID = 1L;

    public MenuPanel(int hgap, int vgap) {
        this(null, hgap, vgap);
    }

    public MenuPanel(MWFrame parent, int hgap, int vgap) {
        super(parent);
        setVisible(false);
        setResizable(false);
        setBarVisible(false);
        setFrameBorder(false);
        setLayout(new VFlowLayout(FlowLayout.LEFT, hgap, vgap, true, false));
        setForeground(FrameUtil.getThemeFontColor(getTheme()));
        setBackground(new Color(127, 127, 127, 127));
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
                setVisible(false);
            }
        });
    }
}