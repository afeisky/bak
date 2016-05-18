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



/* HtmlDemo.java needs no other files. */
package com.afemail;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

public class EmlWrite extends JPanel
                      implements ActionListener, DocumentListener {
    JLabel theLabel;
    JTextArea htmlTextArea;
    private JButton saveBtn=new JButton(" Save ");
    private JButton attBtn=new JButton(" Save ");

	private JTextField toText,ccText;
	private JTextField mcText;
	private JTextField subjectText;
	private JLabel subjectLabel;
	private JPanel botPanel;
	private EmlWrite THIS;
	private JPanel topPanel;
	private JButton[] btnArray = new JButton[20]; //for(int i = 0;i < btnArray.length;i++){ btnArray[i] = new JButton(String.valueOf(i)); }
	private JPopupMenu[] popupMenu=new JPopupMenu[20]; 
	private JMenuItem[] deleteMenuItem=new JMenuItem[20];
	private JSplitPane midPplitPane;
	
    public EmlWrite() {
    	super(new BorderLayout());//setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    	try {  
            UIManager.setLookAndFeel(//关键句1  
            UIManager.getSystemLookAndFeelClassName());//关键句2  
        } catch (Exception e) {  
            e.printStackTrace();  
        }   
    	//setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
    	
    	//init 
    	for(int i = 0;i < btnArray.length;i++)
    	{ 
    		btnArray[i] = new JButton(); 
    		btnArray[i].addActionListener(this);
    	}
    	for(int i = 0;i < deleteMenuItem.length;i++)
    	{ 
    		deleteMenuItem[i] = new JMenuItem("Delete"); 
    		deleteMenuItem[i].addActionListener(this);

    	}       	
    	for(int i = 0;i < popupMenu.length;i++)
    	{ 
    		popupMenu[i] = new JPopupMenu(); 
    		MouseListener popupListener = new PopupListener(popupMenu[i]);
	        btnArray[i].addMouseListener(popupListener);
    	} 	
    	///
        saveBtn = new JButton(" Save ");
        saveBtn.addActionListener(this);//saveBtn.addActionListener(new SaveListener());
        
        attBtn = new JButton("attachment");
        attBtn.addActionListener(this);
        
        //JButton button3 = new JButton(" Demo1 ");
        //JButton button4 = new JButton(" Demo2 ");
        //JButton button8 = new JButton(" Demo8 ");
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.LINE_AXIS));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(3,10,3,10));
        btnPanel.add(saveBtn);
        btnPanel.add(attBtn);
        //btnPanel.add(button3);
        //btnPanel.add(button4);
        //btnPanel.add(button8);
        
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.PAGE_AXIS));
        emailPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        //leftPanel.setBorder(BorderFactory.createCompoundBorder(
         ///       BorderFactory.createTitledBorder(
            //        "Edit the HTML, then click the button"),
              //  BorderFactory.createEmptyBorder(10,10,10,10)));

        toText = new JTextField(10);
        toText.addActionListener(this);
        toText.getDocument().addDocumentListener(this);

        ccText = new JTextField(10);
        ccText.addActionListener(this);
        ccText.getDocument().addDocumentListener(this);

        mcText = new JTextField(10);
        mcText.addActionListener(this);
        mcText.getDocument().addDocumentListener(this);
        
        emailPanel.add(Box.createRigidArea(new Dimension(0,10)));
        emailPanel.add(toText);
        emailPanel.add(ccText);
        emailPanel.add(mcText);

        JPanel subjectPanel = new JPanel();
        subjectPanel.setLayout(new BoxLayout(subjectPanel, BoxLayout.LINE_AXIS));
        subjectPanel.setBorder(BorderFactory.createEmptyBorder(3,10,3,10));
        subjectText = new JTextField(10);
        subjectText.addActionListener(this);
        subjectText.getDocument().addDocumentListener(this);
        subjectLabel = new JLabel("Title:  ");     
        subjectPanel.add(subjectLabel);
        subjectPanel.add(subjectText);
        
        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(3,10,3,10));
        topPanel.add(btnPanel, BorderLayout.PAGE_START);
        //topPanel.add(emailPanel, BorderLayout.CENTER);
        topPanel.add(subjectPanel, BorderLayout.PAGE_END);

        
        String initialText = "<html>\n" +
                "Color and font test:\n" +
                "<ul>\n" +
                "<li><font color=red>red</font>\n测试中事在。地奇怪非机动车要老大哥地， 魂牵梦萦右右协硒鼓 夺" +
                "<li><font color=blue>blue</font>\n" +
                "<li><font color=green>green</font>\n" +
                "<li><font size=-2>small</font>\n" +
                "<li><font size=+2>large</font>\n" +
                "<li><font color=red>red</font>\n测试中事在。地奇怪非机动车要老大哥地， 魂牵梦萦右右协硒鼓 夺" +
                "<li><font color=blue>blue</font>\n" +
                "<li><font color=green>green</font>\n" +
                "<li><font size=-2>small</font>\n" +
                "<li><font size=+2>large</font>\n" +
                "<li><font color=red>red</font>\n测试中事在。地奇怪非机动车要老大哥地， 魂牵梦萦右右协硒鼓 夺" +
                "<li><font color=blue>blue</font>\n" +
                "<li><font color=green>green</font>\n" +
                "<li><font size=-2>small</font>\n" +
                "<li><font size=+2>large</font>\n" +
                "<li><font color=red>red</font>\n测试中事在。地奇怪非机动车要老大哥地， 魂牵梦萦右右协硒鼓 夺" +
                "<li><font color=blue>blue</font>\n" +
                "<li><font color=green>green</font>\n" +
                "<li><font size=-2>small</font>\n" +
                "<li><font size=+2>large</font>\n" +
                "<li><font color=red>red</font>\n测试中事在。地奇怪非机动车要老大哥地， 魂牵梦萦右右协硒鼓 夺" +
                "<li><font color=blue>blue</font>\n" +
                "<li><font color=green>green</font>\n" +
                "<li><font size=-2>small</font>\n" +
                "<li><font size=+2>large</font>\n" +
                "<li><font color=red>red</font>\n测试中事在。地奇怪非机动车要老大哥地， 魂牵梦萦右右协硒鼓 夺" +                
                "<li><i>italic</i>\n" +
                "<li><b>bold</b>\n" +
                "</ul>\n";

        htmlTextArea = new JTextArea();
        htmlTextArea.setText(initialText);
        JScrollPane scrollPane = new JScrollPane(htmlTextArea);

        JButton changeTheLabel = new JButton("Change the label");
        changeTheLabel.setMnemonic(KeyEvent.VK_C);
        changeTheLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeTheLabel.addActionListener(this);

        JButton button9 = new JButton("Change the label");
        button9.setMnemonic(KeyEvent.VK_C);
        button9.setAlignmentX(Component.CENTER_ALIGNMENT);
        button9.addActionListener(this);
        
//        theLabel = new JLabel(initialText) {
//            public Dimension getPreferredSize() {
//                return new Dimension(200, 200);
//            }
//            public Dimension getMinimumSize() {
//                return new Dimension(200, 200);
//            }
//            public Dimension getMaximumSize() {
//                return new Dimension(200, 200);
//            }
//        };
//        theLabel.setVerticalAlignment(SwingConstants.CENTER);
//        theLabel.setHorizontalAlignment(SwingConstants.CENTER);
//

        botPanel = new JPanel();
        JButton button11 = new JButton(" attach11 ");
        JButton button12 = new JButton(" attach12 ");
        botPanel.add(button11);
        botPanel.add(button12);        
        JScrollPane botPane = new JScrollPane(botPanel);


        JTabbedPane tabbedPane = new JTabbedPane();
        ImageIcon icon = null; //createImageIcon("images/middle.gif");
        tabbedPane.addTab("Text Edit", icon, scrollPane, "Does nothing");        
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        
        JComponent panel2 = makeTextPanel("Panel #2");
        tabbedPane.addTab("Html Edit", icon, panel2,  "Does twice as much nothing");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        
		midPplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,tabbedPane, botPane);
		midPplitPane.setOneTouchExpandable(true);       
		scrollPane.setMinimumSize(new Dimension(0, 50));
		botPane.setPreferredSize(new Dimension(0, 50));
		midPplitPane.setDividerLocation(500);
		midPplitPane.setPreferredSize(new Dimension(800, 600));
                   
		//this.setPreferredSize(new Dimension(800, 600));
		this.add(topPanel, BorderLayout.PAGE_START);
		this.add(midPplitPane, BorderLayout.CENTER);

        THIS=this;
        //botPanel.setVisible(eml.listAttachPathName.size()>0);
    }
    
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(SwingConstants.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
    //React to the user pushing the Change button.
    @Override
	public void actionPerformed(ActionEvent e) {
        if (e.getSource()==saveBtn){
        	System.out.println("save button");
        	saveEml();
        }else if (e.getSource()==attBtn){
        	System.out.println("Att button");
        	JFileChooser chooser=new JFileChooser() ;
        	if (chooser.showOpenDialog(null)==0){ //chooser.setCurrentDirectory(new File("C:\\")) , chooser.showSaveDialog()
        		addAttachment(chooser.getSelectedFile().getPath(),chooser.getSelectedFile().getName());//chooser.getSelectedFile().getParentFile().getPath(),chooser.getSelectedFile().getName());
        	}else{
        		
        	}
        }else{
        	System.out.println("getActionCommand="+e.getActionCommand());   
        	if (Data.eml.listAttachPathName.size()>1)
	        	if (deleteMenuItem[0]==deleteMenuItem[1]){
	    			System.out.println("============================");

	    		}
        	
        	for (int i=0;i<Data.eml.listAttachPathName.size();i++){
        		if (e.getSource()==deleteMenuItem[i]){
        			System.out.println(i+"="+e.getSource());
        			removeAttachment(i);
        		}
        	}

        }
    }


	private void addAttachment(String path,String filename){
		System.out.println(path);
		if (Data.eml.addAttachFile(path, filename)==0){
			refreshUI();
		}		
    }
	private void updateAttachButton(){
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<Map<String, Object>> ilist = Data.eml.listAttachPathName.iterator();
		
		if (Data.eml.listAttachPathName!=null){
			botPanel.removeAll();
			int i=0;
			while (ilist.hasNext()){				
				map = ilist.next();
				System.out.println("pathname="+map.get("pathname"));					
				//btnArray[i]=new JButton(map.get("filename").toString());
				btnArray[i].setText(map.get("filename").toString());
				//btnArray[i].addActionListener(this);
				botPanel.add(btnArray[i]);
		        //Create the popup menu.    
		        //deleteMenuItem[i].addActionListener(this);
		        popupMenu[i].add(deleteMenuItem[i]);
		        if (deleteMenuItem[i].getComponentPopupMenu()==popupMenu[i].getComponentPopupMenu()){
		        	System.out.println("i="+i+" OK");
		        }else
		        	System.out.println("i="+i+" fail");
		        //MouseListener popupListener = new PopupListener(popupMenu[i]);
		        //btnArray[i].addMouseListener(popupListener);
		        
				i+=1;
				//Integer currid = (Integer) listAttachPathName.get(position).get("id");		
			}
			botPanel.revalidate();  //JPanel.revalidate()=JPanel.validate();+JPanel.repaint(); 
		}
	}
	private void refreshUI(){
		updateAttachButton();
		saveBtn.setEnabled(Data.eml.listAttachPathName.size()<=btnArray.length);
		//botPanel.setVisible(eml.listAttachPathName.size()>0);
	}
	private void removeAttachment(int indexSort){
		System.out.println("removeAttachment"+indexSort);
		System.out.println("=1="+Data.eml.listAttachPathName.size());
		if (Data.eml.removeAttachFile(indexSort)==0){		
			System.out.println("=2="+Data.eml.listAttachPathName.size());
			refreshUI();
			System.out.println("=3="+Data.eml.listAttachPathName.size());
		}
		System.out.println("=4="+Data.eml.listAttachPathName.size());
    }

	private InternetAddress[] parseAddress(String addr)// cannot use split(";")?
    {
	    StringTokenizer token = new StringTokenizer(addr, ";");
	    InternetAddress[] addrArr = new InternetAddress[token.countTokens()];
	    int i = 0;
	    while (token.hasMoreTokens())
	    {
	    try
	    {
	    addrArr[i] = new InternetAddress(token.nextToken().toString());
	    }
	    catch (AddressException e1)
	    {
	    return null;
	    }
	    i++;
	    }
	    return addrArr;
    } 
    public void saveEml(){

    	Data.eml.to=parseAddress("chaofei.wu.hz@tcl.com");
    	Data.eml.from = parseAddress("chaofei.wu.hz@tcl.com");
    	Data.eml.host = "mail.tcl.com";
    	Data.eml.subject= subjectText.getText();
    	Data.eml.saveFileName= Data.eml.subject;
    	Data.eml.content=htmlTextArea.getText();
    	
    	String saveFileName=Data.eml.getPathFileName();
    	//String msgText1 = "Sending a file.\n";
    	//String subject = "Sending a file";

    	// create some properties and get the default Session
    	Properties props = System.getProperties();
    	props.put("mail.smtp.host", Data.eml.host);
    	
    	Session session = Session.getInstance(props, null);
    	session.setDebug(Data.eml.sessionDebug);
    	
    	try {
    	    // create a message
    	    MimeMessage msg = new MimeMessage(session);
    	    msg.setFrom(Data.eml.from[0]);//msg.setFrom(new InternetAddress(from));
    	    msg.setRecipients(Message.RecipientType.TO, Data.eml.to);
    	    msg.setSubject(Data.eml.subject);

    	    Multipart mp = new MimeMultipart();
    	    	 
    	    // create and fill the first message part
    	    MimeBodyPart mbpContent = new MimeBodyPart();
    	    mbpContent.setText(Data.eml.content);    	    
    	    mp.addBodyPart(mbpContent);    
   
    		Map<String, Object> map = new HashMap<String, Object>();
    		Iterator<Map<String, Object>> ilist = Data.eml.listAttachPathName.iterator();
    		if (Data.eml.listAttachPathName!=null){
    			while (ilist.hasNext()){				
    				map = ilist.next();
    				System.out.println("pathname="+map.get("pathname"));					
    				// create the second message part
    	    	    MimeBodyPart mbp1 = new MimeBodyPart();
    	    	    // attach the file to the message
    	    	    mbp1.attachFile(map.get("pathname").toString());
    	    	    mbp1.setHeader("Content-Transfer-Encoding", "base64");
    	    	    /*
    	    	     * Use the following approach instead of the above line if
    	    	     * you want to control the MIME type of the attached file.
    	    	     * Normally you should never need to do this.
    	    	     *
    	    	    FileDataSource fds = new FileDataSource(filename) {
    	    		public String getContentType() {
    	    		    return "application/octet-stream";
    	    		}
    	    	    };
    	    	    mbp2.setDataHandler(new DataHandler(fds));
    	    	    mbp2.setFileName(fds.getName());
    	    	     */
    	    	    // create the Multipart and add its parts to it
    	    	    mp.addBodyPart(mbp1);
    				//Integer currid = (Integer) listAttachPathName.get(position).get("id");		
    			}
    		}

    	    // add the Multipart to the message
    	    msg.setContent(mp);

    	    // set the Date: header
    	    msg.setSentDate(new Date());

    	    /*
    	     * If you want to control the Content-Transfer-Encoding
    	     * of the attached file, do the following.  Normally you
    	     * should never need to do this.
    	     */
    	    msg.saveChanges();    	    
    	    msg.writeTo(new FileOutputStream(saveFileName));

    	    // send the message
    	    //Transport.send(msg);
    	    
    	} catch (MessagingException mex) {
    	    mex.printStackTrace();
    	    Exception ex = null;
    	    if ((ex = mex.getNextException()) != null) {
    		ex.printStackTrace();
    	    }
    	} catch (IOException ioex) {
    	    ioex.printStackTrace();
    	}
    }

//    class SaveListener implements ActionListener {
//        public void actionPerformed(ActionEvent e) {
//        	saveEml();
//        }
//    }
//  
//    class AttListener implements ActionListener {
//        public void actionPerformed(ActionEvent e) {
//        	addAttachment();
//        }
//    }    
    

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
//    private static void createAndShowGUI() {
//        //Create and set up the window.
//        JFrame frame = new JFrame("HtmlDemo");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        //Add content to the window.
//        frame.add(new EmlWrite(new EmlInfo("G:\\mail")),BorderLayout.CENTER);
//		//int width=Toolkit.getDefaultToolkit().getScreenSize().width;
//		//int height=Toolkit.getDefaultToolkit().getScreenSize().height;
//		///System.out.println("screen size( W " +width+", H "+height+")");
//		frame.setPreferredSize(new Dimension(800, 600));
//		frame.setSize(new Dimension(800, 600));
//        //frame.setLocation(width/2-frame.getWidth()/2,height/2-frame.getWidth()/2);
//		frame.setLocationRelativeTo(null); 
//        //Display the window.
//        frame.pack();
//        frame.setVisible(true);
//    }

//    public static void main(String[] args) {
//        //Schedule a job for the event dispatch thread:
//        //creating and showing this application's GUI.
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                //Turn off metal's use of bold fonts
//	        //UIManager.put("swing.boldMetal", Boolean.FALSE);
//	        createAndShowGUI();
//            }
//        });
//    }

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	 class PopupListener extends MouseAdapter {
	        JPopupMenu popup;

	        PopupListener(JPopupMenu popupMenu) {
	            popup = popupMenu;
	        }

	        @Override
			public void mousePressed(MouseEvent e) {
	            maybeShowPopup(e);
	        }

	        @Override
			public void mouseReleased(MouseEvent e) {
	            maybeShowPopup(e);
	        }

	        private void maybeShowPopup(MouseEvent e) {
	            if (e.isPopupTrigger()) {
	                popup.show(e.getComponent(),
	                           e.getX(), e.getY());
	            }
	        }
	    }
}
