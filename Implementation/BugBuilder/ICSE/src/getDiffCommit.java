import java.io.*;
import java.util.ArrayList;

public class getDiffCommit {
    static  int flag = 1;//用来判断文件是否删除成功
    public static void main(String[] args) throws Exception {
     long start = System.currentTimeMillis();
//        getOriginalRepo();
         getDiffFile();
         System.out.println("finish step 1");
         removeComment.runRemoveComment();
        System.out.println("finish step 2");
         extractChange.runExactChange();
        System.out.println("finish step 3");
         CompileJavaTotalProject.runCompile();
    long end = System.currentTimeMillis();
        if((end -start) > 2400*1000) //设定运行时间为半小时
        {
            System.out.println("wuwuwu");

        }
    System.err.println("运行时间为"+ (end -start));

            //删除一个文件夹下的所有文件(包括子目录内的文件)
            File file = new File("/Users/x/ICSE/diff");//输入要删除文件目录的绝对路径
//        deleteAllFiles(file);
            if (flag == 1){
                System.err.println("文件删除成功！");
            }

    }

    private static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }
//    public static void deleteFile(File file){
//        //判断文件不为null或文件目录存在
//        if (file == null || !file.exists()){
//            flag = 0;
//            System.out.println("文件删除失败,请检查文件路径是否正确");
//            return;
//        }
//        //取得这个目录下的所有子文件对象
//        File[] files = file.listFiles();
//        //遍历该目录下的文件对象
//        for (File f: files){
//            //打印文件名
//            String name = file.getName();
//
//            //判断子目录是否存在子目录,如果是文件则删除
//            if (f.isDirectory()){
//                deleteFile(f);
//            }else {
//                f.delete();
//            }
//        }
//        //删除空文件夹  for循环已经把上一层节点的目录清空。
//
//    }

    public static void getOriginalRepo() throws Exception {

        //Step1 clone original repo
        for(int i=20;i<=20;i++)
        {
            String cmd1 ="/users/x/defects4j/framework/bin/defects4j checkout -p Lang -v "+i+"f -w /Users/x/defects4j/lang_"+i+"_fix";
            String r1 =execCmd(cmd1,null);
            String cmd2 ="/users/x/defects4j/framework/bin/defects4j checkout -p Lang -v "+i+"b -w /Users/x/defects4j/lang_"+i+"_buggy";
            String r2 = execCmd(cmd2,null);
        }


    }

    //step2
    public static void getDiffFile() throws Exception{
        ArrayList<String> version = Utils.readFile("/Users/x/ICSE/src/bug");


            int bugN =0;
            for(int i=0;i< version.size();i++)
            {

                System.out.println("=="+bugN);
                String csvPath="/Users/x/ICSE/diff/cmdDiff_lang"+bugN+".txt";
                String fixV = version.get(i);

                String buggyV=fixV.replace("_fix","_buggy");
                long start = System.currentTimeMillis();
                String cmd = "git diff -U99999 --word-diff=plain "+fixV+ " "+buggyV;
                long end = System.currentTimeMillis();

//                if((end - start) > 30*1000)
//                {
//                    System.out.println("wuwuwu");
//                    break;
//                }
                String result = execCmd(cmd, null);
                Utils.appendFile(result,csvPath);
                Utils.appendFile("\n",csvPath);
                bugN++;
                System.out.println(bugN);
            }

//

    }

    /**
     * 执行系统命令, 返回执行结果
     *
     * @param cmd 需要执行的命令
     * @param dir 执行命令的子进程的工作目录, null 表示和当前主进程工作目录相同
     */
    public static String execCmd(String cmd, File dir) throws Exception {
        StringBuilder result = new StringBuilder();

        Process process = null;
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;

        try {
            // 执行命令, 返回一个子进程对象（命令在子进程中执行）
            process = Runtime.getRuntime().exec(cmd, null, dir);

            // 方法阻塞, 等待命令执行完成（成功会返回0）
            process.waitFor();

            // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
            bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));

            // 读取输出
            String line = null;
            while ((line = bufrIn.readLine()) != null) {
                result.append(line).append('\n');
            }
            while ((line = bufrError.readLine()) != null) {
                result.append(line).append('\n');
            }

        } finally {
            closeStream(bufrIn);
            closeStream(bufrError);

            // 销毁子进程
            if (process != null) {
                process.destroy();
            }
        }

        // 返回执行结果
        return result.toString();
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public static void appendFile(String line, String path){
        FileWriter fw = null;

        try{
            File f = new File(path);
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter pw = new PrintWriter(fw);
        pw.print(line);
        pw.flush();
        try{
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
