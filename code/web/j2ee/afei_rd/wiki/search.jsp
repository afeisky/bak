<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.FileNotFoundException"%>
<%@ page import="java.io.FileReader"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.regex.Matcher"%>
<%@ page import="java.util.regex.Pattern"%>
<%@ page import="java.io.BufferedReader"%>

<%
	request.setCharacterEncoding("utf-8");
	String relativelyPath =request.getSession().getServletContext().getRealPath("/");// System.getProperty("user.dir");//
	String rootDoc = "doc";
	String PATH=relativelyPath +rootDoc;
	String urlRoot= request.getScheme()+"://"+request.getHeader("host");
	String urlApp= urlRoot+request.getContextPath();
    String urlStr= urlRoot+request.getRequestURI();	
	String keyword = request.getParameter("k");
%>	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<link rel="shortcut icon" type="image/x-icon" href="./images/favicon.png" media="screen" /> 
<title>File Search</title>
<style type="text/css">
html, body {
	height: 100%;
	margin: 0 auto;
	padding: 0;
}
a img {
	border: none;
}
a:link {
text-decoration: none;
}
a:visited {
text-decoration: none;
}
a:hover {
text-decoration: none;
}
a:active {
text-decoration: none;
}

tr.folder{
	background: #99cccc;
}
tr.file{
	background: #eeeeee;
}
</style>

<script type="text/javascript">
<!--
	
//-->
</script>
</head>
<body>
<div width="90%" align="center" style=" background:#99ccff; color: #002c57">
<form name="form1" action="search.jsp" method="post">
<input type="text" name="k"><input type="submit" value="search">
</form>
</div>
<%
	if (keyword==null){
		return;
	} 
%>
<div width="90%"  align="center">	
<div id="filelist"  align="left" >	
	<%         
	  String searchStr=keyword;//"system";
        int keywordLength=searchStr.length();
        int keywordExtLen=12;
    	File searchPath = new File(PATH);
    	if(!searchPath.exists() || !searchPath.isDirectory()){
    		out.println("Error, It is  ["+searchPath+"]");
    		return;
    	}	
    	List<String> listA=new ArrayList<String>();
    	List<String> listFile=new ArrayList<String>();		
		Boolean isGoon=true;
		File flist1[] = searchPath.listFiles();
		 for (int i = 0; i < flist1.length; i++) {
			File fs1 = flist1[i];
			if (fs1.isDirectory()) {
				String dir1=fs1.getName();				
				File flist2[] = fs1.listFiles();
				for (int j = 0; j < flist2.length; j++) {
					File fs2 = flist2[j];						
					if (fs2.isDirectory()) {
						String dir2=dir1+"/"+fs2.getName();
						File flist3[] = fs2.listFiles();
						for (int k = 0; k < flist3.length; k++) {
							File fs3 = flist3[k];
							if (fs3.isDirectory()) {
								String dir3=dir2+"/"+fs3.getName();
								File flist4[] = fs3.listFiles();
								for (int n = 0; n < flist4.length; n++) {
									File fs4 = flist4[n];
									if (fs4.isDirectory()) {
										//String dir4=dir3+"/"+fs4.getName();													
									} else {
										listFile.add(dir3+"/"+fs4.getName());
									}
								}									
							} else {
								listFile.add(dir2+"/"+fs3.getName());
							}
						}
					} else {
						listFile.add(dir1+"/"+fs2.getName());
					}
				}
			}else{
				listFile.add(fs1.getName());
			}
		 }
		 for (String f : listFile) {
			 	//out.println("###"+f);
	            File file=new File(PATH+"/"+f);
				try {
					FileReader fr = new FileReader(file);
					BufferedReader br = new BufferedReader(fr);
					String s = br.readLine();
					Pattern p = Pattern.compile(searchStr);//Pattern.compile("\\W(s.([0-9])*?)\\W");
					Matcher m = p.matcher(s);
					String result="";
					int n=0;
					while (s!=null){
						//out.println(s);
						while (m.find()) {
							//out.println(m.group(0).substring(0, searchStr.length()));//得到第0组——整个匹配
							String name=m.group(0).substring(0, searchStr.length());//Integer.parseInt(m.group(1).substring(1, 8));
							if (result.length()>0){
								result+="...";
							}
							result+=name;
						}//???
						if (result.length()==0){
							int pos=s.indexOf(searchStr);
							if (pos>=0){
								if (result.length()>0){
									result+="...";
								}
								int lenS=s.length();
								int lenLeft=(pos<keywordExtLen)?pos:keywordExtLen;
								int lenRight=(lenS<(pos+keywordLength+keywordExtLen))?lenS:(pos+keywordLength+keywordExtLen);								
								result+=s.substring(lenLeft, lenRight);
								//out.println("!!!"+s.substring(lenLeft, lenRight));
								n+=1;
							}
						}			
						if (n>2) 
							break;
						s= br.readLine();
					}					
					br.close();
					if (result.length()>0){
						//out.println("["+file.getAbsolutePath().replace(PATH+"\\","")+"]");
						listA.add(file.getName()+"\r\n"+file.getAbsolutePath().replace(PATH+"\\","").replace("\\"+file.getName(), "")+"\r\n"+result);
					}else{
						if (file.getName().indexOf(searchStr)>=0){
							listA.add(file.getName()+"\r\n"+file.getAbsolutePath().replace(PATH+"\\","").replace("\\"+file.getName(), "")+"\r\n"+file.getName());
						}
					}
				} catch (FileNotFoundException e1) {
					//out.println("File not found: " + file.getAbsolutePath()+"/"+file.getName());
				} catch (IOException e2) {
					e2.printStackTrace();
				}
	       }		 
		 for (String f : listA) {
			 //out.println("["+f+"]");
			 int i=f.indexOf("\r\n");
			 String name=f.substring(0,i);
			 String str=f.substring(i+2, f.length());
			 i=str.indexOf("\r\n");
			 String path=str.substring(0,i);
			 out.println("<div><a href='wiki.jsp?d="+path+"&f="+name+"' target='_blank'>"+name+"</a></div>");
			 out.println("<div>"+str.substring(i, str.length())+"</div><p>");
		 }
	%>
</div>	
 </div>
</body>
</html>