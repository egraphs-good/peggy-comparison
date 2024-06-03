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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 2

Peggy output
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
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 466
      * PEG2PEGTIME 377
      * PBTIME 286
      * ENGINETIME 3
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 448
      * PEG2PEGTIME 403
      * PBTIME 379
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 406
      * PEG2PEGTIME 369
      * PBTIME 360
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
+ Total optimization time = 1688 milliseconds
```

```java
21:14:15.067 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 4

Peggy output
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
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 491
      * PEG2PEGTIME 408
      * PBTIME 269
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
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
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 419
      * PEG2PEGTIME 381
      * PBTIME 358
      * ENGINETIME 1
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 406
      * PEG2PEGTIME 391
      * PBTIME 381
      * ENGINETIME 1
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
+ Total optimization time = 1625 milliseconds
```

```java
21:14:18.366 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 8

Peggy output
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
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 434
      * PEG2PEGTIME 350
      * PBTIME 271
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 1 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 489
      * PEG2PEGTIME 436
      * PBTIME 415
      * ENGINETIME 1
      * Optimization ratio 31519/31519 = 1.0
      * PEG-based Optimization ratio 31519/31519 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 9 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 422
      * PEG2PEGTIME 407
      * PBTIME 386
      * ENGINETIME 9
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
+ Total optimization time = 1669 milliseconds
```

```java
21:14:21.783 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
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
        System.out.println(paramInt1 * paramInt2);
        paramInt1 += 1;
      }
      return;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 16

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 445
      * PEG2PEGTIME 360
      * PBTIME 275
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 8 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 480
      * PEG2PEGTIME 441
      * PBTIME 408
      * ENGINETIME 8
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 462
      * PEG2PEGTIME 446
      * PBTIME 428
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
+ Total optimization time = 1693 milliseconds
```

```java
21:14:25.295 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {
      paramInt1 += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      while (paramInt2 < 20)
      {
        paramInt2 = 1 + paramInt2;
        System.out.println(paramInt2 * paramInt1);
      }
      return;
      paramInt1 = paramInt1;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {
      paramInt1 += 1;
    }
    for (;;)
    {
      for (paramInt2 = 0; paramInt2 < 20; paramInt2 = 1 + paramInt2) {
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 32

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 421
      * PEG2PEGTIME 344
      * PBTIME 266
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 21 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 657
      * PEG2PEGTIME 620
      * PBTIME 576
      * ENGINETIME 21
      * Optimization ratio 31519/31519 = 1.0
      * PEG-based Optimization ratio 31519/31519 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 9 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 606
      * PEG2PEGTIME 590
      * PBTIME 569
      * ENGINETIME 9
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
+ Total optimization time = 1976 milliseconds
```

```java
21:14:29.006 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {}
    for (paramInt1 = 1 + paramInt1;; paramInt1 = paramInt1)
    {
      paramInt2 = 0;
      while (paramInt2 < 20)
      {
        paramInt2 += 1;
        System.out.println(paramInt2 * paramInt1);
      }
      return;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 64

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 425
      * PEG2PEGTIME 350
      * PBTIME 270
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 42 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 966
      * PEG2PEGTIME 930
      * PBTIME 868
      * ENGINETIME 42
      * Optimization ratio 31479/31519 = 0.9987309242044481
      * PEG-based Optimization ratio 31479/31519 = 0.9987309242044481
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 22 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 846
      * PEG2PEGTIME 830
      * PBTIME 797
      * ENGINETIME 22
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
+ Total optimization time = 2548 milliseconds
```

```java
21:14:33.311 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {}
    for (this = 1 + paramInt1;; this = paramInt1)
    {
      paramInt2 = 0;
      paramInt1 = 0;
      this = 1 * this;
      while (paramInt2 < 20)
      {
        paramInt2 = 1 + paramInt2;
        paramInt1 += this;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      paramInt2 *= 1;
      paramInt1 = 0;
      this = 0;
      while (this < 20)
      {
        paramInt1 += paramInt2;
        System.out.println(paramInt1);
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 128

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 441
      * PEG2PEGTIME 359
      * PBTIME 277
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 139 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 1423
      * PEG2PEGTIME 1389
      * PBTIME 1227
      * ENGINETIME 139
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 46 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 1307
      * PEG2PEGTIME 1291
      * PBTIME 1235
      * ENGINETIME 46
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
+ Total optimization time = 3473 milliseconds
```

```java
21:14:38.498 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      this = 0;
      paramInt1 = 0;
      while (20 > paramInt1)
      {
        this = paramInt2 + this;
        System.out.println(this);
        paramInt1 += 1;
      }
      return;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {}
    for (paramInt1 = 1 + paramInt1;; paramInt1 = paramInt1)
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 256

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 440
      * PEG2PEGTIME 353
      * PBTIME 270
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 171 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 2269
      * PEG2PEGTIME 2237
      * PBTIME 2040
      * ENGINETIME 171
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 87 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 2259
      * PEG2PEGTIME 2246
      * PBTIME 2143
      * ENGINETIME 87
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
+ Total optimization time = 5276 milliseconds
```

```java
21:14:45.586 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {}
    for (this = paramInt1 + 1;; this = paramInt1)
    {
      paramInt1 = 0;
      for (paramInt2 = 0; 20 > paramInt2; paramInt2 = 1 + paramInt2)
      {
        paramInt1 = this + paramInt1;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {
      paramInt1 += 1;
    }
    for (;;)
    {
      this = 0;
      paramInt2 = 0;
      while (20 > paramInt2)
      {
        this += paramInt1;
        System.out.println(this);
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 512

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 433
      * PEG2PEGTIME 354
      * PBTIME 264
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 504 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 4725
      * PEG2PEGTIME 4685
      * PBTIME 4159
      * ENGINETIME 504
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 160 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 4573
      * PEG2PEGTIME 4557
      * PBTIME 4378
      * ENGINETIME 161
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
+ Total optimization time = 10051 milliseconds
```

```java
21:14:57.437 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      this = 0;
      paramInt1 = 0;
      while (this < 20)
      {
        this += 1;
        System.out.println(paramInt1);
        paramInt1 += paramInt2;
      }
      return;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {
      paramInt1 += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      for (this = 0; this < 20; this = 1 + this)
      {
        System.out.println(paramInt2);
        paramInt2 += paramInt1;
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 1024

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 475
      * PEG2PEGTIME 383
      * PBTIME 281
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 678 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 14641
      * PEG2PEGTIME 14595
      * PBTIME 13892
      * ENGINETIME 678
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 380 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 12710
      * PEG2PEGTIME 12694
      * PBTIME 12297
      * ENGINETIME 380
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
+ Total optimization time = 28208 milliseconds
```

```java
21:15:27.565 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {}
    for (this = 1 + paramInt1;; this = paramInt1)
    {
      paramInt1 = 0;
      paramInt2 = 0;
      while (20 > paramInt2)
      {
        paramInt1 = this + paramInt1;
        paramInt2 = 1 + paramInt2;
        System.out.println(paramInt1);
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
      this = 0;
      paramInt1 = 0;
      while (paramInt1 < 20)
      {
        this = paramInt2 + this;
        paramInt1 += 1;
        System.out.println(this);
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 2048

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopInvariantCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 501
      * PEG2PEGTIME 368
      * PBTIME 284
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopInvariantCodeMotion: void <init>()>
   - Processing method <LoopInvariantCodeMotion: void original(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 2384 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void original(int,int)> SUCCESSFUL
      * Optimization took 35558
      * PEG2PEGTIME 35513
      * PBTIME 33096
      * ENGINETIME 2384
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <LoopInvariantCodeMotion: void original(int,int)>
   - Processing method <LoopInvariantCodeMotion: void expected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 1610 milliseconds
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
      * Optimization of method <LoopInvariantCodeMotion: void expected(int,int)> SUCCESSFUL
      * Optimization took 34520
      * PEG2PEGTIME 34491
      * PBTIME 32863
      * ENGINETIME 1611
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
+ Total optimization time = 70927 milliseconds
```

```java
21:16:40.357 INFO  jd.cli.Main - Decompiling optimized/LoopInvariantCodeMotion.class
import java.io.PrintStream;

public class LoopInvariantCodeMotion
{
  public void original(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {}
    for (paramInt2 = 1 + paramInt1;; paramInt2 = paramInt1)
    {
      this = 0;
      paramInt1 = 0;
      while (paramInt1 < 20)
      {
        System.out.println(this);
        this = paramInt2 + this;
        paramInt1 += 1;
      }
      return;
    }
  }
  
  public void expected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      this = 0;
      paramInt1 = 0;
      while (20 > this)
      {
        this = 1 + this;
        paramInt1 = paramInt2 + paramInt1;
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
