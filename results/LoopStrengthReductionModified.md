# LoopStrengthReductionModified
## Original
```java
public class LoopStrengthReductionModified {
    public static void original() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i * 500);
            i = i + 1;
            if (d == 150) {
                i = i + 3;
            }
            d++;
        }
    }
    public static void expected() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i);
            i = i + 5;
            if (d % 2 == 0) {
                i = i + 15;
            }
            d++;
        }    
    }
}

```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 2

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file LoopStrengthReductionModified
+ Optimizing class LoopStrengthReductionModified
   - Processing method <LoopStrengthReductionModified: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 2 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 434
      * PEG2PEGTIME 356
      * PBTIME 272
      * ENGINETIME 2
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 503
      * PEG2PEGTIME 452
      * PBTIME 424
      * ENGINETIME 0
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 389
      * PEG2PEGTIME 375
      * PBTIME 363
      * ENGINETIME 1
      * Optimization ratio 31920/31920 = 1.0
      * PEG-based Optimization ratio 31920/31920 = 1.0
   - Done processing method <LoopStrengthReductionModified: void expected()>
+ Done optimizing LoopStrengthReductionModified
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReductionModified: void <init>()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void original()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void expected()>
+ Writing class back to optimized/LoopStrengthReductionModified.class
+ Total optimization time = 1644 milliseconds
```

```java
20:32:54.652 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int j = 0;
    int i = 0;
    if (i < 300)
    {
      int k = j + 1;
      if (i == 150) {
        k += 3;
      }
      for (;;)
      {
        j = k;
        i += 1;
        System.out.println(j * 500);
        break;
        k = k;
      }
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      int k = i + 5;
      if (j % 2 == 0) {
        k += 15;
      }
      for (;;)
      {
        j += 1;
        i = k;
        System.out.println(i);
        break;
        k = k;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReductionModified
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 4

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file LoopStrengthReductionModified
+ Optimizing class LoopStrengthReductionModified
   - Processing method <LoopStrengthReductionModified: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 3 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 492
      * PEG2PEGTIME 403
      * PBTIME 272
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 449
      * PEG2PEGTIME 415
      * PBTIME 386
      * ENGINETIME 1
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 419
      * PEG2PEGTIME 405
      * PBTIME 393
      * ENGINETIME 0
      * Optimization ratio 31920/31920 = 1.0
      * PEG-based Optimization ratio 31920/31920 = 1.0
   - Done processing method <LoopStrengthReductionModified: void expected()>
+ Done optimizing LoopStrengthReductionModified
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReductionModified: void <init>()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void original()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void expected()>
+ Writing class back to optimized/LoopStrengthReductionModified.class
+ Total optimization time = 1689 milliseconds
```

```java
20:32:58.194 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int j = 0;
    int i = 0;
    if (i < 300)
    {
      int k = j + 1;
      if (i == 150) {
        k += 3;
      }
      for (;;)
      {
        j = k;
        i += 1;
        System.out.println(j * 500);
        break;
        k = k;
      }
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      int k = i + 5;
      if (j % 2 == 0) {
        k += 15;
      }
      for (;;)
      {
        j += 1;
        i = k;
        System.out.println(i);
        break;
        k = k;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReductionModified
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 8

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file LoopStrengthReductionModified
+ Optimizing class LoopStrengthReductionModified
   - Processing method <LoopStrengthReductionModified: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 3 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 439
      * PEG2PEGTIME 355
      * PBTIME 272
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 7 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 500
      * PEG2PEGTIME 462
      * PBTIME 427
      * ENGINETIME 7
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 4 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 471
      * PEG2PEGTIME 452
      * PBTIME 435
      * ENGINETIME 4
      * Optimization ratio 31920/31920 = 1.0
      * PEG-based Optimization ratio 31920/31920 = 1.0
   - Done processing method <LoopStrengthReductionModified: void expected()>
+ Done optimizing LoopStrengthReductionModified
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReductionModified: void <init>()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void original()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void expected()>
+ Writing class back to optimized/LoopStrengthReductionModified.class
+ Total optimization time = 1762 milliseconds
```

```java
20:33:01.806 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int i = 0;
    int j = 0;
    if (j < 300)
    {
      int k = 1 + i;
      if (j == 150) {
        k += 3;
      }
      for (;;)
      {
        System.out.println(i * 500);
        i = k;
        j += 1;
        break;
        k = k;
      }
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int k = 0;
    if (300 > j)
    {
      int i = k + 5;
      if (j % 2 == 0) {
        i += 15;
      }
      for (;;)
      {
        j = 1 + j;
        System.out.println(k);
        k = i;
        break;
        i = i;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReductionModified
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 16

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file LoopStrengthReductionModified
+ Optimizing class LoopStrengthReductionModified
   - Processing method <LoopStrengthReductionModified: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 446
      * PEG2PEGTIME 364
      * PBTIME 281
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 10 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 570
      * PEG2PEGTIME 529
      * PBTIME 487
      * ENGINETIME 10
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 4 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 469
      * PEG2PEGTIME 454
      * PBTIME 431
      * ENGINETIME 4
      * Optimization ratio 31920/31920 = 1.0
      * PEG-based Optimization ratio 31920/31920 = 1.0
   - Done processing method <LoopStrengthReductionModified: void expected()>
+ Done optimizing LoopStrengthReductionModified
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReductionModified: void <init>()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void original()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void expected()>
+ Writing class back to optimized/LoopStrengthReductionModified.class
+ Total optimization time = 1811 milliseconds
```

```java
20:33:05.422 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int j = 0;
    int i = 0;
    if (300 > j)
    {
      int k = i + 1;
      if (j == 150) {
        k += 3;
      }
      for (;;)
      {
        j = 1 + j;
        System.out.println(i * 500);
        i = k;
        break;
        k = k;
      }
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int k = 0;
    if (j < 300)
    {
      int i = k + 5;
      if (j % 2 == 0) {
        i += 15;
      }
      for (;;)
      {
        j = 1 + j;
        System.out.println(k);
        k = i;
        break;
        i = i;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReductionModified
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 32

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file LoopStrengthReductionModified
+ Optimizing class LoopStrengthReductionModified
   - Processing method <LoopStrengthReductionModified: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 436
      * PEG2PEGTIME 357
      * PBTIME 281
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 16 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 681
      * PEG2PEGTIME 636
      * PBTIME 598
      * ENGINETIME 16
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 8 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 645
      * PEG2PEGTIME 624
      * PBTIME 582
      * ENGINETIME 8
      * Optimization ratio 31920/31920 = 1.0
      * PEG-based Optimization ratio 31920/31920 = 1.0
   - Done processing method <LoopStrengthReductionModified: void expected()>
+ Done optimizing LoopStrengthReductionModified
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReductionModified: void <init>()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void original()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void expected()>
+ Writing class back to optimized/LoopStrengthReductionModified.class
+ Total optimization time = 2073 milliseconds
```

```java
20:33:09.245 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int k = 0;
    int j = 0;
    if (300 > j)
    {
      int i = 1 + k;
      if (j == 150) {
        i += 3;
      }
      for (;;)
      {
        k = i;
        j += 1;
        System.out.println(500 * k);
        break;
        i = i;
      }
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int i = 0;
    if (300 > i)
    {
      int k = 5 + j;
      if (i % 2 == 0) {
        k += 15;
      }
      for (;;)
      {
        j = k;
        i = 1 + i;
        System.out.println(j);
        break;
        k = k;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReductionModified
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 64

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file LoopStrengthReductionModified
+ Optimizing class LoopStrengthReductionModified
   - Processing method <LoopStrengthReductionModified: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 427
      * PEG2PEGTIME 350
      * PBTIME 269
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 45 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 1005
      * PEG2PEGTIME 970
      * PBTIME 901
      * ENGINETIME 45
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 27 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 957
      * PEG2PEGTIME 943
      * PBTIME 906
      * ENGINETIME 27
      * Optimization ratio 31920/31920 = 1.0
      * PEG-based Optimization ratio 31920/31920 = 1.0
   - Done processing method <LoopStrengthReductionModified: void expected()>
+ Done optimizing LoopStrengthReductionModified
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReductionModified: void <init>()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void original()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void expected()>
+ Writing class back to optimized/LoopStrengthReductionModified.class
+ Total optimization time = 2747 milliseconds
```

```java
20:33:13.847 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int k = 0;
    int i = 0;
    if (300 > k)
    {
      if (k == 150) {}
      for (int j = i + 4;; j = i + 1)
      {
        k += 1;
        System.out.println(i * 500);
        i = j;
        break;
      }
    }
  }
  
  public static void expected()
  {
    int k = 0;
    int i = 0;
    if (k < 300)
    {
      if (k % 2 == 0) {}
      for (int j = i + 20;; j = 5 + i)
      {
        System.out.println(i);
        k += 1;
        i = j;
        break;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReductionModified
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 128

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file LoopStrengthReductionModified
+ Optimizing class LoopStrengthReductionModified
   - Processing method <LoopStrengthReductionModified: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 428
      * PEG2PEGTIME 349
      * PBTIME 272
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 109 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 1554
      * PEG2PEGTIME 1518
      * PBTIME 1386
      * ENGINETIME 109
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 44 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 1340
      * PEG2PEGTIME 1323
      * PBTIME 1266
      * ENGINETIME 44
      * Optimization ratio 31920/31920 = 1.0
      * PEG-based Optimization ratio 31920/31920 = 1.0
   - Done processing method <LoopStrengthReductionModified: void expected()>
+ Done optimizing LoopStrengthReductionModified
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReductionModified: void <init>()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void original()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void expected()>
+ Writing class back to optimized/LoopStrengthReductionModified.class
+ Total optimization time = 3635 milliseconds
```

```java
20:33:19.319 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int j = 0;
    int k = 0;
    if (k < 300)
    {
      int i = j + 1;
      if (k == 150) {
        i += 3;
      }
      for (;;)
      {
        System.out.println(j * 500);
        j = i;
        k += 1;
        break;
        i = i;
      }
    }
  }
  
  public static void expected()
  {
    int k = 0;
    int i = 0;
    if (300 > i)
    {
      int j = 5 + k;
      if (i % 2 == 0) {}
      for (j = 15 + j;; j = j)
      {
        k = j;
        System.out.println(k);
        i += 1;
        break;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReductionModified
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 256

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file LoopStrengthReductionModified
+ Optimizing class LoopStrengthReductionModified
   - Processing method <LoopStrengthReductionModified: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 445
      * PEG2PEGTIME 368
      * PBTIME 284
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 244 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 2701
      * PEG2PEGTIME 2665
      * PBTIME 2398
      * ENGINETIME 244
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 148 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 2773
      * PEG2PEGTIME 2744
      * PBTIME 2576
      * ENGINETIME 149
      * Optimization ratio 31920/31920 = 1.0
      * PEG-based Optimization ratio 31920/31920 = 1.0
   - Done processing method <LoopStrengthReductionModified: void expected()>
+ Done optimizing LoopStrengthReductionModified
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReductionModified: void <init>()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void original()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void expected()>
+ Writing class back to optimized/LoopStrengthReductionModified.class
+ Total optimization time = 6287 milliseconds
```

```java
20:33:27.583 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int i = 0;
    int j = 0;
    if (j < 300)
    {
      if (j == 150) {}
      for (int k = 4 + i;; k = 1 + i)
      {
        System.out.println(500 * i);
        i = k;
        j += 1;
        break;
      }
    }
  }
  
  public static void expected()
  {
    int i = 0;
    int j = 0;
    if (j < 300)
    {
      if (j % 2 == 0) {}
      for (int k = i + 20;; k = 5 + i)
      {
        i = k;
        j = 1 + j;
        System.out.println(i);
        break;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReductionModified
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 512

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file LoopStrengthReductionModified
+ Optimizing class LoopStrengthReductionModified
   - Processing method <LoopStrengthReductionModified: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 539
      * PEG2PEGTIME 455
      * PBTIME 374
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 507 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 8032
      * PEG2PEGTIME 7983
      * PBTIME 7443
      * ENGINETIME 508
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 221 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 4844
      * PEG2PEGTIME 4826
      * PBTIME 4583
      * ENGINETIME 222
      * Optimization ratio 31920/31920 = 1.0
      * PEG-based Optimization ratio 31920/31920 = 1.0
   - Done processing method <LoopStrengthReductionModified: void expected()>
+ Done optimizing LoopStrengthReductionModified
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReductionModified: void <init>()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void original()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void expected()>
+ Writing class back to optimized/LoopStrengthReductionModified.class
+ Total optimization time = 13763 milliseconds
```

```java
20:33:43.343 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int k = 0;
    int i = 0;
    if (k < 300)
    {
      if (k == 150) {}
      for (int j = 4 + i;; j = 1 + i)
      {
        k += 1;
        i = j;
        System.out.println(i * 500);
        break;
      }
    }
  }
  
  public static void expected()
  {
    int i = 0;
    int j = 0;
    if (j < 300)
    {
      if (j % 2 == 0) {}
      for (int k = 20 + i;; k = 5 + i)
      {
        i = k;
        j += 1;
        System.out.println(i);
        break;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReductionModified
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 1024

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file LoopStrengthReductionModified
+ Optimizing class LoopStrengthReductionModified
   - Processing method <LoopStrengthReductionModified: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 460
      * PEG2PEGTIME 379
      * PBTIME 269
      * ENGINETIME 9
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 845 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 18034
      * PEG2PEGTIME 17979
      * PBTIME 17111
      * ENGINETIME 845
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1804 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 23646
      * PEG2PEGTIME 23624
      * PBTIME 21796
      * ENGINETIME 1804
      * Optimization ratio 31920/31920 = 1.0
      * PEG-based Optimization ratio 31920/31920 = 1.0
   - Done processing method <LoopStrengthReductionModified: void expected()>
+ Done optimizing LoopStrengthReductionModified
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReductionModified: void <init>()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void original()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void expected()>
+ Writing class back to optimized/LoopStrengthReductionModified.class
+ Total optimization time = 42515 milliseconds
```

```java
20:34:28.278 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int i = 0;
    int j = 0;
    if (i < 300)
    {
      if (i == 150) {}
      for (int k = 4 + j;; k = j + 1)
      {
        i = 1 + i;
        System.out.println(j * 500);
        j = k;
        break;
      }
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int i = 0;
    if (300 > i)
    {
      int k = j + 5;
      if (i % 2 == 0) {}
      for (k = 15 + k;; k = k)
      {
        j = k;
        i = 1 + i;
        System.out.println(j);
        break;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReductionModified
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 2048

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file LoopStrengthReductionModified
+ Optimizing class LoopStrengthReductionModified
   - Processing method <LoopStrengthReductionModified: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 460
      * PEG2PEGTIME 372
      * PBTIME 278
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 3455 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 37952
      * PEG2PEGTIME 37910
      * PBTIME 34425
      * ENGINETIME 3456
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 5069 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 2341053
      * PEG2PEGTIME 2341027
      * PBTIME 2335944
      * ENGINETIME 5071
      * Optimization ratio 31920/31920 = 1.0
      * PEG-based Optimization ratio 31920/31920 = 1.0
   - Done processing method <LoopStrengthReductionModified: void expected()>
+ Done optimizing LoopStrengthReductionModified
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReductionModified: void <init>()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void original()>
+ Fixing bytecode of method <LoopStrengthReductionModified: void expected()>
+ Writing class back to optimized/LoopStrengthReductionModified.class
+ Total optimization time = 2379891 milliseconds
```

```java
21:14:10.791 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int j = 0;
    int k = 0;
    if (300 > k)
    {
      int i = 1 + j;
      if (k == 150) {
        i += 3;
      }
      for (;;)
      {
        j = i;
        System.out.println(500 * j);
        k += 1;
        break;
        i = i;
      }
    }
  }
  
  public static void expected()
  {
    int i = 0;
    int k = 0;
    if (k < 300)
    {
      int j = i + 5;
      if (k % 2 == 0) {}
      for (j = 15 + j;; j = j)
      {
        System.out.println(i);
        i = j;
        k += 1;
        break;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReductionModified
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
