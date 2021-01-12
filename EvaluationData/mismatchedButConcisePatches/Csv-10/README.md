#Patch in Defects4j:

![image](https://github.com/jiangyanjie/BugBuilder/blob/main/EvaluationData/mismatchedButConcisePatches/pic/csv10-defects4j.png)

#Our patch:

![image](https://github.com/jiangyanjie/BugBuilder/blob/main/EvaluationData/mismatchedButConcisePatches/pic/csv10-our.png)

#Justification for the conciseness of our patch:

Declaring a new exception in the method signature should not be taken as a refactoring. Instead, if it is the consequence of the bug-fixing changes (invocation of printRecord). Consequently, it should be taken as a part of the bug-fixing patch
