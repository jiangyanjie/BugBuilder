#Patch in Defects4j:

![image](https://github.com/SE4Testing/Data4ICSE2021Submission/blob/main/EvaluationData/mismatchedButConcisePatches/pic/lang3-defects4j.png)

#Our patch:

![image](https://github.com/SE4Testing/Data4ICSE2021Submission/blob/main/EvaluationData/mismatchedButConcisePatches/pic/lang3-our.png)

#Justification for the conciseness of our patch:

Although declaring and initializing the local variable (numDecimals) would not change the external behaviors of the program, they are not a common refactoring. Common refactorings are expected to improve the readability and maintainability of software programs, but the declaration and initialization are not. They had better be taken as part of the bug-fixing: the only purpose of the declared and initialization is to facilitate the following inserted if statements (bug-fixing changes)
