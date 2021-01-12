#Patch in Defects4j:

![image](https://github.com/jiangyanjie/BugBuilder/blob/main/EvaluationData/mismatchedAndUnconcisePatches/pic/Clsoure96-defects4j.png)

#Our patch:

![image](https://github.com/jiangyanjie/BugBuilder/blob/main/EvaluationData/mismatchedAndUnconcisePatches/pic/Closure96-BugBuilder.png)

#Comparison and explanation:

Moving the declaration of local variables (i.e., parameter and argument) from inside the WHILE iteration to the outside of the iteration should be taken as refactorings. However, this kind of refactorings is not yet supported by the refactoring mining tool that is leveraged by our approach. Consequently, our approach fails to exclude the changes from bug-fixing patch. 
