import java.io.*;
import java.util.ArrayList;

public class getDiffCommit {
    static  int flag = 1;//用来判断文件是否删除成功
    public static void main(String[] args) throws Exception {
        try{
            long start = System.currentTimeMillis();
            long end;
            // "/Users/yanjiejiang/icse/liqui-1633b/liquibase-core/src/main/java"
//            String dir = System.getProperty("user.dir");
//            System.out.println(dir);
            String buggyV = args[0];
            String fixV = args[1];
            String finalPatch = args[2];
//            String tempdir = args[3];

            getDiffFile(fixV,buggyV);
            System.out.println("finish step 1: generate the diff format file");
            removeComment.runRemoveComment();
            System.out.println("finish step 2: remove the comment");

            System.out.println("Start:: step3: generate candidate patches based on permutation and combination");
            extractChange.runExactChange();
            end = System.currentTimeMillis();
            if(end-start >= 2400*1000){
                System.out.println("time out");
                return;
            }
            System.out.println("finish step 3: generate candidate patches");

            System.out.println("Start:: step4: compile and test each candidate patch");

            CompileJavaTotalProject.runCompile(finalPatch,fixV,buggyV,fixV);
            end = System.currentTimeMillis();
            if(end-start >= 2400*1000){
                System.out.println("time out");
                return;
            }
//            String url = dir+"/src/diff";
            String url = "./src/diff";
            deleteAllFile.delFileMake(url);
            File file = new File("./");
            if(file.isDirectory()){
                File[] files = file.listFiles();
                for(File f:files){
                    String path = f.getAbsolutePath();
                    if(path.endsWith(".txt")){
                        deleteAllFile.deleteFile(new File(path));
                    }
                }
            }
            String fileUrl = "./src/patch.txt";
            deleteAllFile.deleteFile(new File(fileUrl));

        }catch(Exception e){
           System.out.println("failed to generate patch");
//           e.printStackTrace();
            String dir = System.getProperty("user.dir");
            String url = "./src/diff";
            deleteAllFile.delFileMake(url);
            File file = new File("./");
            if(file.isDirectory()){
                File[] files = file.listFiles();
                for(File f:files){
                    String path = f.getAbsolutePath();
                    if(path.endsWith(".txt")){
                        deleteAllFile.deleteFile(new File(path));
                    }
                }
            }
            String fileUrl = "./src/patch.txt";
            deleteAllFile.deleteFile(new File(fileUrl));
        }


//        if((end -start) > 2400*1000) //设定运行时间为半小时
//        {
//            System.out.println("time out");
//        }
            //删除一个文件夹下的所有文件(包括子目录内的文件)
//            File file = new File("/Users/yanjiejiang/ICSE/diff");//输入要删除文件目录的绝对路径
//            deleteAllFiles(file);
//            if (flag == 1){
//                System.err.println("文件删除成功！");
//            }

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
    public static void deleteFile(File file){
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()){
            flag = 0;
            System.out.println("文件删除失败,请检查文件路径是否正确");
            return;
        }
        //取得这个目录下的所有子文件对象
        File[] files = file.listFiles();
        //遍历该目录下的文件对象
        for (File f: files){
            //打印文件名
            String name = file.getName();

            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()){
                deleteFile(f);
            }else {
                f.delete();
            }
        }
        //删除空文件夹  for循环已经把上一层节点的目录清空。

    }

    //step2
    public static void getDiffFile( String fixV, String buggyV) throws Exception{

//        ArrayList<String> version = Utils.readFile(dir+"/src/bug");
        int bugN =0;
//        String csvPath= dir + "/src/diff/diffFile.txt";
        File srcDiff = new File("./src/diff");
        File log = new File("./src/log.txt");
//        if(!log.exists()){
//            log.createNewFile();
//        }
        if(!srcDiff.exists()){
            srcDiff.mkdirs();
        }
        String csvPath= "./src/diff/diffFile.txt";
//        String fixV = version.get(0);
//        String buggyV=fixV.replace("_fix","_buggy");
        String gitOption="git config --global core.whitespace cr-at-eol";
        execCmd(gitOption,null);
        String cmd = "git diff -U99999 --word-diff=plain "+fixV+ " "+buggyV;
        String cmd_patch = "git diff "+fixV+ " "+buggyV;
        System.out.println(cmd);
        String result = execCmd(cmd, null);
        String result_patch = execCmd(cmd_patch,null);
//        String csvPath_patch = dir +"/src/patch.txt";
        String csvPath_patch = "./src/patch.txt";
        Utils.appendFile(result,csvPath);
        Utils.appendFile("\n",csvPath);
//        System.out.println(result_patch);
        Utils.appendFile(result_patch,csvPath_patch);
        Utils.appendFile("\n",csvPath_patch);
        bugN++;
    }

    public static String execCmd(String cmd, File dir) throws Exception {
        StringBuilder result = new StringBuilder();

        Process process = null;
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;

        try {
            process = Runtime.getRuntime().exec(cmd, null, dir);
//            process.waitFor();
//            Thread.sleep(100000);
            bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
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
            if (process != null) {
                process.destroy();
            }
        }
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
