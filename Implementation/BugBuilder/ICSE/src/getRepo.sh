#!/bin/bash
for LINE in `cat /Users/x/ICSE/src/bug`
do
#    echo $LINE
     #拆分字符串到数组
   str=$LINE
   OLD_IFS="$IFS"
   IFS=";"
   arr=($str)
   IFS="$OLD_IFS"

 #遍历回显数组
 # shellcheck disable=SC2068
 for s in ${arr[@]}
  do
#   echo "$s"
   # shellcheck disable=SC1068
   t=${s/fix/buggy}
#   echo "$t"
   git diff -U99999 --word-diff=plain $s $t
  done

# ./getRepo.sh >/Users/x/ICSE/diff/cmdDiff_lang0.txt


#----------------------
# clone repo

done

