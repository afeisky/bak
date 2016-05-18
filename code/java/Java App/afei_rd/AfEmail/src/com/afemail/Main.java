/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package com.afemail;

import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Main extends JPanel
implements ActionListener,TreeSelectionListener,ListSelectionListener{
    protected JTextArea textArea;
    protected String newline = "\n";
	private JTree tree;
	private boolean playWithLineStyle;
	private Object lineStyle;
	private boolean DEBUG=true;
	private Object helpURL;
	private JEditorPane htmlPane;
	private JList list;
    private String[] imageNames = { "Bird", "Cat", "Dog", "Rabbit", "Pig", "dukeWaveRed",
            "kathyCosmo", "lainesTongue", "left", "middle", "right", "stickerface"};
	private JSplitPane splitPaneList;    
	private DefaultMutableTreeNode rootTree;
	private DefaultListModel listModel;
	private TreeInfo currTreeNode;
	private JPanel topPanel;
	private JButton writeBtn;
	private JButton rcvBtn;
	private JButton openBtn;
	private JButton sendBtn;
	private String appDir;
    public Main() {
        super(new BorderLayout());
    	try {  //windows_style
            UIManager.setLookAndFeel(//关键句1  
            UIManager.getSystemLookAndFeelClassName());//关键句2  
        } catch (Exception e) {  
            //e.printStackTrace();  
        }   
    	File directory = new File("");//参数为空
    	appDir = null;
		try {
			appDir = directory.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("appDir="+appDir); 
    	
        //Create the toolbar.
        JToolBar toolBarTop = new JToolBar("toobarTop");
        //addButtons(toolBarTop);

        openBtn = new JButton(" Open");
        openBtn.addActionListener(this);
        
        writeBtn = new JButton(" Write ");
        writeBtn.addActionListener(this);//saveBtn.addActionListener(new SaveListener());
        
        rcvBtn = new JButton(" Receive");
        rcvBtn.addActionListener(this);
    
        sendBtn = new JButton(" Receive");
        sendBtn.addActionListener(this);
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(3,10,3,10));
        topPanel.add(openBtn);
        topPanel.add(writeBtn);
        topPanel.add(rcvBtn);
        topPanel.add(sendBtn);
        
        //create 
        JPanel statusPanel2 = new JPanel();

		// Add the scroll panes to a split pane.
		JSplitPane splitPaneTree;

		// Create the HTML viewing pane.
		htmlPane = new JEditorPane();
		htmlPane.setEditable(true);
		//initHelp();
		JScrollPane htmlView = new JScrollPane(htmlPane);
		//---
		//---------
		JPanel seachPanel = new JPanel();

                       
		list = new JList(CreateListModel());
	    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    list.setSelectedIndex(0);
	    list.addListSelectionListener(this);
	    JScrollPane ListView = new JScrollPane(list);
        splitPaneList  = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,ListView, htmlView);
        splitPaneList.setOneTouchExpandable(true);
        splitPaneList.setDividerLocation(150);
        Dimension minimumSizeList = new Dimension(100, 50);
		ListView.setMinimumSize(minimumSizeList);
		htmlView.setMinimumSize(minimumSizeList);
		splitPaneList.setPreferredSize(new Dimension(400, 200));
           
        //----
		JPanel splitPaneTreeRight = new JPanel();	
		splitPaneTreeRight.setLayout(new BorderLayout());//
        splitPaneTreeRight.add(seachPanel, BorderLayout.PAGE_START);
        splitPaneTreeRight.add(splitPaneList, BorderLayout.CENTER);
			
        //tree panel--------------------------------
        
		DefaultMutableTreeNode rootTree=TreeCreateNodes(appDir);//"G:\\mail"

		// Create a tree that allows one selection at a time.
		tree = new JTree(rootTree);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		if (playWithLineStyle) {
			System.out.println("line style = " + lineStyle);
			tree.putClientProperty("JTree.lineStyle", lineStyle);
		}

		// Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(tree);
		
		splitPaneTree = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,treeView,splitPaneTreeRight);
		splitPaneTree.setOneTouchExpandable(true);
		splitPaneTree.setDividerLocation(150);
		
		//Provide minimum sizes for the two components in the split pane.
		Dimension minimumSizeTree = new Dimension(100, 50);
		treeView.setMinimumSize(minimumSizeTree);
		splitPaneTreeRight.setMinimumSize(minimumSizeTree);
		
		//Provide a preferred size for the split pane.
		splitPaneTree.setPreferredSize(new Dimension(400, 200));
	

		//--------------------------------------------
        JPanel statusPanel = new JPanel();
        //Lay out the main panel.
        //setPreferredSize(new Dimension(450, 130));    
        
        add(topPanel, BorderLayout.PAGE_START);//add(toolBarTop, BorderLayout.PAGE_START);
        add(splitPaneTree, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.PAGE_END);
    }
    
    @Override
	public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        String description = null;
        System.out.println("cmd="+cmd);
        if (e.getSource()==writeBtn){
        	System.out.println("save button");
        	JFrame frame = new JFrame("Write");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		//int width=Toolkit.getDefaultToolkit().getScreenSize().width;
    		//int height=Toolkit.getDefaultToolkit().getScreenSize().height;
    		///System.out.println("screen size( W " +width+", H "+height+")");
    		frame.setPreferredSize(new Dimension(800, 600));
    		frame.setSize(new Dimension(800, 600));
            //frame.setLocation(width/2-frame.getWidth()/2,height/2-frame.getWidth()/2);
    		frame.setLocationRelativeTo(null); 
            frame.add(new EmlWrite());
            //Display the window.
            frame.pack();
            frame.setVisible(true);
        }
    }


	@Override
	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if (node == null)
			return;
		Object info = node.getUserObject();
		TreeInfo nodeinfo = (TreeInfo) info;
		currTreeNode=nodeinfo;
		if (node.getLevel()==0){//根节点
			updateList(nodeinfo.name);
		}else{
			updateList(nodeinfo.parent + "\\"+nodeinfo.name);
	//		if (node.isLeaf()) {//有子节点
	//			if (DEBUG) {
	//				System.err.println(nodeinfo.parent + "\\"+nodeinfo.name);
	//			}
	//		} else {//元子节点
	
	//		}
			if (DEBUG) {
				System.out.println(nodeinfo.parent + "\\"+nodeinfo.toString());
			}
		}
	}

	private class TreeInfo {
		public String parent;
		public String name;
		public int countDir;
		public int countFile;

		public TreeInfo(String dirnameParent, String dirname) {
			parent=dirnameParent;
			name = dirname;
			
			countDir=-1;
			countFile=-1;			
			//System.out.println(dirnameParent+"\\" + dirname);
			//bookURL = getClass().getResource(dirname);
			//if (bookURL == null) {
			//	System.err.println("Couldn't find file: " + dirname);
			//}
			
		}
		
		public void setDirAndFileCount(int nDir,int nFile){
			countDir=nDir;
			countFile=nFile;		        
		}

		@Override
		public String toString() {
			String value=name;
			
			if (parent.length()==0){ // is root node.
				value="ROOT-------";
			}
			if (countFile>0){
			    return value;//+"["+countFile+"]";//可以在new时返回一个字符串
			}else{
				return value;//可以在new时返回一个字符串
			}
		}
	}

	private void initHelp() {
		String s = "TreeDemoHelp.html";
		helpURL = getClass().getResource(s);
		if (helpURL == null) {
			System.err.println("Couldn't open help file: " + s);
		} else if (DEBUG) {
			System.out.println("Help URL is " + helpURL);
		}

		
	}

	private DefaultMutableTreeNode addTreeDirNode(DefaultMutableTreeNode parentNode,String dirParent,String dir){	
		DefaultMutableTreeNode node = null;

        TreeInfo info=new TreeInfo(dirParent,dir);
        getDirAndFileCount(dirParent+"\\"+dir);
        info.setDirAndFileCount(countDir,countFile);
		node = new DefaultMutableTreeNode(info);
		parentNode.add(node);
		return node;
	}
	private void searchDir(DefaultMutableTreeNode parentNode,File path) {
        File[] files = path.listFiles();
         
        //search all .java files in the directory of "E:\Test",
        //but not include those .java files occurs in the sub-directory
        //if(path.getAbsolutePath().equalsIgnoreCase("E:\\Test") ){
        //    listFilteredFileName(path);
        //}
         
        for (File file : files) {
            if (file.isDirectory()) {
                //File dir = file.getAbsoluteFile();
            	//System.out.println(path.getAbsolutePath()+"\\" + file.getName());
            	DefaultMutableTreeNode node=addTreeDirNode(parentNode,path.getAbsolutePath(),file.getName());
                searchDir(node,file);
            }
        }
    }
	
	private int countDir=0;
	private int countFile=0;
	public void getDirAndFileCount(String pathname){
		//
		int nDir=0;
		int nFile=0;
		System.out.println("pathname=" + pathname);
		File path = new File(pathname);
        File[] files = path.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                nDir+=1;
            }else if (file.isFile()) {
            	nFile+=1;
            }
        }
		
		countDir=nDir;
		countFile=nFile;		        
	}	
	private DefaultMutableTreeNode TreeCreateNodes(String rootDir){

		File path = new File(rootDir);

        TreeInfo rootInfo=new TreeInfo("",path.getPath());
        getDirAndFileCount(path.getPath());
        rootInfo.setDirAndFileCount(countDir,countFile);
		DefaultMutableTreeNode parentNode=new DefaultMutableTreeNode(rootInfo);//new NodeInfo("",path.getPath()));
		
		
        if(path.getAbsolutePath().equalsIgnoreCase(path.getPath()) ){
        	System.out.println(" ------------------>???? "+path.getPath());
        }
        searchDir(parentNode,path);
	    return parentNode;
	}
	private void TreeTest(DefaultMutableTreeNode parentNode) {
		DefaultMutableTreeNode dir = null;
		DefaultMutableTreeNode file = null;
		
     	dir = new DefaultMutableTreeNode("111111");
		parentNode.add(dir);

		dir = new DefaultMutableTreeNode(new TreeInfo("222222", "33333333333"));
		parentNode.add(dir);

		file = new DefaultMutableTreeNode("444444444");
		dir.add(file);
	
		file = new DefaultMutableTreeNode(new TreeInfo("55555555555", "6666666666"));
		dir.add(file);
	}

	@Override
    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
		//System.out.println("-- ListSelectionEvent 111 ");
        if (e.getValueIsAdjusting() == false) {
        	System.out.println("-- ListSelectionEvent 222 ");
            if (list.getSelectedIndex() == -1) {
            //No selection, disable fire button.
                //fireButton.setEnabled(false);
            	//System.out.println("-- list.getSelectedIndex() == -1 ");
            } else {
            //Selection, enable the fire button.
                //fireButton.setEnabled(true);
            	//System.out.println(" list.getSelectedIndex()="+list.getSelectedIndex()+ ", " +currTreeNode.parent+"\\"+currTreeNode.name);
            	Eml eml=new Eml();
            	eml.get(currTreeNode.parent+"\\"+currTreeNode.name+"\\"+listModel.get(list.getSelectedIndex()).toString());
            }
        }
    }
	
	private DefaultListModel CreateListModel(){
		
        listModel = new DefaultListModel();
        listModel.addElement("Hello");
        listModel.addElement("  World");
        return listModel;
	}
	

	private void updateList(String pathname){
		File path=new File(pathname);
        File[] files = path.listFiles();
        System.out.println("path="+path.getPath());
        //search all .java files in the directory of "E:\Test",
        //but not include those .java files occurs in the sub-directory
        //if(path.getAbsolutePath().equalsIgnoreCase("E:\\Test") ){
        //    listFilteredFileName(path);
        //}
        listModel.removeAllElements();
        if (files.length>0){
	        for (File file : files) {
	            if (file.isFile()) {
	                //File file = file.getAbsoluteFile();
	            	//System.out.println(path.getAbsolutePath()+"\\" + file.getName());
	            	listModel.addElement(file.getName());	
	            }
	        }
        }
        
        list.setModel(listModel);

	}
	
	
	
	
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
            	JFrame frame = new JFrame("Demo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        		//int width=Toolkit.getDefaultToolkit().getScreenSize().width;
        		//int height=Toolkit.getDefaultToolkit().getScreenSize().height;
        		///System.out.println("screen size( W " +width+", H "+height+")");
        		frame.setPreferredSize(new Dimension(800, 600));
        		frame.setSize(new Dimension(800, 600));
                //frame.setLocation(width/2-frame.getWidth()/2,height/2-frame.getWidth()/2);
        		frame.setLocationRelativeTo(null); 
                frame.add(new Main());
                //Display the window.
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

}
