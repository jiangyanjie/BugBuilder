package refactorbug.handlers.others;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class Utils {
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
	public static CompilationUnit getCompilationUnit(ICompilationUnit iCompilationUnit) {
		ASTParser astParser = ASTParser.newParser(AST.JLS11);  
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setSource(iCompilationUnit);  
		astParser.setResolveBindings(true);
		astParser.setBindingsRecovery(true);
		CompilationUnit unit = (CompilationUnit) (astParser.createAST(null));
		return unit;
	}
}
