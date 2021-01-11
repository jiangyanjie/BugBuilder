```diff
diff --git a/defects4j/codec_17_fix/src/main/java/org/apache/commons/codec/binary/StringUtils.java b/defects4j/codec_17_buggy/src/main/java/org/apache/commons/codec/binary/StringUtils.java
index 7bb15e33..5b14ca0c 100644
--- a/defects4j/codec_17_fix/src/main/java/org/apache/commons/codec/binary/StringUtils.java
+++ b/defects4j/codec_17_buggy/src/main/java/org/apache/commons/codec/binary/StringUtils.java
@@ -336,7 +336,7 @@ public class StringUtils {
      * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
      */
     public static String newStringIso8859_1(final byte[] bytes) {
-        return newString(bytes, Charsets.ISO_8859_1);
+        return new String(bytes, Charsets.ISO_8859_1);
     }
 
     /**
```
