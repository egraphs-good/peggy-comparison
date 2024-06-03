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
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 434
      * PEG2PEGTIME 355
      * PBTIME 274
      * ENGINETIME 3
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 448
      * PEG2PEGTIME 400
      * PBTIME 370
      * ENGINETIME 0
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
+ Total optimization time = 1176 milliseconds
```

```java
19:27:12.045 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
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
        this = 2 + 1;
        System.out.println(i);
        i += 1;
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
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 438
      * PEG2PEGTIME 354
      * PBTIME 274
      * ENGINETIME 3
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
      * Optimization took 442
      * PEG2PEGTIME 395
      * PBTIME 372
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
+ Total optimization time = 1237 milliseconds
```

```java
19:27:15.041 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
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
        this += 1;
        i = 2 + 1;
      }
    }
    while (this < paramInt)
    {
      System.out.println(this);
      this += 1;
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
      * Optimization of method <SimpleLoopUnswitch: void <init>()> SUCCESSFUL
      * Optimization took 516
      * PEG2PEGTIME 365
      * PBTIME 279
      * ENGINETIME 4
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
      * Optimization took 447
      * PEG2PEGTIME 396
      * PBTIME 369
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
+ Total optimization time = 1259 milliseconds
```

```java
19:27:18.044 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    this = 0;
    int i = 0;
    if (paramInt < 0)
    {
      while (this < paramInt)
      {
        int j = this;
        this += 1;
        i = 2 + 1;
        System.out.println(j);
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
      * Optimization took 459
      * PEG2PEGTIME 379
      * PBTIME 291
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
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
      * Optimization of method <SimpleLoopUnswitch: int original(int)> SUCCESSFUL
      * Optimization took 534
      * PEG2PEGTIME 489
      * PBTIME 454
      * ENGINETIME 8
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
+ Total optimization time = 1305 milliseconds
```

```java
19:27:21.183 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (i < paramInt)
      {
        this = i;
        j = 2 + 1;
        i += 1;
        System.out.println(this);
      }
    }
    while (i < paramInt)
    {
      j += 1;
      System.out.println(i);
      i += 1;
    }
    return j;
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
      * Optimization took 499
      * PEG2PEGTIME 420
      * PBTIME 339
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 22 milliseconds
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
      * Optimization took 691
      * PEG2PEGTIME 647
      * PBTIME 594
      * ENGINETIME 22
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
+ Total optimization time = 1479 milliseconds
```

```java
19:27:24.373 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    this = 0;
    int i = 0;
    if (paramInt < 0)
    {
      while (paramInt > this)
      {
        this = 1 + this;
        System.out.println(this);
        i = 1 + 2;
      }
      return i;
    }
    for (;;)
    {
      if (paramInt > this)
      {
        System.out.println(this);
        this = 1 + this;
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
      * Optimization took 488
      * PEG2PEGTIME 410
      * PBTIME 329
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 39 milliseconds
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
      * Optimization took 993
      * PEG2PEGTIME 951
      * PBTIME 890
      * ENGINETIME 40
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
+ Total optimization time = 1773 milliseconds
```

```java
19:27:27.972 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
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
        i += 1;
        System.out.println(i);
        this = 1 + 2;
      }
      return this;
    }
    for (;;)
    {
      if (i < paramInt)
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
      * Optimization took 456
      * PEG2PEGTIME 377
      * PBTIME 290
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 76 milliseconds
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
      * Optimization took 1341
      * PEG2PEGTIME 1298
      * PBTIME 1196
      * ENGINETIME 76
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
+ Total optimization time = 2104 milliseconds
```

```java
19:27:31.937 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    int i = 0;
    this = 0;
    if (paramInt < 0) {
      while (paramInt > i)
      {
        i = 1 + i;
        System.out.println(i);
        this = 2 + 1;
      }
    }
    while (paramInt > i)
    {
      i = 1 + i;
      System.out.println(i);
      this += 1;
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
      * Optimization took 458
      * PEG2PEGTIME 377
      * PBTIME 278
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 138 milliseconds
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
      * Optimization took 2054
      * PEG2PEGTIME 2011
      * PBTIME 1849
      * ENGINETIME 138
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
+ Total optimization time = 2859 milliseconds
```

```java
19:27:36.519 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
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
      * Optimization took 474
      * PEG2PEGTIME 376
      * PBTIME 280
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 302 milliseconds
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
      * Optimization took 4849
      * PEG2PEGTIME 4796
      * PBTIME 4465
      * ENGINETIME 302
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
+ Total optimization time = 5624 milliseconds
```

```java
19:27:44.046 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
import java.io.PrintStream;

public class SimpleLoopUnswitch
{
  public int original(int paramInt)
  {
    int i = 0;
    this = 0;
    if (paramInt < 0) {
      while (paramInt > i)
      {
        int j = i;
        i = 1 + i;
        this = 1 + 2;
        System.out.println(j);
      }
    }
    while (paramInt > i)
    {
      i = 1 + i;
      this = 1 + this;
      System.out.println(i);
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 1024

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
      * Optimization took 428
      * PEG2PEGTIME 351
      * PBTIME 271
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 460 milliseconds
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
      * Optimization took 11302
      * PEG2PEGTIME 11260
      * PBTIME 10771
      * ENGINETIME 461
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
+ Total optimization time = 12009 milliseconds
```

```java
19:27:57.807 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
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
        i = 1 + i;
        System.out.println(i);
        this = 1 + 2;
      }
      return this;
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 2048

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
      * Optimization took 445
      * PEG2PEGTIME 368
      * PBTIME 279
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SimpleLoopUnswitch: void <init>()>
   - Processing method <SimpleLoopUnswitch: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 2204 milliseconds
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
      * Optimization took 29100
      * PEG2PEGTIME 29052
      * PBTIME 26823
      * ENGINETIME 2204
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
+ Total optimization time = 29842 milliseconds
```

```java
19:28:29.635 INFO  jd.cli.Main - Decompiling optimized/SimpleLoopUnswitch.class
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
        i = 2 + 1;
        System.out.println(this);
        this += 1;
      }
      return i;
    }
    for (;;)
    {
      if (this < paramInt)
      {
        System.out.println(this);
        this += 1;
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
