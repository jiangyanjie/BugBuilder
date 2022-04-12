import java.io.File;

public class deleteAllFile {

    public static void deleteFile(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File f : files) {
//                System.out.println(f);
                deleteFile(f);
            }
        }
        file.delete();
    }

    public static void delFileMake(String file){
        File url = new File(file);
        deleteFile(url);
        url.mkdir();

    }
}
