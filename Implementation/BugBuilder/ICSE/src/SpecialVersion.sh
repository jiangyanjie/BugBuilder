#!/bin/bash
for LINE in `cat ./shaInfo`
do
    echo $LINE
     #拆分字符串到数组
   str=$LINE
   OLD_IFS="$IFS"
   IFS=","
   arr=($str)
   IFS="$OLD_IFS"

 #遍历回显数组
 # shellcheck disable=SC2068
 for s in ${arr[@]}
  do
   echo "$s"
  done
   #为自定义变量赋值
  P1=${arr[0]}
  P2=${arr[1]}
  P3=${arr[2]}
  repoName="Lang" # Name of Java project containing the given bug
  # Path to Defects4J,i.e.,where it has been installed
  defects4jPath="/users/yanjiejiang/defects4j/framework/bin/defects4j"
  BugBuilderPath="/Users/yanjiejiang/icse" # Path to BugBuilder, i.e., where it's source code has been placed

$defects4jPath checkout -p $repoName -v "$P1"b -w $BugBuilderPath/"$repoName"_"$P1"_buggy
$defects4jPath checkout -p $repoName -v "$P1"f -w $BugBuilderPath/"$repoName"_"$P1"_fix

cp -r $BugBuilderPath/"$repoName"_"$P1"_buggy/ $BugBuilderPath/buggy"$P1"/
cp -r $BugBuilderPath/"$repoName"_"$P1"_fix/ $BugBuilderPath/fixed"$P1"/

## git checkout to specific version
cd  $BugBuilderPath/buggy"$P1"/
git checkout $P2
#

\cp -rf $BugBuilderPath/buggy"$P1"/src/main $BugBuilderPath/"$repoName"_"$P1"_buggy/src/

cd $BugBuilderPath/"$repoName"_"$P1"_buggy

#$defects4jPath compile
#$defects4jPath test
cd  $BugBuilderPath/fixed"$P1"/
git checkout $P3
\cp -rf $BugBuilderPath/fixed"$P1"/src/main $BugBuilderPath/"$repoName"_"$P1"_fix/src/

cd $BugBuilderPath/"$repoName"_"$P1"_fix
#$defects4jPath compile
#$defects4jPath test

echo "**********END************"


done

