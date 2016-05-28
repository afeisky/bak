package com.afei;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.mail.MessagingException;

import javax.mail.internet.MimeBodyPart;

import javax.swing.JOptionPane;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import swing2swt.layout.BorderLayout;

import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.osgi.internal.signedcontent.Base64;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class Main {
	protected Shell shell;
	private Browser browser;
	protected StyledText textContent;
	private TabItem tabiText;
	private TabItem tabiHtml;
	private String strAppTitle="EML ";
	private Text textTitle;
	private TabFolder tabFolder;
	private String appDir;
	private String filterExtName="*.eml";//"*.aml;*.html;*.htm;*.txt;*.eml";
	private String fileExtName=".eml";//"*.aml;*.html;*.htm;*.txt;*.eml";
	private Button btnAddAttachement;
	private Button btnRemoveAttachement;
	private Shell shell1 = new Shell(Display.getDefault());
	private TableColumn tblclmnFilename;

	private String FilePathName = "";
	private Composite compositeTop;
	private Composite composite_1;
	private Button btnText;
	private Button btnHtml;
	private Composite composite_2;
	private Text textSubject;
	private Composite composite2;
	private Button btnText1;
	private Button btnSave;
	private Button btnSaveAs;	
	private Button btnFont;
	private Eml emlR;
	//---
	private ImageRegistry imageRegistry;
	Image iconFolder = new Image(shell1.getDisplay(), "C:/icons/web/go.gif");
	Image iconFile = new Image(shell1.getDisplay(), "C:/icons/web/go.gif");
	private Table table;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private TabItem tabItem;
	private Button btnOpen;


	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		String filename="";
		System.out.println(args.length);
		if (args.length>0){
			filename=args[0];
			System.out.println(args[0]);
		}
		if (args.length>1)
			System.out.println(args[1]);		
		try {
			Main window = new Main(filename);
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public Main(String pathFileName){
		FilePathName=pathFileName;
	}
	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(300, 300);
		shell.setText("SWT Application");
		shell.setLayout(new BorderLayout(0, 0));
		
		Api.initImageResourse(shell);
		
		compositeTop = new Composite(shell, SWT.NONE);
		compositeTop.setLayoutData(BorderLayout.NORTH);
		compositeTop.setLayout(new FillLayout(SWT.HORIZONTAL));

		composite_1 = new Composite(compositeTop, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		btnOpen = new Button(composite_1, SWT.NONE);
		btnOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String fn=openDialog("Open Eml file from...","");
				if (fn.length()>0){
					FilePathName=fn;
					init(true);
				}else{
					return;
				}
			}
		});
		btnOpen.setText("Open");

		btnText = new Button(composite_1, SWT.NONE);
		btnText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//textContent.setVisible(true);
				//browser.setVisible(false);
			}
		});
		btnText.setText("TEXT");
		btnText.setEnabled(false);
		btnHtml = new Button(composite_1, SWT.NONE);
		btnHtml.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//textContent.setVisible(false);
				//browser.setVisible(true);
				browser.setText(text2Html(textContent.getText()));
			}
		});
		btnHtml.setText("HTML");
		btnHtml.setEnabled(false);
		btnSave = new Button(composite_1, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Save(false);
			}
		});
		btnSave.setText("Save");
		btnSaveAs = new Button(composite_1, SWT.NONE);
		btnSaveAs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Save(true);
			}
		});
		btnSaveAs.setText("Save as");		
		btnFont = new Button(composite_1, SWT.NONE);
		btnFont.setText("Font");
		btnFont.setEnabled(false);
		composite_2 = new Composite(compositeTop, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));

		textSubject = new Text(composite_2, SWT.BORDER);

		composite2 = new Composite(shell, SWT.NONE);
		composite2.setLayout(new StackLayout());
		
		tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.item==tabiHtml){
					browser.setText(text2Html(textContent.getText()));
				}
			}
		});
		tabFolder.setLayoutData(BorderLayout.CENTER);

		tabiText = new TabItem(tabFolder, SWT.NONE);
		tabiText.setText("Text");
		  
		textContent = new StyledText(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);//| SWT.WRAP
		//Font font = new Font(shell.getDisplay(), "Courier New", 12, SWT.NORMAL);      
		//styledText.setFont(font);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		textContent.setLayoutData(gridData);		
		textContent.setText("<html>\r\n<h1>SWT browser</h1>\r\n</html>");
		textContent.addLineStyleListener(new LineStyleListener() {      
			   @Override
			public void lineGetStyle(LineStyleEvent event) {
			    String line = event.lineText;      
			    int cursor = -1;      
			    LinkedList list = new LinkedList();  
			    String keywords="<td>";
			    while( (cursor = line.indexOf(keywords, cursor+1)) >= 0) {      
			       list.add(Api.getForeHighlightStyle(event.lineOffset+cursor, keywords.length(),SWT.COLOR_BLUE));      
			    }
			    keywords="</td>";
			    while( (cursor = line.indexOf(keywords, cursor+1)) >= 0) {      
				   list.add(Api.getForeHighlightStyle(event.lineOffset+cursor, keywords.length(),SWT.COLOR_BLUE));
			    }   			    
			    event.styles = (StyleRange[]) list.toArray(new StyleRange[list.size()]);      
			   }      
			  });

		tabiText.setControl(textContent);
		      
		
		tabiHtml = new TabItem(tabFolder, SWT.NONE);
		tabiHtml.setText("Html");
		
		browser = new Browser(tabFolder, SWT.BORDER);
		tabiHtml.setControl(browser);
		//browser.setUrl("https://www.baidu.com");
        //browser.refresh();
		//browser.setText("<html><body><h1>SWT & JFace </h1></body/html>");
		browser.setText(textContent.getText());
		
		Composite compositeBottom = new Composite(shell, SWT.NONE);
		compositeBottom.setLayoutData(BorderLayout.SOUTH);
		compositeBottom.setLayout(new GridLayout(2, false));
		

		Composite composite = new Composite(compositeBottom, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.VERTICAL));
		
		
		btnAddAttachement = new Button(composite, SWT.NONE);
		btnAddAttachement.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				FileDialog dialog = new FileDialog (shell, SWT.OPEN);  
                dialog.setText("Attach file");  
                //dialog.setFilterNames(new String[] {"AMail Files ("+filterExtName+")","All Files (*.*)"});  
                dialog.setFilterNames(new String[] {"All Files (*.*)"});
                dialog.setFilterExtensions(new String[] {"*.*"});  	
                String filePath = dialog.open();
                System.out.println("filePath filePath ="+filePath);
                System.out.println("filePath getFileName ="+dialog.getFileName());
                if(dialog!=null && filePath!=null){
                	addAttachment(filePath,dialog.getFileName());
                }
				//JFileChooser chooser=new JFileChooser() ;
	        	//if (chooser.showOpenDialog(null)==0){ //chooser.setCurrentDirectory(new File("C:\\")) , chooser.showSaveDialog()
	        	//	if (addAttachment(chooser.getSelectedFile().getPath(),chooser.getSelectedFile().getName())){
	        	//	}
	        	//}
				
			}
		});
		btnAddAttachement.setBounds(5, 5, 80, 27);
		btnAddAttachement.setText("  Add   ");
		
		btnRemoveAttachement = new Button(composite, SWT.NONE);
		btnRemoveAttachement.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int indexSelect=table.getSelectionIndex();
				if (indexSelect>=0){
					//table.remove(indexSelect);
					if (removeAttachment(indexSelect)){

	        		}
				}				
			}
		});
		btnRemoveAttachement.setBounds(90, 5, 80, 27);
		btnRemoveAttachement.setText("Remove");

		
		table = new Table(compositeBottom, SWT.BORDER | SWT.FULL_SELECTION );
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				openAttachfile();
			}
		});
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAddRemoveButton();
			}
		});

		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		//table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnId = new TableColumn(table, SWT.LEFT);
		tblclmnId.setWidth(40);
		tblclmnId.setText("");
		tblclmnId.setMoveable(true);

		tblclmnFilename = new TableColumn(table,SWT.LEFT);
		tblclmnFilename.setMoveable(true);
		tblclmnFilename.setWidth(100);
		tblclmnFilename.setText("FileName");
		tblclmnFilename.setResizable(true);
		
		//TableColumn tblclmnFilesize = new TableColumn(table, SWT.LEFT);
		//tblclmnFilesize.setWidth(100);
		//tblclmnFilesize.setText("Size");
		
		//TableItem tableItem = new TableItem(table, SWT.NONE);
		//tableItem.setText(new String[] { " ","Name", "Size" });
		//tableItem.setText(new String[] { " ","Name", "Size" });
		//tableItem.setImage(1, iconFile);
		//----------
		if (FilePathName==null)
			FilePathName="";		
		File f=new File(FilePathName);
		if (!f.exists()){
			if (FilePathName==null || FilePathName==""){
				init(false);
				return;
			}else{
				FilePathName=openDialog("Open Eml file from...","");
				if (FilePathName.length()>0){
					init(true);
					return;
				}else{
					JOptionPane.showMessageDialog(null,"File can't be opened!\n"+FilePathName);
					System.exit(0);
				}
			}
		}
		init(true);
	}

	public String openDialog(String title,String Fillter){
		String extName=fileExtName;
		FileDialog dialog = new FileDialog (shell, SWT.OPEN);  
        dialog.setText(title);  
        //dialog.setFilterNames(new String[] {"AMail Files ("+filterExtName+")","All Files (*.*)"});  
        dialog.setFilterNames(new String[]  {"Files ("+filterExtName+")"});
        dialog.setFilterExtensions(new String[] {filterExtName});  	
        String filePath = dialog.open();
        System.out.println("filePath filePath ="+filePath);
        System.out.println("filePath getFileName ="+dialog.getFileName());
        if(dialog!=null && filePath!=null){
        	return filePath;
        }
        return "";
	}
	public String saveDialog(String title,String Fillter){
		String extName=fileExtName;
		FileDialog dialog = new FileDialog (shell, SWT.OPEN);  
        dialog.setText(title);  
        //dialog.setFilterNames(new String[] {"AMail Files ("+filterExtName+")","All Files (*.*)"});  
        dialog.setFilterNames(new String[] {"Files ("+filterExtName+")"});
        dialog.setFilterExtensions(new String[] {filterExtName});
        String filePath = dialog.open();
        System.out.println("filePath filePath ="+filePath);
        System.out.println("filePath getFileName ="+dialog.getFileName());
        if(dialog!=null && filePath!=null){
        	if (filePath.length()>=extName.length()){
        		String ext1=filePath.substring(filePath.length()-extName.length(), filePath.length()).toLowerCase();
        		if (!ext1.equals(extName.toLowerCase())) {
        			filePath+=extName;
        		}
        	}else{
        		filePath+=extName;
        	}
        	return filePath;
        }
        return "";
	}
	private void setAddRemoveButton(){
		int indexSelect=table.getSelectionIndex();
		System.out.println("row count: "+table.getItemCount()+", select:"+indexSelect);
		btnRemoveAttachement.setEnabled(indexSelect>=0);		
	}
	private void openAttachfile(){
		setAddRemoveButton();
		int indexSelect=table.getSelectionIndex();
		emlR.openAttachFile(appDir,indexSelect);
	}
	private void init(boolean new_or_open) {
		shell.setSize(800, 600);
		File directory = new File("");// ²ÎÊýÎª¿Õ
		appDir = null;
		try {
			appDir = directory.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("AppDir=" + appDir);

		//FilePathName = "g:\\mail\\" + "test.eml";
		emlR = new Eml();
		if (new_or_open){
			if (emlR.eml_read(FilePathName)) {
			}
		}else{
			if (emlR.newEml(FilePathName)) {
			}
		}
			
		System.out.println("subject:" + emlR.eml_subject);
		System.out.println("content:" + emlR.eml_content);
		System.out.println("eml_contentType:" + emlR.eml_contentType);
		textSubject.setText(emlR.eml_subject);			
		textContent.setText(emlR.eml_content);
		shell.setText(strAppTitle + " - " + FilePathName);		
		attachHTML = emlR.formatAttachHTML();
		browser.setText(text2Html(textContent.getText()));
		
		refreshUI();
		if (1>0){
			shell.setSize(900, 600);
			tblclmnFilename.setWidth(500);
		}		
		//textContent.setVisible(true);
	}

	private String attachHTML = "";
	private String htmlHeader = "<html><body>[#SUBJECT#]</BR><PRE>[#TEXT#]</PRE></body></html>";

	private String text2Html(String content) {
		
		String str=content;
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<Map<String, Object>> ilist = emlR.listAttach.iterator();
		if (emlR.listAttach!=null){
			int i=0;			
			while (ilist.hasNext()){				
				map = ilist.next();			
				if (map.get("contentID")!=""){
					String filename=map.get("contentID").toString();
					System.out.println(i+"--filename:"+filename);
					if (str.indexOf(filename)>=0){
						MimeBodyPart mbp1;
						mbp1=(MimeBodyPart) map.get("MimeBodyPart");								
						try {
							//mbp1.setHeader("Content-Transfer-Encoding", "base64");
				            System.out.println("--mbp1 is "+mbp1.getEncoding());  
				            //mbp1.writeTo(System.out);
				            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
				            mbp1.writeTo(baos);
				            String strOs = baos.toString();				            
				            //System.out.println(strOs);   
				            int n=baos.toString().indexOf("\r\n\r\n");
				            System.out.println(strOs);
				            System.out.println("--mbp1 is "+n);
				            String imagedata="data:image/gif;base64,"+strOs.substring(n+4,strOs.length()).replaceAll("\r\n", "");
				            str=str.replace("cid:"+filename, imagedata);
//							Object o = mbp1.getContent();
//							if (o instanceof InputStream) {
//								System.out.println(i+"====+");
//								InputStream is = (InputStream) o;							
//								//System.out.println(is.toString());
//						        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();  
//						        int j;  
//						        while ((j = is.read()) != -1) {  
//						            baos1.write(j);  
//						        }  
//						        String str1 = baos1.toString();  
//						       // System.out.println(str1);    
//						        //System.out.println(mbp1.getContentMD5());
//						        //System.out.println("+++++++++++"); 
//						        //System.out.println(baos1.toString());  
//								str=str.replace("ATT00001(05-20-15-36-57).jpg", imagedata);
//								System.out.println(i+"+"+str);								
//							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (MessagingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				i+=1;
			}
		}
		System.out.println("++++"+str);	
		//str=htmlHeader.replaceAll("[#TEXT#]", content).replaceAll("[#SUBJECT#]", "<h3>" + str + "<h3>");
		//System.out.println("++++"+str);		
		return str;
	}

	// -------save
	public void Save(boolean isSaveAs) {
		Api.Copy(FilePathName,FilePathName+".bak");
		emlR.eml_subject=textSubject.getText();
		emlR.eml_content=textContent.getText();
		if (isSaveAs){
			String fn=saveDialog("Save Eml file to...","");
			if (fn.length()>0){
				FilePathName=fn;
			}else{
				return;
			}
		}else
			if (FilePathName.length()==0){
				String fn=saveDialog("Save Eml file to...","");
				if (fn.length()>0){
					FilePathName=fn;
				}else{
					return;
				}
			}
		
		if (FilePathName.length()>0){
			if (emlR.Save(FilePathName)) {
				System.out.println("---success----");
				shell.setText(strAppTitle + " - " + FilePathName);	
			}
		}
	}
	private String fileNameAutoSave="";
	public void SaveAuto() {	
		if (fileNameAutoSave==""){
			if (FilePathName==""){
	    		SimpleDateFormat fmt = new SimpleDateFormat("TEMP_yyyy-MM-dd_HHmmss");
				fileNameAutoSave=appDir+"\\~"+fmt.format((new Date()).getTime())+fileExtName;
			}else{
				fileNameAutoSave=FilePathName+"~";
			}
		}
		emlR.eml_subject=textSubject.getText();
		emlR.eml_content=textContent.getText();
		if (emlR.Save(fileNameAutoSave)) {
			System.out.println("---success----");
		}
		
	}


	private boolean addAttachment(String pathFileName,String filename){
		System.out.println(pathFileName);
		
		if (emlR.attachFile(pathFileName, filename)==0){		
			updateAttachButton();
			return true;
		}		
		return false;
    }

	private void updateAttachButton(){
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<Map<String, Object>> ilist = emlR.listAttach.iterator();
		
		if (emlR.listAttach!=null){
			table.removeAll();
			int i=0;
			while (ilist.hasNext()){				
				map = ilist.next();
				System.out.println("pathFileName="+map.get("pathFileName"));		
				TableItem item = new TableItem(table, SWT.NULL);
				int indexSelect=10;//table.getSelectionIndex();
				//table.setSelection(table.getItemCount()-1);
				//item.setBackground(i % 2 == 0 ? shell.getDisplay().getSystemColor(SWT.COLOR_WHITE): shell.getDisplay().getSystemColor(SWT.COLOR_GRAY));
				item.setText(0, ""+(table.getItemCount()));//getFileIconByFileExtName(map.get("filename").toString()));
				if (map.get("fileName")!=""){
					item.setText(1, map.get("fileName").toString());
					//item.setImage(1, getFileIconByFileExtName(map.get("pathFileName").toString()));
				}
				if (map.get("contentID")!=""){
					item.setForeground(0,shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
				}
				//item.setText(2, "1KB ");
				//item.setText(3, new Date(file.lastModified()).toString());
				i+=1;
				//Integer currid = (Integer) listAttach.get(position).get("id");		
			}
			setAddRemoveButton();
		}
	}
	private void refreshUI(){
		updateAttachButton();
		//saveBtn.setEnabled(emlR.listAttach.size()<=btnArray.length);
		//botPanel.setVisible(eml.listAttach.size()>0);
	}
	private boolean removeAttachment(int indexSort){
		System.out.println("removeAttachment"+indexSort);
		System.out.println("=1="+emlR.listAttach.size());
		if (emlR.removeAttachFile(indexSort)==0){		
			System.out.println("=2="+emlR.listAttach.size());
			refreshUI();
			System.out.println("=3="+emlR.listAttach.size());
		}
		System.out.println("=4="+emlR.listAttach.size());
		return false;
    }


	private void dialog1(FileDialog dialog){
		boolean done = false;  
		MessageBox mg = new MessageBox(dialog.getParent(), SWT.ICON_WARNING| SWT.YES);
		
        mg.setText("Hint");  
        mg.setMessage("coding...");  
        done = mg.open() == SWT.YES;  
        done = true;  
	}
}
