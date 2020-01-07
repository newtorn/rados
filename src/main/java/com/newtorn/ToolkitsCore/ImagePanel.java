package com.newtorn.ToolkitsCore;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    Image img = null;

    public ImagePanel() {
        super();
    }

    public ImagePanel(LayoutManager arg0) {
        super(arg0);
    }

    public ImagePanel(LayoutManager arg0, boolean arg1) {
        super(arg0, arg1);
    }

    public ImagePanel(boolean arg0) {
        super(arg0);
    }

    public ImagePanel(Image img) {
        super();
        this.img = img;
    }

    public ImagePanel(Image img, LayoutManager arg0) {
        super(arg0);
        this.img = img;
    }

    public ImagePanel(Image img, LayoutManager arg0, boolean arg1) {
        super(arg0, arg1);
        this.img = img;
    }

    public ImagePanel(String filename) {
        super();
        this.img = FrameUtil.createImage(filename);
    }

    public ImagePanel(String filename, LayoutManager arg0) {
        super(arg0);
        this.img = FrameUtil.createImage(filename);
    }

    public ImagePanel(String filename, LayoutManager arg0, boolean arg1) {
        super(arg0, arg1);
        this.img = FrameUtil.createImage(filename);
    }

    public Image getImage() {
        return img;
    }

    public void setImage(Image img) {
        this.img = img;
        repaint();
    }

    public void setImage(String filename) {
        img = FrameUtil.createImage(filename);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
    }
}