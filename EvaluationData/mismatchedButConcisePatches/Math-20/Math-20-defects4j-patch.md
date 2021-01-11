```diff
diff --git a/defects4j/math_20_fix/src/main/java/org/apache/commons/math3/optimization/direct/CMAESOptimizer.java b/defects4j/math_20_buggy/src/main/java/org/apache/commons/math3/optimization/direct/CMAESOptimizer.java
index b54cb3744..4b7dbf6bb 100644
--- a/defects4j/math_20_fix/src/main/java/org/apache/commons/math3/optimization/direct/CMAESOptimizer.java
+++ b/defects4j/math_20_buggy/src/main/java/org/apache/commons/math3/optimization/direct/CMAESOptimizer.java
@@ -918,8 +918,7 @@ public class CMAESOptimizer
          * @return the original objective variables, possibly repaired.
          */
         public double[] repairAndDecode(final double[] x) {
-            return boundaries != null && isRepairMode ?
-                decode(repair(x)) :
+            return
                 decode(x);
         }
```
