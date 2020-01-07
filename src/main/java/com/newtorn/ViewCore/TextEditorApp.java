package com.newtorn.ViewCore;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import com.newtorn.BeanCore.Config;
import com.newtorn.BeanCore.Finder;
import com.newtorn.BeanCore.FolderEntry;
import com.newtorn.ServiceCore.BlockService;
import com.newtorn.ServiceCore.FileService;
import com.newtorn.ServiceCore.FolderEntryService;
import com.newtorn.ToolkitsCore.AppFrame;
import com.newtorn.ToolkitsCore.FrameUtil;
import com.newtorn.ToolkitsCore.MFrame;
import com.newtorn.ToolkitsCore.MWFrame;
import com.newtorn.ToolkitsCore.Utils;

public class TextEditorApp extends AppFrame {

    private static AppFrame instance = null;
    private TextPane textPane = null;
    private FileService fs = null;
    private FolderEntry fe = null;

    private TextEditorApp() {
        instance = this;
        setAppInfo("TextEditor", "TextEditor");
    }

    @Override
    public void create(MWFrame parent) {
        super.create(parent);
        textPane = new TextPane(frame);
        frame.setSize(800, 600);
        frame.toCenterPosition();
        setFile(fs, fe);
    }

    public static TextEditorApp getInstance() {
        if (instance == null) {
            instance = new TextEditorApp();
        }
        return (TextEditorApp) instance;
    }

    public void setFile(FileService fs, FolderEntry fe) {
        this.fs = fs;
        this.fe = fe;
        if (fs != null && fe != null) {
            frame.setTitle(fe.getName());
            textPane.setText(fs.openFile(fe));
        }
    }

    public void init() {
        fs = null;
        fe = null;
    }

    class TextPane extends JTextArea {
        private static final long serialVersionUID = 1L;

        public TextPane(MFrame frame) {
            setLineWrap(true);
            setWrapStyleWord(true);
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(this, BorderLayout.CENTER);
            frame.getCloseBtn().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (fs == null || fe == null) {
                        if (JOptionPane.showConfirmDialog(frame.getParentFrame(), "file not exists, create a new file?",
                                "TextEditor Error", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                                FrameUtil.ErrorDialogImage) == JOptionPane.YES_OPTION) {
                            while (true) {
                                Object i = JOptionPane.showInputDialog(frame.getParentFrame(), "File Path", "Save File",
                                        JOptionPane.YES_NO_OPTION, FrameUtil.InputDialogImage, null, null);
                                if (i != null) {
                                    File f = new File((String) i);

                                    String[] sn = f.getName().split("\\.");
                                    String fn = sn.length >= 1 ? sn[0] : f.getName();
                                    String en = sn.length >= 2 ? sn[1] : "";
                                    if (fn.length() > Config.MAX_PRENAME_LEN) {
                                        showMessageDialog(frame, "Illegal length of file name", "TextEditor Error");
                                        continue;
                                    }
                                    if (en.length() > Config.MAX_EXTNAME_LEN) {
                                        showMessageDialog(frame, "Illegal length of extension name", "TextEditor Error");
                                        continue;
                                    }

                                    Object[] result = FileService.searchFile(f.getParent(), new BlockService());

                                    if ((boolean) result[0] == false) {
                                        showMessageDialog(frame.getParentFrame(),
                                                "The floder not found: " + f.getParent(), "FileManager Error");
                                        frame.setOnceCancelClose(true);
                                        break;
                                    } else {
                                        Finder.setCFB((int) result[2]);
                                        Finder.setPFB(Utils.objectToList(result[1]));
                                        FileManagerApp.getInstance().refresh((int) result[2]);
                                        DiskManagerApp.getInstance().refresh();
                                    }

                                    int res = new FolderEntryService().addFileEntry(f.getName());
                                    if (res == 0)
                                        showMessageDialog(frame.getParentFrame(), "The floder is full",
                                                "FileManager Error");
                                    else if (res == -1)
                                        showMessageDialog(frame.getParentFrame(), "The file is existed",
                                                "FileManager Error");
                                    else {
                                        FileManagerApp.getInstance().refresh();
                                        DiskManagerApp.getInstance().refresh();
                                    }
                                }
                                break;
                            }
                        }
                    } else {
                        fs.saveFile(getText(), fe);
                        FileManagerApp.getInstance().refresh();
                        DiskManagerApp.getInstance().refresh();
                    }
                }
            });
        }
    }

    static void showMessageDialog(Component parent, String msg, String title) {
        JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.YES_OPTION, FrameUtil.ErrorDialogImage);
    }
}