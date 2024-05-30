# LoopPeeling
## Original
```java
class LoopPeeling {
    public int original (int n) {
        int x = 0;
        int i = 0;
        while (i < n) {
            x += 5;
            i++;
        }
        return x;
    }

    public int expected (int n) {
        int x = 0;
        if (0 >= n) {
            x = 0;
        } else {
            x = 5;
            int i = 1;
            while (i < n) {
                x += 5;
                i++;
            }
        }
        return x;
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
+ Loading class file LoopPeeling
+ Optimizing class LoopPeeling
   - Processing method <LoopPeeling: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 6 milliseconds
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
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 1050
      * PEG2PEGTIME 839
      * PBTIME 606
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
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
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 717
      * PEG2PEGTIME 667
      * PBTIME 603
      * ENGINETIME 0
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
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
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 629
      * PEG2PEGTIME 589
      * PBTIME 558
      * ENGINETIME 0
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <LoopPeeling: int expected(int)>
+ Done optimizing LoopPeeling
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopPeeling: void <init>()>
+ Fixing bytecode of method <LoopPeeling: int original(int)>
+ Fixing bytecode of method <LoopPeeling: int expected(int)>
+ Writing class back to optimized/LoopPeeling.class
+ Total optimization time = 3247 milliseconds
```

```java
21:26:16.753 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
class LoopPeeling
{
  public int original(int paramInt)
  {
    this = 0;
    int i = 0;
    while (this < paramInt)
    {
      this += 1;
      i += 5;
    }
    return i;
  }
  
  public int expected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (paramInt = 0;; paramInt = this)
    {
      return paramInt;
      this = 5;
      int i = 1;
      while (i < paramInt)
      {
        this += 5;
        i += 1;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopPeeling
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
+ Loading class file LoopPeeling
+ Optimizing class LoopPeeling
   - Processing method <LoopPeeling: void <init>()>
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
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 869
      * PEG2PEGTIME 678
      * PBTIME 466
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
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
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 636
      * PEG2PEGTIME 581
      * PBTIME 498
      * ENGINETIME 1
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 16 milliseconds
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
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 622
      * PEG2PEGTIME 578
      * PBTIME 534
      * ENGINETIME 17
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <LoopPeeling: int expected(int)>
+ Done optimizing LoopPeeling
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopPeeling: void <init>()>
+ Fixing bytecode of method <LoopPeeling: int original(int)>
+ Fixing bytecode of method <LoopPeeling: int expected(int)>
+ Writing class back to optimized/LoopPeeling.class
+ Total optimization time = 2790 milliseconds
```

```java
21:26:23.209 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
class LoopPeeling
{
  public int original(int paramInt)
  {
    int i = 0;
    this = 0;
    while (this < paramInt)
    {
      i += 5;
      this += 1;
    }
    return i;
  }
  
  public int expected(int paramInt)
  {
    if (paramInt <= 0) {}
    int j;
    for (int i = 0;; j = this)
    {
      return i;
      this = 5;
      i = 1;
      while (i < paramInt)
      {
        this = 5 + this;
        i += 1;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopPeeling
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
+ Loading class file LoopPeeling
+ Optimizing class LoopPeeling
   - Processing method <LoopPeeling: void <init>()>
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
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 829
      * PEG2PEGTIME 642
      * PBTIME 440
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 16 milliseconds
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
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 545
      * PEG2PEGTIME 499
      * PBTIME 438
      * ENGINETIME 16
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 5 milliseconds
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
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 502
      * PEG2PEGTIME 470
      * PBTIME 448
      * ENGINETIME 5
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <LoopPeeling: int expected(int)>
+ Done optimizing LoopPeeling
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopPeeling: void <init>()>
+ Fixing bytecode of method <LoopPeeling: int original(int)>
+ Fixing bytecode of method <LoopPeeling: int expected(int)>
+ Writing class back to optimized/LoopPeeling.class
+ Total optimization time = 2483 milliseconds
```

```java
21:26:28.788 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
class LoopPeeling
{
  public int original(int paramInt)
  {
    this = 0;
    int i = 0;
    while (paramInt > i)
    {
      this += 5;
      i += 1;
    }
    return this;
  }
  
  public int expected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (int i = 0;; i = i)
    {
      return i;
      this = 1;
      i = 5;
      while (paramInt > this)
      {
        this += 1;
        i += 5;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopPeeling
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
+ Loading class file LoopPeeling
+ Optimizing class LoopPeeling
   - Processing method <LoopPeeling: void <init>()>
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
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 739
      * PEG2PEGTIME 609
      * PBTIME 424
      * ENGINETIME 9
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 21 milliseconds
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
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 601
      * PEG2PEGTIME 557
      * PBTIME 501
      * ENGINETIME 21
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 11 milliseconds
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
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 616
      * PEG2PEGTIME 583
      * PBTIME 550
      * ENGINETIME 11
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <LoopPeeling: int expected(int)>
+ Done optimizing LoopPeeling
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopPeeling: void <init>()>
+ Fixing bytecode of method <LoopPeeling: int original(int)>
+ Fixing bytecode of method <LoopPeeling: int expected(int)>
+ Writing class back to optimized/LoopPeeling.class
+ Total optimization time = 2445 milliseconds
```

```java
21:26:34.191 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
class LoopPeeling
{
  public int original(int paramInt)
  {
    this = 0;
    int i = 0;
    while (i < paramInt)
    {
      this = 5 + this;
      i += 1;
    }
    return this;
  }
  
  public int expected(int paramInt)
  {
    if (paramInt <= 0) {}
    int j;
    for (int i = 0;; j = this)
    {
      return i;
      this = 5;
      i = 1;
      while (i < paramInt)
      {
        this = 5 + this;
        i += 1;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopPeeling
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
+ Loading class file LoopPeeling
+ Optimizing class LoopPeeling
   - Processing method <LoopPeeling: void <init>()>
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
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 727
      * PEG2PEGTIME 594
      * PBTIME 430
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 45 milliseconds
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
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 847
      * PEG2PEGTIME 810
      * PBTIME 733
      * ENGINETIME 45
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 23 milliseconds
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
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 944
      * PEG2PEGTIME 904
      * PBTIME 860
      * ENGINETIME 23
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <LoopPeeling: int expected(int)>
+ Done optimizing LoopPeeling
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopPeeling: void <init>()>
+ Fixing bytecode of method <LoopPeeling: int original(int)>
+ Fixing bytecode of method <LoopPeeling: int expected(int)>
+ Writing class back to optimized/LoopPeeling.class
+ Total optimization time = 3089 milliseconds
```

```java
21:26:40.103 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
class LoopPeeling
{
  public int original(int paramInt)
  {
    this = 0;
    int i = 0;
    while (i < paramInt)
    {
      this = 5 + this;
      i += 1;
    }
    return this;
  }
  
  public int expected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (paramInt = 0;; paramInt = i)
    {
      return paramInt;
      this = 1;
      for (i = 5; this < paramInt; i = 5 + i) {
        this = 1 + this;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopPeeling
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
+ Loading class file LoopPeeling
+ Optimizing class LoopPeeling
   - Processing method <LoopPeeling: void <init>()>
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
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 575
      * PEG2PEGTIME 452
      * PBTIME 304
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 89 milliseconds
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
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 1210
      * PEG2PEGTIME 1173
      * PBTIME 1047
      * ENGINETIME 89
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 54 milliseconds
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
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 2664
      * PEG2PEGTIME 2611
      * PBTIME 2534
      * ENGINETIME 54
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <LoopPeeling: int expected(int)>
+ Done optimizing LoopPeeling
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopPeeling: void <init>()>
+ Fixing bytecode of method <LoopPeeling: int original(int)>
+ Fixing bytecode of method <LoopPeeling: int expected(int)>
+ Writing class back to optimized/LoopPeeling.class
+ Total optimization time = 5009 milliseconds
```

```java
21:26:48.712 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
class LoopPeeling
{
  public int original(int paramInt)
  {
    this = 0;
    for (int i = 0; paramInt > this; i = 5 + i) {
      this += 1;
    }
    return i;
  }
  
  public int expected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (int i = 0;; i = i)
    {
      return i;
      this = 1;
      for (i = 5; paramInt > this; i = 5 + i) {
        this += 1;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopPeeling
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
+ Loading class file LoopPeeling
+ Optimizing class LoopPeeling
   - Processing method <LoopPeeling: void <init>()>
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
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 1534
      * PEG2PEGTIME 1116
      * PBTIME 707
      * ENGINETIME 14
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 572 milliseconds
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
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 4669
      * PEG2PEGTIME 4582
      * PBTIME 3921
      * ENGINETIME 572
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 346 milliseconds
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
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 4888
      * PEG2PEGTIME 4850
      * PBTIME 4458
      * ENGINETIME 346
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <LoopPeeling: int expected(int)>
+ Done optimizing LoopPeeling
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopPeeling: void <init>()>
+ Fixing bytecode of method <LoopPeeling: int original(int)>
+ Fixing bytecode of method <LoopPeeling: int expected(int)>
+ Writing class back to optimized/LoopPeeling.class
+ Total optimization time = 12120 milliseconds
```

```java
21:27:06.225 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
class LoopPeeling
{
  public int original(int paramInt)
  {
    int i = 0;
    this = 0;
    while (this < paramInt)
    {
      i = 5 + i;
      this += 1;
    }
    return i;
  }
  
  public int expected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (paramInt = 0;; paramInt = i)
    {
      return paramInt;
      this = 1;
      for (i = 5; this < paramInt; i = 5 + i) {
        this = 1 + this;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopPeeling
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
+ Loading class file LoopPeeling
+ Optimizing class LoopPeeling
   - Processing method <LoopPeeling: void <init>()>
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
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 582
      * PEG2PEGTIME 458
      * PBTIME 310
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 438 milliseconds
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
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 7433
      * PEG2PEGTIME 7387
      * PBTIME 6913
      * ENGINETIME 439
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 236 milliseconds
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
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 7522
      * PEG2PEGTIME 7492
      * PBTIME 7228
      * ENGINETIME 236
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <LoopPeeling: int expected(int)>
+ Done optimizing LoopPeeling
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopPeeling: void <init>()>
+ Fixing bytecode of method <LoopPeeling: int original(int)>
+ Fixing bytecode of method <LoopPeeling: int expected(int)>
+ Writing class back to optimized/LoopPeeling.class
+ Total optimization time = 15974 milliseconds
```

```java
21:27:24.860 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
class LoopPeeling
{
  public int original(int paramInt)
  {
    this = 0;
    int i = 0;
    while (paramInt > this)
    {
      this += 1;
      i += 5;
    }
    return i;
  }
  
  public int expected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (paramInt = 0;; paramInt = i)
    {
      return paramInt;
      this = 1;
      for (i = 5; this < paramInt; i = 5 + i) {
        this += 1;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopPeeling
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
+ Loading class file LoopPeeling
+ Optimizing class LoopPeeling
   - Processing method <LoopPeeling: void <init>()>
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
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 560
      * PEG2PEGTIME 437
      * PBTIME 301
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 837 milliseconds
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
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 13198
      * PEG2PEGTIME 13161
      * PBTIME 12289
      * ENGINETIME 838
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 613 milliseconds
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
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 13189
      * PEG2PEGTIME 13158
      * PBTIME 12520
      * ENGINETIME 614
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <LoopPeeling: int expected(int)>
+ Done optimizing LoopPeeling
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopPeeling: void <init>()>
+ Fixing bytecode of method <LoopPeeling: int original(int)>
+ Fixing bytecode of method <LoopPeeling: int expected(int)>
+ Writing class back to optimized/LoopPeeling.class
+ Total optimization time = 27379 milliseconds
```

```java
21:27:54.849 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
class LoopPeeling
{
  public int original(int paramInt)
  {
    this = 0;
    for (int i = 0; paramInt > this; i = 5 + i) {
      this += 1;
    }
    return i;
  }
  
  public int expected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (paramInt = 0;; paramInt = this)
    {
      return paramInt;
      int i = 1;
      this = 5;
      while (i < paramInt)
      {
        i = 1 + i;
        this += 5;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopPeeling
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
+ Loading class file LoopPeeling
+ Optimizing class LoopPeeling
   - Processing method <LoopPeeling: void <init>()>
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
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 561
      * PEG2PEGTIME 440
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1547 milliseconds
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
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 18274
      * PEG2PEGTIME 18225
      * PBTIME 16643
      * ENGINETIME 1548
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 2757 milliseconds
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
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 18655
      * PEG2PEGTIME 18621
      * PBTIME 15832
      * ENGINETIME 2758
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <LoopPeeling: int expected(int)>
+ Done optimizing LoopPeeling
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopPeeling: void <init>()>
+ Fixing bytecode of method <LoopPeeling: int original(int)>
+ Fixing bytecode of method <LoopPeeling: int expected(int)>
+ Writing class back to optimized/LoopPeeling.class
+ Total optimization time = 37928 milliseconds
```

```java
21:28:35.441 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
class LoopPeeling
{
  public int original(int paramInt)
  {
    int i = 0;
    this = 0;
    while (paramInt > this)
    {
      i += 5;
      this += 1;
    }
    return i;
  }
  
  public int expected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (paramInt = 0;; paramInt = this)
    {
      return paramInt;
      this = 5;
      int i = 1;
      while (i < paramInt)
      {
        this = 5 + this;
        i += 1;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopPeeling
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
+ Loading class file LoopPeeling
+ Optimizing class LoopPeeling
   - Processing method <LoopPeeling: void <init>()>
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
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 560
      * PEG2PEGTIME 439
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 4056 milliseconds
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
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 26267
      * PEG2PEGTIME 26228
      * PBTIME 22134
      * ENGINETIME 4057
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 3468 milliseconds
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
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 25242
      * PEG2PEGTIME 25208
      * PBTIME 21713
      * ENGINETIME 3470
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <LoopPeeling: int expected(int)>
+ Done optimizing LoopPeeling
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopPeeling: void <init>()>
+ Fixing bytecode of method <LoopPeeling: int original(int)>
+ Fixing bytecode of method <LoopPeeling: int expected(int)>
+ Writing class back to optimized/LoopPeeling.class
+ Total optimization time = 52509 milliseconds
```

```java
21:29:30.604 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
class LoopPeeling
{
  public int original(int paramInt)
  {
    this = 0;
    int i = 0;
    while (paramInt > i)
    {
      this += 5;
      i += 1;
    }
    return this;
  }
  
  public int expected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (paramInt = 0;; paramInt = i)
    {
      return paramInt;
      this = 1;
      for (i = 5; paramInt > this; i = 5 + i) {
        this += 1;
      }
    }
  }
}

/* Location:
 * Qualified Name:     LoopPeeling
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
