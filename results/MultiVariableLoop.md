# MultiVariableLoop
## Original
```java
public class MultiVariableLoop {
    public int original(int n) {
        for (int i = 0, j = 4; i < 10 & j < 2; i++, j++) {
            System.out.println(i);
            System.out.println(j);
            System.out.println(i + j);
            n += 2;
        }
        return n;
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 553
      * PEG2PEGTIME 430
      * PBTIME 305
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 2 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 47
GLPKFormulation: Number of values: 47
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 812
      * PEG2PEGTIME 741
      * PBTIME 693
      * ENGINETIME 2
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 1836 milliseconds
```

### Optimized
```java
22:56:31.733 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    this = 0;
    int j = 4;
    int k = paramInt;
    if (j < 2) {}
    for (paramInt = 1;; paramInt = 0)
    {
      if (this < 10) {}
      for (int i = 1;; i = 0)
      {
        if ((i & paramInt) != 0)
        {
          paramInt = this;
          i = j;
          this += 1;
          j += 1;
          k += 2;
          System.out.println(paramInt);
          System.out.println(i);
          System.out.println(paramInt + i);
          break;
        }
        return k;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 558
      * PEG2PEGTIME 435
      * PBTIME 306
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 2 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 47
GLPKFormulation: Number of values: 47
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 778
      * PEG2PEGTIME 704
      * PBTIME 660
      * ENGINETIME 2
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 1797 milliseconds
```

### Optimized
```java
22:56:36.206 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    int j = 4;
    int k = paramInt;
    paramInt = 0;
    if (paramInt < 10) {}
    for (this = 1;; this = 0)
    {
      if (j < 2) {}
      int i;
      for (MultiVariableLoop localMultiVariableLoop = 1;; i = 0)
      {
        if ((this & localMultiVariableLoop) != 0)
        {
          System.out.println(paramInt);
          System.out.println(j);
          System.out.println(paramInt + j);
          j += 1;
          k += 2;
          paramInt += 1;
          break;
        }
        return k;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 5 milliseconds
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 586
      * PEG2PEGTIME 439
      * PBTIME 301
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 12 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 51
GLPKFormulation: Number of values: 49
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 877
      * PEG2PEGTIME 799
      * PBTIME 738
      * ENGINETIME 12
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 1921 milliseconds
```

### Optimized
```java
22:56:40.780 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    this = 4;
    MultiVariableLoop localMultiVariableLoop = 0;
    int i = paramInt;
    if (this < 2) {}
    for (int k = 1;; k = 0)
    {
      if (localMultiVariableLoop < 10) {}
      for (paramInt = 1;; paramInt = 0)
      {
        if ((paramInt & k) != 0)
        {
          System.out.println(localMultiVariableLoop);
          System.out.println(this);
          System.out.println(localMultiVariableLoop + this);
          this += 1;
          int j;
          localMultiVariableLoop += 1;
          i += 2;
          break;
        }
        return i;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 548
      * PEG2PEGTIME 426
      * PBTIME 303
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 13 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 54
GLPKFormulation: Number of values: 51
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 846
      * PEG2PEGTIME 776
      * PBTIME 726
      * ENGINETIME 13
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 1846 milliseconds
```

### Optimized
```java
22:56:45.308 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    this = 4;
    MultiVariableLoop localMultiVariableLoop = 0;
    int i = paramInt;
    if (this < 2) {}
    for (int k = 1;; k = 0)
    {
      if (localMultiVariableLoop < 10) {}
      for (paramInt = 1;; paramInt = 0)
      {
        if ((paramInt & k) != 0)
        {
          System.out.println(localMultiVariableLoop);
          System.out.println(this);
          System.out.println(localMultiVariableLoop + this);
          this += 1;
          int j = 1 + localMultiVariableLoop;
          i += 2;
          break;
        }
        return i;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 547
      * PEG2PEGTIME 430
      * PBTIME 302
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 30 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 74
GLPKFormulation: Number of values: 69
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 1241
      * PEG2PEGTIME 1167
      * PBTIME 1097
      * ENGINETIME 30
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 2242 milliseconds
```

### Optimized
```java
22:56:50.190 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    int i = paramInt;
    paramInt = 4;
    this = 0;
    if (10 > this) {}
    for (int k = 1;; k = 0)
    {
      if (paramInt < 2) {}
      for (int j = 1;; j = 0)
      {
        if ((j & k) != 0)
        {
          System.out.println(this);
          System.out.println(paramInt);
          System.out.println(this + paramInt);
          i += 2;
          paramInt = 1 + paramInt;
          this = 1 + this;
          break;
        }
        return i;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 553
      * PEG2PEGTIME 423
      * PBTIME 298
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 61 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 101
GLPKFormulation: Number of values: 89
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 1513
      * PEG2PEGTIME 1444
      * PBTIME 1343
      * ENGINETIME 61
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 2521 milliseconds
```

### Optimized
```java
22:56:55.378 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    int k = paramInt;
    paramInt = 4;
    this = 0;
    if (this < 10) {}
    for (int i = 1;; i = 0)
    {
      if (paramInt < 2) {}
      for (int j = 1;; j = 0)
      {
        if ((j & i) != 0)
        {
          k += 2;
          System.out.println(this);
          System.out.println(paramInt);
          System.out.println(this + paramInt);
          paramInt += 1;
          this += 1;
          break;
        }
        return k;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 544
      * PEG2PEGTIME 425
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 145 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 171
GLPKFormulation: Number of values: 150
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 2399
      * PEG2PEGTIME 2326
      * PBTIME 2147
      * ENGINETIME 145
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 3392 milliseconds
```

### Optimized
```java
22:57:01.453 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    paramInt = paramInt;
    this = 0;
    MultiVariableLoop localMultiVariableLoop = 4;
    if (localMultiVariableLoop < 2) {}
    for (int k = 1;; k = 0)
    {
      if (10 > this) {}
      for (int j = 1;; j = 0)
      {
        if ((k & j) != 0)
        {
          paramInt += 2;
          System.out.println(this);
          System.out.println(localMultiVariableLoop);
          System.out.println(localMultiVariableLoop + this);
          this += 1;
          int i;
          localMultiVariableLoop += 1;
          break;
        }
        return paramInt;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 561
      * PEG2PEGTIME 438
      * PBTIME 303
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 304 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 355
GLPKFormulation: Number of values: 234
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 4688
      * PEG2PEGTIME 4616
      * PBTIME 4268
      * ENGINETIME 304
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 5729 milliseconds
```

### Optimized
```java
22:57:09.908 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    int i = paramInt;
    MultiVariableLoop localMultiVariableLoop = 4;
    this = 0;
    if (10 > this) {}
    for (paramInt = 1;; paramInt = 0)
    {
      if (2 > localMultiVariableLoop) {}
      for (int j = 1;; j = 0)
      {
        if ((paramInt & j) != 0)
        {
          System.out.println(this);
          System.out.println(localMultiVariableLoop);
          System.out.println(localMultiVariableLoop + this);
          i += 2;
          int k = 1 + localMultiVariableLoop;
          this += 1;
          break;
        }
        return i;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 555
      * PEG2PEGTIME 434
      * PBTIME 301
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 633 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 604
GLPKFormulation: Number of values: 364
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 7977
      * PEG2PEGTIME 7899
      * PBTIME 7223
      * ENGINETIME 634
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 9024 milliseconds
```

### Optimized
```java
22:57:21.753 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    this = 0;
    int m = 4;
    paramInt = paramInt;
    if (10 > this) {}
    int k;
    for (int i = 1;; k = 0)
    {
      if (m < 2) {}
      int i1;
      for (int n = 1;; i1 = 0)
      {
        if ((n & i) != 0)
        {
          int j = this;
          n = m;
          this = 1 + this;
          m += 1;
          System.out.println(j);
          System.out.println(n);
          System.out.println(j + n);
          paramInt = 2 + paramInt;
          break;
        }
        return paramInt;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 577
      * PEG2PEGTIME 432
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 997 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1173
GLPKFormulation: Number of values: 654
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 15408
      * PEG2PEGTIME 15332
      * PBTIME 14298
      * ENGINETIME 998
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 16446 milliseconds
```

### Optimized
```java
22:57:40.865 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    int j = paramInt;
    MultiVariableLoop localMultiVariableLoop1 = 0;
    paramInt = 4;
    if (10 > localMultiVariableLoop1) {}
    for (this = 1;; this = 0)
    {
      if (2 > paramInt) {}
      int i;
      for (MultiVariableLoop localMultiVariableLoop2 = 1;; i = 0)
      {
        if ((this & localMultiVariableLoop2) != 0)
        {
          this = localMultiVariableLoop1;
          MultiVariableLoop localMultiVariableLoop3 = paramInt;
          j += 2;
          localMultiVariableLoop1 = 1 + localMultiVariableLoop1;
          paramInt += 1;
          System.out.println(this);
          System.out.println(localMultiVariableLoop3);
          System.out.println(this + localMultiVariableLoop3);
          break;
        }
        return j;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 545
      * PEG2PEGTIME 425
      * PBTIME 303
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 5008 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 2782
GLPKFormulation: Number of values: 1357
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 41257
      * PEG2PEGTIME 41175
      * PBTIME 36133
      * ENGINETIME 5008
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 42265 milliseconds
```

### Optimized
```java
22:58:25.839 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    this = paramInt;
    int i = 4;
    int k = 0;
    if (10 > k) {}
    for (paramInt = 1;; paramInt = 0)
    {
      if (2 > i) {}
      for (int j = 1;; j = 0)
      {
        if ((paramInt & j) != 0)
        {
          System.out.println(k);
          System.out.println(i);
          System.out.println(i + k);
          this += 2;
          i = 1 + i;
          k += 1;
          break;
        }
        return this;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
