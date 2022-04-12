import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

public class CompileJavaTotalProject {

    static ArrayList<String> ttlist = new ArrayList<>();
    static ArrayList<String> fileNo = new ArrayList<>();
    static ArrayList<String> failNumber = new ArrayList<>();

    public static void main(String[] args) throws Exception {

    }

    public static ArrayList<String> getCandidiate(ArrayList<String> input){
        ArrayList<String> res = new ArrayList<>();
        ArrayList<Integer> ftn = new ArrayList<>();
        for(int t=0;t<input.size();t++){
            String gftn = input.get(t).split("@#@")[0];
            ftn.add(Integer.valueOf(gftn));
        }

        Collections.sort(ftn);
        if(ftn.size() ==0){
            return res;
        }
        int failingTestNum = ftn.get(0);
//        System.out.println("failingTest Num: " + failingTestNum);
        for(int i=0;i< input.size();i++){
            String fn = input.get(i).split("@#@")[0];
            if(fn.equals(""+failingTestNum)){
                res.add(input.get(i));
            }
        }
//        System.out.println("test: "+res);
        return res; //返回failing test数量最小的候选patch的信息
    }

    public static int getFileOrder(String t){
        int p1 = t.lastIndexOf("/");
        String t1 = t.substring(0,p1);

        int p2 = t1.lastIndexOf("/");
        String t2 = t1.substring(p2+1);
        return Integer.valueOf(t2);
    }
    public static void runCompile( String finalPath, String origBug,String args1, String args2) throws Exception {
//        String diffPath =dir +"/src/diff";
        String diffPath ="./src/diff";
        File folder = new File(diffPath);
        File[] listOfFiles = folder.listFiles();
        long startTime= System.currentTimeMillis();

        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                String dPath = file.getAbsolutePath(); // dPath 是后缀为a的文件夹
                ArrayList<String> modifyFile =removeDuplicate(run(dPath));
                ArrayList<String> compiledPatch = refilter(dPath);
                replaceFile(compiledPatch,modifyFile,origBug, Setting.defects4jPath,finalPath,args1,args2);
                long endTime =System.currentTimeMillis();
                if(endTime-startTime>2400*1000){
                    return;
                }


            }
        }
    }
    @SuppressWarnings("unchecked")
    public static ArrayList<String> removeDuplicate(ArrayList list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    public static ArrayList<String> run(String dirPath) throws Exception { //返回值为项目中修改的文件名 dirPath 是后缀为a的文件夹的路径

        ArrayList<String> fixV =getAllPatchForEach(dirPath);
        ArrayList<String> oldVfix = getVFix(fixV);
        File folder = new File(dirPath);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                String dPath = file.getAbsolutePath();
                comAndFil(dPath);
            }
        }
        return oldVfix;
    }

    // step 得到有修改的文件名字,输入是getallpatchforeach的返回值
    public static ArrayList<String> getVFix(ArrayList<String> t) throws IOException {
        ArrayList<String> vfix = new ArrayList<String>();
        for(int i=0;i< t.size();i++){
            String info = t.get(i);
            int left = info.indexOf("/");
            int right = info.indexOf(".java");
            String q = info.substring(left, right+5);
            vfix.add(q);
        }
        return vfix;
    }

    //step1 从diff文件中分离出涉及到的java文件 dirPath = txta
    public static ArrayList<String> getAllPatchForEach(String dirPath) throws IOException {

        File folder = new File(dirPath);
        File[] listOfFiles = folder.listFiles();

        ArrayList<String> repo = new ArrayList<String>();
        ArrayList<String> mName= new ArrayList<String>();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String oPath = file.getAbsolutePath(); // 0.txt 的路径
                if(oPath.endsWith(".txt"))
                {
                    ArrayList<String> tt = Utils.readFile(oPath);

                    for(int t=0;t< tt.size();t++)
                    {
                        String temp= tt.get(t);
                        if(temp.startsWith("diff")){
                            repo.add(temp); //repo中存储的是diff 那一行信息
                            int left = temp.indexOf("/");
                            int right = temp.indexOf(".java");
                            String q = temp.substring(left, right+5);
                            int l2 = q.lastIndexOf("/");
                            int r2 = q.indexOf(".java");
                            String p = q.substring(l2+1,r2);
                            mName.add(p); // mName 中存储的是修改的java文件名字
                        }
                    }
                    int pos = oPath.lastIndexOf(".");
                    String nPath = oPath.substring(0,pos); // nPath 是去掉后缀.txt 文件路径
                    String[] splitAfter = splitFile(oPath);
                    int length = splitAfter.length;
                    for(int i=1;i<length ;i++)
                    {
                        genPatch.appendFile(splitAfter[i],nPath+"/",mName.get(i-1)+".java");
                    }
                }
            }
        }
        return repo;
    }

    // step2: 去除头文件生成预编译的java文件
    public static void comAndFil(String dirPath) {
        File folder = new File(dirPath);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String oPath = file.getAbsolutePath();
                ArrayList<String> originalF = Utils.readFile(oPath);
                ArrayList<String> removeF = removeHeader(originalF);
                for(int i=0;i<removeF.size();i++)
                {
                    Utils.appendFile(removeF.get(i),oPath.replace(".java","1.java"));
                    Utils.appendFile("\n",oPath.replace(".java","1.java"));
                }
            }
        }
    }

    // step3 : 对生成的patch进行动态编译 // 输入是txta文件的路径，输出是全部可编译的文件夹名称
    public static ArrayList<String> refilter(String path){
        ArrayList<String> compiledH = new ArrayList<String>();

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                String fName = file.getAbsolutePath();
//                System.out.println("数量文件夹" + fName);
              String compiledFile = compileEachFile(fName);
              compiledH.add(compiledFile);
            }
        }
        return compiledH;
    }
  // 输入是文件夹0的路径+ 该路径下文件的名称
    public static String compileEachFile(String eachDirPath){

        ArrayList<String> res = new ArrayList<String>();
        String succeedFile ="";
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int count=0;
        int filecount =getFileNumber(eachDirPath);
//        System.out.println("the number of file"+filecount);
        ArrayList<String> fileName =getFileName(eachDirPath);
        for(int i=0;i< filecount;i++)
        {
            String filePath = fileName.get(i);
            ByteArrayOutputStream err = new ByteArrayOutputStream();

            int result = compiler.run(null, null, err, filePath);
            if(err.toString().contains("import") || err.toString().contains("extends") || err.toString().contains("是公共的"))
            {
                result =0;
            }
            if (result == 0) {
                res.add(filePath);
            } else {
//              System.out.println(err.toString());
            }

        }
        if(filecount == res.size()){
//          System.out.println("全部编译成功");
            succeedFile=eachDirPath;
        }

        return succeedFile;

    }
    public static int getFileNumber(String path){
        int fileCount = 0;
        File d = new File(path);
        File list[] = d.listFiles();
        for(int i = 0; i < list.length; i++){
            if(list[i].isFile()) {
                String fname =list[i].getName();
                if(fname.endsWith("1.java")){
                    fileCount++;
                }
            }
        }
        return fileCount;
    }

     public static ArrayList<String> getFileName(String path){
        ArrayList<String> fileName= new ArrayList<String>();
         File d = new File(path);
         File list[] = d.listFiles();
         for(int i = 0; i < list.length; i++){
             if(list[i].isFile()) {
                 String fName = list[i].getName();
                 String fPath = list[i].getAbsolutePath();
                 if(fName.endsWith("1.java")){
                     fileName.add(fPath);
                 }
             }
         }
         return fileName;
     }

 // step 4 : 将动态编译成功的文件夹，放回fix项目编译测试
    public static void replaceFile(ArrayList<String> res, ArrayList<String> fixV,String origBug, String JPath, String finalPath,String args1,String args2) throws Exception {
//       System.out.println("======");
        ArrayList<String> removedNull = new ArrayList<String>();
        for(int q=0;q<res.size();q++)
        {
            String tmp = res.get(q);
            if(!tmp.equals("")) removedNull.add(tmp);
        }
        long start = System.currentTimeMillis();
        long time;
        long end;
        int fileorder;

        for(int t=0;t< removedNull.size();t++)
        {

            ArrayList<String>  dfPath = new ArrayList<>();
            ArrayList<String>  tOld= new ArrayList<>();
            ArrayList<String> getEachDir =getFileName(removedNull.get(t));

            Collections.sort(getEachDir, Collections.reverseOrder());
            Collections.sort(fixV, Collections.reverseOrder());

            if(getEachDir.size() != fixV.size())
            {
                return;
            }else{
                int show =1;
                for(int i=0;i< fixV.size();i++)
                {
                    String old = fixV.get(i);
                    String oPath=getEachDir.get(0);
                    RC(old,oPath,old);
                    getEachDir.remove(0);
//                    System.out.println("current patch: "+old + " ==" + oPath);
                    System.out.println("current patch: " + " ==" + oPath);
//                  System.out.println(oPath);
                    fileorder=getFileOrder(oPath);
//                   tOld=old;
//                  dfPath = oPath;
                    tOld.add(old);
                    dfPath.add(oPath);
//                    System.out.println(dfPath);
//                    System.out.println(tOld);
                }
//                ArrayList<String> version = Utils.readFile(dir +"/src/bug");
                String temp = origBug.substring(0,origBug.lastIndexOf("/src"));
//                System.out.println("--------" + temp);
                System.out.println("compile and test each candidate patch: (maybe cost long time)");
                String s= compileFile(temp,JPath);
//                if(show==1){
//                    System.out.println("The first version: "+s);
//                    show++;
//                }

                String lable ="";
                if((!s.contains("FAIL")) || (!s.contains("FAILED")))
                {
//                    System.out.println(s);
                    // 对输出信息进行处理

                    Pattern rDelete = Pattern.compile("Failing tests: "+"[0-9]\\d*");
                    Matcher mDelete = rDelete.matcher(s);

                    while (mDelete.find()) {

//                        System.out.println("========="+mDelete.group());
                        ttlist.add(mDelete.group()); // ttlist存储每个候选跑测试用例的failing tests数量
//                        System.out.println("*********" + tOld+ "," +getFileOrder(dfPath));
                        fileNo.add(tOld+ "@#@" +getFileOrder(dfPath.get(0)));
                    }
                }

                end = System.currentTimeMillis();
                time = end - start;
                if(time > 2400*1000) //设定运行时间为半小时
                {
                    System.out.println("time out");
                    return ;
                }
            }
        }

        for(int q=0;q< ttlist.size();q++){
            String[] eachL = ttlist.get(q).split(",");
            for(String s:eachL){
//                System.out.println("@@@@@@@@"+s);
                String t = s.split(" ")[2];
//                System.out.println("sdfafaf" + t);
                failNumber.add(Integer.valueOf(t)+"@#@"+fileNo.get(q));

            }
        }

       //--------------  add 9-24 ----------
        ArrayList<String> canNumbers = getCandidiate(failNumber); //canNumbers 是failing test最小的所有候选的patch信息
        int csize = canNumbers.size();
        if(csize ==1){
            System.out.println("generate one patch");
            String[] aline = canNumbers.get(0).split("@#@");
//            System.out.println("fileName: "+aline[2] +", failingTest: "+aline[0]);
//            String output=System.getProperty("user.dir")+"/src/finalPatch.txt";
            clearPatch.generatePatch(finalPath,args1,args2);
        }else{
           System.out.println("There are multiple candidate");
           selectMinPatch.getOne(canNumbers,finalPath,args1,args2);
        }
    }

    public static String[] splitFile(File file, String charset) throws IOException {

        //根据对应的编码格式读取
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
        StringBuffer content = new StringBuffer();
        String tmp = null;
        while ((tmp = reader.readLine()) != null) {
            content.append(tmp);
            content.append("\n");
        }
        String target = content.toString();
        String[] splitFile = target.split("diff --git");
        return splitFile;
    }
    public static String[] splitFile(String filePath) throws IOException {
        String[] result =splitFile(new File(filePath), "UTF-8");
        return result;
    }

    public static ArrayList<String> removeHeader(ArrayList<String> fjava){
        ArrayList<String> removeHead = new ArrayList<String>();
        for(int i=0;i<fjava.size();i++)
        {
            String fLine = fjava.get(i);
            if((fLine.startsWith(" a/")&&(fLine.endsWith(".java"))) || fLine.startsWith("index") || fLine.startsWith("+++") || fLine.startsWith("---") ||fLine.startsWith("@@"))
            {
//               System.out.println("****"+fLine);
            }else{

                removeHead.add(fLine);
            }
        }
        return removeHead;

    }


    public static void RC(String origialFile, String oFile, String nFile) throws Exception {
        deleteFile(origialFile);
        copyFile(oFile,nFile);
    }

    //编译项目并测试 ///defects4j
    public static String compileFile(String file, String JPath) throws Exception {
//        String compile = JPath+"/defects4j compile";
////      String compile = "mvn compile  -Dlicense.skip=true -Dcheckstyle.skip -Drat.skip -Denforcer.skip -Danimal.sniffer.skip -Dmaven.javadoc.skip -Dfindbugs.skip -Dwarbucks.skip -Dmodernizer.skip -Dimpsort.skip -Dmdep.analyze.skip -Dpgpverify.skip -Dxml.skip=true";
//        String test     = JPath+"/defects4j test";
//      String test = "mvn test -Dlicense.skip=true -Dcheckstyle.skip -Drat.skip -Denforcer.skip -Danimal.sniffer.skip -Dmaven.javadoc.skip -Dfindbugs.skip -Dwarbucks.skip -Dmodernizer.skip -Dimpsort.skip -Dmdep.analyze.skip -Dpgpverify.skip -Dxml.skip=true";
//        String compile="defects4j compile4bugmining -t " + tempdir;
        String compile="defects4j compile";
//        String test="defects4j test4bugmining -e " + tempdir;
        String test="defects4j test";
        String c = getDiffCommit.execCmd(compile , new File(file));
        String d = getDiffCommit.execCmd(test , new File(file));
//        System.out.println("compile and test::");
//        System.out.println(c + "++\n" +d);
        System.out.println(c);
        System.out.println(d);
        return c + "++\n" +d;

    }

    //删除原来的文件
    public static void deleteFile(String old) throws Exception {
        String removeFile = "rm " + old;
        getDiffCommit.execCmd(removeFile, null);
    }

    //复制进入新的文件
    public static void copyFile(String oPath, String nPath) throws Exception {
        String copyFile = "cp " + oPath + " " + nPath;
        getDiffCommit.execCmd(copyFile,null);
    }


}
