# SimpleLoopUnswitch
## Original
```java
public class SimpleLoopUnswitch {
    public int original(int n) {
        int j = 0;
        for (int i = 0; i < n; i++) {
            System.out.println(i);
            if (n < 0) {
                j = 2;
            }
            j++;
        }
        return j;
    }

    // This causes the encoding of output CFG to never terminate
    // public int expected(int n) {
    //     int j = 0;
    //     if (n < 0) {
    //         for (int i = 0; i < n; i++) {
    //             System.out.println(i);
    //             j = 2;
    //             j++;
    //         }
    //     } else {
    //         for (int i = 0; i < n; i++) {
    //             System.out.println(i);
    //             j++;
    //         }
    //     }
    //     return j;
    // }
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
+ Loading class file SimpleLoopUnswitch
+ Optimizing class SimpleLoopUnswitch
   - Processing method <SimpleLoopUnswitch: void <init>()>
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
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 554
      * PEG2PEGTIME 433
      * PBTIME 304
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 27
GLPKFormulation: Number of values: 27
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 562
      * PEG2PEGTIME 490
      * PBTIME 454
      * ENGINETIME 1
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <SimpleLoopUnswitch: int original(int)>
+ Done optimizing SimpleLoopUnswitch
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <SimpleLoopUnswitch: void <init>()>
+ Fixing bytecode of method <SimpleLoopUnswitch: int original(int)>
+ Writing class back to optimized/SimpleLoopUnswitch.class
+ Total optimization time = 1560 milliseconds
```

### Optimized
```java
22:38:56.245 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    this = 0;
    int i = 0;
    if (paramInt < 0) {
      while (this < paramInt)
      {
        this += 1;
        System.out.println(this);
        i = 2 + 1;
      }
    }
    while (this < paramInt)
    {
      this += 1;
      System.out.println(this);
      i += 1;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     SimpleLoopUnswitch
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
+ Loading class file SimpleLoopUnswitch
+ Optimizing class SimpleLoopUnswitch
   - Processing method <SimpleLoopUnswitch: void <init>()>
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
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 551
      * PEG2PEGTIME 428
      * PBTIME 299
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 27
GLPKFormulation: Number of values: 27
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 548
      * PEG2PEGTIME 477
      * PBTIME 441
      * ENGINETIME 1
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <SimpleLoopUnswitch: int original(int)>
+ Done optimizing SimpleLoopUnswitch
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <SimpleLoopUnswitch: void <init>()>
+ Fixing bytecode of method <SimpleLoopUnswitch: int original(int)>
+ Writing class back to optimized/SimpleLoopUnswitch.class
+ Total optimization time = 1530 milliseconds
```

### Optimized
```java
22:39:00.455 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    this = 0;
    int i = 0;
    if (paramInt < 0) {
      while (this < paramInt)
      {
        this += 1;
        System.out.println(this);
        i = 2 + 1;
      }
    }
    while (this < paramInt)
    {
      this += 1;
      System.out.println(this);
      i += 1;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     SimpleLoopUnswitch
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
+ Loading class file SimpleLoopUnswitch
+ Optimizing class SimpleLoopUnswitch
   - Processing method <SimpleLoopUnswitch: void <init>()>
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
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 564
      * PEG2PEGTIME 438
      * PBTIME 306
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 2 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 27
GLPKFormulation: Number of values: 27
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 552
      * PEG2PEGTIME 483
      * PBTIME 443
      * ENGINETIME 2
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <SimpleLoopUnswitch: int original(int)>
+ Done optimizing SimpleLoopUnswitch
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <SimpleLoopUnswitch: void <init>()>
+ Fixing bytecode of method <SimpleLoopUnswitch: int original(int)>
+ Writing class back to optimized/SimpleLoopUnswitch.class
+ Total optimization time = 1549 milliseconds
```

### Optimized
```java
22:39:04.636 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    this = 0;
    int i = 0;
    if (paramInt < 0) {
      while (i < paramInt)
      {
        int j = i;
        this = 2 + 1;
        i += 1;
        System.out.println(j);
      }
    }
    while (i < paramInt)
    {
      this += 1;
      System.out.println(i);
      i += 1;
    }
    return this;
  }
}

/* Location:
 * Qualified Name:     SimpleLoopUnswitch
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
+ Loading class file SimpleLoopUnswitch
+ Optimizing class SimpleLoopUnswitch
   - Processing method <SimpleLoopUnswitch: void <init>()>
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
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 543
      * PEG2PEGTIME 421
      * PBTIME 297
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 14 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 37
GLPKFormulation: Number of values: 33
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 669
      * PEG2PEGTIME 600
      * PBTIME 552
      * ENGINETIME 14
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <SimpleLoopUnswitch: int original(int)>
+ Done optimizing SimpleLoopUnswitch
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <SimpleLoopUnswitch: void <init>()>
+ Fixing bytecode of method <SimpleLoopUnswitch: int original(int)>
+ Writing class back to optimized/SimpleLoopUnswitch.class
+ Total optimization time = 1678 milliseconds
```

### Optimized
```java
22:39:08.956 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    int i = 0;
    this = 0;
    if (paramInt < 0)
    {
      while (i < paramInt)
      {
        System.out.println(i);
        i = 1 + i;
        this = 2 + 1;
      }
      return this;
    }
    for (;;)
    {
      if (i < paramInt)
      {
        System.out.println(i);
        i = 1 + i;
      }
    }
  }
}

/* Location:
 * Qualified Name:     SimpleLoopUnswitch
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
+ Loading class file SimpleLoopUnswitch
+ Optimizing class SimpleLoopUnswitch
   - Processing method <SimpleLoopUnswitch: void <init>()>
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
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 552
      * PEG2PEGTIME 435
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 37 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 55
GLPKFormulation: Number of values: 41
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 893
      * PEG2PEGTIME 829
      * PBTIME 757
      * ENGINETIME 37
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <SimpleLoopUnswitch: int original(int)>
+ Done optimizing SimpleLoopUnswitch
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <SimpleLoopUnswitch: void <init>()>
+ Fixing bytecode of method <SimpleLoopUnswitch: int original(int)>
+ Writing class back to optimized/SimpleLoopUnswitch.class
+ Total optimization time = 1903 milliseconds
```

### Optimized
```java
22:39:13.477 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    int i = 0;
    int j = 0;
    if (paramInt < 0)
    {
      while (paramInt > j)
      {
        this = j;
        i = 2 + 1;
        j += 1;
        System.out.println(this);
      }
      return i;
    }
    for (;;)
    {
      if (paramInt > j)
      {
        j += 1;
        System.out.println(j);
      }
    }
  }
}

/* Location:
 * Qualified Name:     SimpleLoopUnswitch
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
+ Loading class file SimpleLoopUnswitch
+ Optimizing class SimpleLoopUnswitch
   - Processing method <SimpleLoopUnswitch: void <init>()>
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
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 549
      * PEG2PEGTIME 429
      * PBTIME 298
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 66 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 86
GLPKFormulation: Number of values: 74
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 1396
      * PEG2PEGTIME 1328
      * PBTIME 1224
      * ENGINETIME 66
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <SimpleLoopUnswitch: int original(int)>
+ Done optimizing SimpleLoopUnswitch
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <SimpleLoopUnswitch: void <init>()>
+ Fixing bytecode of method <SimpleLoopUnswitch: int original(int)>
+ Writing class back to optimized/SimpleLoopUnswitch.class
+ Total optimization time = 2371 milliseconds
```

### Optimized
```java
22:39:18.485 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    int i = 0;
    this = 0;
    if (paramInt < 0)
    {
      while (paramInt > i)
      {
        System.out.println(i);
        i += 1;
        this = 2 + 1;
      }
      return this;
    }
    for (;;)
    {
      if (paramInt > i)
      {
        System.out.println(i);
        i += 1;
      }
    }
  }
}

/* Location:
 * Qualified Name:     SimpleLoopUnswitch
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
+ Loading class file SimpleLoopUnswitch
+ Optimizing class SimpleLoopUnswitch
   - Processing method <SimpleLoopUnswitch: void <init>()>
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
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 548
      * PEG2PEGTIME 427
      * PBTIME 298
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 122 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 129
GLPKFormulation: Number of values: 103
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 1967
      * PEG2PEGTIME 1903
      * PBTIME 1743
      * ENGINETIME 122
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <SimpleLoopUnswitch: int original(int)>
+ Done optimizing SimpleLoopUnswitch
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <SimpleLoopUnswitch: void <init>()>
+ Fixing bytecode of method <SimpleLoopUnswitch: int original(int)>
+ Writing class back to optimized/SimpleLoopUnswitch.class
+ Total optimization time = 2979 milliseconds
```

### Optimized
```java
22:39:24.070 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    this = 0;
    int i = 0;
    if (paramInt < 0) {
      while (this < paramInt)
      {
        System.out.println(this);
        this = 1 + this;
        i = 2 + 1;
      }
    }
    while (this < paramInt)
    {
      System.out.println(this);
      this = 1 + this;
      i += 1;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     SimpleLoopUnswitch
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
+ Loading class file SimpleLoopUnswitch
+ Optimizing class SimpleLoopUnswitch
   - Processing method <SimpleLoopUnswitch: void <init>()>
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
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 559
      * PEG2PEGTIME 440
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 222 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 213
GLPKFormulation: Number of values: 157
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 3097
      * PEG2PEGTIME 3033
      * PBTIME 2776
      * ENGINETIME 222
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <SimpleLoopUnswitch: int original(int)>
+ Done optimizing SimpleLoopUnswitch
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <SimpleLoopUnswitch: void <init>()>
+ Fixing bytecode of method <SimpleLoopUnswitch: int original(int)>
+ Writing class back to optimized/SimpleLoopUnswitch.class
+ Total optimization time = 4110 milliseconds
```

### Optimized
```java
22:39:30.826 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (paramInt > j)
      {
        this = j;
        j += 1;
        i = 1 + 2;
        System.out.println(this);
      }
    }
    while (paramInt > j)
    {
      j += 1;
      i = 1 + i;
      System.out.println(j);
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     SimpleLoopUnswitch
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
+ Loading class file SimpleLoopUnswitch
+ Optimizing class SimpleLoopUnswitch
   - Processing method <SimpleLoopUnswitch: void <init>()>
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
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 552
      * PEG2PEGTIME 429
      * PBTIME 298
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 407 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 457
GLPKFormulation: Number of values: 318
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 6993
      * PEG2PEGTIME 6927
      * PBTIME 6485
      * ENGINETIME 407
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <SimpleLoopUnswitch: int original(int)>
+ Done optimizing SimpleLoopUnswitch
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <SimpleLoopUnswitch: void <init>()>
+ Fixing bytecode of method <SimpleLoopUnswitch: int original(int)>
+ Writing class back to optimized/SimpleLoopUnswitch.class
+ Total optimization time = 7998 milliseconds
```

### Optimized
```java
22:39:41.457 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    this = 0;
    int i = 0;
    if (paramInt < 0) {
      while (paramInt > i)
      {
        System.out.println(i);
        this = 2 + 1;
        i = 1 + i;
      }
    }
    while (paramInt > i)
    {
      this += 1;
      System.out.println(i);
      i = 1 + i;
    }
    return this;
  }
}

/* Location:
 * Qualified Name:     SimpleLoopUnswitch
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
+ Loading class file SimpleLoopUnswitch
+ Optimizing class SimpleLoopUnswitch
   - Processing method <SimpleLoopUnswitch: void <init>()>
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
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 543
      * PEG2PEGTIME 425
      * PBTIME 303
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 808 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1115
GLPKFormulation: Number of values: 744
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 16597
      * PEG2PEGTIME 16530
      * PBTIME 15687
      * ENGINETIME 808
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <SimpleLoopUnswitch: int original(int)>
+ Done optimizing SimpleLoopUnswitch
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <SimpleLoopUnswitch: void <init>()>
+ Fixing bytecode of method <SimpleLoopUnswitch: int original(int)>
+ Writing class back to optimized/SimpleLoopUnswitch.class
+ Total optimization time = 17569 milliseconds
```

### Optimized
```java
22:40:01.643 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    int i = 0;
    this = 0;
    if (paramInt < 0) {
      while (i < paramInt)
      {
        i += 1;
        System.out.println(i);
        this = 1 + 2;
      }
    }
    while (i < paramInt)
    {
      i += 1;
      System.out.println(i);
      this = 1 + this;
    }
    return this;
  }
}

/* Location:
 * Qualified Name:     SimpleLoopUnswitch
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
+ Loading class file SimpleLoopUnswitch
+ Optimizing class SimpleLoopUnswitch
   - Processing method <SimpleLoopUnswitch: void <init>()>
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
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 556
      * PEG2PEGTIME 433
      * PBTIME 302
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 3410 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 2618
GLPKFormulation: Number of values: 1754
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 42011
      * PEG2PEGTIME 41935
      * PBTIME 38487
      * ENGINETIME 3411
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <SimpleLoopUnswitch: int original(int)>
+ Done optimizing SimpleLoopUnswitch
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <SimpleLoopUnswitch: void <init>()>
+ Fixing bytecode of method <SimpleLoopUnswitch: int original(int)>
+ Writing class back to optimized/SimpleLoopUnswitch.class
+ Total optimization time = 43035 milliseconds
```

### Optimized
```java
22:40:47.434 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    int i = 0;
    this = 0;
    if (paramInt < 0)
    {
      while (this < paramInt)
      {
        i = 1 + 2;
        System.out.println(this);
        this += 1;
      }
      return i;
    }
    for (;;)
    {
      if (this < paramInt)
      {
        this += 1;
        System.out.println(this);
      }
    }
  }
}

/* Location:
 * Qualified Name:     SimpleLoopUnswitch
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
