package StyledText;

import org.eclipse.swt.SWT;      
import org.eclipse.swt.custom.LineBackgroundEvent;      
import org.eclipse.swt.custom.LineBackgroundListener;      
import org.eclipse.swt.custom.StyledText;      
import org.eclipse.swt.layout.GridData;      
import org.eclipse.swt.layout.GridLayout;      
import org.eclipse.swt.widgets.Display;      
import org.eclipse.swt.widgets.Shell;      
   
public class HighlightOddLine {      
     
Display display = new Display();      
Shell shell = new Shell(display);      
     
StyledText styledText;      
     
public HighlightOddLine() {      
      
shell.setLayout(new GridLayout());      
styledText = new StyledText(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);      
styledText.setLayoutData(new GridData(GridData.FILL_BOTH));      
styledText.addLineBackgroundListener(new LineBackgroundListener() {      
 public void lineGetBackground(LineBackgroundEvent event) {      
  if(styledText.getLineAtOffset(event.lineOffset) % 2 == 1)      
   event.lineBackground = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);      
 }      
});      
      
styledText.setText("Line 0\r\nLine 1\r\nLine 2\r\nLine 3\r\nLine 4\r\nLine 5\r\nLine 6");      
   
shell.setSize(300, 150);      
shell.open();      
   
while (!shell.isDisposed()) {      
 if (!display.readAndDispatch()) {      
  display.sleep();      
 }      
}      
display.dispose();      
}      
   
public static void main(String[] args) {      
new HighlightOddLine();      
}      
}      