import java.io.*;
import java.util.ArrayList;

public class removeComment {

    public static void runRemoveComment() throws IOException {

//        File folder = new File(dir +"/src/diff");
        File folder = new File("./src/diff");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String oPath = file.getAbsolutePath();
                String jPath = file.getAbsolutePath();
                if(oPath.endsWith(".txt"))
                {

                    clearComment(oPath);
                    String tPath=oPath.replace(".txt","t.txt");
                    // fix a bug
//                  modifyLittle(jPath,tPath);
                    modifyLittle(oPath,tPath);
                }

            }
        }
    }

    //去除注释  //     \/\*[\w\W]*?\*\/|\/\/.*
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

            String s = target.replaceAll("\\/\\/[^\\n]*|\\/\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*+\\/|\\/\\*\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*+\\/", "");
//          String s = target.replace("\\/\\*[\\w\\W]*?\\*\\/|\\/\\/.*","");
            s= s.replaceAll("\\{\\+\\s*\\+\\}","").replaceAll("\\{\\+\\s*\n","");//.replaceAll("\\+\\}","");
            s= s.replaceAll("\\[\\-\\s*\\-\\]","").replaceAll("\\[\\-\\s*\n","");//.replaceAll("\\-\\]","");
//          s=q;
            //使用对应的编码格式输出  \\s*|\t|\r|\n
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
            out.write(s);
            out.flush();
            out.close();
        }

    public static void clearComment(String filePath) throws IOException {
        clearComment(new File(filePath), "UTF-8");
    }

    public static void modifyLittle(String np, String nPath)  {
        ArrayList<String> diffOrig = extractChange.readFile(np);
        for(int i=0;i< diffOrig.size();i++)
        {
            String line = diffOrig.get(i);
            if(line.contains("-]{+"))
            {
                if(line.contains("+}"))
                {
                    Utils.appendFile(line,nPath);
                    Utils.appendFile("\n",nPath);
                }else{
                    Utils.appendFile(line + "+}",nPath);
                    Utils.appendFile("\n",nPath);
                }
            }else if(line.contains("[-")) {
                if(line.contains("-]"))
                {
                    Utils.appendFile(line,nPath);
                    Utils.appendFile("\n",nPath);
                }else{
                    Utils.appendFile(line + "-]",nPath);
                    Utils.appendFile("\n",nPath);
                }
            } else if(line.contains("{+")) {
                if(line.contains("+}"))
                {
                    Utils.appendFile(line,nPath);
                    Utils.appendFile("\n",nPath);
                }else{
                    Utils.appendFile(line + "+}",nPath);
                    Utils.appendFile("\n",nPath);
                }
            }
            else{
                Utils.appendFile(line,nPath);
                Utils.appendFile("\n",nPath);
            }
        }

    }

    public static void appendFile(String line, String nPath){
        FileWriter fw = null;

        try{
            File f = new File(nPath);
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
