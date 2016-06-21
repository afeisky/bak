<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.File"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%
	request.setCharacterEncoding("utf-8");
	String relativelyPath =request.getSession().getServletContext().getRealPath("/");// System.getProperty("user.dir");//
	String rootDoc = "doc";
	String PATH=relativelyPath +rootDoc;
	String urlRoot= request.getScheme()+"://"+request.getHeader("host");
	String urlApp= urlRoot+request.getContextPath();
    String urlStr= urlRoot+request.getRequestURI();	
	String filename = request.getParameter("df");
	boolean isGoHome=false;
	if (filename==null)
		filename="";
	if (filename.length()>0){
		File df=new File(PATH+"/"+filename);
		boolean flag=false;
		if (df.exists()){
			flag=df.delete();
			if (flag){
				//response.sendRedirect("/");
				//request.getRequestDispatcher("/").forward(request,response); 
				isGoHome=true;
			}
		}
	}
	String dirname = request.getParameter("ad");
	if (dirname==null)
		dirname="";	
	if (dirname.length()>0){
		File folder=new File(PATH+"/"+dirname);
	    if (!folder.exists()){
	    	folder.mkdirs();
			isGoHome=true;
	    }
	}
	
	String dfo = request.getParameter("dfo");
	if (dfo==null)
		dfo="";
	if (dfo.length()>0){
		File deld=new File(PATH+"/"+dfo);
		if (deld.exists()){
			deld.delete();
			isGoHome=true;
		}
	}

	String orn = request.getParameter("orn");
	String ofo = request.getParameter("ofo");
	String nfo = request.getParameter("nfo");
	if (orn==null)
		orn="";
	if (orn.length()==0){
		orn="";
	}else{
    	orn=orn+"/";
	}
	if (ofo==null)
		ofo="";
	if (nfo==null)
		nfo="";
	if (ofo.length()>0 && nfo.length()>0){
		File fofo=new File(PATH+"/"+orn+ofo);
		File fnfo=new File(PATH+"/"+orn+nfo);
		if (fofo.exists() && !fnfo.exists()){
			fofo.renameTo(fnfo); 
			isGoHome=true;
		}else{
		}
	}
	String irn = request.getParameter("irn");
	String ofi = request.getParameter("ofi");
	String nfi = request.getParameter("nfi");
	if (irn==null)
		irn="";
	if (irn.length()==0){
		irn="";
	}else{
    	irn=irn+"/";
	}
	if (ofi==null)
		ofi="";
	if (nfi==null)
		nfi="";
	if (ofi.length()>0 && nfi.length()>0){
		File fofi=new File(PATH+"/"+irn+ofi);
		File fnfi=new File(PATH+"/"+irn+nfi);
		if (fofi.exists() && !fnfi.exists()){
			fofi.renameTo(fnfi); 
			isGoHome=true;
		}else{
		}
	}
	if (isGoHome){
	out.println("<html><script type='text/javascript'>	<!-- ");
	out.println("function load( ){window.location.href='"+urlStr+"' }");
	out.println("//-->	</script><body onload='load();'></body></html>");
	return;
	}

		String imgAddFolder="images/foldera.png";
		String imgDelFolder="images/folderd.png";
		String imgRnFolder="images/folderr.png";
		String imgAddFile="images/filea.png";
		String imgDelFile="images/filed.png";
		String imgRnFile="images/filer.png";
%>	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<link rel="shortcut icon" type="image/x-icon" href="./images/favicon.png" media="screen" /> 
<title>File List</title>
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

#example-basic {
 padding: 0;
 margin: 0;
 cellpadding:0;
 cellspacing:0;
 background-color="#99cc66";
}

#filelist{
	width:95%;
	margin:0 auto
}

.add_folder{
	background:url("images/foldera.png");left no-repeat;
	border-style:none; 
    width:30px;
    height:30px;

}
.del_folder{
	background:url("images/folderd.png");left no-repeat;
	border-style:none; 
    width:30px;
    height:30px;

}
.add_file{
	background:url("images/filea.png");left no-repeat;
	border-style:none; 
    width:30px;
    height:30px;
	
    }
.rn_folder{
	background:url("images/folderr.png");left no-repeat;
	border-style:none; 
    width:30px;
    height:30px;
	
}
.del_file{
	background:url("images/filed.png");left no-repeat;
	border-style:none; 
    width:30px;
    height:30px;

}
.rn_file{
	background:url("images/filer.png");left no-repeat;
	border-style:none; 
    width:30px;
    height:30px;
	
}

.td_op{
	width:120px;
}
.td_time{
	width:230px;
}
tr.folder{
	background: #99cccc;
}
tr.file{
	background: #eeeeee;
}

</style>
    <link rel="stylesheet" href="jquery_treetable/jquery.treetable.css" />
    <link rel="stylesheet" href="jquery_treetable/jquery.treetable.theme.default.css" />
<script type="text/javascript">
<!--
	function doAdd(d){
        window.open ("/wiki/wiki.jsp?d="+d+"&f=&e=1", 'newwindow');
	}
	function doAddFolder(d){		
		var dirname = prompt("请输入文件名", dirname);
		if (dirname.length == 0) {
			return;
		}		
		var form=document.form1;
		form.action ="/wiki/index.jsp?ad="+d+"/"+dirname;
		form.submit(); 
	}
	function doDelFolder(d){
		if(confirm("Delete Folder? \n\n"+d) == true){
		}else{ 
			   return;
		} 		
		var form=document.form1;
		form.action ="/wiki/index.jsp?dfo="+d;
		form.submit(); 
	}
   function doRenameFolder(d,n){
	    var newdir = prompt("请输入新的文件夹名称", n);
		if (newdir.length == 0) {
			return;
		}
		var form=document.form1;
		form.action ="/wiki/index.jsp?orn="+d+"&ofo="+n+"&nfo="+newdir;
		form.submit(); 
	}
   function doRenameFile(d,n){
	    var newname = prompt("请输入新的文件名", n);
		if (newname.length == 0) {
			return;
		}
		var form=document.form1;
		form.action ="/wiki/index.jsp?irn="+d+"&ofi="+n+"&nfi="+newname;
		form.submit(); 
	}
	function doDel(f){
		if(confirm("Delete file? \n\n "+f) == true){
		}else{ 
			   return;
		} 
		var form=document.form1;
		form.action ="/wiki/index.jsp?df="+f;
		form.submit();
	}
	
//-->
</script>
</head>
<body>


	<%
		File f = new File(PATH);
		if (!f.exists()) {
			out.println(PATH + "not exists");
			return;
		}
		File flist1[] = f.listFiles();
	%>
<form name="form1" action="" method="post"></form>
<div id="filelist">	
		<table id='example-basic' border="0" cellpadding="1" cellspacing="1" >
        <caption>File list:     <a href="search.jsp"  target='_blank'>搜索文件</a></caption>
        <thead>
          <tr>
            <th>filename</th>      
			<th>&nbsp;</th>
			<th>time&size</th>
          </tr>
        </thead>
        <tbody>
	<%         
		DecimalFormat df0 = new DecimalFormat("#.00");
		DecimalFormat df = new DecimalFormat("#.00");
		long fileSize=0;
		String StrFileSize="";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        
		for (int i = 0; i < flist1.length; i++) {
			File fs1 = flist1[i];
			String id1=i+"";
			if (fs1.isDirectory()) {
				String dir1=fs1.getName();				
				//out.println("<li><span class='folder'>" + fs1.getName() + "<div>&nbsp;&nbsp;&nbsp;&nbsp;</div><div><a href='wiki.jsp?d="+dir1+"' target='_blank'>Add</div></span>");
				out.println("<tr data-tt-id='"+id1+"' class='folder'><td>"+fs1.getName()+"</td>");				
								out.println("<td class='td_time'><span>"+ formatter.format(fs1.lastModified())+"</span>	<span>&nbsp;</span></td>");
				out.println("<td class='td_op'><input type='button' class='add_file'  onclick='doAdd(\""+dir1+"\");'><input type='button' class='add_folder' onclick='doAddFolder(\""+dir1+"\");'><input type='button' class='del_folder' onclick='doDelFolder(\""+fs1.getName()+"\");'><input type='button' class='rn_folder' onclick='doRenameFolder(\"\",\""+fs1.getName()+"\");'></td></tr>");

				File flist2[] = fs1.listFiles();
				if (flist2.length > 0) {
					//out.println("<tr><td>"+fs1.getName() +"</td><td><a href='wiki.jsp?d="+dir1+"' target='_blank'></td></tr>");
					for (int j = 0; j < flist2.length; j++) {
						File fs2 = flist2[j];						
						String id2=id1+"."+j;
						if (fs2.isDirectory()) {
							String dir2=dir1+"/"+fs2.getName();
							out.println("<tr data-tt-id='"+id2+"' data-tt-parent-id='"+id1+"' class='folder'><td>"+fs2.getName() +"</td>");
							out.println("<td class='td_time'><span>"+ formatter.format(fs2.lastModified())+"</span>	<span>&nbsp;</span></td>");
							out.println("<td class='td_op'><input type='button' class='add_file' onclick='doAdd(\""+dir2+"\");'><input type='button' class='add_folder' onclick='doAddFolder(\""+dir2+"\");'><input type='button' class='del_folder' onclick='doDelFolder(\""+dir2+"\");'><input type='button' class='rn_folder' onclick='doRenameFolder(\""+dir1+"\",\""+fs2.getName()+"\");'></td></tr>");
							File flist3[] = fs2.listFiles();
							if (flist3.length > 0) {
								//out.println("<tr><td>"+fs2.getName() +"</td><td><a href='wiki.jsp?d="+dir2+"' target='_blank'></td></tr>");
								for (int k = 0; k < flist3.length; k++) {
									File fs3 = flist3[k];
									String id3=id2+"."+k;
									if (fs3.isDirectory()) {
										String dir3=dir2+"/"+fs3.getName();
										out.println("<tr  data-tt-id='"+id3+"' data-tt-parent-id='"+id2+"' class='folder'><td>"+fs3.getName() +"</td>");	
																	out.println("<td class='td_time'><span >"+ formatter.format(fs3.lastModified())+"</span>	<span>&nbsp;</span></td>");
										out.println("<td class='td_op'><input type='button' class='add_file' onclick='doAdd(\""+dir3+"\");'><input type='button' class='add_folder' onclick='doAddFolder(\""+dir3+"\");'><input type='button' class='del_folder' onclick='doDelFolder(\""+dir3+"\");'><input type='button' class='rn_folder' onclick='doRenameFolder(\""+dir2+"\",\""+fs3.getName()+"\");'></td></tr>");

										File flist4[] = fs3.listFiles();
										if (flist4.length > 0) {
											//out.println("<tr><td>"+fs3.getName() +"</td><td><a href='wiki.jsp?d="+dir3+"' target='_blank'></td></tr>");
											for (int n = 0; n < flist4.length; n++) {
												File fs4 = flist4[n];
												String id4=id3+"."+n;
												if (fs4.isDirectory()) {
													String dir4=dir3+"/"+fs4.getName();													
													out.println("<tr data-tt-id='"+id4+"' data-tt-parent-id='"+id3+"' class='folder'><td>"+fs4.getName() +"</td>");
																										out.println("<td class='td_time'><span>"+ formatter.format(fs4.lastModified())+"</span>	<span>&nbsp;</span></td>");
													out.println("<td class='td_op'><input type='button' class='del_folder' onclick='doDelFolder(\""+dir4+"\");'><input type='button' class='rn_file' onclick='doRenameFile(\""+dir3+"\",\""+fs4.getName()+"\");'></td></tr>");

												} else {
													fileSize=fs4.length();
											        if (fileSize < 1024) {
											        	StrFileSize = fileSize + "Byte";
											        } else if (fileSize < 1048576) {
											        	StrFileSize = df.format((double) fileSize / 1024) + "K";
											        } else if (fileSize < 1073741824) {
											        	StrFileSize = df.format((double) fileSize / 1048576) + "M";
											        } else {
											        	StrFileSize = df.format((double) fileSize / 1073741824) +"G";
											        }
													out.println("<tr data-tt-id='"+id4+"' data-tt-parent-id='"+id3+"' class='file'><td> <a href='wiki.jsp?d="+dir3+"&f="+fs4.getName()+"' target='_blank'>"+fs4.getName()+"</a></td>");
																		out.println("<td class='td_time'><span>"+ formatter.format(fs4.lastModified())+"</span><span>"+StrFileSize+"</span></td>");
													out.println("<td class='td_op'><input type='button'  class='del_file' onclick='doDel(\""+dir3+"/"+fs4.getName()+"\");'><input type='button' class='rn_file' onclick='doRenameFile(\""+dir3+"\",\""+fs4.getName()+"\");'></td></tr>");
								
												}
											}
											//out.println("</ul>");
										}
										//out.println("</li>");										
									} else {
										fileSize=fs3.length();
								        if (fileSize < 1024) {
								        	StrFileSize = fileSize + "Byte";
								        } else if (fileSize < 1048576) {
								        	StrFileSize = df.format((double) fileSize / 1024) + "K";
								        } else if (fileSize < 1073741824) {
								        	StrFileSize = df.format((double) fileSize / 1048576) + "M";
								        } else {
								        	StrFileSize = df.format((double) fileSize / 1073741824) +"G";
								        }
											out.println("<tr  data-tt-id='"+id3+"' data-tt-parent-id='"+id2+"' class='file'><td><a href='wiki.jsp?d="+dir2+"&f="+fs3.getName()+"' target='_blank'>"+fs3.getName()+"</a></td>");
											out.println("<td class='td_time'><span>"+ formatter.format(fs3.lastModified())+"</span><span>"+StrFileSize+"</span></td>");
											out.println("<td class='td_op'><input type='button' class='del_file' onclick='doDel(\""+dir2+"/"+fs3.getName()+"\");'><input type='button' class='rn_file' onclick='doRenameFile(\""+dir2+"\",\""+fs3.getName()+"\");'></td></tr>");
											
									}
								}
								//out.println("</ul>");
							}
							//out.println("</li>");
						} else {
							fileSize=fs2.length();
					        if (fileSize < 1024) {
					        	StrFileSize = fileSize + "Byte";
					        } else if (fileSize < 1048576) {
					        	StrFileSize = df.format((double) fileSize / 1024) + "K";
					        } else if (fileSize < 1073741824) {
					        	StrFileSize = df.format((double) fileSize / 1048576) + "M";
					        } else {
					        	StrFileSize = df.format((double) fileSize / 1073741824) +"G";
					        }
							out.println("<tr  data-tt-id='"+id2+"' data-tt-parent-id='"+id1+"' class='file'><td><a href='wiki.jsp?d="+dir1+"&f="+fs2.getName()+"' target='_blank'>"+fs2.getName()+"</a></td>");
							out.println("<td class='td_time'><span>"+ formatter.format(fs2.lastModified())+"</span><span>"+StrFileSize+"</span></td>");
							out.println("<td class='td_op'><input type='button' class='del_file' onclick='doDel(\""+dir1+"/"+fs2.getName()+"\");'><input type='button' class='rn_file' onclick='doRenameFile(\""+dir1+"\",\""+fs2.getName()+"\");'></td></tr>");
							
						}
					}
					//out.println("</ul>");
				}
				//out.println("</li>");
			} else {
				fileSize=fs1.length();
		        if (fileSize < 1024) {
		            StrFileSize = fileSize + "Byte";
		        } else if (fileSize < 1048576) {
		            StrFileSize = df.format((double) fileSize / 1024) + "K";
		        } else if (fileSize < 1073741824) {
		            StrFileSize = df.format((double) fileSize / 1048576) + "M";
		        } else {
		            StrFileSize = df.format((double) fileSize / 1073741824) +"G";
		        }		        
				out.println("<tr data-tt-id='"+id1+"' class='file'><td><a href='wiki.jsp?d=&f="+fs1.getName()+"' target='_blank'>"+fs1.getName()+"</a></td>");
				out.println("<td class='td_time'><span>"+ formatter.format(fs1.lastModified())+"</span><span>"+StrFileSize+"</span></td>");
				out.println("<td class='td_op'><input type='button' class='del_file' onclick='doDel(\""+"/"+fs1.getName()+"\");'><input type='button' class='rn_folder' onclick='doRenameFolder(\"\",\""+fs1.getName()+"\");'></td></tr>");
				
			}
		}
		

	%>
	<tbody></table>
</div>	
 <script src="jquery_treetable/jquery.js"></script>
    <script src="jquery_treetable/jquery-ui.js"></script>
    <script src="jquery_treetable/jquery.treetable.js"></script>
    <script>
      $("#example-basic").treetable({ expandable: true });

      $("#example-basic-static").treetable();

      $("#example-basic-expandable").treetable({ expandable: true });

      $("#example-advanced").treetable({ expandable: true });

      // Highlight selected row
      $("#example-advanced tbody tr").mousedown(function() {
        $("tr.selected").removeClass("selected");
        $(this).addClass("selected");
      });

      // Drag & Drop Example Code
      $("#example-advanced .file, #example-advanced .folder").draggable({
        helper: "clone",
        opacity: .75,
        refreshPositions: true, // Performance?
        revert: "invalid",
        revertDuration: 300,
        scroll: true
      });

      $("#example-advanced .folder").each(function() {
        $(this).parents("tr").droppable({
          accept: ".file, .folder",
          drop: function(e, ui) {
            var droppedEl = ui.draggable.parents("tr");
            $("#example-advanced").treetable("move", droppedEl.data("ttId"), $(this).data("ttId"));
          },
          hoverClass: "accept",
          over: function(e, ui) {
            var droppedEl = ui.draggable.parents("tr");
            if(this != droppedEl[0] && !$(this).is(".expanded")) {
              $("#example-advanced").treetable("expandNode", $(this).data("ttId"));
            }
          }
        });
      });

      $("form#reveal").submit(function() {
        var nodeId = $("#revealNodeId").val()

        try {
          $("#example-advanced").treetable("reveal", nodeId);
        }
        catch(error) {
          alert(error.message);
        }

        return false;
      });
    </script>
</body>
</html>