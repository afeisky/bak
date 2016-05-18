package com.afemail;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmlInfo {

	public String subject = null;
	public InternetAddress[] from = null;
	public InternetAddress[] to = null;
	public String host = null;
	public String content = null;
	public String attach_list = null;
	public InputStream inMsg=null;
	public MimeMessage mmsg=null;
	public String mail_contentType;
	public String savePath=null;
	public String saveFileName=null;
	public boolean sessionDebug=true;
	public String fileExtName=".eml";
	//public ArrayList<String> listAttachPathName= new ArrayList<String>();
	public List<Map<String, Object>> listAttachPathName=new ArrayList<Map<String, Object>>();		
	public EmlInfo(){
		
	}
	
	public EmlInfo(String _savePath){
		savePath=_savePath;		
	}	
	public String getPathFileName(){
    	if (subject==null || subject.isEmpty() ){
    		SimpleDateFormat fmt = new SimpleDateFormat("TEMP_yyyy-MM-dd_HHmmss");
    		saveFileName=fmt.format((new Date()).getTime());
    	}
    	//System.out.println("subject="+subject);
    	//System.out.println("saveFileName="+saveFileName);
		return savePath+"\\"+saveFileName+fileExtName;
	}
	
	public int addAttachFile(String pathname,String filename){
		Map<String, Object> map =null;
		map = new HashMap<String, Object>();
		long filesize=0;
		map.put("pathname",pathname);
		map.put("filename",filename);
		map.put("filesize",filesize);
		map.put("fileicon",null);
		listAttachPathName.add(map);
		return 0;
	}
	public int removeAttachFile(int indexSort){
		listAttachPathName.remove(indexSort);
		return 0;		
	}
	public int removeAttachFile(String pathname){
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<Map<String, Object>> ilist = listAttachPathName.iterator();
		if (listAttachPathName!=null){
			while (ilist.hasNext()){				
				map = ilist.next();
				System.out.println("pathname="+map.get("pathname"));
				if (map.get("pathname")==pathname){
					ilist.remove();
				}
				//Integer currid = (Integer) listAttachPathName.get(position).get("id");		
			}			
		}
		return 0;
	}	
	
	public void java_code_demo(){
		
		boolean debug = Boolean.valueOf("true").booleanValue(); // string -> boolean
	}

}