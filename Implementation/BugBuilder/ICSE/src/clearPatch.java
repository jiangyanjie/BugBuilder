import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class clearPatch {

    //处理header 和 comment
    public static ArrayList<String> cleanPatch(String args1, String args2){
        ArrayList<String> step1 = new ArrayList<>();
        String dir = System.getProperty("user.dir");
        String orig_patch = dir + "/src/patch.txt";
//        System.out.println(orig_patch);
        ArrayList<String> patchContext = Utils.readFile(orig_patch);

        for(int i=0;i<patchContext.size();i++){
            String line = patchContext.get(i);
            String line1 = removeHeader(line, args1, args2);

//            String preLine = i>0? patchContext.get(i-1):"";
            String line2 =i>0?  removeComment(line1,patchContext.get(i-1)): removeComment(line1);
            step1.add(line2);
        }
        return step1;
    }

    // 处理空行问题
    public static ArrayList<String> handleEmptyLine(ArrayList<String> step1){
        ArrayList<String> res = new ArrayList<>();
        for(int i=0;i<step1.size();i++){
            String each = step1.get(i);
            if(each.startsWith("+")){
                String temp = each.substring(1).trim();
                if(!temp.equals("")){
                    res.add(each);
                }
            }else if(each.startsWith("-")){
                String temp1 = each.substring(1).trim();
                if(temp1.equals("")){
                    String t1 = each.replace("-"," ");
                    res.add(t1);
                }else{
                    res.add(each);
                }
            }else{
                res.add(each);
            }
        }
        return res;
    }

    // 调整行的标号
    public static ArrayList<String> tuneLineNo(ArrayList<String> step2){
        ArrayList<String> res = new ArrayList<>();
        int startPos=-1, endPos = -1;
        for(int i=0;i< step2.size();i++){
            String eachLine = step2.get(i);
            if(startPos == -1 && eachLine.startsWith("@@")) {
                startPos = i;
            } else if(startPos!=-1 && (eachLine.startsWith("@@") || eachLine.startsWith("diff"))) {
                endPos = i;

                ArrayList<String> temp = new ArrayList<>();
                for (int j = startPos ;j< endPos ;j++) {
                    temp.add(step2.get(j));
                }

//                System.out.println("-----------------");
//                System.out.println(temp);


                ArrayList<String> newTemp = changeLineNumber(temp);

                if(newTemp!=null) {
                    for (int i1 = 0; i1 < newTemp.size(); i1++) {
                        res.add(newTemp.get(i1));
                    }
                }
                if(eachLine.startsWith("@@")) {
                    startPos = endPos;
                }else {
                    startPos = -1;
                }
            }

            if(startPos == -1) {
                res.add(eachLine);
            }

        }
        if(startPos != -1) {
            ArrayList<String> temp = new ArrayList<>();
            for (int j = startPos ;j< step2.size() ;j++) {
                temp.add(step2.get(j));
            }

            ArrayList<String> newTemp = changeLineNumber(temp);

            if(newTemp!=null) {
                for (int i1 = 0; i1 < newTemp.size(); i1++) {
                    res.add(newTemp.get(i1));
                }
            }
        }

        // 测试输出res里的context
//        for(int k=0;k< res.size();k++){
//            System.out.println(res.get(k));
//        }
        return res;
    }

    public static ArrayList<String> tuneLineNo4ChangeSet(ArrayList<String> step2){
        ArrayList<String> res = new ArrayList<>();
        int startPos=-1, endPos = -1;
        for(int i=0;i< step2.size();i++){
            String eachLine = step2.get(i);
            if(startPos == -1 && eachLine.startsWith("@@")) {
                startPos = i;
            } else if(startPos!=-1 && (eachLine.startsWith("@@") || eachLine.startsWith("diff"))) {
                endPos = i;

                ArrayList<String> temp = new ArrayList<>();
                for (int j = startPos ;j< endPos ;j++) {
                    temp.add(step2.get(j));
                }

                ArrayList<String> newTemp = changeLineNumber4changeset(temp);

                if(newTemp!=null) {
                    for (int i1 = 0; i1 < newTemp.size(); i1++) {
                        res.add(newTemp.get(i1));
                    }
                }
                if(eachLine.startsWith("@@")) {
                    startPos = endPos;
                }else {
                    startPos = -1;
                }
            }

            if(startPos == -1) {
                res.add(eachLine);
            }

        }
        if(startPos != -1) {
            ArrayList<String> temp = new ArrayList<>();
            for (int j = startPos ;j< step2.size() ;j++) {
                temp.add(step2.get(j));
            }

            ArrayList<String> newTemp = changeLineNumber4changeset(temp);

            if(newTemp!=null) {
                for (int i1 = 0; i1 < newTemp.size(); i1++) {
                    res.add(newTemp.get(i1));
                }
            }
        }

        // 测试输出res里的context
//        for(int k=0;k< res.size();k++){
//            System.out.println(res.get(k));
//        }
        return res;
    }

    public static ArrayList<String> changeLineNumber4changeset(ArrayList<String> codeBlock){
        ArrayList<String> res = new ArrayList<>();
        String[] headers = codeBlock.get(0).split(" ");
        int buggyIndex=Integer.parseInt(headers[1].split(",")[1]);
        int fixIndex=Integer.parseInt(headers[2].split(",")[1]);
        int addLine=0,deleteLine=0,unchangeLine=0,commentLine=0;
        for(int i=1;i<codeBlock.size();i++){
            String eachL = codeBlock.get(i);
            if(eachL.startsWith("+")){
                addLine++;
            }else if(eachL.startsWith("-")){
                deleteLine++;
            }else{
                unchangeLine++;
            }
        }

        if((unchangeLine == codeBlock.size()-1)){
            return null;
        }
        buggyIndex = deleteLine+unchangeLine;
        fixIndex = addLine+unchangeLine;
        String header = codeBlock.get(0);
        int firstpos = header.indexOf(",");
        StringBuilder sb1 = new StringBuilder(header);
        sb1.replace(firstpos+1,firstpos+2,""+buggyIndex);

        String header2 = sb1.toString();
        int firstpos2 = header2.indexOf(",");
        StringBuilder sb2 = new StringBuilder(header2);
        sb2.replace(firstpos2+1,firstpos2+2,""+fixIndex);
        String finalheader = sb2.toString();
        res.add(finalheader);
        for(int i=1;i<codeBlock.size();i++){
            String t = codeBlock.get(i);
            res.add(t);
        }
        return res;
    }

    //sub-修改行号

    public static ArrayList<String> changeLineNumber(ArrayList<String> codeBlock){
        ArrayList<String> res = new ArrayList<>();
        String[] headers = codeBlock.get(0).split(" ");
        int buggyIndex0=Integer.parseInt(headers[1].split(",")[1]);
        int fixIndex0=Integer.parseInt(headers[2].split(",")[1]);

//        System.out.println(buggyIndex0);
//        System.out.println(fixIndex0);

        int addLine=0,deleteLine=0,unchangeLine=0,commentLine=0;

        for(int i=1;i<codeBlock.size();i++){
            String eachL = codeBlock.get(i);
            if(eachL.startsWith("+")){
                addLine++;
            }else if(eachL.startsWith("-")){
                deleteLine++;
            }else{
                unchangeLine++;
                if(eachL.trim().startsWith("*") || eachL.trim().startsWith("//") || eachL.trim().startsWith("/**")|| eachL.trim().startsWith("**/")||eachL.startsWith("*/")){
                    commentLine++;
                }
            }
        }

        if((commentLine == buggyIndex0) && (commentLine == fixIndex0)){
            return null;
        }
       int buggyIndex = deleteLine+unchangeLine;
       int fixIndex = addLine+unchangeLine;

//        System.out.println(buggyIndex);
//        System.out.println(fixIndex);


        String header = codeBlock.get(0);
        int firstpos = header.indexOf(",");

        String atemp = header.replace(""+buggyIndex0,""+buggyIndex);
//        System.out.println(header);
//        System.out.println(atemp);

//        StringBuilder sb1 = new StringBuilder(header);
//        sb1.replace(firstpos+1,firstpos+2,""+buggyIndex);



//        String header2 = sb1.toString();
        String header2 = atemp;
        String btemp = atemp.replace(""+fixIndex0,""+fixIndex);

//        int firstpos2 = header2.indexOf(",");
//        StringBuilder sb2 = new StringBuilder(header2);
//        sb2.replace(firstpos2+1,firstpos2+2,""+fixIndex);
        String finalheader = btemp;//sb2.toString();
        res.add(finalheader);
        for(int i=1;i<codeBlock.size();i++){
            String t = codeBlock.get(i);
            res.add(t);
        }
        return res;
    }

    public static String removeHeader(String s, String args1, String args2){
//        String args1 = "/Users/yanjiejiang/icse/Codec-3b/src/java";
//        String args2 = "/Users/yanjiejiang/icse/Codec-3f/src/java";
        String bindex="";
        String pattern1 ="[0-9]b/";
        Pattern rAdd = Pattern.compile(pattern1);
        Matcher mAdd = rAdd.matcher(args1);
        while(mAdd.find()){
//            System.out.println(mAdd.group());
            bindex=mAdd.group();
        }

        String findex="";
        String pattern2 ="[0-9]f/";
        Pattern rAdd1 = Pattern.compile(pattern2);
        Matcher mAdd1 = rAdd1.matcher(args2);
        while(mAdd1.find()){
//            System.out.println(mAdd.group());
            findex=mAdd1.group();
        }

     int pos11 = args1.lastIndexOf(bindex);
     String new_args1 = args1.substring(0,pos11);
     int pos12 = new_args1.lastIndexOf("/");
     String new_args11 = args1.substring(1,pos11+bindex.length());

        int pos21 = args2.lastIndexOf(findex);
        String new_args2 = args2.substring(0,pos21);
        int pos22 = new_args2.lastIndexOf("/");
        String new_args22 = args2.substring(1,pos21+findex.length());

     String after=s.replace(new_args11,"").replace(new_args22,"");
//        if((pos1!=(-1))&&(pos2!=(-1))){
//            String temp1 = s.substring(pos1,pos2+1);
//            String fs1 = s.replace(temp1,"");
//            int pos3= fs1.indexOf("/Users");
//            int pos4 = fs1.indexOf("b/src");
//            if((pos3!=(-1)) && (pos4!=(-1))){
//                String temp2 = fs1.substring(pos3,pos4+1);
//                String fs2 = fs1.replace(temp2,"");
//                after=fs2;
//
//            }
//            else{
//                after=fs1;
//            }
//        } else{
//            int pos5 = s.indexOf("/Users");
//            int pos6 = s.indexOf("b/src");
//            if((pos5!=(-1)) && (pos6!=(-1))){
//                String temp3 = s.substring(pos5,pos6+1);
//                String fs3 =s.replace(temp3,"");
//                after =fs3;
//            }
//        }
        return after;
    }
    public static String removeComment(String s){
        String output=s;
        if(s.startsWith("-")){
            String temp1 = s.substring(1).trim();
            if(temp1.startsWith("*") || temp1.startsWith("//") || temp1.startsWith("/**")){
                String res1 = s.replace("-"," ");
                output = res1;
                return output;
            }
        }

        if(s.startsWith("+")){
            String temp2 = s.substring(1).trim();
            if(temp2.startsWith("*/") || temp2.startsWith("**/")){
                return s;
            }else if(temp2.startsWith("*") || temp2.startsWith("//") || temp2.startsWith("/**")){
                String res2 ="+";
                output = res2;
                return output;
            }
        }
        return output;
    }

    public static String removeComment(String s, String preLine){
        String output=s;
        if(s.startsWith("-")){
            String temp1 = s.substring(1).trim();
            if(temp1.startsWith("*") || temp1.startsWith("//") || temp1.startsWith("/*")){
                String res1 = s.replace("-"," ");
                output = res1;
                return output;
            }
        }

        if(s.startsWith("+")){
            String temp2 = s.substring(1).trim();
            String temp3 = preLine.substring(1).trim();
            if(temp2.endsWith("*/") && !temp2.startsWith("/*")){
                return s;
            }else if(temp2.startsWith("/*") && temp3.startsWith("/*")){
                return "+";
            }else if(temp2.startsWith("/*")){
                return s;
            }else if(temp2.startsWith("*") || temp2.startsWith("//")){
                String res2 ="+";
                output = res2;
                return output;
            }
        }
        return output;
    }

    // 删除多余的header section
    public static ArrayList<String> removeRepeatedHeader(ArrayList<String> step3){
        ArrayList<String> res = new ArrayList<>();
        int pos=0;
        for(int i=0;i<step3.size();i++) {
            String each = step3.get(pos);
//            System.out.println(pos);
            if (each.startsWith("diff")) {
                if ((pos + 4) < step3.size()) {
                    if (step3.get(pos + 4).startsWith("diff")) {
                        pos = pos + 4;
                    } else {
                        res.add(each);
                        pos++;
                    }
                }
            } else {
                res.add(each);
                pos++;
            }
        }
//        for (int k = 0; k < res.size(); k++) {
//            System.out.println(res.get(k));
//        }
        return res;

    }

    public static void generatePatch(String output,String args1, String args2){
        ArrayList<String> step1 =cleanPatch(args1,args2);
        ArrayList<String> step2 = handleEmptyLine(step1);
        ArrayList<String> step3=tuneLineNo(step2);
        ArrayList<String> step4 = removeRepeatedHeader(step3);
        for(int i=0;i< step4.size();i++){
            String line = step4.get(i);
            Utils.appendFile(line+"\n",output);
        }
    }

    public static void generatePatch4subset(String output, ArrayList<String>  jt,String args1, String args2){
        ArrayList<String> res = new ArrayList<>();
        //得到修改的列表
        ArrayList<String> changes = changeSet(jt);
        //遍历生成的patch，将不再列表的改成untouched语句
        ArrayList<String> step1 =cleanPatch(args1,args2);
        ArrayList<String> step2 = handleEmptyLine(step1);
        ArrayList<String> step3=tuneLineNo(step2);
        ArrayList<String> step4 = removeRepeatedHeader(step3);
        for(int i=0;i<step4.size();i++){
            String eachLine = step4.get(i);
            if(eachLine.startsWith("+")){
                String newLine = eachLine.substring(1);
                if(changes.contains("+"+newLine.trim())){
                    res.add(eachLine);
                }else{
                    res.add(newLine);
                }
            }else if(eachLine.startsWith("-")){
                String newLine = eachLine.substring(1);
                if(changes.contains("-"+newLine.trim())){
                    res.add(eachLine);
                }else{
                    res.add(newLine);
                }
            }else{
                res.add(eachLine);
            }
        }
         System.out.println("res:  " + res.size());

        for(int w=0;w<res.size();w++){
            System.out.println(res.get(w));
//            Utils.appendFile(res.get(w)+"\n","/Users/yanjiejiang/icse/coll.txt");
        }



        ArrayList<String> step5 = tuneLineNo4ChangeSet(res);
        System.out.println("step5: " + step5.size());
        ArrayList<String> step6 = removeRepeatedHeader(step5);

        System.out.println("-------step6 " + step6.size() );

        for(int i=0;i< step6.size();i++){
            String line = step6.get(i);
            System.out.println(line);
            Utils.appendFile(line+"\n",output);
        }


//
//        for(int i=0;i< step6.size();i++){
//            System.out.println(step6.get(i));
//        }
//        return res;
//     System.out.println("-----------------------");
    }

    public static ArrayList<String> changeSet(ArrayList<String> str){
        ArrayList<String> res = new ArrayList<>();

        for(int k=0;k<str.size();k++){
            String temp = str.get(k).trim();
            if(temp.contains("[-")&& temp.contains("{+")){
                // + code
                int pos1=temp.indexOf("[-");
                int pos2=temp.indexOf("-]");
                String td = temp.substring(pos1+2,pos2);
                String td1= temp.replace("[-"+td+"-]","");
                String da = td1.replace("{+","").replace("+}","");
//                System.out.println(da);
                res.add("+"+da);

                //- code
                int pos3=temp.indexOf("{+");
                int pos4=temp.indexOf("+}");
                String td3 = temp.substring(pos3+2,pos4);
                String td4= temp.replace("{+"+td3+"+}","");
                String da2 = td4.replace("[-","").replace("-]","");
//                System.out.println(da2);
                res.add("-"+da2);
            } else if(temp.contains("[-")){
                String td5= temp.replace("[-","").replace("-]","");
                res.add("-"+td5);
            }else if(temp.contains("{+")){
                String td6 = temp.replace("{+","").replace("+}","");
                res.add("+"+td6);
            }
        }
//        String change = chs.substring(1,chs.length()-1);
//        String[] changes =change.split(",");
//        for(int i=0;i<changes.length;i++){
//            String temp = changes[i].trim();
//            if(temp.contains("[-")&& temp.contains("{+")){
//                // + code
//                int pos1=temp.indexOf("[-");
//                int pos2=temp.indexOf("-]");
//                String td = temp.substring(pos1+2,pos2);
//                String td1= temp.replace("[-"+td+"-]","");
//                String da = td1.replace("{+","").replace("+}","");
////                System.out.println(da);
//                res.add("+"+da);
//
//                //- code
//                int pos3=temp.indexOf("{+");
//                int pos4=temp.indexOf("+}");
//                String td3 = temp.substring(pos3+2,pos4);
//                String td4= temp.replace("{+"+td3+"+}","");
//                String da2 = td4.replace("[-","").replace("-]","");
////                System.out.println(da2);
//                res.add("-"+da2);
//            } else if(temp.contains("[-")){
//                String td5= temp.replace("[-","").replace("-]","");
//                res.add("-"+td5);
//            }else if(temp.contains("{+")){
//                String td6 = temp.replace("{+","").replace("+}","");
//                res.add("+"+td6);
//            }
//        }
//        System.out.println(change);
//        for(int i=0;i<res.size();i++){
//            System.out.println(res.get(i));
//        }
        return res;
    }

    public static void main(String[] args){

////        ArrayList<String> step1 =cleanPatch();
//        ArrayList<String> step2 = handleEmptyLine(step1);
//        ArrayList<String> step3=tuneLineNo(step2);
//        ArrayList<String> step4 = removeRepeatedHeader(step3);
//        for(int i=0;i< step4.size();i++){
//            String line = step4.get(i);
////            System.out.println(line.substring(1).trim().equals(" "));
//            System.out.println(line);
//        }
//        ArrayList<String> a = new ArrayList<>();
//        a.add(" ");
//        System.out.println(a.size());
        String args1 = "/Users/yanjiejiang/icse/Codec-3b/src/java";
        String args2 = "/Users/yanjiejiang/icse/Codec-3f/src/java";

        String pattern1 ="[0-9]b/";
        Pattern rAdd = Pattern.compile(pattern1);
        Matcher mAdd = rAdd.matcher(args1);
        while(mAdd.find()){
           System.out.println(mAdd.group());
        }

//        int pos = args1.indexOf("b/src");
//        String temp1 = args1.substring(0,pos+1);
//        System.out.println(temp1);



        }




}
