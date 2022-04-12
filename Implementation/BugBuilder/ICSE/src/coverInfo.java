import java.awt.image.AreaAveragingScaleFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class coverInfo {
    /**
     * todo list
     * 运行gzoltar得到对应的覆盖信息 done at 2021-10-26 21:08
     * 使用diff命令，得到buggy fix版本修改对应的行号 done at 2021-10-27
     * 按格式输出 done at 2021-10-27
     * **/

    public static ArrayList<String> spectra_buggy = new ArrayList<>();
    public static ArrayList<String> spectra_fix = new ArrayList<>();


    public static ArrayList<String> readFile(String fileName){
        ArrayList<String> result = new ArrayList<String>();
        File file = new File(fileName);
        if(!file.exists()){
            return null;
        }
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
    public static boolean coverInfo(ArrayList<String> spectra1, ArrayList<String> spectra2) throws Exception {

        ArrayList<String> coverLineInfo = getLineNo();
        for(String info : coverLineInfo){
            String[] infos = info.split(":");
            String type = infos[0];
            String context = infos[1];
            if(type.equals("update")){
                String bugLine = context.split(",")[0];
                String fixLine = context.split(",")[1];
                if((!spectra1.contains(bugLine)) || (!spectra2.contains(fixLine))){
                    return false;
                }
            }else if(type.equals("delete")){
                if(!spectra1.contains(context)){
                    return false;
                }
            }else if(type.equals("add")){
                if(spectra2.contains(context)){
                    return false;
                }
            }else{
                return true;
            }
        }
        return true;
    }

    public static void runGzoltar() throws Exception {
        spectra_buggy.clear();
        spectra_fix.clear();
        // ./run_gzoltar.sh Closure 131 /users/yanjiejiang/checkout/Closure_131_buggy /users/yanjiejiang/gzoltar/Closure131-1/ /users/yanjiejiang/gzoltar/com.gzoltar-1.6.0-jar-with-dependencies.jar
        String dir = System.getProperty("user.dir");
        ArrayList<String> version = Utils.readFile(dir+"/src/bug");
        String proId = version.get(0);
        String[] proIDs = proId.split("/");

        String projectName = "";
        String bugID="";
        String proandid="";

        for(String temp:proIDs){
            if(temp.contains("_")){
                //get the project name and bug id
                String[] proIds = temp.split("_");
                projectName = proIds[0];
                bugID = proIds[1];
                proandid=temp;
            }
        }
        // generate the coverage info for fix version
        String cmdGzoltar4fix = dir+"/gzoltar/run_gzoltar.sh" + " "+ projectName+ " "+bugID+" " + dir+"/"+proandid+"  " +dir+"/gzoltar/"+proandid+"/ " + dir+"/gzoltar/com.gzoltar-1.6.0-jar-with-dependencies.jar";
//        System.out.println(cmdGzoltar4fix);
        getDiffCommit.execCmd(cmdGzoltar4fix,null);
        String cmdGzoltar4buggy = cmdGzoltar4fix.replace("_fix","_buggy");
        getDiffCommit.execCmd(cmdGzoltar4buggy,null);

        // 生成的覆盖信息---输出到arrayList
        String fixPath2coverage = dir+"/gzoltar/"+proandid+"/spectra";
        String buggyPath2coverage = fixPath2coverage.replace("_fix","_buggy");

//        System.out.println(buggyPath2coverage);
        spectra_buggy = readFile(buggyPath2coverage);
        spectra_fix = readFile(fixPath2coverage);
//        System.out.println(spectra_fix);
    }

    public static ArrayList<String> getLineNo() throws Exception {
        ArrayList<String> res = new ArrayList<>();
        String dir = System.getProperty("user.dir");
        ArrayList<String> version = Utils.readFile(dir+"/src/bug");
        String fixVersion = version.get(0);
        String buggyVersion = version.get(0).replace("_fix","_buggy");
        String cmd4Diff = "diff -r "+ buggyVersion +" "+fixVersion;
        String t =getDiffCommit.execCmd(cmd4Diff,null);

        StringBuilder  style = new StringBuilder();
        String[] ts =t.split("\n");
        for(int i=0;i<ts.length;i++){
            String temp = ts[i];
            if(isdiffBeginOrNot(temp)){
                String[] diffs = temp.replace(".java","").split(" ");
                String[] tokens = diffs[2].split("/");
                style.setLength(0);
                for(int index=9;index< tokens.length-1;index++){
                    style.append(tokens[index]);
                    style.append(".");
                }
                style.append(tokens[tokens.length-1]);
                style.append("#");
            }

            if(isLineOrNot(temp))
            {
                String temp1 = ts[i+1];
                if(!isCommentOrNot(temp1)){
                    String[] lines = temp.split("c");
                    String buggy = lines[0];
                    String fix = lines[1];
                    ArrayList<String> buggyscope = getLineScope(buggy);
                    ArrayList<String> fixscope = getLineScope(fix);

                    // 求交集 -- 对应update
                    ArrayList<String> buggy1 = (ArrayList<String>) buggyscope.clone();
                    ArrayList<String> fix1  = (ArrayList<String>) fixscope.clone();
                    buggy1.retainAll(fix1);
                    for(int t1=0;t1<buggy1.size();t1++){
                        res.add("update:"+style + buggy1.get(t1)+ ","+ style +buggy1.get(t1));
//                        System.out.println("update:"+style + buggy1.get(t1)+ ","+ style +buggy1.get(t1));
                    }

                    // 求buggy 和fix 的差集 -- 对应 buggy的delete

                    ArrayList<String> buggy2 = (ArrayList<String>) buggyscope.clone();
                    ArrayList<String> fix2 = (ArrayList<String>) fixscope.clone();
                    buggy2.removeAll(fix2);
                    for(int t2=0;t2<buggy2.size();t2++){
                        res.add("delete:"+style.toString() + buggy2.get(t2));
//                        System.out.println("delete:"+style.toString() + buggy2.get(t2));
                    }

                    // 求fix 和buggy的差集 -- 对应 fix的add

                    ArrayList<String> buggy3 = (ArrayList<String>) buggyscope.clone();
                    ArrayList<String> fix3 = (ArrayList<String>) fixscope.clone();
                    fix3.removeAll(buggy3);
                    for(int t3=0;t3<fix3.size();t3++){
                        res.add("add:"+style.toString() + fix3.get(t3));
//                        System.out.println("add:"+style.toString() + fix3.get(t3));
                    }
                }
            }
        }
        return res;
    }

    public static boolean isdiffBeginOrNot(String str){
        if(str.startsWith("diff")){
               return true;
        }
        return false;
    }

    public static boolean isLineOrNot(String str){
        Pattern pattern = Pattern.compile("[0-9].*c.*[0-9]");
        Matcher isLine = pattern.matcher(str);
            if(isLine.matches()){
                return true;
            }
            return false;
    }

    public static boolean isCommentOrNot(String str){
        if(str.startsWith("<") || str.startsWith(">")){
            String ta = str.substring(1).trim();
            if(ta.startsWith("*")||ta.startsWith("//"))
            {
                return true;
            }
        }
        return false;
    }

    public static void handleLineNo(String temp){
        String[] lines = temp.split("c");
        String buggy = lines[0];
        String fix = lines[1];
        ArrayList<String> buggyscope = getLineScope(buggy);
        ArrayList<String> fixscope = getLineScope(fix);

        // 求交集 -- 对应update
        ArrayList<String> buggy1 = (ArrayList<String>) buggyscope.clone();
        ArrayList<String> fix1  = (ArrayList<String>) fixscope.clone();
        buggy1.retainAll(fix1);
        System.out.println(buggy1);

        // 求buggy 和fix 的差集 -- 对应 buggy的delete

        ArrayList<String> buggy2 = (ArrayList<String>) buggyscope.clone();
        ArrayList<String> fix2 = (ArrayList<String>) fixscope.clone();
        buggy2.removeAll(fix2);
        System.out.println(buggy2);

        // 求fix 和buggy的差集 -- 对应 fix的add

        ArrayList<String> buggy3 = (ArrayList<String>) buggyscope.clone();
        ArrayList<String> fix3 = (ArrayList<String>) fixscope.clone();
        fix3.removeAll(buggy3);
        System.out.println(fix3);






//        System.out.println(buggy);
//        System.out.println(fix);

    }

    public static ArrayList<String> getLineScope(String str){
        ArrayList<String> scope = new ArrayList<>();
        String[] line = str.split(",");
        if(line.length == 1){
            scope.add(line[0]);
        }else{
            int  begin = Integer.parseInt(line[0]);
            int stop = Integer.parseInt(line[1]);

            for(int i=begin;i<=stop;i++){
                scope.add(""+i);
            }
        }
//        System.out.println(scope);
        return scope;
    }



    public static void main(String[] args) throws Exception {
        System.out.println("----start----");
        runGzoltar();
//        getLineNo();
//        String test = "223";
//        getLineScope(test);






    }
}
