package com.afei;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Data {
	public static String filterExtName="*.eml";//"*.aml;*.html;*.htm;*.txt;*.eml";
	public static String fileExtName=".eml";//"*.aml;*.html;*.htm;*.txt;*.eml";
	public static Shell shell;
	
	public Data(){
		
	}
	public static boolean init(Shell _shell){
		shell=_shell;
		shell= new Shell(Display.getDefault());
		return true;		
	}
	
	
}
