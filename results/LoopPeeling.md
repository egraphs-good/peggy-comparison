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
+ Loading class file LoopPeeling
+ Optimizing class LoopPeeling
   - Processing method <LoopPeeling: void <init>()>
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
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 564
      * PEG2PEGTIME 441
      * PBTIME 308
      * ENGINETIME 4
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
GLPKFormulation: Number of nodes: 15
GLPKFormulation: Number of values: 15
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 383
      * PEG2PEGTIME 351
      * PBTIME 319
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
GLPKFormulation: Number of nodes: 17
GLPKFormulation: Number of values: 17
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 392
      * PEG2PEGTIME 363
      * PBTIME 348
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
+ Total optimization time = 1784 milliseconds
```

### Optimized
```java
23:15:30.918 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
class LoopPeeling
{
  public int original(int paramInt)
  {
    this = 0;
    int i = 0;
    while (i < paramInt)
    {
      this += 5;
      i += 1;
    }
    return this;
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
        i += 1;
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
+ Loading class file LoopPeeling
+ Optimizing class LoopPeeling
   - Processing method <LoopPeeling: void <init>()>
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
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 572
      * PEG2PEGTIME 449
      * PBTIME 300
      * ENGINETIME 4
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
GLPKFormulation: Number of nodes: 15
GLPKFormulation: Number of values: 15
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 387
      * PEG2PEGTIME 351
      * PBTIME 317
      * ENGINETIME 1
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 17
GLPKFormulation: Number of values: 17
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 403
      * PEG2PEGTIME 375
      * PBTIME 351
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
+ Total optimization time = 1804 milliseconds
```

### Optimized
```java
23:15:35.293 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
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
    int j;
    for (int i = 0;; j = this)
    {
      return i;
      i = 1;
      this = 5;
      while (i < paramInt)
      {
        i += 1;
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
+ Loading class file LoopPeeling
+ Optimizing class LoopPeeling
   - Processing method <LoopPeeling: void <init>()>
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
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 574
      * PEG2PEGTIME 453
      * PBTIME 307
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 14 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 23
GLPKFormulation: Number of values: 19
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 487
      * PEG2PEGTIME 453
      * PBTIME 407
      * ENGINETIME 14
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
GLPKFormulation: Number of nodes: 25
GLPKFormulation: Number of values: 21
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 500
      * PEG2PEGTIME 470
      * PBTIME 446
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
+ Total optimization time = 2027 milliseconds
```

### Optimized
```java
23:15:39.857 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 557
      * PEG2PEGTIME 437
      * PBTIME 299
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 17 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 32
GLPKFormulation: Number of values: 24
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 607
      * PEG2PEGTIME 562
      * PBTIME 510
      * ENGINETIME 17
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 10 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 32
GLPKFormulation: Number of values: 24
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 593
      * PEG2PEGTIME 560
      * PBTIME 528
      * ENGINETIME 10
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
+ Total optimization time = 2192 milliseconds
```

### Optimized
```java
23:15:44.642 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 560
      * PEG2PEGTIME 432
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 41 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 48
GLPKFormulation: Number of values: 29
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 792
      * PEG2PEGTIME 757
      * PBTIME 687
      * ENGINETIME 41
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 22 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 52
GLPKFormulation: Number of values: 33
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 907
      * PEG2PEGTIME 879
      * PBTIME 836
      * ENGINETIME 22
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
+ Total optimization time = 2691 milliseconds
```

### Optimized
```java
23:15:49.955 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
class LoopPeeling
{
  public int original(int paramInt)
  {
    int i = 0;
    for (this = 0; this < paramInt; this = 1 + this) {
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 559
      * PEG2PEGTIME 438
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 87 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 83
GLPKFormulation: Number of values: 41
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 1317
      * PEG2PEGTIME 1274
      * PBTIME 1153
      * ENGINETIME 87
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 50 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 85
GLPKFormulation: Number of values: 46
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 1279
      * PEG2PEGTIME 1251
      * PBTIME 1178
      * ENGINETIME 50
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
+ Total optimization time = 3597 milliseconds
```

### Optimized
```java
23:15:56.113 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 567
      * PEG2PEGTIME 445
      * PBTIME 303
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 193 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 143
GLPKFormulation: Number of values: 60
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 2036
      * PEG2PEGTIME 2002
      * PBTIME 1773
      * ENGINETIME 193
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 117 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 193
GLPKFormulation: Number of values: 94
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 2532
      * PEG2PEGTIME 2506
      * PBTIME 2369
      * ENGINETIME 117
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
+ Total optimization time = 5578 milliseconds
```

### Optimized
```java
23:16:04.372 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
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
    for (paramInt = 0;; paramInt = this)
    {
      return paramInt;
      int i = 1;
      this = 5;
      while (i < paramInt)
      {
        i += 1;
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 557
      * PEG2PEGTIME 436
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 445 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 589
GLPKFormulation: Number of values: 187
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 7140
      * PEG2PEGTIME 7103
      * PBTIME 6625
      * ENGINETIME 445
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 220 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 630
GLPKFormulation: Number of values: 171
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 7494
      * PEG2PEGTIME 7463
      * PBTIME 7217
      * ENGINETIME 220
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
+ Total optimization time = 15625 milliseconds
```

### Optimized
```java
23:16:22.568 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
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
      i = 5;
      while (this < paramInt)
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
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
      * PBTIME 325
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 799 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1098
GLPKFormulation: Number of values: 174
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 13665
      * PEG2PEGTIME 13623
      * PBTIME 12790
      * ENGINETIME 800
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 514 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1076
GLPKFormulation: Number of values: 180
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 13014
      * PEG2PEGTIME 12984
      * PBTIME 12445
      * ENGINETIME 514
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
+ Total optimization time = 27698 milliseconds
```

### Optimized
```java
23:16:52.882 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
class LoopPeeling
{
  public int original(int paramInt)
  {
    int i = 0;
    for (this = 0; paramInt > this; this = 1 + this) {
      i = 5 + i;
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
      i = 5;
      this = 1;
      while (paramInt > this)
      {
        i += 5;
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 559
      * PEG2PEGTIME 438
      * PBTIME 299
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1480 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1507
GLPKFormulation: Number of values: 187
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 17860
      * PEG2PEGTIME 17824
      * PBTIME 16310
      * ENGINETIME 1480
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1588 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1423
GLPKFormulation: Number of values: 157
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 18597
      * PEG2PEGTIME 18564
      * PBTIME 16950
      * ENGINETIME 1589
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
+ Total optimization time = 37456 milliseconds
```

### Optimized
```java
23:17:33.009 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
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
    int j;
    for (int i = 0;; j = this)
    {
      return i;
      this = 5;
      i = 1;
      while (paramInt > i)
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: void <init>()> SUCCESSFUL
      * Optimization took 562
      * PEG2PEGTIME 434
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopPeeling: void <init>()>
   - Processing method <LoopPeeling: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 4238 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 2223
GLPKFormulation: Number of values: 265
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int original(int)> SUCCESSFUL
      * Optimization took 29243
      * PEG2PEGTIME 29204
      * PBTIME 24935
      * ENGINETIME 4239
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <LoopPeeling: int original(int)>
   - Processing method <LoopPeeling: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 3257 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1855
GLPKFormulation: Number of values: 158
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopPeeling: int expected(int)> SUCCESSFUL
      * Optimization took 25451
      * PEG2PEGTIME 25409
      * PBTIME 22128
      * ENGINETIME 3258
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
+ Total optimization time = 55714 milliseconds
```

### Optimized
```java
23:18:31.442 INFO  jd.cli.Main - Decompiling optimized/LoopPeeling.class
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
    int j;
    for (int i = 0;; j = this)
    {
      return i;
      this = 5;
      for (i = 1; paramInt > i; i = 1 + i) {
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
