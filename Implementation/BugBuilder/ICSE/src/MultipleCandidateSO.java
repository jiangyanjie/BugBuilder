import java.io.File;
import java.util.Collections;
import java.util.ArrayList;
public class MultipleCandidateSO {

    public static ArrayList<String> getCandidiate(ArrayList<String> input){
        ArrayList<String> res = new ArrayList<>();
        Collections.sort(input);
        String failingTestNum = input.get(0).split(",")[0];
        for(int i=0;i< input.size();i++){
            String fn = input.get(i).split(",")[0];
            if(fn.equals(failingTestNum)){
                res.add(input.get(i));
            }
        }
        return res;
    }

    public static void selectOne(){
        String dir = System.getProperty("user.dir");
        String p1 = dir +"/src/diff";

        File folder = new File(p1);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            System.out.println(file);
            if (file.isDirectory()) {
                String dPath = file.getAbsolutePath();
                System.out.println(dPath);

            }
        }
    }





    public static void main(String[] args){
        System.out.println("---");
        selectOne();

    }

}

