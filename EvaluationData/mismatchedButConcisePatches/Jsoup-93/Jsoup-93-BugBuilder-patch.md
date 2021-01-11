```diff
diff --git a/defects4j/jsoup93fix/src/main/java/org/jsoup/nodes/FormElement.java b/defects4j/jsoup93buggy/src/main/java/org/jsoup/nodes/FormElement.java
index 5770289..f5c59d3 100644
--- a/defects4j/jsoup93fix/src/main/java/org/jsoup/nodes/FormElement.java
+++ b/defects4j/jsoup93buggy/src/main/java/org/jsoup/nodes/FormElement.java
@@ -86,9 +86,7 @@ public class FormElement extends Element {
             if (name.length() == 0) continue;
             String type = el.attr("type");
 
-            if (type.equalsIgnoreCase("button")) continue; // browsers don't submit these

-            if ("select".equals(el.normalName())) {
+            if ("select".equals(el.tagName())) {
                 Elements options = el.select("option[selected]");
                 boolean set = false;
                 for (Element option: options) {
```
