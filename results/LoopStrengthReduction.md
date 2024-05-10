# First attempt
All had the following params:

```
        run_peggy(
            classname,
            axioms="axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml",
            optimization_level="O2",
            tmp_folder="tmp",
            pb="glpk",
            eto=str(eto_val),
        )
```


ETO val: 10

```java
23:59:49.857 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReduction.class
import java.io.PrintStream;

public class LoopStrengthReduction
{
  public static void original()
  {
    int i = 0;
    while (i < 300)
    {
      System.out.println(i * 5);
      i += 1;
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int i = 0;
    while (j < 300)
    {
      j += 1;
      System.out.println(i);
      i += 5;
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReduction
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
ETO val: 100

```java
23:59:56.768 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReduction.class
import java.io.PrintStream;

public class LoopStrengthReduction
{
  public static void original()
  {
    int j = 0;
    for (int i = 0; 300 > j; i = 5 + i)
    {
      j = 1 + j;
      System.out.println(i);
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int i = 0;
    while (300 > j)
    {
      System.out.println(i);
      j = 1 + j;
      i += 5;
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReduction
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
ETO val: 1000

```java
00:01:08.423 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReduction.class
import java.io.PrintStream;

public class LoopStrengthReduction
{
  public static void original()
  {
    for (int i = 0; 300 > i; i = 1 + i)
    {
      int tmp16_15 = (i + i);
      System.out.println(i + (tmp16_15 + tmp16_15));
    }
  }
  
  public static void expected()
  {
    int i = 0;
    for (int j = 0; j < 300; j = 1 + j)
    {
      System.out.println(i);
      i = 5 + i;
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReduction
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
ETO val: 10000

```java
00:10:58.016 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReduction.class
import java.io.PrintStream;

public class LoopStrengthReduction
{
  public static void original()
  {
    for (int i = 0; 300 > i; i = 1 + i)
    {
      int tmp16_15 = (i + i);
      System.out.println(i + (tmp16_15 + tmp16_15));
    }
  }
  
  public static void expected()
  {
    int i = 0;
    for (int j = 0; j < 300; j = 1 + j)
    {
      System.out.println(i);
      i = 5 + i;
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReduction
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```

NOTE: the iteration bound of 100,000 caused Java to run out of heap space in the engine:

```
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
        at java.io.BufferedWriter.<init>(BufferedWriter.java:104)
        at java.io.BufferedWriter.<init>(BufferedWriter.java:87)
        at java.io.PrintStream.init(PrintStream.java:100)
        at java.io.PrintStream.<init>(PrintStream.java:117)
        at java.io.PrintStream.<init>(PrintStream.java:79)
        at peggy.optimize.java.Main$MyLogger.logException(Main.java:655)
        at peggy.optimize.java.Main.optimizeAll(Main.java:880)
        at peggy.optimize.java.Main.optimizeClass(Main.java:704)
        at peggy.optimize.java.Main.main(Main.java:2881)
```