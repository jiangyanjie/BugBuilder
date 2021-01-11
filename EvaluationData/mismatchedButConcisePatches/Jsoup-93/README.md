#Patch in Defects4j:

![image](https://github.com/SE4Testing/Data4ICSE2021Submission/blob/main/EvaluationData/mismatchedButConcisePatches/pic/jsoup93-defects4j.png)

#Our patch:

![image](https://github.com/SE4Testing/Data4ICSE2021Submission/blob/main/EvaluationData/mismatchedButConcisePatches/pic/jsoup93-our.png)

#Justification for the conciseness of our patch:

Missing the changes (i.e., replacing el.tagName() with el.normalName()) would fail the associated trigger tests, and thus such changes should be taken as part of the bug-fixing patch
