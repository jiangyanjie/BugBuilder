```diff
diff --git a/defects4j/jsoup_13_fix/src/main/java/org/jsoup/nodes/Node.java b/defects4j/jsoup_13_buggy/src/main/java/org/jsoup/nodes/Node.java
index 9456fc4..3d0bf2e 100644
--- a/defects4j/jsoup_13_fix/src/main/java/org/jsoup/nodes/Node.java
+++ b/defects4j/jsoup_13_buggy/src/main/java/org/jsoup/nodes/Node.java
@@ -104,11 +104,6 @@ public abstract class Node implements Cloneable {
     public boolean hasAttr(String attributeKey) {
         Validate.notNull(attributeKey);
 
-        if (attributeKey.toLowerCase().startsWith("abs:")) {
-            String key = attributeKey.substring("abs:".length());
-            if (attributes.hasKey(key) && !absUrl(key).equals(""))
-                return true;
-        }
         return attributes.hasKey(attributeKey);
     }

```
