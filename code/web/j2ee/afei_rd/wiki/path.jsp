<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	String relativelyPath =request.getSession().getServletContext().getRealPath("/");// System.getProperty("user.dir");//
	String rootDoc = "doc";
	String PATH=relativelyPath +rootDoc;
	String docPath= request.getScheme()+"://"+request.getHeader("host")+request.getContextPath() +rootDoc;
	String urlRoot= request.getScheme()+"://"+request.getHeader("host");
	String urlApp= urlRoot+request.getContextPath();
     String urlStr= urlRoot+request.getRequestURI();
	request.setCharacterEncoding("utf-8");
	String pathname = request.getParameter("d");
	String filename = request.getParameter("f");
	String text = request.getParameter("editor1");		
	String editvalue= request.getParameter("e");
     out.println(System.getProperty("user.dir"));   out.println("</br>");
	  out.println(request.getSession().getServletContext().getRealPath("/")); out.println("</br>");
	   out.println(request.getScheme()); out.println("</br>");
	    out.println("host:"+request.getHeader("host"));  out.println("</br>");
		 out.println("request.getRequestURI():"+request.getRequestURI()); out.println("</br>");
		  out.println("request.getContextPath():"+request.getContextPath());  out.println("</br>");
		out.println(request.getSession().getServletContext().getRealPath(request.getRequestURI()));  out.println("</br>"); 
     out.println( request.getScheme()+"://"+request.getHeader("host")+request.getContextPath());  out.println("</br>"); 
out.println( urlRoot);  out.println("</br>"); 
out.println( urlApp);  out.println("</br>"); 
out.println( urlStr);  out.println("</br>"); 
String path = request.getContextPath();  
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";  
String remoteAddress=request.getRemoteAddr();  
String servletPath=request.getServletPath();  
String realPath=request.getRealPath("/");  
String remoteUser=request.getRemoteUser();  
String requestURI=request.getRequestURI();  
out.println("path:"+path+"<br>");  
out.println("basePath:"+basePath+"<br>");  
out.println("remoteAddr:"+remoteAddress+"<br>");  
out.println("servletPath:"+servletPath+"<br>");  
out.println("realPath:"+realPath+"<br>");  
out.println("remoteUser:"+remoteUser+"<br>");  
out.println("requestURI:"+requestURI+"<br>"); 
%>