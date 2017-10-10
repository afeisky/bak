#!/usr/bin/python
# coding:utf-8

"""
#Copyright (C) 2017 The TCL Mobile
Script Name: tctpatch.py
Program: chaofei.wu.hz@tcl.com, 2017-09-22
Info: once push .repo's all files changed.

Usage: tctpatch.py <...>

"""
import os
import sys
import re
import time,datetime
import glob
import commands
from commands import *
from time import strftime, localtime
from xml.dom.minidom import parse
import xml.dom.minidom
import urllib
import MySQLdb
import webbrowser


#    os.system("ls")  
#    p = Popen("cp -rf a/* b/", shell=True, stdout=PIPE, stderr=PIPE)
#    p.wait()
#    if p.returncode != 0:
#        print "Error."
#        return -1
#    status, output = commands.getstatusoutput("ls")
#    output = commands.getoutput("ls")
#    status = commands.getstatus("ls")

global TCL_PATH_KEY

def Readme():
    print('Usage: tclpatch.py <...> for (OpenJDK8)java -jar tclpatch.jar\n')


    def GitEmail(self, email):
        temp = os.popen("git config --list | grep user.email | sed -e 's/.*=//'")
        for lines in temp.readlines():
            email.append(lines[:-1])
        return email


def gitStatus(project_path):
    remove_dir = {"prebuilts/python/linux-x86/2.7.5/lib/python2.7", "modem/build", "modem/out_modem",
                  "development/version/include/"}
    #print(project_path)
    #print(project_lib)
    if not os.path.exists(project_path):
        return "Error: not found %s"%project_path
    os.chdir(project_path)
    path_name = commands.getoutput(
        "git remote -v | tail -1 | awk -F' ' '{print $2}' | sed -e 's/.*://' -e 's/.git//' -e 's/\//.?/g'")
    file_d = commands.getoutput("git status -s |awk '/D /' | awk '{print  $2}'").split("\n")
    file_m = commands.getoutput("git status -s |awk '/M /' | awk '{print  $2}'").split("\n")
    file_a = commands.getoutput("git ls-files -o --exclude-standard").split("\n")
    gitlist = []
    ch_id = 'a'
    for f in file_d:
        #print("-d--"+f)
        if len(f) > 0:
            pathfname = "%s/%s" % (project_path, f)
            is_valid = True
            for rd in remove_dir:
                if pathfname.find(rd) == 0:
                    is_valid = False
            if is_valid:
                one = {"ch": ch_id, "id": len(gitlist) + 1, "path": project_path, "filename": f,
                       "status": "D", "flag": 0, "ok": 0, "*": " "}
                gitlist.append(one)
    for f in file_m:
        #print("-m--"+f)
        if len(f) > 0:
            pathfname = "%s/%s" % (project_path, f)
            is_valid = True
            for rd in remove_dir:
                if pathfname.find(rd) == 0:
                    is_valid = False
            if is_valid:
                one = {"ch": ch_id, "id": len(gitlist) + 1, "path": project_path,  "filename": f,
                       "status": "M", "flag": 0, "ok": 0, "*": " "}
                gitlist.append(one)
    for f in file_a:
        #print("-a--"+f)
        if len(f) > 0:
            pathfname = "%s/%s" % (project_path, f)
            is_valid = True
            for rd in remove_dir:
                if pathfname.find(rd) == 0:
                    is_valid = False
            if is_valid:
                one = {"ch": ch_id, "id": len(gitlist) + 1, "path": project_path, "filename": f,
                       "status": "A", "flag": 0, "ok": 0, "*": " "}
                gitlist.append(one)
    if len(gitlist) == 0:
        #print("\033[31m\033[1mError: WARNING!!! not found the modified files.\033[0m")
        print("Error: WARNING!!! not found the modified files.")
        exit(1)

    # sort asc:

    # set git lib --> a,b,c,....z
    path = "*"
    for a in gitlist:
        ch_id = chr(ord(ch_id) + 1)
        path = a["path"]
        #print(" \033[31;1m%c\033[0m [%s]:"%(ch_id,a["path"]))
    print("[TCL*PATCH]")
    print(gitlist)

def getalmbranch(project):
    url = 'http://10.92.32.10/gitweb.cgi?p=scm_tools.git;a=blob_plain;f=conf/almconfig'
    branch =[]
    f = urllib.urlopen(url, proxies={})
    for line in f:
        line = line.strip()
        line = line.replace('"','')
        almbranch = line.split(',')
        if project.find(almbranch[0])>=0:
            return almbranch
        #if project in almbranch[0]:
        #    return almbranch
    return branch

def checkBugNumber(alm_apply_app,default_revision,cmd):
    # /wcf/tools/scm_tools/tools/ALM_check.py 'pixi4-5-4g-orange-v1.0-dint' 5343679
    # /wcf/tools/scm_tools/tools/ALM_check.py 'pixi3-4.5-4g-v1.0-dint' 5327041
    # /wcf/tools/scm_tools/tools/test.py 'bugNumberCheck' '/wcf/tools/scm_tools/tools/ALM_check.py' 'pixi3-4.5-4g-v1.0-dint' 5327041
    print(alm_apply_app, default_revision, cmd)
    allbranchs=getalmbranch(default_revision)
    print(allbranchs)
    #if len(allbranchs) >= 2:
    #    del allbranchs[0:2]
    lines = commands.getoutput("%s %s %s" % (alm_apply_app, default_revision, cmd)).split('\n')
    ret = lines[len(lines) - 1]
    g_is_mtk_patch=False
    bug_title=''
    bug_dint = ''
    print("ret=%s," % (ret))
    if len(lines) >= 5:
        bug_title = lines[len(lines) - 5]
        bug_dint = lines[len(lines) - 3]
        print("bug_title=%s," % (bug_title))
        find=False
        if bug_title.find("MTK patch") >= 0 or bug_title.find("ALPS")>=0 or bug_title.find("AP P") >= 0:
            print("1111")
            find=True
        if bug_title.find("patch merge") >= 0 and bug_title.find("AP P") >= 0:
            print("2222")
            find=True
        if find:
            print("It is \033[32;1mMTK Patch\033[0m")
            g_is_mtk_patch = True
    if ret.isdigit():
        if int(ret) == 200:
            print("\033[32;1mOK! go next\033[0m")
            update_bug_number = cmd
            print('[TCL*PATCH]')
            if ('all' not in allbranchs and (bug_dint not in allbranchs)):
                print("Error!!bug's branch(%s) not belong %s,\nplease check your bug is right!!!)"%(bug_dint,default_revision))
            else:
                print('{"result":1,"is_mtk_patch":%d,"title":"%s","dint":"%s"}'%(g_is_mtk_patch,bug_title,bug_dint))
        elif int(ret) == 201:
            print("\033[31;1mState isn't Resolved or Opened ,please check status of task or defect!\033[0m")
            print('[TCL*PATCH]')
            print("State isn't Resolved or Opened ,\nplease check status of task or defect!")
        elif int(ret) == 206:
            print("\033[31;1mThis id doesn't have any related utc , please check bugid or contact with SPM!\033[0m")
            print('[TCL*PATCH]')
            print("This id doesn't have any related utc ,\n please check bugid or contact with SPM!")
        elif int(ret) == 404:
            print("This id doesn't exist in Integrity , please check bugid!")
            print('[TCL*PATCH]')
            print("This id doesn't exist in Integrity ,\n please check bugid!")

        elif int(ret) == 500:
            print("\033[31;1mError while requesting the Integrity Server , please contact with INT!\033[0m")
            print('[TCL*PATCH]')
            print("Error while requesting the Integrity Server , please contact with INT!")

        elif int(ret) == 505:
            print("\033[31;1mNO Platform Supported , please check again or contact with SPM!\033[0m")
            print('[TCL*PATCH]')
            print("NO Platform Supported , \nplease check again or contact with SPM!")

        elif int(ret) == 600:
            print("\033[31;1mThe defect/task state is not Opened or Resolved , please check!\033[0m")
            print('[TCL*PATCH]')
            print("The defect/task state is not Opened or Resolved , please check!")
        else:
            print("\033[31;1mError bug number, please check!\033[0m")
            print('[TCL*PATCH]')
            print("Error bug number, please check!")

def gitAdd(gitpath,filename):
    print("git add %s,%s" % (gitpath,filename))
    if not os.path.exists(gitpath):
        print("Error: folder not exists")
        return
    os.chdir(gitpath)
    print("===1")
    cmd="git add '%s'" % (filename)
    print("%s" % (cmd))
    commands.getoutput(cmd)
    print('[TCL*PATCH]')
    print('{"result":1}')

def gitRm(gitpath,filename):
    print("git rm %s,%s" % (gitpath,filename))
    if not os.path.exists(gitpath):
        print("Error: folder not exists")
    os.chdir(gitpath)
    print("===2")
    if (os.path.isdir(filename)):
        cmd = "git rm -r '%s'" % (filename)
    else:
        cmd="git rm '%s'" % (filename)
    print("%s" % (cmd))
    commands.getoutput(cmd)
    print('[TCL*PATCH]')
    print('{"result":1}')


def gitSubmitAndPush(gitpath,gitname,default_revision,comments):
    print("%s" % (comments))
    comments=comments[1:len(comments)-1]
    comments=comments.replace('&nbsp;###%%%','\n###%%%')
    print("%s" % (comments))
    #comments = comments.replace('\&nbsp;', '&nbsp;')
    comments = comments.replace('&nbsp;', ' ')
    comments = comments.replace('###%%%', '\n###%%%')
    if comments.find('\n')==0:
        comments=comments[1:len(comments)]
    print("%s,%s,%s,%s" % (gitpath, gitname, default_revision, comments))
    if not os.path.exists(gitpath):
        print("Error: folder not exists")
        return
    user_name = commands.getoutput("git config --list | grep user.name | sed -e 's/.*=//'")
    user_email = commands.getoutput("git config --list | grep user.email | sed -e 's/.*=//'")
    os.chdir(gitpath)
    cmd="git commit -m '%s'" % (comments+"\n"+"###%%%author email:"+user_email+"\n")
    print("%s" % (cmd))
    lines=commands.getoutput(cmd)
    print("commit=%s," % (lines))
    pushUrl=''
    if True:
        cmd = "git push ssh://%s@10.92.32.10:29418/%s HEAD:refs/for/%s" % (user_name, gitname, default_revision)
        cmd_no_thin = "git push --no-thin ssh://%s@10.92.32.10:29418/%s HEAD:refs/for/%s" % (user_name, gitname, default_revision)
        #cmd=cmd_no_thin;
        print("%s" % (cmd))
        lines=commands.getoutput(cmd)
        print("push=%s," % (lines))
        line = lines
        strS = 'http://10.92.32.45:8081/'
        nPos = line.find(strS)
        if nPos > 0:
            pushUrl = line[nPos:nPos + len(strS) + 7].strip()
        if len(pushUrl)==0:
            for line in lines:
                if nPos>0:
                    pushUrl.append(line[nPos:nPos+len(strS)+7]).strip()
        print(pushUrl)
    print('[TCL*PATCH]')
    if len(pushUrl)>0:
        print('{"result":1,"pushurl":"%s"}'%pushUrl)
    else:
        print('{"result":0,"comment":"error!"}')

def smarttask(gitPath):
    pass

def get_git_branch():
    branch = " "
    branch = getoutput("git branch -a | grep '\->' | sed -e 's/.*jgs.//'")
    return branch

class dotProjectDb:
    def __init__(self):
        self.get_db_connection()
        self.unmgerged_importid_list = []

    def get_db_connection(self):
        #try:
        print("%s Connect to prsm database" % datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
        self.db_conn = MySQLdb.connect(host="10.92.35.20", port=3306, user="INT_PATCH", passwd="Aa123456",db="dotproject",charset="utf8") #, charset="gbk"latin1
        self.db_cursor = self.db_conn.cursor()
        return self.db_cursor
        #except Exception:
        #    print("Error: faile to connect dabatase ")
        #    sys.exit(1)

    def close_db(self):
        print("%s Close the connection of prsm database..." % datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
        if self.db_conn:
            print('Closed DB connecion.')
            self.db_conn.close()

    def query(self, sql, one_record=True):
        try:
            self.db_cursor.execute(sql)
            if one_record:
                result = self.db_cursor.fetchone()
            else:
                result = self.db_cursor.fetchall()
            return result
        except  e:
            print("Exception:%s\nWhile execute %s" % (e, sql))
            return null

    def get_import_name_and_id(self,DevBranchName):
        #print("DevBranchName", DevBranchName)
        result={}
        try:
            mysql = 'SELECT project_id,import_name FROM dotp_cts_branchs WHERE  branch_name="%s" group by project_id order by project_id DESC ' % DevBranchName
            result = self.query(mysql, True)
            print(result)
            if result:
                print("dint_branch= %s, import_id=%d, import_name=%s)"%(DevBranchName,result[0],result[1]))
            else:
                print("can not get project id from dotp_cts_branchs")
        except Exception:
            print("connect to prsm failed")
        return result[0],result[1]

    def get_version_and_pnumber(self,DevBranchName,patchType="ALPS"):
        #print("DevBranchName", DevBranchName)
        projectID = ''
        result=''
        #mysql='select * from dotp_mtk_import where project_id=%d and id in (SELECT import_id FROM dotp_mtk_merge WHERE merge_status=0)'%(impor_id)
        mysql_vnum='select import_patch,vnum,pnum,eservice_id,comment from dotp_mtk_import where patch_type="%s" '\
            ' and project_id in (SELECT project_id FROM dotp_cts_branchs WHERE  branch_name="%s" '\
            ' group by project_id DESC) and id in (SELECT import_id FROM dotp_mtk_merge WHERE merge_patch="%s"' \
            ' and merge_status=0) group by vnum order by vnum'
        mysql_pnum='select import_patch,vnum,pnum,eservice_id,comment from dotp_mtk_import where patch_type="%s" '\
            ' and project_id in (SELECT project_id FROM dotp_cts_branchs WHERE  branch_name="%s" '\
            ' group by project_id DESC) and id in (SELECT import_id FROM dotp_mtk_merge WHERE merge_patch="%s"' \
            ' and merge_status=0) group by pnum order by pnum'
        import_name=''
        vnum=[]
        pnum = []
        try:
            mysql = mysql_vnum % (patchType, DevBranchName, DevBranchName)
            print(mysql)
            data = self.query(mysql, False)
            print(data)
            if data:
                for item in data:
                    print(item)
                    vnum.append(item[1])
                    import_name=item[0]
            else:
                print("can not get project id from dotp_cts_branchs")
            mysql = mysql_pnum % (patchType, DevBranchName, DevBranchName)
            print(mysql)
            data = self.query(mysql, False)
            print(data)
            if data:
                for item in data:
                    print(item)
                    pnum.append(item[2])
                    import_name = item[0]
            else:
                print("can not get project id from dotp_cts_branchs")
        except Exception:
            print("connect to prsm failed")
            return import_name, vnum, pnum
        return import_name,vnum,pnum

def getVersionAndPnumber(DevBranchName,patchType="ALPS"):
    import_name, vnum, pnum=dotProjectDb().get_version_and_pnumber(DevBranchName,patchType)
    print('[TCL*PATCH]')
    if pnum:
        strVnum=''
        strPnum=''
        if vnum:
            for item in vnum:
                if (len(strVnum)>0):
                    strVnum+=','
                strVnum += '"'+item+'"'

        if pnum:
            for item in pnum:
                if (len(strPnum)>0):
                    strPnum+=','
                strPnum += '"'+item+'"'
        print('{"result":1,"comment":"OK","import_name":"%s","vnum":[%s],"pnum":[%s]}'%(import_name,strVnum,strPnum))
    else:
        print('{"result":0,"comment":"Error: fail!"}')

def test():
    print('[TCL*PATCH]')
    print('{"result":1,"comment":"OK"}')






if __name__ == "__main__":
    #print(getVersionAndPnumber('mickey6t-n-v1.0-fsr'))

    params = []
    TCL_PATH_KEY='[TCL*PATCH]'
    if len(sys.argv[1:]) == 0:
        print('java -jar ' + os.getcwd() + '/tclpatch.jar')
        readme()
        sys.exit(1)

    if sys.argv[1] == 'test':
        test()
    elif sys.argv[1] == 'gitStatus':
        gitStatus(sys.argv[2])
    elif sys.argv[1]=="bugNumberCheck":
        checkBugNumber(sys.argv[2],sys.argv[3],sys.argv[4])
    elif sys.argv[1]=="gitAdd":
        gitAdd(sys.argv[2],sys.argv[3])
    elif sys.argv[1]=="gitRm":
        gitRm(sys.argv[2],sys.argv[3])
    elif sys.argv[1]=="gitSubmitAndPush":
        print(sys.argv[5])
        gitSubmitAndPush(sys.argv[2],sys.argv[3],sys.argv[4],sys.argv[5])
    elif sys.argv[1]=="smarttask":
        smarttask(sys.argv[2])
    elif sys.argv[1]=="getVersionAndPnumber":
        getVersionAndPnumber(sys.argv[2],sys.argv[3])
    elif sys.argv[1]=="openSmartTaskUrl":
        url = sys.argv[2] #'http://www.baidu.com'
        webbrowser.open(url)
        exit(1)
    elif len(sys.argv[1:]) == 1:
        print(sys.argv[0])
        jarFileName=''
        if (os.path.exists(sys.argv[0]) and sys.argv[0].find('tclpatch.py')>0):
            jarFileName=sys.argv[0].replace('tclpatch.py', 'tclpatch.jar')
        cmd='java -jar ' + jarFileName+' '+sys.argv[1]+' ' +sys.argv[0]
        print(cmd)
        commands.getoutput(cmd)
    else:
        print("Error: error parameter!!!")


# git reset HEAD^ && cd .. && cd development/   && git reset HEAD^ && cd .. && cd packages/apps/Browser2/  && git reset HEAD^ && git ../../../device

