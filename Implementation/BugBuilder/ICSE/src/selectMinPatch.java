import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class selectMinPatch {

    static ArrayList<String> jtemp = new ArrayList<>();

    public static String execCmd(String cmd, File dir) throws Exception {
        StringBuilder result = new StringBuilder();

        Process process = null;
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;

        try {
            process = Runtime.getRuntime().exec(cmd, null, dir);
//            process.waitFor();
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
    public static ArrayList<String> readFile(String fileName){
        ArrayList<String> result = new ArrayList<String>();
        File file = new File(fileName);
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            while((tempString = reader.readLine()) != null)
            {
                if(!tempString.equals("")){
                    result.add(tempString);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;
    }

    public static ArrayList<String> multiFile(String ps){
        ArrayList<String> prepareFile = new ArrayList<>();
        String temp = ps.substring(1,ps.length()-1);

        System.out.println("-----temp------" + temp);


        String[] temps=temp.split(",");
        for(int i=0;i< temps.length;i++){
            String line = temps[i].trim();
            System.out.println("**********" + line);

            String findex="";
            String pattern2 ="[0-9]f/";
            Pattern rAdd1 = Pattern.compile(pattern2);
            Matcher mAdd1 = rAdd1.matcher(line);
            while(mAdd1.find()){
//            System.out.println(mAdd.group());
                findex=mAdd1.group();}

                String t1 =findex.replace("f","b");
                String t3 = line.replace(findex,t1);




            String replace = line.replace(findex,t1);//"_fix","_buggy"

            System.out.println("replace :: " + replace);

            prepareFile.add(replace);
        }
        return prepareFile;
    }

    public static String getN(String n, int b){
        int p0 = n.lastIndexOf("/");
        int p1=0;
        String m="";
        if(b==1){
            p1= n.lastIndexOf("1.java");
            m = n.substring(p0+1,p1);
        }else{
            p1= n.lastIndexOf(".java");
            m = n.substring(p0+1,p1);
        }
        return m;
    }
    public static ArrayList<String> getChangeSet(String line) throws Exception{
        String afterBK = line.split("@#@")[1];
        ArrayList<String> afterReplace = multiFile(afterBK);
        String fn = line.split("@#@")[2];

//        System.out.println("--------fn------- " + fn);

        String dir = System.getProperty("user.dir");
        String p1 = "./src/diff/diffFilet.txta";
        String nPath = "./src/"+fn+".txt";

        File folder = new File(p1);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                String dPath = file.getAbsolutePath();
                int pos1 = dPath.lastIndexOf("/");
                String fo = dPath.substring(pos1+1);
                if(fn.equals(fo)){
                    File innerFolder = new File(dPath);
                    File[] files = innerFolder.listFiles();
                    for(File f : files){
                        String tempPath = f.getAbsolutePath();

                        if(tempPath.endsWith("1.java")){
                            String t1 = getN(tempPath,1);

                            System.out.println("t1:: " + t1);

                            for(int v=0; v<afterReplace.size();v++){
                                String t2 = afterReplace.get(v);
                                String t22 = getN(t2,0);
                                System.out.println("t22: " + t22);


                                if(t1.equals(t22)){
//                                    String part2 = f.getAbsolutePath();
                                    System.out.println("how to find bug");
                                    System.out.println(tempPath);
                                    System.out.println(t2);

                                    String cmd = "git diff -U99999 --word-diff=plain "+tempPath+ " "+t2;//
                                    String cmd1 = "git diff "+tempPath+" "+t2;

                                    System.out.println("command::" + cmd);
                                    String result = execCmd(cmd, null);




                                    String s = result.replaceAll("\\/\\/[^\\n]*|\\/\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*+\\/", "");
                                    s= s.replaceAll("\\{\\+\\s*\\+\\}","").replaceAll("\\{\\+\\s*\n","");
                                    s= s.replaceAll("\\[\\-\\s*\\-\\]","").replaceAll("\\[\\-\\s*\n","");
//                                    String s = result.replaceAll("\\/\\/[^\\n]*|\\/\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*+\\/|\\/\\*\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*+\\/", "");
////          String s = target.replace("\\/\\*[\\w\\W]*?\\*\\/|\\/\\/.*","");
//                                    s= s.replaceAll("\\{\\+\\s*\\+\\}","").replaceAll("\\{\\+\\s*\n","").replaceAll("\\+\\}","");
//                                    s= s.replaceAll("\\[\\-\\s*\\-\\]","").replaceAll("\\[\\-\\s*\n","").replaceAll("\\-\\]","");

                                    System.out.println("---diff file" + s);


                                    if(s.contains("-]{+"))
                                    {
                                        if(s.contains("+}"))
                                        {
                                            appendFile(s,nPath);
                                            appendFile("\n",nPath);
                                        }else{
                                            appendFile(s + "+}",nPath);
                                            appendFile("\n",nPath);
                                        }
                                    }else if(s.contains("[-")) {
                                        if(s.contains("-]"))
                                        {
                                            appendFile(s,nPath);
                                            appendFile("\n",nPath);
                                        }else{
                                            appendFile(s + "-]",nPath);
                                            appendFile("\n",nPath);
                                        }
                                    } else if(s.contains("{+")) {
                                        if(s.contains("+}"))
                                        {
                                            appendFile(s,nPath);
                                            appendFile("\n",nPath);
                                        }else{
                                            appendFile(s + "+}",nPath);
                                            appendFile("\n",nPath);
                                        }
                                    }
                                    else{
                                        appendFile(s,nPath);
                                        appendFile("\n",nPath);
                                    }

                                }

                            }
                        }
                    }
                }
            }
        }

        // getchange set
        ArrayList<String> temp = readFile("./src/"+fn+".txt");
        ArrayList<String> changeSet = new ArrayList<>();
        for(String t : temp){
            if(t.contains("[-") || t.contains("-]") || t.contains("{+") || t.contains("+}")){
                changeSet.add(t);
            }
        }
//        System.out.println(fn+","+changeSet);
        jtemp.add(fn);

        return changeSet;
    }

    public static void getOne(ArrayList<String> candidates, String path2generated,String args1, String args2) throws Exception{
        ArrayList<ArrayList<String>> res = new ArrayList<>();
        for(String line: candidates){
            ArrayList<String> temp = getChangeSet(line);
            res.add(temp);
        }

//        getSuperSet(res,args1,args2);

        getSubset(res,path2generated,args1,args2);
    }

    public static void getSubset(ArrayList<ArrayList<String>> candidates, String path2generated, String args1, String args2) throws Exception {
//        coverInfo.runGzoltar();

//        if(coverInfo.coverInfo(coverInfo.spectra_buggy,coverInfo.spectra_fix) == true){
            ArrayList<Integer> lenOfArr = new ArrayList<>();
            for(int i=0;i< candidates.size();i++){
                ArrayList<String> temp = candidates.get(i);
                lenOfArr.add(temp.size());
            }
            if(lenOfArr.size()==0){
                System.out.println("Failing (to find by the minimal patch)");
                return;
            }
            int minLen = Collections.min(lenOfArr);

            for(int k=0; k < candidates.size();k++){
                ArrayList<String> t = candidates.get(k);
                String jt = jtemp.get(k);
                int n1=0;

                if(t.size() == minLen){
                    int n = 0;
                    for(int j=0;j<candidates.size();j++){
                        ArrayList<String> p = candidates.get(j);
                        if(p.containsAll(t)){
                            n++;
                        }
                    }

                    if(n==candidates.size()){
//                        System.out.println("succeed: minimal patch");
//                        System.out.println(jt);
//                        System.out.println(t);

                        String output="./src/finalPatch.txt";
                        clearPatch.generatePatch4subset(path2generated,t, args1,args2);

                    }else{
                        System.out.println("Failing (find the minimal patch)");
//                        System.out.println(jt);
//                        System.out.println(t);
                    }
                }else{
                    System.out.println("generate the minimal patch unsuccessfully");
                }
            }
//        }else{
//            System.out.println("------Fail Recommendation-------");
//        }
    }

    public static void getSuperSet(ArrayList<ArrayList<String>> candidates,String args1, String args2){
        ArrayList<Integer> lenOfArr = new ArrayList<>();
        for(int i=0;i<candidates.size();i++){
            ArrayList<String> temp = candidates.get(i);
            lenOfArr.add(temp.size());
        }
        if(lenOfArr.size()==0){
            System.out.println("Failing (to find by the superSet)");
            return;
        }
        int maxLen = Collections.max(lenOfArr);
        HashMap<String,Integer> resultMap = new HashMap<>();
        for(int k=0;k<candidates.size();k++){
            ArrayList<String> t = candidates.get(k);

            String jt = jtemp.get(k);
            if(t.size() == maxLen){
                int n =0 ;
                for(int j=0;j<candidates.size();j++){
                    ArrayList<String> p = candidates.get(j);
                    if(t.containsAll(p)){
                        n++;
//                        System.out.println(n);
                    }
                }

                if(n==candidates.size()){
//                    System.out.println("this is a super patch");
                    for(int v=0;v<t.size();v++){
                        String ttemp = t.get(v);
                        if(!resultMap.containsKey(ttemp)){
                            resultMap.put(ttemp,1);
                        }else{
                            int num = resultMap.get(ttemp)+1;
                            resultMap.put(ttemp,num);
                        }
                    }
                    int m=0;
                    for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {

                        if(entry.getValue() >=2 ){
                            m++;
                        }
                        if(m==resultMap.size()){
//                            System.out.println("succeed super class");
                            String output="./src/finalPatch.txt";
                            clearPatch.generatePatch(output,args1,args2);
//                            System.out.println(jt);
//                            System.out.println(t);
                            return;
                        }
                    }
//                    System.out.println("continues with the next step");
                }
            }
        }
    }


    public static void main(String[] args) throws Exception{

        String line = "2,/Users/yanjiejiang/icse2tsef/icse/jack_1968-9f/";

        String findex="";
        String pattern2 ="[0-9]f/";
        Pattern rAdd1 = Pattern.compile(pattern2);
        Matcher mAdd1 = rAdd1.matcher(line);
        while(mAdd1.find()){
//            System.out.println(mAdd.group());
            findex=mAdd1.group();

            String t1 =findex.replace("f","b");
            String t3 = line.replace(findex,t1);
            System.out.println(t3);
        }

    }
}
