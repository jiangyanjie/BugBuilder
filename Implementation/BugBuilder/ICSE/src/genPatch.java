import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class genPatch {
//    static ArrayList<String> original = extractChange.readFile("/Users/x/ICSE/cmdDiff_lang"+getDiffCommit.bugNO+"t.txt");
//    static ArrayList<String> removeHead = new ArrayList<String>();



    static long addTokenInd;
    static long deleteTokenInd;
    static long modifyTokenInd;

    public static void main(String[] args) throws IOException {
//        extractChange.tokenizer();
//        ArrayList<String> rhead =removeHeader(original);
//        addTokenInd=FindSubSet.Biannary2Decimal(extractChange.addTokenResult.size());
//        deleteTokenInd=FindSubSet.Biannary2Decimal(extractChange.deleteTokenResult.size());
//        modifyTokenInd = FindSubSet.Biannary2Decimal(extractChange.modifyTokenResult.size());
//        System.out.println(deleteTokenInd);
//        System.out.println(modifyTokenInd);
//        System.out.println(extractChange.modifyTokenResult.size());
//        generateFile(rhead, deleteTokenInd, addTokenInd, modifyTokenInd);
//        appendFile("jiangyanjie","/Users/x/ICSE/diff/", "j.txt");

    }


    //step1: 读入java文件，去除头部diff信息


//    public static ArrayList<String> removeHeader(ArrayList<String> fjava){
//        for(int i=0;i<fjava.size();i++)
//        {
//          String fLine = fjava.get(i);
////          fLine.replaceAll("[\\-]*","");
//          if(fLine.startsWith("diff") || fLine.startsWith("index") || fLine.startsWith("+++") || fLine.startsWith("---") ||fLine.startsWith("@@"))
//          {
////                System.out.println("****"+fLine);
//          }else{
//
////              removeHead.add(fLine);
//          }
//        }
////        return removeHead;
//    }


    //step2： 遍历token集合，对每种情况生成对应修改patch

    public static String getMinPatch(String lineCode, ArrayList<String> deletetag, ArrayList<String> addtag, ArrayList<String>modifytag){

         //对修改的处理
          while(lineCode.indexOf("-]{+")!=-1)
          {
              int left1 = lineCode.indexOf("[-");
              int right1 = lineCode.indexOf("-]");
              String tt1 = lineCode.substring(left1,right1+2);

              int left2 = lineCode.indexOf("{+");
              int right2 = lineCode.indexOf("+}");
              String tt2 = lineCode.substring(left2,right2+2);

              String tt3 = lineCode.substring(left1,right2+2);
//              System.out.println("****************" + tt3);

              System.out.println("======" + modifytag.toString());

              if(modifytag.get(0).equals("1"))
              {
                  String temp1 = tt3.replace(tt1,"").replace("{+","").replace("+}","");
                  String t1 = lineCode.replace(tt3,temp1);//.replace("{+","").replace("+}","");
                  lineCode =t1;
              }else{
                  String temp2 = tt3.replace(tt2,"").replace("[-","").replace("-]","");
                  String t2 = lineCode.replace(tt3,temp2);//.replace("[-","").replace("-]","");
                  lineCode = t2;
              }

              modifytag.remove(0);
          }



            //对删除的处理
            while(lineCode.indexOf("[-")!= -1){
                int left = lineCode.indexOf("[-");
                int right = lineCode.indexOf("-]");
                String temp = lineCode.substring(left,right+2);
//                if(deletetag.size()!=0){
                    if(deletetag.get(0).equals("1"))
                    {
//                    System.out.println("jiiiii");
                        String t1= lineCode.replace(temp,"");
                        lineCode = t1;
                    }else{
                        String t2 = lineCode.substring(left+2,right);
                        String t3 = lineCode.replace(temp, t2);
                        lineCode = t3;
                    }
                    deletetag.remove(0);
//                }
            }

        // 对增加处理
        while(lineCode.indexOf("{+")!= -1){
                int left = lineCode.indexOf("{+");
//                System.out.println("==::::::" + left);
                int right = lineCode.lastIndexOf("+}");
//                System.out.println("==::::::" + right);
                String temp = lineCode.substring(left,right+2);
//                 System.out.println("temp " + temp);
//               if(addtag.size()!=0){
//                System.out.println("boolean ::" + addtag.toString());
                if(addtag.get(0).equals("0"))
                {
//                    System.out.println("****==========" + addtag.get(0));
                    String t1= lineCode.replace(temp,"");
                    lineCode = t1;
                }else{
//                    System.out.println("=======****" + addtag.get(0));
                    String t2 = lineCode.substring(left+2,right);
                    String t3 = lineCode.replace(temp, t2);
                    lineCode = t3;
                }
                addtag.remove(0);
//            }
        }
        return lineCode;
    }

    //step3: 生成Java文件
//    public static void generateFile(ArrayList<String> filename, long deleteN, long addN, long modifyN){
//        int count=0;
//
//        for(long i=0;i<= deleteN;i++)
//         {
//          for(long j=0;j<= addN;j++)
//            {
//                for(long t=0; t<= modifyN;t++)
//                {
//                    String path="/Users/x/ICSE/lang"+getDiffCommit.bugNO+"/patch" + count + ".java";
//                    ArrayList<String> modifyT = FindSubSet.getModifyIndex(t);
//                    ArrayList<String> addT= FindSubSet.getAddIndex(j);
//                    ArrayList<String> deleteT= FindSubSet.getDeleteIndex(i);
//
//                    //-----
////                System.out.println("&&&" + deleteT.size());
////                System.out.println("&&&" + addT.size());
//                    for(int k=0;k< filename.size();k++)
//                    {
//                        String eachCode = filename.get(k);
////                 System.out.println("&&&" + eachCode);
//                        String finalEach = getMinPatch(eachCode, deleteT, addT, modifyT);
//                        appendFile(finalEach, path);
//                        appendFile("\n", path);
//                    }
//
//                    count++;
//                }
//
//           }
//
//        }
//        System.out.println(count);
//    }

    public static void appendFile(String line, String path, String fileName){
        String newPath =creatFile(path,fileName);
        FileWriter fw = null;

        try{
            File f = new File(newPath);
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

    public static String creatFile(String filePath, String fileName) {
        File folder = new File(filePath);
        //文件夹路径不存在
        if (!folder.exists() && !folder.isDirectory()) {
//            System.out.println("文件夹路径不存在，创建路径:" + filePath);
            folder.mkdirs();
        } else {
//            System.out.println("文件夹路径存在:" + filePath);
        }

        // 如果文件不存在就创建
        File file = new File(filePath + fileName);
        if (!file.exists()) {
//            System.out.println("文件不存在，创建文件:" + filePath + fileName);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
//            System.out.println("文件已存在，文件为:" + filePath + fileName);
        }
        return filePath+fileName;
    }


}
