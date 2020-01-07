package com.newtorn.ToolkitsCore;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Container;

public class ImageContainer extends Container {
    private static final long serialVersionUID = 1L;
    
    Image img = null;

    public ImageContainer() {
        super();
    }

    public ImageContainer(Image img) {
        super();
        this.img = img;
    }

    public ImageContainer(String filename) {
        super();
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
    public void paint(Graphics g) {
        if (img != null) {
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
        super.paint(g);
    }
}