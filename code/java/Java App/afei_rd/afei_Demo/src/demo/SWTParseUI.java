package demo;




import org.eclipse.swt.SWT;  
import org.eclipse.swt.widgets.Display;  
import org.eclipse.swt.widgets.FileDialog;  
import org.eclipse.swt.widgets.MessageBox;  
import org.eclipse.swt.widgets.Shell;  
import org.eclipse.swt.widgets.Label;  
import org.eclipse.swt.widgets.Text;  
import org.eclipse.swt.widgets.Button;  
import org.eclipse.swt.events.SelectionAdapter;  
import org.eclipse.swt.events.SelectionEvent;  
import org.eclipse.swt.widgets.Group;  
  
public class SWTParseUI {  
  
    protected Shell shell;  
    private Text text_file;  
  
    /** 
     * Launch the application. 
     * @param args 
     */  
    public static void main(String[] args) {  
        try {  
        	SWTParseUI window = new SWTParseUI();  
            window.open();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
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
        shell.setSize(500, 200);  
        shell.setText("解析WSDL");  
          
        text_file = new Text(shell, SWT.BORDER);  
        text_file.setBounds(80, 33, 290, 24);  
        //实现文件浏览功能  
        Button browseButton = new Button(shell,SWT.PUSH);  
        browseButton.setText("浏览...");  
        //browseButton.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NORMAL));  
        browseButton.setBounds(383, 33, 80, 24);  
        browseButton.addSelectionListener(new SelectionAdapter(){  
  
            public void widgetSelected(SelectionEvent e){  
                 FileDialog dialog = new FileDialog (shell, SWT.OPEN);  
                 dialog.setText("Source Folder Selection");  
                 dialog.setFilterExtensions(new String[] {"*.xml","*.wsdl","*.asmx","*.*"});  
                 String filePath = dialog.open();  
                 if(dialog!=null){  
                     text_file.setText(filePath);  
                 }  
            }  
        });  
        Button button_exe = new Button(shell, SWT.NONE);  
        button_exe.addSelectionListener(new SelectionAdapter() {  
            @Override  
            public void widgetSelected(SelectionEvent e) {  
                MessageBox msgbox = new MessageBox(shell,  
                    SWT.ICON_QUESTION | SWT.OK);  
                msgbox.setText("提示");  
                  
                String file = "";  
                file = text_file.getText();  
                if(file.equals("") || file == null){  
                    msgbox.setMessage("WSDL文件不能为空");  
                    msgbox.open();  
                    return;  
                }  
                  
                
  
                msgbox.open();  
          
            }  
        });  
        //button_exe.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NORMAL));  
        button_exe.setBounds(214, 133, 87, 23);  
        button_exe.setText("\u6267\u884C");  
          
        Group group = new Group(shell, SWT.NONE);  
        group.setBounds(10, 10, 472, 117);  
          
        Label label = new Label(group, SWT.NONE);  
        label.setBounds(10, 23, 105, 24);  
        //label.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NORMAL));  
        label.setText("源文件：");  
          
  
    }  
}  