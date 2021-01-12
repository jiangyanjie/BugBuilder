package refactorbug.handlers.renameRefactoring;

public class RenameRecord {
	public String refactorType;
	public String path;
	public int offset;
	public String originalName;
	public String newName;

	public RenameRecord(String refactorType, String path, int offset, String originalName, String newName) {
		super();
		this.refactorType = refactorType;
		this.path = path;
		this.offset = offset;
		this.originalName = originalName;
		this.newName = newName;
	}

	public String getRefactorType() {
		return refactorType;
	}

	public String getPath() {
		return path;
	}

	public int getOffset() {
		return offset;
	}

	public String getOriginalName() {
		return originalName;
	}

	public String getNewName() {
		return newName;
	}
}