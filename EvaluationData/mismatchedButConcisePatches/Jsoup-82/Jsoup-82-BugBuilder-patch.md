```diff
diff --git a/defects4j/jsoup82fix/src/main/java/org/jsoup/helper/DataUtil.java b/defects4j/jsoup82buggy/src/main/java/org/jsoup/helper/DataUtil.java
index f4012fc..0e8e819 100644
--- a/defects4j/jsoup82fix/src/main/java/org/jsoup/helper/DataUtil.java
+++ b/defects4j/jsoup82buggy/src/main/java/org/jsoup/helper/DataUtil.java
@@ -166,12 +166,7 @@ public final class DataUtil {
                 // io exception when parsing (not seen before because reading the stream as we go)
                 throw e.ioException();
             }
-            Charset charset = Charset.forName(charsetName);
-            doc.outputSettings().charset(charset);
-            if (!charset.canEncode()) {
-                // some charsets can read but not encode; switch to an encodable charset and update the meta el
-                doc.charset(Charset.forName(defaultCharset));
-            }
+            doc.outputSettings().charset(charsetName);
         }
         input.close();
         return doc;
```
