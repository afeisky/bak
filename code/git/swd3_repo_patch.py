#!/usr/bin/python
#coding:utf-8 

"""
Script Name: swd3_patch_repo.py
Program: chaofei.wu.hz@tcl.com, 2016-04-12
Info: once push .repo's all files changed.

Usage: swd3_patch_repo.py

"""
import os
import sys
import re
import time
import glob
import commands
from commands import *
from time import *

from xml.dom.minidom import parse 
import xml.dom.minidom 

#    os.system("ls")  
#    p = Popen("cp -rf a/* b/", shell=True, stdout=PIPE, stderr=PIPE)
#    p.wait()
#    if p.returncode != 0:
#        print "Error."
#        return -1
#    status, output = commands.getstatusoutput("ls")
#    output = commands.getoutput("ls")
#    status = commands.getstatus("ls")



class _XML():
	def getAttribute(self,node, attrname):
		 return node.getAttribute(attrname) if node else ''

	def getNodeValue(self,node, index = 0):
		return node.childNodes[index].nodeValue if node else ''

	def getNode(self,node,name):
		return node.getElementsByTagName(name) if node else []


def swd3_repo_push():
	#print(sys.argv[0:])
	print(os.path.realpath(__file__))
	app_path="/home/chaofei/scm_tools/tools" #os.path.realpath(__file__)
	alm_apply_app=app_path+"/ALM_check.py"

	alps_path=os.getcwd()
	gitlist=[]
	fout=""
	
	repo_manifest_xml=".repo/manifest.xml"
	if not os.path.exists(repo_manifest_xml):
		print("\033[31m\033[05m" +"Error: "+"\033[0m"+"\033[1;31;40m"+repo_manifest_xml+"\033[0m"+" not found!")
		exit(1)
	else:
		#fin= open (repo_manifest_xml)
		#line=fin.readline()
		#print line
		#fin.close()
		#------.repo/manifest.xml-------------------------
		XML=_XML()
		doc = xml.dom.minidom.parse(repo_manifest_xml) 
		root = doc.documentElement
		#node_key = 'remote'
		#nodes = XML.getNode(root,node_key)
		#remote_fetch = ""
		#remote_name = ""
		#remote_review = ""
		#for node in nodes: 
			#remote_fetch = XML.getAttribute(node,'fetch')
			#remote_name = XML.getAttribute(node,'name')
			#remote_review = XML.getAttribute(node,'review')
			#print(node_key+":"+remote_fetch+","+remote_name+","+remote_review)
		node_key = 'default'
		nodes = XML.getNode(root,node_key)
		default_remote = ""
		default_revision = ""
		for node in nodes: 
			default_remote = XML.getAttribute(node,'remote')
			default_revision = XML.getAttribute(node,'revision')
			#print(node_key+":"+default_remote+","+default_revision)
		node_key = 'project'
		nodes = XML.getNode(root,node_key)
		project_name = ""
		project_path = ""
		for node in nodes: 
			project_name = XML.getAttribute(node,'name')
			project_path = XML.getAttribute(node,'path')
			if project_path=='device':
				project_device=project_name.replace('/device','')
			#print(node_key+":"+project_name+","+project_path)
		#---get repo manifest
		prj_link = "http://10.92.32.10/gitweb.cgi?p=scm_tools.git;a=blob_plain;f=conf/config";
		#---
		
		user_name = commands.getoutput("git config --list | grep user.name | sed -e 's/.*=//'")
		user_email = commands.getoutput("git config --list | grep user.email | sed -e 's/.*=//'")
		print("-------------------------------------------------")
		print("user_name: "+user_name+"		user_email: "+user_email)
		print("repo manifest is : \033[31m\033[1m" +default_revision+"     ["+project_device+"]\033[0m")

		gitstatus={}

		alps_path=os.getcwd()
		#print(os.getcwd())

		ch_id='a'
		n_id=1
		for node in nodes: 
			project_path = XML.getAttribute(node,'path')
			project_lib = XML.getAttribute(node,'name')
			#if project_path=='build':
			#print(project_path)
			os.chdir(alps_path+"/"+project_path)
			#print(os.getcwd())
			path_name = commands.getoutput("git remote -v | tail -1 | awk -F' ' '{print $2}' | sed -e 's/.*://' -e 's/.git//' -e 's/\//.?/g'")
			#file_modified1=commands.getoutput("git ls-files -m | awk '{print  $1}'").split("\n") # >>/tmp/~tcl_patch_delivery_git_modified") 
			#file_modified2=commands.getoutput("git status -s | awk  '/M /' | awk '{print  $2}'").split("\n") #>>/tmp/~tcl_patch_delivery_git_modified")
			#file_deleted=commands.getoutput("git ls-files -d | awk '{print  $1}'").split("\n") #>>/tmp/~tcl_patch_delivery_git_deleted")
			#file_added1=commands.getoutput("git status -s |awk '/A /' | awk '{print  $2}'").split("\n") #>>/tmp/~tcl_patch_delivery_git_added")
			#file_added2=commands.getoutput("git ls-files -o --exclude-standard | awk '{print  $1}'").split("\n") #>>/tmp/~tcl_patch_delivery_git_added")
			#file_rename=commands.getoutput("git status -uno | awk '/renamed/' | awk '{print  $3\" \"$4\" \"$5}'").split("\n") #>>/tmp/~tcl_patch_delivery_git_renamed")
			#for f in file_deleted: 
				#print(f)
				#one={"path":project_path,"filename":f,"status":"D"}
				#gitlist.append(one)
			#for f in file_rename: 
				#print(f)
				#one={"path":project_path,"filename":f,"status":"R"}
				#gitlist.append(one)
			#for f in file_modified1: 
				#print(f)
				#one={"path":project_path,"filename":f,"status":"M"}
				#gitlist.append(one)
			#for f in file_modified2: 
				#print(f)
				#one={"path":project_path,"filename":f,"status":"M"}
				#gitlist.append(one)
			#for f in file_added1: 
				#print(f)
				#one={"path":project_path,"filename":f,"status":"A"}
				#gitlist.append(one)
			#for f in file_added2: 
				#print(f)
				#one={"path":project_path,"filename":f,"status":"A"}
				#gitlist.append(one)
				
			
			file_d=commands.getoutput("git status -s |awk '/D /' | awk '{print  $2}'").split("\n")
			file_m=commands.getoutput("git status -s |awk '/M /' | awk '{print  $2}'").split("\n")
			file_a=commands.getoutput("git ls-files -o --exclude-standard").split("\n")
			#print("len==%d"%(len(file_d)))

			for f in file_d: 
				#print("---"+f)
				if len(f)>0:
					one={"ch":ch_id,"id":len(gitlist)+1,"path":project_path,"lib":project_lib,"filename":f,"status":"D","flag":0,"ok":0,"*":" "}
					gitlist.append(one)
			for f in file_m: 
				#print("---"+f)
				if len(f)>0:
					one={"ch":ch_id,"id":len(gitlist)+1,"path":project_path,"lib":project_lib,"filename":f,"status":"M","flag":0,"ok":0,"*":" "}
					gitlist.append(one)
			for f in file_a: 
				#print("---"+f)
				if len(f)>0:
					one={"ch":ch_id,"id":len(gitlist)+1,"path":project_path,"lib":project_lib,"filename":f,"status":"A","flag":0,"ok":0,"*":" "}
					gitlist.append(one)
			if len(gitlist)>0:
				#str="  \033[31m\033[05m%c\033[0m  [%s]:"%(ch_id,project_path)
				#print(str)
				#fout.write(str+"\n")
				for a in gitlist:
					#str=" \033[31m\033[05m%4d\033[0m  [%s]  %s"%(n_id,a['status'],a['filename'])
					#print(str)
					#fout.writelines(str+"\n")
					#print("==%s (%s) (%d) [%s] %s    [%s]"%(a['path'],a['ch'],a['id'],a['status'],a['filename'],a['flag']))
					n_id=n_id+1
				#ch_id=chr(ord(ch_id)+1)
				#print("ch_id=%s, n_id=%d"%(ch_id,n_id))
				#print("path_name: "+path_name)

		if len(gitlist)==0:
			print("\033[31m\033[1mWARNING!!! not found the modified files.\033[0m")
			exit(1)

		#sort asc:
		
		#set git lib --> a,b,c,....z
		if len(gitlist)>0:
			ch_id='`'
			fout= open(alps_path+'/~tmp_repo.log','w')
			a=gitlist[0]
			path="*"
			for a in gitlist:
				if a['path']!=path:
					ch_id=chr(ord(ch_id)+1)
					path=a["path"]
					#print(" \033[31;1m%c\033[0m [%s]:"%(ch_id,a["path"]))
					fout.write("%c  [%s]:\n"%(ch_id,a["path"]))
				a["ch"]=ch_id
				#print("==%s (%s) (%d) [%s] %s    [%s]"%(a['path'],a["ch"],a['id'],a['status'],a['filename'],a['flag']))
				#fout.write("==%s (%s) (%d) [%s] %s    [%s]"%(a["path"],a['ch'],a['id'],a['status'],a['filename'],a['flag']))
			fout.close()
		#exit(1) #DEBUG=1
		#---------------------------------------------------
		isModify=0
		while True:
			print("----------------------------------------")
			ch_cur=''
			n_cur=1
			fout= open(alps_path+'/~tmp_repo.log','w')
			for a in gitlist:
				error_file=""
				#check some error.
				if not os.path.exists(alps_path+'/'+a["path"]+"/"+a["filename"]) and (a['status']=="A" or a['status']=="M"):
					error_file=" [ERROR]"
				if a['ch']!=ch_cur:
					ch_cur=a['ch']
					print(" \033[31;1m%c\033[0m [%s]:"%(ch_cur,a['path']))
					fout.write("%c  [%s]:\n"%(ch_cur,a['path']))
				if a['flag']==0:
					print("\033[31;1m%4d [%s] %s %s\033[0m"%(n_cur,a['status'],a['filename'],error_file))
					fout.write("%4d  [%s]  %s\n"%(n_cur,a['status'],a['filename']))
				else:
					print("\033[32;1m%4d [%s] %s %s\033[0m"%(n_cur,a['status'],a['filename'],error_file))
					fout.write("%4d  [%s]  %s\n"%(n_cur,a['status'],a['filename']))
				n_cur=n_cur+1
			fout.close()
			str="git add update flow here:\n    1) If add all, pls input '*'.\n    2) If more one file add pls use ',' split.\n    3) If end add file, pls input 'Q|q'.\n"
			cmdstr=raw_input(str+"You choice: ") #sys.stdin.read()
			if len(cmdstr)>0:
				if cmdstr=='q' or cmdstr=='Q' :
					if isModify==0:
						print("\033[32;1mExit! Change Nothing!\033[0m")
						exit(1)
					break;
				cmdIsOK=0
				if cmdstr=='*':
					for a in gitlist:
						a['flag']=1
						#print(a['filename'])
						cmdIsOK=1
						isModify=1
					continue;
				cmds=cmdstr.split(",")
				cmdIsOK=0
				for cmd in cmds: 
					if not cmd.isspace():
						#print("==>"+cmd)
						if cmd.isalpha():
							for a in gitlist:
								if cmd==a['ch']:
									a['flag']=1
									cmdIsOK=1
						if cmd.isdigit():
							for a in gitlist:
								#print("==%s (%s) (%d) [%s] %s    [%s]"%(a['path'],a['ch'],a['id'],a['status'],a['filename'],a['flag']))			
								if int(cmd)==a['id']:
									a['flag']=1
									#print(a['filename'])
									cmdIsOK=1
				if cmdIsOK==0:
					print("\033[32;1mError input: %s\033[0m"%(cmdstr))
				else:
					isModify=1
			else:
				continue
		
		#-------
		update_bug_number="0"
		while True:
			cmds=raw_input("Bug Number 1944552: ").split("\n")
			cmd=cmds[0]
			#if cmd=='q' or cmd=='Q' :
			#	exit(1)
			if cmd.isdigit():
				if (int(cmd)>100):
					#ALM_check.py pixi45-ct-v4.0-dint 1944552
					lines=commands.getoutput("%s %s %s"%(alm_apply_app,default_revision,cmd)).split('\n')
					ret=lines[len(lines)-1]
					#print("ret=%s,"%(ret))
					if ret.isdigit():
						if int(ret)==200:
							print("\033[32;1mOK! go next\033[0m");
							update_bug_number=cmd
							break;
						elif int(ret)==201:
							print("\033[31;1mState isn't Resolved or Opened ,please check status of task or defect!\033[0m");
							continue;
						elif int(ret)==206:
							print("\033[31;1mThis id doesn't have any related utc , please check bugid or contact with SPM!\033[0m");
							continue;
						elif int(ret)==404:
							print("\033[31;1mThis id doesn't exist in Integrity , please check bugid!\033[0m");
							continue;
						elif int(ret)==500:
							print("\033[31;1mError while requesting the Integrity Server , please contact with INT!\033[0m");
							continue;
						elif int(ret)==505:
							print("\033[31;1mNO Platform Supported , please check again or contact with SPM!\033[0m");
							continue;
						elif int(ret)==600:
							print("\033[31;1mThe defect/task state is not Opened or Resolved , please check!\033[0m");
							continue;
					print("\033[31;1mError bug number, please check!\033[0m");
					continue;
		update_patch_comments=""
		while True:
			cmds=raw_input("Bug Comment(length>6): ").split("\n")
			cmd=cmds[0]
			if not cmd.isspace() and len(cmd)>6:
				update_patch_comments=cmd
				break;
		#------------------------------------------------------------------
		update_root_case=""
		update_bug_category=""
		update_module_impact=""
		update_test_suggestion=""
		update_solution=""
		update_test_report=""
		update_bug_reason=""
		list_select=[]
		list_select.append({"root_case":"Design","bug_category":"Platform"})
		list_select.append({"root_case":"Design","bug_category":"Android"})
		list_select.append({"root_case":"Design","bug_category":"3rd Party"})
		list_select.append({"root_case":"Regression","bug_category":"Platform"})
		list_select.append({"root_case":"Regression","bug_category":"Android"})
		list_select.append({"root_case":"Regression","bug_category":"3rd Party"})
		while True:
			str=""
			n=1
			for line in list_select:
				str+=" \033[31;1m%d\033[0m root_case:\033[31;05m%s\033[0m bug_category:\033[31;05m%s\033[0m\n"%(n,line['root_case'],line['bug_category'])
				n+=1
			cmds=raw_input(str+"Please select number:").split("\n")
			cmd=cmds[0]
			if cmd.isdigit() and int(cmd)<(len(list_select)+1):
				line=list_select[int(cmd)-1]
				update_root_case=line['root_case']
				update_bug_category=line['bug_category']
				break;
		###%%%product name:pixi4-55-3g-shine-3g-v1.0-dint ###%%%root cause:Design 
		###%%%Bug category:Android ###%%%Module_Impact: ###%%%Test_Suggestion: 
		###%%%Solution: ###%%%Test_Report: ###%%%Bug_Reason: ###%%%author email:lang.feng@tcl.com"
		update_str="###%%%comment:"+update_patch_comments+"\n"
		update_str+="###%%%bug number:"+update_bug_number+"\n"
		update_str+="###%%%product name:"+default_revision+"\n"
		update_str+="###%%%root cause:"+update_root_case+"\n"
		update_str+="###%%%Bug category:"+update_bug_category+"\n"
		update_str+="###%%%Module_Impact:"+update_module_impact+"\n"
		update_str+="###%%%Test_Suggestion:"+update_test_suggestion+"\n"
		update_str+="###%%%Solution:"+update_solution+"\n"
		update_str+="###%%%Test_Report:"+update_test_report+"\n"
		update_str+="###%%%Bug_Reason:"+update_bug_reason+"\n"
		update_str+="###%%%author email:"+user_email+"\n"

		print("------------------------------------------")
		fout= open(alps_path+'/~tmp_repo.log','w')
		ch_cur=''
		n_cur=1
		n_sort=1
		fout.write("-------updated----------------------------\n")
		for a in gitlist:
			if a['ch']!=ch_cur:
				ch_cur=a['ch']
				print("->\033[31;1m %c\033[0m [%s]:"%(ch_cur,a['path']))
				fout.write("%c  [%s]:\n"%(ch_cur,a['path']))
			if a['flag']==1:
				if a['status']=='D':
					os.chdir(alps_path+"/"+a['path'])
					#print("%s, "%(os.getcwd()))	
					commands.getoutput("git rm '%s'"%(a['filename']))
					a['ok']=1
					#a['*']="*"
					print("\033[31;1m%4d%s [%s] %s\033[0m"%(n_sort,a['*'],a['status'],a['filename']))
					fout.write("%4d%s [%s]  %s\n"%(n_sort,a['*'],a['status'],a['filename']))
					n_sort=n_sort+1
				elif a['status']=='A' or a['status']=='M':
					os.chdir(alps_path+"/"+a['path'])
					#print("%s, "%(os.getcwd()))
					commands.getoutput("git add '%s'"%(a['filename']))
					a['ok']=1
					#a['*']="*"
					print("\033[31;1m%4d%s [%s] %s\033[0m"%(n_sort,a['*'],a['status'],a['filename']))
					fout.write("%4d%s [%s]  %s\n"%(n_sort,a['*'],a['status'],a['filename']))
					n_sort=n_sort+1
				elif a['status']=='R':
					print("\033[31;1mERROR! ---[R] %s, %s\033[0m"%(os.getcwd(),a['filename']))
					fout.write("ERROR! %4d  [%s]  %s\n"%(n_cur,a['status'],a['filename']))
				#else:
				#	print("\033[31;1mERROR!\033[0m  %4d [%s] %s"%(n_cur,a['status'],a['filename']))
				#	fout.write("ERROR! %4d  [%s]  %s\n"%(n_cur,a['status'],a['filename']))
			#else:
			#	print("  \033[31;1m%4d\033[0m%s \033[31;05m[%s] %s\033[0m"%(n_cur,a['*'],a['status'],a['filename']))
			#	fout.write("%4d  [%s]  %s\n"%(n_cur,a['status'],a['filename']))
			n_cur=n_cur+1
		#print more:
		#----------commit & push-----------------------------------------------
		ch_cur='A'
		n_cur=1
		n_sort=1
		for a in gitlist:
			if a['ch']!=ch_cur:
				ch_cur=a['ch']
				flag_need_commit=0
				for b in gitlist:
					if b['ok']==1:
						flag_need_commit=1
						break;
				if flag_need_commit==1:
					os.chdir(alps_path+"/"+a['path'])
					str_commit="git commit -m '%s'"%(update_str)
					#print(str_commit)
					commands.getoutput(str_commit)
					str_push="git push ssh://%s@10.92.32.10:29418/%s HEAD:refs/for/%s"%(user_name,a["lib"],default_revision)
					print("----------------------------")
					print(str_push)
					#git push ssh://chaofei.wu@10.92.32.10:29418/sdd3/mtk6735/build HEAD:refs/for/pixi45-ct-v4.0-dint
					print(commands.getoutput(str_push))

		fout.write("-------modified, but not update---------------\n")
		ch_cur='A'
		n_cur=1
		n_sort=1
		for a in gitlist:
			if a['ch']!=ch_cur:
				ch_cur=a['ch']
				fout.write("%c  [%s]:\n"%(ch_cur,a['path']))
			if a['flag']==1:				
				if a['status']=='D':
					a=""
				elif a['status']=='A' or a['status']=='M':
					a=""
				elif a['status']=='R':
					fout.write("ERROR! %4d  [%s]  %s\n"%(n_sort,a['status'],a['filename']))
					n_sort=n_sort+1
				else:
					fout.write("ERROR! %4d  [%s]  %s\n"%(n_sort,a['status'],a['filename']))
					n_sort=n_sort+1
			else:
				fout.write("%4d  [%s]  %s\n"%(n_sort,a['status'],a['filename']))
				n_sort=n_sort+1
			n_cur=n_cur+1
			
		ch_cur='A'
		n_cur=1
		n_sort=1
		fout.write("\n\n---------------------------------------------\n")
		for a in gitlist:
			if a['ch']!=ch_cur:
				ch_cur=a['ch']
				fout.write("\n\n--------%s--------------------------------\n"%(a['path']))
				os.chdir(alps_path+"/"+a['path'])
				fout.write(commands.getoutput("git status")+'\n')
		fout.close()
		
		
	

#-------------------------------------------

if __name__ == "__main__":
	swd3_repo_push()

###%%%comment:ALPS02602030(For_jhz6735_65t_m_alps-mp-m0.mp1-V2.39.1_P10 
###%%%bug number:1862623 
###%%%product name:pixi4-5-4g-orange-v1.0-dint 
###%%%root cause:Design 
###%%%Bug category:Android 
###%%%Module_Impact: 
###%%%Test_Suggestion: 
###%%%Solution: 
###%%%Test_Report: 
###%%%Bug_Reason: 
###%%%author email:lang.feng@tcl.com	






