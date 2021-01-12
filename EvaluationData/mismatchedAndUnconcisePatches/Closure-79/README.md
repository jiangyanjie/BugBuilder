#Patch in Defects4j:

![image](https://github.com/jiangyanjie/BugBuilder/blob/main/EvaluationData/mismatchedAndUnconcisePatches/pic/Closure79-defects4j.png)

#Our patch:

![image](https://github.com/jiangyanjie/BugBuilder/blob/main/EvaluationData/mismatchedAndUnconcisePatches/pic/Closure79-BugBuilder.png)

#Comparison and explanation:

Replacing the method invocation of NodeTraversal.traverse with an instance creation statement is something like inline method refactoring because the instance creation statement invokes a construction method whose body is exactly the same as the replaced statements. They should be taken as refactorings other than bug-fixing. 
Notably, refactoring is debatable because the changes are not to improve the quality of the program (as refactorings usually do), but to serve as an indispensable part of the bug-fix: to fix the bug, developers have to replace the method invocation with the method body, and change the new statements. They CANNOT change the invocated method directly because the method is correct (and thus should not be changed) for other users who invoke the method.  
We conservatively take it as an unconcise patch for our approach although it is debatable. 
