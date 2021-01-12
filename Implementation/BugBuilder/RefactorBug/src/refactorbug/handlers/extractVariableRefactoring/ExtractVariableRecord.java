package refactorbug.handlers.extractVariableRefactoring;

public class ExtractVariableRecord {
	public String refactorType;
	public String path;

	public String originalExpression;
	public String newName;
	public int lineNum;
	public ExtractVariableRecord(String refactorType, String path, String originalExpression, String newName, int lineNum) {
		super();
		this.refactorType = refactorType;
		this.path = path;
		this.originalExpression = originalExpression;
		this.newName = newName;
		this.lineNum = lineNum;
	}
	public String getRefactorType() {
		return refactorType;
	}
	public void setRefactorType(String refactorType) {
		this.refactorType = refactorType;
	}
	public String getOriginalExpression() {
		return originalExpression;
	}
	public void setOriginalExpression(String originalExpression) {
		this.originalExpression = originalExpression;
	}
	public String getNewName() {
		return newName;
	}
	public void setNewName(String newName) {
		this.newName = newName;
	}
	public int getLineNum() {
		return lineNum;
	}
	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	
}