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
      * Engine reached iteration bound of 2 after 3 milliseconds
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
      * Optimization took 584
      * PEG2PEGTIME 458
      * PBTIME 325
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 1 milliseconds
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
      * Optimization took 587
      * PEG2PEGTIME 519
      * PBTIME 476
      * ENGINETIME 1
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
      * Optimization took 510
      * PEG2PEGTIME 488
      * PBTIME 472
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
+ Total optimization time = 2203 milliseconds
```

```java
23:13:00.572 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int j = 0;
    int k = 0;
    if (j < 300)
    {
      int i = k + 1;
      if (j == 150) {
        i += 3;
      }
      for (;;)
      {
        j += 1;
        k = i;
        System.out.println(k * 500);
        break;
        i = i;
      }
    }
  }
  
  public static void expected()
  {
    int k = 0;
    int j = 0;
    if (k < 300)
    {
      int i = j + 5;
      if (k % 2 == 0) {
        i += 15;
      }
      for (;;)
      {
        k += 1;
        System.out.println(j);
        j = i;
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
      * Engine reached iteration bound of 4 after 5 milliseconds
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
      * Optimization took 548
      * PEG2PEGTIME 429
      * PBTIME 305
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 1 milliseconds
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
      * Optimization took 548
      * PEG2PEGTIME 493
      * PBTIME 458
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
      * Optimization took 497
      * PEG2PEGTIME 475
      * PBTIME 457
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
+ Total optimization time = 2080 milliseconds
```

```java
23:13:05.372 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      int k = i + 1;
      if (j == 150) {
        k += 3;
      }
      for (;;)
      {
        j += 1;
        System.out.println(i * 500);
        i = k;
        break;
        k = k;
      }
    }
  }
  
  public static void expected()
  {
    int k = 0;
    int j = 0;
    if (j < 300)
    {
      int i = k + 5;
      if (j % 2 == 0) {
        i += 15;
      }
      for (;;)
      {
        k = i;
        j += 1;
        System.out.println(k);
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
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 548
      * PEG2PEGTIME 426
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 10 milliseconds
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
      * Optimization took 602
      * PEG2PEGTIME 547
      * PBTIME 502
      * ENGINETIME 10
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 2 milliseconds
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
      * Optimization took 526
      * PEG2PEGTIME 504
      * PBTIME 486
      * ENGINETIME 2
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
+ Total optimization time = 2160 milliseconds
```

```java
23:13:10.238 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      int k = i + 1;
      if (j == 150) {
        k += 3;
      }
      for (;;)
      {
        System.out.println(i * 500);
        j += 1;
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
        System.out.println(k);
        j += 1;
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
      * Optimization took 669
      * PEG2PEGTIME 542
      * PBTIME 415
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 13 milliseconds
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
      * Optimization took 703
      * PEG2PEGTIME 649
      * PBTIME 600
      * ENGINETIME 13
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 7 milliseconds
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
      * Optimization took 643
      * PEG2PEGTIME 618
      * PBTIME 587
      * ENGINETIME 7
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
+ Total optimization time = 2578 milliseconds
```

```java
23:13:15.793 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      int k = 1 + i;
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
    int i = 0;
    if (j < 300)
    {
      int k = i + 5;
      if (j % 2 == 0) {
        k += 15;
      }
      for (;;)
      {
        j = 1 + j;
        System.out.println(i);
        i = k;
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
      * Optimization took 561
      * PEG2PEGTIME 438
      * PBTIME 303
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 26 milliseconds
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
      * Optimization took 1032
      * PEG2PEGTIME 974
      * PBTIME 910
      * ENGINETIME 26
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 13 milliseconds
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
      * Optimization took 819
      * PEG2PEGTIME 797
      * PBTIME 765
      * ENGINETIME 13
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
+ Total optimization time = 2894 milliseconds
```

```java
23:13:21.451 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int k = 0;
    int j = 0;
    if (300 > j)
    {
      int i = k + 1;
      if (j == 150) {
        i += 3;
      }
      for (;;)
      {
        k = i;
        j += 1;
        System.out.println(k * 500);
        break;
        i = i;
      }
    }
  }
  
  public static void expected()
  {
    int i = 0;
    int j = 0;
    if (j < 300)
    {
      int k = 5 + i;
      if (j % 2 == 0) {
        k += 15;
      }
      for (;;)
      {
        i = k;
        j += 1;
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
      * Optimization took 571
      * PEG2PEGTIME 451
      * PBTIME 304
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 73 milliseconds
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
      * Optimization took 1443
      * PEG2PEGTIME 1390
      * PBTIME 1275
      * ENGINETIME 73
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 43 milliseconds
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
      * Optimization took 1322
      * PEG2PEGTIME 1300
      * PBTIME 1241
      * ENGINETIME 43
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
+ Total optimization time = 3851 milliseconds
```

```java
23:13:27.986 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
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
      for (int k = 4 + i;; k = i + 1)
      {
        i = k;
        j = 1 + j;
        System.out.println(500 * i);
        break;
      }
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int k = 0;
    if (300 > j)
    {
      if (j % 2 == 0) {}
      for (int i = 20 + k;; i = 5 + k)
      {
        j += 1;
        System.out.println(k);
        k = i;
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
      * Optimization took 567
      * PEG2PEGTIME 440
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 149 milliseconds
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
      * Optimization took 2359
      * PEG2PEGTIME 2300
      * PBTIME 2112
      * ENGINETIME 149
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 88 milliseconds
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
      * Optimization took 2028
      * PEG2PEGTIME 2003
      * PBTIME 1894
      * ENGINETIME 89
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
+ Total optimization time = 5442 milliseconds
```

```java
23:13:36.131 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      if (j == 150) {}
      for (int k = 4 + i;; k = i + 1)
      {
        System.out.println(500 * i);
        j = 1 + j;
        i = k;
        break;
      }
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      if (j % 2 == 0) {}
      for (int k = 20 + i;; k = i + 5)
      {
        j = 1 + j;
        System.out.println(i);
        i = k;
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
      * Optimization took 582
      * PEG2PEGTIME 456
      * PBTIME 321
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 365 milliseconds
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
      * Optimization took 4322
      * PEG2PEGTIME 4263
      * PBTIME 3857
      * ENGINETIME 365
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 198 milliseconds
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
      * Optimization took 3518
      * PEG2PEGTIME 3499
      * PBTIME 3280
      * ENGINETIME 198
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
+ Total optimization time = 8959 milliseconds
```

```java
23:13:47.974 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int k = 0;
    int j = 0;
    if (300 > j)
    {
      if (j == 150) {}
      for (int i = 4 + k;; i = 1 + k)
      {
        System.out.println(k * 500);
        k = i;
        j = 1 + j;
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
      if (i % 2 == 0) {}
      for (int k = j + 20;; k = 5 + j)
      {
        System.out.println(j);
        j = k;
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
      * Optimization took 560
      * PEG2PEGTIME 437
      * PBTIME 304
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 597 milliseconds
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
      * Optimization took 7928
      * PEG2PEGTIME 7868
      * PBTIME 7231
      * ENGINETIME 598
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 309 milliseconds
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
      * Optimization took 8547
      * PEG2PEGTIME 8506
      * PBTIME 8173
      * ENGINETIME 310
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
+ Total optimization time = 17529 milliseconds
```

```java
23:14:08.230 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int i = 0;
    int k = 0;
    if (k < 300)
    {
      if (k == 150) {}
      for (int j = 4 + i;; j = i + 1)
      {
        i = j;
        System.out.println(i * 500);
        k = 1 + k;
        break;
      }
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      if (j % 2 == 0) {}
      for (int k = 20 + i;; k = i + 5)
      {
        System.out.println(i);
        j += 1;
        i = k;
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
      * Optimization took 552
      * PEG2PEGTIME 431
      * PBTIME 304
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1177 milliseconds
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
      * Optimization took 22935
      * PEG2PEGTIME 22876
      * PBTIME 21664
      * ENGINETIME 1178
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 2313 milliseconds
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
      * Optimization took 34817
      * PEG2PEGTIME 34763
      * PBTIME 32426
      * ENGINETIME 2313
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
+ Total optimization time = 58827 milliseconds
```

```java
23:15:09.787 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int k = 0;
    int j = 0;
    if (k < 300)
    {
      int i = 1 + j;
      if (k == 150) {}
      for (i = 3 + i;; i = i)
      {
        k += 1;
        j = i;
        System.out.println(j * 500);
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
      for (int k = i + 20;; k = i + 5)
      {
        i = k;
        System.out.println(i);
        j += 1;
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
      * Optimization took 626
      * PEG2PEGTIME 492
      * PBTIME 349
      * ENGINETIME 9
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 4931 milliseconds
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
      * Optimization took 62391
      * PEG2PEGTIME 62320
      * PBTIME 57349
      * ENGINETIME 4932
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 8078 milliseconds
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
      * Optimization took 130103
      * PEG2PEGTIME 130058
      * PBTIME 121954
      * ENGINETIME 8080
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
+ Total optimization time = 193693 milliseconds
```

```java
23:18:26.333 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
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
        i = k;
        j = 1 + j;
        System.out.println(500 * i);
        break;
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
      if (j % 2 == 0) {}
      for (k = 15 + k;; k = k)
      {
        System.out.println(i);
        j = 1 + j;
        i = k;
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
