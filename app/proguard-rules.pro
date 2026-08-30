-dontpreverify
-allowaccessmodification

-obfuscationdictionary obf-dict.txt
-classobfuscationdictionary obf-dict.txt
-packageobfuscationdictionary obf-dict.txt

-overloadaggressively
-repackageclasses ''
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute 'obf'

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void check*(...);
    public static void throw*(...);
}
-assumenosideeffects class java.util.Objects {
    ** requireNonNull(...);
}

-keep class io.github.wabtest.hook.HookEntry

-dontwarn java.lang.reflect.AnnotatedType
