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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 2

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 710
      * PEG2PEGTIME 566
      * PBTIME 428
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 954
      * PEG2PEGTIME 880
      * PBTIME 832
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 543
      * PEG2PEGTIME 521
      * PBTIME 502
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
+ Total optimization time = 2728 milliseconds
```

```java
02:16:55.732 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 4

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 777
      * PEG2PEGTIME 441
      * PBTIME 303
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 486
      * PEG2PEGTIME 420
      * PBTIME 367
      * ENGINETIME 1
      * Optimization ratio 695/695 = 1.0
      * PEG-based Optimization ratio 695/695 = 1.0
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 389
      * PEG2PEGTIME 372
      * PBTIME 354
      * ENGINETIME 1
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
+ Total optimization time = 2115 milliseconds
```

```java
02:17:00.519 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 8

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 592
      * PEG2PEGTIME 465
      * PBTIME 318
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 462
      * PEG2PEGTIME 414
      * PBTIME 367
      * ENGINETIME 2
      * Optimization ratio 695/695 = 1.0
      * PEG-based Optimization ratio 695/695 = 1.0
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 14 milliseconds
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 499
      * PEG2PEGTIME 481
      * PBTIME 452
      * ENGINETIME 14
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
+ Total optimization time = 2000 milliseconds
```

```java
02:17:05.141 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 16

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 615
      * PEG2PEGTIME 482
      * PBTIME 324
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 19 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 624
      * PEG2PEGTIME 566
      * PBTIME 508
      * ENGINETIME 19
      * Optimization ratio 695/695 = 1.0
      * PEG-based Optimization ratio 695/695 = 1.0
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 12 milliseconds
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 676
      * PEG2PEGTIME 659
      * PBTIME 630
      * ENGINETIME 12
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
+ Total optimization time = 2466 milliseconds
```

```java
02:17:10.609 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 32

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 638
      * PEG2PEGTIME 486
      * PBTIME 327
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 44 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 831
      * PEG2PEGTIME 780
      * PBTIME 702
      * ENGINETIME 44
      * Optimization ratio 695/695 = 1.0
      * PEG-based Optimization ratio 695/695 = 1.0
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 804
      * PEG2PEGTIME 788
      * PBTIME 744
      * ENGINETIME 26
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
+ Total optimization time = 3066 milliseconds
```

```java
02:17:16.526 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
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
    for (BranchHoisting localBranchHoisting = 2 * this;; localBranchHoisting = 3 * this) {
      return localBranchHoisting;
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 64

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 558
      * PEG2PEGTIME 435
      * PBTIME 304
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 86 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 1446
      * PEG2PEGTIME 1389
      * PBTIME 1272
      * ENGINETIME 86
      * Optimization ratio 575/695 = 0.8273381294964028
      * PEG-based Optimization ratio 575/695 = 0.8273381294964028
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 63 milliseconds
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 1349
      * PEG2PEGTIME 1336
      * PBTIME 1253
      * ENGINETIME 63
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
+ Total optimization time = 3817 milliseconds
```

```java
02:17:23.042 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    if (paramInt == 0)
    {
      this = 0;
      for (paramInt = 0;; paramInt = paramInt)
      {
        paramInt += 1;
        if (500 <= paramInt) {
          break;
        }
        this += 2;
      }
    }
    for (paramInt = this;; paramInt = this)
    {
      return paramInt;
      this = 0;
      for (paramInt = 0;; paramInt = paramInt)
      {
        paramInt += 1;
        if (500 <= paramInt) {
          break;
        }
        this += 3;
      }
    }
  }
  
  public int expected(int paramInt)
  {
    this = 0;
    while (500 > this) {
      this += 1;
    }
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 128

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 644
      * PEG2PEGTIME 499
      * PBTIME 320
      * ENGINETIME 10
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 262 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 2742
      * PEG2PEGTIME 2688
      * PBTIME 2385
      * ENGINETIME 262
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 115 milliseconds
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 2564
      * PEG2PEGTIME 2542
      * PBTIME 2410
      * ENGINETIME 115
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
+ Total optimization time = 6475 milliseconds
```

```java
02:17:32.161 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    BranchHoisting localBranchHoisting;
    for (int i = 0;; localBranchHoisting = this)
    {
      this = 1 + i;
      if (500 <= this) {
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
    this = 0;
    while (500 > this) {
      this += 1;
    }
    BranchHoisting localBranchHoisting = this + this;
    if (paramInt == 0) {
      localBranchHoisting = localBranchHoisting;
    }
    for (;;)
    {
      return localBranchHoisting;
      int i;
      localBranchHoisting += this;
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 256

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 643
      * PEG2PEGTIME 522
      * PBTIME 384
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 377 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 5768
      * PEG2PEGTIME 5717
      * PBTIME 5307
      * ENGINETIME 377
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 255 milliseconds
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 5266
      * PEG2PEGTIME 5250
      * PBTIME 4978
      * ENGINETIME 255
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
+ Total optimization time = 12133 milliseconds
```

```java
02:17:47.035 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    int j;
    for (int i = 0;; j = this)
    {
      this = i + 1;
      if (500 <= this) {
        break;
      }
    }
    this = j + j;
    if (paramInt == 0) {}
    int k;
    for (BranchHoisting localBranchHoisting = this;; k = this + localBranchHoisting) {
      return localBranchHoisting;
    }
  }
  
  public int expected(int paramInt)
  {
    this = 0;
    while (this < 500) {
      this += 1;
    }
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 512

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 579
      * PEG2PEGTIME 456
      * PBTIME 322
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 816 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 11003
      * PEG2PEGTIME 10951
      * PBTIME 10101
      * ENGINETIME 817
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 360 milliseconds
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 10524
      * PEG2PEGTIME 10506
      * PBTIME 10126
      * ENGINETIME 361
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
+ Total optimization time = 22590 milliseconds
```

```java
02:18:12.309 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    int j;
    for (int i = 0;; j = this)
    {
      this = 1 + i;
      if (this >= 500) {
        break;
      }
    }
    this = j + j;
    if (paramInt == 0) {}
    int k;
    for (BranchHoisting localBranchHoisting = this;; k = this + localBranchHoisting) {
      return localBranchHoisting;
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 1024

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 579
      * PEG2PEGTIME 454
      * PBTIME 310
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1392 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 27352
      * PEG2PEGTIME 27293
      * PBTIME 25863
      * ENGINETIME 1392
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 771 milliseconds
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 23082
      * PEG2PEGTIME 23064
      * PBTIME 22270
      * ENGINETIME 772
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
+ Total optimization time = 51479 milliseconds
```

```java
02:19:06.431 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
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
    this = j + j;
    if (paramInt == 0) {}
    int k;
    for (BranchHoisting localBranchHoisting = this;; k = this + localBranchHoisting) {
      return localBranchHoisting;
    }
  }
  
  public int expected(int paramInt)
  {
    this = 0;
    while (500 > this) {
      this += 1;
    }
    BranchHoisting localBranchHoisting = this + this;
    if (paramInt == 0) {
      localBranchHoisting = localBranchHoisting;
    }
    for (;;)
    {
      return localBranchHoisting;
      int i;
      localBranchHoisting += this;
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 2048

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 576
      * PEG2PEGTIME 448
      * PBTIME 310
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 3953 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 70441
      * PEG2PEGTIME 70382
      * PBTIME 66395
      * ENGINETIME 3953
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 1974 milliseconds
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 70300
      * PEG2PEGTIME 70277
      * PBTIME 68275
      * ENGINETIME 1975
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
+ Total optimization time = 141805 milliseconds
```

```java
02:21:31.017 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
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
