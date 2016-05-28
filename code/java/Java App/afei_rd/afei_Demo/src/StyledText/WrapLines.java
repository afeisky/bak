package StyledText;

import org.eclipse.swt.SWT;      
import org.eclipse.swt.layout.GridData;      
import org.eclipse.swt.layout.GridLayout;      
import org.eclipse.swt.widgets.Display;      
import org.eclipse.swt.widgets.Label;      
import org.eclipse.swt.widgets.Shell;      
import org.eclipse.swt.widgets.Text;      
  
public class WrapLines {      
    
Display display = new Display();      
Shell shell = new Shell(display);      
    
Text text1;      
Text text2;      
String line = "abcdefghijklmnopqrstuvwxyz0123456789";      
    
private void init() {      
     
text1 = new Text(shell, SWT.BORDER | SWT.MULTI);      
//text.setTextLimit(12);      
text1.setText(line);      
text2 = new Text(shell, SWT.BORDER | SWT.WRAP);      
text2.setText(line);      
}      
  
public WrapLines() {      
     
shell.setLayout(new GridLayout(2, true));      
(new Label(shell, SWT.NULL)).setText("SWT.BORDER |\nSWT.MUTLI");      
(new Label(shell, SWT.NULL)).setText("SWT.BORDER |\nSWT.WRAP");      
init();      
GridData gridData = new GridData(GridData.FILL_BOTH);      
text1.setLayoutData(gridData);      
     
gridData = new GridData(GridData.FILL_BOTH);      
text2.setLayoutData(gridData);      
  
shell.pack();      
shell.open();      
     
while (!shell.isDisposed()) {      
if (!display.readAndDispatch()) {      
 display.sleep();      
}      
}      
display.dispose();      
}      
  
public static void main(String[] args) {      
new WrapLines();      
}      
}      
  