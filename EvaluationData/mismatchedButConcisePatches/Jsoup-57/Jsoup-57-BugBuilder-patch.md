```diff
diff --git a/defects4j/jsoup57fix/src/main/java/org/jsoup/nodes/Attributes.java b/defects4j/jsoup57buggy/src/main/java/org/jsoup/nodes/Attributes.java
index 8fdb654..30dffaf 100644
--- a/defects4j/jsoup57fix/src/main/java/org/jsoup/nodes/Attributes.java
+++ b/defects4j/jsoup57buggy/src/main/java/org/jsoup/nodes/Attributes.java
@@ -119,10 +119,9 @@ public class Attributes implements Iterable<Attribute>, Cloneable {
         Validate.notEmpty(key);
         if (attributes == null)
             return;
-        for (Iterator<String> it = attributes.keySet().iterator(); it.hasNext(); ) {
-            String attrKey = it.next();
+        for (String attrKey : attributes.keySet()) {
             if (attrKey.equalsIgnoreCase(key))
-                it.remove();
+                attributes.remove(attrKey);
         }
     }
```
