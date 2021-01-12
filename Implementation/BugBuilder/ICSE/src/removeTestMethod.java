import java.io.*;
import java.util.ArrayList;

public class removeTestMethod {

    public static String dir ="/Users/yanjiejiang/ICSE/diff/cmdDiff_lang3t.txt";

    public static ArrayList<String> getHeader(){
        ArrayList<String> diffF = Utils.readFile(dir);
        ArrayList<String> method = new ArrayList<String>();
        for(int i=0;i< diffF.size();i++)
        {
            String lineInfo = diffF.get(i);
            if(lineInfo.startsWith("diff --git")){
                if(lineInfo.contains("/src/test")){
                    method.add("");
                }else{
                    method.add(lineInfo);
                }
            }
        }
       return method;
    }

    public static void splitFile(File file, String charset, ArrayList<String> method) throws IOException {

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
//        return splitFile;
        //----------
        int num =splitFile.length;
        for(int i=1;i< num;i++){

            int left = splitFile[i].indexOf("_fix/src/");
            String isTest = splitFile[i].substring(left+9);
            if(isTest.startsWith("test")){
                splitFile[i]="";
            }
        }
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
        for(int j = 1; j< splitFile.length; j++){
            out.write(method.get(j-1));
            out.write(splitFile[j]);
        }

        out.flush();
        out.close();


    }
    public static void splitFile(String filePath, ArrayList<String> method) throws IOException {
        splitFile(new File(filePath), "UTF-8", method);
//        return result;
    }


    public static void main(String[] args) throws IOException {
//splitFile
        splitFile(dir,getHeader());
//        getHeader();
    }

}
