package org.refactoringminer;

import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.UMLParameter;
import gr.uom.java.xmi.decomposition.AbstractCodeFragment;
import gr.uom.java.xmi.decomposition.AbstractCodeMapping;
import gr.uom.java.xmi.decomposition.replacement.Replacement;
import gr.uom.java.xmi.diff.*;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestRefactor {
    public static void main(String[] args) throws Exception {
        String buggyPath ="/Users/yanjiejiang/ICSE/Lang_26_buggy";
        testRefactor(buggyPath);

    }

    public static void testRefactor(String buggyPath) throws Exception {
        String dir = System.getProperty("user.dir");
        ArrayList<String> sha = readFile("./RefactoringMiner-master/src/shaList.txt");
        for(int i=0;i< sha.size();i++){
            String[] info = sha.get(i).split(",");
            GitService gitService = new GitServiceImpl();
            GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

            String projectPath = "/Users/yanjiejiang/ICSE/Lang_26_buggy"; //buggy project path
            Repository repo = gitService.openRepository(projectPath);
            String buggySHA =info[1];
            System.out.println("buggysha " + buggySHA);
            String fixedSHA =info[2];
            String outPath = "./RefactorFile_" + buggySHA + ".txt";
            HandleOnePair(miner, repo, buggySHA, fixedSHA, outPath);
        }
    }

    public static ArrayList<String> readFile(String fileName){
        ArrayList<String> result = new ArrayList<String>();
        File file = new File(fileName);
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            while((tempString = reader.readLine()) != null)
            {
                if(!tempString.equals("")){
                    result.add(tempString);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;
    }

    private static void HandleOnePair(GitHistoryRefactoringMiner miner, Repository repo, String buggySHA, String fixedSHA, String outPath) throws Exception {
        ArrayList<String> refactorLines = new ArrayList<>();
        miner.detectBetweenCommits(repo, buggySHA, fixedSHA,
                new RefactoringHandler() {
                    @Override
                    public void handle(String commitId, List<Refactoring> refactorings) {
                        int i=0;
                        for (Refactoring ref : refactorings) {
                            String line;
                            System.out.println("========k开始========");
                            if (ref instanceof RenameVariableRefactoring) {
                                line = getRenameVariableOrParameterRefactoringLine((RenameVariableRefactoring) ref);
                                refactorLines.add(line);
                                i++;
                            } else if (ref instanceof RenameOperationRefactoring) {
                                line = getRenameMethodRefactoringLine((RenameOperationRefactoring) ref);
                                refactorLines.add(line);
                                i++;
                            } else if (ref instanceof RenameClassRefactoring) {
                                line = getRenameClassRefactoringLine((RenameClassRefactoring) ref);
                                refactorLines.add(line);
                                i++;
                            } else if (ref instanceof ExtractVariableRefactoring) {
                                line = getExtractVariableRefactoringLine((ExtractVariableRefactoring) ref);
                                refactorLines.add(line);
                                i++;
                            } else if (ref instanceof ExtractOperationRefactoring) {
                                line = getExtractMethodRefactoringLine((ExtractOperationRefactoring) ref);
//                                line = ref.toJSON();
                                refactorLines.add(line);
                                i++;
                            } else {
                                line = "todo " + ref.toJSON() + "\n";
                                System.out.println(line);
                                i++;
                            }
                           System.out.println("********结束**********");
                            System.out.println("\n");
                        }

                    }
                });

        writeFile(outPath, refactorLines);
    }

    private static String getExtractMethodRefactoringLine(ExtractOperationRefactoring ref) {
        UMLOperation newMethod = ref.getExtractedOperation();
        ArrayList<String> strs = new ArrayList<>();
        strs.add(ref.getRefactoringType().toString());
        strs.add(String.valueOf(ref.getExtractedOperationInvocations().size()));

        strs.add(newMethod.getVisibility());

        if(newMethod.isAbstract()) {
            strs.add("abstract");
        } else {
            strs.add("noabstract");
        }
        strs.add(newMethod.getMethodName());

        UMLParameter returnParameter = newMethod.getReturnParameter();
        strs.add(returnParameter.getType().getClassType());
        List<UMLParameter> parameters = newMethod.getParameters();


        Set<AbstractCodeFragment> temp = ref.getExtractedCodeFragmentsFromSourceOperation();
        int startOffset = Integer.MAX_VALUE;
        int endOffset = Integer.MIN_VALUE;
        LocationInfo startLocationInfo = null;
        LocationInfo endLocationInfo = null;
        for (AbstractCodeFragment abstractCodeFragment: temp) {
            LocationInfo locationInfo = abstractCodeFragment.getLocationInfo();

            if (locationInfo.getStartOffset() < startOffset) {
                startOffset = locationInfo.getStartOffset();
                startLocationInfo = locationInfo;
            }
            if (locationInfo.getEndOffset() > endOffset) {
                endOffset = locationInfo.getEndOffset();
                endLocationInfo = locationInfo;
            }
        }
        strs.add(startLocationInfo.getFilePath());
        strs.add(String.valueOf(startLocationInfo.getStartOffset()));
        strs.add(String.valueOf(endLocationInfo.getEndOffset()));
        strs.add(String.valueOf(startLocationInfo.getStartLine()));
        strs.add(String.valueOf(startLocationInfo.getStartColumn()));
        strs.add(String.valueOf(endLocationInfo.getEndLine()));
        strs.add(String.valueOf(endLocationInfo.getEndColumn()));

        ArrayList<String> types = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for(int i=0; i<parameters.size(); i++) {
            UMLParameter parameter = parameters.get(i);
            if (!parameter.getKind().equals("return")) {
                names.add(parameter.getName());
                types.add(parameter.getType().getClassType());
            }
        }
        strs.add(String.join("##", types));
        strs.add(String.join("##", names));
        strs.add(startLocationInfo.getMyString().replaceAll("\n", ""));
        strs.add(String.valueOf(startLocationInfo.getStartOffset()));
        strs.add(String.valueOf(startLocationInfo.getEndOffset()));
        strs.add(endLocationInfo.getMyString().replaceAll("\n", ""));
        strs.add(String.valueOf(endLocationInfo.getStartOffset()));
        strs.add(String.valueOf(endLocationInfo.getEndOffset()));
        strs.add(String.valueOf(ref.getExtractedOperation().getLocationInfo().getStartOffset()));
        strs.add(String.valueOf(ref.getExtractedOperation().getLocationInfo().getEndOffset()));

        return String.join("\t", strs) + "\n";
    }

    private static String getExtractVariableRefactoringLine(ExtractVariableRefactoring extractVariableRefactoring) {
        Set<AbstractCodeMapping> references = extractVariableRefactoring.getReferences();
        for (AbstractCodeMapping reference: references) {
            Set<Replacement> replacements = reference.getReplacements();
            for (Replacement replacement: replacements) {
                if (replacement.getAfter().equals(extractVariableRefactoring.getVariableDeclaration().getVariableName())) {
                    AbstractCodeFragment beforeFragment = reference.getFragment1();
                    return extractVariableRefactoring.getRefactoringType()
                            + "\t"
                            + beforeFragment.getLocationInfo().getFilePath()
                            + "\t"
                            + replacement.getBefore()
                            + "\t"
                            + extractVariableRefactoring.getVariableDeclaration().getVariableName()
                            + "\t"
                            + beforeFragment.getLocationInfo().getStartLine()
                            + "\n";
                }
            }
        }
        throw new IllegalArgumentException("Error Extract Variable");
    }

    private static String getRenameClassRefactoringLine(RenameClassRefactoring ref) {
        RenameClassRefactoring renameClassRefactoring = ref;

        return renameClassRefactoring.getRefactoringType()
                + "\t"
                + renameClassRefactoring.getOriginalClass().getLocationInfo().getFilePath()
                + "\t"
                + renameClassRefactoring.getOriginalClass().getLocationInfo().getMyOffset()
                + "\t"
                + getLastString(renameClassRefactoring.getOriginalClass().getName())
                + "\t"
                + getLastString(renameClassRefactoring.getRenamedClass().getName())
                + "\n";
    }

    private static String getLastString(String str) {
        if (str.contains(".")) {
            String[] parts = str.split("\\.");
            return parts[parts.length-1];
        }
        return str;
    }

    private static String getRenameMethodRefactoringLine(RenameOperationRefactoring ref) {
        RenameOperationRefactoring renameOperationRefactoring = ref;
        return renameOperationRefactoring.getRefactoringType()
                + "\t"
                + renameOperationRefactoring.getOriginalOperation().getLocationInfo().getFilePath()
                + "\t"
                + renameOperationRefactoring.getOriginalOperation().getLocationInfo().getMyOffset()
                + "\t"
                + renameOperationRefactoring.getOriginalOperation().getMethodName()
                + "\t"
                + renameOperationRefactoring.getRenamedOperation().getMethodName()
                + "\n";
    }

    private static String getRenameVariableOrParameterRefactoringLine(RenameVariableRefactoring ref) {
        RenameVariableRefactoring renameVariableRefactoring = ref;
        return renameVariableRefactoring.getRefactoringType()
                + "\t"
                + renameVariableRefactoring.getOriginalVariable().getLocationInfo().getFilePath()
                + "\t"
                + renameVariableRefactoring.getOriginalVariable().getLocationInfo().getMyOffset()
                + "\t"
                + renameVariableRefactoring.getOriginalVariable().getVariableName()
                + "\t"
                + renameVariableRefactoring.getRenamedVariable().getVariableName()
                + "\n";
    }

    public static void writeFile(String path, ArrayList<String> lines){
        FileWriter fw = null;
        try{
            File f = new File(path);
            fw = new FileWriter(f, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter pw = new PrintWriter(fw);
        for (String line: lines) {
            pw.print(line);
        }
        pw.flush();
        try{
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
