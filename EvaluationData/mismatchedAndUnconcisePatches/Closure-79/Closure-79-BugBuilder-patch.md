```diff
diff --git a/defects4j/closure79fix/src/com/google/javascript/jscomp/Normalize.java b/defects4j/closure79buggy/src/com/google/javascript/jscomp/Normalize.java
index 8bee886d..3d4951fa 100644
--- a/defects4j/closure79fix/src/com/google/javascript/jscomp/Normalize.java
+++ b/defects4j/closure79buggy/src/com/google/javascript/jscomp/Normalize.java
@@ -117,9 +117,8 @@ class Normalize implements CompilerPass {
 
   @Override
   public void process(Node externs, Node root) {
-    new NodeTraversal(
-        compiler, new NormalizeStatements(compiler, assertOnChange))
-        .traverseRoots(externs, root);
+    NodeTraversal.traverse(compiler, root,
+        new NormalizeStatements(compiler, assertOnChange));
     if (MAKE_LOCAL_NAMES_UNIQUE) {
       MakeDeclaredNamesUnique renamer = new MakeDeclaredNamesUnique();
       NodeTraversal t = new NodeTraversal(compiler, renamer);
diff --git a/defects4j/closure79fix/src/com/google/javascript/jscomp/VarCheck.java b/defects4j/closure79buggy/src/com/google/javascript/jscomp/VarCheck.java
index f8ebfa33..c8196b12 100644
--- a/defects4j/closure79fix/src/com/google/javascript/jscomp/VarCheck.java
+++ b/defects4j/closure79buggy/src/com/google/javascript/jscomp/VarCheck.java
@@ -218,7 +218,6 @@ class VarCheck extends AbstractPostOrderCallback implements CompilerPass {
     getSynthesizedExternsRoot().addChildToBack(
         new Node(Token.VAR, nameNode));
     varsToDeclareInExterns.remove(varName);
-    compiler.reportCodeChange();
   }
 
   /**

```
