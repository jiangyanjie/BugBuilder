```diff
diff --git a/defects4j/jsoup_57_fix/src/main/java/org/jsoup/nodes/Attributes.java b/defects4j/jsoup_57_buggy/src/main/java/org/jsoup/nodes/Attributes.java
index 8fdb654..49ff25d 100644
--- a/defects4j/jsoup_57_fix/src/main/java/org/jsoup/nodes/Attributes.java
+++ b/defects4j/jsoup_57_buggy/src/main/java/org/jsoup/nodes/Attributes.java
@@ -122,7 +122,7 @@ public class Attributes implements Iterable<Attribute>, Cloneable {
         for (Iterator<String> it = attributes.keySet().iterator(); it.hasNext(); ) {
             String attrKey = it.next();
             if (attrKey.equalsIgnoreCase(key))
-                it.remove();
+                attributes.remove(attrKey);
         }
     }
```
