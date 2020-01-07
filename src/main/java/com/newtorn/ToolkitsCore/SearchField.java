package com.newtorn.ToolkitsCore;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public final class SearchField extends TransparentPanel {
    private static final long serialVersionUID = 1L;

    ImageButton searchBtn = new ImageButton();
    HintTextField inputField = new HintTextField("搜索");

    public SearchField() {
        setLayout(new BorderLayout());
        add(searchBtn, BorderLayout.WEST);
        add(inputField, BorderLayout.EAST);
        setBackground(Color.LIGHT_GRAY);
        setSearchIcon(20, 20);
        inputField.setBorder(null);
    }

    public void setSearchIcon(int w, int h) {
        searchBtn.setIcon(FrameUtil.getThemeIcon("Search", FrameUtil.DARK_THEME, w-6, h-6));
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        setSearchIcon(d.height, d.height);
        inputField.setSize(d.width - searchBtn.getWidth(), d.height);
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        setSearchIcon(h, h);
        inputField.setSize(w - searchBtn.getWidth(), h);
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        setSearchIcon(preferredSize.height, preferredSize.height);
        inputField.setPreferredSize(
                new Dimension(preferredSize.width - searchBtn.getPreferredSize().width, preferredSize.height));
    }
}