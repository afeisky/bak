package com.afei;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;

public class Api {
	//---
	private ImageRegistry imageRegistry;

	private Table table;
	private static Shell shell=null;
	static Image iconFolder;
	static Image iconFile;
	
	
	public static void initImageResourse(Shell _shell){
		shell=_shell;
		iconFolder = new Image(shell.getDisplay(), "C:/icons/web/go.gif");
		iconFile = new Image(shell.getDisplay(), "C:/icons/web/go.gif");
	}
	public Image getIcon(File file) {
		if (shell==null){
			return null;
		}		
		if (file.isDirectory())
			return iconFolder;
		int lastDotPos = file.getName().indexOf('.');
		if (lastDotPos == -1)
			return iconFile;
		Image image = getIcon(file.getName().substring(lastDotPos + 1));
		return image == null ? iconFile : image;
	}
	public Image getFileIconByFileExtName(String FileName) {
		if (shell==null){
			return null;
		}		
		int lastDotPos =FileName.indexOf('.');
		if (lastDotPos == -1)
			return iconFile;
		Image image = getIcon(FileName.substring(lastDotPos + 1));
		return image == null ? iconFile : image;
	}	

	public Image getIcon(String extension) {
		if (shell==null){
			return null;
		}
		if (imageRegistry == null)
			imageRegistry = new ImageRegistry();
		Image image = imageRegistry.get(extension);
		if (image != null)
			return image;
		System.out.println("extension:"+extension);
		Program program = Program.findProgram(extension);
		ImageData imageData = (program == null ? null : program.getImageData());
		if (imageData != null) {
			image = new Image(shell.getDisplay(), imageData);
			imageRegistry.put(extension, image);
		} else {
			image = iconFile;
		}
		return image;
	}
	
	public static void Copy(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					//System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		} catch (Exception e) {
			System.out.println("Error!!!");
			e.printStackTrace();
		}
	}
	
	public static StyleRange getBackHighlightStyle(int startOffset, int length,int color) {      
	        
		  StyleRange styleRange = new StyleRange();      
		  styleRange.start = startOffset;      
		  styleRange.length = length;      
		  styleRange.background = shell.getDisplay().getSystemColor(color);//SWT.COLOR_YELLOW
		  return styleRange;      
		 }    
	public static StyleRange getForeHighlightStyle(int startOffset, int length,int color) {      
        
		  StyleRange styleRange = new StyleRange();      
		  styleRange.start = startOffset;      
		  styleRange.length = length;      
		  styleRange.foreground = shell.getDisplay().getSystemColor(color);//SWT.COLOR_YELLOW
		  return styleRange;      
		 }    	
    public static String inputStream2String(InputStream is, String charset) {  
        ByteArrayOutputStream baos = null;
        try {  
            baos = new ByteArrayOutputStream();  
            int i = -1;  
            while ((i = is.read()) != -1) {  
                baos.write(i);  
            }  
            return baos.toString(charset);  
        } catch (IOException e) {  
            e.printStackTrace(); 
              
        } finally {  
            if (null != baos) {  
                try {  
                    baos.close();  
                } catch (IOException e) {  
                    e.printStackTrace();
                }  
                baos = null;  
            }  
        }  
        return null;  
    }  	
    public static String inputStream2String(InputStream is){
	   BufferedReader in = new BufferedReader(new InputStreamReader(is));
	   StringBuffer buffer = new StringBuffer();
	   String line = "";
	   try {
			while ((line = in.readLine()) != null){
			     buffer.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	   return buffer.toString();
    	}
	public static String openDialog(String title,String Fillter){
		String extName=Data.fileExtName;
		FileDialog dialog = new FileDialog (Data.shell, SWT.OPEN);  
        dialog.setText(title);  
        //dialog.setFilterNames(new String[] {"AMail Files ("+filterExtName+")","All Files (*.*)"});  
        dialog.setFilterNames(new String[]  {"Files ("+Data.filterExtName+")"});
        dialog.setFilterExtensions(new String[] {Data.filterExtName});  	
        String filePath = dialog.open();
        System.out.println("filePath filePath ="+filePath);
        System.out.println("filePath getFileName ="+dialog.getFileName());
        if(dialog!=null && filePath!=null){
        	return filePath;
        }
        return "";
	}
	
	public static String saveDialog(String title,String Fillter){
		String extName=Data.fileExtName;
		FileDialog dialog = new FileDialog (Data.shell, SWT.OPEN);  
        dialog.setText(title);  
        //dialog.setFilterNames(new String[] {"AMail Files ("+filterExtName+")","All Files (*.*)"});  
        dialog.setFilterNames(new String[] {"Files ("+Data.filterExtName+")"});
        dialog.setFilterExtensions(new String[] {Data.filterExtName});
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

	private void dialog1(FileDialog dialog){
		boolean done = false;  
		MessageBox mg = new MessageBox(dialog.getParent(), SWT.ICON_WARNING| SWT.YES);
		
        mg.setText("Hint");  
        mg.setMessage("coding...");  
        done = mg.open() == SWT.YES;  
        done = true;  
	}
	
}
