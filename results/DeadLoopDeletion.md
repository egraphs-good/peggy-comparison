# DeadLoopDeletion
## Original
```java
public class DeadLoopDeletion {
    public int original() {
        int j = 3;
        for (int i = 0; i < 4; i++) {
            j++;
        }
        j = 2;
        return j;
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 486
      * PEG2PEGTIME 408
      * PBTIME 317
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
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
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 239
      * PEG2PEGTIME 232
      * PBTIME 220
      * ENGINETIME 0
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1001 milliseconds
```

```java
20:17:07.845 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 463
      * PEG2PEGTIME 378
      * PBTIME 282
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 4 milliseconds
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
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 478
      * PEG2PEGTIME 463
      * PBTIME 444
      * ENGINETIME 4
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1395 milliseconds
```

```java
20:17:11.279 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 520
      * PEG2PEGTIME 423
      * PBTIME 304
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
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
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 251
      * PEG2PEGTIME 243
      * PBTIME 225
      * ENGINETIME 1
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1106 milliseconds
```

```java
20:17:14.636 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 488
      * PEG2PEGTIME 382
      * PBTIME 281
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
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
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 250
      * PEG2PEGTIME 242
      * PBTIME 224
      * ENGINETIME 0
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1003 milliseconds
```

```java
20:17:17.618 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 475
      * PEG2PEGTIME 394
      * PBTIME 294
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
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
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 269
      * PEG2PEGTIME 259
      * PBTIME 242
      * ENGINETIME 0
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1153 milliseconds
```

```java
20:17:20.687 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 449
      * PEG2PEGTIME 370
      * PBTIME 279
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
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
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 250
      * PEG2PEGTIME 243
      * PBTIME 231
      * ENGINETIME 0
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1023 milliseconds
```

```java
20:17:23.566 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 448
      * PEG2PEGTIME 364
      * PBTIME 270
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
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
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 143
      * PEG2PEGTIME 133
      * PBTIME 119
      * ENGINETIME 1
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 859 milliseconds
```

```java
20:17:26.274 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 466
      * PEG2PEGTIME 387
      * PBTIME 281
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
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
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 237
      * PEG2PEGTIME 231
      * PBTIME 219
      * ENGINETIME 0
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 973 milliseconds
```

```java
20:32:42.023 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 520
      * PEG2PEGTIME 440
      * PBTIME 326
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
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
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 244
      * PEG2PEGTIME 238
      * PBTIME 225
      * ENGINETIME 1
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1012 milliseconds
```

```java
20:32:44.920 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 510
      * PEG2PEGTIME 399
      * PBTIME 283
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
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
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 246
      * PEG2PEGTIME 239
      * PBTIME 225
      * ENGINETIME 1
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1014 milliseconds
```

```java
20:32:47.692 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 442
      * PEG2PEGTIME 359
      * PBTIME 271
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
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
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 253
      * PEG2PEGTIME 245
      * PBTIME 221
      * ENGINETIME 0
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 956 milliseconds
```

```java
20:32:50.370 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
