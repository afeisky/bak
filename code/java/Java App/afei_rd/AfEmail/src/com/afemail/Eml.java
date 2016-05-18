package com.afemail;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

public class Eml {

	public String subject = null;
	public Address[] from = null;
	public String[] to = null;
	public String content = null;
	public String attach_list = null;
	public InputStream inMsg=null;
	public MimeMessage mmsg=null;
	private String mail_contentType;
	public Eml() {
	
	}
	private boolean isMimeType(MimeMessage mmsg,String keyword){
		try {
			//return msg.isMimeType("multipart/*") || msg.isMimeType("MULTIPART/*")
			return mmsg.isMimeType(keyword.toLowerCase()) || mmsg.isMimeType(keyword.toUpperCase());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public void get(String pathname) {
		try {
			// pathname="G:\\11\\[HZ SWD3 RELEASE] Pixi4-55_3G EU 2SIM Internal
			System.out.println("pathname : " + pathname);

			Properties props = new Properties();  
	        Session session = Session.getDefaultInstance(props, null);  
	        InputStream inMsg;  
	        inMsg = new FileInputStream(pathname);  
	        MimeMessage msg = new MimeMessage(session, inMsg);  
	        parseEml(msg);  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void parseEml(MimeMessage msg) {
		try {

			String[] date = msg.getHeader("Date");

			subject = msg.getSubject();
			from = msg.getFrom();
			
			Address[] froms = msg.getFrom();  
	        if (froms != null) {  
	            // System.out.println("发件人信息:" + froms[0]);  
	            InternetAddress addr = (InternetAddress) froms[0];  
	            System.out.println("发件人地址:" + addr.getAddress());  
	            System.out.println("发件人显示名:" + addr.getPersonal());  
	        }  
	        System.out.println("邮件主题:" + msg.getSubject());  
			System.out.println("邮件类型:" + msg.getContentType());
			mail_contentType=msg.getContentType();
			if (mail_contentType.contains("multipart/alternative"))	{
				System.out.println("size:" + msg.getSize());
				System.out.println("tostring:" + msg.toString());
				//Object o = msg.getContent();
				Part messagePart=msg;//获取信息对象

			}else {// multipart/related , multipart/mixe
		        Object o = msg.getContent(); // getContent() 是获取包裹内容, Part相当于外包装  
    
		        if (o instanceof Multipart) {  
		        	System.out.println("o instanceof Multipart");
		            Multipart multipart = (Multipart) o;  
		            //reMultipart(multipart);  
		        } else if (o instanceof Part) {  
		        	System.out.println("o instanceof Part");
		            Part part = (Part) o;  
		            //rePart(part);  
		        } else {  
		            System.out.println("类型" + msg.getContentType());  
		            System.out.println("内容" + msg.getContent());  
		        }  
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void rePart(Part part) throws Exception {  
		  
        if (part.getDisposition() != null) {  
  
            String strFileNmae = part.getFileName();  
            if(!strFileNmae.isEmpty()) // if(!StringUtils.isEmpty(strFileNmae))  
            {   // MimeUtility.decodeText解决附件名乱码问题  
                strFileNmae=MimeUtility.decodeText(strFileNmae);  
                System.out.println("发现附件: "+ strFileNmae);  
                  
                InputStream in = part.getInputStream();// 打开附件的输入流  
                // 读取附件字节并存储到文件中  
                java.io.FileOutputStream out = new FileOutputStream(strFileNmae);  
                int data;  
                while ((data = in.read()) != -1) {  
                    out.write(data);  
                }  
                in.close();  
                out.close();  
                  
            }  
              
            System.out.println("内容类型: "+ MimeUtility.decodeText(part.getContentType()));  
            System.out.println("附件内容:" + part.getContent());  
              
              
        } else {  
            if (part.getContentType().startsWith("text/plain")) {  
                System.out.println("文本内容：" + part.getContent());  
            } else {  
                // System.out.println("HTML内容：" + part.getContent());  
            }  
        }  
    }  
  
    /** 
     * @param multipart 
     *            // 接卸包裹（含所有邮件内容(包裹+正文+附件)） 
     * @throws Exception 
     */  
    private static void reMultipart(Multipart multipart) throws Exception {  
        // System.out.println("邮件共有" + multipart.getCount() + "部分组成");  
        // 依次处理各个部分  
        for (int j = 0, n = multipart.getCount(); j < n; j++) {  
            // System.out.println("处理第" + j + "部分");  
            Part part = multipart.getBodyPart(j);// 解包, 取出 MultiPart的各个部分,  
                                                    // 每部分可能是邮件内容,  
            // 也可能是另一个小包裹(MultipPart)  
            // 判断此包裹内容是不是一个小包裹, 一般这一部分是 正文 Content-Type: multipart/alternative  
            if (part.getContent() instanceof Multipart) {  
                Multipart p = (Multipart) part.getContent();// 转成小包裹  
                // 递归迭代  
                reMultipart(p);  
            } else {  
                rePart(part);  
            }  
        }  
    }  
    public static String getMailContent(Part part) throws Exception {  
        String contenttype = part.getContentType();  
        int nameindex = contenttype.indexOf("name");  
        boolean conname = false;  
        if (nameindex != -1) {  
            conname = true;  
        }  
        StringBuilder bodytext = new StringBuilder();  
        if (part.isMimeType("text/plain") && !conname) {  
            bodytext.append((String) part.getContent());  
        } else if (part.isMimeType("text/html") && !conname) {  
            bodytext.append((String) part.getContent());  
        } else if (part.isMimeType("multipart/*")) {  
            Multipart multipart = (Multipart) part.getContent();  
            int counts = multipart.getCount();  
            for (int i = 0; i < counts; i++) {  
                getMailContent(multipart.getBodyPart(i));  
            }  
        } else if (part.isMimeType("message/rfc822")) {  
            getMailContent((Part) part.getContent());  
        } else {  
        }  
        return bodytext.toString();  
    }  
    
    //----------------------------------
    
	public void getPart(Part p) throws Exception {
		if (p instanceof Message) {
			// Call methos writeEnvelope
			System.out.println("--------111111--------------------");
			writeEnvelope((Message) p);
		}

		System.out.println("----------------------------");
		System.out.println("CONTENT-TYPE: " + p.getContentType());

		// check if the content is plain text
		if (p.isMimeType("text/plain")) {
			System.out.println("This is plain text");
			System.out.println("---------------------------");
			System.out.println((String) p.getContent());
		}
		// check if the content has attachment
		else if (p.isMimeType("multipart/*")) {
			System.out.println("This is a Multipart");
			System.out.println("---------------------------");
			Multipart mp = (Multipart) p.getContent();
			int count = mp.getCount();
			for (int i = 0; i < count; i++)
				writePart(mp.getBodyPart(i));
		}
		// check if the content is a nested message
		else if (p.isMimeType("message/rfc822")) {
			System.out.println("This is a Nested Message");
			System.out.println("---------------------------");
			writePart((Part) p.getContent());
		}
		// check if the content is an inline image
		else if (p.isMimeType("image/jpeg")) {
			System.out.println("--------> image/jpeg");
			Object o = p.getContent();

			InputStream x = (InputStream) o;
			// Construct the required byte array
			System.out.println("x.length = " + x.available());
			int i = 0;
			byte[] bArray = new byte[x.available()];

			while ((i = x.available()) > 0) {
				int result = (x.read(bArray));
				if (result == -1)
					break;
			}
			FileOutputStream f2 = new FileOutputStream("/tmp/image.jpg");
			f2.write(bArray);
		} else if (p.getContentType().contains("image/")) {
			System.out.println("content type" + p.getContentType());
			File f = new File("image" + new Date().getTime() + ".jpg");
			DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
			com.sun.mail.util.BASE64DecoderStream test = (com.sun.mail.util.BASE64DecoderStream) p.getContent();
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = test.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
		} else {
			Object o = p.getContent();
			if (o instanceof String) {
				System.out.println("This is a string");
				System.out.println("---------------------------");
				System.out.println((String) o);
			} else if (o instanceof InputStream) {
				System.out.println("This is just an input stream");
				System.out.println("---------------------------");

				File f = new File("G:\\11\\" + p.getFileName());// "image" + new
																// Date().getTime()
																// + ".jpg");
				System.out.println("==>G:\\11\\" + p.getFileName());
				DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
				InputStream is = (InputStream) o;
				is = (InputStream) o;
				int c;
				while ((c = is.read()) != -1) {
					//System.out.write(c);
					output.write(c);
				}
				output.close();
			} else {
				System.out.println("This is an unknown type");
				System.out.println("---------------------------");
				System.out.println(o.toString());
			}
		}

	}

	
	public void test(String pathname) {
		try {

			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			InputStream inMsg;
			// pathname="G:\\11\\[HZ SWD3 RELEASE] Pixi4-55_3G EU 2SIM Internal
			// PA Version v7G41 Delivery!.eml";
			inMsg = new FileInputStream(pathname);

			MimeMessage msg = new MimeMessage(session, inMsg);
			String[] date = msg.getHeader("Date");
			// String[] from=
			// String[] to=
			System.out.println("Subject : " + msg.getSubject());
			System.out.println("From : " + msg.getFrom()[0]);
			System.out.println("--------------");
			System.out.println("Body : " + msg.getContent());

			if (msg.isMimeType("multipart/*") || msg.isMimeType("MULTIPART/*")) {
				System.out.println("2222222");
				Multipart mp = (Multipart) msg.getContent();

				int totalAttachments = mp.getCount();
				if (totalAttachments > 0) {
					System.out.println("333333333");
					for (int i = 0; i < totalAttachments; i++) {
						System.out.println("444 [" + i + "]");
						Part part = mp.getBodyPart(i);

						String attachFileName = part.getFileName();
						String disposition = part.getDisposition();
						String contentType = part.getContentType();
						System.out.println("444 attachFileName=" + attachFileName + "]");
						System.out.println("444 disposition=" + disposition + "]");
						System.out.println("444 contentType=" + contentType + "]");
						// String s = getMailContent1(part);
						// System.out.println(s);
						writePart(part);

						if ((attachFileName != null && attachFileName.endsWith(".ics"))
								|| contentType.indexOf("text/calendar") >= 0) {
							System.out.println("555 ");
							String[] dateHeader = msg.getHeader("date");
						}
					}
					inMsg.close();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int fs_write(String fileName, String text) {
		int ret = 0;
		try {
			FileOutputStream fout = new FileOutputStream(fileName);
			byte[] bytes = text.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
			ret = -1;
		}
		return ret;
	}

	public String getMailContent1(Part part) throws Exception {
		String contenttype = part.getContentType();
		int nameindex = contenttype.indexOf("name");
		boolean conname = false;
		if (nameindex != -1) {
			conname = true;
		}
		StringBuilder bodytext = new StringBuilder();

		if (part.isMimeType("text/plain") && !conname) {
			System.out.println("77777777 ");
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("text/html") && !conname) {
			System.out.println("888 ");
			bodytext.append((String) part.getContent());
			System.out.println(part.getContent());
		} else if (part.isMimeType("multipart/*")) {
			System.out.println("9999");
			Multipart multipart = (Multipart) part.getContent();
			int counts = multipart.getCount();
			for (int i = 0; i < counts; i++) {
				System.out.println("999  " + i);
				getMailContent1(multipart.getBodyPart(i));
			}
		} else if (part.isMimeType("message/rfc822")) {
			System.out.println("1000 ");
			getMailContent1((Part) part.getContent());
		} else {
			System.out.println("111111 " + part.getSize());
			String fullFileName = "G:\\11\\" + part.getFileName();
			// fs_write("G:\\11\\"+part.getFileName(),
			// part.getContent().toString());
			// BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new
			// FileOutputStream(fullFileName), "UTF-8"));
			// bw.write(part.writeTo(arg0););
			// bw.flush();
			// bw.close();

			// writeTo(new FileOutputStream(fullFileName));

		}

		return bodytext.toString();
	}

	/*
	 * This method checks for content-type based on which, it processes and
	 * fetches the content of the message
	 */
	public void writePart(Part p) throws Exception {
		if (p instanceof Message) {
			// Call methos writeEnvelope
			System.out.println("--------111111--------------------");
			writeEnvelope((Message) p);
		}

		System.out.println("----------------------------");
		System.out.println("CONTENT-TYPE: " + p.getContentType());

		// check if the content is plain text
		if (p.isMimeType("text/plain")) {
			System.out.println("This is plain text");
			System.out.println("---------------------------");
			System.out.println((String) p.getContent());
		}
		// check if the content has attachment
		else if (p.isMimeType("multipart/*")) {
			System.out.println("This is a Multipart");
			System.out.println("---------------------------");
			Multipart mp = (Multipart) p.getContent();
			int count = mp.getCount();
			for (int i = 0; i < count; i++)
				writePart(mp.getBodyPart(i));
		}
		// check if the content is a nested message
		else if (p.isMimeType("message/rfc822")) {
			System.out.println("This is a Nested Message");
			System.out.println("---------------------------");
			writePart((Part) p.getContent());
		}
		// check if the content is an inline image
		else if (p.isMimeType("image/jpeg")) {
			System.out.println("--------> image/jpeg");
			Object o = p.getContent();

			InputStream x = (InputStream) o;
			// Construct the required byte array
			System.out.println("x.length = " + x.available());
			int i = 0;
			byte[] bArray = new byte[x.available()];

			while ((i = x.available()) > 0) {
				int result = (x.read(bArray));
				if (result == -1)
					break;
			}
			FileOutputStream f2 = new FileOutputStream("/tmp/image.jpg");
			f2.write(bArray);
		} else if (p.getContentType().contains("image/")) {
			System.out.println("content type" + p.getContentType());
			File f = new File("image" + new Date().getTime() + ".jpg");
			DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
			com.sun.mail.util.BASE64DecoderStream test = (com.sun.mail.util.BASE64DecoderStream) p.getContent();
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = test.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
		} else {
			Object o = p.getContent();
			if (o instanceof String) {
				System.out.println("This is a string");
				System.out.println("---------------------------");
				System.out.println((String) o);
			} else if (o instanceof InputStream) {
				System.out.println("This is just an input stream");
				System.out.println("---------------------------");

				File f = new File("G:\\11\\" + p.getFileName());// "image" + new
																// Date().getTime()
																// + ".jpg");
				System.out.println("==>G:\\11\\" + p.getFileName());
				DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
				InputStream is = (InputStream) o;
				is = (InputStream) o;
				int c;
				while ((c = is.read()) != -1) {
					System.out.write(c);
					output.write(c);
				}
				output.close();
			} else {
				System.out.println("This is an unknown type");
				System.out.println("---------------------------");
				System.out.println(o.toString());
			}
		}

	}

	/*
	 * This method would print FROM,TO and SUBJECT of the message
	 */
	public void writeEnvelope(Message m) throws Exception {
		System.out.println("This is the message envelope");
		System.out.println("-------------??--------------");
		Address[] a;

		// FROM
		if ((a = m.getFrom()) != null) {
			for (int j = 0; j < a.length; j++)
				System.out.println("FROM: " + a[j].toString());
		}

		// TO
		if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
			for (int j = 0; j < a.length; j++)
				System.out.println("TO: " + a[j].toString());
		}

		// SUBJECT
		if (m.getSubject() != null)
			System.out.println("SUBJECT: " + m.getSubject());
		System.out.println("---------====---------------");
	}
	
	public String getMailAddress(String type) throws Exception {
		  String mailaddr = "";
		  String addtype = type.toUpperCase();
		  InternetAddress[] address = null;
		  if (addtype.equals("TO") || addtype.equals("CC")
		    || addtype.equals("BCC")) {
		   if (addtype.equals("TO")) {
		    address = (InternetAddress[]) mmsg
		      .getRecipients(Message.RecipientType.TO);
		   } else if (addtype.equals("CC")) {
		    address = (InternetAddress[]) mmsg
		      .getRecipients(Message.RecipientType.CC);
		   } else {
		    address = (InternetAddress[]) mmsg
		      .getRecipients(Message.RecipientType.BCC);
		   }
		   if (address != null) {
		    for (int i = 0; i < address.length; i++) {
		     String email = address[i].getAddress();
		     if (email == null)
		      email = "";
		     else {
		      email = MimeUtility.decodeText(email);
		     }
		     String personal = address[i].getPersonal();
		     if (personal == null)
		      personal = "";
		     else {
		      personal = MimeUtility.decodeText(personal);
		     }
		     String compositeto = personal + "<" + email + ">";
		     mailaddr += "," + compositeto;
		    }
		    mailaddr = mailaddr.substring(1);
		   }
		  } else {
		   throw new Exception("Error emailaddr type!");
		  }
		  return mailaddr;
		 }
}