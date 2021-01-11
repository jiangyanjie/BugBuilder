package refactorbug.handlers.extractMethodRefactoring;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.cdt.debug.ui.breakpointactions.SoundAction;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.internal.corext.refactoring.ParameterInfo;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractMethodRefactoring;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringExecutionHelper;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import refactorbug.handlers.extractVariableRefactoring.ExpressionVisitor;
import refactorbug.handlers.others.Utils;
import refactorbug.handlers.renameRefactoring.MyRenameRefactoring;
import refactorbug.handlers.renameRefactoring.RenameRecord;

public class MyExtractMethodRefactoring {
	public void handleOneProjectForExtractMethod(IJavaProject javaProject, String refactorFile) throws Exception {
		ArrayList<String> lines = Utils.readFile(refactorFile);
		ArrayList<RenameRecord> oneRenames = new ArrayList<RenameRecord>();

		ArrayList<ExtractMethodRecord> extractMethodRecords = new ArrayList<ExtractMethodRecord>();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			String[] cols = line.split("\t");
			if (cols[0].equals("EXTRACT_OPERATION")) {
				ExtractMethodRecord extractMethodRecord = readExtractMethodRecordFromLine(cols);
				extractMethodRecords.add(extractMethodRecord);
			} else if (cols[0].startsWith("RENAME")) {
				RenameRecord oneRename = MyRenameRefactoring.readRenameRecordFromLine(cols);
				oneRenames.add(oneRename);
			} 
		}
		
		extractMethodRecords = removeDuplicate(extractMethodRecords);
		renameExtractMethodRecords(extractMethodRecords, oneRenames);
		
		Collections.sort(extractMethodRecords, new Comparator<ExtractMethodRecord>() {
            @Override
            public int compare(ExtractMethodRecord o1, ExtractMethodRecord o2) {
                // ½µÐò
                 return o2.getStartOffset()-o1.getStartOffset();
            }
        });
		
		for (int i = 0; i < extractMethodRecords.size(); i++) {
			ExtractMethodRecord extractMethodRecord = extractMethodRecords.get(i);
			if (extractMethodRecord.getRefactorType().equals("EXTRACT_OPERATION")) {
				extract(javaProject, extractMethodRecord);
			}
		}
	}
	
	private void renameExtractMethodRecords(ArrayList<ExtractMethodRecord> extractMethodRecords,
			ArrayList<RenameRecord> oneRenames) {
		for (ExtractMethodRecord extractMethodRecord : extractMethodRecords) {
			for (RenameRecord renameRecord : oneRenames) {
				if (renameRecord.getPath().equals(extractMethodRecord.getPath())) {
					if (renameRecord.getOffset() >= extractMethodRecord.getFirstStatementStartOffset()
						&& renameRecord.getOffset() <= extractMethodRecord.getFirstStatementEndOffset()) {
						extractMethodRecord.setFirstStatement(extractMethodRecord.getFirstStatement().replaceAll(
								renameRecord.getOriginalName(), renameRecord.getNewName()));
					}
					if (renameRecord.getOffset() >= extractMethodRecord.getLastStatementStartOffset()
							&& renameRecord.getOffset() <= extractMethodRecord.getLastStatementEndOffset()) {
							extractMethodRecord.setLastStatement(extractMethodRecord.getLastStatement().replaceAll(
									renameRecord.getOriginalName(), renameRecord.getNewName()));
						}
				}
			}
		}
		
	}

	private ArrayList<ExtractMethodRecord> removeDuplicate(ArrayList<ExtractMethodRecord> extractMethodRecords) {
		ArrayList<ExtractMethodRecord> result = new ArrayList<ExtractMethodRecord>();
		for (ExtractMethodRecord extractMethodRecord : extractMethodRecords) {
			if (notExist(extractMethodRecord, result)) {
				result.add(extractMethodRecord);
			}
		}
		return result;
	}

	private boolean notExist(ExtractMethodRecord extractMethodRecord, ArrayList<ExtractMethodRecord> result) {
		for (ExtractMethodRecord extractMethodRecord2 : result) {
			if (extractMethodRecord.getNewMethodStartOffset() == extractMethodRecord2.getNewMethodStartOffset()
					&& extractMethodRecord.getNewMethodEndOffset() == extractMethodRecord2.getNewMethodEndOffset()) {
				return false;
			}
		}
		return true;
	}

	protected void extract(IJavaProject javaProject, ExtractMethodRecord extractMethodRecord) throws Exception {
		IPackageFragment[] fragments = javaProject.getPackageFragments();
		for (int j = 0; j < fragments.length; j++) {
			IPackageFragment fragment = fragments[j];
			ICompilationUnit[] iCompilationUnits = fragment.getCompilationUnits();
			for (int k = 0; k < iCompilationUnits.length; k++) {
				ICompilationUnit iCompilationUnit = iCompilationUnits[k];
				if (iCompilationUnit.getPath().toString().endsWith(extractMethodRecord.getPath())) {
					ExtractMethodRefactoring extractMethodRefactoring = 
							constructFromExtractMethodRecord(iCompilationUnit, extractMethodRecord);
					performExtractMethodVariable(extractMethodRefactoring, extractMethodRecord.getParameterType(), extractMethodRecord.getParameterName());				
				}
			}
		}
	}

	private StatementVisitor getOffset(ICompilationUnit iCompilationUnit, String firstStatement, String lastStatement) {
		ASTParser astParser = ASTParser.newParser(AST.JLS11);  
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setSource(iCompilationUnit);  
		CompilationUnit unit = (CompilationUnit) (astParser.createAST(null));
		StatementVisitor statementVisitor = new StatementVisitor(unit, firstStatement, lastStatement);
		unit.accept(statementVisitor);
		return statementVisitor;
	}
	
	ExtractMethodRefactoring constructFromExtractMethodRecord(ICompilationUnit iCompilationUnit, 
			ExtractMethodRecord extractMethodRecord) {
		CompilationUnit compilationUnit = Utils.getCompilationUnit(iCompilationUnit);
		StatementVisitor statementVisitor = getOffset(iCompilationUnit, 
				extractMethodRecord.getFirstStatement(), extractMethodRecord.getLastStatement());
		
		ExtractMethodRefactoring extractMethodRefactoring = new ExtractMethodRefactoring(compilationUnit, 
				statementVisitor.getStartOffset(), 
				statementVisitor.getEndOffset() - statementVisitor.getStartOffset());
		
		if (extractMethodRecord.getVisibility().equals("public")) {
			extractMethodRefactoring.setVisibility(Modifier.PUBLIC);
		} else if (extractMethodRecord.getVisibility().equals("protected")) {
			extractMethodRefactoring.setVisibility(Modifier.PROTECTED);
		} else if (extractMethodRecord.getVisibility().equals("package")) {
			extractMethodRefactoring.setVisibility(Modifier.NONE);
		} else if (extractMethodRecord.getVisibility().equals("private")) {
			extractMethodRefactoring.setVisibility(Modifier.PRIVATE);
		} else {
			System.out.println("ERROR VISIBILITY");
		}
		
		extractMethodRefactoring.setMethodName(extractMethodRecord.getNewMethodName());
		extractMethodRefactoring.setReplaceDuplicates(true);
		
		List<ParameterInfo> parameterInfos = extractMethodRefactoring.getParameterInfos();
//		
//		for (int i = 0; i < parameterInfos.size(); i++) {
//			parameterInfos.get(i).setNewName(extractMethodRecord.getParameterName().get(i));
//		}
		return extractMethodRefactoring;
	}

	protected void performExtractMethodVariable(ExtractMethodRefactoring refactoring, ArrayList<String> types, ArrayList<String> names)
			throws InterruptedException, InvocationTargetException {
		Shell shell=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		MyRefactoringExecutionHelper helper= new MyRefactoringExecutionHelper(refactoring,
				RefactoringCore.getConditionCheckingFailedSeverity(),
				RefactoringSaveHelper.SAVE_NOTHING,
				shell,
				PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		helper.setExpectedParameter(types, names);
		helper.perform(true, true);
	}
	
	public ExtractMethodRecord readExtractMethodRecordFromLine(String[] cols) {
		String refactorType = cols[0];
		int extractedOperationInvocationNum = Integer.parseInt(cols[1]);
		String visibility = cols[2];
		String abstractOrNot = cols[3];
		String newMethodName = cols[4];
		String returnType = cols[5];
		String path = cols[6];
		int startOffset = Integer.parseInt(cols[7]);
		int endOffset = Integer.parseInt(cols[8]);
		int startLine = Integer.parseInt(cols[9]);
		int startColumn = Integer.parseInt(cols[10]);
		int endLine = Integer.parseInt(cols[11]);
		int endColumn = Integer.parseInt(cols[12]);
		ArrayList<String> parameterType = new ArrayList<String>(Arrays.asList(cols[13].split("##")));
		ArrayList<String> parameterName = new ArrayList<String>(Arrays.asList(cols[14].split("##")));
		String firstStatement = cols[15];
		int firstStatementStartOffset = Integer.parseInt(cols[16]);
		int firstStatementEndOffset = Integer.parseInt(cols[17]);
		String lastStatement = cols[18];
		int lastStatementStartOffset = Integer.parseInt(cols[19]);
		int lastStatementEndOffset = Integer.parseInt(cols[20]);
		int newMethodStartOffset = Integer.parseInt(cols[21]);
		int newMethodEndOffset = Integer.parseInt(cols[22]);
		
		return new ExtractMethodRecord(refactorType, extractedOperationInvocationNum, 
				visibility, abstractOrNot, newMethodName, returnType, path, 
				startOffset, endOffset, startLine, startColumn, endLine, endColumn, 
				parameterType, parameterName, 
				firstStatement, firstStatementStartOffset, firstStatementEndOffset, 
				lastStatement, lastStatementStartOffset, lastStatementEndOffset, 
				newMethodStartOffset, newMethodEndOffset);
	}
}
