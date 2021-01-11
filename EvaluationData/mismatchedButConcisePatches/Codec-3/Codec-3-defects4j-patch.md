```diff
diff --git a/defects4j/codec_3_fix/src/java/org/apache/commons/codec/language/DoubleMetaphone.java b/defects4j/codec_3_buggy/src/java/org/apache/commons/codec/language/DoubleMetaphone.java
index f7122d41..e2b8e110 100644
--- a/defects4j/codec_3_fix/src/java/org/apache/commons/codec/language/DoubleMetaphone.java
+++ b/defects4j/codec_3_buggy/src/java/org/apache/commons/codec/language/DoubleMetaphone.java
@@ -452,7 +452,7 @@ public class DoubleMetaphone implements StringEncoder {
             if ((contains(value, 0 ,4, "VAN ", "VON ") || contains(value, 0, 3, "SCH")) || contains(value, index + 1, 2, "ET")) {
                 //-- obvious germanic --//
                 result.append('K');
-            } else if (contains(value, index + 1, 3, "IER")) {
+            } else if (contains(value, index + 1, 4, "IER")) {
                 result.append('J');
             } else {
                 result.append('J', 'K');
```
