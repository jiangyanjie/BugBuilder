package refactorbug.handlers.extractMethodRefactoring;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;


public class StatementVisitor extends ASTVisitor{
	private CompilationUnit unit;
	private int startOffset = 0;
	private int endOffset = 0;
	private String firstStatement;
	private String lastStatement;
	
	public StatementVisitor(CompilationUnit unit, String firstExpression, String lastExpression) {
		super();
		this.unit = unit;
		this.firstStatement = firstExpression;
		this.lastStatement = lastExpression;
	}

	@Override
	public void preVisit(ASTNode node) {
		String nodeString = node.toString().replaceAll("\n", "");
		if (nodeString.equals(this.firstStatement)) {
			this.startOffset = node.getStartPosition();
		}
		if (nodeString.equals(this.lastStatement)) {
			this.endOffset = node.getStartPosition() + node.getLength();
		}
		super.preVisit(node);
	}

	public int getStartOffset() {
		return startOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}
}
