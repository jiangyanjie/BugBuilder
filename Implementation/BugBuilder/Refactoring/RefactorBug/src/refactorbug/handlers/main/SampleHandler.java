package refactorbug.handlers.main;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import refactorbug.handlers.extractMethodRefactoring.MyExtractMethodRefactoring;
import refactorbug.handlers.extractVariableRefactoring.MyExtractVariableRefactoring;
import refactorbug.handlers.renameRefactoring.MyRenameRefactoring;

public class SampleHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		try {
			handleCommand();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void handleCommand() throws Exception {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];
			IJavaProject javaProject = JavaCore.create(project);
			String refactorPath = 
					"/Users/x/hurefactoring/RefactorFile_c5ec81dc0b6aaaefcdfd84ee10e43f3312350d23.txt";
			handleOneProject(javaProject, refactorPath);
		}
	}
	protected void handleOneProject(IJavaProject javaProject, String refactorFile) throws Exception {
		new MyRenameRefactoring().handleOneProjectForRename(javaProject, refactorFile);
		new MyExtractVariableRefactoring().handleOneProjectForExtractVariable(javaProject, refactorFile);
		new MyExtractMethodRefactoring().handleOneProjectForExtractMethod(javaProject, refactorFile);
	}
}
