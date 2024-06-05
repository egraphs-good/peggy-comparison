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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 2

### Peggy output
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
      * Engine reached iteration bound of 2 after 4 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 667
      * PEG2PEGTIME 543
      * PBTIME 420
      * ENGINETIME 4
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
GLPKFormulation: Number of nodes: 31
GLPKFormulation: Number of values: 31
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 552
      * PEG2PEGTIME 496
      * PBTIME 461
      * ENGINETIME 1
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 1 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 31
GLPKFormulation: Number of values: 31
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 495
      * PEG2PEGTIME 473
      * PBTIME 456
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
+ Total optimization time = 2222 milliseconds
```

### Optimized
```java
23:00:03.065 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
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
        j = i;
        System.out.println(j * 500);
        k += 1;
        break;
        i = i;
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 4

### Peggy output
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
      * Engine reached iteration bound of 4 after 4 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 565
      * PEG2PEGTIME 445
      * PBTIME 303
      * ENGINETIME 4
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
GLPKFormulation: Number of nodes: 31
GLPKFormulation: Number of values: 31
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 581
      * PEG2PEGTIME 526
      * PBTIME 489
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
GLPKFormulation: Number of nodes: 31
GLPKFormulation: Number of values: 31
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 494
      * PEG2PEGTIME 473
      * PBTIME 456
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
+ Total optimization time = 2110 milliseconds
```

### Optimized
```java
23:00:07.990 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
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
        System.out.println(j * 500);
        i += 1;
        break;
        k = k;
      }
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int k = 0;
    if (k < 300)
    {
      int i = j + 5;
      if (k % 2 == 0) {
        i += 15;
      }
      for (;;)
      {
        j = i;
        k += 1;
        System.out.println(j);
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 8

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 544
      * PEG2PEGTIME 423
      * PBTIME 299
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
GLPKFormulation: Number of nodes: 35
GLPKFormulation: Number of values: 33
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 596
      * PEG2PEGTIME 541
      * PBTIME 499
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
GLPKFormulation: Number of nodes: 32
GLPKFormulation: Number of values: 31
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 520
      * PEG2PEGTIME 499
      * PBTIME 480
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
+ Total optimization time = 2128 milliseconds
```

### Optimized
```java
23:00:12.906 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 16

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 549
      * PEG2PEGTIME 429
      * PBTIME 299
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 17 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 44
GLPKFormulation: Number of values: 37
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 731
      * PEG2PEGTIME 678
      * PBTIME 620
      * ENGINETIME 17
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 6 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 36
GLPKFormulation: Number of values: 32
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 557
      * PEG2PEGTIME 535
      * PBTIME 510
      * ENGINETIME 6
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
+ Total optimization time = 2299 milliseconds
```

### Optimized
```java
23:00:17.885 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
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
      if (j == 150) {}
      for (k = 3 + k;; k = k)
      {
        System.out.println(i * 500);
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
      int k = i + 5;
      if (j % 2 == 0) {
        k += 15;
      }
      for (;;)
      {
        j = 1 + j;
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 32

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 550
      * PEG2PEGTIME 431
      * PBTIME 298
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 36 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 58
GLPKFormulation: Number of values: 45
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 896
      * PEG2PEGTIME 842
      * PBTIME 770
      * ENGINETIME 36
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 12 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 58
GLPKFormulation: Number of values: 46
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 824
      * PEG2PEGTIME 800
      * PBTIME 772
      * ENGINETIME 12
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
+ Total optimization time = 2744 milliseconds
```

### Optimized
```java
23:00:23.259 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int k = 0;
    int j = 0;
    if (300 > k)
    {
      int i = j + 1;
      if (k == 150) {
        i += 3;
      }
      for (;;)
      {
        k += 1;
        System.out.println(j * 500);
        j = i;
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
      int i = 5 + j;
      if (k % 2 == 0) {
        i += 15;
      }
      for (;;)
      {
        k += 1;
        j = i;
        System.out.println(j);
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 64

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 542
      * PEG2PEGTIME 422
      * PBTIME 299
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 76 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 97
GLPKFormulation: Number of values: 72
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 1365
      * PEG2PEGTIME 1313
      * PBTIME 1204
      * ENGINETIME 76
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 42 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 95
GLPKFormulation: Number of values: 74
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 1343
      * PEG2PEGTIME 1319
      * PBTIME 1260
      * ENGINETIME 42
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
+ Total optimization time = 3721 milliseconds
```

### Optimized
```java
23:00:29.646 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
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
        j += 1;
        i = k;
        System.out.println(i * 500);
        break;
        k = k;
      }
    }
  }
  
  public static void expected()
  {
    int i = 0;
    int j = 0;
    if (300 > j)
    {
      if (j % 2 == 0) {}
      for (int k = i + 20;; k = i + 5)
      {
        i = k;
        System.out.println(i);
        j = 1 + j;
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 128

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 557
      * PEG2PEGTIME 434
      * PBTIME 299
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 152 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 148
GLPKFormulation: Number of values: 108
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 2139
      * PEG2PEGTIME 2081
      * PBTIME 1896
      * ENGINETIME 152
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 70 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 149
GLPKFormulation: Number of values: 108
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 2003
      * PEG2PEGTIME 1979
      * PBTIME 1889
      * ENGINETIME 70
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
+ Total optimization time = 5171 milliseconds
```

### Optimized
```java
23:00:37.480 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
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
        k = 1 + k;
        i = j;
        System.out.println(500 * i);
        break;
      }
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int k = 0;
    if (j < 300)
    {
      if (j % 2 == 0) {}
      for (int i = 20 + k;; i = k + 5)
      {
        System.out.println(k);
        j += 1;
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 256

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 549
      * PEG2PEGTIME 428
      * PBTIME 298
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 318 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 281
GLPKFormulation: Number of values: 180
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 3971
      * PEG2PEGTIME 3918
      * PBTIME 3561
      * ENGINETIME 318
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 172 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 263
GLPKFormulation: Number of values: 183
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 3404
      * PEG2PEGTIME 3383
      * PBTIME 3188
      * ENGINETIME 173
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
+ Total optimization time = 8387 milliseconds
```

### Optimized
```java
23:00:48.540 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
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
    int k = 0;
    if (300 > j)
    {
      if (j % 2 == 0) {}
      for (int i = k + 20;; i = k + 5)
      {
        System.out.println(k);
        j += 1;
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 512

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 557
      * PEG2PEGTIME 433
      * PBTIME 302
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 602 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 581
GLPKFormulation: Number of values: 366
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 7847
      * PEG2PEGTIME 7789
      * PBTIME 7150
      * ENGINETIME 602
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 304 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 668
GLPKFormulation: Number of values: 410
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 8434
      * PEG2PEGTIME 8412
      * PBTIME 8086
      * ENGINETIME 305
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
+ Total optimization time = 17316 milliseconds
```

### Optimized
```java
23:01:08.500 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int j = 0;
    int i = 0;
    if (i < 300)
    {
      if (i == 150) {}
      for (int k = 4 + j;; k = j + 1)
      {
        System.out.println(j * 500);
        j = k;
        i = 1 + i;
        break;
      }
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int i = 0;
    if (300 > j)
    {
      if (j % 2 == 0) {}
      for (int k = 20 + i;; k = i + 5)
      {
        j += 1;
        i = k;
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 1024

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void <init>()> SUCCESSFUL
      * Optimization took 556
      * PEG2PEGTIME 435
      * PBTIME 303
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1183 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1641
GLPKFormulation: Number of values: 1093
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 21755
      * PEG2PEGTIME 21696
      * PBTIME 20472
      * ENGINETIME 1184
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1140 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 2522
GLPKFormulation: Number of values: 1411
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 32603
      * PEG2PEGTIME 32576
      * PBTIME 31415
      * ENGINETIME 1140
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
+ Total optimization time = 55413 milliseconds
```

### Optimized
```java
23:02:06.634 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int j = 0;
    int i = 0;
    if (300 > j)
    {
      int k = 1 + i;
      if (j == 150) {}
      for (k = 3 + k;; k = k)
      {
        System.out.println(i * 500);
        j += 1;
        i = k;
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
      int k = 5 + i;
      if (j % 2 == 0) {}
      for (k = 15 + k;; k = k)
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 2048

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
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
      * PBTIME 303
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReductionModified: void <init>()>
   - Processing method <LoopStrengthReductionModified: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 5916 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 4026
GLPKFormulation: Number of values: 2825
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void original()> SUCCESSFUL
      * Optimization took 58987
      * PEG2PEGTIME 58926
      * PBTIME 52977
      * ENGINETIME 5917
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <LoopStrengthReductionModified: void original()>
   - Processing method <LoopStrengthReductionModified: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 13050 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 7354
GLPKFormulation: Number of values: 3808
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReductionModified: void expected()> SUCCESSFUL
      * Optimization took 115199
      * PEG2PEGTIME 115155
      * PBTIME 102080
      * ENGINETIME 13052
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
+ Total optimization time = 175271 milliseconds
```

### Optimized
```java
23:05:04.722 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReductionModified.class
import java.io.PrintStream;

public class LoopStrengthReductionModified
{
  public static void original()
  {
    int i = 0;
    int j = 0;
    if (300 > j)
    {
      if (j == 150) {}
      for (int k = 4 + i;; k = i + 1)
      {
        System.out.println(i * 500);
        i = k;
        j += 1;
        break;
      }
    }
  }
  
  public static void expected()
  {
    int i = 0;
    int k = 0;
    if (i < 300)
    {
      int j = 5 + k;
      if (i % 2 == 0) {}
      for (j = 15 + j;; j = j)
      {
        i = 1 + i;
        k = j;
        System.out.println(k);
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
