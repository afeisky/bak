package com.afei;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParseException;

import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.graphics.Image;

public class Eml {

	public InternetAddress[] from = null;
	public InternetAddress[] to = null;
	public String host = "mail.tcl.com";;
	public String attach_list = null;
	public InputStream inMsg=null;
	public MimeMessage mmsg=null;
	private String mail_contentType;
	
	private boolean saveAttachments = false;
	private boolean showStructure = false;
	private int attnum = 1;
	public String eml_subject="";
	public String eml_type="";
	public String eml_content="";
	public String eml_contentType="";
	public String eml_attachment[]=new String[10];	
	public List<Map<String, Object>> listAttach = new ArrayList<Map<String, Object>>();
	
	public Map<String, Object> mapAttach = null;
	public Iterator<Map<String, Object>> ilist = null;

	private int count=0;
	private MimeMessage MimeMsg;
	private Multipart MimeMp;
	private MimeBodyPart MimeBp;
	private Date eml_date;
	private String imageExtName[]={"png","jpg","jpeg","gif","bmp"};
	public boolean isModify=false;
	
	public Eml() {
	
	}

	public int attachBodyPart(MimeBodyPart mbp1,String fileName) throws MessagingException {
		Map<String, Object> map =new HashMap<String, Object>();
		String contentID=mbp1.getContentID();
		pr("getContentType:"+mbp1.getContentType());
		//pr("getContent:"+mbp1.getContent());
		pr("getContentID:"+mbp1.getContentID());
		map.put("MimeBodyPart", mbp1);

		boolean isImage=mbp1.isMimeType("image/*");
		for (int i=0;i<imageExtName.length-1;i++){
			if (contentID!=null)
				if (contentID.lastIndexOf("."+imageExtName[i])>=0){
					isImage=true;
					break;
				}
			if (fileName!=null)
				if (fileName.lastIndexOf("."+imageExtName[i])>=0){
					isImage=true;
					break;
				}			
		}
		
    	if (isImage){
    		fileName=mbp1.getContentID();
    		contentID=fileName;
    		pr(fileName+": has image contentID:"+mbp1.getContentID());
    	}
	    if (fileName==null){	    		
	    	fileName="";
	    	contentID="";
	    	pr(fileName+": is attachment");
	    }
	    map.put("pathFileName", fileName);
	    map.put("fileName", fileName);
	    if (contentID==null){
	    	contentID="";
	    }
		map.put("contentID", contentID);
	    listAttach.add(map);
	    return 0;
	}
	public void setAttachInfo() {
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<Map<String, Object>> ilist = listAttach.iterator();
		MimeBodyPart mbp1;
		if (listAttach != null) {
			while (ilist.hasNext()) {
				map = ilist.next();
				mbp1=(MimeBodyPart) map.get("MimeBodyPart");
				try {
					//Api.log("getContentType:"+mbp1.getContentType());
					//Api.log("getContentID:"+mbp1.getContentID());
					if (map.get("fileName").toString().length()==0){
						//Api.log("----getContent:"+mbp1.getContent());
						if (mbp1.isMimeType("text/html")){
							eml_content= mbp1.getContent().toString();
							ilist.remove();
						}else if (mbp1.isMimeType("text/*")){
							eml_content= mbp1.getContent().toString();
							ilist.remove();
						}
					}
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();	
				}
			}
		}		

	}
	private String image[]={"png","jpg","gif","bmp"};
	public int attachFile(String pathFileName,String fileName) {
		
        MimeBodyPart mbp1 = new MimeBodyPart();
        
        boolean isImage=false;
		int lastDotPos =fileName.lastIndexOf('.');
		if (lastDotPos != -1){
			String extName="xxx";
			extName=fileName.substring(lastDotPos + 1);
			for (int i=0;i<image.length-1;i++){
				Api.log("image[i]:"+image[i]+","+extName);
				if (extName.equals(image[i])){
					Api.log("--> is image");
					isImage=true;
					break;
				}
			}
		}		
        
	    try {
		    Map<String, Object> map =new HashMap<String, Object>();
		    map.put("MimeBodyPart", mbp1);
		    String contentID="";
	    	if (isImage){
		        FileDataSource fds = new FileDataSource(pathFileName);  
		        mbp1.setDataHandler(new DataHandler(fds));  
		        mbp1.setFileName(fds.getName());
		        mbp1.setContentID(fds.getName());
		        mbp1.setHeader("Content-Transfer-Encoding", "base64");
	            Api.log("--mbp1 is "+mbp1.getEncoding());  
	            Api.log("--encode source is : " + MimeUtility.getEncoding(fds));
	            //mbp1.writeTo(System.out);
		        contentID=mbp1.getContentID();
	    	}else{
	    		mbp1.attachFile(pathFileName);
	    		mbp1.setHeader("Content-Transfer-Encoding", "base64");
	    	}
		    map.put("pathFileName", pathFileName);	  	    	
		    map.put("fileName", fileName);//getFileNameInPathFileName(pathFileName));
		    map.put("contentID", contentID);
		    Api.log("getContentType:"+mbp1.getContentType());
		    Api.log("getContent:"+mbp1.getContent());
		    listAttach.add(map);
		    isModify=true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        return 0;  	    
	}

	public int removeAttachFile(int indexSort) {
		listAttach.remove(indexSort);
		isModify=true;
		return 0;
	}

//	public int removeAttachFile(String pathFileName) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		Iterator<Map<String, Object>> ilist = listAttach.iterator();
//		if (listAttach != null) {
//			while (ilist.hasNext()) {
//				map = ilist.next();
//				Api.log("pathFileName=" + map.get("pathFileName"));
//				if (map.get("pathFileName") == pathFileName ) {
//					ilist.remove();
//					isModify=true;
//				}
//				// Integer currid = (Integer)
//				// listAttachPathName.get(position).get("id");
//			}
//		}
//		return 0;
//	}
	
//	public int removeAttach(int indexSort) {
//		listAttach.remove(indexSort);
//		return 0;
//	}
	public String getFileNameInPathFileName(String pathFileName){
		pathFileName.replaceAll("//", "\\");
	    int i=pathFileName.lastIndexOf("\\");
	    if (i>=0)
	    	return pathFileName.substring(0,i);
	    else
	    	return pathFileName;
	}
	public boolean eml_read(String pathFilename) {
		// Get a Properties object
		Properties props = System.getProperties();

		// Get a Session object
		Session session = Session.getInstance(props, null);
		session.setDebug(true);

		
		//ilist = listAttach.iterator();
		try {
			pr("read pathFilename:" +pathFilename);
			MimeMsg = new MimeMessage(session, new BufferedInputStream(new FileInputStream(pathFilename)));
			dumpPart(MimeMsg);
			pr("#############listAttach.size=" + listAttach.size());
			
			Iterator<Map<String, Object>> ilist = listAttach.iterator();
			
			if (listAttach != null) {
				int count=0;
				while (ilist.hasNext()) {
					
					pr("---------"+count+"------------");
					Map<String, Object> map = new HashMap<String, Object>();
					map = ilist.next();
					if (map.get("dirName")!=null)
						pr("dirName:"+ map.get("dirName"));
					if (map.get("fileName")!=null)
						pr("fileName:"+ map.get("fileName"));
					if (map.get("fileType")!=null)
						pr("fileType:"+ map.get("fileType"));	
					if (map.get("fileContent")!=null)
						pr("fileContent:"+ map.get("fileContent"));	
					//if ((Integer)map.get("fileSize")!=-1)
					//	pr("fileSize:"+ map.get("fileSize"));
					if (map.get("strID")!=null)
						pr("strID:"+ map.get("strID"));
					count+=1;
				}
			}
			setAttachInfo();
			isModify=false;
			pr("--Done!----------------------EML2-");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return true;
	}

	public void dumpPart(Part p) throws Exception {
		pr("[dumpPart] 1-->> ");
		if (p instanceof Message){
			dumpEnvelope((Message) p);
		}
		pr("[dumpPart] 2-->> ");
		/**
		 * Dump input stream ..
		 * 
		 * InputStream is = p.getInputStream(); // If "is" is not already
		 * buffered, wrap a BufferedInputStream // around it. if (!(is
		 * instanceof BufferedInputStream)) is = new BufferedInputStream(is);
		 * int c; while ((c = is.read()) != -1) System.out.write(c);
		 * 
		 **/

		String ct = p.getContentType();
		try {
			pr("CONTENT-TYPE: " + (new ContentType(ct)).toString());
		} catch (ParseException pex) {
			pr("BAD CONTENT-TYPE: " + ct);
		}
		String filename = p.getFileName();
		if (filename != null){
			pr("FILENAME: " + filename);
			
		}else{
			pr("FILENAME: is null");
		
		}
		/*
		 * Using isMimeType to determine the content type avoids fetching the
		 * actual content data until we need it.
		 */
		if (p.isMimeType("text/plain")) {
			pr("This is plain text");
			pr("---------------------------");
			// if (!showStructure && !saveAttachments)
			//Api.log((String) p.getContent());
			eml_content=(String) p.getContent();
			eml_contentType=p.getContentType().toString();
			if (p.getContent().toString().length()>0){
				Api.log(eml_content);
				attachBodyPart((MimeBodyPart)p,filename);				
			}
			
		} else if (p.isMimeType("multipart/*")) {
			pr("This is a Multipart");
			pr("---------------------------");
			Multipart mp = (Multipart) p.getContent();
			level++;
			int count = mp.getCount();
			for (int i = 0; i < count; i++){
				pr("getBodyPart------>>>> "+i);
				dumpPart(mp.getBodyPart(i));
				pr("getBodyPart------<<< "+i);
			}
			level--;
		} else if (p.isMimeType("message/rfc822")) {
			pr("This is a Nested Message");
			pr("---------------------------");
			level++;
			dumpPart((Part) p.getContent());
			level--;
		} else {
			pr("------ELSE---------------------");
			// if (!showStructure && !saveAttachments) {
			if (true) {
				/*
				 * If we actually want to see the data, and it's not a MIME type
				 * we know, fetch it and check its Java type.
				 */
				Object o = p.getContent();
				if (o instanceof String) {
					pr("This is a string");
					pr("---------------------------");
					//Api.log(o.toString().substring(50));
					attachBodyPart((MimeBodyPart)p,filename);
					//getAttach(null,filename,"String",o.toString(),-1,o.toString());
					//MimeMp.addBodyPart((MimeBodyPart) p);
				} else if (o instanceof InputStream) {
					pr("This is just an input stream");
					pr("---------------------------");
					InputStream is = (InputStream) o;
					int c;
					//while ((c = is.read()) != -1)
					//	System.out.write(c);
					attachBodyPart((MimeBodyPart)p,filename);
					//getAttach(null,filename,"stream",o.toString(),-1,o.toString());
					//MimeMp.addBodyPart((MimeBodyPart) p);
				} else {
					pr("This is an unknown type");
					pr("--------unknow-------------------");
					pr(o.toString());
					//MimeMp.addBodyPart((MimeBodyPart) p);
				}
			} else {
				// just a separator
				pr("---------------------------");
			}
		}

		/*
		 * If we're saving attachments, write out anything that looks like an
		 * attachment into an appropriately named file. Don't overwrite existing
		 * files to prevent mistakes.
		 */
		if (saveAttachments && level != 0 && p instanceof MimeBodyPart && !p.isMimeType("multipart/*")) {
			String disp = p.getDisposition();
			// many mailers don't include a Content-Disposition
			if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT)) {
				if (filename == null)
					filename = "Attachment" + attnum++;
				pr("Saving attachment to file " + filename);
				try {
					File f = new File(filename);
					if (f.exists())
						// XXX - could try a series of names
						throw new IOException("file exists");
					((MimeBodyPart) p).saveFile(f);
				} catch (IOException ex) {
					pr("Failed to save attachment: " + ex);
				}
				pr("---------------------------");
			}
		}
	}

	public void dumpEnvelope(Message m) throws Exception {
		pr("This is the message envelope");
		pr("-----------dumpEnvelope---------------->>>");
		Address[] a;
		// FROM
		if ((a = m.getFrom()) != null) {
			for (int j = 0; j < a.length; j++)
				pr("FROM: " + a[j].toString());
		}

		// REPLY TO
		if ((a = m.getReplyTo()) != null) {
			for (int j = 0; j < a.length; j++)
				pr("REPLY TO: " + a[j].toString());
		}

		// TO
		if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
			for (int j = 0; j < a.length; j++) {
				pr("TO: " + a[j].toString());
				InternetAddress ia = (InternetAddress) a[j];
				if (ia.isGroup()) {
					InternetAddress[] aa = ia.getGroup(false);
					for (int k = 0; k < aa.length; k++)
						pr("  GROUP: " + aa[k].toString());
				}
			}
		}

		// SUBJECT
		eml_subject=m.getSubject();
		pr("SUBJECT: " + m.getSubject());

		// DATE
		Date d = m.getSentDate();
		eml_date=m.getSentDate();
		pr("SendDate: " + (d != null ? d.toString() : "UNKNOWN"));

		// FLAGS
		Flags flags = m.getFlags();
		StringBuffer sb = new StringBuffer();
		Flags.Flag[] sf = flags.getSystemFlags(); // get the system flags

		boolean first = true;
		for (int i = 0; i < sf.length; i++) {
			String s;
			Flags.Flag f = sf[i];
			if (f == Flags.Flag.ANSWERED)
				s = "\\Answered";
			else if (f == Flags.Flag.DELETED)
				s = "\\Deleted";
			else if (f == Flags.Flag.DRAFT)
				s = "\\Draft";
			else if (f == Flags.Flag.FLAGGED)
				s = "\\Flagged";
			else if (f == Flags.Flag.RECENT)
				s = "\\Recent";
			else if (f == Flags.Flag.SEEN)
				s = "\\Seen";
			else
				continue; // skip it
			if (first)
				first = false;
			else
				sb.append(' ');
			sb.append(s);
		}

		String[] uf = flags.getUserFlags(); // get the user flag strings
		for (int i = 0; i < uf.length; i++) {
			if (first)
				first = false;
			else
				sb.append(' ');
			sb.append(uf[i]);
		}
		pr("FLAGS: " + sb.toString());

		// X-MAILER
		String[] hdrs = m.getHeader("X-Mailer");
		if (hdrs != null)
			pr("X-Mailer: " + hdrs[0]);
		else
			pr("X-Mailer NOT available");
		
//	    MimeMsg.setFrom(from[0]);//msg.setFrom(new InternetAddress(from));
//	    MimeMsg.setRecipients(Message.RecipientType.TO, to);
//	    MimeMsg.setSubject(eml_subject);
//	    MimeMp = new MimeMultipart();
//	    
//	    
//	    // create and fill the first message part
//	    MimeBodyPart mbpContent = new MimeBodyPart();
//	    // 准备邮件正文数据
//	    //text.setContent("这是一封邮件正文带图片<img src='cid:xxx.jpg'>的邮件", "text/html;charset=UTF-8");
//	    Api.log("=================");
//	    Api.log(content);
//	    mbpContent.setText(content, "text/html;charset=UTF-8"); 
//	    MimeMp.addBodyPart(mbpContent);
		pr("----------dumpEnvelope-----------------<<<");
	}

	public String formatAttachHTML(){
		pr("#############listAttach.size=" + listAttach.size());
		
		Iterator<Map<String, Object>> ilist = listAttach.iterator();
		String ret="<table>";
		if (listAttach != null) {
			int count=0;
			while (ilist.hasNext()) {
				ret+="<tr>";
				pr("---------"+count+"------------");
				Map<String, Object> map = new HashMap<String, Object>();
				map = ilist.next();
				ret+="<td>"+map.get("fileName")+"</td>";
				ret+="<td>"+map.get("fileType")+"</td>";				
				count+=1;
				ret+="</tr>";
			}
		}
		ret+="</table>";
		return ret;		
	}
	private String indentStr = "                                               ";
	private int level = 0;
	private boolean sessionDebug=true;

	public void pr(String s) {
		if (Api.DEBUG)
			System.out.print(indentStr.substring(0, level * 4));
		Api.log(s);
	}

	public boolean Save(String pathFileName){
		try {
			Api.log("pathFileName:"+pathFileName);
	    	// create some properties and get the default Session
	    	Properties props = System.getProperties();
	    	props.put("mail.smtp.host", host);	    	
	    	Session session = Session.getInstance(props, null);
	    	//Session session = Session.getDefaultInstance(new Properties());  
	    	session.setDebug(sessionDebug);			
    	    // create a message
    	    MimeMessage MimeMsg = new MimeMessage(session);
			//MimeMsg.setFrom(from[0]);//msg.setFrom(new InternetAddress(from));
			//MimeMsg.setRecipients(Message.RecipientType.TO, to);
    	    Api.log("subject:"+eml_subject);
			MimeMsg.setSubject(eml_subject);
    	    Api.log("=================");
    	    Api.log(eml_content);
			MimeMultipart allPart = new MimeMultipart("mixed");  
			
	        // 用于保存最终正文部分   
	        MimeBodyPart contentBody = new MimeBodyPart();  
	        // 用于组合文本和图片，"related"型的MimeMultipart对象  
	        MimeMultipart contentMulti = new MimeMultipart("related");  
	        // 正文的文本部分  
	        MimeBodyPart textBody = new MimeBodyPart();  
	        textBody.setContent(eml_content, "text/html;charset=gbk");  
	        contentMulti.addBodyPart(textBody);    

    	    // add the Multipart to the message
    	    Map<String, Object> map = new HashMap<String, Object>();
    		Iterator<Map<String, Object>> ilist = listAttach.iterator();
    		if (listAttach!=null){
    			while (ilist.hasNext()){				
    				map = ilist.next();
    				String contentID=(String) map.get("contentID").toString();
    				Api.log("Save fileName="+map.get("fileName").toString()+",contentID:"+contentID);		
    				if (contentID.length()>0){
    					Api.log("is image");
    			        contentMulti.addBodyPart((MimeBodyPart)map.get("MimeBodyPart"));
    				}else{
    					Api.log("is attachment");
    					allPart.addBodyPart((MimeBodyPart)map.get("MimeBodyPart"));
    				}
    			}
    		}    	    
    		// 将上面"related"型的 MimeMultipart 对象作为邮件的正文  
    		contentBody.setContent(contentMulti);  //contentBody has Multi
    		allPart.addBodyPart(contentBody);
    	    MimeMsg.setContent(allPart);
    	    // set the Date: header
    	    MimeMsg.setSentDate(new Date());
    	    /*
    	     * If you want to control the Content-Transfer-Encoding
    	     * of the attached file, do the following.  Normally you
    	     * should never need to do this.
    	     */
    	    FileOutputStream fos=new FileOutputStream(pathFileName);
    	    MimeMsg.saveChanges();    	    
    	    MimeMsg.writeTo(fos);
    	    fos.close();
    	    MimeMsg=null;
    	    // send the message
    	    //Transport.send(msg);
    	    return true;   
    	} catch (MessagingException mex) {
    	    mex.printStackTrace();
    	    Exception ex = null;
    	    if ((ex = mex.getNextException()) != null) {
    		ex.printStackTrace();
    	    }
    	} catch (IOException ioex) {
    	    ioex.printStackTrace();
    	}
		return true;
		
	}
	public boolean newEml(String pathFileName){
		try {
	    	// create some properties and get the default Session
	    	Properties props = System.getProperties();
	    	props.put("mail.smtp.host", host);	    	
	    	Session session = Session.getInstance(props, null);
	    	//Session session = Session.getDefaultInstance(new Properties());  
	    	session.setDebug(sessionDebug);			
    	    // create a message
    	    MimeMsg = new MimeMessage(session);
			//MimeMsg.setFrom(from[0]);//msg.setFrom(new InternetAddress(from));
			//MimeMsg.setRecipients(Message.RecipientType.TO, to);
    	    Api.log("subject:"+eml_subject);
			MimeMsg.setSubject(eml_subject);
    	    Api.log("=================");
    	    Api.log(eml_content);
    	    listAttach = new ArrayList<Map<String, Object>>();
    	    // send the message
    	    //Transport.send(msg);
    	    return true;   
    	} catch (MessagingException mex) {
    	    mex.printStackTrace();
    	    Exception ex = null;
    	    if ((ex = mex.getNextException()) != null) {
    		ex.printStackTrace();
    	    }
    	}
		return true;
		
	}

		public String openAttachFile(String appDir,int index) {	
			String filename="";
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
			String name=appDir+"\\~~TEMP_"+fmt.format((new Date()).getTime());
			Api.log(index+" name:"+name);
    	    Map<String, Object> map = new HashMap<String, Object>();
    		Iterator<Map<String, Object>> ilist = listAttach.iterator();
    		if (listAttach!=null){
    			int n=0;
    			while (ilist.hasNext()){
    				map = ilist.next();
    				if (n==index){
    					MimeBodyPart mbp=(MimeBodyPart)map.get("MimeBodyPart");
    					name=name+map.get("fileName").toString();
    					Api.log(index+"]  name:"+name);
						try {
							
							Object o = mbp.getContent();
							if (o instanceof String) {	
								pr(mbp.getContentType());
								Enumeration en= mbp.getAllHeaderLines();								
								//while (en.hasMoreElements()){
								//	pr(en.nextElement().toString());
								//}
								String str=(String)o.toString();
								pr("a=="+mbp.getDescription());
								Api.log("+++="+mbp.getEncoding()+mbp.getAllHeaderLines());
						        //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name),mbp.getEncoding()));
								FileWriter fw = new FileWriter(name);
								//str.getBytes("gb2312");
								fw.write(str);
								fw.close();						        
								pr("------This is a string!--------------");
								pr(str);
							}else if (o instanceof InputStream) {
								FileOutputStream fos = new FileOutputStream(name);
								//mbp.writeTo(fos);
								pr("------This is just an input stream!----------");
								InputStream is = (InputStream) o;
								int c;
								while ((c = is.read()) != -1){
									//System.out.write(c);
									fos.write(c);
								}
								fos.close();
								Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", name});
					            
							}else {
								FileOutputStream fos = new FileOutputStream(name);
								pr("-----This is an unknown type!-------");
								//pr(o.toString());
								fos.write(o.toString().getBytes());
								fos.close();
							}
							
	    					filename=name;
	    					break;
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();	
						} catch (MessagingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();							
						}    
    				}
    				n+=1;
    			}
    		}  
    		return filename;
			
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
	
	private void func_readme(){
//		MimeBodyPart createAttachment(String fileName) 方法用于创建附件并返回；
//		MimeBodyPart createContent(String body, String fileName) 方法用于创建正文部分并返回；
//		MimeMessage createMessage(Session session) 方法用于调用上面的两个方法生成邮件。
	}
}