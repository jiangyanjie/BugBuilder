#Patch in Defects4j:

![image](https://github.com/jiangyanjie/BugBuilder/blob/main/EvaluationData/mismatchedButConcisePatches/pic/jsoup82-defects4j.png)

#Our patch:

![image](https://github.com/jiangyanjie/BugBuilder/blob/main/EvaluationData/mismatchedButConcisePatches/pic/jsoup82-our.png)

#Justification for the conciseness of our patch:

The added statements (“Charset charset=Charset.forName(charsetName); doc.outputSettings().charset(charset)” is not equal to the deleted statement (doc.outputSettings().charset(charsetName);"). Missing the changes would fail the associated trigger tests, and thus such changes should be taken as part of the bug-fixing patch
