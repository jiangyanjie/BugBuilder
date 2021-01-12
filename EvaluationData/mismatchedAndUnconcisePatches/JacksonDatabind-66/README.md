#Patch in Defects4j:

![image](https://github.com/jiangyanjie/BugBuilder/blob/main/EvaluationData/mismatchedAndUnconcisePatches/pic/JacksonDatabind66-defects4j.png)

#Our patch:

![image](https://github.com/jiangyanjie/BugBuilder/blob/main/EvaluationData/mismatchedAndUnconcisePatches/pic/JacksonDatabind66-BugBuilder.png)

#Comparison and explanation:

Event the buggy version does not throws `JsonProcessingException`. Consequently, removing it is not to fix the bug, but to clean the code. `@SuppressWarnings` is a setting for compilers, but has no effect on the resulting binary code (and thus has no effect on the program). Consequently, these changes are not bug-fixing actions, and should be excluded from the bug-fixing patch.
