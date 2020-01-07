package com.newtorn.ToolkitsCore;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public final class HintTextField extends JTextField {
    private static final long serialVersionUID = 1L;

    private String hintText;
    public HintTextField() {
        this(null);
    }

    public HintTextField(String hintText) {
        this.hintText = hintText;
        setOpaque(false);
        setText(hintText);
        setForeground(Color.GRAY);
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getText().equals(hintText)) {
                    setText("");
                    setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().equals("")) {
                    setText(hintText);
                    setForeground(Color.GRAY);
                }
            }
        });
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    public String getHintText() {
        return hintText;
    }
}