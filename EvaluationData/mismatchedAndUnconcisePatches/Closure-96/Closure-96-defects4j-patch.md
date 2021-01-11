```diff
diff --git a/defects4j/closure_96_fix/src/com/google/javascript/jscomp/TypeCheck.java b/defects4j/closure_96_buggy/src/com/google/javascript/jscomp/TypeCheck.java
index b05fbc44..8077e0d8 100644
--- a/defects4j/closure_96_fix/src/com/google/javascript/jscomp/TypeCheck.java
+++ b/defects4j/closure_96_buggy/src/com/google/javascript/jscomp/TypeCheck.java
@@ -1406,13 +1406,10 @@ public class TypeCheck implements NodeTraversal.Callback, CompilerPass {
     Node parameter = null;
     Node argument = null;
     while (arguments.hasNext() &&
-           (parameters.hasNext() ||
-            parameter != null && parameter.isVarArgs())) {
+           parameters.hasNext()) {
       // If there are no parameters left in the list, then the while loop
       // above implies that this must be a var_args function.
-      if (parameters.hasNext()) {
         parameter = parameters.next();
-      }
       argument = arguments.next();
       ordinal++;
 

```
