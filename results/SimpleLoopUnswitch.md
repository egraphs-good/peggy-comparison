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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 2

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 668
      * PEG2PEGTIME 538
      * PBTIME 408
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
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
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 578
      * PEG2PEGTIME 495
      * PBTIME 448
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
+ Total optimization time = 1708 milliseconds
```

```java
20:19:16.043 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 4

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 608
      * PEG2PEGTIME 477
      * PBTIME 341
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
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
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 576
      * PEG2PEGTIME 505
      * PBTIME 455
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
+ Total optimization time = 1666 milliseconds
```

```java
20:19:20.437 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 8

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 553
      * PEG2PEGTIME 429
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 563
      * PEG2PEGTIME 490
      * PBTIME 446
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
+ Total optimization time = 1583 milliseconds
```

```java
20:19:24.786 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    int i = 0;
    int j = 0;
    if (paramInt < 0)
    {
      while (i < paramInt)
      {
        this = i;
        i += 1;
        j = 2 + 1;
        System.out.println(this);
      }
      return j;
    }
    for (;;)
    {
      if (i < paramInt)
      {
        i += 1;
        System.out.println(i);
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 16

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 589
      * PEG2PEGTIME 449
      * PBTIME 317
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 15 milliseconds
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
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 685
      * PEG2PEGTIME 616
      * PBTIME 562
      * ENGINETIME 15
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
+ Total optimization time = 1730 milliseconds
```

```java
20:19:29.212 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
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
        System.out.println(i);
        this = 2 + 1;
        i = 1 + i;
      }
    }
    while (i < paramInt)
    {
      System.out.println(i);
      this += 1;
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 32

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 564
      * PEG2PEGTIME 441
      * PBTIME 306
      * ENGINETIME 9
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 36 milliseconds
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
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 899
      * PEG2PEGTIME 831
      * PBTIME 755
      * ENGINETIME 36
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
+ Total optimization time = 1907 milliseconds
```

```java
20:19:33.800 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 64

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 565
      * PEG2PEGTIME 438
      * PBTIME 304
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 65 milliseconds
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
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 1353
      * PEG2PEGTIME 1286
      * PBTIME 1190
      * ENGINETIME 65
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
+ Total optimization time = 2368 milliseconds
```

```java
20:19:38.906 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 128

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 569
      * PEG2PEGTIME 443
      * PBTIME 305
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 133 milliseconds
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
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 2012
      * PEG2PEGTIME 1946
      * PBTIME 1782
      * ENGINETIME 133
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
+ Total optimization time = 3037 milliseconds
```

```java
20:19:44.645 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0)
    {
      while (i < paramInt)
      {
        this = i;
        j = 2 + 1;
        i = 1 + i;
        System.out.println(this);
      }
      return j;
    }
    for (;;)
    {
      if (i < paramInt)
      {
        i = 1 + i;
        System.out.println(i);
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 256

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 563
      * PEG2PEGTIME 440
      * PBTIME 304
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 227 milliseconds
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
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 3169
      * PEG2PEGTIME 3090
      * PBTIME 2829
      * ENGINETIME 227
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
+ Total optimization time = 4174 milliseconds
```

```java
20:19:51.648 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
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
        i = 1 + 2;
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 512

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 599
      * PEG2PEGTIME 463
      * PBTIME 315
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 456 milliseconds
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
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 7184
      * PEG2PEGTIME 7111
      * PBTIME 6621
      * ENGINETIME 456
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
+ Total optimization time = 8283 milliseconds
```

```java
20:20:03.036 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
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
