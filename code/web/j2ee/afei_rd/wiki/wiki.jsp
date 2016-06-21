<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.FileReader"%>
<%@ page import="java.io.FileOutputStream"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.io.FileNotFoundException"%>
<%
	request.setCharacterEncoding("utf-8");
	String relativelyPath =request.getSession().getServletContext().getRealPath("/");// System.getProperty("user.dir");//
	String rootDoc = "doc";
	String PATH=relativelyPath +rootDoc;
	String urlRoot= request.getScheme()+"://"+request.getHeader("host");
	String urlApp= urlRoot+request.getContextPath();
     String urlStr= urlRoot+request.getRequestURI();
	String pathname = request.getParameter("d");
	String filename = request.getParameter("f");
	String text = request.getParameter("editor1");		
	String editvalue= request.getParameter("e");
	String title="";
	boolean isPost=false;
	boolean isEdit=false;
	//out.println(relativelyPath);
	//out.println(urlStr);	
	//out.println("["+pathname+"]/["+filename+"]");
	
	if (filename==null)
		filename="";
	if (pathname==null)
		pathname="";
	if (text==null){
		isPost=false;
		text="";
	}else
		isPost=true;
	if (editvalue==null){
	}else{
		if (editvalue.equalsIgnoreCase("1")){
			isEdit=true;
		}
	}
	String longfilename = PATH + "/"+ pathname;
	File file = new File(PATH);
	if (!file.exists()){
		out.println("Error, It is  [" + longfilename + "]");
		return;
	}
	boolean isHtml=true;
	if (filename.length()>0){
		
	   if (filename.length()<4){
		   isHtml=false;
	   }else{
		  int len=filename.length();
		  String extName4=filename.substring(len-4,len);  
		  //out.println("==4 2,["+extName4+"]"+".txt");	
		  if (extName4.equalsIgnoreCase(".txt") || extName4.equalsIgnoreCase(".htm") || extName4.equalsIgnoreCase(".log")){
		  }else {
			  if (filename.length()>5 ){				   
		  		   String extName5=filename.substring(len-5,len);
				    if (extName5.equalsIgnoreCase(".html")){
					}else{
						isHtml=false;  
					}
			  }else{
			  	isHtml=false;
			  }
		  }
	   }
	   longfilename = PATH + "/"+ pathname + "/" + filename;
	   longfilename=longfilename.replaceAll("\'", "").replaceAll("\"", "");
	   if (!isHtml){
		   response.sendRedirect(urlRoot+"/w/"+rootDoc+"/"+ pathname + "/" + filename);  
	   
		   return;
	   }
		//out.println("[" + longfilename + "]");
		file = new File(longfilename);
		if (text != null && isPost)  {
			if (file.isDirectory()) {
				out.println("Error, It is  [" + longfilename + "]");
				return;
			}
			//out.println("---5555");
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(new FileOutputStream(file, false));
				pw.write(text);
				pw.flush();
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//out.println("---666"+text);
			text = text.replaceAll("\r\n", "");
		}
		if (!file.exists() || file.isDirectory()) {
			out.println("Error FileName: [" + longfilename + "]");
			return;
		}
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuffer sb = new StringBuffer();
		String temp = null;
		temp = br.readLine();
		while (temp != null) {
			sb.append(temp + " ");
			temp = br.readLine();
		}
		text = sb.toString().replaceAll("\r\n", "");
		//out.println("<p></br>{" + text + "}</br>");
		if (!isEdit){ // entry view status:
		   out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		   out.println("<html dir=\"ltr\"><head><title>"+filename+"</title>");
		   out.println("<link type=\"text/css\" rel=stylesheet href=\""+urlApp+"/js/ckeditor/contents.css\"></head><body class=\"cke_show_borders\" >");  
		   out.println("<form method='post' action='"+urlStr+"?d="+pathname+"&f="+filename+"&e=1'><div><h2><span>"+filename+"</span><input type='submit' value='Edit'></h2></div></form>");  
			out.println(text);
			out.println("</body></html>");
			return;
		}
		title=filename;

	}else{
		 title="new file";
	}

	%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=title%></title>
<link type="text/css" rel="stylesheet" href="js/ckeditor/contents.css" />
<script type="text/javascript" src="js/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="js/ckfinder/ckfinder.js"></script>
<style type="text/css">
body {
	margin: 0px;
	padding: 0px;
	height: 100%;
	width: 100%;
	font-size: 12px;
}
</style>
<script type="text/javascript">
function edit_save(){
	alert("1111111111");
	if ( typeof CKEDITOR == 'undefined' )
	{
		alert("222222222");
	}
	else
	{
	    alert("3333333");
		var editor = CKEDITOR.replace( 'editor1' );
		 editor.updateElement(); //It is very import one word!!!
		 alert(editor.getData());
	}
}
</script>
</head>
<body>

	<p>

		<form name="form1" action="" onsubmit="return edit_save(this);"
		method="post"><textarea id="editor1" name="editor1" rows="10" cols="80"
			class="ckeditor"></textarea>
		<input type="hidden" id="url" value="<%=urlStr%>"> <input
			type="hidden" id="d" value="<%=pathname%>"><input
			type="hidden" id="f" value="<%=filename%>"></form>
	<script type="text/javascript">
        //var text="<p>Just click the <b>Image</b> or <b>Link</b> button, and then <b>&quot;Browse Server&quot;</b>.</p>";
var text= '<%=text%>';

// This is a check for the CKEditor class. If not defined, the paths must be checked.
if ( typeof CKEDITOR == 'undefined' )
{
	document.write(
		'<strong><span style="color: #ff0000">Error</span>: CKEditor not found</strong>.' +
		'This sample assumes that CKEditor (not included with CKFinder) is installed in' +
		'the "/ckeditor/" path. If you have it installed in a different place, just edit' +
		'this file, changing the wrong paths in the &lt;head&gt; (line 5) and the "BasePath"' +
		'value (line 32).' ) ;
}
else
{
	var editor = CKEDITOR.replace( 'editor1' );
	editor.setData(text);
	  //editor.setData( '<p>Just click the <b>Image</b> or <b>Link</b> button, and then <b>&quot;Browse Server&quot;</b>.</p>' );
			// Just call CKFinder.SetupCKEditor and pass the CKEditor instance as the first argument.
			// The second parameter (optional), is the path for the CKFinder installation (default = "/ckfinder/").
	CKFinder.setupCKEditor(editor, 'js/ckfinder/');
			// It is also possible to pass an object with selected CKFinder properties as a second argument.
			// CKFinder.SetupCKEditor( editor, { BasePath : '../../', RememberLastFolder : false } ) ;
}
		// define-self submit event:
	editor.on("instanceReady", function(evt) {
			editor.addCommand("save", {
				modes : {
					wysiwyg : 1,
					source : 1
				},
				exec : function(editor) {
					//alert("0000");
					var form = document.form1;
					form.action = "";//传想要跳转的路径
					var dir = document.getElementById("d").value;
					var filename = document.getElementById("f").value;
					//alert("111111111");
					if (filename.length == 0) {
						filename = prompt("请输入文件名", filename);
					}
					
					if (filename.length == 0) {
						return true;
					}
					var extname = "";
					var len = filename.length;
					//alert("len=" + len);
					if (len < 4) {
						//alert("222");
						extname = ".htm";
					} else {			
						//alert("3333");
						var extName4=filename.substring(len-4,len);
						//alert("3333 "+extName4);
						if (extName4==".txt" || extName4==".htm" || extName4==".log"){	
							//alert("444");
						}else{
							//alert("555");
							if (len>5){				 
								 extName4=filename.substring(len-5,len);  
								 if (extName4==".html" || extName4==".text"){
									 //alert("555");
								 }else{
									 extname=".htm";					 
								 }
							}else{
								extname=".htm";
							}
						}
					}
					filename=filename+extname;	
					var url=document.getElementById("url").value;
					document.getElementById("f").value = filename;	
					form.action =url+"?d="+dir+"&f="+filename+"&e=1";
					//alert(url+"?d="+dir+"&f="+filename+"&e=1");
					form.submit(); 
					return true;
				}
			});
		});

editor.on("instanceReady", function(evt) {
			editor.addCommand("about", {
				modes : {
					wysiwyg : 1,
					source : 1
				},
				exec : function(editor) {
					window.close();
					return true;
				}
			});
		});
(function () {
            editor.on('instanceReady', function (event) {
                var editor = event.editor;
                setTimeout(function () {
                    // Delay bit more if editor is still not ready.
                    if (!editor.element) {
                        setTimeout(arguments.callee, 100);
                        return;
                    }
                    event.removeListener('instanceReady', this.callee);
                    if (editor.name == 'editor1') {
                        var command = editor.getCommand('maximize');
                        command.exec();
                    }
                }, 0);
            }, null, null, 9999);
        })();

	</script>
</body>
</html>