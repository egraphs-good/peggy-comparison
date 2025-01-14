# Benchmark
## Original
```java
public class Benchmark {

    public static int branchHoistingOriginal(int n) {
        int y = 0;
        int x = 0;
        while (y < 500) {
            y++;
            if (n == 0) {
                x = y * 2;
            } else {
                x = y * 3;
            }
        }
        return x;
    }

    public static int branchHoistingExpected(int n) {
        int y = 0;
        int x = 0;
        while (y < 500)
            y++;

        if (n == 0) {
            x = y * 2;
        } else {
            x = y * 3;
        }

        return x;
    }

    public static int conditionalConstantFoldingOriginal(int x) {
        if (x == 5) {
            return 4 * x;
        } else if (x == 4) {
            return 5 * x;
        } else {
            return 20;
        }
    }

    public static int conditionalConstantFoldingExpected() {
        return 20;
    }

    public static int constantFoldOriginal() {
        int j = 1 + 1;
        int k = j * 3;
        return k - 10;
    }

    public static int deadLoopDeletionOriginal() {
        int j = 3;
        for (int i = 0; i < 4; i++) {
            j++;
        }
        j = 2;
        return j;
    }

    public static int functionInliningFoo() {
        return 1;
    }

    public static int functionInliningOriginal(int x) {
        return functionInliningFoo() + 1;
    }

    public static int functionInliningExpected(int x) {
        return x + 2;
    }

    public static int ifTrueOriginal(int x) {
        if (true) {
            return x;
        } else {
            return x - 1;
        }
    }

    public static int ifTrueExpected(int x) {
        return x;
    }

    public static int infiniteEffectfulLoopOriginal() {
        int j = 0;
        for (int i = 5; i == 5;) {
            System.out.println(j);
        }
        j = 2;
        return j;
    }

    public static int infiniteLoopOriginal() {
        int j = 0;
        for (int i = 5; i == 5;) {
            j++;
        }
        return j;
    }

    // Modified from the paper example (see loop strength reduction)
    public static int loopBasedCodeMotionOriginal() {
        int x = 0;
        while (x < 3) {
            x += 1;
        }
        return x * 50;
    }

    public static int loopBasedCodeMotionExpected() {
        int x = 0;
        while (x < 150) {
            x += 50;
        }
        return x;
    }

    public static void loopInvariantCodeMotionOriginal(int n, int m) {
        for (int i = 0; i < 20; i++) {
            int j = n * 20;
            if (j < m) {
                j++;
            }
            System.out.println(i * j);
        }
    }

    public static void loopInvariantCodeMotionExpected(int n, int m) {
        int j = n * 20;
        if (j < m) {
            j++;
        }
        for (int i = 0; i < 20; i++) {
            System.out.println(i * j);
        }
    }

    public static int loopPeelingOriginal(int n) {
        int x = 0;
        int i = 0;
        while (i < n) {
            x += 5;
            i++;
        }
        return x;
    }

    public static int loopPeelingExpected(int n) {
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

    public static void loopStrengthReductionOriginal() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i * 5);
            i = i + 1;
            d++;
        }
    }

    public static void loopStrengthReductionExpected() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i);
            i = i + 5;
            d++;
        }
    }

    // Modified from the paper example, as multiplying by 5 makes
    // the optimizer just turn it into five additions.
    public static void loopStrengthReductionModifiedOriginal() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i * 50);
            i = i + 1;
            if (d == 150) {
                i = i + 3;
            }
            d++;
        }
    }

    public static void loopStrengthReductionModifiedExpected() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i);
            i = i + 50;
            if (d == 150) {
                i = i + 150;
            }
            d++;
        }
    }

    public static int loopUnrollOriginal(int n) {
        int i = 0;
        while (i < 1) {
            i++;
        }
        return i;
    }

    public static int loopUnrollExpected(int n) {
        return 1;
    }

    public static int simpleLoopUnswitchOriginal(int n) {
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

    // The expected code for simple loop unswitch causes encoding
    // of the output CFG to never terminate, so it is in Failing.java.
}

```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmp_folder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
+ !!! CRITICAL ERROR: Error parsing command line: Unrecognized option: tmp_folder !!!
```

### Optimized
```java
18:03:41.944 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (i < 500)
    {
      i++;
      if (paramInt == 0) {
        j = i * 2;
      } else {
        j = i * 3;
      }
    }
    return j;
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int i = 0;
    while (500 > i) {
      i += 1;
    }
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
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
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 150 > i; i = 50 + i) {}
    return i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; i < 150; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (i < paramInt2) {}
    for (paramInt2 = i + 1;; paramInt2 = i)
    {
      i = 0;
      for (paramInt1 = 0; 20 > i; paramInt1 = paramInt2 + paramInt1)
      {
        System.out.println(paramInt1);
        i += 1;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (paramInt2 > i) {}
    for (paramInt1 = 1 + i;; paramInt1 = i)
    {
      i = 0;
      paramInt2 = 0;
      while (20 > paramInt2)
      {
        System.out.println(i);
        i = paramInt1 + i;
        paramInt2 += 1;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (j < paramInt)
    {
      i += 5;
      j += 1;
    }
    return i;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (paramInt = 0;; paramInt = i)
    {
      return paramInt;
      i = 5;
      int j = 1;
      while (j < paramInt)
      {
        i += 5;
        j += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (i < 1500)
    {
      System.out.println(i);
      i += 5;
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int i = 0;
    int j = 0;
    while (i < 300)
    {
      i += 1;
      System.out.println(j);
      j += 5;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (i < 300)
    {
      if (i == 150) {}
      for (int k = j + 4;; k = 1 + j)
      {
        System.out.println(j * 50);
        j = k;
        i += 1;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int k = 0;
    int j = 0;
    if (j < 300)
    {
      if (j == 150) {}
      for (int i = k + 200;; i = 50 + k)
      {
        k = i;
        System.out.println(k);
        j = 1 + j;
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (1 > paramInt) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (paramInt > i)
      {
        j = 2 + 1;
        System.out.println(i);
        i += 1;
      }
    }
    while (paramInt > i)
    {
      j += 1;
      System.out.println(i);
      i += 1;
    }
    return j;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmp_folder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
Cannot connect to the Docker daemon at unix:///Users/kirsten/.docker/run/docker.sock. Is the docker daemon running?
```

### Optimized
```java
13:49:03.706 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (i < 500)
    {
      i++;
      if (paramInt == 0) {
        j = i * 2;
      } else {
        j = i * 3;
      }
    }
    return j;
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int i = 0;
    while (500 > i) {
      i += 1;
    }
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
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
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 150 > i; i = 50 + i) {}
    return i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; i < 150; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (i < paramInt2) {}
    for (paramInt2 = i + 1;; paramInt2 = i)
    {
      i = 0;
      for (paramInt1 = 0; 20 > i; paramInt1 = paramInt2 + paramInt1)
      {
        System.out.println(paramInt1);
        i += 1;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (paramInt2 > i) {}
    for (paramInt1 = 1 + i;; paramInt1 = i)
    {
      i = 0;
      paramInt2 = 0;
      while (20 > paramInt2)
      {
        System.out.println(i);
        i = paramInt1 + i;
        paramInt2 += 1;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (j < paramInt)
    {
      i += 5;
      j += 1;
    }
    return i;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (paramInt = 0;; paramInt = i)
    {
      return paramInt;
      i = 5;
      int j = 1;
      while (j < paramInt)
      {
        i += 5;
        j += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (i < 1500)
    {
      System.out.println(i);
      i += 5;
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int i = 0;
    int j = 0;
    while (i < 300)
    {
      i += 1;
      System.out.println(j);
      j += 5;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (i < 300)
    {
      if (i == 150) {}
      for (int k = j + 4;; k = 1 + j)
      {
        System.out.println(j * 50);
        j = k;
        i += 1;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int k = 0;
    int j = 0;
    if (j < 300)
    {
      if (j == 150) {}
      for (int i = k + 200;; i = 50 + k)
      {
        k = i;
        System.out.println(k);
        j = 1 + j;
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (1 > paramInt) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (paramInt > i)
      {
        j = 2 + 1;
        System.out.println(i);
        i += 1;
      }
    }
    while (paramInt > i)
    {
      j += 1;
      System.out.println(i);
      i += 1;
    }
    return j;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 439
      * PEG2PEGTIME 372
      * PBTIME 291
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 289 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 474
GLPKFormulation: Number of values: 221
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 4520
      * PEG2PEGTIME 4472
      * PBTIME 4159
      * ENGINETIME 289
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 136 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 386
GLPKFormulation: Number of values: 183
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 3518
      * PEG2PEGTIME 3505
      * PBTIME 3355
      * ENGINETIME 136
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 298
      * PEG2PEGTIME 290
      * PBTIME 274
      * ENGINETIME 4
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 232
      * PEG2PEGTIME 230
      * PBTIME 223
      * ENGINETIME 0
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 234
      * PEG2PEGTIME 231
      * PBTIME 223
      * ENGINETIME 1
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 232
      * PEG2PEGTIME 230
      * PBTIME 223
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 254
      * PEG2PEGTIME 251
      * PBTIME 246
      * ENGINETIME 0
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 273
      * PEG2PEGTIME 266
      * PBTIME 254
      * ENGINETIME 4
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 250
      * PEG2PEGTIME 247
      * PBTIME 237
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 225
      * PEG2PEGTIME 224
      * PBTIME 218
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 230
      * PEG2PEGTIME 227
      * PBTIME 220
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 49 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 236
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 98 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 230
GLPKFormulation: Number of values: 70
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteLoopOriginal()> FAILED
   - Processing method <Benchmark: int loopBasedCodeMotionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 66 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 223
GLPKFormulation: Number of values: 73
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 1862
      * PEG2PEGTIME 1856
      * PBTIME 1782
      * ENGINETIME 66
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 151 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 1801
      * PEG2PEGTIME 1793
      * PBTIME 1631
      * ENGINETIME 152
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 30 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 201
GLPKFormulation: Number of values: 127
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 1711
      * PEG2PEGTIME 1693
      * PBTIME 1657
      * ENGINETIME 30
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 33 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 228
GLPKFormulation: Number of values: 138
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 2047
      * PEG2PEGTIME 2033
      * PBTIME 1994
      * ENGINETIME 33
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 101 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 525
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 4282
      * PEG2PEGTIME 4273
      * PBTIME 4150
      * ENGINETIME 101
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 176 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 633
GLPKFormulation: Number of values: 224
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 5349
      * PEG2PEGTIME 5337
      * PBTIME 5150
      * ENGINETIME 176
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 46 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 258
GLPKFormulation: Number of values: 160
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 2173
      * PEG2PEGTIME 2163
      * PBTIME 2107
      * ENGINETIME 46
      * Optimization ratio 31430/31490 = 0.9980946332168943
      * PEG-based Optimization ratio 31430/31490 = 0.9980946332168943
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 34 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 143
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 1760
      * PEG2PEGTIME 1753
      * PBTIME 1710
      * ENGINETIME 34
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 35 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 273
GLPKFormulation: Number of values: 168
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 2410
      * PEG2PEGTIME 2390
      * PBTIME 2346
      * ENGINETIME 35
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 31 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 233
GLPKFormulation: Number of values: 171
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedExpected()> SUCCESSFUL
      * Optimization took 2016
      * PEG2PEGTIME 2000
      * PBTIME 1959
      * ENGINETIME 31
      * Optimization ratio 31520/31520 = 1.0
      * PEG-based Optimization ratio 31520/31520 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
   - Processing method <Benchmark: int loopUnrollOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 45 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollOriginal(int)> SUCCESSFUL
      * Optimization took 1692
      * PEG2PEGTIME 1681
      * PBTIME 1631
      * ENGINETIME 45
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopUnrollOriginal(int)>
   - Processing method <Benchmark: int loopUnrollExpected(int)>
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
      * Optimization of method <Benchmark: int loopUnrollExpected(int)> SUCCESSFUL
      * Optimization took 270
      * PEG2PEGTIME 242
      * PBTIME 232
      * ENGINETIME 1
   - Done processing method <Benchmark: int loopUnrollExpected(int)>
   - Processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 20 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 224
GLPKFormulation: Number of values: 153
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int simpleLoopUnswitchOriginal(int)> SUCCESSFUL
      * Optimization took 1858
      * PEG2PEGTIME 1841
      * PBTIME 1815
      * ENGINETIME 20
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Done optimizing Benchmark
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 2
   - Total methods = 27
+ Fixing bytecode of method <Benchmark: void <init>()>
+ Fixing bytecode of method <Benchmark: int branchHoistingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int branchHoistingExpected(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingExpected()>
+ Fixing bytecode of method <Benchmark: int constantFoldOriginal()>
+ Fixing bytecode of method <Benchmark: int deadLoopDeletionOriginal()>
+ Fixing bytecode of method <Benchmark: int functionInliningFoo()>
+ Fixing bytecode of method <Benchmark: int functionInliningOriginal(int)>
+ Fixing bytecode of method <Benchmark: int functionInliningExpected(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueOriginal(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueExpected(int)>
+ Fixing bytecode of method <Benchmark: int infiniteEffectfulLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int infiniteLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionExpected()>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingExpected(int)>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionExpected()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedExpected()>
+ Fixing bytecode of method <Benchmark: int loopUnrollOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopUnrollExpected(int)>
+ Fixing bytecode of method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Writing class back to optimized/Benchmark.class
+ Total optimization time = 44720 milliseconds
```

### Optimized
```java
13:54:29.648 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i;
    for (int j = 0;; j = i)
    {
      i = j + 1;
      if (500 <= i) {
        break;
      }
    }
    j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int j = 0;
    while (500 > j) {
      j += 1;
    }
    int i = j + j;
    if (paramInt == 0) {}
    for (paramInt = i;; paramInt = i + j) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
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
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 3 > i; i = 1 + i) {}
    return i * 50;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; 150 > i; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      int i = 0;
      paramInt1 = 0;
      while (20 > i)
      {
        i += 1;
        System.out.println(paramInt1);
        paramInt1 += paramInt2;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      paramInt1 = 0;
      int i = 0;
      while (20 > i)
      {
        System.out.println(paramInt1);
        paramInt1 += paramInt2;
        i += 1;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    for (int j = 0; paramInt > j; j = 1 + j) {
      i = 5 + i;
    }
    return i;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (int j = 0;; j = i)
    {
      return j;
      i = 5;
      j = 1;
      while (j < paramInt)
      {
        i += 5;
        j += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    int j = 0;
    while (300 > j)
    {
      i = 5 + i;
      System.out.println(i);
      j += 1;
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int i = 0;
    for (int j = 0; j < 300; j = 1 + j)
    {
      System.out.println(i);
      i = 5 + i;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int k = 0;
    int j = 0;
    if (300 > j)
    {
      int i = 1 + k;
      if (j == 150) {
        i += 3;
      }
      for (;;)
      {
        System.out.println(k * 50);
        k = i;
        j = 1 + j;
        break;
        i = i;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int j = 0;
    int i = 0;
    if (300 > i)
    {
      if (i == 150) {}
      for (int k = j + 200;; k = j + 50)
      {
        j = k;
        i += 1;
        System.out.println(j);
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    for (paramInt = 0; 1 > paramInt; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0)
    {
      while (j < paramInt)
      {
        j += 1;
        System.out.println(j);
        i = 2 + 1;
      }
      return i;
    }
    for (;;)
    {
      if (j < paramInt)
      {
        j += 1;
        System.out.println(j);
      }
    }
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 442
      * PEG2PEGTIME 365
      * PBTIME 281
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 311 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 463
GLPKFormulation: Number of values: 216
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 4546
      * PEG2PEGTIME 4509
      * PBTIME 4177
      * ENGINETIME 311
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 129 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 386
GLPKFormulation: Number of values: 181
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 3635
      * PEG2PEGTIME 3620
      * PBTIME 3477
      * ENGINETIME 129
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 302
      * PEG2PEGTIME 294
      * PBTIME 280
      * ENGINETIME 5
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 238
      * PEG2PEGTIME 235
      * PBTIME 228
      * ENGINETIME 1
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 239
      * PEG2PEGTIME 236
      * PBTIME 227
      * ENGINETIME 1
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 276
      * PEG2PEGTIME 272
      * PBTIME 265
      * ENGINETIME 1
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 236
      * PEG2PEGTIME 235
      * PBTIME 225
      * ENGINETIME 1
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 267
      * PEG2PEGTIME 260
      * PBTIME 250
      * ENGINETIME 3
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 237
      * PEG2PEGTIME 234
      * PBTIME 228
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 228
      * PEG2PEGTIME 227
      * PBTIME 221
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 238
      * PEG2PEGTIME 232
      * PBTIME 224
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 47 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 236
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 185 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 230
GLPKFormulation: Number of values: 70
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteLoopOriginal()> FAILED
   - Processing method <Benchmark: int loopBasedCodeMotionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 172 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 223
GLPKFormulation: Number of values: 73
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 2188
      * PEG2PEGTIME 2177
      * PBTIME 1993
      * ENGINETIME 172
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 59 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 1734
      * PEG2PEGTIME 1728
      * PBTIME 1658
      * ENGINETIME 59
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 32 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 200
GLPKFormulation: Number of values: 131
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 1773
      * PEG2PEGTIME 1756
      * PBTIME 1718
      * ENGINETIME 32
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 47 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 227
GLPKFormulation: Number of values: 137
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 2235
      * PEG2PEGTIME 2216
      * PBTIME 2161
      * ENGINETIME 47
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 196 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 525
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 5080
      * PEG2PEGTIME 5070
      * PBTIME 4865
      * ENGINETIME 196
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 103 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 622
GLPKFormulation: Number of values: 207
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 5562
      * PEG2PEGTIME 5551
      * PBTIME 5429
      * ENGINETIME 103
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 35 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 255
GLPKFormulation: Number of values: 158
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 2175
      * PEG2PEGTIME 2163
      * PBTIME 2107
      * ENGINETIME 35
      * Optimization ratio 31430/31490 = 0.9980946332168943
      * PEG-based Optimization ratio 31430/31490 = 0.9980946332168943
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 74 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 244
GLPKFormulation: Number of values: 150
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 2118
      * PEG2PEGTIME 2109
      * PBTIME 2027
      * ENGINETIME 74
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 51 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 290
GLPKFormulation: Number of values: 184
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 2607
      * PEG2PEGTIME 2592
      * PBTIME 2534
      * ENGINETIME 51
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 36 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 242
GLPKFormulation: Number of values: 171
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedExpected()> SUCCESSFUL
      * Optimization took 2111
      * PEG2PEGTIME 2096
      * PBTIME 2053
      * ENGINETIME 36
      * Optimization ratio 31520/31520 = 1.0
      * PEG-based Optimization ratio 31520/31520 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
   - Processing method <Benchmark: int loopUnrollOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 51 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollOriginal(int)> SUCCESSFUL
      * Optimization took 1874
      * PEG2PEGTIME 1862
      * PBTIME 1806
      * ENGINETIME 51
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopUnrollOriginal(int)>
   - Processing method <Benchmark: int loopUnrollExpected(int)>
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
      * Optimization of method <Benchmark: int loopUnrollExpected(int)> SUCCESSFUL
      * Optimization took 252
      * PEG2PEGTIME 235
      * PBTIME 226
      * ENGINETIME 1
   - Done processing method <Benchmark: int loopUnrollExpected(int)>
   - Processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 23 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 224
GLPKFormulation: Number of values: 155
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int simpleLoopUnswitchOriginal(int)> SUCCESSFUL
      * Optimization took 2126
      * PEG2PEGTIME 2097
      * PBTIME 2068
      * ENGINETIME 23
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Done optimizing Benchmark
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 2
   - Total methods = 27
+ Fixing bytecode of method <Benchmark: void <init>()>
+ Fixing bytecode of method <Benchmark: int branchHoistingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int branchHoistingExpected(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingExpected()>
+ Fixing bytecode of method <Benchmark: int constantFoldOriginal()>
+ Fixing bytecode of method <Benchmark: int deadLoopDeletionOriginal()>
+ Fixing bytecode of method <Benchmark: int functionInliningFoo()>
+ Fixing bytecode of method <Benchmark: int functionInliningOriginal(int)>
+ Fixing bytecode of method <Benchmark: int functionInliningExpected(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueOriginal(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueExpected(int)>
+ Fixing bytecode of method <Benchmark: int infiniteEffectfulLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int infiniteLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionExpected()>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingExpected(int)>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionExpected()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedExpected()>
+ Fixing bytecode of method <Benchmark: int loopUnrollOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopUnrollExpected(int)>
+ Fixing bytecode of method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Writing class back to optimized/Benchmark.class
+ Total optimization time = 48008 milliseconds
```

### Optimized
```java
14:01:24.364 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int j;
    for (int i = 0;; i = j)
    {
      j = i + 1;
      if (j >= 500) {
        break;
      }
    }
    i = j + j;
    if (paramInt == 0) {}
    for (i = i;; i = j + i) {
      return i;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    for (int i = 0; i < 500; i = 1 + i) {}
    int j = i + i;
    if (paramInt == 0) {
      j = j;
    }
    for (;;)
    {
      return j;
      j += i;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
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
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return functionInliningFoo() + 1;
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    int i = 0;
    while (3 > i) {
      i += 1;
    }
    return i * 50;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    int i = 0;
    while (150 > i) {
      i += 50;
    }
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (paramInt2 > i) {}
    for (paramInt1 = i + 1;; paramInt1 = i)
    {
      i = 0;
      for (paramInt2 = 0; 20 > i; paramInt2 = paramInt1 + paramInt2)
      {
        System.out.println(paramInt2);
        i += 1;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (i < paramInt2) {}
    for (paramInt2 = 1 + i;; paramInt2 = i)
    {
      i = 0;
      for (paramInt1 = 0; 20 > i; paramInt1 = paramInt2 + paramInt1)
      {
        i += 1;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    for (int j = 0; i < paramInt; j = 5 + j) {
      i += 1;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (int j = 0;; j = j)
    {
      return j;
      int i = 1;
      for (j = 5; i < paramInt; j = 5 + j) {
        i += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int j = 0;
    int i = 0;
    while (300 > j)
    {
      System.out.println(i);
      j += 1;
      i += 5;
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    for (int i = 0; 300 > j; i = 5 + i)
    {
      System.out.println(i);
      j += 1;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int k = 0;
    if (k < 300)
    {
      int i = 1 + j;
      if (k == 150) {}
      for (i = 3 + i;; i = i)
      {
        j = i;
        System.out.println(j * 50);
        k += 1;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int k = 0;
    int j = 0;
    if (300 > j)
    {
      int i = k + 50;
      if (j == 150) {}
      for (i = 150 + i;; i = i)
      {
        k = i;
        System.out.println(k);
        j = 1 + j;
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    for (paramInt = 0; 1 > paramInt; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (paramInt > i)
      {
        j = 2 + 1;
        System.out.println(i);
        i = 1 + i;
      }
    }
    while (paramInt > i)
    {
      j += 1;
      System.out.println(i);
      i = 1 + i;
    }
    return j;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 442
      * PEG2PEGTIME 368
      * PBTIME 293
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 304 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 474
GLPKFormulation: Number of values: 221
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 4274
      * PEG2PEGTIME 4240
      * PBTIME 3913
      * ENGINETIME 304
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 132 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 388
GLPKFormulation: Number of values: 183
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 3632
      * PEG2PEGTIME 3618
      * PBTIME 3471
      * ENGINETIME 132
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 302
      * PEG2PEGTIME 292
      * PBTIME 273
      * ENGINETIME 7
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 230
      * PEG2PEGTIME 229
      * PBTIME 221
      * ENGINETIME 1
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 237
      * PEG2PEGTIME 233
      * PBTIME 224
      * ENGINETIME 2
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 232
      * PEG2PEGTIME 229
      * PBTIME 223
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 259
      * PEG2PEGTIME 255
      * PBTIME 243
      * ENGINETIME 0
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 282
      * PEG2PEGTIME 276
      * PBTIME 265
      * ENGINETIME 3
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 242
      * PEG2PEGTIME 239
      * PBTIME 231
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 232
      * PEG2PEGTIME 230
      * PBTIME 224
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 240
      * PEG2PEGTIME 236
      * PBTIME 230
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 49 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 236
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 100 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 230
GLPKFormulation: Number of values: 70
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteLoopOriginal()> FAILED
   - Processing method <Benchmark: int loopBasedCodeMotionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 74 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 223
GLPKFormulation: Number of values: 73
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 1838
      * PEG2PEGTIME 1832
      * PBTIME 1748
      * ENGINETIME 74
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 88 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 1733
      * PEG2PEGTIME 1724
      * PBTIME 1627
      * ENGINETIME 88
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 31 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 227
GLPKFormulation: Number of values: 138
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 1917
      * PEG2PEGTIME 1899
      * PBTIME 1862
      * ENGINETIME 31
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 31 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 227
GLPKFormulation: Number of values: 138
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 1987
      * PEG2PEGTIME 1975
      * PBTIME 1936
      * ENGINETIME 31
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 70 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 525
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 4350
      * PEG2PEGTIME 4343
      * PBTIME 4266
      * ENGINETIME 70
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 60 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 602
GLPKFormulation: Number of values: 180
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 5015
      * PEG2PEGTIME 5003
      * PBTIME 4931
      * ENGINETIME 60
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 44 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 255
GLPKFormulation: Number of values: 158
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 2457
      * PEG2PEGTIME 2446
      * PBTIME 2392
      * ENGINETIME 44
      * Optimization ratio 31430/31490 = 0.9980946332168943
      * PEG-based Optimization ratio 31430/31490 = 0.9980946332168943
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 36 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 208
GLPKFormulation: Number of values: 144
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 1781
      * PEG2PEGTIME 1772
      * PBTIME 1706
      * ENGINETIME 36
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 182 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 277
GLPKFormulation: Number of values: 178
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 2623
      * PEG2PEGTIME 2594
      * PBTIME 2405
      * ENGINETIME 182
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 35 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 240
GLPKFormulation: Number of values: 159
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedExpected()> SUCCESSFUL
      * Optimization took 2035
      * PEG2PEGTIME 2019
      * PBTIME 1973
      * ENGINETIME 35
      * Optimization ratio 31520/31520 = 1.0
      * PEG-based Optimization ratio 31520/31520 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
   - Processing method <Benchmark: int loopUnrollOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 42 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollOriginal(int)> SUCCESSFUL
      * Optimization took 1653
      * PEG2PEGTIME 1645
      * PBTIME 1596
      * ENGINETIME 42
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopUnrollOriginal(int)>
   - Processing method <Benchmark: int loopUnrollExpected(int)>
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
      * Optimization of method <Benchmark: int loopUnrollExpected(int)> SUCCESSFUL
      * Optimization took 259
      * PEG2PEGTIME 234
      * PBTIME 227
      * ENGINETIME 1
   - Done processing method <Benchmark: int loopUnrollExpected(int)>
   - Processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 23 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 213
GLPKFormulation: Number of values: 159
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int simpleLoopUnswitchOriginal(int)> SUCCESSFUL
      * Optimization took 1957
      * PEG2PEGTIME 1929
      * PBTIME 1900
      * ENGINETIME 24
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Done optimizing Benchmark
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 2
   - Total methods = 27
+ Fixing bytecode of method <Benchmark: void <init>()>
+ Fixing bytecode of method <Benchmark: int branchHoistingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int branchHoistingExpected(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingExpected()>
+ Fixing bytecode of method <Benchmark: int constantFoldOriginal()>
+ Fixing bytecode of method <Benchmark: int deadLoopDeletionOriginal()>
+ Fixing bytecode of method <Benchmark: int functionInliningFoo()>
+ Fixing bytecode of method <Benchmark: int functionInliningOriginal(int)>
+ Fixing bytecode of method <Benchmark: int functionInliningExpected(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueOriginal(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueExpected(int)>
+ Fixing bytecode of method <Benchmark: int infiniteEffectfulLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int infiniteLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionExpected()>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingExpected(int)>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionExpected()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedExpected()>
+ Fixing bytecode of method <Benchmark: int loopUnrollOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopUnrollExpected(int)>
+ Fixing bytecode of method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Writing class back to optimized/Benchmark.class
+ Total optimization time = 45225 milliseconds
```

### Optimized
```java
14:23:41.743 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int j;
    for (int i = 0;; i = j)
    {
      j = i + 1;
      if (500 <= j) {
        break;
      }
    }
    i = j + j;
    if (paramInt == 0) {
      i = i;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int i = 0;
    while (500 > i) {
      i += 1;
    }
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = i + j) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
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
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; i < 3; i = 1 + i) {}
    return i * 50;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    int i = 0;
    while (i < 150) {
      i += 50;
    }
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {}
    for (int i = paramInt1 + 1;; i = paramInt1)
    {
      paramInt1 = 0;
      for (paramInt2 = 0; paramInt2 < 20; paramInt2 = 1 + paramInt2)
      {
        System.out.println(paramInt1);
        paramInt1 += i;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {}
    for (paramInt1 = 1 + paramInt1;; paramInt1 = paramInt1)
    {
      int i = 0;
      for (paramInt2 = 0; i < 20; paramInt2 = paramInt1 + paramInt2)
      {
        System.out.println(paramInt2);
        i += 1;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    for (int j = 0; j < paramInt; j = 1 + j) {
      i = 5 + i;
    }
    return i;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (int i = 0;; i = i)
    {
      return i;
      int j = 1;
      i = 5;
      while (j < paramInt)
      {
        j = 1 + j;
        i += 5;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int j = 0;
    int i = 0;
    while (300 > j)
    {
      j += 1;
      i += 5;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int i = 0;
    int j = 0;
    while (i < 300)
    {
      i = 1 + i;
      System.out.println(j);
      j += 5;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int k = 0;
    if (300 > j)
    {
      int i = 1 + k;
      if (j == 150) {
        i += 3;
      }
      for (;;)
      {
        j += 1;
        k = i;
        System.out.println(50 * k);
        break;
        i = i;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int j = 0;
    int k = 0;
    if (300 > k)
    {
      int i = j + 50;
      if (k == 150) {}
      for (i = 150 + i;; i = i)
      {
        j = i;
        k += 1;
        System.out.println(j);
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    for (paramInt = 0; paramInt < 1; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int k = 0;
    int j = 0;
    if (paramInt < 0) {
      while (paramInt > j)
      {
        int i = j;
        k = 2 + 1;
        j += 1;
        System.out.println(i);
      }
    }
    while (paramInt > j)
    {
      k += 1;
      j += 1;
      System.out.println(j);
    }
    return k;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 463
      * PEG2PEGTIME 370
      * PBTIME 283
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 370 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 480
GLPKFormulation: Number of values: 221
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 5482
      * PEG2PEGTIME 5437
      * PBTIME 5048
      * ENGINETIME 370
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 124 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 389
GLPKFormulation: Number of values: 186
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 4275
      * PEG2PEGTIME 4260
      * PBTIME 4113
      * ENGINETIME 125
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 324
      * PEG2PEGTIME 317
      * PBTIME 291
      * ENGINETIME 10
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 244
      * PEG2PEGTIME 242
      * PBTIME 234
      * ENGINETIME 1
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 240
      * PEG2PEGTIME 238
      * PBTIME 230
      * ENGINETIME 3
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 233
      * PEG2PEGTIME 230
      * PBTIME 224
      * ENGINETIME 1
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 232
      * PEG2PEGTIME 231
      * PBTIME 224
      * ENGINETIME 0
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 269
      * PEG2PEGTIME 261
      * PBTIME 252
      * ENGINETIME 2
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 237
      * PEG2PEGTIME 235
      * PBTIME 228
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 230
      * PEG2PEGTIME 229
      * PBTIME 222
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 229
      * PEG2PEGTIME 227
      * PBTIME 221
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 69 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 236
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 158 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 230
GLPKFormulation: Number of values: 70
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteLoopOriginal()> FAILED
   - Processing method <Benchmark: int loopBasedCodeMotionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 73 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 223
GLPKFormulation: Number of values: 73
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 2344
      * PEG2PEGTIME 2332
      * PBTIME 2245
      * ENGINETIME 73
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 146 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 2056
      * PEG2PEGTIME 2045
      * PBTIME 1874
      * ENGINETIME 146
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 49 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 203
GLPKFormulation: Number of values: 132
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 2336
      * PEG2PEGTIME 2295
      * PBTIME 2240
      * ENGINETIME 49
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 42 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 228
GLPKFormulation: Number of values: 138
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 2529
      * PEG2PEGTIME 2515
      * PBTIME 2461
      * ENGINETIME 42
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 84 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 525
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 4859
      * PEG2PEGTIME 4844
      * PBTIME 4750
      * ENGINETIME 84
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 86 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 623
GLPKFormulation: Number of values: 208
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 5873
      * PEG2PEGTIME 5863
      * PBTIME 5764
      * ENGINETIME 86
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 45 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 253
GLPKFormulation: Number of values: 156
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 2525
      * PEG2PEGTIME 2513
      * PBTIME 2451
      * ENGINETIME 45
      * Optimization ratio 31430/31490 = 0.9980946332168943
      * PEG-based Optimization ratio 31430/31490 = 0.9980946332168943
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 43 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 246
GLPKFormulation: Number of values: 149
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 2325
      * PEG2PEGTIME 2316
      * PBTIME 2261
      * ENGINETIME 43
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 41 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 277
GLPKFormulation: Number of values: 172
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 2740
      * PEG2PEGTIME 2725
      * PBTIME 2677
      * ENGINETIME 41
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 39 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 236
GLPKFormulation: Number of values: 169
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedExpected()> SUCCESSFUL
      * Optimization took 2384
      * PEG2PEGTIME 2364
      * PBTIME 2316
      * ENGINETIME 39
      * Optimization ratio 31520/31520 = 1.0
      * PEG-based Optimization ratio 31520/31520 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
   - Processing method <Benchmark: int loopUnrollOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 48 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollOriginal(int)> SUCCESSFUL
      * Optimization took 2145
      * PEG2PEGTIME 2134
      * PBTIME 2064
      * ENGINETIME 48
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopUnrollOriginal(int)>
   - Processing method <Benchmark: int loopUnrollExpected(int)>
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
      * Optimization of method <Benchmark: int loopUnrollExpected(int)> SUCCESSFUL
      * Optimization took 252
      * PEG2PEGTIME 235
      * PBTIME 226
      * ENGINETIME 1
   - Done processing method <Benchmark: int loopUnrollExpected(int)>
   - Processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 43 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 219
GLPKFormulation: Number of values: 155
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int simpleLoopUnswitchOriginal(int)> SUCCESSFUL
      * Optimization took 2396
      * PEG2PEGTIME 2353
      * PBTIME 2301
      * ENGINETIME 43
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Done optimizing Benchmark
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 2
   - Total methods = 27
+ Fixing bytecode of method <Benchmark: void <init>()>
+ Fixing bytecode of method <Benchmark: int branchHoistingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int branchHoistingExpected(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingExpected()>
+ Fixing bytecode of method <Benchmark: int constantFoldOriginal()>
+ Fixing bytecode of method <Benchmark: int deadLoopDeletionOriginal()>
+ Fixing bytecode of method <Benchmark: int functionInliningFoo()>
+ Fixing bytecode of method <Benchmark: int functionInliningOriginal(int)>
+ Fixing bytecode of method <Benchmark: int functionInliningExpected(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueOriginal(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueExpected(int)>
+ Fixing bytecode of method <Benchmark: int infiniteEffectfulLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int infiniteLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionExpected()>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingExpected(int)>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionExpected()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedExpected()>
+ Fixing bytecode of method <Benchmark: int loopUnrollOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopUnrollExpected(int)>
+ Fixing bytecode of method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Writing class back to optimized/Benchmark.class
+ Total optimization time = 53486 milliseconds
```

### Optimized
```java
14:28:09.262 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i;
    for (int j = 0;; j = i)
    {
      i = j + 1;
      if (i >= 500) {
        break;
      }
    }
    j = i + i;
    if (paramInt == 0) {
      i = j;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    for (int j = 0; 500 > j; j = 1 + j) {}
    int i = j + j;
    if (paramInt == 0) {}
    for (j = i;; j = i + j) {
      return j;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
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
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return functionInliningFoo() + 1;
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return 2 + paramInt;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    int i = 0;
    while (3 > i) {
      i += 1;
    }
    return i * 50;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    int i = 0;
    while (i < 150) {
      i += 50;
    }
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      paramInt1 = 0;
      int i = 0;
      while (20 > i)
      {
        System.out.println(paramInt1);
        paramInt1 += paramInt2;
        i += 1;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (paramInt2 > i) {}
    for (i = 1 + i;; i = i)
    {
      paramInt2 = 0;
      paramInt1 = 0;
      while (20 > paramInt1)
      {
        paramInt2 += i;
        paramInt1 = 1 + paramInt1;
        System.out.println(paramInt2);
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int j = 0;
    for (int i = 0; i < paramInt; i = 1 + i) {
      j += 5;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int j;
    for (paramInt = 0;; paramInt = j)
    {
      return paramInt;
      int i = 1;
      for (j = 5; paramInt > i; j = 5 + j) {
        i = 1 + i;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int j = 0;
    int i = 0;
    while (300 > j)
    {
      System.out.println(i);
      j = 1 + j;
      i += 5;
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    for (int i = 0; 300 > j; i = 5 + i)
    {
      j += 1;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int i = 0;
    int k = 0;
    if (300 > i)
    {
      int j = 1 + k;
      if (i == 150) {}
      for (j = 3 + j;; j = j)
      {
        System.out.println(50 * k);
        i += 1;
        k = j;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int k = 0;
    int j = 0;
    if (k < 300)
    {
      int i = 50 + j;
      if (k == 150) {}
      for (i = 150 + i;; i = i)
      {
        k += 1;
        System.out.println(j);
        j = i;
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (i < paramInt)
      {
        j = 2 + 1;
        System.out.println(i);
        i = 1 + i;
      }
    }
    while (i < paramInt)
    {
      j += 1;
      System.out.println(i);
      i = 1 + i;
    }
    return j;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 459
      * PEG2PEGTIME 366
      * PBTIME 294
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 299 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 463
GLPKFormulation: Number of values: 216
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 4382
      * PEG2PEGTIME 4347
      * PBTIME 4025
      * ENGINETIME 299
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 120 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 386
GLPKFormulation: Number of values: 181
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 3473
      * PEG2PEGTIME 3462
      * PBTIME 3327
      * ENGINETIME 121
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 300
      * PEG2PEGTIME 291
      * PBTIME 274
      * ENGINETIME 7
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 245
      * PEG2PEGTIME 243
      * PBTIME 236
      * ENGINETIME 0
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 245
      * PEG2PEGTIME 242
      * PBTIME 233
      * ENGINETIME 2
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 255
      * PEG2PEGTIME 252
      * PBTIME 245
      * ENGINETIME 1
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 230
      * PEG2PEGTIME 228
      * PBTIME 221
      * ENGINETIME 1
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 264
      * PEG2PEGTIME 258
      * PBTIME 251
      * ENGINETIME 1
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 239
      * PEG2PEGTIME 236
      * PBTIME 230
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 230
      * PEG2PEGTIME 229
      * PBTIME 222
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 239
      * PEG2PEGTIME 236
      * PBTIME 228
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 52 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 236
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 205 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 230
GLPKFormulation: Number of values: 70
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteLoopOriginal()> FAILED
   - Processing method <Benchmark: int loopBasedCodeMotionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 180 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 223
GLPKFormulation: Number of values: 73
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 1944
      * PEG2PEGTIME 1936
      * PBTIME 1737
      * ENGINETIME 181
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 57 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 1707
      * PEG2PEGTIME 1701
      * PBTIME 1636
      * ENGINETIME 57
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 35 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 227
GLPKFormulation: Number of values: 137
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 1897
      * PEG2PEGTIME 1881
      * PBTIME 1838
      * ENGINETIME 35
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 33 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 230
GLPKFormulation: Number of values: 140
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 2001
      * PEG2PEGTIME 1986
      * PBTIME 1946
      * ENGINETIME 33
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 129 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 558
GLPKFormulation: Number of values: 189
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 4698
      * PEG2PEGTIME 4687
      * PBTIME 4553
      * ENGINETIME 129
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 94 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 623
GLPKFormulation: Number of values: 206
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 5328
      * PEG2PEGTIME 5313
      * PBTIME 5208
      * ENGINETIME 94
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 53 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 257
GLPKFormulation: Number of values: 159
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 2333
      * PEG2PEGTIME 2319
      * PBTIME 2253
      * ENGINETIME 53
      * Optimization ratio 31430/31490 = 0.9980946332168943
      * PEG-based Optimization ratio 31430/31490 = 0.9980946332168943
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 72 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 137
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 1811
      * PEG2PEGTIME 1805
      * PBTIME 1719
      * ENGINETIME 72
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 77 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 275
GLPKFormulation: Number of values: 165
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 2613
      * PEG2PEGTIME 2594
      * PBTIME 2511
      * ENGINETIME 77
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 82 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 257
GLPKFormulation: Number of values: 178
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedExpected()> SUCCESSFUL
      * Optimization took 2411
      * PEG2PEGTIME 2394
      * PBTIME 2303
      * ENGINETIME 82
      * Optimization ratio 31520/31520 = 1.0
      * PEG-based Optimization ratio 31520/31520 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
   - Processing method <Benchmark: int loopUnrollOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 39 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollOriginal(int)> SUCCESSFUL
      * Optimization took 1711
      * PEG2PEGTIME 1701
      * PBTIME 1652
      * ENGINETIME 40
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopUnrollOriginal(int)>
   - Processing method <Benchmark: int loopUnrollExpected(int)>
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
      * Optimization of method <Benchmark: int loopUnrollExpected(int)> SUCCESSFUL
      * Optimization took 242
      * PEG2PEGTIME 233
      * PBTIME 226
      * ENGINETIME 0
   - Done processing method <Benchmark: int loopUnrollExpected(int)>
   - Processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 30 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 224
GLPKFormulation: Number of values: 153
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int simpleLoopUnswitchOriginal(int)> SUCCESSFUL
      * Optimization took 2225
      * PEG2PEGTIME 2160
      * PBTIME 2123
      * ENGINETIME 30
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Done optimizing Benchmark
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 2
   - Total methods = 27
+ Fixing bytecode of method <Benchmark: void <init>()>
+ Fixing bytecode of method <Benchmark: int branchHoistingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int branchHoistingExpected(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingExpected()>
+ Fixing bytecode of method <Benchmark: int constantFoldOriginal()>
+ Fixing bytecode of method <Benchmark: int deadLoopDeletionOriginal()>
+ Fixing bytecode of method <Benchmark: int functionInliningFoo()>
+ Fixing bytecode of method <Benchmark: int functionInliningOriginal(int)>
+ Fixing bytecode of method <Benchmark: int functionInliningExpected(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueOriginal(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueExpected(int)>
+ Fixing bytecode of method <Benchmark: int infiniteEffectfulLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int infiniteLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionExpected()>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingExpected(int)>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionExpected()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedExpected()>
+ Fixing bytecode of method <Benchmark: int loopUnrollOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopUnrollExpected(int)>
+ Fixing bytecode of method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Writing class back to optimized/Benchmark.class
+ Total optimization time = 46467 milliseconds
```

### Optimized
```java
14:42:23.853 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int j;
    for (int i = 0;; i = j)
    {
      j = i + 1;
      if (j >= 500) {
        break;
      }
    }
    i = j + j;
    if (paramInt == 0) {}
    for (i = i;; i = j + i) {
      return i;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    for (int i = 0; i < 500; i = 1 + i) {}
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
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
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    int i = 0;
    while (3 > i) {
      i += 1;
    }
    return i * 50;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; 150 > i; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (int i = 1 + paramInt1;; i = paramInt1)
    {
      paramInt1 = 0;
      paramInt2 = 0;
      while (paramInt2 < 20)
      {
        paramInt1 += i;
        paramInt2 += 1;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = 1 + paramInt1;; paramInt2 = paramInt1)
    {
      int i = 0;
      paramInt1 = 0;
      while (i < 20)
      {
        i += 1;
        paramInt1 = paramInt2 + paramInt1;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int j = 0;
    for (int i = 0; paramInt > i; i = 1 + i) {
      j += 5;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (int j = 0;; j = i)
    {
      return j;
      i = 5;
      j = 1;
      while (j < paramInt)
      {
        i += 5;
        j += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    for (int j = 0; i < 300; j = 5 + j)
    {
      System.out.println(j);
      i += 1;
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    for (int i = 0; j < 300; i = 5 + i)
    {
      j = 1 + j;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      int k = i + 1;
      if (j == 150) {}
      for (k = 3 + k;; k = k)
      {
        j += 1;
        i = k;
        System.out.println(50 * i);
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int i = 0;
    int j = 0;
    if (300 > j)
    {
      if (j == 150) {}
      for (int k = i + 200;; k = 50 + i)
      {
        i = k;
        j = 1 + j;
        System.out.println(i);
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    for (paramInt = 0; paramInt < 1; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    if (paramInt < 0) {
      while (j < paramInt)
      {
        i = 1 + 2;
        System.out.println(j);
        j += 1;
      }
    }
    while (j < paramInt)
    {
      i = 1 + i;
      System.out.println(j);
      j += 1;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 494
      * PEG2PEGTIME 409
      * PBTIME 320
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 372 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 480
GLPKFormulation: Number of values: 221
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 4722
      * PEG2PEGTIME 4690
      * PBTIME 4292
      * ENGINETIME 372
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 131 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 388
GLPKFormulation: Number of values: 182
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 3751
      * PEG2PEGTIME 3737
      * PBTIME 3589
      * ENGINETIME 131
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 307
      * PEG2PEGTIME 299
      * PBTIME 284
      * ENGINETIME 5
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 232
      * PEG2PEGTIME 229
      * PBTIME 222
      * ENGINETIME 1
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 253
      * PEG2PEGTIME 249
      * PBTIME 230
      * ENGINETIME 2
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 249
      * PEG2PEGTIME 246
      * PBTIME 230
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 237
      * PEG2PEGTIME 234
      * PBTIME 227
      * ENGINETIME 0
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 309
      * PEG2PEGTIME 303
      * PBTIME 251
      * ENGINETIME 5
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 240
      * PEG2PEGTIME 237
      * PBTIME 230
      * ENGINETIME 2
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 238
      * PEG2PEGTIME 236
      * PBTIME 230
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 237
      * PEG2PEGTIME 234
      * PBTIME 226
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 49 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 236
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 123 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 237
GLPKFormulation: Number of values: 73
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteLoopOriginal()> FAILED
   - Processing method <Benchmark: int loopBasedCodeMotionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 81 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 223
GLPKFormulation: Number of values: 73
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 1836
      * PEG2PEGTIME 1828
      * PBTIME 1739
      * ENGINETIME 81
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 160 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 1919
      * PEG2PEGTIME 1911
      * PBTIME 1742
      * ENGINETIME 160
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 43 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 228
GLPKFormulation: Number of values: 138
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 2034
      * PEG2PEGTIME 2014
      * PBTIME 1950
      * ENGINETIME 43
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 33 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 202
GLPKFormulation: Number of values: 130
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 1754
      * PEG2PEGTIME 1732
      * PBTIME 1692
      * ENGINETIME 33
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 68 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 561
GLPKFormulation: Number of values: 187
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 4659
      * PEG2PEGTIME 4649
      * PBTIME 4572
      * ENGINETIME 68
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 76 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 623
GLPKFormulation: Number of values: 205
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 5178
      * PEG2PEGTIME 5167
      * PBTIME 5076
      * ENGINETIME 76
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 32 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 258
GLPKFormulation: Number of values: 160
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 2310
      * PEG2PEGTIME 2298
      * PBTIME 2254
      * ENGINETIME 33
      * Optimization ratio 31430/31490 = 0.9980946332168943
      * PEG-based Optimization ratio 31430/31490 = 0.9980946332168943
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 37 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 244
GLPKFormulation: Number of values: 150
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 2075
      * PEG2PEGTIME 2067
      * PBTIME 2018
      * ENGINETIME 37
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 40 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 267
GLPKFormulation: Number of values: 163
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 2383
      * PEG2PEGTIME 2369
      * PBTIME 2312
      * ENGINETIME 40
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 69 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 236
GLPKFormulation: Number of values: 172
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedExpected()> SUCCESSFUL
      * Optimization took 2113
      * PEG2PEGTIME 2096
      * PBTIME 2015
      * ENGINETIME 69
      * Optimization ratio 31520/31520 = 1.0
      * PEG-based Optimization ratio 31520/31520 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
   - Processing method <Benchmark: int loopUnrollOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 159 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollOriginal(int)> SUCCESSFUL
      * Optimization took 1707
      * PEG2PEGTIME 1699
      * PBTIME 1532
      * ENGINETIME 159
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopUnrollOriginal(int)>
   - Processing method <Benchmark: int loopUnrollExpected(int)>
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
      * Optimization of method <Benchmark: int loopUnrollExpected(int)> SUCCESSFUL
      * Optimization took 243
      * PEG2PEGTIME 230
      * PBTIME 223
      * ENGINETIME 1
   - Done processing method <Benchmark: int loopUnrollExpected(int)>
   - Processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 81 milliseconds
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
      * Optimization of method <Benchmark: int simpleLoopUnswitchOriginal(int)> SUCCESSFUL
      * Optimization took 1977
      * PEG2PEGTIME 1952
      * PBTIME 1866
      * ENGINETIME 81
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Done optimizing Benchmark
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 2
   - Total methods = 27
+ Fixing bytecode of method <Benchmark: void <init>()>
+ Fixing bytecode of method <Benchmark: int branchHoistingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int branchHoistingExpected(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingExpected()>
+ Fixing bytecode of method <Benchmark: int constantFoldOriginal()>
+ Fixing bytecode of method <Benchmark: int deadLoopDeletionOriginal()>
+ Fixing bytecode of method <Benchmark: int functionInliningFoo()>
+ Fixing bytecode of method <Benchmark: int functionInliningOriginal(int)>
+ Fixing bytecode of method <Benchmark: int functionInliningExpected(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueOriginal(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueExpected(int)>
+ Fixing bytecode of method <Benchmark: int infiniteEffectfulLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int infiniteLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionExpected()>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingExpected(int)>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionExpected()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedExpected()>
+ Fixing bytecode of method <Benchmark: int loopUnrollOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopUnrollExpected(int)>
+ Fixing bytecode of method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Writing class back to optimized/Benchmark.class
+ Total optimization time = 46431 milliseconds
```

### Optimized
```java
14:52:28.216 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    for (int i = 0;; i = i)
    {
      i += 1;
      if (i >= 500) {
        break;
      }
    }
    int j = i + i;
    if (paramInt == 0) {
      i = j;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int i = 0;
    while (i < 500) {
      i += 1;
    }
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = i + j) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
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
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return functionInliningFoo() + 1;
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    int i = 0;
    while (i < 3) {
      i += 1;
    }
    return 50 * i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    int i = 0;
    while (i < 150) {
      i += 50;
    }
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {
      paramInt1 += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      for (int i = 0; 20 > i; i = 1 + i)
      {
        System.out.println(paramInt2);
        paramInt2 = paramInt1 + paramInt2;
      }
      return;
      paramInt1 = paramInt1;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {
      paramInt1 += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      for (int i = 0; 20 > i; i = 1 + i)
      {
        System.out.println(paramInt2);
        paramInt2 = paramInt1 + paramInt2;
      }
      return;
      paramInt1 = paramInt1;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    for (int j = 0; i < paramInt; j = 5 + j) {
      i += 1;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int j;
    for (paramInt = 0;; paramInt = j)
    {
      return paramInt;
      int i = 1;
      for (j = 5; paramInt > i; j = 5 + j) {
        i = 1 + i;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int j = 0;
    for (int i = 0; i < 300; i = 1 + i)
    {
      j += 5;
      System.out.println(j);
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int i = 0;
    int j = 0;
    while (j < 300)
    {
      i += 5;
      System.out.println(i);
      j += 1;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int k = 0;
    int j = 0;
    if (k < 300)
    {
      int i = j + 1;
      if (k == 150) {
        i += 3;
      }
      for (;;)
      {
        k = 1 + k;
        j = i;
        System.out.println(50 * j);
        break;
        i = i;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int k = 0;
    int j = 0;
    if (300 > j)
    {
      int i = k + 50;
      if (j == 150) {
        i += 150;
      }
      for (;;)
      {
        k = i;
        j += 1;
        System.out.println(k);
        break;
        i = i;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (1 > paramInt) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (paramInt > j)
      {
        j += 1;
        System.out.println(j);
        i = 1 + 2;
      }
    }
    while (paramInt > j)
    {
      j += 1;
      System.out.println(j);
      i = 1 + i;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 426
      * PEG2PEGTIME 351
      * PBTIME 275
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 307 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 474
GLPKFormulation: Number of values: 221
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 4871
      * PEG2PEGTIME 4815
      * PBTIME 4477
      * ENGINETIME 307
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 160 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 385
GLPKFormulation: Number of values: 182
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 3835
      * PEG2PEGTIME 3820
      * PBTIME 3646
      * ENGINETIME 160
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 301
      * PEG2PEGTIME 291
      * PBTIME 275
      * ENGINETIME 5
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 242
      * PEG2PEGTIME 240
      * PBTIME 228
      * ENGINETIME 0
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 242
      * PEG2PEGTIME 238
      * PBTIME 226
      * ENGINETIME 2
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 236
      * PEG2PEGTIME 232
      * PBTIME 223
      * ENGINETIME 1
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 270
      * PEG2PEGTIME 267
      * PBTIME 260
      * ENGINETIME 0
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 272
      * PEG2PEGTIME 264
      * PBTIME 250
      * ENGINETIME 4
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 246
      * PEG2PEGTIME 243
      * PBTIME 233
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 237
      * PEG2PEGTIME 234
      * PBTIME 228
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 236
      * PEG2PEGTIME 234
      * PBTIME 227
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 50 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 236
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 105 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 230
GLPKFormulation: Number of values: 70
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteLoopOriginal()> FAILED
   - Processing method <Benchmark: int loopBasedCodeMotionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 73 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 223
GLPKFormulation: Number of values: 73
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 1839
      * PEG2PEGTIME 1831
      * PBTIME 1739
      * ENGINETIME 73
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 124 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 1740
      * PEG2PEGTIME 1734
      * PBTIME 1601
      * ENGINETIME 124
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 29 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 228
GLPKFormulation: Number of values: 138
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 1876
      * PEG2PEGTIME 1862
      * PBTIME 1827
      * ENGINETIME 29
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 40 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 202
GLPKFormulation: Number of values: 128
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 1550
      * PEG2PEGTIME 1538
      * PBTIME 1491
      * ENGINETIME 40
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 72 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 525
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 4552
      * PEG2PEGTIME 4546
      * PBTIME 4461
      * ENGINETIME 72
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 73 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 626
GLPKFormulation: Number of values: 201
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 5303
      * PEG2PEGTIME 5287
      * PBTIME 5199
      * ENGINETIME 73
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 42 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 257
GLPKFormulation: Number of values: 159
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 2388
      * PEG2PEGTIME 2370
      * PBTIME 2312
      * ENGINETIME 42
      * Optimization ratio 31430/31490 = 0.9980946332168943
      * PEG-based Optimization ratio 31430/31490 = 0.9980946332168943
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 33 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 217
GLPKFormulation: Number of values: 137
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 1906
      * PEG2PEGTIME 1896
      * PBTIME 1851
      * ENGINETIME 33
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 38 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 267
GLPKFormulation: Number of values: 159
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 2339
      * PEG2PEGTIME 2315
      * PBTIME 2270
      * ENGINETIME 38
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 40 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 232
GLPKFormulation: Number of values: 168
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedExpected()> SUCCESSFUL
      * Optimization took 2003
      * PEG2PEGTIME 1986
      * PBTIME 1937
      * ENGINETIME 40
      * Optimization ratio 31520/31520 = 1.0
      * PEG-based Optimization ratio 31520/31520 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
   - Processing method <Benchmark: int loopUnrollOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 56 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollOriginal(int)> SUCCESSFUL
      * Optimization took 1677
      * PEG2PEGTIME 1670
      * PBTIME 1608
      * ENGINETIME 56
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopUnrollOriginal(int)>
   - Processing method <Benchmark: int loopUnrollExpected(int)>
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
      * Optimization of method <Benchmark: int loopUnrollExpected(int)> SUCCESSFUL
      * Optimization took 254
      * PEG2PEGTIME 236
      * PBTIME 228
      * ENGINETIME 1
   - Done processing method <Benchmark: int loopUnrollExpected(int)>
   - Processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 28 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 219
GLPKFormulation: Number of values: 152
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int simpleLoopUnswitchOriginal(int)> SUCCESSFUL
      * Optimization took 2111
      * PEG2PEGTIME 2078
      * PBTIME 2043
      * ENGINETIME 28
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Done optimizing Benchmark
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 2
   - Total methods = 27
+ Fixing bytecode of method <Benchmark: void <init>()>
+ Fixing bytecode of method <Benchmark: int branchHoistingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int branchHoistingExpected(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingExpected()>
+ Fixing bytecode of method <Benchmark: int constantFoldOriginal()>
+ Fixing bytecode of method <Benchmark: int deadLoopDeletionOriginal()>
+ Fixing bytecode of method <Benchmark: int functionInliningFoo()>
+ Fixing bytecode of method <Benchmark: int functionInliningOriginal(int)>
+ Fixing bytecode of method <Benchmark: int functionInliningExpected(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueOriginal(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueExpected(int)>
+ Fixing bytecode of method <Benchmark: int infiniteEffectfulLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int infiniteLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionExpected()>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingExpected(int)>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionExpected()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedExpected()>
+ Fixing bytecode of method <Benchmark: int loopUnrollOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopUnrollExpected(int)>
+ Fixing bytecode of method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Writing class back to optimized/Benchmark.class
+ Total optimization time = 45899 milliseconds
```

### Optimized
```java
14:58:44.410 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i;
    for (int j = 0;; j = i)
    {
      i = j + 1;
      if (500 <= i) {
        break;
      }
    }
    j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    for (int i = 0; i < 500; i = 1 + i) {}
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
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
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return 2 + paramInt;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 3 > i; i = 1 + i) {}
    return 50 * i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    int i = 0;
    while (150 > i) {
      i += 50;
    }
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = 1 + paramInt1;; paramInt2 = paramInt1)
    {
      int i = 0;
      for (paramInt1 = 0; i < 20; paramInt1 = paramInt2 + paramInt1)
      {
        i += 1;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    int i = 20 * paramInt1;
    if (paramInt2 > i) {}
    for (paramInt2 = 1 + i;; paramInt2 = i)
    {
      paramInt1 = 0;
      i = 0;
      while (20 > i)
      {
        paramInt1 += paramInt2;
        System.out.println(paramInt1);
        i += 1;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    for (int j = 0; paramInt > j; j = 1 + j) {
      i += 5;
    }
    return i;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (paramInt = 0;; paramInt = i)
    {
      return paramInt;
      i = 5;
      int j = 1;
      while (paramInt > j)
      {
        i += 5;
        j += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int j = 0;
    int i = 0;
    while (j < 300)
    {
      j = 1 + j;
      i = 5 + i;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    int i = 0;
    while (300 > j)
    {
      j += 1;
      System.out.println(i);
      i += 5;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (300 > i)
    {
      if (i == 150) {}
      for (int k = 4 + j;; k = 1 + j)
      {
        j = k;
        i += 1;
        System.out.println(j * 50);
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int k = 0;
    int j = 0;
    if (300 > k)
    {
      int i = j + 50;
      if (k == 150) {}
      for (i = 150 + i;; i = i)
      {
        k += 1;
        System.out.println(j);
        j = i;
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    for (paramInt = 0; paramInt < 1; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    if (paramInt < 0)
    {
      while (i < paramInt)
      {
        i += 1;
        System.out.println(i);
        j = 1 + 2;
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
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 458
      * PEG2PEGTIME 387
      * PBTIME 286
      * ENGINETIME 9
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 322 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 463
GLPKFormulation: Number of values: 215
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 4333
      * PEG2PEGTIME 4299
      * PBTIME 3950
      * ENGINETIME 322
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 107 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 389
GLPKFormulation: Number of values: 184
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 3490
      * PEG2PEGTIME 3478
      * PBTIME 3352
      * ENGINETIME 107
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 315
      * PEG2PEGTIME 308
      * PBTIME 286
      * ENGINETIME 10
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 236
      * PEG2PEGTIME 234
      * PBTIME 226
      * ENGINETIME 1
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 232
      * PEG2PEGTIME 230
      * PBTIME 221
      * ENGINETIME 1
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 239
      * PEG2PEGTIME 237
      * PBTIME 230
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 254
      * PEG2PEGTIME 250
      * PBTIME 242
      * ENGINETIME 1
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 262
      * PEG2PEGTIME 256
      * PBTIME 245
      * ENGINETIME 4
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 235
      * PEG2PEGTIME 232
      * PBTIME 226
      * ENGINETIME 0
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 231
      * PEG2PEGTIME 230
      * PBTIME 224
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 232
      * PEG2PEGTIME 230
      * PBTIME 223
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 45 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 236
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 99 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 237
GLPKFormulation: Number of values: 73
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteLoopOriginal()> FAILED
   - Processing method <Benchmark: int loopBasedCodeMotionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 83 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 223
GLPKFormulation: Number of values: 73
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 1972
      * PEG2PEGTIME 1964
      * PBTIME 1871
      * ENGINETIME 84
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 146 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 1827
      * PEG2PEGTIME 1817
      * PBTIME 1661
      * ENGINETIME 146
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 35 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 202
GLPKFormulation: Number of values: 132
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 1697
      * PEG2PEGTIME 1679
      * PBTIME 1637
      * ENGINETIME 35
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 31 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 227
GLPKFormulation: Number of values: 137
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 1970
      * PEG2PEGTIME 1931
      * PBTIME 1892
      * ENGINETIME 31
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 69 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 525
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 4601
      * PEG2PEGTIME 4493
      * PBTIME 4401
      * ENGINETIME 69
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 78 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 601
GLPKFormulation: Number of values: 170
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 4933
      * PEG2PEGTIME 4920
      * PBTIME 4828
      * ENGINETIME 78
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 76 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 253
GLPKFormulation: Number of values: 156
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 2325
      * PEG2PEGTIME 2311
      * PBTIME 2215
      * ENGINETIME 77
      * Optimization ratio 31430/31490 = 0.9980946332168943
      * PEG-based Optimization ratio 31430/31490 = 0.9980946332168943
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 43 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 208
GLPKFormulation: Number of values: 137
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 2050
      * PEG2PEGTIME 2037
      * PBTIME 1970
      * ENGINETIME 43
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 47 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 268
GLPKFormulation: Number of values: 160
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 2467
      * PEG2PEGTIME 2446
      * PBTIME 2378
      * ENGINETIME 47
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 91 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 233
GLPKFormulation: Number of values: 169
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedExpected()> SUCCESSFUL
      * Optimization took 2095
      * PEG2PEGTIME 2081
      * PBTIME 1973
      * ENGINETIME 91
      * Optimization ratio 31520/31520 = 1.0
      * PEG-based Optimization ratio 31520/31520 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
   - Processing method <Benchmark: int loopUnrollOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 78 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollOriginal(int)> SUCCESSFUL
      * Optimization took 1748
      * PEG2PEGTIME 1739
      * PBTIME 1656
      * ENGINETIME 79
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopUnrollOriginal(int)>
   - Processing method <Benchmark: int loopUnrollExpected(int)>
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
      * Optimization of method <Benchmark: int loopUnrollExpected(int)> SUCCESSFUL
      * Optimization took 267
      * PEG2PEGTIME 249
      * PBTIME 240
      * ENGINETIME 0
   - Done processing method <Benchmark: int loopUnrollExpected(int)>
   - Processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 51 milliseconds
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
      * Optimization of method <Benchmark: int simpleLoopUnswitchOriginal(int)> SUCCESSFUL
      * Optimization took 2056
      * PEG2PEGTIME 1879
      * PBTIME 1823
      * ENGINETIME 51
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Done optimizing Benchmark
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 2
   - Total methods = 27
+ Fixing bytecode of method <Benchmark: void <init>()>
+ Fixing bytecode of method <Benchmark: int branchHoistingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int branchHoistingExpected(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingExpected()>
+ Fixing bytecode of method <Benchmark: int constantFoldOriginal()>
+ Fixing bytecode of method <Benchmark: int deadLoopDeletionOriginal()>
+ Fixing bytecode of method <Benchmark: int functionInliningFoo()>
+ Fixing bytecode of method <Benchmark: int functionInliningOriginal(int)>
+ Fixing bytecode of method <Benchmark: int functionInliningExpected(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueOriginal(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueExpected(int)>
+ Fixing bytecode of method <Benchmark: int infiniteEffectfulLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int infiniteLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionExpected()>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingExpected(int)>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionExpected()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedExpected()>
+ Fixing bytecode of method <Benchmark: int loopUnrollOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopUnrollExpected(int)>
+ Fixing bytecode of method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Writing class back to optimized/Benchmark.class
+ Total optimization time = 44927 milliseconds
```

### Optimized
```java
15:08:43.513 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int j;
    for (int i = 0;; i = j)
    {
      j = 1 + i;
      if (j >= 500) {
        break;
      }
    }
    i = j + j;
    if (paramInt == 0) {}
    for (i = i;; i = j + i) {
      return i;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    for (int i = 0; i < 500; i = 1 + i) {}
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
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
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 3 > i; i = 1 + i) {}
    return i * 50;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; 150 > i; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      int i = 0;
      paramInt1 = 0;
      while (20 > paramInt1)
      {
        i += paramInt2;
        System.out.println(i);
        paramInt1 += 1;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    int i = 20 * paramInt1;
    if (paramInt2 > i) {}
    for (i = 1 + i;; i = i)
    {
      paramInt2 = 0;
      for (paramInt1 = 0; paramInt2 < 20; paramInt1 = i + paramInt1)
      {
        paramInt2 += 1;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    for (int j = 0; i < paramInt; j = 5 + j) {
      i = 1 + i;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (paramInt = 0;; paramInt = i)
    {
      return paramInt;
      i = 5;
      for (int j = 1; j < paramInt; j = 1 + j) {
        i += 5;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    int j = 0;
    while (300 > i)
    {
      i = 1 + i;
      j = 5 + j;
      System.out.println(j);
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    int i = 0;
    while (300 > i)
    {
      j = 5 + j;
      i += 1;
      System.out.println(j);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      int k = i + 1;
      if (j == 150) {}
      for (k = 3 + k;; k = k)
      {
        j += 1;
        i = k;
        System.out.println(i * 50);
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int i = 0;
    int k = 0;
    if (300 > i)
    {
      int j = 50 + k;
      if (i == 150) {}
      for (j = 150 + j;; j = j)
      {
        i = 1 + i;
        k = j;
        System.out.println(k);
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    if (paramInt < 0)
    {
      while (i < paramInt)
      {
        i += 1;
        System.out.println(i);
        j = 2 + 1;
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
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
^C```

### Optimized
```java
15:12:54.366 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i;
    for (int j = 0;; j = i)
    {
      i = j + 1;
      if (i >= 500) {
        break;
      }
    }
    j = i + i;
    if (paramInt == 0) {
      i = j;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    for (int j = 0; 500 > j; j = 1 + j) {}
    int i = j + j;
    if (paramInt == 0) {}
    for (paramInt = i;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
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
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 3 > i; i = 1 + i) {}
    return 50 * i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    int i = 0;
    while (i < 150) {
      i += 50;
    }
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (int i = 1 + paramInt1;; i = paramInt1)
    {
      paramInt2 = 0;
      for (paramInt1 = 0; paramInt2 < 20; paramInt1 = i + paramInt1)
      {
        paramInt2 = 1 + paramInt2;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (int i = paramInt1 + 1;; i = paramInt1)
    {
      paramInt2 = 0;
      paramInt1 = 0;
      while (20 > paramInt1)
      {
        paramInt2 = i + paramInt2;
        paramInt1 = 1 + paramInt1;
        System.out.println(paramInt2);
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int j = 0;
    for (int i = 0; i < paramInt; i = 1 + i) {
      j += 5;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (int i = 0;; i = i)
    {
      return i;
      int j = 1;
      for (i = 5; paramInt > j; i = 5 + i) {
        j += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    for (int i = 0; 300 > i; i = 1 + i)
    {
      int tmp15_14 = (i + i);
      System.out.println(tmp15_14 + (tmp15_14 + i));
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    int i = 0;
    while (300 > i)
    {
      j += 5;
      i += 1;
      System.out.println(j);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int i = 0;
    int k = 0;
    if (300 > k)
    {
      if (k == 150) {}
      for (int j = i + 4;; j = 1 + i)
      {
        System.out.println(50 * i);
        i = j;
        k = 1 + k;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int j = 0;
    int k = 0;
    if (300 > j)
    {
      int i = 50 + k;
      if (j == 150) {}
      for (i = 150 + i;; i = i)
      {
        j += 1;
        System.out.println(k);
        k = i;
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    for (paramInt = 0; 1 > paramInt; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (j < paramInt)
      {
        j += 1;
        System.out.println(j);
        i = 2 + 1;
      }
    }
    while (j < paramInt)
    {
      j += 1;
      System.out.println(j);
      i += 1;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 471
      * PEG2PEGTIME 380
      * PBTIME 281
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 388 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 474
GLPKFormulation: Number of values: 221
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 4923
      * PEG2PEGTIME 4881
      * PBTIME 4467
      * ENGINETIME 389
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 131 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 388
GLPKFormulation: Number of values: 183
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 3727
      * PEG2PEGTIME 3714
      * PBTIME 3570
      * ENGINETIME 131
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 383
      * PEG2PEGTIME 307
      * PBTIME 283
      * ENGINETIME 6
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 234
      * PEG2PEGTIME 232
      * PBTIME 223
      * ENGINETIME 1
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 240
      * PEG2PEGTIME 238
      * PBTIME 230
      * ENGINETIME 1
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 249
      * PEG2PEGTIME 244
      * PBTIME 232
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 273
      * PEG2PEGTIME 270
      * PBTIME 259
      * ENGINETIME 1
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 273
      * PEG2PEGTIME 264
      * PBTIME 253
      * ENGINETIME 4
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 245
      * PEG2PEGTIME 243
      * PBTIME 234
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 243
      * PEG2PEGTIME 239
      * PBTIME 234
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 239
      * PEG2PEGTIME 233
      * PBTIME 222
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 54 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 236
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 105 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 230
GLPKFormulation: Number of values: 70
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteLoopOriginal()> FAILED
   - Processing method <Benchmark: int loopBasedCodeMotionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 76 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 223
GLPKFormulation: Number of values: 73
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 2025
      * PEG2PEGTIME 2014
      * PBTIME 1906
      * ENGINETIME 76
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 133 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 2022
      * PEG2PEGTIME 2010
      * PBTIME 1867
      * ENGINETIME 133
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 57 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 227
GLPKFormulation: Number of values: 137
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 2058
      * PEG2PEGTIME 2033
      * PBTIME 1966
      * ENGINETIME 57
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 41 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 227
GLPKFormulation: Number of values: 137
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 2011
      * PEG2PEGTIME 1991
      * PBTIME 1940
      * ENGINETIME 41
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 73 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 556
GLPKFormulation: Number of values: 187
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 4758
      * PEG2PEGTIME 4749
      * PBTIME 4665
      * ENGINETIME 73
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 84 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 601
GLPKFormulation: Number of values: 170
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 5044
      * PEG2PEGTIME 5029
      * PBTIME 4931
      * ENGINETIME 84
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 45 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 253
GLPKFormulation: Number of values: 156
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 2316
      * PEG2PEGTIME 2301
      * PBTIME 2242
      * ENGINETIME 45
      * Optimization ratio 31430/31490 = 0.9980946332168943
      * PEG-based Optimization ratio 31430/31490 = 0.9980946332168943
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 40 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 208
GLPKFormulation: Number of values: 138
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 1712
      * PEG2PEGTIME 1692
      * PBTIME 1637
      * ENGINETIME 40
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 129 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 276
GLPKFormulation: Number of values: 178
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 2489
      * PEG2PEGTIME 2467
      * PBTIME 2324
      * ENGINETIME 129
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 46 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 236
GLPKFormulation: Number of values: 164
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedExpected()> SUCCESSFUL
      * Optimization took 2303
      * PEG2PEGTIME 2264
      * PBTIME 2203
      * ENGINETIME 46
      * Optimization ratio 31520/31520 = 1.0
      * PEG-based Optimization ratio 31520/31520 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
   - Processing method <Benchmark: int loopUnrollOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 54 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollOriginal(int)> SUCCESSFUL
      * Optimization took 1958
      * PEG2PEGTIME 1944
      * PBTIME 1879
      * ENGINETIME 54
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopUnrollOriginal(int)>
   - Processing method <Benchmark: int loopUnrollExpected(int)>
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
      * Optimization of method <Benchmark: int loopUnrollExpected(int)> SUCCESSFUL
      * Optimization took 261
      * PEG2PEGTIME 246
      * PBTIME 235
      * ENGINETIME 1
   - Done processing method <Benchmark: int loopUnrollExpected(int)>
   - Processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 25 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 218
GLPKFormulation: Number of values: 150
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int simpleLoopUnswitchOriginal(int)> SUCCESSFUL
      * Optimization took 2175
      * PEG2PEGTIME 2083
      * PBTIME 2052
      * ENGINETIME 26
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Done optimizing Benchmark
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 2
   - Total methods = 27
+ Fixing bytecode of method <Benchmark: void <init>()>
+ Fixing bytecode of method <Benchmark: int branchHoistingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int branchHoistingExpected(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingExpected()>
+ Fixing bytecode of method <Benchmark: int constantFoldOriginal()>
+ Fixing bytecode of method <Benchmark: int deadLoopDeletionOriginal()>
+ Fixing bytecode of method <Benchmark: int functionInliningFoo()>
+ Fixing bytecode of method <Benchmark: int functionInliningOriginal(int)>
+ Fixing bytecode of method <Benchmark: int functionInliningExpected(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueOriginal(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueExpected(int)>
+ Fixing bytecode of method <Benchmark: int infiniteEffectfulLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int infiniteLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionExpected()>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingExpected(int)>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionExpected()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedExpected()>
+ Fixing bytecode of method <Benchmark: int loopUnrollOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopUnrollExpected(int)>
+ Fixing bytecode of method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Writing class back to optimized/Benchmark.class
+ Total optimization time = 47834 milliseconds
```

### Optimized
```java
15:14:22.098 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int j;
    for (int i = 0;; i = j)
    {
      j = i + 1;
      if (500 <= j) {
        break;
      }
    }
    i = j + j;
    if (paramInt == 0) {
      i = i;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int i = 0;
    while (500 > i) {
      i += 1;
    }
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = i + j) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
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
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 3 > i; i = 1 + i) {}
    return 50 * i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    int i = 0;
    while (i < 150) {
      i += 50;
    }
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (paramInt1 = 1 + paramInt1;; paramInt1 = paramInt1)
    {
      int i = 0;
      for (paramInt2 = 0; 20 > i; paramInt2 = paramInt1 + paramInt2)
      {
        i = 1 + i;
        System.out.println(paramInt2);
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (i < paramInt2) {}
    for (paramInt1 = i + 1;; paramInt1 = i)
    {
      paramInt2 = 0;
      i = 0;
      while (20 > i)
      {
        paramInt2 += paramInt1;
        System.out.println(paramInt2);
        i += 1;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (paramInt > i)
    {
      i = 1 + i;
      j += 5;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (int j = 0;; j = j)
    {
      return j;
      int i = 1;
      j = 5;
      while (paramInt > i)
      {
        i += 1;
        j += 5;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int j = 0;
    int i = 0;
    while (j < 300)
    {
      System.out.println(i);
      j += 1;
      i += 5;
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int i = 0;
    int j = 0;
    while (j < 300)
    {
      i += 5;
      j = 1 + j;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      int k = i + 1;
      if (j == 150) {}
      for (k = 3 + k;; k = k)
      {
        j += 1;
        System.out.println(50 * i);
        i = k;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int j = 0;
    int k = 0;
    if (300 > j)
    {
      int i = 50 + k;
      if (j == 150) {
        i += 150;
      }
      for (;;)
      {
        System.out.println(k);
        j += 1;
        k = i;
        break;
        i = i;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    for (paramInt = 0; 1 > paramInt; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    if (paramInt < 0) {
      while (paramInt > j)
      {
        System.out.println(j);
        i = 1 + 2;
        j += 1;
      }
    }
    while (paramInt > j)
    {
      System.out.println(j);
      i = 1 + i;
      j += 1;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 736
      * PEG2PEGTIME 609
      * PBTIME 455
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 403 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 463
GLPKFormulation: Number of values: 215
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 5200
      * PEG2PEGTIME 5144
      * PBTIME 4713
      * ENGINETIME 403
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 162 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 389
GLPKFormulation: Number of values: 184
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 3854
      * PEG2PEGTIME 3836
      * PBTIME 3652
      * ENGINETIME 162
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 307
      * PEG2PEGTIME 299
      * PBTIME 277
      * ENGINETIME 6
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 240
      * PEG2PEGTIME 239
      * PBTIME 232
      * ENGINETIME 0
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 234
      * PEG2PEGTIME 231
      * PBTIME 224
      * ENGINETIME 1
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 231
      * PEG2PEGTIME 227
      * PBTIME 220
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 304
      * PEG2PEGTIME 299
      * PBTIME 292
      * ENGINETIME 1
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 278
      * PEG2PEGTIME 272
      * PBTIME 254
      * ENGINETIME 6
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 248
      * PEG2PEGTIME 244
      * PBTIME 236
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 231
      * PEG2PEGTIME 228
      * PBTIME 222
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 236
      * PEG2PEGTIME 233
      * PBTIME 226
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 54 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 236
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 112 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 230
GLPKFormulation: Number of values: 70
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteLoopOriginal()> FAILED
   - Processing method <Benchmark: int loopBasedCodeMotionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 154 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 223
GLPKFormulation: Number of values: 73
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 2014
      * PEG2PEGTIME 2005
      * PBTIME 1830
      * ENGINETIME 154
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 279 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 2249
      * PEG2PEGTIME 2237
      * PBTIME 1950
      * ENGINETIME 279
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 35 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 200
GLPKFormulation: Number of values: 127
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 2099
      * PEG2PEGTIME 2060
      * PBTIME 2014
      * ENGINETIME 35
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 37 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 200
GLPKFormulation: Number of values: 131
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 1834
      * PEG2PEGTIME 1816
      * PBTIME 1759
      * ENGINETIME 37
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 78 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 525
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 4354
      * PEG2PEGTIME 4344
      * PBTIME 4258
      * ENGINETIME 78
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 65 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 633
GLPKFormulation: Number of values: 210
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 5504
      * PEG2PEGTIME 5482
      * PBTIME 5343
      * ENGINETIME 65
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 44 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 255
GLPKFormulation: Number of values: 158
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 2359
      * PEG2PEGTIME 2345
      * PBTIME 2281
      * ENGINETIME 44
      * Optimization ratio 31430/31490 = 0.9980946332168943
      * PEG-based Optimization ratio 31430/31490 = 0.9980946332168943
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 52 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 208
GLPKFormulation: Number of values: 136
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 2458
      * PEG2PEGTIME 2435
      * PBTIME 2375
      * ENGINETIME 52
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 67 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 282
GLPKFormulation: Number of values: 175
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 2835
      * PEG2PEGTIME 2809
      * PBTIME 2720
      * ENGINETIME 67
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 121 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 235
GLPKFormulation: Number of values: 166
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedExpected()> SUCCESSFUL
      * Optimization took 2607
      * PEG2PEGTIME 2566
      * PBTIME 2433
      * ENGINETIME 121
      * Optimization ratio 31520/31520 = 1.0
      * PEG-based Optimization ratio 31520/31520 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
   - Processing method <Benchmark: int loopUnrollOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 106 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollOriginal(int)> SUCCESSFUL
      * Optimization took 2140
      * PEG2PEGTIME 2128
      * PBTIME 2012
      * ENGINETIME 106
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopUnrollOriginal(int)>
   - Processing method <Benchmark: int loopUnrollExpected(int)>
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
      * Optimization of method <Benchmark: int loopUnrollExpected(int)> SUCCESSFUL
      * Optimization took 553
      * PEG2PEGTIME 513
      * PBTIME 503
      * ENGINETIME 1
   - Done processing method <Benchmark: int loopUnrollExpected(int)>
   - Processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 53 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 219
GLPKFormulation: Number of values: 153
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int simpleLoopUnswitchOriginal(int)> SUCCESSFUL
      * Optimization took 2451
      * PEG2PEGTIME 2260
      * PBTIME 2198
      * ENGINETIME 53
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
   - Done processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Done optimizing Benchmark
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 2
   - Total methods = 27
+ Fixing bytecode of method <Benchmark: void <init>()>
+ Fixing bytecode of method <Benchmark: int branchHoistingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int branchHoistingExpected(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingExpected()>
+ Fixing bytecode of method <Benchmark: int constantFoldOriginal()>
+ Fixing bytecode of method <Benchmark: int deadLoopDeletionOriginal()>
+ Fixing bytecode of method <Benchmark: int functionInliningFoo()>
+ Fixing bytecode of method <Benchmark: int functionInliningOriginal(int)>
+ Fixing bytecode of method <Benchmark: int functionInliningExpected(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueOriginal(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueExpected(int)>
+ Fixing bytecode of method <Benchmark: int infiniteEffectfulLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int infiniteLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionExpected()>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingExpected(int)>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionExpected()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedExpected()>
+ Fixing bytecode of method <Benchmark: int loopUnrollOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopUnrollExpected(int)>
+ Fixing bytecode of method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Writing class back to optimized/Benchmark.class
+ Total optimization time = 50894 milliseconds
```

### Optimized
```java
15:59:47.627 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int j;
    for (int i = 0;; i = j)
    {
      j = 1 + i;
      if (j >= 500) {
        break;
      }
    }
    i = j + j;
    if (paramInt == 0) {}
    for (i = i;; i = j + i) {
      return i;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    for (int j = 0; j < 500; j = 1 + j) {}
    int i = j + j;
    if (paramInt == 0) {}
    for (paramInt = i;; paramInt = i + j) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
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
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return functionInliningFoo() + 1;
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    int i = 0;
    while (i < 3) {
      i += 1;
    }
    return i * 50;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; i < 150; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {}
    for (int i = paramInt1 + 1;; i = paramInt1)
    {
      paramInt2 = 0;
      paramInt1 = 0;
      while (20 > paramInt2)
      {
        paramInt2 = 1 + paramInt2;
        paramInt1 = i + paramInt1;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {}
    for (paramInt1 = 1 + paramInt1;; paramInt1 = paramInt1)
    {
      paramInt2 = 0;
      for (int i = 0; 20 > i; i = 1 + i)
      {
        paramInt2 = paramInt1 + paramInt2;
        System.out.println(paramInt2);
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (j < paramInt)
    {
      i += 5;
      j += 1;
    }
    return i;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (int i = 0;; i = i)
    {
      return i;
      i = 5;
      for (int j = 1; paramInt > j; j = 1 + j) {
        i = 5 + i;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int j = 0;
    int i = 0;
    while (300 > j)
    {
      System.out.println(i);
      j += 1;
      i += 5;
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int i = 0;
    for (int j = 0; j < 300; j = 1 + j)
    {
      i += 5;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (300 > j)
    {
      int k = 1 + i;
      if (j == 150) {}
      for (k = 3 + k;; k = k)
      {
        j += 1;
        i = k;
        System.out.println(50 * i);
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int k = 0;
    int j = 0;
    if (j < 300)
    {
      int i = 50 + k;
      if (j == 150) {}
      for (i = 150 + i;; i = i)
      {
        k = i;
        System.out.println(k);
        j += 1;
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (paramInt > i)
      {
        j = 1 + 2;
        System.out.println(i);
        i += 1;
      }
    }
    while (paramInt > i)
    {
      System.out.println(i);
      j = 1 + j;
      i += 1;
    }
    return j;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 648
      * PEG2PEGTIME 540
      * PBTIME 416
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 485 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 463
GLPKFormulation: Number of values: 215
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 6523
      * PEG2PEGTIME 6471
      * PBTIME 5949
      * ENGINETIME 485
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 224 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 390
GLPKFormulation: Number of values: 185
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 5618
      * PEG2PEGTIME 5590
      * PBTIME 5340
      * ENGINETIME 224
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 326
      * PEG2PEGTIME 317
      * PBTIME 294
      * ENGINETIME 6
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 250
      * PEG2PEGTIME 248
      * PBTIME 239
      * ENGINETIME 0
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 250
      * PEG2PEGTIME 247
      * PBTIME 238
      * ENGINETIME 1
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 261
      * PEG2PEGTIME 258
      * PBTIME 250
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 242
      * PEG2PEGTIME 240
      * PBTIME 230
      * ENGINETIME 2
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 283
      * PEG2PEGTIME 276
      * PBTIME 265
      * ENGINETIME 4
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 359
      * PEG2PEGTIME 355
      * PBTIME 347
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 240
      * PEG2PEGTIME 239
      * PBTIME 232
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 243
      * PEG2PEGTIME 239
      * PBTIME 232
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 69 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 236
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 155 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 230
GLPKFormulation: Number of values: 70
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteLoopOriginal()> FAILED
   - Processing method <Benchmark: int loopBasedCodeMotionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 163 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 223
GLPKFormulation: Number of values: 73
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 2813
      * PEG2PEGTIME 2803
      * PBTIME 2626
      * ENGINETIME 163
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 93 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 2535
      * PEG2PEGTIME 2525
      * PBTIME 2421
      * ENGINETIME 93
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 51 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 227
GLPKFormulation: Number of values: 137
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 3059
      * PEG2PEGTIME 3013
      * PBTIME 2950
      * ENGINETIME 51
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 54 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 203
GLPKFormulation: Number of values: 132
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 2639
      * PEG2PEGTIME 2575
      * PBTIME 2505
      * ENGINETIME 55
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 98 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 525
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 6327
      * PEG2PEGTIME 6314
      * PBTIME 6205
      * ENGINETIME 98
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 128 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 630
GLPKFormulation: Number of values: 215
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 7746
      * PEG2PEGTIME 7728
      * PBTIME 7580
      * ENGINETIME 129
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 241 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 255
GLPKFormulation: Number of values: 158
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 3511
      * PEG2PEGTIME 3494
      * PBTIME 3240
      * ENGINETIME 241
      * Optimization ratio 31430/31490 = 0.9980946332168943
      * PEG-based Optimization ratio 31430/31490 = 0.9980946332168943
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 54 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 208
GLPKFormulation: Number of values: 137
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 2573
      * PEG2PEGTIME 2562
      * PBTIME 2495
      * ENGINETIME 54
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 57 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 273
GLPKFormulation: Number of values: 163
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 3542
      * PEG2PEGTIME 3511
      * PBTIME 3446
      * ENGINETIME 57
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 56 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 261
GLPKFormulation: Number of values: 179
```

### Optimized
```java
14:46:11.019 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (i < 500)
    {
      i++;
      if (paramInt == 0) {
        j = i * 2;
      } else {
        j = i * 3;
      }
    }
    return j;
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    for (int i = 0; i < 500; i = 1 + i) {}
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = i + j) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
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
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return functionInliningFoo() + 1;
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    int i = 0;
    while (i < 150) {
      i += 50;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    int i = 0;
    while (150 > i) {
      i += 50;
    }
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = 1 + paramInt1;; paramInt2 = paramInt1)
    {
      int i = 0;
      paramInt1 = 0;
      while (i < 20)
      {
        i = 1 + i;
        paramInt1 = paramInt2 + paramInt1;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      int i = 0;
      paramInt1 = 0;
      while (i < 20)
      {
        i = 1 + i;
        paramInt1 = paramInt2 + paramInt1;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int j = 0;
    for (int i = 0; paramInt > j; i = 5 + i) {
      j = 1 + j;
    }
    return i;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (paramInt = 0;; paramInt = i)
    {
      return paramInt;
      i = 5;
      for (int j = 1; j < paramInt; j = 1 + j) {
        i = 5 + i;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (i < 1500)
    {
      i = 5 + i;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    int i = 0;
    while (j < 300)
    {
      j += 1;
      i += 5;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      if (j == 150) {}
      for (int k = i + 4;; k = i + 1)
      {
        j += 1;
        System.out.println(i * 50);
        i = k;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      if (j == 150) {}
      for (int k = 200 + i;; k = 50 + i)
      {
        j += 1;
        System.out.println(i);
        i = k;
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    for (paramInt = 0; 1 > paramInt; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    if (paramInt < 0)
    {
      while (paramInt > i)
      {
        System.out.println(i);
        i = 1 + i;
        j = 1 + 2;
      }
      return j;
    }
    for (;;)
    {
      if (paramInt > i)
      {
        System.out.println(i);
        i = 1 + i;
      }
    }
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '250'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 726
      * PEG2PEGTIME 591
      * PBTIME 436
      * ENGINETIME 11
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 509 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 463
GLPKFormulation: Number of values: 215
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 6604
      * PEG2PEGTIME 6551
      * PBTIME 6007
      * ENGINETIME 510
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 166 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 389
GLPKFormulation: Number of values: 184
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 5568
      * PEG2PEGTIME 5544
      * PBTIME 5343
      * ENGINETIME 166
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 366
      * PEG2PEGTIME 352
      * PBTIME 310
      * ENGINETIME 17
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 249
      * PEG2PEGTIME 246
      * PBTIME 235
      * ENGINETIME 1
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 251
      * PEG2PEGTIME 248
      * PBTIME 239
      * ENGINETIME 2
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 290
      * PEG2PEGTIME 285
      * PBTIME 277
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 240
      * PEG2PEGTIME 239
      * PBTIME 231
      * ENGINETIME 0
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 398
      * PEG2PEGTIME 389
      * PBTIME 379
      * ENGINETIME 3
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 375
      * PEG2PEGTIME 371
      * PBTIME 359
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 265
      * PEG2PEGTIME 262
      * PBTIME 250
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 243
      * PEG2PEGTIME 239
      * PBTIME 230
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 81 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 236
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 506 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 230
GLPKFormulation: Number of values: 70
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteLoopOriginal()> FAILED
   - Processing method <Benchmark: int loopBasedCodeMotionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 379 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 223
GLPKFormulation: Number of values: 73
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 3652
      * PEG2PEGTIME 3625
      * PBTIME 3202
      * ENGINETIME 380
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 101 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 207
GLPKFormulation: Number of values: 64
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 2950
      * PEG2PEGTIME 2937
      * PBTIME 2821
      * ENGINETIME 102
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 59 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 227
GLPKFormulation: Number of values: 138
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 2897
      * PEG2PEGTIME 2874
      * PBTIME 2798
      * ENGINETIME 60
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 48 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 230
GLPKFormulation: Number of values: 140
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 2999
      * PEG2PEGTIME 2978
      * PBTIME 2916
      * ENGINETIME 48
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 235 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 525
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 6988
      * PEG2PEGTIME 6973
      * PBTIME 6728
      * ENGINETIME 235
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 144 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 629
GLPKFormulation: Number of values: 208
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 8119
      * PEG2PEGTIME 8103
      * PBTIME 7933
      * ENGINETIME 145
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 250 after 63 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 258
GLPKFormulation: Number of values: 160
```

### Optimized
```java
15:24:57.778 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (i < 500)
    {
      i++;
      if (paramInt == 0) {
        j = i * 2;
      } else {
        j = i * 3;
      }
    }
    return j;
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int i = 0;
    while (i < 500) {
      i += 1;
    }
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = i + j) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
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
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return functionInliningFoo() + 1;
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    int i = 0;
    while (150 > i) {
      i += 50;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; i < 150; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (i < paramInt2) {}
    for (paramInt2 = 1 + i;; paramInt2 = i)
    {
      i = 0;
      for (paramInt1 = 0; 20 > paramInt1; paramInt1 = 1 + paramInt1)
      {
        System.out.println(i);
        i = paramInt2 + i;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    int i = 20 * paramInt1;
    if (paramInt2 > i) {}
    for (paramInt1 = 1 + i;; paramInt1 = i)
    {
      i = 0;
      paramInt2 = 0;
      while (20 > i)
      {
        System.out.println(paramInt2);
        i += 1;
        paramInt2 += paramInt1;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    for (int j = 0; paramInt > i; j = 5 + j) {
      i += 1;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (int j = 0;; j = j)
    {
      return j;
      int i = 1;
      for (j = 5; paramInt > i; j = 5 + j) {
        i += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (i < 1500)
    {
      System.out.println(i);
      i += 5;
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    int i = 0;
    while (300 > j)
    {
      j = 1 + j;
      System.out.println(i);
      i += 5;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int i = 0;
    int j = 0;
    if (300 > j)
    {
      int k = i + 1;
      if (j == 150) {
        k += 3;
      }
      for (;;)
      {
        i = k;
        j = 1 + j;
        System.out.println(i * 50);
        break;
        k = k;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int k = 0;
    int j = 0;
    if (j < 300)
    {
      int i = 50 + k;
      if (j == 150) {
        i += 150;
      }
      for (;;)
      {
        System.out.println(k);
        k = i;
        j = 1 + j;
        break;
        i = i;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    if (paramInt < 0)
    {
      while (paramInt > i)
      {
        System.out.println(i);
        i = 1 + i;
        j = 1 + 2;
      }
      return j;
    }
    for (;;)
    {
      if (paramInt > i)
      {
        System.out.println(i);
        i = 1 + i;
      }
    }
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
