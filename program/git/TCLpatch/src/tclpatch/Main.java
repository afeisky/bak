//# Copyright (C) 2017 TCL Mobile
package tclpatch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JDesktopPane;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.w3c.dom.*;

/**
 *
 * @author chaofei.wu.hz@tcl.com
 */
public class Main extends JFrame {

	private boolean _DEBUG=true;
	private boolean _DEBUG_EXT=false;
	
    private final DefaultTreeModel defaultTreeModel;
    private DefaultMutableTreeNode root;

    private JButton btnSubmit;
    private final String SoftwareVersion = "v1.0.20170927";
    private final String SoftwareMailto = "chaofei.wu.hz@tcl.com";
    private CheckBoxTreeNode rootNode;

    private final String ResultKey = "[TCL*PATCH]";
    private String appRootDir = "";
    private String codeRootDir = "";
    private String scm_tools_dir = "";
    private String ALM_check_py = "ALM_check.py";
    private String ALM_tclpatch_py = "tclpatch.py";
    private String cmd_file_name = "";

    
    private String repo_fetch = "";
    private String repo_revision = "ALPS";
    private String repo_device = "";

    //private ArrayList<HashMap> gitlist = new ArrayList<>();GitProject
    private ArrayList<GitProject> gitlist = new ArrayList<>();
    //---------------------
    private JFileChooser chooser;

    //-----------------------
    private JToolBar toolbar = new JToolBar();
    private JButton openALpsBtn = new JButton("Open");
    private JButton addBtn = new JButton("Add");
    private JButton submitBtn = new JButton("Submit");

    private JScrollPane scrollPaneTree = new JScrollPane();
    private JTree tree = new JTree();
    private JScrollPane leftSp = new JScrollPane();
    private JScrollPane treeSp = new JScrollPane();
    private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSp, treeSp);

    private JPanel leftPane = new JPanel();

  //------copy UI code:
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBoxMtkPatch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton jbuttonNumber;
    private java.awt.Button jbuttonSmartTaskUrl;
    private javax.swing.JComboBox<String> jcomboBoxBug;
    private javax.swing.JComboBox<String> jcomboboxPatchNumber;
    private javax.swing.JComboBox<String> jcomboboxPatchType;
    private javax.swing.JComboBox<String> jcomboboxPatchVersion;
    private javax.swing.JComboBox<String> jcomboboxRoot;
    private javax.swing.JTextField jtextComment;
    private javax.swing.JSpinner jtextHours;
    private javax.swing.JTextField jtextModule;
    private javax.swing.JTextField jtextNumber;
    private javax.swing.JTextField jtextRootDetail;
    private javax.swing.JTextField jtextSolution;
    private javax.swing.JTextField jtextTest;

    //------
    private int THREAD_GET_STATUS = 0;
    private int THREAD_GIT_ADD = 1;
    private int THREAD_GIT_COMMIT_SUBMIT = 2;
    private String in_tclpatch_py_dir = "";

    
    private int isMtkPatch=0;
	private String AlmDint="";
	private String AlmTitle="";
	

    
    public Main(String _tclpatch_py_dir) {
        //ImageIcon icon=new ImageIcon("tclpatch/icon.png");
        //this.setIconImage(icon.getImage());
        
    	in_tclpatch_py_dir = _tclpatch_py_dir;

        setBounds(100, 200, 400, 800);

        JButton aboutBtn = new JButton("About");
        JButton exitBtn = new JButton(" Exit ");
        aboutBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String historyVersion = "Copyright (C) 2017 TCL Mobile\n\n";
                historyVersion += "v1.0.20170922    chaofei.wu.hz@tcl.com";
                JOptionPane.showMessageDialog(null, historyVersion + "\n\n", "Version History", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        exitBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JToolBar toolbar1 = new JToolBar();
        //toolbar1.add(aboutBtn);
        toolbar1.add(exitBtn);

        toolbar.add(aboutBtn);
        toolbar.add(openALpsBtn);

        toolbar.add(addBtn);
        toolbar.add(submitBtn);
        JPanel toolPane = new JPanel();
        chooser = new JFileChooser(appRootDir);
        openALpsBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //updateTree();
                chooser.setSelectedFile(new File(codeRootDir));
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(Main.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    //This is where a real application would open the file.
                    logd("Opening: " + chooser.getSelectedFile().getPath());
                    openFolder(chooser.getSelectedFile().getPath());
                }
            }
        });
        addBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                add();
            }
        });
        submitBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                commit_push();
            }
        });

        initLeftCompent();
        jCheckBoxMtkPatch.setEnabled(false);/////
        setMtkPatchCom();
        leftSp.add(leftPane);
        leftSp.setViewportView(leftPane);
        leftSp.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        scrollPaneTree.getViewport().add(tree, null);
        treeSp.add(tree);
        treeSp.setViewportView(tree);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSp, treeSp);
        splitPane.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        //getContentPane().add(splitPane, BorderLayout.CENTER);
        //getContentPane().add(toolbar, BorderLayout.NORTH);
        toolPane.setLayout(new BorderLayout(5, 5));
        toolPane.add(toolbar, BorderLayout.WEST);
        toolPane.add(toolbar1, BorderLayout.EAST);
        //toolPane.setBackground(Color.darkGray);

        this.add(toolPane, BorderLayout.NORTH);//this.add(panelTop, BorderLayout.SOUTH);
        this.add(splitPane, BorderLayout.CENTER);

        this.setSize(1000, 700);
        this.setLocationRelativeTo(null); // window center 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        rootNode = new CheckBoxTreeNode(repo_revision);
        defaultTreeModel = new DefaultTreeModel(rootNode);
        tree.addMouseListener(new CheckBoxTreeNodeSelectionListener());
        tree.setModel(defaultTreeModel);
        tree.setCellRenderer(new CheckBoxTreeCellRenderer());

        //JScrollPane scroll = new JScrollPane(tree);  
        //scroll.setBounds(0, 0, 300, 320);  
        //getContentPane().add(scroll);          
        //updateTree();
        //openALpsBtn.doClick();
        addBtn.setVisible(false);
        submitBtn.setVisible(false);
        
        String s=jcomboboxPatchType.getItemAt(jcomboboxPatchType.getSelectedIndex());
        logd("====>"+s);
        if (initVar() == 1) {
            //openFolder("/disk1/a3an1");
        }
    }

    private void openFolder(String folderName) {
        codeRootDir = folderName;//chooser.getSelectedFile().getPath();
        //
        //
        if (getRepoFile() == 1) {
            Thread t = new AfThread(THREAD_GET_STATUS);
            t.start();
            processing("Search...","", "Cancel");
        } else {
            /*repo_revision="ALPS";
        rootNode.removeAllChildren();     
        TreePath treePath = new TreePath(tree.getModel().getRoot());
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        node.setUserObject(repo_revision + " -- " + codeRootDir);        
        defaultTreeModel.reload(rootNode);
        gitlist.clear();
             */
        }
    }

    private void initLeftCompent() {

        jLabel1 = new javax.swing.JLabel();
        jtextNumber = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jtextModule = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jtextTest = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jtextSolution = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        jtextComment = new javax.swing.JTextField();
        jtextHours = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        jtextRootDetail = new javax.swing.JTextField();
        jcomboboxPatchType = new javax.swing.JComboBox<>();
        jcomboboxRoot = new javax.swing.JComboBox<>();
        jcomboBoxBug = new javax.swing.JComboBox<>();
        jCheckBoxMtkPatch = new javax.swing.JCheckBox();
        jbuttonNumber = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jcomboboxPatchVersion = new javax.swing.JComboBox<>();
        jcomboboxPatchNumber = new javax.swing.JComboBox<>();
        jbuttonSmartTaskUrl = new java.awt.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Bug number");

        jtextNumber.setText("");

        jLabel2.setText("Root cause");

        jLabel3.setText("Bug Category");

        jLabel4.setText("Module impact");

        jtextModule.setText("");

        jLabel5.setText("test suggestion");

        jtextTest.setText("");

        jLabel7.setText("solution");

        jtextSolution.setText("");

        jLabel9.setText("Action Hours");

        jCheckBox1.setText("Is change MenuTree?");

        jLabel6.setText("Comment");

        jtextComment.setText("");

        jLabel8.setText("Root cause detial");

        jtextRootDetail.setText("");

        jcomboboxPatchType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALPS", "MOLY", "SIXTH" }));
        jcomboboxPatchType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getVersionAndPnumber();
            }
        });

        jcomboboxRoot.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Design", "Coding", "Architecture", "Regression", "Evolution", "Unknown_Today", "Specification" }));

        jcomboBoxBug.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Platform", "Android", "TCT", "3rd Party" }));

        jCheckBoxMtkPatch.setForeground(new java.awt.Color(0, 0, 255));
        jCheckBoxMtkPatch.setText("Is MTK Patch?");
        jCheckBoxMtkPatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	setMtkPatchCom();
            	getVersionAndPnumber();
            }
        });

        jbuttonNumber.setText("...");
        jbuttonNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                 checkBugNumber();
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);
        jcomboboxPatchVersion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "v1.0.1", "v1.0.2"}));
        jcomboboxPatchNumber.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "P1", "P2"}));
        jbuttonSmartTaskUrl.setLabel("smarttask");
        jbuttonSmartTaskUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSmartUrl();
            }
        });
        GroupLayout layout = new GroupLayout(leftPane);
        leftPane.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtextComment, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jcomboboxRoot, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtextRootDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jcomboBoxBug, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtextModule, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtextTest, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtextSolution, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtextHours, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(112, 112, 112)
                                .addComponent(jCheckBox1)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jcomboboxPatchType, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jcomboboxPatchVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(jcomboboxPatchNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jCheckBoxMtkPatch, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jbuttonSmartTaskUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jtextNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbuttonNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(22, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtextNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbuttonNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBoxMtkPatch)
                    .addComponent(jbuttonSmartTaskUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcomboboxPatchNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcomboboxPatchType, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcomboboxPatchVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jtextComment, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcomboboxRoot, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtextRootDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcomboBoxBug, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtextModule, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtextTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtextSolution, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtextHours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox1)
                .addGap(29, 29, 29)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
        );

    }


    private void setMtkPatchCom(){
        jcomboboxPatchType.setEnabled(jCheckBoxMtkPatch.isSelected());
        jcomboboxPatchVersion.setEnabled(jCheckBoxMtkPatch.isSelected());
        jcomboboxPatchNumber.setEnabled(jCheckBoxMtkPatch.isSelected()); 
        if (gitlist.size()==0){
        	jcomboboxPatchVersion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { ""}));
        	jcomboboxPatchNumber.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { ""}));
        }
    }
    private String getScmtoolDirPathName(String dirpath){
    	
    	chooser.setSelectedFile(new File(dirpath));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        String dialogTitle="Open Scm_tools dir:";
        chooser.setDialogTitle(dialogTitle);
        int returnVal = chooser.showOpenDialog(Main.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            //This is where a real application would open the file.
            logd("Opening: " + chooser.getSelectedFile().getPath());
            return chooser.getSelectedFile().getPath();
        }else{
        	System.exit(1);
        }
        return "";
    }
    private boolean checkScmtools(String dirpath){
    	File f;
    	if (dirpath.length() > 0) {
    		logi("[checkScmtools] "+dirpath);
            f = new File(Paths.get(dirpath, ALM_check_py).toString());
            logd("Warnning: not found scm_tools dir: ["+dirpath+"]!");
            if (f.exists() && f.isFile()) {
                scm_tools_dir = Paths.get(dirpath, "..").toString();
                ALM_check_py=f.getAbsolutePath();
                return true;
            }
            f = new File(Paths.get(dirpath, "tools",ALM_check_py).toString());
            if (f.exists() && f.isFile()) {
                scm_tools_dir = dirpath;
                ALM_check_py=f.getAbsolutePath();
                return true;
            }            
        }    
    	logd("Warnning: not found scm_tools dir: ["+dirpath+"]!");
    	return false;
    }
    private String settingfile = ".tclpatch.tcl";
    private boolean readSettingFile(){    	
    	
    	boolean ret=false;
    	if (settingfile.length() > 0) {
    		 File f = new File(Paths.get(settingfile).toString());
             String data = "";
             if (f.isFile() && f.exists()) {
                 InputStreamReader read;
				try {
					read = new InputStreamReader(new FileInputStream(f), "utf-8");
					BufferedReader reader = new BufferedReader(read);
	                 String line;
	                 if ((line = reader.readLine()) != null) {
	                	 logi("[readSettingFile] scm_tools dir: "+line);
	                	 ret=checkScmtools(line);
	                 }
	                 read.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}                 
             }
        }
    	return ret;
    }    
    private boolean saveSettingFile(String dirpath){
    	File f;
    	try{
    	logd("== " + settingfile);
    	 OutputStreamWriter outFile = new OutputStreamWriter(new FileOutputStream(settingfile),"utf-8");
    	 outFile.write(dirpath + "\n");
    	 outFile.close();
    	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
    	}   
    	return false;
    }       
    private int initVar() {
        this.setTitle("TCL Patch Delivery" + " " + SoftwareVersion);
        appRootDir = System.getProperty("user.dir");
        logd("== " + appRootDir);
        //----
        boolean isFindScmTools = false;
        File f;
        
        if (in_tclpatch_py_dir.length()==0){
        	cmd_file_name = Paths.get(appRootDir, ALM_tclpatch_py).toString();
        	in_tclpatch_py_dir=appRootDir;
        	settingfile=Paths.get(appRootDir, settingfile).toString();
        }else{
        	settingfile=Paths.get(in_tclpatch_py_dir, settingfile).toString();
        }
        logd("settingfile="+settingfile);
        if (_DEBUG_EXT)	jTextArea1.append(debugLog+"\n");
        if (!checkScmtools(in_tclpatch_py_dir)){
        	if (!readSettingFile()){
        		if (!readSettingFile()){
        			String dirpath=appRootDir;
		        	while (!checkScmtools(dirpath)){
		        		dirpath=getScmtoolDirPathName(dirpath);
		        	}
	                saveSettingFile(scm_tools_dir);
            	}
        	}       	
        }
        if (!new File(cmd_file_name).exists()){
        	cmd_file_name= Paths.get(in_tclpatch_py_dir, ALM_tclpatch_py).toString();
        }      

        //for test:
        if (scm_tools_dir.equals("/wcf/tools/scm_tools")) {
            jtextNumber.setText("5361454");//5361454  ///5367854
        }
        jtextComment.setText("");
        jtextModule.setText("system");
        jtextTest.setText("none");
        jtextSolution.setText("fix bug");
        jtextRootDetail.setText("none");        
        //----
        /*
        InputStream in = Main.class.getClassLoader().getResourceAsStream("/tclpatch.py");
        DataOutputStream out;
        try {
            out = new DataOutputStream(new FileOutputStream(
                    cmd_file_name));
            byte[] buffer = new byte[4096];
            int count = 0;
            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
            out.close();
            in.close();
        }catch (Exception e) {
            e.printStackTrace();           
        }
         */
        //logd(is.toString());
        logw("->" + ALM_check_py);
        logw("-> " + cmd_file_name);
        f = new File(ALM_check_py);
        if (new File(cmd_file_name.replace(ALM_tclpatch_py,"_debug_ext")).exists()){
        	_DEBUG_EXT=true;
        }
        if (_DEBUG_EXT)	jTextArea1.append(debugLog+"\n");
        if (!f.exists()) {
            JOptionPane.showMessageDialog(null, "Please run in scm_tools/tools/\n\n Not found " + ALM_check_py + "!\n\n", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        f = new File(cmd_file_name);
        if (!f.exists() || f.length() < 3000) {
        	cmd_file_name = Paths.get(appRootDir, ALM_tclpatch_py).toString();
        	f = new File(cmd_file_name);
        }        
        if (!f.exists() || f.length() < 3000) {
            JOptionPane.showMessageDialog(null, "Please run in scm_tools/tools/\n\n Not found " + ALM_tclpatch_py + "!\n\n", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String cmd = "chmod 777 "+ cmd_file_name;
        String lines = cmdLinux(cmd);
        lines = runCmd("test");
        DataClass data = parseCmdOut(lines);
        if (data.result==1) {
            return 1;
        }else{
            JOptionPane.showMessageDialog(null, "Please run in scm_tools/tools/\n\n Not found " + data.comment + "!\n\n", "Error", JOptionPane.ERROR_MESSAGE);
            loge("Error: bugNumberCheck fail.");
            System.exit(0);
        }  
        return 0;
    }

//    private void initTree() {
//        rootNode = new CheckBoxTreeNode("root");
//        CheckBoxTreeNode node1 = new CheckBoxTreeNode("node_1");
//        CheckBoxTreeNode node1_1 = new CheckBoxTreeNode("node_1_1");
//        CheckBoxTreeNode node1_2 = new CheckBoxTreeNode("node_1_2");
//        CheckBoxTreeNode node1_3 = new CheckBoxTreeNode("node_1_3");
//        node1.add(node1_1);
//        node1.add(node1_2);
//        node1.add(node1_3);
//        CheckBoxTreeNode node2 = new CheckBoxTreeNode("node_2");
//        CheckBoxTreeNode node2_1 = new CheckBoxTreeNode("node_2_1");
//        CheckBoxTreeNode node2_2 = new CheckBoxTreeNode("node_2_2");
//        node2.add(node2_1);
//        node2.add(node2_2);
//        rootNode.add(node1);
//        rootNode.add(node2);
//    }

    public boolean setData(boolean isSelected, int nodeLevel, int subLevel, String gitpath, String filename) {
        for (int i = 0; i < gitlist.size(); i++) {
            GitProject m = gitlist.get(i);
            ArrayList<FileNode> sublist = m.getFileLine();
            for (int k = 0; k < sublist.size(); k++) {
                if ((i == nodeLevel) && (k == subLevel)) {
                    FileNode m1 = sublist.get(k);
                    String path = m.getPath();
                    String name = m1.getFileName();
                    logd(path + "," + name);
                    if (gitpath.equals(path) && filename.equals(name)) {
                        String txtlabel = "[" + m1.getType() + "] " + m1.getFileName();
                        logd(m1.getSelect() + m1.getFileName() + m1.getType());
                        m1.setSelect(isSelected);
                        return true;
                    } else {
                        loge("Error: have error.!!!");
                    }
                }

            }

        }
        return false;
    }

    private void add() {

        if (checkBugNumber() == 1) {
            int result = updateRecordFromTree();
            if (result < 0) {
                JOptionPane.showMessageDialog(null, "Have some errors, please open workspace again!\n", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (result == 0) {
                JOptionPane.showMessageDialog(null, "please select files!\n", "Warrning", JOptionPane.ERROR_MESSAGE);
            } else {
                AfThread t = new AfThread(THREAD_GIT_ADD);
                if (result > 500) {
                    t.start();
                    processing("Git add files...","", "Cancel");
                } else {
                    t.run();
                }

            }
        }
    }

    private int checkBugNumber() {
        String str = jtextNumber.getText().trim();
        if (str.length() <= 3) {
            JOptionPane.showMessageDialog(null, "Bug Number is error!\n" + str, "Error", JOptionPane.ERROR_MESSAGE);
            return 0;
        }
        int nubmer = 0;
        try {
            nubmer = Integer.valueOf(str);
        } catch (NumberFormatException ex) {
            logw("Error: bug number is not string.");
            return 0;
        }
        String cmd = "bugNumberCheck '" + ALM_check_py + "' '" + repo_revision + "' " + nubmer;
        if (jCheckBoxMtkPatch.isSelected()){
	    	cmd+=" "+jcomboboxPatchType.getItemAt(jcomboboxPatchType.getSelectedIndex());
	    	cmd+=" "+jcomboboxPatchVersion.getItemAt(jcomboboxPatchVersion.getSelectedIndex());
	    	cmd+=" "+jcomboboxPatchNumber.getItemAt(jcomboboxPatchNumber.getSelectedIndex());
        }
        String lines = runCmd(cmd);
        DataClass data = parseCmdOut(lines);
        if (data.result==1) {
        		isMtkPatch=(Integer)data.argJsonObject.get("is_mtk_patch");
        		AlmDint=data.argJsonObject.get("dint").toString();
        		AlmTitle=data.argJsonObject.get("title").toString();
        		if (isMtkPatch==1){
        			jCheckBoxMtkPatch.setSelected(true);
        		}
    			jtextComment.setText(AlmTitle);        		
                return 1;
        }else{
            String result_hint = data.comment;
            JOptionPane.showMessageDialog(null, "Bug Number is error!\n" + str+"\n\n"+result_hint+"\n\n", "Error", JOptionPane.ERROR_MESSAGE);
            loge("Error: bugNumberCheck fail.");
            return 0;
        }        
    }

    private DataClass parseCmdOut(String outString) {
        DataClass data = new DataClass(0);
        logd(outString.indexOf(ResultKey) + "," + outString);
        String str = "";
        JSONObject jsonObject = JSONObject.fromObject("{}");
        if (outString.indexOf(ResultKey) != -1) {//(!str.startsWith("Error:")){
            str = outString.substring(outString.indexOf(ResultKey) + ResultKey.length());
            if (str.length() > 2) {
                logd(str);
                data.comment=str;
                try {
                    jsonObject = JSONObject.fromObject(str);                    
                    data.result=(Integer)jsonObject.get("result");
                    if (data.result==1){
                        data.argJsonObject=jsonObject;
                    }else{
                     data.comment=jsonObject.get("comment").toString();   
                    }
                }catch (Exception e){
                    return data;
                }
                //JSONArray jsonArr = JSONArray.fromObject(str);
                //for (int k = 0; k < jsonArr.size(); k++) {
                //for (int k = 0; k < jsonArr.size(); k++) {
                //    JSONObject jo = jsonArr.getJSONObject(k);
                //    logd(jo.get("filename"));
                //}
            }
        }
        return data;
    }

    public int updateRecordFromTree() {
        int count = 0;
        Object root = tree.getModel().getRoot();
        TreePath treePath = new TreePath(root);
        Object object = treePath.getLastPathComponent();
        if (object == null) {
            return 0;
        }
        logd("submit=>");
        TreeModel model = tree.getModel();
        int n = model.getChildCount(object);
        for (int i = 0; i < n; i++) {
            Object child = model.getChild(object, i);
            TreePath path = treePath.pathByAddingChild(child);
            logd(i + ":" + path + ":" + path.getLastPathComponent().toString());
            //next level:
            if (path != null) {
                CheckBoxTreeNode node = (CheckBoxTreeNode) path.getLastPathComponent();
                if (node == null) {
                    return 0;
                }
                TreeModel m2 = tree.getModel();
                int nc = m2.getChildCount(child);
                for (int k = 0; k < nc; k++) {
                    Object child2 = m2.getChild(node, k);
                    TreePath path2 = path.pathByAddingChild(child2);
                    CheckBoxTreeNode node2 = (CheckBoxTreeNode) path2.getLastPathComponent();
                    String pathname = path.getLastPathComponent().toString();
                    String filename = path2.getLastPathComponent().toString().substring("[M] ".length());
                    logd("==" + node2.isSelected + k + ":" + pathname + ":" + filename);
                    if (!setData(node2.isSelected, i, k, pathname, filename)) {
                        logd("Error: " + node2.isSelected + k + ":" + pathname + ":" + filename);
                        return -1;
                    }
                    if (node2.isSelected) {
                        count++;
                    }
                }
            }
        }
        //if (treePath != null) {
        //    tree.setSelectionPath(treePath);
        //    tree.scrollPathToVisible(treePath);
        //}
        logd("---update data----");
        for (int i = 0; i < gitlist.size(); i++) {
            GitProject m = gitlist.get(i);
            ArrayList<FileNode> sublist = m.getFileLine();
            logd(i + ": " + m.getPath());

            for (int k = 0; k < sublist.size(); k++) {
                String path = m.getPath();
                FileNode m1 = sublist.get(k);
                String name = m1.getFileName();
                if (m1.getSelect()) {
                    logd(String.valueOf(m1.getSelect() ? 1 : 0) + " " + m1.getType() + " " + name);
                }
            }
        }

        return count;
    }

    private Dialog dialog= new Dialog(this, "Doing...", true);
    private JButton dialog_btn = new JButton();
    private JLabel dialog_hint = new JLabel();
    private JProgressBar dialog_progressBar = new JProgressBar();        
        
        
    private boolean processing(String title,String msg, String labelBtn) {
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                logd("=== close .");
                dialog.setVisible(false);
            }
        });
        dialog.setTitle(title);
        dialog_hint.setText(msg);
        dialog_btn.setText(labelBtn);
        GroupLayout layout = new GroupLayout(dialog);
        dialog.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(143, 143, 143)
                                        .addComponent(dialog_btn))
                                .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(dialog_progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(19, 150, 150)
                                        .addComponent(dialog_hint)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap(49, Short.MAX_VALUE)
                        .addComponent(dialog_hint)
                        .addGap(18, 18, 18)
                        .addComponent(dialog_progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(dialog_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
        );

        dialog_hint.getAccessibleContext().setAccessibleName("dialog_hint");
        dialog_btn.getAccessibleContext().setAccessibleName("dialog_OK");

        dialog.setBounds(400, 200, 369, 165);
        dialog.setVisible(true);
        /*
        
       progressbar = new JProgressBar();
       progressbar.setOrientation(JProgressBar.HORIZONTAL);
       progressbar.setMinimum(0);
       progressbar.setMaximum(100);
       progressbar.setValue(0);
       progressbar.setStringPainted(true);
       progressbar.setPreferredSize(new Dimension(300, 20));
       
        
        dialog = new Dialog(this, "Doing...", true);
        dialog.setBounds(400, 200, 350, 150);
        dialog.setLayout(new FlowLayout());
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                logd("=== close .");
                dialog.setVisible(false);
            }
        });
        JLabel hint=new JLabel("---");
        dialog.add(hint);
        dialog.add(progressbar);
        dialog.setVisible(true);
         */

        return false;
    }

    private boolean submit_add() throws IOException {
        for (int i = 0; i < gitlist.size(); i++) {
            GitProject m = gitlist.get(i);
            m.setUpdate(false);
            boolean isUpdate = false;
            ArrayList<FileNode> sublist = m.getFileLine();
            for (int k = 0; k < sublist.size(); k++) {
                FileNode m1 = sublist.get(k);
                boolean isSelected = m1.getSelect();
                if (isSelected) {
                    logd(m1.getFileName());
                    String longpath = Paths.get(codeRootDir, m.getPath()).toString();
                    String filename = m1.getFileName();
                    logd(m1.getFileName());
                    String opertor = "gitAdd";
                    if (m1.getType().equals("D")) {
                        opertor = "gitRm";
                    }
                    String cmd = opertor + " " + longpath + " '" + filename + "'";//cmd1+" & "+cmd2                
                    String lines = runCmd(cmd);
                    logd(lines);
                    isUpdate = true;
                }

            }
            if (isUpdate) {
                m.setUpdate(true);
            }
        }
        submitBtn.setVisible(true);

        if ((jtextComment.getText().trim().length() == 0)
                && (jtextRootDetail.getText().trim().length() == 0)
                && (jtextRootDetail.getText().trim().length() == 0)
                && (jtextModule.getText().trim().length() == 0)
                && (jtextTest.getText().trim().length() == 0)
                && (jtextSolution.getText().trim().length() == 0)) {
            jtextComment.setText("mtk patch P1");
            jtextModule.setText("mtk-patch");
            jtextTest.setText("mtk-patch");
            jtextSolution.setText("mtk-patch");
            jtextRootDetail.setText("mtk-patch");
        }
        return true;
    }

    private void commit_push() {
        String comments = getComments();
        if (comments.length() == 0) {
            JOptionPane.showMessageDialog(null, "\nThe input box for typing * cannot be empty\n\n", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (jCheckBoxMtkPatch.isSelected()){
	    	if (jcomboboxPatchType.getItemAt(jcomboboxPatchType.getSelectedIndex()).trim().length()==0){
	            JOptionPane.showMessageDialog(null, "\nPlease select MTK Patch Type\n\n", "Error", JOptionPane.ERROR_MESSAGE);
	            return;
	    	}
	    	if (jcomboboxPatchVersion.getItemAt(jcomboboxPatchVersion.getSelectedIndex()).trim().length()==0){
	            JOptionPane.showMessageDialog(null, "\nPlease select MTK Patch Version\n\n", "Error", JOptionPane.ERROR_MESSAGE);
	            return;
	    	}
	    	if (jcomboboxPatchNumber.getItemAt(jcomboboxPatchNumber.getSelectedIndex()).trim().length()==0){
	            JOptionPane.showMessageDialog(null, "\nPlease select MTK Patch P?\n\n", "Error", JOptionPane.ERROR_MESSAGE);
	            return;
	    	}
    	}
        if (gitlist.size() > 0) {
            AfThread t = new AfThread(THREAD_GIT_COMMIT_SUBMIT);
            t.start();
            processing("Commit and push to gerrit...","git push ...", "Cancel");
        }

    }

    private String getComments() {

        String str = "";
        //String mentree = jCheckBox1.isSelected() ? "y" : "n";

        if (jtextNumber.getText().trim().length() == 0) {
            return "";
        }
        if (jcomboboxRoot.getSelectedItem().toString().length() == 0) {
            return "";
        }
        if (jtextRootDetail.getText().trim().length() == 0) {
            return "";
        }
        if (jcomboBoxBug.getSelectedItem().toString().length() == 0) {
            return "";
        }
        if (jtextModule.getText().trim().length() == 0) {
            return "";
        }
        if (jtextTest.getText().trim().length() == 0) {
            return "";
        }

        if (jtextSolution.getText().trim().length() == 0) {
            return "";
        }

        if (true) {
            //str+="###%%%comment:"+jTextField0.getText().trim()+"\n";
            str += "###%%%comment:" + jtextComment.getText().trim();
            str += "###%%%bug number:" + jtextNumber.getText().trim();
            str += "###%%%product name:" + repo_revision;
            str += "###%%%root cause:" + jcomboboxRoot.getSelectedItem().toString();
            str += "###%%%root cause detail:" + jtextRootDetail.getText().trim();
            str += "###%%%Bug category:" + jcomboBoxBug.getSelectedItem().toString();
            str+="###%%%Generated by:"+"SW Code";
            str += "###%%%Module_Impact:" + jtextModule.getText().trim();
            //str+="###%%%Change Menutree or image:"+mentree;
            str += "###%%%Test_Suggestion:" + jtextTest.getText().trim();
            str += "###%%%Solution:" + jtextSolution.getText().trim();
            str += "###%%%Test_Report:" +"none";
            str += "###%%%Actual Hours:" + jtextHours.getValue();
            //str+="###%%%Bug Reason:"+jTextField8.getText().trim();               
        } else {
            //str+="###%%%comment:"+jTextField0.getText().trim()+"\n";
            str += "###%%%comment:" + "Design";
            str += "###%%%bug number:" + jtextNumber.getText().trim();
            str += "###%%%product name:" + repo_revision;
            str += "###%%%root cause:" + "Platform";
            str += "###%%%root cause detail:" + "Platform";
            str += "###%%%Bug category:" + "Platform";
            //str+="###%%%Generated by:"+"Platform";
            str += "###%%%Module Impact:" + "Platform";
            //str+="###%%%Change Menutree or image:"+mentree;
            str += "###%%%Test Suggestion:" + "mtk-patch";
            str += "###%%%Solution:" + "mtk-patch";
            str += "###%%%Actual Hours:" + jtextHours.getValue();
        }

        return str;
    }

    private String pushUrls[] = new String[500]; 
    private void git_commit_and_push() {
    	for (int i = 0; i < pushUrls.length; i++) {
    		pushUrls[i]="";
    	}
    	String patchtype="";
    	String patchversion="";
    	String patchnumber="";
    	if (jCheckBoxMtkPatch.isSelected()){
	    	if (jcomboboxPatchType.getItemAt(jcomboboxPatchType.getSelectedIndex()).trim().length()>0)
	    		patchtype=jcomboboxPatchType.getItemAt(jcomboboxPatchType.getSelectedIndex()).trim();
	    	if (jcomboboxPatchVersion.getItemAt(jcomboboxPatchVersion.getSelectedIndex()).trim().length()>0)
	    		patchversion=jcomboboxPatchVersion.getItemAt(jcomboboxPatchVersion.getSelectedIndex()).trim();
	    	if (jcomboboxPatchNumber.getItemAt(jcomboboxPatchNumber.getSelectedIndex()).trim().length()>0)
	    		patchnumber=jcomboboxPatchNumber.getItemAt(jcomboboxPatchNumber.getSelectedIndex()).trim();
    	}
        String comments = getComments();
        jTextArea1.append("-----push-->"+jtextNumber.getText().trim()+","+repo_revision+"\n");
        for (int i = 0; i < gitlist.size(); i++) {
            GitProject m = gitlist.get(i);
            logd(i + ":" + m.getUpdate() + " " + m.getPath());
            boolean isModify = m.getUpdate();
            sendMsg(THREAD_GIT_COMMIT_SUBMIT, i * 100 / gitlist.size(), m.getPath());
            if (isModify) {
                String longpath = Paths.get(codeRootDir, m.getPath()).toString();
                String name = m.getName();
                comments = comments.replaceAll("&nbsp;", "\\&nbsp;");
                comments = comments.replaceAll(" ", "&nbsp;");
                logd(comments);
                String cmd = "gitSubmitAndPush " + longpath + " " + name + " " + repo_revision + " \"" + comments + "\"";//cmd1+" & "+cmd2
                if (jCheckBoxMtkPatch.isSelected()){
	            	cmd+=" "+patchtype;
	            	cmd+=" "+patchversion;
	            	cmd+=" "+patchnumber;           	
                }
            	
                String lines = runCmd(cmd);
                DataClass data = parseCmdOut(lines);
                if (data.result==1) {
                	JSONObject jobj=data.argJsonObject;
                	pushUrls[i]=jobj.get("pushurl").toString();                	
                }else{                    
                    loge("Error: push fail! "+data.comment+"," +m.getPath());
                }  
                jTextArea1.append(pushUrls[i]+","+"\n");
            }
            loge("push URL:"+pushUrls[i]);
        }
        jTextArea1.append("-----push done-------\n\n");
    }

    private void openSmartUrl(){
    	//http://10.92.35.20/SmartTask/index.php?m=mtkpatch&mtkstatus=0&mergepatch=mickey6t-n-v1.0-fsr&filter_mergepatch=mickey6t-n-v1.0-fsr
    	String strSmartUrl="http://10.92.35.20/SmartTask/index.php?m=mtkpatch&mtkstatus=0&mergepatch="+repo_revision+"&filter_mergepatch="+repo_revision;    	
    	runCmdNoback("openSmartTaskUrl "+strSmartUrl+" &");  
    	jbuttonSmartTaskUrl.setFocusable(false);
    }
    private int getVersionAndPnumber(){
    	jcomboboxPatchVersion.removeAllItems();
    	jcomboboxPatchNumber.removeAllItems();
    	String cmd = "getVersionAndPnumber " + repo_revision +" "+ jcomboboxPatchType.getItemAt(jcomboboxPatchType.getSelectedIndex());
        String lines = runCmd(cmd);
        DataClass data = parseCmdOut(lines);
        if (data.result==1) {
        	JSONObject jobj=data.argJsonObject;
        	JSONArray jsonArr=jobj.getJSONArray("vnum");
        	//JSONArray jsonArr = JSONArray.fromObject(jobj.get("vnum").toString());
            for (int k = 0; k < jsonArr.size(); k++) {
                jcomboboxPatchVersion.addItem((String)jsonArr.get(k));
            }   
            jsonArr=jobj.getJSONArray("pnum");            
            //jsonArr = JSONArray.fromObject(jobj.get("pnum").toString());
            for (int k = 0; k < jsonArr.size(); k++) {              
                jcomboboxPatchNumber.addItem((String)jsonArr.get(k));
            }
            logd("import_name : "+(String)jobj.get("import_name"));
        }else{                    
            //loge("Error: push fail! "+data.comment+"," +m.getPath());
        }  
        
        return 1;
    }


    private String doFinish(){
    	
    	String cmd = " finish  " + repo_revision +" "+jtextNumber.getText()+" ";
    	if (jCheckBoxMtkPatch.isSelected()){
	    	cmd+=" "+jcomboboxPatchType.getItemAt(jcomboboxPatchType.getSelectedIndex());
	    	cmd+=" "+jcomboboxPatchVersion.getItemAt(jcomboboxPatchVersion.getSelectedIndex());
	    	cmd+=" "+jcomboboxPatchNumber.getItemAt(jcomboboxPatchNumber.getSelectedIndex());
    	}
    	cmd+=" ";
    	for (int i = 0; i < pushUrls.length; i++) {
    		if (pushUrls[i].length()>0)
    			cmd+=pushUrls[i]+",";
    	}
        String lines = runCmd(cmd);
        DataClass data = parseCmdOut(lines);
        if (data.result==1) {
            logd("import_name : "+data.comment);
            return "";
        }else{                    
            return (data.comment);
        }          
        
    }
    
    private void runThread(int run_type) {
        AfThread a = new AfThread(run_type);
        a.run();
    }

    class AfThread extends Thread {

        private int run_type;

        public AfThread(int type) {
            run_type = type;
        }

        public void run() {
            if (run_type == THREAD_GET_STATUS) {
                try {
                    getRepoGitStatus();
                    updateTree();
                    logd("getGitStatusThread===>Done");
                    jCheckBoxMtkPatch.setEnabled(true);
                    jbuttonSmartTaskUrl.setLabel("SmartTask "+repo_revision);
                    dialog.setVisible(false);
                } catch (Exception e) {
                    logd("Error:");
                }
            } else if (run_type == THREAD_GIT_ADD) {

                try {
                    if (submit_add()) {
                       
                        /*
                        int res = JOptionPane.showConfirmDialog(null, "Submit", "need submit?", JOptionPane.YES_NO_OPTION);
                        if (res == JOptionPane.YES_OPTION) {
                            logd("Yes");
                        } else {
                            logd("No");
                        }*/
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } finally {

                }
            } else if (run_type == THREAD_GIT_COMMIT_SUBMIT) {
                git_commit_and_push();               
                String comment=doFinish();
                dialog.setVisible(false);
                if (comment.length()>0){
                	JOptionPane.showMessageDialog(null, comment + "\n\n", "Error!", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                logd("Error: null thread.");
            }
        }

    }

    public void updateTree() {

        rootNode.removeAllChildren();
        for (int i = 0; i < gitlist.size(); i++) {
            GitProject m = gitlist.get(i);
            if (m.getFind()) {
                String path = m.getPath();
                CheckBoxTreeNode node1 = new CheckBoxTreeNode(path);
                ArrayList<FileNode> sublist = m.getFileLine();
                for (int k = 0; k < sublist.size(); k++) {
                    FileNode m1 = sublist.get(k);
                    String txtlabel = "[" + m1.getType() + "] " + m1.getFileName();
                    logd(m1.getFileName());
                    logd(m1.getType());
                    logd(m1.getSelect()?"True":"False");
                    CheckBoxTreeNode node0 = new CheckBoxTreeNode(txtlabel);
                    node1.add(node0);
                }
                rootNode.add(node1);
            }
        }
        TreePath treePath = new TreePath(tree.getModel().getRoot());
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        node.setUserObject(repo_revision + " -- " + codeRootDir);
        //tree.updateUI();
        defaultTreeModel.reload(rootNode);
        //tree.setModel(defaultTreeModel);  
        logd("====>");
        for (int i = 0; i < gitlist.size(); i++) {
            GitProject m = gitlist.get(i);
            logd(i + ",");
            if (m.getFind()) {
                ArrayList<FileNode> sublist = m.getFileLine();
                for (int k = 0; k < sublist.size(); k++) {
                    FileNode m1 = sublist.get(k);
                    String path = m.getPath();
                    String name = m1.getFileName();
                    logd(i + "," + k + ":" + path + "->" + name);
                }
            }
        }
        logd("===update-===");

        addBtn.setVisible(gitlist.size() > 0);
        submitBtn.setVisible(false);
    }

    private void updateProcessing(int value) {
        if (dialog != null && dialog.isVisible()) {
            if (value >= 0 && value <= 100) {
                logd("===updateProcessing-===" + value);
                dialog_progressBar.setValue(value);
            }
        }
    }

    //---message
    private Message gMessage = new Message();

    private void sendMsg(int what, int percent, String text) {

        gMessage.what = what;
        gMessage.percent = percent;
        gMessage.argStr1 = text;
        new Thread(new Runnable() {
            public void run() {
                //Message msg = new Message();
                //msg.what = gMessage.what;
                //msg.percent = gMessage.percent;
                //msg.argStr1= gMessage.argStr1;
                //logd("thread " + Thread.currentThread().getName()+ ": send message--" + Integer.valueOf(count));
                //handler.sendMessage(msg);
                dialog_progressBar.setValue(gMessage.percent);
                dialog_hint.setText(gMessage.argStr1);
            }
        }).start();
    }

    //---message
    private int getRepoFile() {
        logd("!!!cmd_file_name=" + cmd_file_name);
        String repo_manifest_xml = codeRootDir + "/.repo/manifest.xml";
        File f = new File(repo_manifest_xml);
        if (!f.exists()) {
            System.out.print("\033[31m\033[05m" + "Error: " + "\033[0m" + "\033[1;31;40m" + repo_manifest_xml + "\033[0m" + " not found!");
            return 0;
        }
        gitlist.clear();
        getXml(repo_manifest_xml);
        logd("!!!cmd_file_name=" + cmd_file_name);
        //cmd_file_name = scm_tools_dir + "/test.py";
        //String cmd1 = "test cd \"/wcf/a3ap1/device/mediateksample/k37mv1_64\"";
        //String cmd2 = "git remote -v | tail -1 | awk -F' ' '{print $2}' | sed -e 's/.*://' -e 's/.git//' -e 's/\\//.?/g'";
        //String cmd = "" + cmd_file_name + " 1 '" + codeRootDir + "'";//cmd1+" & "+cmd2
        //String cmd=cmd1+" & "+cmd2;
        //String lines = runCmd(cmd);
        logd("========gitlist.size()=" + gitlist.size());
        if (gitlist.size() > 0) {
            TreePath treePath = new TreePath(tree.getModel().getRoot());
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            node.setUserObject(repo_revision + " -- " + codeRootDir);
            //tree.updateUI();
            defaultTreeModel.reload(rootNode);            
            return 1;
        } else {
            return 0;
        }
    }

    private int getRepoGitStatus() {

        for (int i = 0; i < gitlist.size(); i++) {
            boolean find = false;
            GitProject m = gitlist.get(i);
            String path = m.getPath();
            //updateProcessing(i * 100 / gitlist.size());
            sendMsg(THREAD_GET_STATUS, i * 100 / gitlist.size(), path);
            if (!(path.indexOf("device") == 0 || path.indexOf("packages/apps/B") == 0)) {
                //continue;
            }
            //logd(path);
            File dir=new File(Paths.get(codeRootDir, path).toString());
            if (!(dir.exists() && dir.isDirectory())){
            	//logd("dir.exists="+dir.getAbsolutePath());
            	continue;
            }
            String str = runCmd("gitStatus " + Paths.get(codeRootDir, path).toString());
            //logd(str.indexOf(ResultKey) + "," + str);
            m.setFind(false);
            ArrayList<FileNode> sublist = new ArrayList<>();
            m.setFileLine(sublist);
            if (str.indexOf(ResultKey) != -1) {//(!str.startsWith("Error:")){
                //logd("=++");
                str = str.substring(str.indexOf(ResultKey) + ResultKey.length());
                //logd(str);
                //JSONObject jsonObject = JSONObject.fromObject("[{'status': 'M', 'flag': 0, 'ch': 'a', 'ok': 0, 'path': '/wcf/a3ap1/device/mediateksample/k37mv1_64', '*': ' ', 'id': 1, 'filename': 'ProjectConfig.mk'}, {'status': 'M', 'flag': 0, 'ch': 'a', 'ok': 0, 'path': '/wcf/a3ap1/device/mediateksample/k37mv1_64', '*': ' ', 'id': 2, 'filename': 'init.project.rc'}]");
                JSONArray jsonArr = JSONArray.fromObject(str);
                for (int k = 0; k < jsonArr.size(); k++) {
                    JSONObject jo = jsonArr.getJSONObject(k);
                    logd((String)jo.get("filename"));
                    FileNode fleNode = new FileNode((String) jo.get("filename"));
                    fleNode.setType((String) jo.get("status"));
                    fleNode.setSelect(false);
                    sublist.add(fleNode);
                    m.setFileLine(sublist);
                }
                if (jsonArr.size() > 0) {
                    m.setFind(true);
                    find = true;
                    logd("=" + i);
                }
            }
        }
        //logd("gitlist.size()=" + gitlist.size());

        for (int i = 0; i < gitlist.size(); i++) {
            GitProject m = gitlist.get(i);
            ArrayList<FileNode> sublist = m.getFileLine();
            if (sublist == null || sublist.size() == 0) {//(!((boolean)m.get("find"))){
                //logd("--" + i);
                gitlist.remove(i);
                i--;
            } else {
                //logd("" + i);
            }
        }
        for (int i = 0; i < gitlist.size(); i++) {
            GitProject m = gitlist.get(i);
            logd(i + ":" + m.getPath());
            ArrayList<FileNode> fleNode = m.getFileLine();
            if (fleNode == null) {
                continue;
            }
            Collections.sort(fleNode, new Comparator<FileNode>() {
                public int compare(FileNode o1, FileNode o2) {
                    return o1.getFileName().compareTo(o2.getFileName());
                }
            });
            int k = 0;
            for (Iterator iter = fleNode.iterator(); iter.hasNext();) {
                FileNode item = (FileNode) iter.next();
                //logd(" -- " + k + ":" + item.getType() + item.getFileName());
                k++;
            }
        }
        logd("gitlist.size()=" + gitlist.size());
        return 1;

    }

    private void getXml(String filename) {

        try {
            File f = new File(filename);
            System.out.print(filename + "\n");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(f);
            Element root = doc.getDocumentElement();
            NodeList nlist = root.getChildNodes();
            //remote fetch="git@10.92.32.10:" name="jgs" review="http://10.92.32.10:8081"/>
            //<default remote="jgs" revision="mtk6750-o-v1.0-dint" sync-j="4"/>
            //<project name="mtk6750t/modem/LWTG" path="modem/LWTG"/>
            //<project name="mtk6750t/modem/C2K" path="modem/C2K"/>
            if (nlist != null) {
                for (int i = 0; i < nlist.getLength(); i++) {
                    Node rnode = nlist.item(i);
                    if (rnode.getNodeType() == Node.ELEMENT_NODE) {
                        //logd(i + ":" + rnode.getNodeName() + ":" + rnode.getNodeValue());

                        if (rnode.getNodeName().equals("remote")) {
                            Element element = (Element) rnode;
                            if (element.hasAttributes()) {
                                NamedNodeMap namenm = element.getAttributes();//Node  
                                for (int k = 0; k < namenm.getLength(); k++) {
                                    Attr attr = (Attr) namenm.item(k);
                                    //logd("name:" + attr.getNodeName() + " value:" + attr.getNodeValue() + "  type:" + attr.getNodeType());
                                    if (attr.getNodeName().equals("fetch")) {
                                        repo_fetch = attr.getNodeValue();
                                    }
                                }
                            }
                        } else if (rnode.getNodeName().equals("default")) {
                            Element element = (Element) rnode;
                            if (element.hasAttributes()) {
                                NamedNodeMap namenm = element.getAttributes();//Node  
                                for (int k = 0; k < namenm.getLength(); k++) {
                                    Attr attr = (Attr) namenm.item(k);
                                    //logd("name:" + attr.getNodeName() + " value:" + attr.getNodeValue() + "  type:" + attr.getNodeType());
                                    if (attr.getNodeName().equals("revision")) {
                                        repo_revision = attr.getNodeValue();
                                    }
                                }
                            }
                        } else if (rnode.getNodeName().equals("project")) {
                            Element element = (Element) rnode;
                            if (element.hasAttributes()) {
                                NamedNodeMap namenm = element.getAttributes();//Node  
                                String repo_name = "";
                                String repo_path = "";
                                for (int k = 0; k < namenm.getLength(); k++) {
                                    Attr attr = (Attr) namenm.item(k);
                                    //logd("name:" + attr.getNodeName() + " value:" + attr.getNodeValue() + "  type:" + attr.getNodeType());
                                    if (attr.getNodeName().equals("name")) {
                                        repo_name = attr.getNodeValue();
                                    } else if (attr.getNodeName().equals("path")) {
                                        repo_path = attr.getNodeValue();
                                    }
                                }
                                //遍历map中的键  
                                if (repo_name.length() > 0 && repo_path.length() > 0) {
                                    //HashMap map = new HashMap();
                                    //map.put("name", repo_name);
                                    //map.put("path", repo_path);
                                    //map.put("find", false);                                   
                                    GitProject map = new GitProject(repo_path, repo_name, false);
                                    gitlist.add(map);
                                }

                            }
                        }
                    }
                    if (i > 5) {
                        //break;
                    }
                }
            }
            logd("repo_fetch:" + repo_fetch + "\n");
            logd("repo_revision:" + repo_revision + "\n");
            logd("repo_device:" + repo_device + "\n");
            //sort:
            Collections.sort(gitlist, new Comparator<GitProject>() {
                public int compare(GitProject o1, GitProject o2) {
                    return o1.getPath().compareTo(o2.getPath());
                }
            });
            for (int i = 0; i < gitlist.size(); i++) {
                GitProject m = gitlist.get(i);
                //logd(i + ":" + m.getPath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String runCmdNoback(String strCmd){
    	String outline = "";
        logw("[" + strCmd + "]");
        try {
            Process process = Runtime.getRuntime().exec(cmd_file_name+" "+strCmd);
            return "";
        } catch (Exception e) {
            loge(e.getMessage().toLowerCase());
            return "";
        }
    }
    private String runCmd(String strCmd){
    	return cmdLinux(cmd_file_name+" "+strCmd);
    }
    private String cmdLinux(String strCmd) {
        //strCmd = "chdir"; 
        String outline = "";
        logw("[" + strCmd + "]");
        try {
            Process process = Runtime.getRuntime().exec(strCmd);
            BufferedReader strCon = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = strCon.readLine()) != null) {
                logw("CMDLOG: " + line);
                outline += line;
            }
            //logd("CMDLOG: "+outline);
            return outline;
        } catch (Exception e) {
            loge(e.getMessage().toLowerCase());
            return "";
        }
    }

    private String cmdLinux(String strCmd, String[] a, String dir) throws IOException {
        //strCmd = "chdir"; 
        logw("[" + strCmd + "]");
        Process process = Runtime.getRuntime().exec(strCmd);
        BufferedReader strCon = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        String outline = "";
        while ((line = strCon.readLine()) != null) {
            //logd(line);
            outline += line;
        }
        return outline;
    }

	private String debugLog="";
    private void logd(String text){
    	if (_DEBUG_EXT) debugLog+=text+"\n";
        System.out.println(text);
    }
    private void logi(String text){
    	if (_DEBUG_EXT) debugLog+=text+"\n";
        System.out.println(text);
    }        
    private void logd(String text, int hide){
        System.out.println(text);
    }    
    private void logw(String text){
    	if (_DEBUG_EXT) debugLog+=text+"\n";
        System.out.println(text);
    }    
    private void loge(String text){
    	if (_DEBUG_EXT) debugLog+=text+"\n";
        System.err.println(text);
    }    
    private void test() {
        
    }

    public void run() {

    }

    public static void main(String[] args) {
        String scm_tools_dir = "";
        String tclpatch_tools_dir="";
        System.out.println("args.length="+args.length);
        
        if (args.length >0) {
            System.out.println(args[0]);
            tclpatch_tools_dir = args[0];
        }
        new Main(tclpatch_tools_dir);
    }
}
        		