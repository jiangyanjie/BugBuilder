package refactorbug.handlers.extractVariableRefactoring;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;


public class ExpressionVisitor extends ASTVisitor{
	private int offset = 0;
	private CompilationUnit unit;
	private String originalExpression;
	private int lineNum;
	
	
	public ExpressionVisitor(CompilationUnit unit, String originalExpression, int lineNum) {
		super();
		this.unit = unit;
		this.originalExpression = originalExpression;
		this.lineNum = lineNum;
	}
	@Override
	public void preVisit(ASTNode node) {
		String nodeString = node.toString();
//		System.out.println(nodeString);
		if (nodeString.equals(this.originalExpression) && unit.getLineNumber(node.getStartPosition()) == lineNum) {
			this.offset = node.getStartPosition();
		}
		super.preVisit(node);
	}
	public int getOffset() {
		return offset;
	}
}
