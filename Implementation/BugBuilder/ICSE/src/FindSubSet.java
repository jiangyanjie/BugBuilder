import java.io.IOException;
import java.util.ArrayList;

public class FindSubSet {

    static long deleteTokenIndex;
    static long addTokenIndex;
    static long modifyTokenIndex;
    public static String[] addTokenArray;
    public static String[] deleteTokenArray;
    public static String[] modifyTokenArray;

    public static void main(String[] args) throws IOException {
//        extractChange.tokenizer();
//        extractChange.deletetokenizer();
//        getAddToken();
//        getDeleteToken();
//        getModifyToken();
        addTokenIndex = Biannary2Decimal(addTokenArray.length);
        deleteTokenIndex = Biannary2Decimal(deleteTokenArray.length);
        modifyTokenIndex = Biannary2Decimal(modifyTokenArray.length);

        System.out.println("+++"+addTokenIndex+",,,"+addTokenArray.length);
        System.out.println("---"+deleteTokenIndex+",,,"+deleteTokenArray.length);
        System.out.println("***"+modifyTokenIndex+",,,"+modifyTokenArray.length);

//        getAddIndex(addTokenIndex);
//        getDeleteIndex(deleteTokenIndex);
//        getModifyIndex(modifyTokenIndex);

    }
//    public static long getAddIndex (){
//        return Biannary2Decimal(addTokenArray.length);
//    }
//    public static ArrayList<String> addTokenTag = new ArrayList<String>(); //装add标记值
//    public static ArrayList<String> deleteTokenTag = new ArrayList<String>(); //装delete标记值

//    public static void getAddToken() throws IOException {
//        addTokenArray =getAddTokenArray(); // 装add的token
//    }
//
//    public static void getDeleteToken() throws IOException {
//        deleteTokenArray = getDeleteTokenArray(); //装delete的token
//    }
//
//    public static void getModifyToken(){
//        modifyTokenArray = getModifyTokenArray();
//    }

//    public static ArrayList<String> getAddIndex(long addTokenIndex) {
//        String addIntToBinary =intToBinary(addTokenIndex,extractChange.addTokenResult.size());
//        ArrayList<String> addTokenTag = new ArrayList<String>(); //装add标记值
//        for(int i=0;i< addIntToBinary.length();i++)
//        {
//            String temp =String.valueOf(addIntToBinary.charAt(i));
//            addTokenTag.add(temp);
//        }
//        return addTokenTag;
//    }



//    public static ArrayList<String> getDeleteIndex(long deleteTokenIndex) {
//        String deleteIntToBinary =intToBinary(deleteTokenIndex,extractChange.deleteTokenResult.size());
//        ArrayList<String> deleteTokenTag = new ArrayList<String>(); //装delete标记值
//        for(int i=0;i< deleteIntToBinary.length();i++)
//        {
//            String temp =String.valueOf(deleteIntToBinary.charAt(i));
//            deleteTokenTag.add(temp);
//        }
//
//        return deleteTokenTag;
//
//    }

//    public static ArrayList<String> getModifyIndex(long modifyTokenIndex){
//        String modifyIntToBinary = intToBinary(modifyTokenIndex, extractChange.modifyTokenResult.size());
//        ArrayList<String> modifyTokenTag = new ArrayList<String>();
//        for(int i=0;i<modifyIntToBinary.length();i++)
//        {
//            String temp = String.valueOf(modifyIntToBinary.charAt(i));
//            modifyTokenTag.add(temp);
//        }
//        return modifyTokenTag;
//    }



    //step1: 得到addToken deleteToken的数组

//    public static String[] getAddTokenArray(){
//        String[] addTokenArray = extractChange.addTokenResult.toArray(new String[0]);
//        return addTokenArray;
//
//
//    }
//    public static String[] getDeleteTokenArray(){
//        String[] deleteTokenArray = extractChange.deleteTokenResult.toArray(new String[0]);
//        return deleteTokenArray;
//    }
//
//    public static String[] getModifyTokenArray(){
//        String[] modifyTokenArray = extractChange.modifyTokenResult.toArray(new String[0]);
//        return modifyTokenArray;
//    }

    public static long Biannary2Decimal(int len){ //已知二进制的位数，输出最大十进制数
        if(len ==0)
        {
            return 0;
        }
        long sum = 0;
        for (int i=1;i<=len;i++){
            int dt = 1;
            sum+=(long)Math.pow(2,len-i)*dt;
        }
        return  sum;
    }

    public static String intToBinary(long num, int bitNum){
        if(bitNum ==0)
        {
            return "0";
        }
        //1.补零
        String binaryStr = Long.toBinaryString(num);

        if(bitNum<binaryStr.length()) {
            bitNum += bitNum;//不断翻倍8 16 32 64...
        }
        while(binaryStr.length() < bitNum){
            binaryStr = "0"+binaryStr;
        }
        //2.格式化
        String str = "";
        for (int i = 0; i <binaryStr.length();) {
            str += binaryStr.substring(i, i=i+bitNum)+",";
        }
        return str.substring(0, str.length()-1);
    }


}
