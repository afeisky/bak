    import org.eclipse.swt.SWT;  
    import org.eclipse.swt.browser.Browser;  
    import org.eclipse.swt.browser.CloseWindowListener;  
    import org.eclipse.swt.browser.OpenWindowListener;  

    import org.eclipse.swt.browser.VisibilityWindowListener;  
    import org.eclipse.swt.browser.WindowEvent;  

    import org.eclipse.swt.graphics.Point;  
    import org.eclipse.swt.layout.FillLayout;  
    import org.eclipse.swt.layout.GridData;  
    import org.eclipse.swt.layout.GridLayout;  

    import org.eclipse.swt.widgets.Composite;  
    import org.eclipse.swt.widgets.Display;  

    import org.eclipse.swt.widgets.Label;  

    import org.eclipse.swt.widgets.Shell;  
    import org.eclipse.swt.widgets.Text;  

      
    public class SWTBrowser {  
      
        Display display = new Display();  
        Shell shell = new Shell(display);  
        Text textLocation;  
        Browser browser;  
        Label labelStatus;  
      
        public SWTBrowser() {  
      
            shell.setLayout(new GridLayout());  

            Composite compositeLocation = new Composite(shell, SWT.NULL);  
            compositeLocation.setLayout(new GridLayout(3, false));  
            compositeLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            
            
            browser = new Browser(shell, SWT.BORDER);  
            browser.setLayoutData(new GridData(GridData.FILL_BOTH));  
            Composite compositeStatus = new Composite(shell, SWT.NULL);  
            compositeStatus.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));  
            compositeStatus.setLayout(new GridLayout(2, false));  
           
          
            //initialize(display, browser);  
            shell.setSize(500, 400);  
            shell.open();  
            //browser.setUrl("https://www.baidu.com");
            //browser.refresh();
            browser.setText("<html><body><h1>SWT & JFace </h1></body/html>");  
            while (!shell.isDisposed()) {  
                if (!display.readAndDispatch()) {  
                    display.sleep();  
                }  
            }  
            display.dispose();  
        }  
      
        static void initialize(final Display display, Browser browser) {  
            browser.addOpenWindowListener(new OpenWindowListener() {  
                public void open(WindowEvent event) {  
                    Shell shell = new Shell(display);  
                    shell.setText("New Window");  
                    shell.setLayout(new FillLayout());  
                    Browser browser = new Browser(shell, SWT.NONE);  
                    initialize(display, browser);  
                    event.browser = browser;  
                }  
            });  
            browser.addVisibilityWindowListener(new VisibilityWindowListener() {  
                public void hide(WindowEvent event) {  
                    Browser browser = (Browser) event.widget;  
                    Shell shell = browser.getShell();  
                    shell.setVisible(false);  
                }  
      
                public void show(WindowEvent event) {  
                    Browser browser = (Browser) event.widget;  
                    Shell shell = browser.getShell();  
                    if (event.location != null)  
                        shell.setLocation(event.location);  
                    if (event.size != null) {  
                        Point size = event.size;  
                        shell.setSize(shell.computeSize(size.x, size.y));  
                    }  
                    shell.open();  
                }  
            });  
            browser.addCloseWindowListener(new CloseWindowListener() {  
                public void close(WindowEvent event) {  
                    Browser browser = (Browser) event.widget;  
                    Shell shell = browser.getShell();  
                    shell.close();  
                }  
            });  
        }  
      
        public static void main(String[] args) {  
            new SWTBrowser();  
        }  
    }  