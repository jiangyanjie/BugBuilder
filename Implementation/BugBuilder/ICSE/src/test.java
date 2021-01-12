import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

    public static void main(String[] args) throws IOException {

//     clearComment("/Users/yanjiejiang/ICSE/diff/cmdDiff_lang19.txt");
//        modifyChange();
        ArrayList<String> t = new ArrayList<String>();
        t.add("1");
        t.add("2");
        t.remove(0);
        System.out.println(t);

    }

    public static void clearComment(File file, String charset) throws IOException {

        //根据对应的编码格式读取
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
        StringBuffer content = new StringBuffer();
        String tmp = null;
        while ((tmp = reader.readLine()) != null) {
            content.append(tmp);
            content.append("\n");
        }
        String target = content.toString();

        String s = target.replaceAll("\\/\\/[^\\n]*|\\/\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*+\\/", "");

        s= s.replaceAll("\\{\\+\\s*\\+\\}","").replaceAll("\\{\\+\\s*\n","");
//             s=q;
        System.out.println("++++");
        //使用对应的编码格式输出  \\s*|\t|\r|\n
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
        out.write(s);
        out.flush();
        out.close();
    }


    public static void clearComment(String filePath) throws IOException {
        clearComment(new File(filePath), "UTF-8");
    }

    public static void modifyChange(){
        ArrayList<String> file = Utils.readFile("/Users/yanjiejiang/ICSE/diff/cmdDiff_lang19.txt");
        for(int i=0;i< file.size();i++)
        {
            String line = file.get(i);
            if(line.contains("{+") && line.contains("+}"))
            {
                int left = line.indexOf("{+");
                int right = line.indexOf("+}");
                String temp =line.substring(left+2,right);
                boolean t = temp.equals("    ");

                System.out.println(t);
            }



        }
    }
}
