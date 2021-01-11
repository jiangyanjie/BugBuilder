```diff
diff --git a/defects4j/lang_14_fix/src/main/java/org/apache/commons/lang3/StringUtils.java b/defects4j/lang_14_buggy/src/main/java/org/apache/commons/lang3/StringUtils.java
index 4733b7e9..535a3f24 100644
--- a/defects4j/lang_14_fix/src/main/java/org/apache/commons/lang3/StringUtils.java
+++ b/defects4j/lang_14_buggy/src/main/java/org/apache/commons/lang3/StringUtils.java
@@ -785,10 +785,7 @@ public class StringUtils {
         if (cs1 == null || cs2 == null) {
             return false;
         }
-        if (cs1 instanceof String && cs2 instanceof String) {
             return cs1.equals(cs2);
-        }
-        return CharSequenceUtils.regionMatches(cs1, false, 0, cs2, 0, Math.max(cs1.length(), cs2.length()));
     }
 
     /**
```
