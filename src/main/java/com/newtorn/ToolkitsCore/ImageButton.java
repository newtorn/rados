package com.newtorn.ToolkitsCore;

import java.awt.Image;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ImageButton extends JButton {
    private static final long serialVersionUID = 1L;

    protected Image icon = null;

    public ImageButton(String txt) {
        setText(txt);
    }

    public ImageButton() {
        init();    
    }

    public ImageButton(ImageIcon icon) {
        setIcon(icon);
        this.icon = icon.getImage();
        init();
    }

    public ImageButton(Image icon) {
        setIcon(new ImageIcon(icon));
        this.icon = icon;
        init();
    }

    @Override
    public void setIcon(Icon defaultIcon) {
        super.setIcon(defaultIcon);
    }

    private void init() {
        setMargin(new Insets(0, 0, 0, 0));
        setIconTextGap(0);
        setBorderPainted(false);
        setBorder(null);
        setText(null);
        setFocusPainted(false);
        setContentAreaFilled(false);
    }
}
