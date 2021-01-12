
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class extractChange {
    static String patternAdd = "\\{\\+(.*?)\\+\\}";
    static String patternDelete="\\[\\-(.*?)\\-\\]";
    static String patternModify="\\[\\-(.*?)\\+\\}";

    public static void main(String[] args) throws IOException {
//        runExactChange();
    }

    public static void runExactChange(String dir) throws IOException {
        File folder = new File(dir +"/diff");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String oPath = file.getAbsolutePath();
                if(oPath.endsWith("t.txt"))
                {
                    ArrayList<String> gitDiffData = readFile(oPath);
                    tokenizer(gitDiffData,oPath);
                }
            }
        }
    }


    private static String[] getAddToken(ArrayList<String> addTokenResult) {
        String[] addTokenArray = addTokenResult.toArray(new String[0]);
        return addTokenArray;
    }

    private static String[] getDeleteToken(ArrayList<String> deleteTokenResult) {
        String[] deleteTokenArray = deleteTokenResult.toArray(new String[0]);
        return deleteTokenArray;
    }

    private static String[] getModifyToken(ArrayList<String> modifyTokenResult) {
        String[] modifyTokenArray = modifyTokenResult.toArray(new String[0]);
        return modifyTokenArray;
    }

    private static ArrayList<String> getModifyIndex(long modifyTokenIndex, ArrayList<String> modifyTokenResult) {
        String modifyIntToBinary = FindSubSet.intToBinary(modifyTokenIndex, modifyTokenResult.size());
        ArrayList<String> modifyTokenTag = new ArrayList<String>();
        for(int i=0;i<modifyIntToBinary.length();i++)
        {
            String temp = String.valueOf(modifyIntToBinary.charAt(i));
            modifyTokenTag.add(temp);
        }
        return modifyTokenTag;
    }

    private static ArrayList<String> getDeleteIndex(long deleteTokenIndex, ArrayList<String> deleteTokenResult) {
        String deleteIntToBinary =FindSubSet.intToBinary(deleteTokenIndex,deleteTokenResult.size());
        ArrayList<String> deleteTokenTag = new ArrayList<String>(); //装delete标记值
        for(int i=0;i< deleteIntToBinary.length();i++)
        {
            String temp =String.valueOf(deleteIntToBinary.charAt(i));
            deleteTokenTag.add(temp);
        }
        return deleteTokenTag;
    }

    private static ArrayList<String> getAddIndex(long addTokenIndex, ArrayList<String> addTokenResult) {
        String addIntToBinary =FindSubSet.intToBinary(addTokenIndex,addTokenResult.size());
        ArrayList<String> addTokenTag = new ArrayList<String>(); //装add标记值
        for(int i=0;i< addIntToBinary.length();i++)
        {
            String temp =String.valueOf(addIntToBinary.charAt(i));
            addTokenTag.add(temp);
        }
        return addTokenTag;
    }


    public static void tokenizer(ArrayList<String> gitDiffData, String aPath) throws IOException {

        ArrayList<String> extractedResult = new ArrayList<>();
        ArrayList<String> res = extractChange(gitDiffData,extractedResult);
//      System.out.println("total number :"+res.size());
        ArrayList<String> addToken = new ArrayList<>();
        ArrayList<String> deleteToken = new ArrayList<>();
        ArrayList<String> modifyToken = new ArrayList<>();
        getChangeCode(res,modifyToken,deleteToken,addToken);
        ArrayList<String> addTokenResult= new ArrayList<>(0);
        ArrayList<String> deleteTokenResult = new ArrayList<>(0);
        ArrayList<String> modifyTokenResult = new ArrayList<String>();
        int addTokenNum = 0;
        int deleteTokenNum=0;
        int modifyTokenNum=0;

        for(int i=0;i< addToken.size();i++)
        {
            String temp1 = addToken.get(i);
            addTokenResult.add(temp1);
            addTokenNum++;
        }

        for(int r=0;r< deleteToken.size();r++)
        {
            String temp1 = deleteToken.get(r);
            deleteTokenResult.add(temp1);
            deleteTokenNum++;
        }

        for(int t=0; t<modifyToken.size();t++){
            String temp1 = modifyToken.get(t);
            modifyTokenResult.add(temp1);
            modifyTokenNum++;
        }

        //输出信息
//        System.out.println("add token number: " + addTokenResult.size());
//        for(int i=0;i< addTokenResult.size();i++)
//        {
//            System.out.println(addTokenResult.get(i));
//        }
//        System.out.println("delete token number: "+ deleteTokenNum);
//        for(int i=0;i< deleteTokenResult.size();i++)
//        {
//            System.out.println(deleteTokenResult.get(i));
//        }
//
//        System.out.println("modify token number: "+ modifyTokenNum);
//        for(int i=0;i< modifyTokenResult.size();i++)
//        {
//            System.out.println(modifyTokenResult.get(i));
//        }

        //----------follow here---------

        String[] addTokenArray = getAddToken(addTokenResult);
        String[] deleteTokenArray = getDeleteToken(deleteTokenResult);
        String[] modifyTokenArray = getModifyToken(modifyTokenResult);

        long addTokenIndex =FindSubSet.Biannary2Decimal(addTokenArray.length);
        long deleteTokenIndex = FindSubSet.Biannary2Decimal(deleteTokenArray.length);
        long modifyTokenIndex = FindSubSet.Biannary2Decimal(modifyTokenArray.length);

//        System.out.println("+++"+addTokenIndex+",,,"+addTokenArray.length);
//        System.out.println("---"+deleteTokenIndex+",,,"+deleteTokenArray.length);
//        System.out.println("***"+modifyTokenIndex+",,,"+modifyTokenArray.length);

        getAddIndex(addTokenIndex,addTokenResult);
        getDeleteIndex(deleteTokenIndex,deleteTokenResult);
        getModifyIndex(modifyTokenIndex,modifyTokenResult);

        //generate patch (生成diff的patch)
        ArrayList<String> removeHead = new ArrayList<String>();
        ArrayList<String> rhead =removeHeader(gitDiffData,removeHead);

        long addTokenInd=FindSubSet.Biannary2Decimal(addTokenResult.size());
        long deleteTokenInd=FindSubSet.Biannary2Decimal(deleteTokenResult.size());
        long modifyTokenInd = FindSubSet.Biannary2Decimal(modifyTokenResult.size());
//        System.out.println("add token ind"+addTokenInd);
//        System.out.println("delete token ind"+deleteTokenInd);
//        System.out.println("modify token ind"+modifyTokenInd);
        generateFile(gitDiffData, deleteTokenInd, addTokenInd, modifyTokenInd, modifyTokenResult,addTokenResult,deleteTokenResult,aPath);

    }

    public static void generateFile(ArrayList<String> filename, long deleteN, long addN, long modifyN,ArrayList<String> modifyTokenResult,ArrayList<String> addTokenResult,ArrayList<String> deleteTokenResult,String aPath){
        int count=0;

        for(long i=0;i<= deleteN;i++)
        {
            for(long j=0;j<= addN;j++)
            {
                for(long t=0; t<= modifyN;t++)
                {
                    String path=aPath +"a/";//+ count + ".java";
                    ArrayList<String> modifyT = getModifyIndex(t,modifyTokenResult);
                    ArrayList<String> addT= getAddIndex(j,addTokenResult);
                    ArrayList<String> deleteT= getDeleteIndex(i,deleteTokenResult);

                    for(int k=0;k< filename.size();k++)
                    {
                        String eachCode = filename.get(k);
                        String finalEach = genPatch.getMinPatch(eachCode, deleteT, addT, modifyT);
                        genPatch.appendFile(finalEach, path,count+".txt");
                        genPatch.appendFile("\n", path, count+".txt");
                    }
                    count++;
                }

            }

        }
//        System.out.println(count);
    }

    private static ArrayList<String> removeHeader(ArrayList<String> fjava,ArrayList<String> removeHead) {
        for(int i=0;i<fjava.size();i++)
        {
            String fLine = fjava.get(i);
//          fLine.replaceAll("[\\-]*","");
            if(fLine.startsWith("diff") || fLine.startsWith("index") || fLine.startsWith("+++") || fLine.startsWith("---") ||fLine.startsWith("@@"))
            {
//                System.out.println("****"+fLine);
            }else{

                removeHead.add(fLine);
            }
        }
        return removeHead;
    }

    //提取出所有修改的部分
    public static ArrayList<String> extractChange(ArrayList<String> gitDiffData, ArrayList<String> extractedResult){

        for(int i=0;i< gitDiffData.size();i++)
        {
            String lineChange = gitDiffData.get(i);//.replace("[-+][0-9][0-9]):([0-9][0-9])$\", \"$1$2\"); -]","");

            Pattern rModify = Pattern.compile(patternModify); // Create a Pattern object
            Matcher mModify = rModify.matcher(lineChange); // Now create matcher object.
            StringBuffer sb1 = new StringBuffer();
            StringBuffer sb2 = new StringBuffer();
            StringBuffer sb3 = new StringBuffer();
            String afterM="";
            String afterD="";
            while (mModify.find()) {
                extractedResult.add(mModify.group());
//                afterM=mModify.group();
                mModify.appendReplacement(sb1,"").toString();

            }
            mModify.appendTail(sb1);
            afterD = sb1.toString();
            Pattern rDelete = Pattern.compile(patternDelete);
            Matcher mDelete = rDelete.matcher(afterD);

            String afterA="";
            while(mDelete.find()){
                extractedResult.add(mDelete.group());
                mDelete.appendReplacement(sb2,"").toString();

                afterA = mDelete.group();
            }
            mDelete.appendTail(sb2);
//            String afterS = afterD.replace(afterA,"");
             String afterS = sb2.toString();
            Pattern rAdd = Pattern.compile(patternAdd);
            Matcher mAdd = rAdd.matcher(afterS);
            while(mAdd.find()){
                extractedResult.add(mAdd.group());
            }
        }
        return extractedResult;

    }
    //读入git-diff文件
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

    public static void getChangeCode(ArrayList<String> change, ArrayList<String> modifyToken, ArrayList<String> deleteToken, ArrayList<String> addToken) throws IOException {
        for(int i=0;i< change.size();i++)
        {
            String lineInfo = change.get(i);//.replace("[-+][0-9][0-9]):([0-9][0-9])$\", \"$1$2\"); -]","");
            //修改token
            Pattern modModify = Pattern.compile(patternModify);
            Matcher tModify = modModify.matcher(lineInfo);

            while(tModify.find())
            {
                String temp = tModify.group();
//                modifyToken.add(tModify.group().replace("[-","").replace("-]","").replace("{+","").replace("+}",""));
                modifyToken.add(tModify.group());
                lineInfo= lineInfo.replace(temp,"");
            }

            //删token
            Pattern delModify = Pattern.compile(patternDelete);
            Matcher mModify = delModify.matcher(lineInfo);
            while (mModify.find()) {
                if(mModify.group().contains("-]"))
                {
                    deleteToken.add(mModify.group().replace("[-","").replace("-]",""));
                }else
                {
                    deleteToken.add(mModify.group().replace("[-",""));
                }
            }
           // 加token
            Pattern aModify = Pattern.compile(patternAdd);
            Matcher nModify = aModify.matcher(lineInfo);
            while (nModify.find()) {
                if(nModify.group().contains("+}"))
                {
                    addToken.add(nModify.group().replace("{+","").replace("+}",""));
                }else
                {
                    addToken.add(nModify.group().replace("{+",""));
                }
//                addToken.add(nModify.group().replace("{+","").replace("+}",""));
            }
        }
    }

    public static String getDeleteToken(String deleteToken) throws IOException {
         return split(deleteToken);
    }

    public static String getAddToken(String addToken) throws IOException {
            return split(addToken);
    }
    public static String split(String code){
        String splitResult="";
        StringTokenizer st=new StringTokenizer(code.trim(),";,(,),=,.,!, ", true);
        while(st.hasMoreTokens()) {
                 splitResult += st.nextToken()+ ",";
        }
        return splitResult;
    }
}
