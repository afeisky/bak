#!/usr/bin/python
import sys
import os
import re
import pexpect
import getopt
from commands import *

BAND_LIST = ['EU','LATAM1','LATAM2','MEA','APAC']
MAKE_FILE_LIST = ['JHZ6753_65T_3_M0(LWG_DSDS).mak']
BAND=""
MAKE_FILE = ""
PROJECT=""

_DEBUG = True

MODEM_DIR="/modem/lwg"

def modifyFiles():
    out = getstatusoutput('find mtk6753_wimdata_ng/wprocedures/*/isdm_sys_makefile.plf | xargs grep \'<SDMID>RUSSIA_FDN_MODEM_REQ</SDMID>\'')
    files = []
    if out[0] == 0:
        for line in out[1].split('\n'):
            match = re.search(r'(mtk6753_wimdata_ng/wprocedures/.*?/isdm_sys_makefile\.plf):', line)
            if match:
                files.append(match.group(1))
    if not files:
        return
    file = files[0] if files.__len__() == 1 else [x for x in files if x.find('/jrdhz/') < 0][0]
    flag = False
    print file
    with open(file) as f:
        for line in f.readlines():
            if line.find('<SDMID>RUSSIA_FDN_MODEM_REQ</SDMID>') > -1 and not flag:
                flag = True
                continue
            if flag:
                match = re.search(r'<VALUE>"?(.*?)"?</VALUE>', line)
                if match:
                    flag = match.group(1)
                    break
    print flag
    if flag:
        out = getstatusoutput('cat modem/make/JHZ6753_65T_3_M0\(LWG_DSDS\).mak | grep JRD_FDN_OPTR')
        if out[0] == 0:
            for line in out[1].split('\n'):
                match = re.search('^(JRD_FDN_OPTR\s?)=.*?$', line)
                if match:
                    os.system('sed \'s/^{0}=.*/{0}={1}/\' modem/make/JHZ6753_65T_3_M0\(LWG_DSDS\).mak -i'.format(match.group(1), flag))
 
def copyfile(band, root):
    os.chdir(root)
    os.system('./device/mediatek/build/build/tools/modemRenameCopy.pl ./modem "%s"' % PROJECT_MODEM)
    stat = getstatusoutput('find *_wimdata_ng/wcustores/*/Modem/%s/' % band)
    if stat[0] == 0:
        copy(stat[1].split('\n')[0])
    copy("vendor/mediatek/proprietary/modem/jhz6753_65t_3_m0_lwg_dsds")
    
#-------------
def copy_band_and_compile(band,makefile):
    print "*******************************************************"
    print("     Compiling %s ,%s  " % (band,makefile))
    print "*******************************************************"
    # check if band directory exists in path "./tct/band/"
    if band and os.path.exists('./tct/band/%s' % band):  
        key1 = os.system('cp ./tct/band/%s/* -dpRv ./custom/modem/' % band)
        #key2 = os.system('rm ./custom/modem/l1_rf/MT6735_2G_MT6169_CUSTOM/m12193.h')
        #key3 = os.system('rm ./custom/modem/l1_rf/MT6735_2G_MT6169_CUSTOM/m12190.c')
        key5 = os.system('cp ./tct/band/%s/l1_rf/MT6735_2G_MT6169_CUSTOM/m12193.h ./modem/gl1/l1_dm/l1d_ext/m12193.h' % band) 
        key6 = os.system('cp ./tct/band/%s/l1_rf/MT6735_2G_MT6169_CUSTOM/m12190.c ./modem/gl1/l1_dm/l1d_ext/m12190.c' % band)
        if key1 == 0 and key5 == 0 and key6 == 0:
            flag = os.system('./m "%s" new' % MAKE_FILE)
            return flag
    elif not band:
        flag = os.system('./m "%s" new' % MAKE_FILE)
        return flag
    else:
        print "RF FILES DO NOT EXIST, Please check ./tct/band/*!"
        return 0
def copy_rename_out_modem(band,makefile,project,projectNoFlavorUC,flavorUC):
    os.system('rm -fr ./temp_modem/*')
    os.system('../../device/mediatek/build/build/tools/modemRenameCopy.pl ./ "%s"' % makefile)
    os.system('cp -fr ./mtk_rel/%s/%s/dhl/database/mcddll.dll ./temp_modem/' % (projectNoFlavorUC,flavorUC))
    if len(project)>0:
        glist=get_modem_list(project)
        project_modem_dir=""
        if len(glist)==1:
            project_modem_dir=glist[0]
        elif len(glist)==2: # lwg_dsds / lttg_dsds
            if glist[1].lower().find("lwg")>0 and makefile.lower().find("lwg")>0:
                project_modem_dir=glist[1]
            elif glist[1].lower().find("lttg")>0 and makefile.lower().find("lttg")>0:
                project_modem_dir=glist[1]
            if glist[0].lower().find("lwg")>0 and makefile.lower().find("lwg")>0:
                project_modem_dir=glist[0]
            elif glist[0].lower().find("lttg")>0 and makefile.lower().find("lttg")>0:
                project_modem_dir=glist[0]
            else:# it is cdma2000 ?
                project_modem_dir=glist[0]
        else:
             pass
        print(project_modem_dir)
        if len(project_modem_dir)>0:
            vendor_modem_dir="../../vendor/mediatek/proprietary/modem/%s/" % (project_modem_dir)
            os.system('rm -fr ./%s/*' % vendor_modem_dir)
            os.system('mkdir -p ./%s' % vendor_modem_dir)
            os.system('cp -fr ./temp_modem/* ./%s/' % vendor_modem_dir)
            os.system('cp -f ../../device/mediatek/build/build/tools/modem/modem_Android.mk %s/Android.mk' %(vendor_modem_dir))
            print("modem: %s"%(vendor_modem_dir))


def copy_to_out_project(band,makefile):
    stat = getstatusoutput('find *_wimdata_ng/wcustores/*/Modem/%s/' % band)
    if stat[0] == 0:
        copy(stat[1].split('\n')[0])
    copy("vendor/mediatek/proprietary/modem/jhz6753_65t_3_m0_lwg_dsds")

def get_modem_list(project):
    projectconfigmk='../../device/jrdchz/%s/ProjectConfig.mk' % project
    print('cat %s | grep "CUSTOM_MODEM"' % projectconfigmk)
    glist=getstatusoutput('cat %s | grep "CUSTOM_MODEM"' % projectconfigmk)
    list_modem=[]
    if len(glist)>0:
        strCustomModem=glist[1]
        match=re.search("=(.*) (.*)",strCustomModem)
        if match:
           list_modem.append(match.group(1))
           list_modem.append(match.group(2))
        else:
           match=re.search("=(.*)",strCustomModem)
           list_modem.append(match.group(1))
        print(list_modem[0]+","+list_modem[1])
    return list_modem


def sys_exit(build_path,root):
   if build_path==2:
      os.chdir(root)
   sys.exit(1)

def readme():
    print "./%s EU <project name> <.mak>   " % sys.argv[0]
    print "./%s EU <jhz6753_65u_3_m0> <JHZ6753_65T_3_M0(LWG_DSDS).mak> " % sys.argv[0]
    print "./%s MEA <elsa6> <JHZ6753_65T_3_M0(LWG_DSDS).mak> " % sys.argv[0]


if __name__ == "__main__":

    BAND=BAND_LIST[0]
    MAKE_FILE=MAKE_FILE_LIST[0]    
    PROJECT = ""

    root = os.getcwd() # os.path.dirname(os.path.abspath(__file__))
    path = os.getcwd()
    aaa = os.path.dirname(os.path.abspath(__file__))
    bbb = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    print("=1="+aaa)
    print("=2="+bbb)
    #if _DEBUG:
        #print(BAND)
        #print(MAKE_FILE)
        #print(path)
        #print(sys.argv[0])
    exist1=os.path.exists(root+"/make")
    exist2=os.path.exists(root+"/mtk_rel")
    build_path=0
    if exist1 and exist2:
        build_path=1
    else:
        exist1=os.path.exists(root+"/modem")
        exist2=os.path.exists(root+"/device")
        exist3=os.path.exists(root+"/vendor")
        if exist1 and exist2 and exist3:
            build_path=2
            path=root+"/"+MODEM_DIR
        else:
            build_path=0
            readme()
    #print(build_path)
    str_error=""
    if build_path==2:
       os.chdir(path)
    if len(sys.argv) == 3 or len(sys.argv) == 4:
        p1=sys.argv[1]
        p2=sys.argv[2]
        p3=""
        if len(sys.argv) == 4:
            p3=sys.argv[3]
        print(sys.argv[0]+","+p1+","+p2+","+p3)

        if os.path.exists("./tct/band/%s" % (p1)):
            BAND=p1
        else:
            print("Error: "+"./tct/band/%s" % (p1))
            readme()
            sys_exit(build_path,root)
        
        if p2.find(".mak")>0:
            MAKE_FILE=p2           
            PROJECT=p3
        elif p3.find(".mak")>0:
            MAKE_FILE=p3           
            PROJECT=p2
        else:
            PROJECT=p2

        if not os.path.exists("./make/"+MAKE_FILE):
            print("Error: "+"./make/"+MAKE_FILE)
            readme()
            sys_exit(build_path,root)

        if len(PROJECT)>0:
            #print("../../device/jrdchz/"+PROJECT)
            if not os.path.exists("../../device/jrdchz/"+PROJECT):
                print("Error: "+"../../device/jrdchz/"+PROJECT)
                readme()
                sys_exit(build_path,root)
    else:
        pass
    #--
    print(sys.argv[0]+' : '+BAND+', "'+MAKE_FILE+'",'+PROJECT)

    flavorUC = "DEFAULT"# default flavor is "DEFAULT"
    projectNoFlavorUC=""
    match=re.search("(.*)\((.*)\).*",MAKE_FILE)  #if ($project =~ /(.*)\((.*)\)/)
    if match:
        projectNoFlavorUC=match.group(1)
        flavorUC=match.group(2)
    else:
        match=re.search("(.*).mak",MAKE_FILE)
        projectNoFlavorUC=match.group(1)
    #print("%s,%s" % (projectNoFlavorUC,flavorUC))

    #copy_band_and_compile(BAND,MAKE_FILE)
    #copy_rename_out_modem(BAND,MAKE_FILE,PROJECT,projectNoFlavorUC,flavorUC)

    sys_exit(build_path,root)

#--------------------------------------------------------
