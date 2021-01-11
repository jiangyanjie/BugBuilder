```diff
diff --git a/defects4j/closure_79_fix/src/com/google/javascript/jscomp/Normalize.java b/defects4j/closure_79_buggy/src/com/google/javascript/jscomp/Normalize.java
index 8bee886d..6738b109 100644
--- a/defects4j/closure_79_fix/src/com/google/javascript/jscomp/Normalize.java
+++ b/defects4j/closure_79_buggy/src/com/google/javascript/jscomp/Normalize.java
@@ -119,7 +119,7 @@ class Normalize implements CompilerPass {
   public void process(Node externs, Node root) {
     new NodeTraversal(
         compiler, new NormalizeStatements(compiler, assertOnChange))
-        .traverseRoots(externs, root);
+        .traverse(root);
     if (MAKE_LOCAL_NAMES_UNIQUE) {
       MakeDeclaredNamesUnique renamer = new MakeDeclaredNamesUnique();
       NodeTraversal t = new NodeTraversal(compiler, renamer);
diff --git a/defects4j/closure_79_fix/src/com/google/javascript/jscomp/VarCheck.java b/defects4j/closure_79_buggy/src/com/google/javascript/jscomp/VarCheck.java
index f8ebfa33..c8196b12 100644
--- a/defects4j/closure_79_fix/src/com/google/javascript/jscomp/VarCheck.java
+++ b/defects4j/closure_79_buggy/src/com/google/javascript/jscomp/VarCheck.java
@@ -218,7 +218,6 @@ class VarCheck extends AbstractPostOrderCallback implements CompilerPass {
     getSynthesizedExternsRoot().addChildToBack(
         new Node(Token.VAR, nameNode));
     varsToDeclareInExterns.remove(varName);
-    compiler.reportCodeChange();
   }
 
   /**

```
