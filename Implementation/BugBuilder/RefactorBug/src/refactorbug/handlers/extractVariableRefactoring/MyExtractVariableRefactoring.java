package refactorbug.handlers.extractVariableRefactoring;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractTempRefactoring;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringExecutionHelper;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import refactorbug.handlers.others.Utils;

public class MyExtractVariableRefactoring {
	public void handleOneProjectForExtractVariable(IJavaProject javaProject, String refactorFile) throws Exception {
		ArrayList<String> lines = Utils.readFile(refactorFile);
		ArrayList<ExtractVariableRecord> extractVariableRecords = new ArrayList<ExtractVariableRecord>();
		for (int i = lines.size() - 1; i >= 0; i--) {
			String line = lines.get(i);
			String[] cols = line.split("\t");
			if (cols[0].equals("EXTRACT_VARIABLE")) {
				ExtractVariableRecord extractVariableRecord = readExtractVariableRecordFromLine(cols);
				extractVariableRecords.add(extractVariableRecord);
			}
		}
		
		Collections.sort(extractVariableRecords, new Comparator<ExtractVariableRecord>() {
            @Override
            public int compare(ExtractVariableRecord o1, ExtractVariableRecord o2) {
                // ½µÐò
                 return o2.getLineNum()-o1.getLineNum();
            }
        });
		
		for (int i = 0; i < extractVariableRecords.size(); i++) {
			ExtractVariableRecord extractVariableRecord = extractVariableRecords.get(i);
			if (extractVariableRecord.getRefactorType().equals("EXTRACT_VARIABLE")) {
				extract(javaProject, extractVariableRecord);
			}
		}
	}
	protected void extract(IJavaProject javaProject, ExtractVariableRecord extractVariableRecord) throws Exception {
		IPackageFragment[] fragments = javaProject.getPackageFragments();
		for (int j = 0; j < fragments.length; j++) {
			IPackageFragment fragment = fragments[j];
			ICompilationUnit[] iCompilationUnits = fragment.getCompilationUnits();
			for (int k = 0; k < iCompilationUnits.length; k++) {
				ICompilationUnit iCompilationUnit = iCompilationUnits[k];
				if (iCompilationUnit.getPath().toString().endsWith(extractVariableRecord.getPath())) {
					int offset = getOffset(iCompilationUnit, extractVariableRecord.getOriginalExpression(), extractVariableRecord.getLineNum());
					CompilationUnit compilationUnit = Utils.getCompilationUnit(iCompilationUnit);
					ExtractTempRefactoring refactoring= new ExtractTempRefactoring(compilationUnit,
							offset,extractVariableRecord.getOriginalExpression().length());
					
					
					refactoring.setTempName(extractVariableRecord.getNewName());
					
					performExtractLocalVariable(refactoring);				
				}
			}
		}
	}
	
	protected void performExtractLocalVariable(ExtractTempRefactoring refactoring)
			throws InterruptedException, InvocationTargetException {
		Shell shell=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		RefactoringExecutionHelper helper= new RefactoringExecutionHelper(refactoring,
				RefactoringCore.getConditionCheckingFailedSeverity(),
				RefactoringSaveHelper.SAVE_NOTHING,
				shell,
				PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		helper.perform(true, true);
	}
	
	private int getOffset(ICompilationUnit iCompilationUnit, String originalExpression, int lineNum) {
		ASTParser astParser = ASTParser.newParser(AST.JLS11);  
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setSource(iCompilationUnit);  
		CompilationUnit unit = (CompilationUnit) (astParser.createAST(null));
		ExpressionVisitor expressionVisitor = new ExpressionVisitor(unit, originalExpression, lineNum);
		unit.accept(expressionVisitor);
		return expressionVisitor.getOffset();
	}
	
	public ExtractVariableRecord readExtractVariableRecordFromLine(String[] cols) {
		String refactorType = cols[0];
		String path = cols[1];
		String originalExpression = cols[2];
		String newName = cols[3];
		int lineNum = Integer.parseInt(cols[4]);
		return new ExtractVariableRecord(refactorType, path, originalExpression, newName, lineNum);
	}
}
