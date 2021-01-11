import java.io.File;
import java.util.ArrayList;

public class CompileJava {
    public static void main(String[] args) throws Exception {
//       String old =getDiffCommit.fixV;
//       String path ="/Users/x/ICSE/lang"+getDiffCommit.bugNO;
//
//        int m =FilterJava.countFile(path);
//        ArrayList<Integer> res = FilterJava.filter(m,"");
//        System.out.println(res.size());
//        long start = System.currentTimeMillis();
//        long end;
//        long time;
//
//       for(int i=0;i<res.size();i++)
//       {
//           String oPath="/Users/x/ICSE/lang"+getDiffCommit.bugNO+"/patch"+res.get(i)+".java";
//
//           RC(old,oPath,old);
//           String s= compileFile("/Users/x/defects4j/lang_"+getDiffCommit.bugNO+"_fix");
//
//           if((!s.contains("FAIL")) || (!s.contains("FAILED")))
//           {
//               System.out.println("succeed : " + res.get(i));
//
//               System.out.println(oPath);
//               System.out.println(s);
//           }
//           end = System.currentTimeMillis();
//           time = end - start;
//           if(time > 1200*1000) //设定运行时间为半小时
//           {
//               System.out.println("wuwuwu");
//               break;
//           }
//       }
        runCompile();


    }

    public static void runCompile() throws Exception {
        ArrayList<String> vfix = Utils.readFile("/Users/x/ICSE/src/bug");

        for(int i=0;i< vfix.size();i++)
        {
            String old = vfix.get(i);
            String path ="/Users/x/ICSE/diff/cmdDiff_lang"+(i+17)+"t.txta/";

            int m = FilterJava.countFile(path);
            ArrayList<String> res = FilterJava.filter(path);
            System.out.println(res.size());

            long start = System.currentTimeMillis();
            long end;
            long time;

            for(int j=0;j<res.size();j++)
            {
                String oPath=path+res.get(j)+".java";

                RC(old,oPath,old);
                String s= compileFile("/Users/x/defects4j/lang_"+(i+17)+"_fix");

                if((!s.contains("FAIL")) || (!s.contains("FAILED")))
                {
                    System.out.println("succeed : " + res.get(i));
                    Utils.appendFile("succeed:" + res.get(i),"/Users/x/ICSE/diff/result.txt");
                    Utils.appendFile("\n","/Users/x/ICSE/diff/result.txt");

                    System.out.println(oPath);
                    Utils.appendFile(oPath,"/Users/x/ICSE/diff/result.txt");
                    Utils.appendFile("\n","/Users/x/ICSE/diff/result.txt");
                    System.out.println(s);
                    Utils.appendFile(s,"/Users/x/ICSE/diff/result.txt");
                    Utils.appendFile("\n","/Users/x/ICSE/diff/result.txt");
                }
                end = System.currentTimeMillis();
                time = end - start;
                if(time > 1200*1000) //设定运行时间为半小时
                {
                    System.out.println("wuwuwu");
                    break;
                }
            }


        }
    }

    public static void RC(String origialFile, String oFile, String nFile) throws Exception {
        deleteFile(origialFile);
        copyFile(oFile,nFile);
    }

    //编译项目并测试
    public static String compileFile(String file) throws Exception {
        String compile = "/users/x/defects4j/framework/bin/defects4j compile";
        String test     = "/users/x/defects4j/framework/bin/defects4j test";
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
