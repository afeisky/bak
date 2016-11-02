#!/bin/bash
p=( $1 $2)
p1=$1 #"alps/frameworks/base/api/system-current.txt"
p2=$2"/copy_to" #"copy_to"
function readme() {
cat <<EOF
-readme-----------------------------------chaofei.wu add-------
     copy.sh <source_file/dir>   <target_dir>
     eg: copy "alps/build/build.sh"  "./"
     拷贝文件,自动创造路径   taget dir is copy_to ,这个文件放到 ubuntu /sbin/copy 
 ------------------------------------------------------------------
EOF
}
function copy_file()
{
    #echo "$p1 $p2"
    f1=$p1
    #echo "$f1"
    d1=''
    if [ -f  $f1 ]; then
         #echo "--1"
         d1=${f1%/*}
	 if [ ! -d $p2"/"$d1 ]; then
	     #echo "--2"
	     mkdir -p $p2"/"$d1 
         fi  
	 cp -f $p1 $p2"/"$p1
    elif [ -d $f1 ]; then
          #echo ${f1: -1}
          if [ ${f1: -1} == "/" ]; then
		#echo "/"
		f1=${f1%/*}
          fi
          d1=${f1%/*}
          if [ ! -d $p2"/"$d1 ]; then
	     #echo "mkdir -p $p2"/"$d1"
	     mkdir -p $p2"/"$d1
         fi
	 #echo "cp -r $p1 $p2"/"$d1"
	 cp -r $p1 $p2"/"$d1
    else
          echo -e "\033[31m \033[05m not found!  [$f1]\033[0m"
	  return 1
    fi
    echo "$p2"/"$p1"
}

if [ "$1" != "" ] && [ "$2" != "" ];then
     copy_file $p1 $p2
else
      readme
fi

