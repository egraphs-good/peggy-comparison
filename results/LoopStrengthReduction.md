# LoopStrengthReduction
## Original
```java
public class LoopStrengthReduction {
    public static void original() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i * 5);
            i = i + 1;
            d++;
        }
    }
    public static void expected() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i);
            i = i + 5;
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
+ Loading class file LoopStrengthReduction
+ Optimizing class LoopStrengthReduction
   - Processing method <LoopStrengthReduction: void <init>()>
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
      * Optimization of method <LoopStrengthReduction: void <init>()> SUCCESSFUL
      * Optimization took 553
      * PEG2PEGTIME 432
      * PBTIME 302
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReduction: void <init>()>
   - Processing method <LoopStrengthReduction: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 24
GLPKFormulation: Number of values: 24
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void original()> SUCCESSFUL
      * Optimization took 574
      * PEG2PEGTIME 520
      * PBTIME 486
      * ENGINETIME 0
      * Optimization ratio 31490/31490 = 1.0
      * PEG-based Optimization ratio 31490/31490 = 1.0
   - Done processing method <LoopStrengthReduction: void original()>
   - Processing method <LoopStrengthReduction: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 25
GLPKFormulation: Number of values: 25
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void expected()> SUCCESSFUL
      * Optimization took 441
      * PEG2PEGTIME 427
      * PBTIME 403
      * ENGINETIME 1
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <LoopStrengthReduction: void expected()>
+ Done optimizing LoopStrengthReduction
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReduction: void <init>()>
+ Fixing bytecode of method <LoopStrengthReduction: void original()>
+ Fixing bytecode of method <LoopStrengthReduction: void expected()>
+ Writing class back to optimized/LoopStrengthReduction.class
+ Total optimization time = 2024 milliseconds
```

### Optimized
```java
23:08:38.452 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReduction.class
import java.io.PrintStream;

public class LoopStrengthReduction
{
  public static void original()
  {
    int i = 0;
    while (i < 300)
    {
      i += 1;
      System.out.println(i * 5);
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int i = 0;
    while (j < 300)
    {
      System.out.println(i);
      j += 1;
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
+ Loading class file LoopStrengthReduction
+ Optimizing class LoopStrengthReduction
   - Processing method <LoopStrengthReduction: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 3 milliseconds
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
      * Optimization of method <LoopStrengthReduction: void <init>()> SUCCESSFUL
      * Optimization took 549
      * PEG2PEGTIME 428
      * PBTIME 304
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReduction: void <init>()>
   - Processing method <LoopStrengthReduction: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 24
GLPKFormulation: Number of values: 24
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void original()> SUCCESSFUL
      * Optimization took 471
      * PEG2PEGTIME 431
      * PBTIME 385
      * ENGINETIME 0
      * Optimization ratio 31490/31490 = 1.0
      * PEG-based Optimization ratio 31490/31490 = 1.0
   - Done processing method <LoopStrengthReduction: void original()>
   - Processing method <LoopStrengthReduction: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 1 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 25
GLPKFormulation: Number of values: 25
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void expected()> SUCCESSFUL
      * Optimization took 429
      * PEG2PEGTIME 415
      * PBTIME 399
      * ENGINETIME 1
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <LoopStrengthReduction: void expected()>
+ Done optimizing LoopStrengthReduction
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReduction: void <init>()>
+ Fixing bytecode of method <LoopStrengthReduction: void original()>
+ Fixing bytecode of method <LoopStrengthReduction: void expected()>
+ Writing class back to optimized/LoopStrengthReduction.class
+ Total optimization time = 1903 milliseconds
```

### Optimized
```java
23:08:42.987 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReduction.class
import java.io.PrintStream;

public class LoopStrengthReduction
{
  public static void original()
  {
    int i = 0;
    while (i < 300)
    {
      i += 1;
      System.out.println(i * 5);
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int i = 0;
    while (i < 300)
    {
      j += 5;
      i += 1;
      System.out.println(j);
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReduction
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
+ Loading class file LoopStrengthReduction
+ Optimizing class LoopStrengthReduction
   - Processing method <LoopStrengthReduction: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 6 milliseconds
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
      * Optimization of method <LoopStrengthReduction: void <init>()> SUCCESSFUL
      * Optimization took 547
      * PEG2PEGTIME 424
      * PBTIME 298
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReduction: void <init>()>
   - Processing method <LoopStrengthReduction: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 2 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 24
GLPKFormulation: Number of values: 24
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void original()> SUCCESSFUL
      * Optimization took 457
      * PEG2PEGTIME 417
      * PBTIME 384
      * ENGINETIME 2
      * Optimization ratio 31490/31490 = 1.0
      * PEG-based Optimization ratio 31490/31490 = 1.0
   - Done processing method <LoopStrengthReduction: void original()>
   - Processing method <LoopStrengthReduction: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 1 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 25
GLPKFormulation: Number of values: 25
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void expected()> SUCCESSFUL
      * Optimization took 432
      * PEG2PEGTIME 419
      * PBTIME 399
      * ENGINETIME 1
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <LoopStrengthReduction: void expected()>
+ Done optimizing LoopStrengthReduction
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReduction: void <init>()>
+ Fixing bytecode of method <LoopStrengthReduction: void original()>
+ Fixing bytecode of method <LoopStrengthReduction: void expected()>
+ Writing class back to optimized/LoopStrengthReduction.class
+ Total optimization time = 1899 milliseconds
```

### Optimized
```java
23:08:47.566 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReduction.class
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
+ Loading class file LoopStrengthReduction
+ Optimizing class LoopStrengthReduction
   - Processing method <LoopStrengthReduction: void <init>()>
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
      * Optimization of method <LoopStrengthReduction: void <init>()> SUCCESSFUL
      * Optimization took 551
      * PEG2PEGTIME 431
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReduction: void <init>()>
   - Processing method <LoopStrengthReduction: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 13 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 32
GLPKFormulation: Number of values: 28
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void original()> SUCCESSFUL
      * Optimization took 566
      * PEG2PEGTIME 524
      * PBTIME 476
      * ENGINETIME 13
      * Optimization ratio 31490/31490 = 1.0
      * PEG-based Optimization ratio 31490/31490 = 1.0
   - Done processing method <LoopStrengthReduction: void original()>
   - Processing method <LoopStrengthReduction: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 6 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 37
GLPKFormulation: Number of values: 31
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void expected()> SUCCESSFUL
      * Optimization took 576
      * PEG2PEGTIME 560
      * PBTIME 538
      * ENGINETIME 6
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <LoopStrengthReduction: void expected()>
+ Done optimizing LoopStrengthReduction
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReduction: void <init>()>
+ Fixing bytecode of method <LoopStrengthReduction: void original()>
+ Fixing bytecode of method <LoopStrengthReduction: void expected()>
+ Writing class back to optimized/LoopStrengthReduction.class
+ Total optimization time = 2137 milliseconds
```

### Optimized
```java
23:08:52.393 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReduction.class
import java.io.PrintStream;

public class LoopStrengthReduction
{
  public static void original()
  {
    int i = 0;
    while (i < 300)
    {
      i = 1 + i;
      System.out.println(i * 5);
    }
  }
  
  public static void expected()
  {
    int i = 0;
    int j = 0;
    while (300 > i)
    {
      i = 1 + i;
      j += 5;
      System.out.println(j);
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReduction
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
+ Loading class file LoopStrengthReduction
+ Optimizing class LoopStrengthReduction
   - Processing method <LoopStrengthReduction: void <init>()>
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
      * Optimization of method <LoopStrengthReduction: void <init>()> SUCCESSFUL
      * Optimization took 542
      * PEG2PEGTIME 423
      * PBTIME 300
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReduction: void <init>()>
   - Processing method <LoopStrengthReduction: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 39 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 49
GLPKFormulation: Number of values: 36
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void original()> SUCCESSFUL
      * Optimization took 800
      * PEG2PEGTIME 763
      * PBTIME 692
      * ENGINETIME 39
      * Optimization ratio 31490/31490 = 1.0
      * PEG-based Optimization ratio 31490/31490 = 1.0
   - Done processing method <LoopStrengthReduction: void original()>
   - Processing method <LoopStrengthReduction: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 17 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 50
GLPKFormulation: Number of values: 45
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void expected()> SUCCESSFUL
      * Optimization took 728
      * PEG2PEGTIME 714
      * PBTIME 674
      * ENGINETIME 17
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <LoopStrengthReduction: void expected()>
+ Done optimizing LoopStrengthReduction
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReduction: void <init>()>
+ Fixing bytecode of method <LoopStrengthReduction: void original()>
+ Fixing bytecode of method <LoopStrengthReduction: void expected()>
+ Writing class back to optimized/LoopStrengthReduction.class
+ Total optimization time = 2508 milliseconds
```

### Optimized
```java
23:08:57.582 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReduction.class
import java.io.PrintStream;

public class LoopStrengthReduction
{
  public static void original()
  {
    int i = 0;
    while (i < 300)
    {
      i = 1 + i;
      System.out.println(5 * i);
    }
  }
  
  public static void expected()
  {
    int j = 0;
    for (int i = 0; 300 > i; i = 1 + i)
    {
      System.out.println(j);
      j += 5;
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReduction
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
+ Loading class file LoopStrengthReduction
+ Optimizing class LoopStrengthReduction
   - Processing method <LoopStrengthReduction: void <init>()>
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
      * Optimization of method <LoopStrengthReduction: void <init>()> SUCCESSFUL
      * Optimization took 553
      * PEG2PEGTIME 432
      * PBTIME 304
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReduction: void <init>()>
   - Processing method <LoopStrengthReduction: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 71 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 78
GLPKFormulation: Number of values: 61
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void original()> SUCCESSFUL
      * Optimization took 1111
      * PEG2PEGTIME 1068
      * PBTIME 966
      * ENGINETIME 71
      * Optimization ratio 31430/31490 = 0.9980946332168943
      * PEG-based Optimization ratio 31430/31490 = 0.9980946332168943
   - Done processing method <LoopStrengthReduction: void original()>
   - Processing method <LoopStrengthReduction: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 47 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 87
GLPKFormulation: Number of values: 60
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void expected()> SUCCESSFUL
      * Optimization took 1117
      * PEG2PEGTIME 1105
      * PBTIME 1042
      * ENGINETIME 47
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <LoopStrengthReduction: void expected()>
+ Done optimizing LoopStrengthReduction
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReduction: void <init>()>
+ Fixing bytecode of method <LoopStrengthReduction: void original()>
+ Fixing bytecode of method <LoopStrengthReduction: void expected()>
+ Writing class back to optimized/LoopStrengthReduction.class
+ Total optimization time = 3261 milliseconds
```

### Optimized
```java
23:09:03.545 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReduction.class
import java.io.PrintStream;

public class LoopStrengthReduction
{
  public static void original()
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
  
  public static void expected()
  {
    int i = 0;
    for (int j = 0; 300 > j; j = 1 + j)
    {
      i += 5;
      System.out.println(i);
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReduction
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
+ Loading class file LoopStrengthReduction
+ Optimizing class LoopStrengthReduction
   - Processing method <LoopStrengthReduction: void <init>()>
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
      * Optimization of method <LoopStrengthReduction: void <init>()> SUCCESSFUL
      * Optimization took 564
      * PEG2PEGTIME 442
      * PBTIME 303
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReduction: void <init>()>
   - Processing method <LoopStrengthReduction: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 149 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 128
GLPKFormulation: Number of values: 78
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void original()> SUCCESSFUL
      * Optimization took 1701
      * PEG2PEGTIME 1663
      * PBTIME 1480
      * ENGINETIME 149
      * Optimization ratio 31430/31490 = 0.9980946332168943
      * PEG-based Optimization ratio 31430/31490 = 0.9980946332168943
   - Done processing method <LoopStrengthReduction: void original()>
   - Processing method <LoopStrengthReduction: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 72 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 124
GLPKFormulation: Number of values: 94
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void expected()> SUCCESSFUL
      * Optimization took 1712
      * PEG2PEGTIME 1699
      * PBTIME 1611
      * ENGINETIME 72
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <LoopStrengthReduction: void expected()>
+ Done optimizing LoopStrengthReduction
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReduction: void <init>()>
+ Fixing bytecode of method <LoopStrengthReduction: void original()>
+ Fixing bytecode of method <LoopStrengthReduction: void expected()>
+ Writing class back to optimized/LoopStrengthReduction.class
+ Total optimization time = 4425 milliseconds
```

### Optimized
```java
23:09:10.621 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReduction.class
import java.io.PrintStream;

public class LoopStrengthReduction
{
  public static void original()
  {
    int j = 0;
    int i = 0;
    while (300 > i)
    {
      j += 5;
      System.out.println(j);
      i += 1;
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int i = 0;
    while (j < 300)
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
+ Loading class file LoopStrengthReduction
+ Optimizing class LoopStrengthReduction
   - Processing method <LoopStrengthReduction: void <init>()>
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
      * Optimization of method <LoopStrengthReduction: void <init>()> SUCCESSFUL
      * Optimization took 553
      * PEG2PEGTIME 433
      * PBTIME 299
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReduction: void <init>()>
   - Processing method <LoopStrengthReduction: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 264 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 261
GLPKFormulation: Number of values: 161
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void original()> SUCCESSFUL
      * Optimization took 3515
      * PEG2PEGTIME 3472
      * PBTIME 3174
      * ENGINETIME 264
      * Optimization ratio 31430/31490 = 0.9980946332168943
      * PEG-based Optimization ratio 31430/31490 = 0.9980946332168943
   - Done processing method <LoopStrengthReduction: void original()>
   - Processing method <LoopStrengthReduction: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 178 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 208
GLPKFormulation: Number of values: 144
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void expected()> SUCCESSFUL
      * Optimization took 2702
      * PEG2PEGTIME 2686
      * PBTIME 2484
      * ENGINETIME 178
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <LoopStrengthReduction: void expected()>
+ Done optimizing LoopStrengthReduction
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReduction: void <init>()>
+ Fixing bytecode of method <LoopStrengthReduction: void original()>
+ Fixing bytecode of method <LoopStrengthReduction: void expected()>
+ Writing class back to optimized/LoopStrengthReduction.class
+ Total optimization time = 7215 milliseconds
```

### Optimized
```java
23:09:20.558 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReduction.class
import java.io.PrintStream;

public class LoopStrengthReduction
{
  public static void original()
  {
    int j = 0;
    for (int i = 0; 300 > j; i = 5 + i)
    {
      System.out.println(i);
      j = 1 + j;
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int i = 0;
    while (i < 300)
    {
      j += 5;
      i += 1;
      System.out.println(j);
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReduction
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
+ Loading class file LoopStrengthReduction
+ Optimizing class LoopStrengthReduction
   - Processing method <LoopStrengthReduction: void <init>()>
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
      * Optimization of method <LoopStrengthReduction: void <init>()> SUCCESSFUL
      * Optimization took 547
      * PEG2PEGTIME 426
      * PBTIME 303
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReduction: void <init>()>
   - Processing method <LoopStrengthReduction: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 775 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1315
GLPKFormulation: Number of values: 589
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void original()> SUCCESSFUL
      * Optimization took 16937
      * PEG2PEGTIME 16894
      * PBTIME 16085
      * ENGINETIME 776
      * Optimization ratio 31410/31490 = 0.997459510955859
      * PEG-based Optimization ratio 31410/31490 = 0.997459510955859
   - Done processing method <LoopStrengthReduction: void original()>
   - Processing method <LoopStrengthReduction: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 265 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 819
GLPKFormulation: Number of values: 378
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void expected()> SUCCESSFUL
      * Optimization took 10330
      * PEG2PEGTIME 10315
      * PBTIME 10030
      * ENGINETIME 265
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <LoopStrengthReduction: void expected()>
+ Done optimizing LoopStrengthReduction
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReduction: void <init>()>
+ Fixing bytecode of method <LoopStrengthReduction: void original()>
+ Fixing bytecode of method <LoopStrengthReduction: void expected()>
+ Writing class back to optimized/LoopStrengthReduction.class
+ Total optimization time = 28270 milliseconds
```

### Optimized
```java
23:09:51.517 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReduction.class
import java.io.PrintStream;

public class LoopStrengthReduction
{
  public static void original()
  {
    int i = 0;
    while (300 > i)
    {
      int tmp16_15 = (i + i);
      System.out.println(i + (tmp16_15 + tmp16_15));
      i += 1;
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
+ Loading class file LoopStrengthReduction
+ Optimizing class LoopStrengthReduction
   - Processing method <LoopStrengthReduction: void <init>()>
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
      * Optimization of method <LoopStrengthReduction: void <init>()> SUCCESSFUL
      * Optimization took 570
      * PEG2PEGTIME 451
      * PBTIME 318
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReduction: void <init>()>
   - Processing method <LoopStrengthReduction: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1814 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 3260
GLPKFormulation: Number of values: 1249
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void original()> SUCCESSFUL
      * Optimization took 64695
      * PEG2PEGTIME 64599
      * PBTIME 62755
      * ENGINETIME 1814
      * Optimization ratio 31410/31490 = 0.997459510955859
      * PEG-based Optimization ratio 31410/31490 = 0.997459510955859
   - Done processing method <LoopStrengthReduction: void original()>
   - Processing method <LoopStrengthReduction: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 755 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1808
GLPKFormulation: Number of values: 527
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void expected()> SUCCESSFUL
      * Optimization took 23846
      * PEG2PEGTIME 23828
      * PBTIME 23047
      * ENGINETIME 755
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <LoopStrengthReduction: void expected()>
+ Done optimizing LoopStrengthReduction
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReduction: void <init>()>
+ Fixing bytecode of method <LoopStrengthReduction: void original()>
+ Fixing bytecode of method <LoopStrengthReduction: void expected()>
+ Writing class back to optimized/LoopStrengthReduction.class
+ Total optimization time = 89586 milliseconds
```

### Optimized
```java
23:11:23.858 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReduction.class
import java.io.PrintStream;

public class LoopStrengthReduction
{
  public static void original()
  {
    for (int j = 0; 300 > j; j = 1 + j)
    {
      int i = j + j;
      System.out.println(j + i + i);
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int i = 0;
    while (300 > j)
    {
      j = 1 + j;
      i += 5;
      System.out.println(i);
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReduction
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
+ Loading class file LoopStrengthReduction
+ Optimizing class LoopStrengthReduction
   - Processing method <LoopStrengthReduction: void <init>()>
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
      * Optimization of method <LoopStrengthReduction: void <init>()> SUCCESSFUL
      * Optimization took 560
      * PEG2PEGTIME 435
      * PBTIME 303
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopStrengthReduction: void <init>()>
   - Processing method <LoopStrengthReduction: void original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 22958 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 6904
GLPKFormulation: Number of values: 1965
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void original()> SUCCESSFUL
      * Optimization took 156749
      * PEG2PEGTIME 156697
      * PBTIME 133700
      * ENGINETIME 22959
      * Optimization ratio 31410/31490 = 0.997459510955859
      * PEG-based Optimization ratio 31410/31490 = 0.997459510955859
   - Done processing method <LoopStrengthReduction: void original()>
   - Processing method <LoopStrengthReduction: void expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 7660 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 2555
GLPKFormulation: Number of values: 721
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopStrengthReduction: void expected()> SUCCESSFUL
      * Optimization took 80892
      * PEG2PEGTIME 80871
      * PBTIME 73183
      * ENGINETIME 7661
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <LoopStrengthReduction: void expected()>
+ Done optimizing LoopStrengthReduction
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopStrengthReduction: void <init>()>
+ Fixing bytecode of method <LoopStrengthReduction: void original()>
+ Fixing bytecode of method <LoopStrengthReduction: void expected()>
+ Writing class back to optimized/LoopStrengthReduction.class
+ Total optimization time = 238687 milliseconds
```

### Optimized
```java
23:15:25.306 INFO  jd.cli.Main - Decompiling optimized/LoopStrengthReduction.class
import java.io.PrintStream;

public class LoopStrengthReduction
{
  public static void original()
  {
    for (int i = 0; i < 300; i = 1 + i)
    {
      int tmp15_14 = (i + i);
      System.out.println(tmp15_14 + (tmp15_14 + i));
    }
  }
  
  public static void expected()
  {
    int j = 0;
    int i = 0;
    while (300 > j)
    {
      j += 1;
      i = 5 + i;
      System.out.println(i);
    }
  }
}

/* Location:
 * Qualified Name:     LoopStrengthReduction
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
