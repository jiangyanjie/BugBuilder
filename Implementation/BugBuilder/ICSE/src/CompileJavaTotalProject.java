import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class CompileJavaTotalProject {
    public static void main(String[] args) throws Exception {

    }

    public static void runCompile(String dir) throws Exception {
        String diffPath =dir +"/diff";
        File folder = new File(diffPath);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                String dPath = file.getAbsolutePath(); // dPath 是后缀为a的文件夹
                ArrayList<String> modifyFile =removeDuplicate(run(dPath));
                ArrayList<String> compiledPatch = refilter(dPath);
                replaceFile(compiledPatch,modifyFile,dir, Setting.defects4jPath);
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
    public static void replaceFile(ArrayList<String> res, ArrayList<String> fixV,String dir, String JPath) throws Exception {
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
        for(int t=0;t< removedNull.size();t++)
        {
            ArrayList<String> getEachDir =getFileName(removedNull.get(t));

            Collections.sort(getEachDir, Collections.reverseOrder());
            Collections.sort(fixV, Collections.reverseOrder());

            if(getEachDir.size() != fixV.size())
            {
                return;
            }else{
                for(int i=0;i< fixV.size();i++)
                {
                    String old = fixV.get(i);
                    String oPath=getEachDir.get(0);
                    RC(old,oPath,old);
                    getEachDir.remove(0);
//                  System.out.println(old + " ==" + oPath);
//                  System.out.println(oPath);
                }
                ArrayList<String> version = Utils.readFile(dir +"/bug");
                String temp = version.get(0).substring(0,version.get(0).lastIndexOf("/src"));
                String s= compileFile(temp,JPath);
                if((!s.contains("FAIL")) || (!s.contains("FAILED")))
                {
                    System.out.println(s);
                }

                end = System.currentTimeMillis();
                time = end - start;
                if(time > 2400*1000) //设定运行时间为半小时
                {
                    System.out.println("time out");
                    break;
                }
            }
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
            if(fLine.startsWith(" a/") || fLine.startsWith("index") || fLine.startsWith("+++") || fLine.startsWith("---") ||fLine.startsWith("@@"))
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
        String compile = JPath+"/defects4j compile";
//      String compile = "mvn compile -Dlicense.skip=true -Dcheckstyle.skip -Drat.skip -Denforcer.skip -Danimal.sniffer.skip -Dmaven.javadoc.skip -Dfindbugs.skip -Dwarbucks.skip -Dmodernizer.skip -Dimpsort.skip -Dmdep.analyze.skip -Dpgpverify.skip -Dxml.skip=true";
        String test     = JPath+"/defects4j test";
//      String test = "mvn test -Dlicense.skip=true -Dcheckstyle.skip -Drat.skip -Denforcer.skip -Danimal.sniffer.skip -Dmaven.javadoc.skip -Dfindbugs.skip -Dwarbucks.skip -Dmodernizer.skip -Dimpsort.skip -Dmdep.analyze.skip -Dpgpverify.skip -Dxml.skip=true";
        String c = getDiffCommit.execCmd(compile , new File(file));
        String d = getDiffCommit.execCmd(test , new File(file));
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
