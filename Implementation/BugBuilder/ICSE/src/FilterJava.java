import java.io.File;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class FilterJava {

       public static int countFile(String path)
       {
           int fileCount = 0;
           File d = new File(path);
           File list[] = d.listFiles();
           for(int i = 0; i < list.length; i++){
               if(list[i].isFile()) {
                   String abpath = list[i].getAbsolutePath();
                   if(abpath.endsWith("1.java"))
                   {
                       fileCount++;
                   }
               }
           }
           return fileCount;
       }

       public static ArrayList<String> filter(String fileP){
           ArrayList<String> res = new ArrayList<String>();
           JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

           File folder = new File(fileP);
           File[] listOfFiles = folder.listFiles();

           for (File file : listOfFiles) {
               if (file.isFile()) {
                   String fName = file.getAbsolutePath();
                   if(fName.endsWith("1.java"))
                   {
                           ByteArrayOutputStream err = new ByteArrayOutputStream();

                           int result = compiler.run(null, null, err, fName);
                           if(err.toString().contains("import") || err.toString().contains("extends") || err.toString().contains("是公共的"))
                           {
                               result =0;
                           }
                           if (result == 0) {
                               res.add(fName);
                           } else {
                               System.out.println(err.toString());
                           }
                   }
               }
           }






//           System.out.println(count);
           return res;
       }
        public static void main(String[] args) {
////            String path ="/Users/x/ICSE/lang"+getDiffCommit.bugNO;
//            int m =countFile(path);
//            System.out.println(m);
//            ArrayList<Integer> res = filter(m,"");
//            System.out.println(res.size());
//            System.out.println("res size is " + res);

        }

        public static void runFilter()
        {


        }

    }

