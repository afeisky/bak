package StyledText;


import org.eclipse.swt.SWT;      
import org.eclipse.swt.custom.StyleRange;      
import org.eclipse.swt.custom.StyledText;      
import org.eclipse.swt.graphics.Font;      
import org.eclipse.swt.layout.GridData;      
import org.eclipse.swt.layout.GridLayout;      
import org.eclipse.swt.widgets.Display;      
import org.eclipse.swt.widgets.Shell;      
     
public class SetLineBackground {      
       
 Display display = new Display();      
 Shell shell = new Shell(display);      
       
 StyledText styledText;      
     
 public SetLineBackground() {      
     
  shell.setLayout(new GridLayout());      
  styledText = new StyledText(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL);      
  styledText.setLayoutData(new GridData(GridData.FILL_BOTH));      
  Font font = new Font(shell.getDisplay(), "Courier New", 12, SWT.NORMAL);      
  styledText.setFont(font);      
  styledText.setText("abcdefg\r\nhijklmn");      
  StyleRange styleRange1 = new StyleRange();      
  styleRange1.start = 2;      
  styleRange1.length = 3;      
  styleRange1.foreground = shell.getDisplay().getSystemColor(SWT.COLOR_BLUE);      
  styleRange1.background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);      
  styleRange1.fontStyle = SWT.BOLD;        
        
  styledText.setStyleRange(styleRange1);      
  styledText.setLineBackground(0, 1, shell.getDisplay().getSystemColor(SWT.COLOR_GREEN));      
  styledText.setLineBackground(1, 1, shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW));      
        
  shell.setSize(300, 120);      
  shell.open();      
  while (!shell.isDisposed()) {      
   if (!display.readAndDispatch()) {      
    display.sleep();      
   }      
  }      
  display.dispose();      
 }      
       
 public static void main(String[] args) {      
  new SetLineBackground();      
 }      
}      
     