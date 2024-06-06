# LoopInvariantCodeMotion
## Original
```java
public class LoopInvariantCodeMotion {
    public void original(int n, int m) {
        for (int i = 0; i < 20; i++) {
            int j = n * 20;
            if (j < m) {
                j++;
            }
            System.out.println(i * j);
        }
    }

    public void expected(int n, int m) {
        int j = n * 20;
        if (j < m) {
            j++;
        }
        for (int i = 0; i < 20; i++) {
            System.out.println(i * j);
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
+ Loading class file LoopInvariantCodeMotion
+ Optimizing class LoopInvariantCodeMotion
   - Processing method <LoopInvariantCodeMotion: void <init>()>
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
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 558
      * PEG2PEGTIME 431
      * PBTIME 303
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 29
GLPKFormulation: Number of values: 29
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 545
      * PEG2PEGTIME 487
      * PBTIME 452
      * ENGINETIME 0
      * Optimization ratio 31519/31519 = 1.0
      * PEG-based Optimization ratio 31519/31519 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 29
GLPKFormulation: Number of values: 29
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 476
      * PEG2PEGTIME 456
      * PBTIME 439
      * ENGINETIME 0
      * Optimization ratio 31519/31519 = 1.0
      * PEG-based Optimization ratio 31519/31519 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void expected(int,int)>
+ Done optimizing LoopInvariantCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopInvariantCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void original(int,int)>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void expected(int,int)>
+ Writing class back to optimized/LoopInvariantCodeMotion.class
+ Total optimization time = 2081 milliseconds
```

### Optimized
```java
23:05:10.767 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {
      paramInt1 += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      while (paramInt2 < 20)
      {
        System.out.println(paramInt2 * paramInt1);
        paramInt2 += 1;
      }
      return;
      paramInt1 = paramInt1;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {
      paramInt1 += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      while (paramInt2 < 20)
      {
        System.out.println(paramInt2 * paramInt1);
        paramInt2 += 1;
      }
      return;
      paramInt1 = paramInt1;
    }
  }
}

/* Location:
 * Qualified Name:     LoopInvariantCodeMotion
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
+ Loading class file LoopInvariantCodeMotion
+ Optimizing class LoopInvariantCodeMotion
   - Processing method <LoopInvariantCodeMotion: void <init>()>
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
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 560
      * PEG2PEGTIME 435
      * PBTIME 306
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 2 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 29
GLPKFormulation: Number of values: 29
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 639
      * PEG2PEGTIME 584
      * PBTIME 545
      * ENGINETIME 2
      * Optimization ratio 31519/31519 = 1.0
      * PEG-based Optimization ratio 31519/31519 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 29
GLPKFormulation: Number of values: 29
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 491
      * PEG2PEGTIME 471
      * PBTIME 453
      * ENGINETIME 0
      * Optimization ratio 31519/31519 = 1.0
      * PEG-based Optimization ratio 31519/31519 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void expected(int,int)>
+ Done optimizing LoopInvariantCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopInvariantCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void original(int,int)>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void expected(int,int)>
+ Writing class back to optimized/LoopInvariantCodeMotion.class
+ Total optimization time = 2157 milliseconds
```

### Optimized
```java
23:05:15.621 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      paramInt1 = 0;
      while (paramInt1 < 20)
      {
        paramInt1 += 1;
        System.out.println(paramInt1 * paramInt2);
      }
      return;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      paramInt1 = 0;
      while (paramInt1 < 20)
      {
        paramInt1 += 1;
        System.out.println(paramInt1 * paramInt2);
      }
      return;
    }
  }
}

/* Location:
 * Qualified Name:     LoopInvariantCodeMotion
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
+ Loading class file LoopInvariantCodeMotion
+ Optimizing class LoopInvariantCodeMotion
   - Processing method <LoopInvariantCodeMotion: void <init>()>
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
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 549
      * PEG2PEGTIME 427
      * PBTIME 303
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 2 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 29
GLPKFormulation: Number of values: 29
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 545
      * PEG2PEGTIME 488
      * PBTIME 451
      * ENGINETIME 2
      * Optimization ratio 31519/31519 = 1.0
      * PEG-based Optimization ratio 31519/31519 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 2 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 29
GLPKFormulation: Number of values: 29
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 490
      * PEG2PEGTIME 471
      * PBTIME 451
      * ENGINETIME 2
      * Optimization ratio 31519/31519 = 1.0
      * PEG-based Optimization ratio 31519/31519 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void expected(int,int)>
+ Done optimizing LoopInvariantCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopInvariantCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void original(int,int)>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void expected(int,int)>
+ Writing class back to optimized/LoopInvariantCodeMotion.class
+ Total optimization time = 2050 milliseconds
```

### Optimized
```java
23:05:20.325 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {
      paramInt1 += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      while (paramInt2 < 20)
      {
        paramInt2 += 1;
        System.out.println(paramInt2 * paramInt1);
      }
      return;
      paramInt1 = paramInt1;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {
      paramInt1 += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      while (paramInt2 < 20)
      {
        paramInt2 += 1;
        System.out.println(paramInt2 * paramInt1);
      }
      return;
      paramInt1 = paramInt1;
    }
  }
}

/* Location:
 * Qualified Name:     LoopInvariantCodeMotion
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
+ Loading class file LoopInvariantCodeMotion
+ Optimizing class LoopInvariantCodeMotion
   - Processing method <LoopInvariantCodeMotion: void <init>()>
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
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 552
      * PEG2PEGTIME 429
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 15 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 39
GLPKFormulation: Number of values: 33
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 671
      * PEG2PEGTIME 619
      * PBTIME 560
      * ENGINETIME 15
      * Optimization ratio 31519/31519 = 1.0
      * PEG-based Optimization ratio 31519/31519 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 5 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 34
GLPKFormulation: Number of values: 31
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 575
      * PEG2PEGTIME 551
      * PBTIME 523
      * ENGINETIME 5
      * Optimization ratio 31519/31519 = 1.0
      * PEG-based Optimization ratio 31519/31519 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void expected(int,int)>
+ Done optimizing LoopInvariantCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopInvariantCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void original(int,int)>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void expected(int,int)>
+ Writing class back to optimized/LoopInvariantCodeMotion.class
+ Total optimization time = 2300 milliseconds
```

### Optimized
```java
23:05:25.273 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {
      paramInt1 += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      while (20 > paramInt2)
      {
        System.out.println(paramInt2 * paramInt1);
        paramInt2 += 1;
      }
      return;
      paramInt1 = paramInt1;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      for (paramInt1 = 0; paramInt1 < 20; paramInt1 = 1 + paramInt1) {
        System.out.println(paramInt1 * paramInt2);
      }
      return;
    }
  }
}

/* Location:
 * Qualified Name:     LoopInvariantCodeMotion
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
+ Loading class file LoopInvariantCodeMotion
+ Optimizing class LoopInvariantCodeMotion
   - Processing method <LoopInvariantCodeMotion: void <init>()>
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
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 558
      * PEG2PEGTIME 437
      * PBTIME 305
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 34 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 49
GLPKFormulation: Number of values: 37
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 814
      * PEG2PEGTIME 762
      * PBTIME 690
      * ENGINETIME 34
      * Optimization ratio 31519/31519 = 1.0
      * PEG-based Optimization ratio 31519/31519 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 14 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 49
GLPKFormulation: Number of values: 37
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 732
      * PEG2PEGTIME 714
      * PBTIME 683
      * ENGINETIME 14
      * Optimization ratio 31519/31519 = 1.0
      * PEG-based Optimization ratio 31519/31519 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void expected(int,int)>
+ Done optimizing LoopInvariantCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopInvariantCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void original(int,int)>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void expected(int,int)>
+ Writing class back to optimized/LoopInvariantCodeMotion.class
+ Total optimization time = 2601 milliseconds
```

### Optimized
```java
23:05:30.563 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {
      paramInt1 += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      while (20 > paramInt2)
      {
        System.out.println(paramInt2 * paramInt1);
        paramInt2 += 1;
      }
      return;
      paramInt1 = paramInt1;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {}
    for (paramInt1 = 1 + paramInt1;; paramInt1 = paramInt1)
    {
      for (paramInt2 = 0; paramInt2 < 20; paramInt2 = 1 + paramInt2) {
        System.out.println(paramInt2 * paramInt1);
      }
      return;
    }
  }
}

/* Location:
 * Qualified Name:     LoopInvariantCodeMotion
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
+ Loading class file LoopInvariantCodeMotion
+ Optimizing class LoopInvariantCodeMotion
   - Processing method <LoopInvariantCodeMotion: void <init>()>
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
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 556
      * PEG2PEGTIME 434
      * PBTIME 301
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 74 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 85
GLPKFormulation: Number of values: 52
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 1323
      * PEG2PEGTIME 1265
      * PBTIME 1147
      * ENGINETIME 74
      * Optimization ratio 31479/31519 = 0.9987309242044481
      * PEG-based Optimization ratio 31479/31519 = 0.9987309242044481
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 46 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 85
GLPKFormulation: Number of values: 60
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 1227
      * PEG2PEGTIME 1204
      * PBTIME 1139
      * ENGINETIME 46
      * Optimization ratio 31479/31519 = 0.9987309242044481
      * PEG-based Optimization ratio 31479/31519 = 0.9987309242044481
   - Done processing method <LoopInvariantCodeMotion: void expected(int,int)>
+ Done optimizing LoopInvariantCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopInvariantCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void original(int,int)>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void expected(int,int)>
+ Writing class back to optimized/LoopInvariantCodeMotion.class
+ Total optimization time = 3594 milliseconds
```

### Optimized
```java
23:05:36.859 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      paramInt1 = 0;
      this = paramInt2 * 1;
      paramInt2 = 0;
      while (paramInt1 < 20)
      {
        System.out.println(paramInt2);
        paramInt1 += 1;
        paramInt2 += this;
      }
      return;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {}
    for (paramInt2 = 1 + paramInt1;; paramInt2 = paramInt1)
    {
      this = paramInt2 * 1;
      paramInt1 = 0;
      paramInt2 = 0;
      while (paramInt1 < 20)
      {
        paramInt1 += 1;
        System.out.println(paramInt2);
        paramInt2 += this;
      }
      return;
    }
  }
}

/* Location:
 * Qualified Name:     LoopInvariantCodeMotion
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
+ Loading class file LoopInvariantCodeMotion
+ Optimizing class LoopInvariantCodeMotion
   - Processing method <LoopInvariantCodeMotion: void <init>()>
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
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 546
      * PEG2PEGTIME 427
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 147 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 133
GLPKFormulation: Number of values: 84
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 1986
      * PEG2PEGTIME 1935
      * PBTIME 1755
      * ENGINETIME 147
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 91 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 133
GLPKFormulation: Number of values: 84
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 1798
      * PEG2PEGTIME 1777
      * PBTIME 1670
      * ENGINETIME 92
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <LoopInvariantCodeMotion: void expected(int,int)>
+ Done optimizing LoopInvariantCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopInvariantCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void original(int,int)>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void expected(int,int)>
+ Writing class back to optimized/LoopInvariantCodeMotion.class
+ Total optimization time = 4808 milliseconds
```

### Optimized
```java
23:05:44.305 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {
      paramInt1 += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      this = 0;
      while (20 > this)
      {
        paramInt2 = paramInt1 + paramInt2;
        this = 1 + this;
        System.out.println(paramInt2);
      }
      return;
      paramInt1 = paramInt1;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {}
    for (paramInt1 = 1 + paramInt1;; paramInt1 = paramInt1)
    {
      paramInt2 = 0;
      this = 0;
      while (this < 20)
      {
        paramInt2 = paramInt1 + paramInt2;
        this += 1;
        System.out.println(paramInt2);
      }
      return;
    }
  }
}

/* Location:
 * Qualified Name:     LoopInvariantCodeMotion
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
+ Loading class file LoopInvariantCodeMotion
+ Optimizing class LoopInvariantCodeMotion
   - Processing method <LoopInvariantCodeMotion: void <init>()>
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
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 556
      * PEG2PEGTIME 426
      * PBTIME 302
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 256 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 235
GLPKFormulation: Number of values: 141
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 3234
      * PEG2PEGTIME 3172
      * PBTIME 2881
      * ENGINETIME 257
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 126 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 230
GLPKFormulation: Number of values: 140
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 2933
      * PEG2PEGTIME 2911
      * PBTIME 2760
      * ENGINETIME 126
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <LoopInvariantCodeMotion: void expected(int,int)>
+ Done optimizing LoopInvariantCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopInvariantCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void original(int,int)>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void expected(int,int)>
+ Writing class back to optimized/LoopInvariantCodeMotion.class
+ Total optimization time = 7213 milliseconds
```

### Optimized
```java
23:05:54.193 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {}
    for (paramInt2 = 1 + paramInt1;; paramInt2 = paramInt1)
    {
      this = 0;
      paramInt1 = 0;
      while (20 > this)
      {
        this = 1 + this;
        paramInt1 += paramInt2;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {}
    for (paramInt1 = 1 + paramInt1;; paramInt1 = paramInt1)
    {
      paramInt2 = 0;
      this = 0;
      while (20 > this)
      {
        paramInt2 += paramInt1;
        System.out.println(paramInt2);
        this += 1;
      }
      return;
    }
  }
}

/* Location:
 * Qualified Name:     LoopInvariantCodeMotion
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
+ Loading class file LoopInvariantCodeMotion
+ Optimizing class LoopInvariantCodeMotion
   - Processing method <LoopInvariantCodeMotion: void <init>()>
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
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 552
      * PEG2PEGTIME 431
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 516 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 494
GLPKFormulation: Number of values: 279
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 6807
      * PEG2PEGTIME 6751
      * PBTIME 6194
      * ENGINETIME 517
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 241 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 510
GLPKFormulation: Number of values: 281
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 6520
      * PEG2PEGTIME 6500
      * PBTIME 6236
      * ENGINETIME 241
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <LoopInvariantCodeMotion: void expected(int,int)>
+ Done optimizing LoopInvariantCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopInvariantCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void original(int,int)>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void expected(int,int)>
+ Writing class back to optimized/LoopInvariantCodeMotion.class
+ Total optimization time = 14345 milliseconds
```

### Optimized
```java
23:06:11.204 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {}
    for (paramInt1 = 1 + paramInt1;; paramInt1 = paramInt1)
    {
      this = 0;
      for (paramInt2 = 0; paramInt2 < 20; paramInt2 = 1 + paramInt2)
      {
        this = paramInt1 + this;
        System.out.println(this);
      }
      return;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {}
    for (paramInt2 = 1 + paramInt1;; paramInt2 = paramInt1)
    {
      this = 0;
      for (paramInt1 = 0; 20 > this; paramInt1 = paramInt2 + paramInt1)
      {
        this = 1 + this;
        System.out.println(paramInt1);
      }
      return;
    }
  }
}

/* Location:
 * Qualified Name:     LoopInvariantCodeMotion
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
+ Loading class file LoopInvariantCodeMotion
+ Optimizing class LoopInvariantCodeMotion
   - Processing method <LoopInvariantCodeMotion: void <init>()>
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
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 550
      * PEG2PEGTIME 430
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 954 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1285
GLPKFormulation: Number of values: 641
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 18493
      * PEG2PEGTIME 18436
      * PBTIME 17427
      * ENGINETIME 955
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 457 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1299
GLPKFormulation: Number of values: 644
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 23893
      * PEG2PEGTIME 23872
      * PBTIME 23393
      * ENGINETIME 457
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <LoopInvariantCodeMotion: void expected(int,int)>
+ Done optimizing LoopInvariantCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopInvariantCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void original(int,int)>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void expected(int,int)>
+ Writing class back to optimized/LoopInvariantCodeMotion.class
+ Total optimization time = 43411 milliseconds
```

### Optimized
```java
23:06:57.319 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {}
    for (this = paramInt1 + 1;; this = paramInt1)
    {
      paramInt1 = 0;
      paramInt2 = 0;
      while (paramInt2 < 20)
      {
        System.out.println(paramInt1);
        paramInt1 += this;
        paramInt2 += 1;
      }
      return;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {}
    for (this = paramInt1 + 1;; this = paramInt1)
    {
      paramInt2 = 0;
      for (paramInt1 = 0; paramInt2 < 20; paramInt1 = this + paramInt1)
      {
        System.out.println(paramInt1);
        paramInt2 += 1;
      }
      return;
    }
  }
}

/* Location:
 * Qualified Name:     LoopInvariantCodeMotion
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
+ Loading class file LoopInvariantCodeMotion
+ Optimizing class LoopInvariantCodeMotion
   - Processing method <LoopInvariantCodeMotion: void <init>()>
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
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 564
      * PEG2PEGTIME 436
      * PBTIME 304
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 2990 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 3327
GLPKFormulation: Number of values: 1542
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 47864
      * PEG2PEGTIME 47804
      * PBTIME 44774
      * ENGINETIME 2991
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 2424 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 3309
GLPKFormulation: Number of values: 1536
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 43590
      * PEG2PEGTIME 43561
      * PBTIME 41113
      * ENGINETIME 2425
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <LoopInvariantCodeMotion: void expected(int,int)>
+ Done optimizing LoopInvariantCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopInvariantCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void original(int,int)>
+ Fixing bytecode of method <LoopInvariantCodeMotion: void expected(int,int)>
+ Writing class back to optimized/LoopInvariantCodeMotion.class
+ Total optimization time = 92531 milliseconds
```

### Optimized
```java
23:08:32.665 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      this = 0;
      for (paramInt1 = 0; this < 20; paramInt1 = paramInt2 + paramInt1)
      {
        this += 1;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {}
    for (this = paramInt1 + 1;; this = paramInt1)
    {
      paramInt2 = 0;
      paramInt1 = 0;
      while (20 > paramInt2)
      {
        paramInt2 += 1;
        System.out.println(paramInt1);
        paramInt1 += this;
      }
      return;
    }
  }
}

/* Location:
 * Qualified Name:     LoopInvariantCodeMotion
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
