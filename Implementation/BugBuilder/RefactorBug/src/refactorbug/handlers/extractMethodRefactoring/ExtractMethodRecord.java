package refactorbug.handlers.extractMethodRefactoring;

import java.util.ArrayList;		

public class ExtractMethodRecord {
	private String refactorType;
	private int extractedOperationInvocationNum;
	private String visibility;
	private String abstractOrNot;
	private String newMethodName;
	private String returnType;
	private String path;
	private int startOffset;
	private int endOffset;
	private int startLine;
	private int startColumn;
	private int endLine;
	private int endColumn;
	private ArrayList<String> parameterType;
	private ArrayList<String> parameterName;
	private String firstStatement;
	private int firstStatementStartOffset;
	private int firstStatementEndOffset;
	private String lastStatement;
	private int lastStatementStartOffset;
	private int lastStatementEndOffset;
	private int newMethodStartOffset;
	private int newMethodEndOffset;

	

	public ExtractMethodRecord(String refactorType, int extractedOperationInvocationNum, String visibility,
			String abstractOrNot, String newMethodName, String returnType, String path, int startOffset, int endOffset,
			int startLine, int startColumn, int endLine, int endColumn, ArrayList<String> parameterType,
			ArrayList<String> parameterName, String firstStatement, int firstStatementStartOffset,
			int firstStatementEndOffset, String lastStatement, int lastStatementStartOffset, int lastStatementEndOffset,
			int newMethodStartOffset, int newMethodEndOffset) {
		super();
		this.refactorType = refactorType;
		this.extractedOperationInvocationNum = extractedOperationInvocationNum;
		this.visibility = visibility;
		this.abstractOrNot = abstractOrNot;
		this.newMethodName = newMethodName;
		this.returnType = returnType;
		this.path = path;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.startLine = startLine;
		this.startColumn = startColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
		this.parameterType = parameterType;
		this.parameterName = parameterName;
		this.firstStatement = firstStatement;
		this.firstStatementStartOffset = firstStatementStartOffset;
		this.firstStatementEndOffset = firstStatementEndOffset;
		this.lastStatement = lastStatement;
		this.lastStatementStartOffset = lastStatementStartOffset;
		this.lastStatementEndOffset = lastStatementEndOffset;
		this.newMethodStartOffset = newMethodStartOffset;
		this.newMethodEndOffset = newMethodEndOffset;
	}

	public String getRefactorType() {
		return refactorType;
	}
	
	public void setRefactorType(String refactorType) {
		this.refactorType = refactorType;
	}
	public int getExtractedOperationInvocationNum() {
		return extractedOperationInvocationNum;
	}
	public void setExtractedOperationInvocationNum(int extractedOperationInvocationNum) {
		this.extractedOperationInvocationNum = extractedOperationInvocationNum;
	}
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	public String getAbstractOrNot() {
		return abstractOrNot;
	}
	public void setAbstractOrNot(String abstractOrNot) {
		this.abstractOrNot = abstractOrNot;
	}
	public String getNewMethodName() {
		return newMethodName;
	}
	public void setNewMethodName(String newMethodName) {
		this.newMethodName = newMethodName;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getStartOffset() {
		return startOffset;
	}
	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}
	public int getEndOffset() {
		return endOffset;
	}
	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}
	public int getStartLine() {
		return startLine;
	}
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}
	public int getStartColumn() {
		return startColumn;
	}
	public void setStartColumn(int startColumn) {
		this.startColumn = startColumn;
	}
	public int getEndLine() {
		return endLine;
	}
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	public int getEndColumn() {
		return endColumn;
	}
	public void setEndColumn(int endColumn) {
		this.endColumn = endColumn;
	}
	public ArrayList<String> getParameterType() {
		return parameterType;
	}
	public void setParameterType(ArrayList<String> parameterType) {
		this.parameterType = parameterType;
	}
	public ArrayList<String> getParameterName() {
		return parameterName;
	}
	public void setParameterName(ArrayList<String> parameterName) {
		this.parameterName = parameterName;
	}
	public String getFirstStatement() {
		return firstStatement;
	}
	public void setFirstStatement(String firstStatement) {
		this.firstStatement = firstStatement;
	}
	public String getLastStatement() {
		return lastStatement;
	}
	public void setLastStatement(String lastStatement) {
		this.lastStatement = lastStatement;
	}
	public int getNewMethodStartOffset() {
		return newMethodStartOffset;
	}
	public void setNewMethodStartOffset(int newMethodStartOffset) {
		this.newMethodStartOffset = newMethodStartOffset;
	}
	public int getNewMethodEndOffset() {
		return newMethodEndOffset;
	}
	public void setNewMethodEndOffset(int newMethodEndOffset) {
		this.newMethodEndOffset = newMethodEndOffset;
	}

	public int getFirstStatementStartOffset() {
		return firstStatementStartOffset;
	}

	public void setFirstStatementStartOffset(int firstStatementStartOffset) {
		this.firstStatementStartOffset = firstStatementStartOffset;
	}

	public int getFirstStatementEndOffset() {
		return firstStatementEndOffset;
	}

	public void setFirstStatementEndOffset(int firstStatementEndOffset) {
		this.firstStatementEndOffset = firstStatementEndOffset;
	}

	public int getLastStatementStartOffset() {
		return lastStatementStartOffset;
	}

	public void setLastStatementStartOffset(int lastStatementStartOffset) {
		this.lastStatementStartOffset = lastStatementStartOffset;
	}

	public int getLastStatementEndOffset() {
		return lastStatementEndOffset;
	}

	public void setLastStatementEndOffset(int lastStatementEndOffset) {
		this.lastStatementEndOffset = lastStatementEndOffset;
	}
	
	
}