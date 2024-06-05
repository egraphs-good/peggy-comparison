# BranchHoisting
## Original
```java
public class BranchHoisting {
    public int original(int n) {
        int y = 0;
        int x = 0;
        while (y < 500) {
            if (n == 0) {
               x = y * 2;
            } else {
                x = y * 3;
            }
            y++;
        }
        return x;
    }

    public int expected(int n) {
        int y = 0;
        int x = 0;
        while (y < 500) y++;

        if (n == 0) {
            x = y * 2;
        } else {
            x = y * 3;
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 554
      * PEG2PEGTIME 434
      * PBTIME 299
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 20
GLPKFormulation: Number of values: 20
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 463
      * PEG2PEGTIME 397
      * PBTIME 363
      * ENGINETIME 1
      * Optimization ratio 695/695 = 1.0
      * PEG-based Optimization ratio 695/695 = 1.0
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 19
GLPKFormulation: Number of values: 19
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 390
      * PEG2PEGTIME 376
      * PBTIME 358
      * ENGINETIME 0
      * Optimization ratio 235/235 = 1.0
      * PEG-based Optimization ratio 235/235 = 1.0
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 1845 milliseconds
```

### Optimized
```java
22:52:02.149 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    for (this = 0;; this = localBranchHoisting)
    {
      localBranchHoisting = this + 1;
      if (localBranchHoisting >= 500) {
        break;
      }
    }
    if (paramInt == 0) {}
    for (BranchHoisting localBranchHoisting = this * 2;; localBranchHoisting = this * 3) {
      return localBranchHoisting;
    }
  }
  
  public int expected(int paramInt)
  {
    this = 0;
    while (this < 500) {
      this += 1;
    }
    if (paramInt == 0) {}
    for (paramInt = this * 2;; paramInt = this * 3) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 549
      * PEG2PEGTIME 428
      * PBTIME 300
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 20
GLPKFormulation: Number of values: 20
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 460
      * PEG2PEGTIME 409
      * PBTIME 377
      * ENGINETIME 0
      * Optimization ratio 695/695 = 1.0
      * PEG-based Optimization ratio 695/695 = 1.0
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 10 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 20
GLPKFormulation: Number of values: 19
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 406
      * PEG2PEGTIME 390
      * PBTIME 364
      * ENGINETIME 10
      * Optimization ratio 235/235 = 1.0
      * PEG-based Optimization ratio 235/235 = 1.0
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 1860 milliseconds
```

### Optimized
```java
22:52:06.588 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    int j;
    for (int i = 0;; j = this)
    {
      this = i + 1;
      if (this >= 500) {
        break;
      }
    }
    int k;
    if (paramInt == 0) {
      j *= 2;
    }
    for (;;)
    {
      return k;
      k *= 3;
    }
  }
  
  public int expected(int paramInt)
  {
    this = 0;
    while (this < 500) {
      this += 1;
    }
    if (paramInt == 0) {}
    for (paramInt = this * 2;; paramInt = this * 3) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 567
      * PEG2PEGTIME 447
      * PBTIME 309
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 2 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 20
GLPKFormulation: Number of values: 20
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 454
      * PEG2PEGTIME 402
      * PBTIME 362
      * ENGINETIME 2
      * Optimization ratio 695/695 = 1.0
      * PEG-based Optimization ratio 695/695 = 1.0
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 15 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 27
GLPKFormulation: Number of values: 23
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 494
      * PEG2PEGTIME 477
      * PBTIME 446
      * ENGINETIME 15
      * Optimization ratio 235/235 = 1.0
      * PEG-based Optimization ratio 235/235 = 1.0
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 1957 milliseconds
```

### Optimized
```java
22:52:11.220 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    int j;
    for (int i = 0;; j = this)
    {
      this = i + 1;
      if (this >= 500) {
        break;
      }
    }
    if (paramInt == 0) {}
    for (paramInt = j * 2;; paramInt = j * 3) {
      return paramInt;
    }
  }
  
  public int expected(int paramInt)
  {
    for (this = 0; 500 > this; this = 1 + this) {}
    if (paramInt == 0) {}
    for (paramInt = this * 2;; paramInt = this * 3) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 612
      * PEG2PEGTIME 489
      * PBTIME 325
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 16 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 32
GLPKFormulation: Number of values: 26
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 615
      * PEG2PEGTIME 562
      * PBTIME 510
      * ENGINETIME 16
      * Optimization ratio 695/695 = 1.0
      * PEG-based Optimization ratio 695/695 = 1.0
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 10 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 33
GLPKFormulation: Number of values: 25
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 571
      * PEG2PEGTIME 553
      * PBTIME 522
      * ENGINETIME 11
      * Optimization ratio 235/235 = 1.0
      * PEG-based Optimization ratio 235/235 = 1.0
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 2256 milliseconds
```

### Optimized
```java
22:52:16.054 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    BranchHoisting localBranchHoisting;
    for (this = 0;; this = localBranchHoisting)
    {
      localBranchHoisting = this + 1;
      if (localBranchHoisting >= 500) {
        break;
      }
    }
    if (paramInt == 0) {}
    for (paramInt = 2 * this;; paramInt = this * 3) {
      return paramInt;
    }
  }
  
  public int expected(int paramInt)
  {
    for (this = 0; this < 500; this = 1 + this) {}
    if (paramInt == 0) {}
    for (paramInt = 2 * this;; paramInt = 3 * this) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 576
      * PEG2PEGTIME 454
      * PBTIME 311
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 43 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 48
GLPKFormulation: Number of values: 41
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 992
      * PEG2PEGTIME 940
      * PBTIME 853
      * ENGINETIME 43
      * Optimization ratio 695/695 = 1.0
      * PEG-based Optimization ratio 695/695 = 1.0
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 23 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 50
GLPKFormulation: Number of values: 32
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 773
      * PEG2PEGTIME 759
      * PBTIME 719
      * ENGINETIME 23
      * Optimization ratio 235/235 = 1.0
      * PEG-based Optimization ratio 235/235 = 1.0
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 2775 milliseconds
```

### Optimized
```java
22:52:21.436 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    int j;
    for (int i = 0;; j = this)
    {
      this = i + 1;
      if (this >= 500) {
        break;
      }
    }
    if (paramInt == 0) {}
    for (paramInt = 2 * j;; paramInt = 3 * j) {
      return paramInt;
    }
  }
  
  public int expected(int paramInt)
  {
    for (this = 0; 500 > this; this = 1 + this) {}
    if (paramInt == 0) {}
    for (paramInt = this * 2;; paramInt = this * 3) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 581
      * PEG2PEGTIME 463
      * PBTIME 299
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 96 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 89
GLPKFormulation: Number of values: 63
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 1471
      * PEG2PEGTIME 1415
      * PBTIME 1285
      * ENGINETIME 96
      * Optimization ratio 575/695 = 0.8273381294964028
      * PEG-based Optimization ratio 575/695 = 0.8273381294964028
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 60 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 96
GLPKFormulation: Number of values: 58
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 1482
      * PEG2PEGTIME 1467
      * PBTIME 1386
      * ENGINETIME 60
      * Optimization ratio 219/235 = 0.9319148936170213
      * PEG-based Optimization ratio 219/235 = 0.9319148936170213
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 3975 milliseconds
```

### Optimized
```java
22:52:28.010 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    if (paramInt == 0)
    {
      this = 0;
      for (paramInt = 0;; paramInt = 2 + paramInt)
      {
        this += 1;
        if (500 <= this) {
          break;
        }
        this = this;
      }
    }
    for (paramInt = paramInt;; paramInt = paramInt)
    {
      return paramInt;
      this = 0;
      paramInt = 0;
      for (;;)
      {
        this += 1;
        if (500 <= this) {
          break;
        }
        this = this;
        paramInt += 3;
      }
    }
  }
  
  public int expected(int paramInt)
  {
    for (this = 0; 500 > this; this = 1 + this) {}
    if (paramInt == 0) {}
    for (paramInt = this + this;; paramInt = 3 * this) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 568
      * PEG2PEGTIME 445
      * PBTIME 305
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 195 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 185
GLPKFormulation: Number of values: 118
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 2673
      * PEG2PEGTIME 2623
      * PBTIME 2386
      * ENGINETIME 196
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 106 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 176
GLPKFormulation: Number of values: 98
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 2431
      * PEG2PEGTIME 2418
      * PBTIME 2292
      * ENGINETIME 107
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 6131 milliseconds
```

### Optimized
```java
22:52:36.770 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    BranchHoisting localBranchHoisting;
    for (int i = 0;; localBranchHoisting = this)
    {
      this = i + 1;
      if (this >= 500) {
        break;
      }
    }
    this = localBranchHoisting + localBranchHoisting;
    if (paramInt == 0) {}
    for (paramInt = this;; paramInt = this + localBranchHoisting) {
      return paramInt;
    }
  }
  
  public int expected(int paramInt)
  {
    for (BranchHoisting localBranchHoisting = 0; 500 > localBranchHoisting; localBranchHoisting = 1 + localBranchHoisting) {}
    this = localBranchHoisting + localBranchHoisting;
    if (paramInt == 0) {}
    for (paramInt = this;; paramInt = this + localBranchHoisting) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 561
      * PEG2PEGTIME 438
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 368 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 428
GLPKFormulation: Number of values: 234
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 5687
      * PEG2PEGTIME 5635
      * PBTIME 5233
      * ENGINETIME 368
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 269 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 390
GLPKFormulation: Number of values: 182
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 5209
      * PEG2PEGTIME 5191
      * PBTIME 4903
      * ENGINETIME 270
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 11913 milliseconds
```

### Optimized
```java
22:52:51.319 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    BranchHoisting localBranchHoisting;
    for (int i = 0;; localBranchHoisting = this)
    {
      this = i + 1;
      if (500 <= this) {
        break;
      }
    }
    this = localBranchHoisting + localBranchHoisting;
    if (paramInt == 0) {}
    for (paramInt = this;; paramInt = localBranchHoisting + this) {
      return paramInt;
    }
  }
  
  public int expected(int paramInt)
  {
    this = 0;
    while (500 > this) {
      this += 1;
    }
    BranchHoisting localBranchHoisting = this + this;
    if (paramInt == 0) {}
    for (paramInt = localBranchHoisting;; paramInt = this + localBranchHoisting) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 558
      * PEG2PEGTIME 437
      * PBTIME 299
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 738 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 880
GLPKFormulation: Number of values: 362
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 11240
      * PEG2PEGTIME 11183
      * PBTIME 10411
      * ENGINETIME 738
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 322 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 828
GLPKFormulation: Number of values: 364
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 11084
      * PEG2PEGTIME 11068
      * PBTIME 10723
      * ENGINETIME 323
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 23325 milliseconds
```

### Optimized
```java
22:53:17.304 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    for (this = 0;; this = localBranchHoisting)
    {
      localBranchHoisting = this + 1;
      if (localBranchHoisting >= 500) {
        break;
      }
    }
    BranchHoisting localBranchHoisting = this + this;
    if (paramInt == 0) {}
    for (paramInt = localBranchHoisting;; paramInt = localBranchHoisting + this) {
      return paramInt;
    }
  }
  
  public int expected(int paramInt)
  {
    for (this = 0; this < 500; this = 1 + this) {}
    BranchHoisting localBranchHoisting = this + this;
    if (paramInt == 0) {}
    for (paramInt = localBranchHoisting;; paramInt = localBranchHoisting + this) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 597
      * PEG2PEGTIME 462
      * PBTIME 307
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1378 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 2197
GLPKFormulation: Number of values: 922
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 27467
      * PEG2PEGTIME 27414
      * PBTIME 26003
      * ENGINETIME 1378
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 732 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1752
GLPKFormulation: Number of values: 795
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 24490
      * PEG2PEGTIME 24473
      * PBTIME 23717
      * ENGINETIME 733
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 53003 milliseconds
```

### Optimized
```java
22:54:12.979 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    BranchHoisting localBranchHoisting;
    for (int i = 0;; localBranchHoisting = this)
    {
      this = i + 1;
      if (this >= 500) {
        break;
      }
    }
    this = localBranchHoisting + localBranchHoisting;
    if (paramInt == 0) {}
    for (paramInt = this;; paramInt = localBranchHoisting + this) {
      return paramInt;
    }
  }
  
  public int expected(int paramInt)
  {
    for (this = 0; this < 500; this = 1 + this) {}
    BranchHoisting localBranchHoisting = this + this;
    if (paramInt == 0) {}
    for (paramInt = localBranchHoisting;; paramInt = localBranchHoisting + this) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 569
      * PEG2PEGTIME 442
      * PBTIME 305
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 4663 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5482
GLPKFormulation: Number of values: 2516
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 70905
      * PEG2PEGTIME 70844
      * PBTIME 66150
      * ENGINETIME 4663
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 2098 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 4113
GLPKFormulation: Number of values: 1916
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 58376
      * PEG2PEGTIME 58354
      * PBTIME 56225
      * ENGINETIME 2099
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 130328 milliseconds
```

### Optimized
```java
22:56:25.989 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    for (this = 0;; this = localBranchHoisting)
    {
      localBranchHoisting = 1 + this;
      if (500 <= localBranchHoisting) {
        break;
      }
    }
    BranchHoisting localBranchHoisting = this + this;
    if (paramInt == 0) {}
    for (paramInt = localBranchHoisting;; paramInt = this + localBranchHoisting) {
      return paramInt;
    }
  }
  
  public int expected(int paramInt)
  {
    if (paramInt == 0)
    {
      for (paramInt = 0; paramInt < 500; paramInt = 1 + paramInt) {}
      paramInt += paramInt;
    }
    for (;;)
    {
      return paramInt;
      for (paramInt = 0; paramInt < 500; paramInt = 1 + paramInt) {}
      paramInt = paramInt + paramInt + paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
