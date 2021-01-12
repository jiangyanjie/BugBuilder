package refactorbug.handlers.renameRefactoring;


import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.RenameJavaElementDescriptor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenamingNameSuggestor;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.refactoring.DelegateUIHelper;
import org.eclipse.jdt.internal.ui.refactoring.reorg.RenameRefactoringWizard;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;


public class Rename {

	public void rename(IJavaElement element, String newName) {
		try {
			RenameJavaElementDescriptor descriptor= createRenameDescriptor(element, newName);
			RenameSupport renameSupport= RenameSupport.create(descriptor);
			
			Shell shell=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			renameSupport.perform(shell, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		}catch (CoreException ex) {
			JavaPlugin.log(ex);
		} catch (InterruptedException ex) {
			// canceling is OK -> redo text changes in that case?
		} catch (InvocationTargetException ex) {
			JavaPlugin.log(ex);
		} finally {

		}
	}
	
	private RenameJavaElementDescriptor createRenameDescriptor(IJavaElement javaElement, String newName) throws JavaModelException {
		String contributionId;
		// see RefactoringExecutionStarter#createRenameSupport(..):
		int elementType= javaElement.getElementType();
		switch (elementType) {
			case IJavaElement.JAVA_PROJECT:
				contributionId= IJavaRefactorings.RENAME_JAVA_PROJECT;
				break;
			case IJavaElement.PACKAGE_FRAGMENT_ROOT:
				contributionId= IJavaRefactorings.RENAME_SOURCE_FOLDER;
				break;
			case IJavaElement.PACKAGE_FRAGMENT:
				contributionId= IJavaRefactorings.RENAME_PACKAGE;
				break;
			case IJavaElement.COMPILATION_UNIT:
				contributionId= IJavaRefactorings.RENAME_COMPILATION_UNIT;
				break;
			case IJavaElement.TYPE:
				contributionId= IJavaRefactorings.RENAME_TYPE;
				break;
			case IJavaElement.METHOD:
				final IMethod method= (IMethod) javaElement;
				if (method.isConstructor())
					return createRenameDescriptor(method.getDeclaringType(), newName);
				else
					contributionId= IJavaRefactorings.RENAME_METHOD;
				break;
			case IJavaElement.FIELD:
				IField field= (IField) javaElement;
				if (field.isEnumConstant())
					contributionId= IJavaRefactorings.RENAME_ENUM_CONSTANT;
				else
					contributionId= IJavaRefactorings.RENAME_FIELD;
				break;
			case IJavaElement.TYPE_PARAMETER:
				contributionId= IJavaRefactorings.RENAME_TYPE_PARAMETER;
				break;
			case IJavaElement.LOCAL_VARIABLE:
				contributionId= IJavaRefactorings.RENAME_LOCAL_VARIABLE;
				break;
			default:
				return null;
		}

		RenameJavaElementDescriptor descriptor= (RenameJavaElementDescriptor) RefactoringCore.getRefactoringContribution(contributionId).createDescriptor();
		descriptor.setJavaElement(javaElement);
		descriptor.setNewName(newName);
		if (elementType != IJavaElement.PACKAGE_FRAGMENT_ROOT)
			descriptor.setUpdateReferences(true);

		IDialogSettings javaSettings= JavaPlugin.getDefault().getDialogSettings();
		IDialogSettings refactoringSettings= javaSettings.getSection(RefactoringWizardPage.REFACTORING_SETTINGS); //TODO: undocumented API
		if (refactoringSettings == null) {
			refactoringSettings= javaSettings.addNewSection(RefactoringWizardPage.REFACTORING_SETTINGS);
		}

		switch (elementType) {
			case IJavaElement.METHOD:
			case IJavaElement.FIELD:
				descriptor.setDeprecateDelegate(refactoringSettings.getBoolean(DelegateUIHelper.DELEGATE_DEPRECATION));
				descriptor.setKeepOriginal(refactoringSettings.getBoolean(DelegateUIHelper.DELEGATE_UPDATING));
		}
		switch (elementType) {
			case IJavaElement.TYPE:
//			case IJavaElement.COMPILATION_UNIT: // TODO
				descriptor.setUpdateSimilarDeclarations(refactoringSettings.getBoolean(RenameRefactoringWizard.TYPE_UPDATE_SIMILAR_ELEMENTS));
				int strategy;
				try {
					strategy= refactoringSettings.getInt(RenameRefactoringWizard.TYPE_SIMILAR_MATCH_STRATEGY);
				} catch (NumberFormatException e) {
					strategy= RenamingNameSuggestor.STRATEGY_EXACT;
				}
				descriptor.setMatchStrategy(strategy);
		}
		switch (elementType) {
			case IJavaElement.PACKAGE_FRAGMENT:
				descriptor.setUpdateHierarchy(refactoringSettings.getBoolean(RenameRefactoringWizard.PACKAGE_RENAME_SUBPACKAGES));
		}
		switch (elementType) {
			case IJavaElement.PACKAGE_FRAGMENT:
			case IJavaElement.TYPE:
				String fileNamePatterns= refactoringSettings.get(RenameRefactoringWizard.QUALIFIED_NAMES_PATTERNS);
				if (fileNamePatterns != null && fileNamePatterns.length() != 0) {
					descriptor.setFileNamePatterns(fileNamePatterns);
					boolean updateQualifiedNames= refactoringSettings.getBoolean(RenameRefactoringWizard.UPDATE_QUALIFIED_NAMES);
					descriptor.setUpdateQualifiedNames(updateQualifiedNames);

				}
		}
		switch (elementType) {
			case IJavaElement.PACKAGE_FRAGMENT:
			case IJavaElement.TYPE:
			case IJavaElement.FIELD:
				boolean updateTextualOccurrences= refactoringSettings.getBoolean(RenameRefactoringWizard.UPDATE_TEXTUAL_MATCHES);
				descriptor.setUpdateTextualOccurrences(updateTextualOccurrences);

		}
		switch (elementType) {
			case IJavaElement.FIELD:
				descriptor.setRenameGetters(refactoringSettings.getBoolean(RenameRefactoringWizard.FIELD_RENAME_GETTER));
				descriptor.setRenameSetters(refactoringSettings.getBoolean(RenameRefactoringWizard.FIELD_RENAME_SETTER));
		}
		return descriptor;
	}

}
