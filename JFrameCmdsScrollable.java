/** 
 * basic form to display and process grand tours and scroll files;
 * generic toggle button is meant to have new text when this class is subsequently extended;
 */
public class JFrameCmdsScrollable extends javax.swing.JFrame {
    protected javax.swing.JList jListCmd;
    protected javax.swing.JPanel jPanelCmd;
    protected javax.swing.JScrollPane jScrollPaneCmd;
    protected javax.swing.JTextField jTextFieldFilename;
    protected javax.swing.JToggleButton jToggleButtonAutoUpdate;
    protected javax.swing.JToggleButton jToggleButtonLoad;
    protected javax.swing.JToggleButton jToggleButtonNext;
    protected javax.swing.JToggleButton jToggleButtonOption1;
    protected javax.swing.JToggleButton jToggleButtonOption2;
    protected javax.swing.JToggleButton jToggleButtonPrevious;
    
    public JFrameCmdsScrollable(String title) {
        super(title);
        setDefaultLookAndFeelDecorated(true);
        initComponents();
        screenPlacement.getInstance().center(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        jPanelCmd = new javax.swing.JPanel();
        jScrollPaneCmd = new javax.swing.JScrollPane();
        jListCmd = new javax.swing.JList();
        jToggleButtonOption1 = new javax.swing.JToggleButton();
        jToggleButtonOption2 = new javax.swing.JToggleButton();
        jToggleButtonNext = new javax.swing.JToggleButton();
        jToggleButtonPrevious = new javax.swing.JToggleButton();
        jToggleButtonLoad = new javax.swing.JToggleButton();
        jTextFieldFilename = new javax.swing.JTextField();
        jToggleButtonAutoUpdate = new javax.swing.JToggleButton();

        getContentPane().setLayout(new AbsoluteLayout());

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanelCmd.setLayout(new AbsoluteLayout());

        jListCmd.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListCmd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jListCmdMouseReleased(evt);
            }
        });

        jScrollPaneCmd.setViewportView(jListCmd);

        jPanelCmd.add(jScrollPaneCmd, new AbsoluteConstraints(0, 40, 550, 420));

        jToggleButtonOption1.setText("option1");
        jToggleButtonOption1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonOption1ActionPerformed(evt);
            }
        });

        jPanelCmd.add(jToggleButtonOption1, new AbsoluteConstraints(320, 470, 80, -1));

        jToggleButtonOption2.setText("option2");
        jToggleButtonOption2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonOption2ActionPerformed(evt);
            }
        });

        jPanelCmd.add(jToggleButtonOption2, new AbsoluteConstraints(410, 470, 80, -1));

        jToggleButtonNext.setText("next");
        jToggleButtonNext.setToolTipText("next in list");
        jToggleButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonNextActionPerformed(evt);
            }
        });

        jPanelCmd.add(jToggleButtonNext, new AbsoluteConstraints(230, 470, 80, -1));

        jToggleButtonPrevious.setText("previous");
        jToggleButtonPrevious.setToolTipText("previous in list");
        jToggleButtonPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonPreviousActionPerformed(evt);
            }
        });

        jPanelCmd.add(jToggleButtonPrevious, new AbsoluteConstraints(140, 470, 80, -1));

        jToggleButtonLoad.setText("load");
        jToggleButtonLoad.setToolTipText("load a file");
        jToggleButtonLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonLoadActionPerformed(evt);
            }
        });

        jPanelCmd.add(jToggleButtonLoad, new AbsoluteConstraints(50, 470, 80, -1));

        jTextFieldFilename.setEditable(false);
        jTextFieldFilename.setText("(filename)");
        jPanelCmd.add(jTextFieldFilename, new AbsoluteConstraints(0, 10, 420, -1));

        jToggleButtonAutoUpdate.setText("auto update");
        jToggleButtonAutoUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonAutoUpdateActionPerformed(evt);
            }
        });

        jPanelCmd.add(jToggleButtonAutoUpdate, new AbsoluteConstraints(440, 8, 100, -1));

        getContentPane().add(jPanelCmd, new AbsoluteConstraints(0, 0, 550, 500));

        pack();
    }

    protected void jToggleButtonAutoUpdateActionPerformed(java.awt.event.ActionEvent evt) {
    }

    protected void jListCmdMouseReleased(java.awt.event.MouseEvent evt) {
    }

    protected void jToggleButtonLoadActionPerformed(java.awt.event.ActionEvent evt) {
    }

    protected void jToggleButtonPreviousActionPerformed(java.awt.event.ActionEvent evt) {
    }

    protected void jToggleButtonOption2ActionPerformed(java.awt.event.ActionEvent evt) {
    }

    protected void jToggleButtonOption1ActionPerformed(java.awt.event.ActionEvent evt) {
    }

    protected void jToggleButtonNextActionPerformed(java.awt.event.ActionEvent evt) {
    }
    
    protected void exitForm(java.awt.event.WindowEvent evt) {
        setVisible(false);
    }
    
    void setFilename(String filename) {
        jTextFieldFilename.setText(filename);
    }
}
