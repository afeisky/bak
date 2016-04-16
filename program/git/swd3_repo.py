#!/usr/bin/python
#coding:utf-8 

"""
Script Name: swd3_patch_repo.py
Program: chaofei.wu.hz@tcl.com, 2016-04-12
Info: once push .repo's all files changed.

Usage: 
1: swd3_patch_repo.py
2: swd3_patch_repo.py -i  or import       # be use on "import" branch 


go on:

bug1:  bug number not is in current repo branch version should not be commited. ALM_check.py's bug.

"""
import os
import sys
import re
import time
import datetime
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


def swd3_repo_patch_push(isImport=False):

	DEBUG=0
	app_path=os.path.realpath(__file__) 
	alm_apply_app=app_path.replace("/"+os.path.basename(__file__),"")+"/ALM_check.py"
	alps_path=os.getcwd()
	log_file_name=alps_path+'/~tmp_repo.log'	
	gitlist=[]
	fout=""
	default_revision=""
	project_name = ""
	project_path = ""
	
	user_name = commands.getoutput("git config --list | grep user.name | sed -e 's/.*=//'")
	user_email = commands.getoutput("git config --list | grep user.email | sed -e 's/.*=//'")
	print("-------------------------------------------------")
	print("user_name: "+user_name+"		user_email: "+user_email)
		
	repo_or_lib=-1
	repo_manifest_xml=".repo/manifest.xml"
	XML=_XML()
	nodes=[]
	if os.path.exists(repo_manifest_xml):
		repo_or_lib=0
	else:
		lines = commands.getoutput("git branch -a | grep '\->' | sed -e 's/.*jgs.//'").split("\n")		
		if os.path.exists(os.getcwd()+"/.git") and len(lines)==1:
			repo_or_lib=1
			log_file_name='/tmp/~tmp_swd3_patch_lib.log'
	
	
	if not(repo_or_lib==0 or repo_or_lib==1):
		print("\033[31m\033[05m" +"Error: "+"\033[0m"+"\033[1;31;40m"+repo_manifest_xml+"\033[0m"+" not found!")
		exit(1)
	
	if repo_or_lib==0:
		#------.repo/manifest.xml-------------------------
		doc = xml.dom.minidom.parse(repo_manifest_xml) 
		root = doc.documentElement
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

		for node in nodes: 
			project_name = XML.getAttribute(node,"name")
			project_path = XML.getAttribute(node,"path")
			if project_path=='device':
				project_device=project_name.replace('/device','')
			#print(node_key+":"+project_name+","+project_path)
		#---get repo manifest
		prj_link = "http://10.92.32.10/gitweb.cgi?p=scm_tools.git;a=blob_plain;f=conf/config";
		#---
		print("repo manifest is : \033[31m\033[1m" +default_revision+"     ["+project_device+"]\033[0m")
	elif repo_or_lib==1:
		default_revision = commands.getoutput("git branch -a | grep '\->' | sed -e 's/.*jgs.//'")
		project_lib = commands.getoutput("git remote -v | tail -1 | awk -F' ' '{print $2}' | sed -e 's/.*://' -e 's/.git//' -e 's/\//\//g'")
		project_path=project_lib
		nodes.append(project_path)
		print("repo manifest is : \033[31m\033[1m" +default_revision+"     ["+project_path+"]\033[0m")
	else:
		print("\033[31m\033[05m" +"Error: "+"\033[0m"+"\033[1;31;40m"+repo_manifest_xml+"\033[0m"+" and git lib not found!")
		exit(1)
	#------------
	gitstatus={}
	
	alps_path=os.getcwd()
	#print(os.getcwd())
	#get all git status file:------------------
	ch_id='a'
	n_id=1
	for node in nodes: 
		if repo_or_lib==0:
			project_path = XML.getAttribute(node,"path")  #device
			project_lib = XML.getAttribute(node,"name")  #sdd3/mtk6735m/device
			os.system("echo -n '.'")
			os.chdir(alps_path+"/"+project_path)
		#elif repo_or_lib==1:			
	
		#print(os.getcwd())
		#path_name = commands.getoutput("git remote -v | tail -1 | awk -F' ' '{print $2}' | sed -e 's/.*://' -e 's/.git//' -e 's/\//.?/g'")
		start=datetime.datetime.now().microsecond
		file_d=commands.getoutput("git ls-files -d").split("\n")
		file_m=commands.getoutput("git ls-files -m").split("\n")
		file_a=commands.getoutput("git ls-files -o").split("\n")
		#print("len==%d"%(len(file_d)))
		mid=datetime.datetime.now().microsecond
		for f in file_d: 
			#print("---"+f)
			if len(f)>0:
				if f[0:1]=='"' and f[-1:]=='"' and not f[-2:]=='\\"':
					f=f[1:-1]
				if project_path=="modem" and f[:6]=="build/":
					continue;
				one={"ch":ch_id,"id":len(gitlist)+1,"path":project_path,"lib":project_lib,"filename":f,"status":"D","select":0,"do":0,"*":" "}
				gitlist.append(one)
		for f in file_m: 
			#print("---"+f)
			if len(f)>0 and not f in file_d:
				if f[0:1]=='"' and f[-1:]=='"' and not f[-2:]=='\\"':
					f=f[1:-1]
				if project_path=="modem" and f[:6]=="build/":
					continue;
				one={"ch":ch_id,"id":len(gitlist)+1,"path":project_path,"lib":project_lib,"filename":f,"status":"M","select":0,"do":0,"*":" "}
				gitlist.append(one)
		for f in file_a: 
			#print("---"+f)
			if len(f)>0:
				if f[0:1]=='"' and f[-1:]=='"' and not f[-2:]=='\\"':
					f=f[1:-1]
				if project_path=="modem" and f[:6]=="build/":
					continue;
				one={"ch":ch_id,"id":len(gitlist)+1,"path":project_path,"lib":project_lib,"filename":f,"status":"A","select":0,"do":0,"*":" "}
				gitlist.append(one)
		end=datetime.datetime.now().microsecond
		#print("%d->%d,	%10d		%10d	%10d	%s"%(start,end,mid-start,end-mid,end-start,project_path))

	os.system("echo \n")
	if len(gitlist)==0:
		print("\033[31m\033[1mWARNING!!! not found the modified files.\033[0m")
		exit(1)
	
	#sort asc:
	
	#set git lib --> a,b,c,....z
	if len(gitlist)>0:
		ch_id='`'
		#fout= open(log_file_name,'w')
		a=gitlist[0]
		path="*"
		for a in gitlist:
			if a["path"]!=path:
				if ch_id>"z" or ch_id=="":
					ch_id=""
				else:
					ch_id=chr(ord(ch_id)+1)
				
				path=a["path"]
				#print("[%s]:"%(a["path"])) #print(" \033[31;1m%s\033[0m [%s]:"%(ch_id,a["path"]))
				#fout.write("%c  [%s]:\n"%(ch_id,a["path"]))
			a["ch"]=ch_id
			#print("==%s (%s) (%d) [%s] %s    [%s]"%(a["path"],a["ch"],a['id'],a['status'],a['filename'],a['s']))
			#fout.write("==%s (%s) (%d) [%s] %s    [%s]"%(a["path"],a['ch'],a['id'],a["status"],a['filename'],a["select"]))
		#fout.close()
	#exit(1) #DEBUG=1
	#----select files:--------------------------------------------
	isModify=0
	while True:
		ch_cur=''
		n_cur=1
		fout= open(log_file_name,'w')
		print("###############################################################")
		for a in gitlist:
			error_file=""
			#check some error.
			#if not os.path.exists(alps_path+'/'+a["path"]+"/"+a["filename"]) and (a["status"]=="A" or a["status"]=="M"):
			#	print("Error: "+alps_path+'/'+a["path"]+"/"+a["filename"])
			#	error_file=" [ERROR]"
			if a['ch']!=ch_cur:
				ch_cur=a['ch']
				print(" \033[31;1m%c\033[0m [%s]:"%(ch_cur,a["path"]))
				fout.write("%c  [%s]:\n"%(ch_cur,a["path"]))
			if a["select"]==0:
				print("\033[31;1m%4d [%s] %s %s\033[0m"%(n_cur,a["status"],a['filename'],error_file))
				fout.write("%4d  [%s]  %s\n"%(n_cur,a["status"],a['filename']))
			else:
				print("\033[33;1m%4d [%s] %s %s\033[0m"%(n_cur,a["status"],a['filename'],error_file))
				fout.write("%4d  [%s]  %s\n"%(n_cur,a["status"],a['filename']))
			n_cur=n_cur+1
		fout.close()
		str="git add update flow here:\n    1) If add all, pls input '*'.\n    2) If more one file add pls use ',' split.\n    3) If end add file, pls input 'Q|q'.\n"
		cmdstr=raw_input(str+"You choice: ") #sys.stdin.read()
		if len(cmdstr)>0:
			if cmdstr=='q' or cmdstr=='Q' :
				if isModify==0:
					print("\033[33;1mExit! Change Nothing!\033[0m")
					exit(1)
				break;
			cmdIsOK=0
			if cmdstr=='*':
				for a in gitlist:
					a["select"]=1
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
								a["select"]=1
								cmdIsOK=1
					if cmd.isdigit():
						for a in gitlist:
							#print("==%s (%s) (%d) [%s] %s    [%s]"%(a["path"],a['ch'],a['id'],a["status"],a['filename'],a["select"]))			
							if int(cmd)==a['id']:
								a["select"]=1
								#print(a['filename'])
								cmdIsOK=1
			if cmdIsOK==0:
				print("\033[33;1mError input: %s\033[0m"%(cmdstr))
			else:
				isModify=1
		else:
			continue
	
	#-------
	update_bug_number="0"
	while True and isImport==0:
		cmds=raw_input("Bug Number: ").split("\n") #1944552
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
						print("\033[33;1mOK! go next\033[0m");
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
	list_select.append({"root_case":"Design","bug_category":"TCT"})
	list_select.append({"root_case":"Design","bug_category":"Platform"})
	list_select.append({"root_case":"Design","bug_category":"Android"})
	list_select.append({"root_case":"Design","bug_category":"3rd Party"})
	list_select.append({"root_case":"Regression","bug_category":"Platform"})
	list_select.append({"root_case":"Regression","bug_category":"Android"})
	list_select.append({"root_case":"Regression","bug_category":"3rd Party"})
	while True and isImport==0:
		str=""
		n=1
		for line in list_select:
			str+=" \033[31;1m%d\033[0m root_case:\033[31;05m%s\033[0m   bug_category:\033[31;05m%s\033[0m\n"%(n,line['root_case'],line['bug_category'])
			n+=1
		cmds=raw_input(str+"Please select number:").split("\n")
		cmd=cmds[0]
		if cmd.isdigit() and int(cmd)<(len(list_select)+1) and int(cmd)>0:
			line=list_select[int(cmd)-1]
			update_root_case=line['root_case']
			update_bug_category=line['bug_category']
			break;
	TCT_bug= ['UE Implementation', 'UE Design', 'SW Code', 'Perso Tool Chain', 'HW Related Implementation', 'New Requirement','UE Improvement','Not Reproduced','Not A Bug','Duplicated','Parameters','PC Tools','FOTA Tools Server Chain','Non GApp Scope','Non Isolation GApp', 'SW Translation Error' , 'External Translation feedback' , 'Customization config' , 'Customization mechanism' , 'Parameters Integration' , 'GAPP' , 'Others']
	update_generated_by=""
	if update_bug_category=="TCT":
		while True:
			str=""
			n=0
			array_size=len(TCT_bug)/2
			#print(array_size)
			for i in range(array_size):
				print("\033[31;1m%d\033[0m:%s  		\033[31;1m%d\033[0m:%s"%(i*2+1,TCT_bug[i*2],i*2+2,TCT_bug[i*2+1]))
			array_more=len(TCT_bug)%2
			if array_more==1:
				print("\033[31;1m%d\033[0m:%s"%(len(TCT_bug),TCT_bug[len(TCT_bug)-1]))
			cmds=raw_input(str+"Please select number:").split("\n")
			cmd=cmds[0]
			if cmd.isdigit() and int(cmd)<(len(TCT_bug)+1) and int(cmd)>0:
				update_generated_by=TCT_bug[int(cmd)-1]
				break;
	###%%%product name:pixi4-55-3g-shine-3g-v1.0-dint ###%%%root cause:Design 
	###%%%Bug category:Android ###%%%Generated by:###%%%Module_Impact: ###%%%Test_Suggestion: 
	###%%%Solution: ###%%%Test_Report: ###%%%Bug_Reason: ###%%%author email:lang.feng@tcl.com"
	update_str="###%%%comment:"+update_patch_comments+"\n"
	update_str+="###%%%bug number:"+update_bug_number+"\n"
	update_str+="###%%%product name:"+default_revision+"\n"
	update_str+="###%%%root cause:"+update_root_case+"\n"
	update_str+="###%%%Bug category:"+update_bug_category+"\n"
	if not update_generated_by=="":
		update_str+="###%%%Generated by:"+update_generated_by+"\n"
	update_str+="###%%%Module_Impact:"+update_module_impact+"\n"
	update_str+="###%%%Test_Suggestion:"+update_test_suggestion+"\n"
	update_str+="###%%%Solution:"+update_solution+"\n"
	update_str+="###%%%Test_Report:"+update_test_report+"\n"
	update_str+="###%%%Bug_Reason:"+update_bug_reason+"\n"
	update_str+="###%%%author email:"+user_email+"\n"

	print("###############################################################")
	fout= open(alps_path+'/~tmp_repo.log','w')
	ch_cur=''
	n_cur=1
	n_sort=1
	fout.write("-------updated----------------------------\n")
	update_git_arr=[]
	update_git_count=[]
	for g in gitlist:
		if g['ch']!=ch_cur:
			ch_cur=g['ch']
			#print("\033[31;1m %c\033[0m [%s]:"%(ch_cur,g["path"]))
			#fout.write("%c  [%s]:\n"%(ch_cur,g["path"]))
			temp_curr_git_file_count=0
			for a in gitlist:
				if a["ch"]==ch_cur and a["select"]==1:
					#print("==%s (%s) (%d) [%s] %s    [%s][%s]"%(a["path"],a["ch"],a['id'],a["status"],a['filename'],a["select"],a["do"]))					
					temp_curr_git_file_count+=1
			if temp_curr_git_file_count>0:
				#print(g["path"])
				update_git_arr.append(g["path"])
				update_git_count.append(temp_curr_git_file_count)

	ch_cur='`'
	n_count=0
	for a in gitlist:
		if a['ch']!=ch_cur:
			ch_cur=a['ch']
			n_count=0
			i=0
			for s in update_git_arr:
				if a["path"]==s:
					print(" %s:"%(update_git_arr[i]))
					fout.write(" %s:"%(update_git_arr[i]))
					break;
				i+=1
		if a["select"]==1:
			if a["status"]=='D':
				#os.chdir(alps_path+"/"+a["path"])
				#print("%s, "%(os.getcwd()))
				a["do"]=1
				#a['*']="*"
				n_count=n_count+1
				print("\033[31;1m%4d%s [%s] %s\033[0m"%(n_count,a['*'],a["status"],a['filename']))
				fout.write("%4d%s [%s]  %s\n"%(n_count,a['*'],a["status"],a['filename']))				
			elif a["status"]=='A' or a["status"]=='M':
				#os.chdir(alps_path+"/"+a["path"])
				#print("%s, "%(os.getcwd()))
				a["do"]=1
				#a['*']="*"
				n_count=n_count+1
				print("\033[31;1m%4d%s [%s] %s\033[0m"%(n_count,a['*'],a["status"],a['filename']))
				fout.write("%4d%s [%s]  %s\n"%(n_count,a['*'],a["status"],a['filename']))
			elif a["status"]=='R':
				print("\033[31;1mERROR! ---[R] %s, %s\033[0m"%(os.getcwd(),a['filename']))
				fout.write("ERROR! %4d --[R] [%s]  %s\n"%(n_count,a["status"],a['filename']))
			#else:
			#	n_count=n_count+1
			#	print("\033[31;1mERROR!\033[0m  %4d [%s] %s"%(n_count,a["status"],a['filename']))
			#	fout.write("ERROR! %4d  [%s]  %s\n"%(n_count,a["status"],a['filename']))
		#else:
		#	print("  \033[31;1m%4d\033[0m%s \033[31;05m[%s] %s\033[0m"%(n_cur,a['*'],a["status"],a['filename']))
		#	fout.write("%4d  [%s]  %s\n"%(n_cur,a["status"],a['filename']))
	#print more:
	
	
	

	fout.write("-------modified, but not update---------------\n")
	ch_cur='A'
	n_cur=1
	n_sort=1
	for a in gitlist:
		if a['ch']!=ch_cur:
			ch_cur=a['ch']
			fout.write("%c  [%s]:\n"%(ch_cur,a["path"]))
		if a["select"]==1:				
			if a["status"]=='D':
				a=""
			elif a["status"]=='A' or a["status"]=='M':
				a=""
			elif a["status"]=='R':
				fout.write("ERROR! %4d  [%s]  %s\n"%(n_sort,a["status"],a['filename']))
				n_sort=n_sort+1
			else:
				fout.write("ERROR! %4d  [%s]  %s\n"%(n_sort,a["status"],a['filename']))
				n_sort=n_sort+1
		else:
			fout.write("%4d  [%s]  %s\n"%(n_sort,a["status"],a['filename']))
			n_sort=n_sort+1
		n_cur=n_cur+1

	fout.write("\n\n---------------------------------------------\n")
	
	#ch_cur='`'
	#update_git_arr=[]
	#for a in gitlist:
		#if a['ch']!=ch_cur:
			#ch_cur=a['ch']
			#temp_curr_git_file_count=0
			#for b in gitlist:
				#if b["ch"]==ch_cur and b["do"]==1:
					##print("==%s (%s) (%d) [%s] %s    [%s][%s]"%(b["path"],b["ch"],b['id'],b["status"],b['filename'],b["select"],b["do"]))					
					#temp_curr_git_file_count+=1
			#if temp_curr_git_file_count>0:
				#update_git_arr.append({"path":a["path"],"count":temp_curr_git_file_count})
	fout.close()
	#----------commit & push-----------------------------------------------	
	print("---------------------------------------------------------------")
	while True:
		cmds=raw_input("\033[31;1mConfirm update these files\033[0m: [yes/no]:").split("\n")
		cmd=cmds[0]
		if cmd=="yes":
			break;
		elif cmd=="no":	
			exit(1)

	if isImport==1:
		update_str=update_patch_comments
		
	#do:git add/rm . & git commit -m ..& git push .
	fout= open(alps_path+'/~tmp_repo.log','w+')
	ch_cur='`'
	n_cur=1
	n_sort=1
	for k in gitlist:
		if k['ch']!=ch_cur:
			ch_cur=k['ch']
			flag_need_commit=0
			for a in gitlist:
				if a["select"]==1 and a["ch"]==ch_cur:
					if a["status"]=='D':
						os.chdir(alps_path+"/"+a["path"])
						#print("%s, "%(os.getcwd()))
						commands.getoutput("git rm '%s'"%(a['filename']))
						a["do"]=1
						#print("\033[31;1m%4d%s [%s] %s\033[0m"%(n_sort,a['*'],a["status"],a['filename']))
						#fout.write("%4d%s [%s]  %s\n"%(n_sort,a['*'],a["status"],a['filename']))
						flag_need_commit=flag_need_commit+1
					elif a["status"]=='A' or a["status"]=='M':
						os.chdir(alps_path+"/"+a["path"])
						#print("%s, "%(os.getcwd()))
						commands.getoutput("git add '%s'"%(a['filename']))
						a["do"]=1
						#print("\033[31;1m%4d%s [%s] %s\033[0m"%(n_sort,a['*'],a["status"],a['filename']))
						#fout.write("%4d%s [%s]  %s\n"%(n_sort,a['*'],a["status"],a['filename']))
						flag_need_commit=flag_need_commit+1
					#elif a["status"]=='R':
						#print("\033[31;1mERROR! ---[R] %s, %s\033[0m"%(os.getcwd(),a['filename']))
						#fout.write("ERROR! %4d  [%s]  %s\n"%(n_cur,a["status"],a['filename']))
			if flag_need_commit>0:
				os.chdir(alps_path+"/"+k["path"])
				str_commit="git commit -m '%s'"%(update_str)
				#print(str_commit)
				commands.getoutput(str_commit)
				str_push="git push ssh://%s@10.92.32.10:29418/%s HEAD:refs/for/%s"%(user_name,k["lib"],default_revision)
				print("----------------------------")
				print(str_push)
				#git push ssh://chaofei.wu@10.92.32.10:29418/sdd3/mtk6735/build HEAD:refs/for/pixi45-ct-v4.0-dint
				print(commands.getoutput(str_push))
	#view git status
	for a in gitlist:
		if a['ch']!=ch_cur:
			ch_cur=a['ch']
			fout.write("\n\n--------%s--------------------------------\n"%(a["path"]))
			os.chdir(alps_path+"/"+a["path"])
			fout.write(commands.getoutput("git status")+'\n')
	fout.close()
	print("----------Done!-----------------------------------")

#-------------------------------------------

if __name__ == "__main__":
	try:
		argvs = sys.argv[1:]
		if len(argvs) < 1:
			swd3_repo_patch_push(False)
		else:
			if 'import' in argvs or '-i' in argvs:
				swd3_repo_patch_push(True)
	except:
		print("")
		exit(1)

        
	





