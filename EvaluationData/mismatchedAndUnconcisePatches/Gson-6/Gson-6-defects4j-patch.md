```diff
diff --git a/defects4j/gson_6_fix/gson/src/main/java/com/google/gson/internal/bind/JsonAdapterAnnotationTypeAdapterFactory.java b/defects4j/gson_6_buggy/gson/src/main/java/com/google/gson/internal/bind/JsonAdapterAnnotationTypeAdapterFactory.java
index b52e1573..30c4f6e9 100644
--- a/defects4j/gson_6_fix/gson/src/main/java/com/google/gson/internal/bind/JsonAdapterAnnotationTypeAdapterFactory.java
+++ b/defects4j/gson_6_buggy/gson/src/main/java/com/google/gson/internal/bind/JsonAdapterAnnotationTypeAdapterFactory.java
@@ -64,9 +64,7 @@ public final class JsonAdapterAnnotationTypeAdapterFactory implements TypeAdapte
       throw new IllegalArgumentException(
           "@JsonAdapter value must be TypeAdapter or TypeAdapterFactory reference.");
     }
-    if (typeAdapter != null) {
       typeAdapter = typeAdapter.nullSafe();
-    }
     return typeAdapter;
   }
 }

```
