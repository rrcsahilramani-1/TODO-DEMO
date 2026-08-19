# Room generates implementation classes reflectively referenced by name.
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-dontwarn androidx.room.paging.**
