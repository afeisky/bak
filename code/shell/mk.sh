#!/bin/bash
CMD=$1  
PROJECT=$2 # soul35

function readme(){
cat <<EOF
  -------readme------------------
EOF
  printf " -------------
  Usage:
    ./mk.sh "project9"    
 ------------------------------------
"
}
prj=('project1' 'project2')
prj=(${prj[@]} "default")

function _array(){
    echo "function param: $1 $2"  #---
	if [ "$CMD" = "" ]||[ "$PROJECT" = "" ]; then 
	   for (( i=0; i<${#prj[@]}; i++ )); do  # sort:
		  for (( n=0; n<(${#prj[@]}-i-1); n++ )); do			
			if [ "${prj[$(($n))]} " \> "${prj[$(($n+1))]}"  ]; then
				item=${prj[$(($n))]} 
				prj[$(($n))]=${prj[$(($n+1))]} 
				prj[$(($n+1))]=$item
			fi
		  done
	   done

	   local n=0
	   for i in `echo ${prj[@]} | sort`; do
		   n=$(($n+1))
		   echo "  $n):  $i"
	   done
	fi
    
    return 1  #----
}

if _array "test_array" "ok_ko" ;then  #---
  echo "success!"
fi

unset prj  #---

_array "111" "222"

function get_make_command()
{
  echo command make
}

function _make(){
    local start_time=$(date +"%s")
    $(get_make_command) "$@"
    local ret=$?
    local end_time=$(date +"%s")
    local tdiff=$(($end_time-$start_time))
    local hours=$(($tdiff / 3600 ))
    local mins=$((($tdiff % 3600) / 60))
    local secs=$(($tdiff % 60))
    local ncolors=$(tput colors 2>/dev/null)
    if [ $hours -gt 0 ] ; then
        printf "(%02g:%02g:%02g (hh:mm:ss))" $hours $mins $secs
    elif [ $mins -gt 0 ] ; then
        printf "(%02g:%02g (mm:ss))" $mins $secs
    elif [ $secs -gt 0 ] ; then
        printf "(%s seconds)" $secs
    fi
}
_make

function get_abs_build_var()
{
    T=$(gettop)
    if [ ! "$T" ]; then
        echo "Couldn't locate the top of the tree.  Try setting TOP." >&2
        return
    fi
    (\cd $T; CALLED_FROM_SETUP=true BUILD_SYSTEM=build/core \
      command make --no-print-directory -f build/core/config.mk dumpvar-abs-$1)
}

function gettop(){
   PWD= /bin/pwd
   echo $PWD
}

#----------------------------------------------
#readme: source mk.sh , input `echo $TEST1` in ubuntu cmd line
function set_stuff_for_environment()
{
    #----type echo $VARIANT_1 in ubuntu cmd line
    export TEST1="OK"
    export BUILD_TOP=$(gettop)
    export GCC_COLORS='error=01;31:warning=01;35:note=01;36:caret=01;32:locus=01:quote=01'
    export TEST_OPTIONS=detect_leaks=0
}

set_stuff_for_environment
#------------------------------------------








