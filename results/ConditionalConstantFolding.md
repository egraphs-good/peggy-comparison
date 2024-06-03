# ConditionalConstantFolding
## Original
```java
public class ConditionalConstantFolding {
    public int original(int x) {
        if (x == 5) {
            return 4 * x;
        } else if (x == 4) {
            return 5 * x;
        } else {
            return 20;
        }
    }

    public int expected() {
        return 20;
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 2 milliseconds
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 502
      * PEG2PEGTIME 423
      * PBTIME 323
      * ENGINETIME 2
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
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
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 314
      * PEG2PEGTIME 286
      * PBTIME 266
      * ENGINETIME 0
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 128
      * PEG2PEGTIME 125
      * PBTIME 116
      * ENGINETIME 0
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1229 milliseconds
```

```java
20:16:33.683 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 438
      * PEG2PEGTIME 358
      * PBTIME 274
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
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
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 313
      * PEG2PEGTIME 282
      * PBTIME 264
      * ENGINETIME 0
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 2 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 258
      * PEG2PEGTIME 253
      * PBTIME 242
      * ENGINETIME 2
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1293 milliseconds
```

```java
20:16:36.755 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 3 milliseconds
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 441
      * PEG2PEGTIME 362
      * PBTIME 270
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
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
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 311
      * PEG2PEGTIME 286
      * PBTIME 262
      * ENGINETIME 1
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 130
      * PEG2PEGTIME 127
      * PBTIME 118
      * ENGINETIME 1
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1175 milliseconds
```

```java
20:16:39.614 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 453
      * PEG2PEGTIME 372
      * PBTIME 279
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 10 milliseconds
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
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 325
      * PEG2PEGTIME 295
      * PBTIME 270
      * ENGINETIME 10
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 241
      * PEG2PEGTIME 238
      * PBTIME 225
      * ENGINETIME 0
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1286 milliseconds
```

```java
20:16:42.674 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 476
      * PEG2PEGTIME 395
      * PBTIME 282
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
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
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 374
      * PEG2PEGTIME 345
      * PBTIME 284
      * ENGINETIME 45
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 134
      * PEG2PEGTIME 133
      * PBTIME 120
      * ENGINETIME 0
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1304 milliseconds
```

```java
20:16:45.796 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 447
      * PEG2PEGTIME 369
      * PBTIME 278
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
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
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 329
      * PEG2PEGTIME 304
      * PBTIME 271
      * ENGINETIME 10
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 233
      * PEG2PEGTIME 232
      * PBTIME 220
      * ENGINETIME 0
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1287 milliseconds
```

```java
20:16:48.967 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 467
      * PEG2PEGTIME 379
      * PBTIME 284
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
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
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 393
      * PEG2PEGTIME 366
      * PBTIME 332
      * ENGINETIME 10
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 239
      * PEG2PEGTIME 237
      * PBTIME 225
      * ENGINETIME 0
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1384 milliseconds
```

```java
20:16:52.144 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 476
      * PEG2PEGTIME 376
      * PBTIME 282
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
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
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 347
      * PEG2PEGTIME 320
      * PBTIME 288
      * ENGINETIME 11
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 233
      * PEG2PEGTIME 230
      * PBTIME 219
      * ENGINETIME 0
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1321 milliseconds
```

```java
20:16:55.346 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 493
      * PEG2PEGTIME 370
      * PBTIME 266
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
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
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 324
      * PEG2PEGTIME 296
      * PBTIME 263
      * ENGINETIME 11
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 234
      * PEG2PEGTIME 232
      * PBTIME 219
      * ENGINETIME 1
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1335 milliseconds
```

```java
20:16:58.437 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 446
      * PEG2PEGTIME 366
      * PBTIME 278
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
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
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 317
      * PEG2PEGTIME 290
      * PBTIME 264
      * ENGINETIME 10
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 135
      * PEG2PEGTIME 134
      * PBTIME 121
      * ENGINETIME 0
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1162 milliseconds
```

```java
20:17:01.364 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 466
      * PEG2PEGTIME 380
      * PBTIME 286
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
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
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 325
      * PEG2PEGTIME 299
      * PBTIME 267
      * ENGINETIME 11
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 132
      * PEG2PEGTIME 130
      * PBTIME 117
      * ENGINETIME 1
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1196 milliseconds
```

```java
20:17:04.331 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
