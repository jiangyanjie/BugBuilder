package refactorbug.handlers.renameRefactoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

import refactorbug.handlers.others.Utils;

public class MyRenameRefactoring {
	public void handleOneProjectForRename(IJavaProject javaProject, String refactorFile) throws Exception {
		ArrayList<String> lines = Utils.readFile(refactorFile);
		ArrayList<RenameRecord> oneRenames = new ArrayList<RenameRecord>();
		for (int i = lines.size() - 1; i >= 0; i--) {
			String line = lines.get(i);
			String[] cols = line.split("\t");
			if (cols[0].startsWith("RENAME")) {
				RenameRecord oneRename = readRenameRecordFromLine(cols);
				oneRenames.add(oneRename);
			} 
		}
		
		Collections.sort(oneRenames, new Comparator<RenameRecord>() {
            @Override
            public int compare(RenameRecord o1, RenameRecord o2) {
                // ����
                 return o2.getOffset()-o1.getOffset();
            }
        });
		for (int i = 0; i < oneRenames.size(); i++) {
			RenameRecord oneRename = oneRenames.get(i);
			if (oneRename.getRefactorType().equals("RENAME_VARIABLE") ||
					oneRename.getRefactorType().equals("RENAME_PARAMETER") ||
					oneRename.getRefactorType().equals("RENAME_METHOD") ||
					oneRename.getRefactorType().equals("RENAME_CLASS")) {
				rename(javaProject, oneRename);
			}
		}
	}
	
	protected void rename(IJavaProject javaProject, RenameRecord oneRename) throws JavaModelException {
		IPackageFragment[] fragments = javaProject.getPackageFragments();
		for (int j = 0; j < fragments.length; j++) {
			IPackageFragment fragment = fragments[j];
			ICompilationUnit[] iCompilationUnits = fragment.getCompilationUnits();
			for (int k = 0; k < iCompilationUnits.length; k++) {
				ICompilationUnit iCompilationUnit = iCompilationUnits[k];
				if (iCompilationUnit.getPath().toString().endsWith(oneRename.getPath())) {
					
					IJavaElement[] iJavaElements = iCompilationUnit.codeSelect(oneRename.getOffset(), 0);
					if (iJavaElements.length == 1 && oneRename.getOriginalName().equals(iJavaElements[0].getElementName().toString())) {
						new Rename().rename(iJavaElements[0], oneRename.getNewName());
						System.out.println("rename");
					} else {
						System.out.println(iJavaElements.length);
					}
				}
			}
		}
	}
	
	public static RenameRecord readRenameRecordFromLine(String[] cols) {
		String refactorType = cols[0];
		String path = cols[1];
		int offset = Integer.parseInt(cols[2]);
		String originalName = cols[3];
		String newName = cols[4];
		return new RenameRecord(refactorType, path, offset, originalName, newName);
	}
	
}
