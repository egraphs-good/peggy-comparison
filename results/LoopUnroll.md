# LoopUnroll
## Original
```java
public class LoopUnroll {
    public int original(int n) {
        int i = 0;
        while (i < 1) {
            i++;
        }
        return i;
    }

    public int expected(int n) {
        return 1;
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 579
      * PEG2PEGTIME 439
      * PBTIME 302
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 11
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 353
      * PEG2PEGTIME 323
      * PBTIME 288
      * ENGINETIME 0
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 248
      * PEG2PEGTIME 245
      * PBTIME 229
      * ENGINETIME 0
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 1591 milliseconds
```

### Optimized
```java
22:45:07.227 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 558
      * PEG2PEGTIME 435
      * PBTIME 301
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 11
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 345
      * PEG2PEGTIME 315
      * PBTIME 287
      * ENGINETIME 0
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 2 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 260
      * PEG2PEGTIME 255
      * PBTIME 235
      * ENGINETIME 2
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 1595 milliseconds
```

### Optimized
```java
22:45:11.428 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 549
      * PEG2PEGTIME 428
      * PBTIME 299
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 14 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 19
GLPKFormulation: Number of values: 15
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 461
      * PEG2PEGTIME 430
      * PBTIME 383
      * ENGINETIME 14
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 255
      * PEG2PEGTIME 251
      * PBTIME 236
      * ENGINETIME 1
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 1677 milliseconds
```

### Optimized
```java
22:45:15.656 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    for (paramInt = 0; paramInt < 1; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 562
      * PEG2PEGTIME 440
      * PBTIME 302
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 28 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 28
GLPKFormulation: Number of values: 19
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 570
      * PEG2PEGTIME 540
      * PBTIME 482
      * ENGINETIME 28
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 259
      * PEG2PEGTIME 252
      * PBTIME 230
      * ENGINETIME 1
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 1825 milliseconds
```

### Optimized
```java
22:45:20.071 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 557
      * PEG2PEGTIME 436
      * PBTIME 298
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 50 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 43
GLPKFormulation: Number of values: 24
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 740
      * PEG2PEGTIME 709
      * PBTIME 628
      * ENGINETIME 51
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 254
      * PEG2PEGTIME 249
      * PBTIME 230
      * ENGINETIME 0
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 1970 milliseconds
```

### Optimized
```java
22:45:24.588 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 571
      * PEG2PEGTIME 450
      * PBTIME 308
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 95 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 72
GLPKFormulation: Number of values: 35
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 1154
      * PEG2PEGTIME 1121
      * PBTIME 999
      * ENGINETIME 95
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 252
      * PEG2PEGTIME 247
      * PBTIME 229
      * ENGINETIME 0
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 2391 milliseconds
```

### Optimized
```java
22:45:29.536 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    for (paramInt = 0; paramInt < 1; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 556
      * PEG2PEGTIME 436
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 203 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 113
GLPKFormulation: Number of values: 43
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 1603
      * PEG2PEGTIME 1573
      * PBTIME 1339
      * ENGINETIME 203
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 249
      * PEG2PEGTIME 246
      * PBTIME 226
      * ENGINETIME 1
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 2839 milliseconds
```

### Optimized
```java
22:45:34.988 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    paramInt = 0;
    while (1 > paramInt) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 559
      * PEG2PEGTIME 438
      * PBTIME 299
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 415 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 219
GLPKFormulation: Number of values: 70
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 3095
      * PEG2PEGTIME 3060
      * PBTIME 2612
      * ENGINETIME 415
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 262
      * PEG2PEGTIME 257
      * PBTIME 236
      * ENGINETIME 3
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 4339 milliseconds
```

### Optimized
```java
22:45:41.993 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 563
      * PEG2PEGTIME 441
      * PBTIME 299
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 894 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 425
GLPKFormulation: Number of values: 113
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 5709
      * PEG2PEGTIME 5670
      * PBTIME 4747
      * ENGINETIME 895
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 266
      * PEG2PEGTIME 260
      * PBTIME 236
      * ENGINETIME 3
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 6956 milliseconds
```

### Optimized
```java
22:45:51.523 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    for (paramInt = 0; 1 > paramInt; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 559
      * PEG2PEGTIME 439
      * PBTIME 303
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1434 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 745
GLPKFormulation: Number of values: 126
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 9551
      * PEG2PEGTIME 9514
      * PBTIME 8054
      * ENGINETIME 1434
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 269
      * PEG2PEGTIME 262
      * PBTIME 241
      * ENGINETIME 2
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 10793 milliseconds
```

### Optimized
```java
22:46:04.918 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    for (paramInt = 0; 1 > paramInt; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 577
      * PEG2PEGTIME 446
      * PBTIME 299
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 4937 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1438
GLPKFormulation: Number of values: 179
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 20532
      * PEG2PEGTIME 20496
      * PBTIME 15524
      * ENGINETIME 4938
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 271
      * PEG2PEGTIME 264
      * PBTIME 242
      * ENGINETIME 2
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 21793 milliseconds
```

### Optimized
```java
22:46:29.430 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    for (paramInt = 0; paramInt < 1; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
