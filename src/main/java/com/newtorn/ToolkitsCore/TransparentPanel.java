package com.newtorn.ToolkitsCore;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class TransparentPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    {
        setOpaque(false);
    }

    public TransparentPanel() {
        super();
    }
    
    public TransparentPanel(LayoutManager mgr) {
        super(mgr);
    }

    public TransparentPanel(LayoutManager mgr, Color backColor) {
        super(mgr);
        setBackground(backColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        Rectangle r = g.getClipBounds();
        g.fillRect(r.x, r.y, r.width, r.height);
        super.paintComponent(g);
    }
}