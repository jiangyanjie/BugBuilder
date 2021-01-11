```diff
diff --git a/defects4j/closure96fix/src/com/google/javascript/jscomp/TypeCheck.java b/defects4j/closure96buggy/src/com/google/javascript/jscomp/TypeCheck.java
index b05fbc44..8198efc8 100644
--- a/defects4j/closure96fix/src/com/google/javascript/jscomp/TypeCheck.java
+++ b/defects4j/closure96buggy/src/com/google/javascript/jscomp/TypeCheck.java
@@ -1403,17 +1403,9 @@ public class TypeCheck implements NodeTraversal.Callback, CompilerPass {
 
     Iterator<Node> parameters = functionType.getParameters().iterator();
     int ordinal = 0;
-    Node parameter = null;
-    Node argument = null;
     while (arguments.hasNext() &&
-           (parameters.hasNext() ||
-            parameter != null && parameter.isVarArgs())) {
+            parameters.hasNext()) {

-      if (parameters.hasNext()) {
-        parameter = parameters.next();
+        Node parameter = parameters.next();
-      }
-      argument = arguments.next();
+      Node argument = arguments.next();
       ordinal++;
 
       validator.expectArgumentMatchesParameter(t, argument,

```
