package com.newtorn.ViewCore;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.newtorn.BeanCore.Config;
import com.newtorn.ServiceCore.BlockService;
import com.newtorn.ToolkitsCore.AppFrame;
import com.newtorn.ToolkitsCore.FrameUtil;
import com.newtorn.ToolkitsCore.MWFrame;

public class DiskManagerApp extends AppFrame {
    private static AppFrame instance = null;

    private DiskPanel dp = null;

    private DiskManagerApp() {
        instance = this;
        setAppInfo("DiskManager", "DiskManager");
    }

    @Override
    public void create(MWFrame parent) {
        super.create(parent);
        frame.getContentPane().setLayout(new BorderLayout());
        dp = new DiskPanel();
        frame.getContentPane().add(dp, BorderLayout.CENTER);
        frame.setSize(300, 400);
        frame.toCenterPosition();
    }

    public void refresh() {
        if (dp != null) {
            dp.refresh();
        }
    }

    public static DiskManagerApp getInstance() {
        if (instance == null) {
            instance = new DiskManagerApp();
        }
        return (DiskManagerApp)instance;
    }

    private class DiskPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        JPanel status = new JPanel(new GridLayout(16, 16));
        ImageIcon i1 = FrameUtil.createImageIcon("CUBE_01");
        ImageIcon i2 = FrameUtil.createImageIcon("CUBE_02");
        JLabel[] block = new JLabel[Config.MAX_DISK_BLOCK_ENTRIES];
        public DiskPanel() {
            super();
            setLayout(new BorderLayout());
            for (int i = 0; i < Config.MAX_DISK_BLOCK_ENTRIES; i++) {
                block[i] = new JLabel();
                block[i].setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            }
            for (int i = 0; i < Config.MAX_DISK_BLOCK_ENTRIES; i++) {
                status.add(block[i]);
            }
            add(status, BorderLayout.NORTH);

            JPanel precent = new JPanel(new GridLayout(1, 4));
            JLabel l = new JLabel(i1);
            l.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            precent.add(l);
            precent.add(new JLabel("Used"));
            JLabel r = new JLabel(i2);
            r.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            precent.add(r);
            precent.add(new JLabel("Free"));
            add(precent, BorderLayout.SOUTH);

            refresh();
        }

        public void refresh() {
            List<Integer> list = new BlockService().getAllFreeBLock();
            for (int i = 0; i < Config.MAX_DISK_BLOCK_ENTRIES; i++) {
                block[i].setIcon(list.contains(i) ? i2 : i1);
            }
        }
    }
}