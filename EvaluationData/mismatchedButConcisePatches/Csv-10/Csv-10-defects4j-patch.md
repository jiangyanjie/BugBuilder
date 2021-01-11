```diff
diff --git a/defects4j/csv_10_fix/src/main/java/org/apache/commons/csv/CSVPrinter.java b/defects4j/csv_10_buggy/src/main/java/org/apache/commons/csv/CSVPrinter.java
index d2968b5..94e5852 100644
--- a/defects4j/csv_10_fix/src/main/java/org/apache/commons/csv/CSVPrinter.java
+++ b/defects4j/csv_10_buggy/src/main/java/org/apache/commons/csv/CSVPrinter.java
@@ -67,9 +67,6 @@ public final class CSVPrinter implements Flushable, Closeable {
         this.format.validate();
         // TODO: Is it a good idea to do this here instead of on the first call to a print method?
         // It seems a pain to have to track whether the header has already been printed or not.
-        if (format.getHeader() != null) {
-            this.printRecord((Object[]) format.getHeader());
-        }
     }
 
     // ======================================================
```
