package com.newtorn.ViewCore;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.newtorn.BeanCore.ClipBoard;
import com.newtorn.BeanCore.Config;
import com.newtorn.BeanCore.Finder;
import com.newtorn.BeanCore.FolderEntry;
import com.newtorn.ServiceCore.BlockService;
import com.newtorn.ServiceCore.DiskService;
import com.newtorn.ServiceCore.FileService;
import com.newtorn.ServiceCore.FolderEntryService;
import com.newtorn.ServiceCore.TaskService;
import com.newtorn.ToolkitsCore.AppFrame;
import com.newtorn.ToolkitsCore.FrameUtil;
import com.newtorn.ToolkitsCore.ImageButton;
import com.newtorn.ToolkitsCore.MFrame;
import com.newtorn.ToolkitsCore.MWFrame;
import com.newtorn.ToolkitsCore.MenuPanel;
import com.newtorn.ToolkitsCore.TransparentButton;
import com.newtorn.ToolkitsCore.TransparentPanel;
import com.newtorn.ToolkitsCore.Utils;
import com.newtorn.ToolkitsCore.CompileUtil;

public class FileManagerApp extends AppFrame {

    private static AppFrame instance = null;
    private FilePane fp = null;

    private FileManagerApp() {
        instance = this;
        setAppInfo("FileManager", "FileManager");
    }

    @Override
    public void create(MWFrame parent) {
        super.create(parent);
        fp = new FilePane(frame);
        frame.toCenterPosition();
    }

    public static FileManagerApp getInstance() {
        if (instance == null) {
            instance = new FileManagerApp();
        }
        return (FileManagerApp) instance;
    }

    public void refresh() {
        if (fp != null) {
            fp.refresh(Finder.getCFB());
        }
    }

    public void refresh(int CFB) {
        if (fp != null) {
            fp.refresh(CFB);
        }
    }

    public class FilePane extends TransparentPanel {
        private static final long serialVersionUID = 1L;
        ImageIcon up = FrameUtil.createImageIcon("FM_LEFT_CHEVRON");
        ImageIcon go = FrameUtil.createImageIcon("FM_RIGHT_CHEVRON");
        ImageIcon folder = FrameUtil.createImageIcon("FM_FOLDER");
        ImageIcon folder_select = FrameUtil.createImageIcon("FM_FOLDER_SELECT");
        ImageIcon file = FrameUtil.createImageIcon("FM_FILE");

        MenuPanel mp_0 = new MenuPanel(frame.getParentFrame(), 5, 5);
        MenuPanel mp_1 = new MenuPanel(frame.getParentFrame(), 5, 5);
        MenuPanel mp_2 = new MenuPanel(frame.getParentFrame(), 5, 5);

        FolderEntryService fes = new FolderEntryService();
        BlockService bs = new BlockService();
        FileService fs = new FileService();
        FolderEntry folderFE, fileFE;

        public FilePane(MFrame frame) {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            setPreferredSize(new Dimension(500, 2400));

            JScrollPane JSP = new JScrollPane();
            add(JSP);
            frame.getCloseBtn().addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    Finder.init();
                }
            });

            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(this, BorderLayout.CENTER);
            frame.setSize(800, 600);

            mp_0.setSize(200, 75);
            mp_1.setSize(200, 100);
            mp_2.setSize(200, 75);

            TransparentButton tb_new_file = new TransparentButton("New File");
            TransparentButton tb_new_folder = new TransparentButton("New Folder");
            TransparentButton tb_paste = new TransparentButton("Paste");
            TransparentButton tb_cutFile = new TransparentButton("Cut File");
            TransparentButton tb_copyFile = new TransparentButton("Copy File");
            TransparentButton tb_deleteFile = new TransparentButton("Delete File");
            TransparentButton tb_executeFile = new TransparentButton("Execute File");
            TransparentButton tb_cutFolder = new TransparentButton("Cut Folder");
            TransparentButton tb_copyFolder = new TransparentButton("Copy Folder");
            TransparentButton tb_deleteFolder = new TransparentButton("Delete Folder");
            mp_0.getContentPane().add(tb_new_file);
            mp_0.getContentPane().add(tb_new_folder);
            mp_0.getContentPane().add(tb_paste);

            mp_1.getContentPane().add(tb_cutFile);
            mp_1.getContentPane().add(tb_copyFile);
            mp_1.getContentPane().add(tb_deleteFile);
            mp_1.getContentPane().add(tb_executeFile);

            mp_2.getContentPane().add(tb_cutFolder);
            mp_2.getContentPane().add(tb_copyFolder);
            mp_2.getContentPane().add(tb_deleteFolder);

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        mp_0.setLocation(e.getLocationOnScreen());
                        mp_0.setVisible(true);
                    }
                }
            });

            tb_new_folder.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    mp_0.setVisible(false);
                    while (true) {
                        String name = showInputDialog(frame, "Folder Name: ", "New Folder");
                        int result = 0;
                        if (name != null) {
                            if (name.contains(Config.FILE_SEPARATOR)) {
                                showMessageDialog(frame, "Illegal char in folder name", "FileManager Error");
                                break;
                            }
                            if (name.length() > Config.MAX_PRENAME_LEN) {
                                showMessageDialog(frame, "Illegal length of floder name", "FileManager Error");
                                continue;
                            }
                            result = fes.addFolderEntry(name);
                            if (result == 0)
                                showMessageDialog(frame, "The folder is full", "FileManager Error");
                            if (result == -1)
                                showMessageDialog(frame, "The floder is existed", "FileManager Error ");
                            if (result == 1) {
                                JLabel newlabel = new JLabel(name);
                                newlabel.setIcon(folder);
                                newlabel.setPreferredSize(new Dimension(150, 70));
                                add(newlabel);
                            }
                        }
                        DiskManagerApp.getInstance().refresh();
                        setVisible(false);
                        refresh();
                        setVisible(true);
                        break;
                    }
                }
            });

            tb_new_file.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    mp_0.setVisible(false);

                    while (true) {
                        String name = showInputDialog(frame, "File Name (default *.txt): ", "New File");
                        int result = 0;
                        if (name != null) {
                            if (name.contains(Config.FILE_SEPARATOR)) {
                                showMessageDialog(frame, "Illegal char in file name", "FileManager Error");
                                break;
                            }

                            String[] sn = name.split("\\.");
                            String fn = sn.length >= 1 ? sn[0] : name;
                            String en = sn.length >= 2 ? sn[1] : "";
                            if (fn.length() > Config.MAX_PRENAME_LEN) {
                                showMessageDialog(frame, "Illegal length of file name", "FileManager Error");
                                continue;
                            }
                            if (en.length() > Config.MAX_EXTNAME_LEN) {
                                showMessageDialog(frame, "Illegal length of extension name", "FileManager Error");
                                continue;
                            }

                            result = fes.addFileEntry(name);
                            if (result == 0)
                                showMessageDialog(frame, "The floder is full", "FileManager Error");
                            else if (result == -1)
                                showMessageDialog(frame, "The file is existed", "FileManager Error");
                            else if (result == 1) {
                                JLabel newlabel = new JLabel(name);
                                newlabel.setIcon(file);
                                newlabel.setPreferredSize(new Dimension(150, 70));
                                add(newlabel);
                            }
                        }
                        DiskManagerApp.getInstance().refresh();
                        setVisible(false);
                        refresh();
                        setVisible(true);
                        break;
                    }
                }
            });

            tb_paste.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    mp_0.setVisible(false);
                    if (ClipBoard.isFile() == true) {
                        int result = fs.postFile();
                        if (result == 0) {
                            showMessageDialog(frame, "ClipBoard is empty", "ClipBoard Error");
                        } else if (result == -1) {
                            showMessageDialog(frame, "the file name is existed", "ClipBoard Error");
                        } else {
                            DiskManagerApp.getInstance().refresh();
                            setVisible(false);
                            refresh();
                            setVisible(true);
                        }
                    } else {
                        int result = fs.postDir();
                        if (result == 0) {
                            showMessageDialog(frame, "ClipBoard is empty", "ClipBoard Error");
                        } else if (result == -1) {
                            showMessageDialog(frame, "the file name is existed", "ClipBoard Error");
                        } else {
                            DiskManagerApp.getInstance().refresh();
                            setVisible(false);
                            refresh();
                            setVisible(true);
                        }
                    }
                }
            });

            tb_cutFile.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    mp_1.setVisible(false);
                    fs.cutFile(fileFE);
                    fs.cutFolder(folderFE);
                }
            });

            tb_copyFile.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    mp_1.setVisible(false);
                    if (fs.existsFile(fileFE, Finder.getCFB())) {
                        if ((fileFE.getName() + "-dp").length() <= Config.MAX_PRENAME_LEN) {
                            fileFE.setName(fileFE.getName() + "-dp");
                            fs.copyFile(fileFE);
                        }
                    } else {
                        fs.copyFile(fileFE);
                    }
                }
            });

            tb_deleteFile.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    mp_1.setVisible(false);
                    fs.deleteFile(fileFE, Finder.getCFB());
                    setVisible(false);
                    refresh();
                    setVisible(true);
                    TextEditorApp.getInstance().init();
                }
            });

            tb_executeFile.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    mp_1.setVisible(false);
                }
            });

            tb_cutFolder.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    mp_2.setVisible(false);
                    fs.cutFolder(folderFE);
                }
            });

            tb_copyFolder.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    mp_2.setVisible(false);
                    if (fs.existsFile(folderFE, Finder.getCFB())) {
                        if ((folderFE.getName() + "-dp").length() <= Config.MAX_PRENAME_LEN) {
                            folderFE.setName(folderFE.getName() + "-dp");
                            fs.copyFolder(folderFE);
                        }
                    } else {
                        fs.copyFolder(folderFE);
                    }
                }
            });

            tb_deleteFolder.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    mp_2.setVisible(false);
                    fes.deleteFolderEntry(folderFE, Finder.getCFB());
                    DiskManagerApp.getInstance().refresh();
                    setVisible(false);
                    refresh();
                    setVisible(true);
                    TextEditorApp.getInstance().init();
                }
            });

            TransparentPanel tp = new TransparentPanel();
            tp.setLayout(new BorderLayout());

            ImageButton ib_previous = new ImageButton(up);
            ib_previous.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (Finder.getCFB() == 0) {
                        refresh(0);
                    } else {
                        int CDB = Finder.popPFB();
                        Finder.setCFB(CDB);
                        refresh(CDB);
                    }
                }
            });

            ImageButton ib_go = new ImageButton(go);
            JTextField path = new JTextField();
            ib_go.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() >= 1) {
                        Object[] result = FileService.searchFile(path.getText(), bs);
                        if ((boolean) result[0] == false) {
                            showMessageDialog(frame, "The floder not found: " + path.getText(), "FileManager Error");
                        } else {
                            Finder.setCFB((int) result[2]);
                            Finder.setPFB(Utils.objectToList(result[1]));
                            refresh((int) result[2]);
                        }
                    }
                }
            });

            tp.add(ib_previous, BorderLayout.WEST);
            tp.add(path, BorderLayout.CENTER);
            tp.add(ib_go, BorderLayout.EAST);
            frame.getContentPane().add(tp, BorderLayout.NORTH);

            tb_executeFile.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (fileFE.getExtName().equals("exe")) {
                        TaskService.create(frame, fileFE.getName(), CompileUtil.complie(frame, fs.openFile(fileFE)));
                    } else {
                        showMessageDialog(frame, "The file cannot run", "FileManager Error");
                    }
                }
            });

            refresh();
        }

        public void refresh() {
            refresh(Finder.getCFB());
        }

        public void refresh(int CFB) {
            List<FolderEntry> feList = bs.searchBlock(CFB);
            initFilePanel(feList);
        }

        public void initFilePanel(List<FolderEntry> feList) {
            setVisible(false);
            removeAll();
            setVisible(true);
            for (int i = 0; i < feList.size(); i++) {
                final FolderEntry fe = feList.get(i);
                if (fe.getAttribute() < 0) {
                    // 则为目录目录项
                    final JLabel flabel = new JLabel(fe.getName());
                    flabel.setIcon(folder);
                    flabel.setToolTipText(fe.getName());
                    flabel.setPreferredSize(new Dimension(150, 70));

                    flabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (e.getClickCount() == 1) {
                                if (e.getButton() == MouseEvent.BUTTON3) {
                                    folderFE = fe;
                                    mp_2.setVisible(true);
                                    mp_2.setLocation(e.getLocationOnScreen());
                                } else {
                                    setVisible(false);
                                    setVisible(true);
                                    flabel.setIcon(folder_select);
                                }
                            } else if (e.getClickCount() >= 2) {
                                flabel.setIcon(folder_select);
                                if (fe.getFolderAddress() == 0 && fe.getIndexAddress() == 0) {
                                    bs = new BlockService();
                                    fes = new FolderEntryService();
                                    int CDB = Finder.getCFB();
                                    int temp = bs.getFreeBlock();
                                    int[] position = bs.getFolderEntryPosition(fe, CDB);
                                    if (position[1] == Config.MAX_BLOCK_SEP && position[0] == 0) {
                                        throw new IllegalAccessError("Floder item position exception");
                                    }
                                    fe.setFolderAddress((short) temp);
                                    if (temp == 0) {
                                        throw new IllegalAccessError("No enough memory zone");
                                    } else {
                                        new DiskService().writeToDisk(fe, position[0], position[1]);
                                    }
                                    Finder.setPFB();
                                    Finder.setCFB(temp);
                                    initFilePanel(new ArrayList<FolderEntry>());
                                } else {
                                    if (fe.getFolderAddress() == 0) {
                                        int temp = fe.getIndexAddress();
                                        initFilePanel(bs.readBlock(-temp));
                                    } else {
                                        initFilePanel(bs.readBlock(fe.getFolderAddress()));
                                    }
                                }
                            }
                        }
                    });

                    setVisible(false);
                    add(flabel);
                    setVisible(true);
                } else {
                    final JLabel Tlabel = new JLabel(fe.getName() + "." + fe.getExtName());
                    Tlabel.setIcon(file);
                    Tlabel.setToolTipText(fe.getName() + "." + fe.getExtName());
                    Tlabel.setPreferredSize(new Dimension(150, 70));

                    Tlabel.addMouseListener(new MouseAdapter() {

                        public void mouseClicked(MouseEvent e) {
                            if (e.getClickCount() >= 2) {
                                TextEditorApp.getInstance().getFixedBtn().dispatchEvent(e);
                                TextEditorApp.getInstance().setFile(fs, fe);
                            } else if (e.getButton() == MouseEvent.BUTTON3) {
                                fileFE = fe;
                                mp_1.setVisible(true);
                                mp_1.setLocation(e.getLocationOnScreen());
                            }

                        }
                    });
                    setVisible(false);
                    add(Tlabel);
                    setVisible(true);
                }
            }
        }
    }

    static void showMessageDialog(Component parent, String msg, String title) {
        JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.YES_OPTION, FrameUtil.ErrorDialogImage);
    }

    static String showInputDialog(Component parent, String msg, String title) {
        Object i = JOptionPane.showInputDialog(parent, msg, title, JOptionPane.YES_NO_CANCEL_OPTION,
                FrameUtil.InputDialogImage, null, null);
        return i == null ? null : i.toString();
    }
}