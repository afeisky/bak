#!/usr/bin/python
#coding:utf-8 

"""
Script Name: swd3_mtk_patch_merge.py
Program: chaofei.wu.hz@tcl.com, 2016-04-12
Info: Can auto merge MTK ALPS Patch.

Usage: 
1: swd3_mtk_patch_merge.py alps_patch_file.tar.gz


go on:


git some:
删除dint分支的代码，保留.git，repo forall –c “git rm . -r”
删除一部的FSR代码中.git库，repo forall –c “rm –rf *.git”，可用find –name “*.git”查看是否全部删除
git merge-file new_file origin_file patch_file   # new_file需要打补丁的文件, origin_file是上一次补丁的文件, patch_file是当前补丁的文件
git format-patch commit_id_old commit_id_new
git am format-patch_index001.patch

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

#----------------
ONOFF_MERGE_COPY_FILE=1

#---------------
_DEBUG=0

class XML():
	def getAttribute(self,node, attrname):
		 return node.getAttribute(attrname) if node else ''

	def getNodeValue(self,node, index = 0):
		return node.childNodes[index].nodeValue if node else ''

	def getNode(self,node,name):
		return node.getElementsByTagName(name) if node else []

class SYSTEM():
	def cmd(self,cmd,debug=0):
		if debug==1 or _DEBUG==1:
			print(cmd)
		out=commands.getoutput(cmd)
		if debug==1 or _DEBUG==1:
			print(out)
		return out
class LOG():
	def ln(self,string,debug=0):
		if debug==1:
			print(string)
		elif _DEBUG==1:
			print(string)

class GIT():
	#def merge_file(self,dintname,patchname):
		#os.chdir(ALPS_DIR)
		#new_file=ALPS_DIR+"/"+dintname
		#old_file=BRANCH_IMPORT_DIR+"/"+patchname
		#patch_file=PATH_ALPS_DIR+"/"+patchname

		#LOG().ln("git merge-file '%s' '%s' '%s'"%(new_file,old_file,patch_file))
		#cmdout=commands.getoutput("git merge-file --theirs '%s' '%s' '%s'"%(new_file,old_file,patch_file))
		##冲突，是两进制文件使用patch的
		#lines=cmdout
		#LOG().ln("["+lines+"]")
		#if lines.count("error: Cannot merge binary files")>0:
			##cmdout=commands.getoutput("git merge-file --theirs '%s' '%s' '%s'"%(new_file,old_file,patch_file))
			#if ONOFF_MERGE_COPY_FILE==1:
				#LOG().ln("cp '%s' '%s' "%(patch_file,new_file))			
				#cmdout=commands.getoutput("cp -f '%s' '%s' "%(patch_file,new_file))
				#LOG().ln("(%s)"%(cmdout))
			#return -1
		##冲突
		#lines=commands.getoutput("cat '%s' | grep '<<<<<<< %s'"%(dintname,new_file)).split("\n")
		#LOG().ln("cat '%s' | grep '<<<<<<< %s'"%(dintname,new_file))
		#for line in lines:
			#LOG().ln("cat[%s]"%(line))
		#if (len(lines[0])>0):
			#return len(lines)

		#return 0
		
	#def push(self,comment,git_lib,root_path):
		#LOG().ln("-->")
		#for one in git_lib:
			#os.chdir(root_path+"/"+one["path"])
			#lines=commands.getoutput("git status -s").split('\n')
			#if lines>0 and len(lines[0])>0:
				#for line in lines:
					#LOG().ln(line)
				##commands.getoutput("git add '%s'"%(a['filename']))
			#str_commit="git commit -m '%s'"%(comment)
			##cmdout=commands.getoutput(str_commit)
			#str_push="git push ssh://%s@10.92.32.10:29418/%s HEAD:refs/for/%s"%(user_name,one["name"],default_revision)
			#LOG().ln(str_push)
			##git push ssh://chaofei.wu@10.92.32.10:29418/sdd3/mtk6735/build HEAD:refs/for/pixi45-ct-v4.0-dint
			##LOG().ln(commands.getoutput(str_push))
		#pass
	

	def get_commit_last(self,gitLibPath):
		os.chdir(gitLibPath)
		log=SYSTEM().cmd("git log").split('\n')
		one={}
		count=len(log)
		for n in range(count):
			#LOG().ln("n=%d"%(n))
			if log[n].find("commit ")==0:
				commit=log[n].replace("commit ","").rstrip().strip()
				author=log[n+1].replace("Author  ","").rstrip().strip()
				date=log[n+2].replace("Date: :   ","").rstrip().strip()
				comment=log[n+4].rstrip().strip()
				if (n+6)<len(log):
					change=log[n+6].replace("Change-Id:   ","").rstrip().strip()
				one={"commit":commit,"Author":author,"Date":date,"comment":comment,"change":change}
				#LOG().ln("[%s %s %s %s]"%(comment,commit,date,author))
				break
		return one

PATH_LOG_FILE=os.getcwd()+"~mtk_patch_merge.log"

class _G():
	TABLE_DINT_AND_IMPORT=[]
	pass
	TABLE_DINT_AND_IMPORT.append({"DINT":"pixi4-55-3g-shine-3g-v1.0-dint","IMPORT":"mtk6580-3g-v1.0-import","LD":"","LI":""})
	TABLE_DINT_AND_IMPORT.append({"DINT":"pixi4-5-4g-orange-v1.0-dint","IMPORT":"pixi45-ct-v2.0-import","LD":"","LI":""})
	#please add here
	pass
	TABLE_DINT_AND_IMPORT_INDEX=-1
	user_name = SYSTEM().cmd("git config --list | grep user.name | sed -e 's/.*=//'")
	user_email = SYSTEM().cmd("git config --list | grep user.email | sed -e 's/.*=//'")
	DINT_PATH_DIR=os.getcwd()
	PATCH_FILE_NAME="ALPS02670712(For_jhz6580_we_3_m_alps-mp-m0.mp1-V2.34_P12).tar.gz"
	PATCH_PATH_DIR="/home/chaofei/2016/patch_merge_1/AP"
	PATCH_PATH_ALPS=PATCH_PATH_DIR+"/alps"
	PATCH_P_X_CURR=""
	PATCH_P_X_LAST=""
	
	List_Associated_Files=[]
	List_Delete_Files=[]
	
	list_git_lib=[]
	
	default_remote = ""
	default_revision = ""
	project_device = ""

	def input_patch_dir(self):
		while True:
			if not self.PATCH_PATH_DIR==None:
				return 1
			cmds=raw_input("patch file dir(which dir is ALPSxxx(xxxP1).tag.gz] in)?:\n").split("\n")
			cmd=cmds[0]
			if not cmd.isspace() and len(cmd)>2:
				if not os.path.exists(cmd):
					LOG().ln("\033[31;1mError: not found dir [%s]\033[0m "%(cmd),1)
					continue
				elif cmd==self.DINT_PATH_DIR:
					LOG().ln("\033[31;1mError: patch dir is not same as current dir[%s]\033[0m "%(cmd),1)
					continue
				else:
					self.PATCH_PATH_DIR=cmd
					if self.input_patch_filename()==1:
						return 1
					else:
						continue
			continue
	def input_patch_filename(self):
		while True:
			if not self.PATCH_FILE_NAME==None:
				return 1
			cmds=raw_input("patch file name: eg [ALPSxxx(xxxP1).tag.gz]?:\n").split("\n")
			cmd=cmds[0]
			if not cmd.isspace() and len(cmd)>2:
				#LOG().ln("%s/%s"%(self.PATCH_PATH_DIR,cmd))
				if not os.path.exists("%s/%s"%(self.PATCH_PATH_DIR,cmd)):
					LOG().ln("\033[31;1mError: not found dir [%s]\033[0m "%(cmd),1)
					LOG().ln("22222222222222222222")
					continue
				elif not self.check_patch_name_dir()==1:
					LOG().ln("3333333333333333")
					continue
				else:
					self.PATCH_FILE_NAME=cmd
					LOG().ln("4444444444444444444")
					return 1
			LOG().ln("555555555555555555")
			continue

	def check_patch_name_dir(self):
		self.PATCH_PATH_ALPS=self.PATCH_PATH_DIR+"/alps"
		self.PATCH_P_X_CURR=self.PATCH_FILE_NAME[self.PATCH_FILE_NAME.find("_P")+1:self.PATCH_FILE_NAME.find(").tar.gz")]

		PATCH_P_X_CURR_N=int(self.PATCH_P_X_CURR.replace("P",""))
		if self.PATCH_P_X_CURR=="P1":
			self.PATCH_P_X_LAST="-import"
		else:
			self.PATCH_P_X_LAST="MTK Critical Patch P%d"%(PATCH_P_X_CURR_N-1)
		self.PATCH_P_X_CURR="MTK Critical Patch P%d"%(PATCH_P_X_CURR_N)
		LOG().ln("PATCH STRING: %s"%(self.PATCH_P_X_CURR))
		return 1

	def get_log_file_info(self):
		PATH_TEXT_FILE="%s/%s"%(self.PATCH_PATH_DIR,self.PATCH_FILE_NAME.replace(".tar.gz",".txt"))
		PATH_TEXT_FILE_1="%s/patch_list.txt"%(self.PATCH_PATH_DIR)
		cmdout=SYSTEM().cmd("rm '%s/*.txt'"%(self.PATCH_PATH_DIR))
		cmdout=SYSTEM().cmd("rm '%s'"%(PATH_TEXT_FILE))
		cmdout=SYSTEM().cmd("rm '%s'"%(PATH_TEXT_FILE_1))
		cmdout=SYSTEM().cmd("rm -rf '%s/'"%(self.PATCH_PATH_ALPS))
		#tar -zxvf patch.tar.gz:
		os.chdir(self.PATCH_PATH_DIR)
		#LOG().ln("tar -zxvf '%s/%s'"%(self.PATCH_PATH_DIR,self.PATCH_FILE_NAME))
		cmdout=SYSTEM().cmd("tar -zxvf '%s/%s'"%(self.PATCH_PATH_DIR,self.PATCH_FILE_NAME),0)
		
		if os.path.exists(PATH_TEXT_FILE):
			pass
		elif os.path.exists(PATH_TEXT_FILE_1):
			PATH_TEXT_FILE=PATH_TEXT_FILE_1
		else:
			LOG().ln("Error: not found patch_list file.",1)
			return -2
			
		#read patch text file:
		fin= open(PATH_TEXT_FILE,'r')
		Associated_Files=[]
		AssociatedFiles_start=False
		Delete_Files=[]
		DeleteFiles_start=False
		while True:
			line = fin.readline()
			if not line:
				break
			line=line.replace("\n","")
			#LOG().ln("[%s]"%(line.replace("\n","")))
			if line=="":
				AssociatedFiles_start=False
				DeleteFiles_start=False
			
			if line[:len("Associated Files:")]=="Associated Files:":
				AssociatedFiles_start=True
				continue
			
			if AssociatedFiles_start==True:
				line=line.replace("  ","")
				#LOG().ln("[%s]"%(line))
				Associated_Files.append(line)

			if line[:len("Delete Files:")]=="Delete Files:":
				DeleteFiles_start=True
				continue
				
			if DeleteFiles_start==True:
				line=line.replace("delete ","")
				#LOG().ln("[%s]"%(line))
				Delete_Files.append(line)
			
			pass
		fin.close()
		#-------------------------------------
		fout= open(PATH_LOG_FILE,'w')
		LOG().ln("Delete Files: %d files"%(len(Delete_Files)),1)
		fout.write("Delete Files: %d files\n"%(len(Delete_Files)))
		for line in Delete_Files:
			isDir=False
			if line[-1:]=="/":
				isDir=True
				one={"dint":line,"patch":line,"isDir":isDir,"fail":0,"comment":""}	
			one={"filename":line,"isDir":False,"do":0}
			self.List_Delete_Files.append(one)
			#LOG().ln(line)
			fout.write("%s [%s]\n"%((isDir and "(DIR)	" or "	"),line))
		LOG().ln("Associated Files: %d files"%(len(Associated_Files)),1)
		fout.write("Associated Files: %d files\n"%(len(Associated_Files)))
		for line in Associated_Files:
			one={"dint":line,"patch":line,"isDir":False,"fail":0,"comment":""}
			self.List_Associated_Files.append(one)
			#LOG().ln(line)
			fout.write("[%s]\n"%(line))	
		fout.close()

		#BEGIN-Pixi4-55 3G , jhz6580_we_3_m -->rename --> jhz6580_we_m----
		Others_Files=[]
		for one in self.List_Delete_Files:
			patchname=one["patch"]
			if patchname.count("device/jrdchz/jhz6580_we_3_m/")>0:
				one["dint"]=patchname.replace("jhz6580_we_3_m","jhz6580_we_m")
			elif patchname.count("device/jrdchz/jhz6580_weg_3_m/")>0:
				one["dint"]=patchname.replace("jhz6580_weg_3_m","jhz6580_weg_m")
			#LOG().ln(":"+one["dint"]+"<--,"+patchname)
		for one in self.List_Associated_Files:
			patchname=one["patch"]
			newfile=""
			if patchname.count("device/jrdchz/jhz6580_we_3_m/")>0:
				one["dint"]=patchname.replace("jhz6580_we_3_m","jhz6580_we_m")
				#BEGIN-Pixi4-55 3G , jhz6580_we_m --> clone project --> pixi4_55
				newfile=patchname.replace("jhz6580_we_3_m/","pixi4_55/")
				#Others_Files.append()

				#END---Pixi4-55 3G , jhz6580_we_m --> clone project --> pixi4_55
			elif patchname.count("device/jrdchz/jhz6580_weg_3_m/")>0:
				one["dint"]=patchname.replace("jhz6580_weg_3_m","jhz6580_weg_m")
			#LOG().ln(":"+one["dint"]+"<--,"+patchname)

			if len(newfile)>0:
				#LOG().ln(one)
				two={"dint":newfile,"patch":patchname,"isDir":False,"fail":0,"comment":""}
				Others_Files.append(two)
		if len(Others_Files)>0:
			for one in Others_Files:
				#LOG().ln(one)
				self.List_Associated_Files.append(one)
		#END---Pixi4-55 3G , jhz6580_we_3_m -->rename --> jhz6580_we_m----

		#LOG().ln("-------")
		#for one in self.List_Delete_Files:
			#LOG().ln(":"+one["dint"]+"<--,"+one["patch"])
		#for one in self.List_Associated_Files:
			#LOG().ln(":"+one["dint"]+"<--,"+one["patch"])
		#------------------------------
		#LOG().ln("########################################")
		return 0
		
	#def get_git_lib1(self):
		#os.chdir(self.DINT_PATH_DIR)
		#lines=SYSTEM.cmd("cat .repo/project.list",0).split('\n')
		#for line in lines:
			#isFind=0
			#for one in self.List_Delete_Files:
				##LOG().ln(one["dint"][:len(line)+1])
				#if line+"/"==one["dint"][:len(line)+1]:
					#isFind=1
					#break
			#if isFind==0:
				#for one in self.List_Associated_Files:
					##LOG().ln(one["filename"][:len(project_path)+1])
					#if line+"/"==one["dint"][:len(line)+1]:
						#isFind=1
						#break
			#if isFind==1:
				#self.list_git_lib.append({"name":line,"path":line,"add":0,"del":0,"do":0,"fail":0,"filelist":[]})
				##LOG().ln("******")
		#for lib in self.list_git_lib:
			#LOG().ln(lib["name"]+", "+lib["path"])
			#for one in self.List_Associated_Files:
				#LOG().ln(lib["path"])
				#LOG().ln(one["dint"])
				#if one["dint"].find(lib["path"])==0:
					#lib["add"]+=1
			#for one in self.List_Delete_Files:
				#if one["dint"].find(lib["path"])==0:
					#lib["del"]+=1
		#LOG().ln("[Added/Modified files]	[Deleted Files]		[git lib]")
		#for lib in self.list_git_lib:
			#LOG().ln("	%d 			   %d		%s/"%(lib["add"],lib["del"],lib["path"]))
	def get_git_lib(self,repo_manifest_xml):
		#check .repo/manifest:
		#------.repo/manifest.xml-------------------------
		doc = xml.dom.minidom.parse(repo_manifest_xml) 
		root = doc.documentElement
		node_key = 'default'
		nodes = XML().getNode(root,node_key)

		for node in nodes: 
			self.default_remote = XML().getAttribute(node,'remote')
			self.default_revision = XML().getAttribute(node,'revision')
			#LOG().ln(node_key+":"+self.default_remote+","+self.default_revision)
		node_key = 'project'
		nodes = XML().getNode(root,node_key)

		for node in nodes: 
			project_name = XML().getAttribute(node,"name")  #"/sdd3/..."
			project_path = XML().getAttribute(node,"path")
			if project_path=='device':
				project_device=project_name.replace('/device','')
			isFind=0
			for one in self.List_Delete_Files:
				#LOG().ln(one["dint"][:len(project_path)+1])
				if project_path+"/"==one["dint"][:len(project_path)+1]:
					isFind=1
					break
			if isFind==0:
				for one in self.List_Associated_Files:
					#LOG().ln(one["dint"][:len(project_path)+1])
					if project_path+"/"==one["dint"][:len(project_path)+1]:
						isFind=1
						break
			if isFind==1:
				self.list_git_lib.append({"name":project_name,"path":project_path,"add":0,"del":0,"do":0,"success":0,"filelist":[],"LI":"","LD":"","D":[],"M":[],"A":[],"R":[]})
				#LOG().ln("******")
			
			#LOG().ln(node_key+":"+project_name+","+project_path)
		#---get repo manifest
		prj_link = "http://10.92.32.10/gitweb.cgi?p=scm_tools.git;a=blob_plain;f=conf/config";
		#---

		if len(self.list_git_lib)<=0:
			return -1
		elif len(self.list_git_lib)>0:
			for i in range(len(self.TABLE_DINT_AND_IMPORT)):
				one=self.TABLE_DINT_AND_IMPORT[i]
				if one["DINT"].find(self.default_revision)>=0:
					self.TABLE_DINT_AND_IMPORT_INDEX=i
					break
		if self.TABLE_DINT_AND_IMPORT_INDEX>=0:
			LOG().ln("repo manifest is : \033[31m\033[1m" +self.default_revision+"     ["+self.project_device+"]\033[0m",1)
			LOG().ln("import branch is : \033[31m\033[1m" +self.TABLE_DINT_AND_IMPORT[self.TABLE_DINT_AND_IMPORT_INDEX]["IMPORT"]+"\033[0m",1)
			for one in self.list_git_lib:
				cmdout=self.get_import_branch_name(self.DINT_PATH_DIR+"/"+one["path"])
				if len(cmdout)>0:
					one["LI"]=cmdout
				else:
					one["LI"]=""
					LOG().ln("%s: not found import branch! %s"%(one["path"],self.TABLE_DINT_AND_IMPORT[self.TABLE_DINT_AND_IMPORT_INDEX]["IMPORT"]),1)
					return -3
		else:
			LOG().ln(self.default_revision)
			return -2
		return 0
		
	def get_import_branch_name(self,path_name):
		os.chdir(path_name)
		lines=SYSTEM().cmd("git branch -a | grep '%s'"%(self.TABLE_DINT_AND_IMPORT[self.TABLE_DINT_AND_IMPORT_INDEX]["IMPORT"]))
		#LOG().ln(lines)
		if len(lines)>=0:
			long_import_branch_name=lines.strip()
			return long_import_branch_name
		return ""

	def get_import_commit_list(self,gitLibPath,logcmd):
		os.chdir(gitLibPath)
		#lines=SYSTEM().cmd("git log --pretty=format:'%h %s' --abbrev-commit").split('\n')
		lines=SYSTEM().cmd(logcmd).split('\n')
		listc=[]
		for line in lines:
			#LOG().ln(line)
			commit=line[0:7].strip()
			comment=line[8:].strip()
			if comment==self.PATCH_P_X_CURR: # comment.find(self.PATCH_P_X_CURR)>=0:
				one={"commit":commit,"Author":"","Date":"","comment":comment,"change":""}
				listc.append(one)
				LOG().ln("===################################")
			#LOG().ln(one)
		return listc
	def filter_commit_list(self,listc):
		list2=[]
		for one in listc:
			#LOG().ln(one)
			commit=one["commit"]
			comment=one["comment"]
			if comment.find(self.PATCH_P_X_CURR)>=0:
				one={"commit":commit,"Author":"","Date":"","comment":comment,"change":""}
				list2.append(one)
				LOG().ln("===##############===================")
			#LOG().ln(one)
		return list2
			
	def get_commit_list(self):
		for lib in self.list_git_lib:
			path=self.DINT_PATH_DIR+"/"+lib["path"]
			#lines=SYSTEM().cmd("git branch -a | grep 'v1.0-import'"%())
			#cmdstr="git log --pretty=format:'%h %s' --abbrev-commit"
			#cmdstr="git log remotes/jgs/mtk6580-3g-v1.0-import --pretty=format:'%h %s' --abbrev-commit"
			cmdstr="git log "+lib["LI"]+" --pretty=format:'%h %s' --abbrev-commit"
			listc=self.get_import_commit_list(path,cmdstr)
			lib["filelist"]=listc
			#LOG().ln(lib)
		pass
	def print_git_lib_info(self):
		LOG().ln("----------------------------------------------",1)
		n=1
		for lib in self.list_git_lib:
			LOG().ln("  \033[31m\033[1m%3d %s\033[0m"%(n,lib["path"]),1)
			listc=lib["filelist"]
			for one in listc:
				LOG().ln("	patch: %s"%(one["comment"]),1)
			n+=1
		pass

	def merge(self):
		for lib in self.list_git_lib:
			LOG().ln(lib["name"]+", "+lib["path"])
			os.chdir(self.DINT_PATH_DIR+"/"+lib["path"])
			listc=lib["filelist"]
			for one in listc:
				cmdout=SYSTEM().cmd("git reset --hard")
				lines=SYSTEM().cmd("git log --pretty=format:'%h %s' --abbrev-commit HEAD^..HEAD").split('\n')
				commit=lines[0][0:7].strip()
				cmdstr="git cherry-pick '%s'"%(one["commit"])
				cmdout=SYSTEM().cmd(cmdstr,1)
				LOG().ln(cmdout)
				if cmdout.find("error")>=0:
					LOG().ln("\033[31m\033[05mERROR! merge fail [%s] \033[0m"%(lib["path"]),1)
				else:
					LOG().ln("---> merge ok [%s]"%(lib["path"]),1)
					lib["success"]=1
					cmdout=SYSTEM().cmd("git reset --soft '%s'"%(commit)) #("git reset --soft HEAD^")
		pass


class _PATCH():
	list_patch_file=[]
	list_git_lib=[]
	DINT_PATH_DIR=""
	default_revision=""
	app_path=os.path.realpath(__file__) 
	alm_apply_app=app_path.replace("/"+os.path.basename(__file__),"")+"/ALM_check.py"
	user_name = SYSTEM().cmd("git config --list | grep user.name | sed -e 's/.*=//'")
	user_email = SYSTEM().cmd("git config --list | grep user.email | sed -e 's/.*=//'")
	def patch_check(self):
		ch_id='a'
		n=1
		for lib in self.list_git_lib:
			os.chdir(self.DINT_PATH_DIR+"/"+lib["path"])
			project_path=lib["path"]
			project_lib=lib["name"]
			file_d=SYSTEM().cmd("git status -s | awk '/D /' | sed -e 's/ D //' | sed -e 's/D  //'").split("\n")
			file_d+=SYSTEM().cmd("git ls-files -d").split("\n")
			file_m=SYSTEM().cmd("git status -s | awk '/M /' | sed -e 's/ M //' | sed -e 's/M  //'").split("\n")
			file_m+=SYSTEM().cmd("git ls-files -m").split("\n")
			file_a=SYSTEM().cmd("git ls-files -o --exclude-standard").split("\n")
			file_a+=SYSTEM().cmd("git ls-files -o").split("\n")
			#LOG().ln("len==%d"%(len(file_d)))
			file_d = list(set(file_d))
			file_m = list(set(file_m))
			file_a = list(set(file_a))
			file_d.sort()
			file_m.sort()
			file_a.sort()
			#do special file name with ( space , '"' )
			for f in file_d: 
				if len(f)>0:
					if f[0:1]=='"' and f[-1:]=='"' and not f[-2:]=='\\"':
						f=f[1:-1]
					one={"ch":ch_id,"id":len(self.list_patch_file)+1,"path":project_path,"name":project_lib,"filename":f,"status":"D","select":0,"do":0,"*":" "}
					self.list_patch_file.append(one)
					#LOG().ln(one)
				#else:
					#file_d.remove(f)
			for f in file_m: 
				if len(f)>0 and not f in file_d:
					if f[0:1]=='"' and f[-1:]=='"' and not f[-2:]=='\\"':
						f=f[1:-1]
					one={"ch":ch_id,"id":len(self.list_patch_file)+1,"path":project_path,"name":project_lib,"filename":f,"status":"M","select":0,"do":0,"*":" "}
					self.list_patch_file.append(one)
					#LOG().ln(one)
				#else:
					#file_m.remove(f)
			for f in file_a: 
				if len(f)>0:
					if f[0:1]=='"' and f[-1:]=='"' and not f[-2:]=='\\"':
						f=f[1:-1]
					one={"ch":ch_id,"id":len(self.list_patch_file)+1,"path":project_path,"name":project_lib,"filename":f,"status":"A","select":0,"do":0,"*":" "}
					self.list_patch_file.append(one)
					#LOG().ln(one)
				#else:
					#file_a.remove(f)

			if ch_id>"z" or ch_id=="":
				ch_id=""
			else:
				ch_id=chr(ord(ch_id)+1)
			#LOG().ln("---------------")
			#LOG().ln(file_d)
			#LOG().ln(file_m)
			#LOG().ln(file_a)
			#lib["D"]=file_d
			#lib["M"]=file_m
			#lib["A"]=file_a

	def print_patch_files(self):
		ch_id=''
		for a in self.list_patch_file:
			error_file=""
			if a['ch']!=ch_id:
				ch_id=a['ch']
				LOG().ln(" \033[31;1m%c\033[0m [%s]:"%(ch_id,a["path"]),1)
				#fout.write("%c  [%s]:\n"%(ch_id,a["path"]))
			if a["select"]==0:
				LOG().ln("\033[31;1m%4d [%s] %s %s\033[0m"%(a["id"],a["status"],a['filename'],error_file),1)
				#fout.write("%4d  [%s]  %s\n"%(ch_id,a["status"],a['filename']))
			else:
				LOG().ln("\033[32;1m%4d [%s] %s %s\033[0m"%(a["id"],a["status"],a['filename'],error_file),1)
				#fout.write("%4d  [%s]  %s\n"%(ch_id,a["status"],a['filename']))
			
							
			#LOG().ln("%c    [%s]"%(ch_id,lib["path"]))
			#if ch_id>"z" or ch_id=="":
				#ch_id=""
			#else:
				#ch_id=chr(ord(ch_id)+1)
			#if not lib["D"]==None:
				#for line in lib["D"]:
					#LOG().ln(" %4d [D] (%s)"%(n,line))
					#n+=1
			#if not lib["M"]==None:
				#for line in lib["M"]:
					#LOG().ln(" %4d [M] (%s)"%(n,line))
					#n+=1
			#if not lib["A"]==None:
				#for line in lib["A"]:
					#LOG().ln(" %4d [A] (%s)"%(n,line))
					#n+=1
	def popup_window(self):
		while True:
			self.print_patch_files()
			str="git add update flow here:\n    1) If add all, pls input '*'.\n    2) If more one file add pls use ',' split.\n    3) If end add file, pls input 'Q|q'.\n"
			cmdstr=raw_input(str+"You choice: ") #sys.stdin.read()
			if len(cmdstr)>0:
				if cmdstr=='q' or cmdstr=='Q' :
					#if isModify==0:
					#	LOG().ln("\033[32;1mExit! Change Nothing!\033[0m",1)
					#	return
					print("===++++")
					ret=self.patch_add()
					print("add:%d"%(ret))
					if ret==0:
						break
					else:
						continue # break;
				cmdIsOK=0
				if cmdstr=='*':
					for a in self.list_patch_file:
						a["select"]=1
						#LOG().ln(a['filename'])
						cmdIsOK=1
						isModify=1
					continue
				cmds=cmdstr.split(",")
				cmdIsOK=0
				for cmd in cmds: 
					if not cmd.isspace():
						#LOG().ln("==>"+cmd)
						if cmd.isalpha():
							for a in self.list_patch_file:
								if cmd==a['ch']:
									a["select"]=1
									cmdIsOK=1
						if cmd.isdigit():
							for a in self.list_patch_file:
								#LOG().ln("==%s (%s) (%d) [%s] %s    [%s]"%(a["path"],a['ch'],a['id'],a["status"],a['filename'],a["select"]))			
								if int(cmd)==a['id']:
									a["select"]=1
									#LOG().ln(a['filename'])
									cmdIsOK=1
				if cmdIsOK==0:
					LOG().ln("\033[33;1mError input: %s\033[0m"%(cmdstr),1)
				else:
					isModify=1
			else:
				continue
	def patch_add(self):
		while True:
			cmds=raw_input("do you want to do 'GIT ADD'? [yes/no]:").split("\n")
			cmd=cmds[0]
			if cmd=="yes":
				break;
			elif cmd=="no":	
				return 1
		
		print("------11111---------------===")
		for lib in self.list_patch_file:
			os.chdir(self.DINT_PATH_DIR+"/"+lib["path"])
			project_path=lib["path"]
			project_lib=lib["name"]
			cmdout=SYSTEM().cmd("git add '%s'"%(lib["filename"])).split("\n")
			print(cmdout)
		print("------222222---------------===")
		while True:
			cmds=raw_input("do you want to do 'GIT Commint'? [yes/no]:").split("\n")
			cmd=cmds[0]
			if cmd=="yes":
				break;
			elif cmd=="no":	
				return 2
		print("------3333333---------------===")
		update_bug_number="0"
		while True:
			cmds=raw_input("Bug Number: ").split("\n") #1944552
			cmd=cmds[0]
			#if cmd=='q' or cmd=='Q' :
			#	exit(1)
			if cmd.isdigit():
				if (int(cmd)>100):
					#ALM_check.py pixi45-ct-v4.0-dint 1944552
					lines=SYSTEM().cmd("%s %s %s"%(self.alm_apply_app,self.default_revision,cmd)).split('\n')
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
		print("------4444444444---------------===")
		update_patch_comments=""
		while True:
			cmds=raw_input("Patch Comment(length>6): ").split("\n")
			cmd=cmds[0]
			if not cmd.isspace() and len(cmd)>6:
				update_patch_comments=cmd
				break
			else:
				continue
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
		while True:
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
		update_str+="###%%%product name:"+self.default_revision+"\n"
		update_str+="###%%%root cause:"+update_root_case+"\n"
		update_str+="###%%%Bug category:"+update_bug_category+"\n"
		if not update_generated_by=="":
			update_str+="###%%%Generated by:"+update_generated_by+"\n"
		update_str+="###%%%Module_Impact:"+update_module_impact+"\n"
		update_str+="###%%%Test_Suggestion:"+update_test_suggestion+"\n"
		update_str+="###%%%Solution:"+update_solution+"\n"
		update_str+="###%%%Test_Report:"+update_test_report+"\n"
		update_str+="###%%%Bug_Reason:"+update_bug_reason+"\n"
		update_str+="###%%%author email:"+self.user_email+"\n"

		ch_id=''
		for lib in self.list_patch_file:
			if lib['ch']!=ch_id:
				ch_id=lib['ch']
				os.chdir(self.DINT_PATH_DIR+"/"+lib["path"])
				str_commit="git commit -m '%s'"%(update_str)
				print(str_commit)
				cmdout=SYSTEM().cmd(str_commit).split("\n")
				print(cmdout)
		
		print("------5555---------------===")
		while True:
			cmds=raw_input("do you want to do 'GIT push'? [yes/no]:").split("\n")
			cmd=cmds[0]
			if cmd=="yes":
				break;
			elif cmd=="no":	
				return 3
	
		print("------666666---------------===")
		ch_id=''
		for lib in self.list_patch_file:
			if lib['ch']!=ch_id:
				ch_id=lib['ch']
				os.chdir(self.DINT_PATH_DIR+"/"+lib["path"])
				str_push="git push ssh://%s@10.92.32.10:29418/%s HEAD:refs/for/%s"%(self.user_name,lib["name"],self.default_revision)
				print(str_push)
				####git push ssh://chaofei.wu@10.92.32.10:29418/sdd3/mtk6735/build HEAD:refs/for/pixi45-ct-v4.0-dint
				cmdout=SYSTEM().cmd(str_push).split("\n")
				print(cmdout)
		
		print("------77777777777---------------===")
		return 0 # success OK
		
		
#-------------------
G = _G()

#------------------
def mtk_patch_merge(patch_dir=None,patch_file_name=None):
	LOG().ln("%s  %s"%(patch_dir,patch_file_name))

	if not patch_dir==None:
		G.PATCH_PATH_DIR=patch_dir
	if not patch_file_name==None:
		G.PATCH_FILE_NAME=patch_file_name
	LOG().ln("%s/%s"%(G.PATCH_PATH_DIR,G.PATCH_FILE_NAME))
	if not os.path.exists("%s/%s"%(G.PATCH_PATH_DIR,G.PATCH_FILE_NAME)):
		LOG().ln("error dir or file name")
		G.PATCH_PATH_DIR=None
		G.PATCH_FILE_NAME=None
		G.input_patch_dir()
	else:
		if not G.check_patch_name_dir()==1:
			LOG().ln("3333333333333333")
			G.input_patch_dir()

	repo_manifest_xml=G.DINT_PATH_DIR+"/"+".repo/manifest.xml"
	if not os.path.exists(repo_manifest_xml):
		LOG().ln("\033[31m\033[05mERROR! %s not found!!!\033[0m"%(repo_manifest_xml),1)
		exit(1)


	G.get_log_file_info()
	LOG().ln("Patch: "+G.PATCH_P_X_CURR)
	ret=G.get_git_lib(repo_manifest_xml)
	if ret==0:
		pass
	else:
		LOG().ln("\033[31m\033[05mERROR! %s parse fail! (%d)\033[0m"%(repo_manifest_xml,ret),1)
		exit(1)


	G.get_commit_list()
	G.print_git_lib_info()
	LOG().ln("Patch: "+G.PATCH_P_X_CURR)
	while True:
		cmds=raw_input("Please confirm update there patchs? [yes/no]:").split("\n")
		cmd=cmds[0]
		if cmd=="yes":
			break;
		elif cmd=="no":	
			exit(1)
			
	G.merge()

	mtk_patch_push()

def mtk_patch_push():
	while True:
		cmds=raw_input("Do you want to contine pushing code to server? [yes/no]:").split("\n")
		cmd=cmds[0]
		if cmd=="yes":
			break
		elif cmd=="no":	
			exit(1)	
	
	PATCH=_PATCH()
	PATCH.DINT_PATH_DIR=G.DINT_PATH_DIR
	PATCH.list_git_lib=G.list_git_lib
	PATCH.default_revision=G.default_revision
	
	PATCH.patch_check()
	PATCH.popup_window()


def readme():
	str = '''  
	eg:
	./swd3_patch.py -mtk <patch dir> <patch file name>
	./swd3_patch.py -push

	'''
	LOG().ln(str)
	
if __name__ == "__main__":
	#try:
		argvs = sys.argv[1:]
		#LOG().ln("%d  %s"%(len(argvs),argvs))
		if len(argvs) < 1:
			readme()
		elif '-mtk' in argvs:
			patch_dir=None
			patch_file_name=None
			if argvs>=2:
				patch_dir=sys.argv[2:3][0]
			if argvs>=3:
				patch_file_name=sys.argv[3:4][0]
				
			#patch_dir="/home/chaofei/2016/patch_merge_1/AP"
			#patch_file_name="ALPS02670712(For_jhz6580_we_3_m_alps-mp-m0.mp1-V2.34_P12).tar.gz"
		
			mtk_patch_merge(patch_dir,patch_file_name)
		else:
			if '-push' in argvs:
				mtk_patch_push()
	#except:
	#	LOG().ln("Error! have some errors!")
	#	exit(1)

        
