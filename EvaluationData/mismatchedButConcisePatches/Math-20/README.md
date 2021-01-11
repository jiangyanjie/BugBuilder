#Patch in Defects4j:

![image](https://github.com/SE4Testing/Data4ICSE2021Submission/blob/main/EvaluationData/mismatchedButConcisePatches/pic/math20-defects4j.png)

#Our patch:

![image](https://github.com/SE4Testing/Data4ICSE2021Submission/blob/main/EvaluationData/mismatchedButConcisePatches/pic/math20-our.png)

#Justification for the conciseness of our patch:

Defects4J supposes that the developers inserted the body of the new method repairAndDecode as an argument in the creation of a PointValuePair instance, and then extracted the inserted source code as a new method by applying extract method refactoring. However, it is unlikely for the developers to insert such a complex method body as an argument. It more likely that the developers created a new method repairAndDecode and then called it to fix the incorrect argument. Consequently, all such changes as a whole should be taken as a bug-fixing patch
