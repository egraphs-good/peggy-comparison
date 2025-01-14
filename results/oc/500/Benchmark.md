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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmp_folder': 'tmp', 'pb': 'glpk', 'eto': '500'}

### Peggy output
```
+ !!! CRITICAL ERROR: Error parsing command line: Unrecognized option: tmp_folder !!!
```

### Optimized
```java
18:03:42.658 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmp_folder': 'tmp', 'pb': 'glpk', 'eto': '500'}

### Peggy output
```
Cannot connect to the Docker daemon at unix:///Users/kirsten/.docker/run/docker.sock. Is the docker daemon running?
```

### Optimized
```java
13:49:03.985 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

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
      * Optimization took 474
      * PEG2PEGTIME 373
      * PBTIME 281
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 633 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 898
GLPKFormulation: Number of values: 316
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 8376
      * PEG2PEGTIME 8329
      * PBTIME 7668
      * ENGINETIME 633
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 203 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 793
GLPKFormulation: Number of values: 348
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 7590
      * PEG2PEGTIME 7573
      * PBTIME 7354
      * ENGINETIME 203
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
      * Optimization took 308
      * PEG2PEGTIME 300
      * PBTIME 287
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
      * Optimization took 234
      * PEG2PEGTIME 231
      * PBTIME 226
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
      * Optimization took 259
      * PEG2PEGTIME 257
      * PBTIME 250
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
      * Optimization took 234
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
      * Optimization took 264
      * PEG2PEGTIME 259
      * PBTIME 250
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
      * Optimization took 244
      * PEG2PEGTIME 241
      * PBTIME 234
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
      * Optimization took 232
      * PEG2PEGTIME 230
      * PBTIME 223
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
      * PEG2PEGTIME 233
      * PBTIME 226
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 108 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 545
GLPKFormulation: Number of values: 395
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
      * Engine reached iteration bound of 500 after 306 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
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
      * Engine reached iteration bound of 500 after 182 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 3295
      * PEG2PEGTIME 3234
      * PBTIME 3037
      * ENGINETIME 182
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 361 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 4187
      * PEG2PEGTIME 4178
      * PBTIME 3774
      * ENGINETIME 362
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 136 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 489
GLPKFormulation: Number of values: 277
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 4645
      * PEG2PEGTIME 4619
      * PBTIME 4453
      * ENGINETIME 137
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 107 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 489
GLPKFormulation: Number of values: 272
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 4458
      * PEG2PEGTIME 4436
      * PBTIME 4315
      * ENGINETIME 107
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 439 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1343
GLPKFormulation: Number of values: 222
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 12072
      * PEG2PEGTIME 12046
      * PBTIME 11598
      * ENGINETIME 440
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 218 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1055
GLPKFormulation: Number of values: 172
```

### Optimized
```java
13:55:30.158 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

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
      * Optimization took 507
      * PEG2PEGTIME 405
      * PBTIME 329
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 597 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 898
GLPKFormulation: Number of values: 316
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 8385
      * PEG2PEGTIME 8340
      * PBTIME 7712
      * ENGINETIME 598
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 185 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 793
GLPKFormulation: Number of values: 348
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 7228
      * PEG2PEGTIME 7213
      * PBTIME 7011
      * ENGINETIME 185
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
      * PBTIME 282
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
      * Optimization took 240
      * PEG2PEGTIME 238
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
      * Optimization took 244
      * PEG2PEGTIME 239
      * PBTIME 232
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
      * Optimization took 278
      * PEG2PEGTIME 275
      * PBTIME 269
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
      * Optimization took 232
      * PEG2PEGTIME 230
      * PBTIME 222
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
      * Optimization took 268
      * PEG2PEGTIME 261
      * PBTIME 251
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
      * Optimization took 246
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
      * Optimization took 234
      * PEG2PEGTIME 231
      * PBTIME 225
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
      * Optimization took 226
      * PEG2PEGTIME 222
      * PBTIME 216
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 84 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 542
GLPKFormulation: Number of values: 394
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
      * Engine reached iteration bound of 500 after 320 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
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
      * Engine reached iteration bound of 500 after 176 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 3231
      * PEG2PEGTIME 3218
      * PBTIME 3031
      * ENGINETIME 176
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 148 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 3117
      * PEG2PEGTIME 3109
      * PBTIME 2949
      * ENGINETIME 148
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 80 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 489
GLPKFormulation: Number of values: 274
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 4492
      * PEG2PEGTIME 4468
      * PBTIME 4380
      * ENGINETIME 80
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 97 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 489
GLPKFormulation: Number of values: 272
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 4381
      * PEG2PEGTIME 4364
      * PBTIME 4257
      * ENGINETIME 97
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 235 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1094
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 9889
      * PEG2PEGTIME 9874
      * PBTIME 9630
      * ENGINETIME 235
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 208 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1073
GLPKFormulation: Number of values: 179
```

### Optimized
```java
14:02:24.779 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

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
      * Optimization took 430
      * PEG2PEGTIME 348
      * PBTIME 272
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 639 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 898
GLPKFormulation: Number of values: 316
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 8178
      * PEG2PEGTIME 8144
      * PBTIME 7474
      * ENGINETIME 639
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 211 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 793
GLPKFormulation: Number of values: 348
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 7396
      * PEG2PEGTIME 7381
      * PBTIME 7152
      * ENGINETIME 212
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
      * PEG2PEGTIME 292
      * PBTIME 278
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
      * Optimization took 233
      * PEG2PEGTIME 231
      * PBTIME 225
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
      * PEG2PEGTIME 234
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
      * Optimization took 315
      * PEG2PEGTIME 310
      * PBTIME 304
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
      * Optimization took 236
      * PEG2PEGTIME 233
      * PBTIME 225
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
      * Optimization took 319
      * PEG2PEGTIME 313
      * PBTIME 298
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
      * Optimization took 283
      * PEG2PEGTIME 280
      * PBTIME 273
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
      * Optimization took 246
      * PEG2PEGTIME 242
      * PBTIME 234
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
      * Optimization took 240
      * PEG2PEGTIME 237
      * PBTIME 229
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 83 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 539
GLPKFormulation: Number of values: 391
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
      * Engine reached iteration bound of 500 after 321 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
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
      * Engine reached iteration bound of 500 after 172 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 3176
      * PEG2PEGTIME 3166
      * PBTIME 2984
      * ENGINETIME 172
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 154 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 3104
      * PEG2PEGTIME 3099
      * PBTIME 2931
      * ENGINETIME 155
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 140 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 483
GLPKFormulation: Number of values: 273
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 4396
      * PEG2PEGTIME 4376
      * PBTIME 4230
      * ENGINETIME 140
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 109 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 483
GLPKFormulation: Number of values: 273
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 4810
      * PEG2PEGTIME 4792
      * PBTIME 4675
      * ENGINETIME 109
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 244 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1093
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 9124
      * PEG2PEGTIME 9115
      * PBTIME 8860
      * ENGINETIME 245
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 743 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1069
GLPKFormulation: Number of values: 179
```

### Optimized
```java
14:24:42.158 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

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
      * Optimization took 485
      * PEG2PEGTIME 386
      * PBTIME 283
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 661 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 898
GLPKFormulation: Number of values: 316
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 9682
      * PEG2PEGTIME 9644
      * PBTIME 8949
      * ENGINETIME 661
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 236 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 793
GLPKFormulation: Number of values: 348
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 9372
      * PEG2PEGTIME 9356
      * PBTIME 9091
      * ENGINETIME 236
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
      * Optimization took 339
      * PEG2PEGTIME 331
      * PBTIME 315
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
      * Optimization took 243
      * PEG2PEGTIME 240
      * PBTIME 233
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
      * Optimization took 235
      * PEG2PEGTIME 233
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
      * Optimization took 282
      * PEG2PEGTIME 279
      * PBTIME 273
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
      * Optimization took 234
      * PEG2PEGTIME 232
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
      * PEG2PEGTIME 264
      * PBTIME 256
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
      * Optimization took 253
      * PEG2PEGTIME 249
      * PBTIME 242
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
      * Optimization took 247
      * PEG2PEGTIME 244
      * PBTIME 237
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
      * Optimization took 234
      * PEG2PEGTIME 231
      * PBTIME 225
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 99 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 545
GLPKFormulation: Number of values: 395
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
      * Engine reached iteration bound of 500 after 297 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
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
      * Engine reached iteration bound of 500 after 208 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 3866
      * PEG2PEGTIME 3854
      * PBTIME 3634
      * ENGINETIME 208
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 182 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 3705
      * PEG2PEGTIME 3696
      * PBTIME 3499
      * ENGINETIME 182
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 120 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 489
GLPKFormulation: Number of values: 272
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 4931
      * PEG2PEGTIME 4908
      * PBTIME 4775
      * ENGINETIME 120
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 111 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 489
GLPKFormulation: Number of values: 272
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 5169
      * PEG2PEGTIME 5140
      * PBTIME 5019
      * ENGINETIME 111
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 862 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1321
GLPKFormulation: Number of values: 195
```

### Optimized
```java
14:29:09.686 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

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
      * Optimization took 425
      * PEG2PEGTIME 353
      * PBTIME 281
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 588 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 898
GLPKFormulation: Number of values: 316
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 8116
      * PEG2PEGTIME 8075
      * PBTIME 7451
      * ENGINETIME 589
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 198 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 796
GLPKFormulation: Number of values: 350
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 7474
      * PEG2PEGTIME 7461
      * PBTIME 7247
      * ENGINETIME 198
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
      * PBTIME 282
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
      * Optimization took 239
      * PEG2PEGTIME 235
      * PBTIME 229
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
      * Optimization took 283
      * PEG2PEGTIME 279
      * PBTIME 269
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
      * PEG2PEGTIME 229
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
      * Optimization took 265
      * PEG2PEGTIME 260
      * PBTIME 251
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
      * Optimization took 246
      * PEG2PEGTIME 243
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
      * Optimization took 238
      * PEG2PEGTIME 235
      * PBTIME 228
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
      * Optimization took 235
      * PEG2PEGTIME 231
      * PBTIME 224
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 89 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 540
GLPKFormulation: Number of values: 392
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
      * Engine reached iteration bound of 500 after 275 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
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
      * Engine reached iteration bound of 500 after 253 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 3298
      * PEG2PEGTIME 3281
      * PBTIME 3018
      * ENGINETIME 253
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 173 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 3114
      * PEG2PEGTIME 3108
      * PBTIME 2923
      * ENGINETIME 173
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 77 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 489
GLPKFormulation: Number of values: 274
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 4318
      * PEG2PEGTIME 4286
      * PBTIME 4204
      * ENGINETIME 77
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 80 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 483
GLPKFormulation: Number of values: 269
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 4334
      * PEG2PEGTIME 4318
      * PBTIME 4228
      * ENGINETIME 80
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 204 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1332
GLPKFormulation: Number of values: 216
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 10616
      * PEG2PEGTIME 10609
      * PBTIME 10399
      * ENGINETIME 204
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 791 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1060
GLPKFormulation: Number of values: 223
```

### Optimized
```java
14:43:24.285 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

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
      * Optimization took 435
      * PEG2PEGTIME 357
      * PBTIME 272
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 715 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 898
GLPKFormulation: Number of values: 316
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 8288
      * PEG2PEGTIME 8248
      * PBTIME 7480
      * ENGINETIME 716
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 176 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 796
GLPKFormulation: Number of values: 350
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 7808
      * PEG2PEGTIME 7793
      * PBTIME 7604
      * ENGINETIME 176
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
      * Optimization took 293
      * PEG2PEGTIME 286
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
      * Optimization took 233
      * PEG2PEGTIME 231
      * PBTIME 226
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
      * PEG2PEGTIME 239
      * PBTIME 231
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
      * Optimization took 428
      * PEG2PEGTIME 419
      * PBTIME 383
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
      * Optimization took 240
      * PEG2PEGTIME 236
      * PBTIME 230
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
      * Optimization took 270
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
      * Optimization took 267
      * PEG2PEGTIME 264
      * PBTIME 257
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
      * Optimization took 234
      * PEG2PEGTIME 232
      * PBTIME 224
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
      * Optimization took 252
      * PEG2PEGTIME 247
      * PBTIME 240
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 115 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 545
GLPKFormulation: Number of values: 395
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
      * Engine reached iteration bound of 500 after 260 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
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
      * Engine reached iteration bound of 500 after 146 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 3199
      * PEG2PEGTIME 3173
      * PBTIME 3018
      * ENGINETIME 146
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 142 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 3314
      * PEG2PEGTIME 3297
      * PBTIME 3141
      * ENGINETIME 142
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 92 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 488
GLPKFormulation: Number of values: 276
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 4296
      * PEG2PEGTIME 4274
      * PBTIME 4174
      * ENGINETIME 92
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 89 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 483
GLPKFormulation: Number of values: 271
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 4285
      * PEG2PEGTIME 4269
      * PBTIME 4170
      * ENGINETIME 90
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 206 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1093
GLPKFormulation: Number of values: 177
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 9381
      * PEG2PEGTIME 9354
      * PBTIME 9140
      * ENGINETIME 206
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 198 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1073
GLPKFormulation: Number of values: 180
```

### Optimized
```java
14:53:29.088 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

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
      * Optimization took 441
      * PEG2PEGTIME 361
      * PBTIME 281
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 560 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 898
GLPKFormulation: Number of values: 300
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 7977
      * PEG2PEGTIME 7942
      * PBTIME 7361
      * ENGINETIME 561
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 343 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 806
GLPKFormulation: Number of values: 354
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 7471
      * PEG2PEGTIME 7455
      * PBTIME 7094
      * ENGINETIME 345
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
      * Optimization took 320
      * PEG2PEGTIME 311
      * PBTIME 297
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
      * Optimization took 241
      * PEG2PEGTIME 237
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
      * Optimization took 241
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
      * Optimization took 265
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
      * Optimization took 245
      * PEG2PEGTIME 240
      * PBTIME 234
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
      * Optimization took 267
      * PEG2PEGTIME 262
      * PBTIME 251
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
      * Optimization took 243
      * PEG2PEGTIME 240
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
      * Optimization took 242
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
      * Optimization took 239
      * PEG2PEGTIME 233
      * PBTIME 226
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 92 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 545
GLPKFormulation: Number of values: 395
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
      * Engine reached iteration bound of 500 after 279 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
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
      * Engine reached iteration bound of 500 after 190 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 3010
      * PEG2PEGTIME 3001
      * PBTIME 2802
      * ENGINETIME 190
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 176 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 3114
      * PEG2PEGTIME 3098
      * PBTIME 2912
      * ENGINETIME 176
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 118 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 485
GLPKFormulation: Number of values: 277
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 4400
      * PEG2PEGTIME 4375
      * PBTIME 4247
      * ENGINETIME 119
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 93 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 483
GLPKFormulation: Number of values: 273
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 4458
      * PEG2PEGTIME 4441
      * PBTIME 4340
      * ENGINETIME 93
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 209 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1095
GLPKFormulation: Number of values: 177
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 9063
      * PEG2PEGTIME 9053
      * PBTIME 8836
      * ENGINETIME 209
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 171 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1038
GLPKFormulation: Number of values: 207
```

### Optimized
```java
14:59:44.832 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

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
      * Optimization took 486
      * PEG2PEGTIME 408
      * PBTIME 318
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 609 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 898
GLPKFormulation: Number of values: 316
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 8093
      * PEG2PEGTIME 8048
      * PBTIME 7408
      * ENGINETIME 609
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 175 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 796
GLPKFormulation: Number of values: 350
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 7420
      * PEG2PEGTIME 7404
      * PBTIME 7214
      * ENGINETIME 175
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
      * Optimization took 310
      * PEG2PEGTIME 301
      * PBTIME 290
      * ENGINETIME 3
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
      * Optimization took 239
      * PEG2PEGTIME 236
      * PBTIME 229
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
      * Optimization took 238
      * PEG2PEGTIME 235
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
      * Optimization took 169
      * PEG2PEGTIME 166
      * PBTIME 160
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
      * Optimization took 228
      * PEG2PEGTIME 226
      * PBTIME 220
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
      * PEG2PEGTIME 262
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
      * Optimization took 245
      * PEG2PEGTIME 242
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
      * Optimization took 233
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
      * Optimization took 234
      * PEG2PEGTIME 230
      * PBTIME 223
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 86 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 542
GLPKFormulation: Number of values: 394
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
      * Engine reached iteration bound of 500 after 272 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
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
      * Engine reached iteration bound of 500 after 159 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 3051
      * PEG2PEGTIME 3038
      * PBTIME 2859
      * ENGINETIME 159
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 153 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 3296
      * PEG2PEGTIME 3288
      * PBTIME 3124
      * ENGINETIME 153
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 101 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 489
GLPKFormulation: Number of values: 276
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 4292
      * PEG2PEGTIME 4267
      * PBTIME 4157
      * ENGINETIME 101
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 116 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 483
GLPKFormulation: Number of values: 273
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 4265
      * PEG2PEGTIME 4247
      * PBTIME 4121
      * ENGINETIME 116
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 219 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1097
GLPKFormulation: Number of values: 178
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 9154
      * PEG2PEGTIME 9137
      * PBTIME 8910
      * ENGINETIME 219
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 217 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1072
GLPKFormulation: Number of values: 178
```

### Optimized
```java
15:09:43.945 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

### Peggy output
```
^[[A^[[A+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
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
      * Optimization took 656
      * PEG2PEGTIME 501
      * PBTIME 414
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 921 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 898
GLPKFormulation: Number of values: 300
^C```

### Optimized
```java
15:12:58.338 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

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
      * Optimization took 536
      * PEG2PEGTIME 383
      * PBTIME 289
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 605 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 898
GLPKFormulation: Number of values: 316
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 8251
      * PEG2PEGTIME 8202
      * PBTIME 7565
      * ENGINETIME 605
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 208 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 793
GLPKFormulation: Number of values: 348
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 7169
      * PEG2PEGTIME 7154
      * PBTIME 6931
      * ENGINETIME 208
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
      * PEG2PEGTIME 293
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
      * Optimization took 241
      * PEG2PEGTIME 239
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
      * Optimization took 255
      * PEG2PEGTIME 251
      * PBTIME 243
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
      * Optimization took 291
      * PEG2PEGTIME 287
      * PBTIME 280
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
      * Optimization took 239
      * PEG2PEGTIME 237
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
      * Optimization took 384
      * PEG2PEGTIME 375
      * PBTIME 363
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
      * Optimization took 404
      * PEG2PEGTIME 398
      * PBTIME 390
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
      * Optimization took 327
      * PEG2PEGTIME 317
      * PBTIME 293
      * ENGINETIME 2
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
      * Optimization took 380
      * PEG2PEGTIME 371
      * PBTIME 360
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 228 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 540
GLPKFormulation: Number of values: 392
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
      * Engine reached iteration bound of 500 after 399 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
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
      * Engine reached iteration bound of 500 after 175 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 3115
      * PEG2PEGTIME 3099
      * PBTIME 2909
      * ENGINETIME 175
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 544 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 3813
      * PEG2PEGTIME 3803
      * PBTIME 3238
      * ENGINETIME 545
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 237 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 489
GLPKFormulation: Number of values: 272
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 4908
      * PEG2PEGTIME 4882
      * PBTIME 4635
      * ENGINETIME 238
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 99 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 489
GLPKFormulation: Number of values: 276
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 4663
      * PEG2PEGTIME 4636
      * PBTIME 4526
      * ENGINETIME 99
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 443 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1303
GLPKFormulation: Number of values: 188
```

### Optimized
```java
15:15:22.542 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

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
      * Optimization took 675
      * PEG2PEGTIME 573
      * PBTIME 500
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 1270 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 862
GLPKFormulation: Number of values: 300
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 8921
      * PEG2PEGTIME 8813
      * PBTIME 7496
      * ENGINETIME 1270
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 194 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 786
GLPKFormulation: Number of values: 343
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 7251
      * PEG2PEGTIME 7233
      * PBTIME 7018
      * ENGINETIME 194
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
      * Optimization took 318
      * PEG2PEGTIME 308
      * PBTIME 285
      * ENGINETIME 8
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
      * Optimization took 239
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
      * Optimization took 253
      * PEG2PEGTIME 249
      * PBTIME 239
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
      * Optimization took 233
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
      * Optimization took 280
      * PEG2PEGTIME 277
      * PBTIME 270
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
      * PEG2PEGTIME 267
      * PBTIME 256
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
      * Optimization took 238
      * PEG2PEGTIME 236
      * PBTIME 229
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
      * Optimization took 229
      * PEG2PEGTIME 226
      * PBTIME 220
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
      * Optimization took 234
      * PEG2PEGTIME 231
      * PBTIME 224
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 74 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 545
GLPKFormulation: Number of values: 395
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
      * Engine reached iteration bound of 500 after 286 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
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
      * Engine reached iteration bound of 500 after 181 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 3075
      * PEG2PEGTIME 3061
      * PBTIME 2868
      * ENGINETIME 181
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 150 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 3426
      * PEG2PEGTIME 3411
      * PBTIME 3240
      * ENGINETIME 150
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 163 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 483
GLPKFormulation: Number of values: 273
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 4654
      * PEG2PEGTIME 4612
      * PBTIME 4424
      * ENGINETIME 163
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 83 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 489
GLPKFormulation: Number of values: 274
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 4514
      * PEG2PEGTIME 4495
      * PBTIME 4401
      * ENGINETIME 83
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 274 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1334
GLPKFormulation: Number of values: 196
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 10864
      * PEG2PEGTIME 10845
      * PBTIME 10560
      * ENGINETIME 275
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 177 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1065
GLPKFormulation: Number of values: 177
```

### Optimized
```java
16:00:48.083 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

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
      * Optimization took 733
      * PEG2PEGTIME 597
      * PBTIME 424
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 1385 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 869
GLPKFormulation: Number of values: 310
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 12776
      * PEG2PEGTIME 12718
      * PBTIME 11296
      * ENGINETIME 1386
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 282 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 806
GLPKFormulation: Number of values: 355
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 10712
      * PEG2PEGTIME 10695
      * PBTIME 10387
      * ENGINETIME 283
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
      * Optimization took 345
      * PEG2PEGTIME 335
      * PBTIME 315
      * ENGINETIME 9
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
      * Optimization took 247
      * PEG2PEGTIME 245
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
      * Optimization took 252
      * PEG2PEGTIME 250
      * PBTIME 241
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
      * PEG2PEGTIME 247
      * PBTIME 238
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
      * Optimization took 242
      * PEG2PEGTIME 239
      * PBTIME 233
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
      * Optimization took 389
      * PEG2PEGTIME 381
      * PBTIME 373
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
      * Optimization took 362
      * PEG2PEGTIME 358
      * PBTIME 349
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
      * Optimization took 288
      * PEG2PEGTIME 282
      * PBTIME 274
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
      * Optimization took 251
      * PEG2PEGTIME 248
      * PBTIME 239
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 123 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 542
GLPKFormulation: Number of values: 394
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
      * Engine reached iteration bound of 500 after 404 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
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
      * Engine reached iteration bound of 500 after 223 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 4559
      * PEG2PEGTIME 4546
      * PBTIME 4310
      * ENGINETIME 223
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 198 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 4959
      * PEG2PEGTIME 4947
      * PBTIME 4735
      * ENGINETIME 198
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 216 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 483
GLPKFormulation: Number of values: 271
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 6385
      * PEG2PEGTIME 6339
      * PBTIME 6100
      * ENGINETIME 217
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 215 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 483
GLPKFormulation: Number of values: 269
```

### Optimized
```java
14:47:11.571 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i;
    for (int j = 0;; j = i)
    {
      i = 1 + j;
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
    for (int j = 0; j < 500; j = 1 + j) {}
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
    int i = 20 * paramInt1;
    if (i < paramInt2) {}
    for (paramInt1 = i + 1;; paramInt1 = i)
    {
      paramInt2 = 0;
      i = 0;
      while (20 > i)
      {
        System.out.println(paramInt2);
        paramInt2 = paramInt1 + paramInt2;
        i += 1;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = 1 + paramInt1;; paramInt2 = paramInt1)
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
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    while (i < paramInt)
    {
      j = 5 + j;
      i += 1;
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
      while (paramInt > j)
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
      j += 1;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int i = 0;
    for (int j = 0; i < 300; j = 5 + j)
    {
      System.out.println(j);
      i = 1 + i;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      if (j == 150) {}
      for (int k = 4 + i;; k = 1 + i)
      {
        j = 1 + j;
        i = k;
        System.out.println(i * 50);
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
      int i = 50 + k;
      if (j == 150) {
        i += 150;
      }
      for (;;)
      {
        k = i;
        j = 1 + j;
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
    int i = 0;
    int j = 0;
    if (paramInt < 0) {
      while (j < paramInt)
      {
        System.out.println(j);
        i = 1 + 2;
        j = 1 + j;
      }
    }
    while (j < paramInt)
    {
      i = 1 + i;
      System.out.println(j);
      j = 1 + j;
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

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
      * Optimization took 693
      * PEG2PEGTIME 559
      * PBTIME 417
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 1133 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 918
GLPKFormulation: Number of values: 310
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 13141
      * PEG2PEGTIME 13089
      * PBTIME 11911
      * ENGINETIME 1134
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 315 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 789
GLPKFormulation: Number of values: 341
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 11137
      * PEG2PEGTIME 11107
      * PBTIME 10766
      * ENGINETIME 315
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
      * Optimization took 470
      * PEG2PEGTIME 460
      * PBTIME 436
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
      * Optimization took 358
      * PEG2PEGTIME 354
      * PBTIME 345
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
      * Optimization took 498
      * PEG2PEGTIME 493
      * PBTIME 477
      * ENGINETIME 5
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
      * Optimization took 377
      * PEG2PEGTIME 372
      * PBTIME 351
      * ENGINETIME 2
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
      * Optimization took 523
      * PEG2PEGTIME 519
      * PBTIME 357
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
      * Optimization took 410
      * PEG2PEGTIME 402
      * PBTIME 387
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
      * Optimization took 372
      * PEG2PEGTIME 367
      * PBTIME 356
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
      * Optimization took 234
      * PEG2PEGTIME 232
      * PBTIME 224
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
      * Optimization took 246
      * PEG2PEGTIME 243
      * PBTIME 234
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 124 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 540
GLPKFormulation: Number of values: 392
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
      * Engine reached iteration bound of 500 after 404 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
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
      * Engine reached iteration bound of 500 after 216 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 4585
      * PEG2PEGTIME 4573
      * PBTIME 4341
      * ENGINETIME 217
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 231 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 4902
      * PEG2PEGTIME 4889
      * PBTIME 4642
      * ENGINETIME 231
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 122 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 510
GLPKFormulation: Number of values: 281
         @ Running solver
```

### Optimized
```java
15:25:58.307 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
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
    int i = 20 * paramInt1;
    if (i < paramInt2) {}
    for (paramInt1 = 1 + i;; paramInt1 = i)
    {
      i = 0;
      for (paramInt2 = 0; i < 20; paramInt2 = paramInt1 + paramInt2)
      {
        i = 1 + i;
        System.out.println(paramInt2);
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      paramInt1 = 0;
      for (int i = 0; i < 20; i = 1 + i)
      {
        System.out.println(paramInt1);
        paramInt1 += paramInt2;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int j = 0;
    for (int i = 0; i < paramInt; i = 1 + i) {
      j = 5 + j;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (int i = 0;; i = i)
    {
      return i;
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
    for (int j = 0; 300 > j; j = 1 + j)
    {
      i = 5 + i;
      System.out.println(i);
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
    int j = 0;
    int i = 0;
    if (300 > j)
    {
      if (j == 150) {}
      for (int k = 4 + i;; k = i + 1)
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
    int i = 0;
    if (j < 300)
    {
      int k = i + 50;
      if (j == 150) {}
      for (k = 150 + k;; k = k)
      {
        j = 1 + j;
        i = k;
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
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (i < paramInt)
      {
        System.out.println(i);
        j = 2 + 1;
        i += 1;
      }
    }
    while (i < paramInt)
    {
      System.out.println(i);
      j += 1;
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

### Peggy output
```
Cannot connect to the Docker daemon at unix:///Users/kirsten/.docker/run/docker.sock. Is the docker daemon running?
```

### Optimized
```java
09:36:03.294 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    for (int i = 0;; i = i)
    {
      i = 1 + i;
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
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (paramInt1 = 1 + paramInt1;; paramInt1 = paramInt1)
    {
      paramInt2 = 0;
      for (int i = 0; i < 20; i = 1 + i)
      {
        System.out.println(paramInt2);
        paramInt2 = paramInt1 + paramInt2;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    int i = 20 * paramInt1;
    if (i < paramInt2) {
      i += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      paramInt1 = 0;
      while (20 > paramInt1)
      {
        System.out.println(paramInt2);
        paramInt2 += i;
        paramInt1 += 1;
      }
      return;
      i = i;
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
      for (j = 5; i < paramInt; j = 5 + j) {
        i = 1 + i;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    for (int j = 0; 300 > j; j = 1 + j)
    {
      int i = j + j;
      System.out.println(j + i + i);
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
    if (300 > i)
    {
      int k = 1 + j;
      if (i == 150) {
        k += 3;
      }
      for (;;)
      {
        j = k;
        System.out.println(j * 50);
        i += 1;
        break;
        k = k;
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
        j += 1;
        System.out.println(i);
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

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
      * Optimization took 650
      * PEG2PEGTIME 525
      * PBTIME 402
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 948 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 918
GLPKFormulation: Number of values: 310
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 12560
      * PEG2PEGTIME 12509
      * PBTIME 11523
      * ENGINETIME 949
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 272 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 789
GLPKFormulation: Number of values: 341
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 10986
      * PEG2PEGTIME 10967
      * PBTIME 10674
      * ENGINETIME 272
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
      * Optimization took 359
      * PEG2PEGTIME 349
      * PBTIME 324
      * ENGINETIME 12
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
      * Optimization took 248
      * PEG2PEGTIME 246
      * PBTIME 237
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
      * Optimization took 255
      * PEG2PEGTIME 251
      * PBTIME 238
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
      * Optimization took 241
      * PEG2PEGTIME 239
      * PBTIME 229
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
      * Optimization took 285
      * PEG2PEGTIME 282
      * PBTIME 229
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
      * Optimization took 286
      * PEG2PEGTIME 280
      * PBTIME 271
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
      * Optimization took 255
      * PEG2PEGTIME 252
      * PBTIME 243
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
      * PBTIME 227
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
      * Optimization took 237
      * PEG2PEGTIME 235
      * PBTIME 228
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 113 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 545
GLPKFormulation: Number of values: 395
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
      * Engine reached iteration bound of 500 after 378 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
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
      * Engine reached iteration bound of 500 after 267 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 4610
      * PEG2PEGTIME 4604
      * PBTIME 4326
      * ENGINETIME 268
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 202 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 4932
      * PEG2PEGTIME 4919
      * PBTIME 4705
      * ENGINETIME 202
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 137 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 489
GLPKFormulation: Number of values: 277
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 6595
      * PEG2PEGTIME 6569
      * PBTIME 6414
      * ENGINETIME 138
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 120 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 510
GLPKFormulation: Number of values: 281
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 6867
      * PEG2PEGTIME 6851
      * PBTIME 6721
      * ENGINETIME 120
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 251 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1094
GLPKFormulation: Number of values: 177
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 13220
      * PEG2PEGTIME 13213
      * PBTIME 12954
      * ENGINETIME 251
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 286 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1062
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 12575
      * PEG2PEGTIME 12560
      * PBTIME 12257
      * ENGINETIME 286
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 206 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1285
GLPKFormulation: Number of values: 571
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 19389
      * PEG2PEGTIME 19376
      * PBTIME 19159
      * ENGINETIME 206
      * Optimization ratio 31410/31490 = 0.997459510955859
      * PEG-based Optimization ratio 31410/31490 = 0.997459510955859
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 140 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 742
GLPKFormulation: Number of values: 360
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 9901
      * PEG2PEGTIME 9890
      * PBTIME 9739
      * ENGINETIME 140
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 150 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 626
GLPKFormulation: Number of values: 398
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 8411
      * PEG2PEGTIME 8385
      * PBTIME 8226
      * ENGINETIME 150
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 456 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 618
GLPKFormulation: Number of values: 410
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedExpected()> SUCCESSFUL
      * Optimization took 8890
      * PEG2PEGTIME 8862
      * PBTIME 8393
      * ENGINETIME 458
      * Optimization ratio 31520/31520 = 1.0
      * PEG-based Optimization ratio 31520/31520 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
   - Processing method <Benchmark: int loopUnrollOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 192 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollOriginal(int)> SUCCESSFUL
      * Optimization took 4688
      * PEG2PEGTIME 4680
      * PBTIME 4478
      * ENGINETIME 193
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
      * PEG2PEGTIME 237
      * PBTIME 229
      * ENGINETIME 0
   - Done processing method <Benchmark: int loopUnrollExpected(int)>
   - Processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 110 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 466
GLPKFormulation: Number of values: 308
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int simpleLoopUnswitchOriginal(int)> SUCCESSFUL
      * Optimization took 6454
      * PEG2PEGTIME 6409
      * PBTIME 6291
      * ENGINETIME 110
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
+ Total optimization time = 147142 milliseconds
```

### Optimized
```java
09:38:53.103 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    for (int i = 0;; i = i)
    {
      i = 1 + i;
      if (500 <= i) {
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
    while (500 > i) {
      i += 1;
    }
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
    int i = paramInt1 * 20;
    if (paramInt2 > i) {}
    for (paramInt1 = 1 + i;; paramInt1 = i)
    {
      i = 0;
      paramInt2 = 0;
      while (20 > paramInt2)
      {
        i = paramInt1 + i;
        System.out.println(i);
        paramInt2 += 1;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    int i = 20 * paramInt1;
    if (i < paramInt2) {}
    for (i = 1 + i;; i = i)
    {
      paramInt2 = 0;
      for (paramInt1 = 0; 20 > paramInt2; paramInt1 = i + paramInt1)
      {
        System.out.println(paramInt1);
        paramInt2 += 1;
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
      j = 5;
      for (int i = 1; i < paramInt; i = 1 + i) {
        j += 5;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (i < 300)
    {
      int j = i;
      i = 1 + i;
      int tmp21_20 = (j + j);
      System.out.println(tmp21_20 + (tmp21_20 + j));
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    int i = 0;
    while (j < 300)
    {
      j += 1;
      i = 5 + i;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int k = 0;
    int i = 0;
    if (300 > k)
    {
      if (k == 150) {}
      for (int j = i + 4;; j = 1 + i)
      {
        System.out.println(i * 50);
        k += 1;
        i = j;
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
      int k = 50 + i;
      if (j == 150) {
        k += 150;
      }
      for (;;)
      {
        System.out.println(i);
        j = 1 + j;
        i = k;
        break;
        k = k;
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
      while (paramInt > j)
      {
        System.out.println(j);
        j += 1;
        i = 1 + 2;
      }
    }
    while (paramInt > j)
    {
      System.out.println(j);
      j += 1;
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

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
      * Optimization took 554
      * PEG2PEGTIME 430
      * PBTIME 306
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 915 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 918
GLPKFormulation: Number of values: 310
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 12125
      * PEG2PEGTIME 12069
      * PBTIME 11120
      * ENGINETIME 916
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 284 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 798
GLPKFormulation: Number of values: 349
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 11122
      * PEG2PEGTIME 11102
      * PBTIME 10788
      * ENGINETIME 284
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
      * Optimization took 329
      * PEG2PEGTIME 317
      * PBTIME 298
      * ENGINETIME 8
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
      * Optimization took 256
      * PEG2PEGTIME 253
      * PBTIME 244
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
      * Optimization took 249
      * PEG2PEGTIME 246
      * PBTIME 237
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
      * Optimization took 281
      * PEG2PEGTIME 278
      * PBTIME 270
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
      * PBTIME 231
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
      * Optimization took 412
      * PEG2PEGTIME 404
      * PBTIME 394
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
      * Optimization took 258
      * PEG2PEGTIME 256
      * PBTIME 248
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
      * Optimization took 241
      * PEG2PEGTIME 238
      * PBTIME 231
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
      * Optimization took 241
      * PEG2PEGTIME 237
      * PBTIME 230
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 109 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 539
GLPKFormulation: Number of values: 391
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
      * Engine reached iteration bound of 500 after 449 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
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
      * Engine reached iteration bound of 500 after 178 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 4157
      * PEG2PEGTIME 4151
      * PBTIME 3962
      * ENGINETIME 179
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 250 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 4865
      * PEG2PEGTIME 4853
      * PBTIME 4592
      * ENGINETIME 250
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 108 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 483
GLPKFormulation: Number of values: 273
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 6320
      * PEG2PEGTIME 6296
      * PBTIME 6177
      * ENGINETIME 108
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 116 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 512
GLPKFormulation: Number of values: 283
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 6510
      * PEG2PEGTIME 6494
      * PBTIME 6370
      * ENGINETIME 116
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 330 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1353
GLPKFormulation: Number of values: 221
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 16256
      * PEG2PEGTIME 16241
      * PBTIME 15903
      * ENGINETIME 330
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 301 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1070
GLPKFormulation: Number of values: 177
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 15009
      * PEG2PEGTIME 14988
      * PBTIME 14661
      * ENGINETIME 301
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 220 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1288
GLPKFormulation: Number of values: 572
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 17002
      * PEG2PEGTIME 16988
      * PBTIME 16752
      * ENGINETIME 221
      * Optimization ratio 31410/31490 = 0.997459510955859
      * PEG-based Optimization ratio 31410/31490 = 0.997459510955859
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 115 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 741
GLPKFormulation: Number of values: 359
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 10162
      * PEG2PEGTIME 10152
      * PBTIME 10026
      * ENGINETIME 115
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 153 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 587
GLPKFormulation: Number of values: 375
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 8021
      * PEG2PEGTIME 7996
      * PBTIME 7835
      * ENGINETIME 153
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 313 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 595
GLPKFormulation: Number of values: 389
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedExpected()> SUCCESSFUL
      * Optimization took 8062
      * PEG2PEGTIME 8033
      * PBTIME 7711
      * ENGINETIME 313
      * Optimization ratio 31520/31520 = 1.0
      * PEG-based Optimization ratio 31520/31520 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
   - Processing method <Benchmark: int loopUnrollOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 183 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollOriginal(int)> SUCCESSFUL
      * Optimization took 4715
      * PEG2PEGTIME 4706
      * PBTIME 4506
      * ENGINETIME 183
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
      * PEG2PEGTIME 239
      * PBTIME 232
      * ENGINETIME 1
   - Done processing method <Benchmark: int loopUnrollExpected(int)>
   - Processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 95 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 453
GLPKFormulation: Number of values: 310
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int simpleLoopUnswitchOriginal(int)> SUCCESSFUL
      * Optimization took 6551
      * PEG2PEGTIME 6477
      * PBTIME 6375
      * ENGINETIME 95
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
+ Total optimization time = 148078 milliseconds
```

### Optimized
```java
09:56:12.982 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    for (int j = 0;; j = j)
    {
      j = 1 + j;
      if (500 <= j) {
        break;
      }
    }
    int i = j + j;
    if (paramInt == 0) {}
    for (i = i;; i = j + i) {
      return i;
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
    for (int i = 0; 3 > i; i = 1 + i) {}
    return i * 50;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; i < 150; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    int i = 20 * paramInt1;
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
    int i = 20 * paramInt1;
    if (i < paramInt2) {}
    for (paramInt1 = i + 1;; paramInt1 = i)
    {
      i = 0;
      paramInt2 = 0;
      while (paramInt2 < 20)
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
    for (int j = 0; paramInt > i; j = 5 + j) {
      i += 1;
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
      for (j = 1; paramInt > j; j = 1 + j) {
        i = 5 + i;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (300 > i)
    {
      int j = i;
      i = 1 + i;
      int tmp21_20 = (j + j);
      System.out.println(tmp21_20 + tmp21_20 + j);
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    int i = 0;
    while (300 > j)
    {
      j = 1 + j;
      i += 5;
      System.out.println(i);
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
        System.out.println(50 * i);
        j = 1 + j;
        break;
        k = k;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int i = 0;
    int k = 0;
    if (300 > k)
    {
      if (k == 150) {}
      for (int j = i + 200;; j = i + 50)
      {
        i = j;
        System.out.println(i);
        k += 1;
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
        System.out.println(i);
        j = 1 + 2;
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

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

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
      * Optimization took 543
      * PEG2PEGTIME 422
      * PBTIME 305
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 941 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 918
GLPKFormulation: Number of values: 310
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 12246
      * PEG2PEGTIME 12196
      * PBTIME 11216
      * ENGINETIME 942
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 254 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 798
GLPKFormulation: Number of values: 349
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 11871
      * PEG2PEGTIME 11851
      * PBTIME 11571
      * ENGINETIME 254
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
      * Optimization took 466
      * PEG2PEGTIME 453
      * PBTIME 423
      * ENGINETIME 15
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
      * Optimization took 254
      * PEG2PEGTIME 251
      * PBTIME 241
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
      * Optimization took 255
      * PEG2PEGTIME 250
      * PBTIME 239
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
      * Optimization took 312
      * PEG2PEGTIME 307
      * PBTIME 299
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
      * Optimization took 244
      * PEG2PEGTIME 241
      * PBTIME 231
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
      * Optimization took 400
      * PEG2PEGTIME 392
      * PBTIME 374
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
      * Optimization took 384
      * PEG2PEGTIME 380
      * PBTIME 358
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
      * PEG2PEGTIME 240
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
      * Optimization took 244
      * PEG2PEGTIME 241
      * PBTIME 233
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 113 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 545
GLPKFormulation: Number of values: 395
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
      * Engine reached iteration bound of 500 after 623 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
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
      * Engine reached iteration bound of 500 after 216 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 4776
      * PEG2PEGTIME 4736
      * PBTIME 4493
      * ENGINETIME 216
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 255 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 5144
      * PEG2PEGTIME 5130
      * PBTIME 4856
      * ENGINETIME 255
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 180 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 488
GLPKFormulation: Number of values: 276
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 6864
      * PEG2PEGTIME 6832
      * PBTIME 6636
      * ENGINETIME 180
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 128 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 483
GLPKFormulation: Number of values: 273
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 6469
      * PEG2PEGTIME 6436
      * PBTIME 6284
      * ENGINETIME 128
      * Optimization ratio 31459/31519 = 0.9980963863066722
      * PEG-based Optimization ratio 31459/31519 = 0.9980963863066722
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 344 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1314
GLPKFormulation: Number of values: 189
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 16294
      * PEG2PEGTIME 16279
      * PBTIME 15918
      * ENGINETIME 344
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 292 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1043
GLPKFormulation: Number of values: 216
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 12739
      * PEG2PEGTIME 12723
      * PBTIME 12404
      * ENGINETIME 292
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 203 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1279
GLPKFormulation: Number of values: 570
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 16717
      * PEG2PEGTIME 16705
      * PBTIME 16489
      * ENGINETIME 203
      * Optimization ratio 31410/31490 = 0.997459510955859
      * PEG-based Optimization ratio 31410/31490 = 0.997459510955859
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 135 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 737
GLPKFormulation: Number of values: 352
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 10032
      * PEG2PEGTIME 10008
      * PBTIME 9855
      * ENGINETIME 135
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 165 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 607
GLPKFormulation: Number of values: 352
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 7958
      * PEG2PEGTIME 7928
      * PBTIME 7743
      * ENGINETIME 165
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 132 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 612
GLPKFormulation: Number of values: 389
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedExpected()> SUCCESSFUL
      * Optimization took 8042
      * PEG2PEGTIME 8016
      * PBTIME 7873
      * ENGINETIME 132
      * Optimization ratio 31520/31520 = 1.0
      * PEG-based Optimization ratio 31520/31520 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
   - Processing method <Benchmark: int loopUnrollOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 807 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollOriginal(int)> SUCCESSFUL
      * Optimization took 5481
      * PEG2PEGTIME 5464
      * PBTIME 4648
      * ENGINETIME 809
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
      * Optimization took 246
      * PEG2PEGTIME 242
      * PBTIME 226
      * ENGINETIME 1
   - Done processing method <Benchmark: int loopUnrollExpected(int)>
   - Processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 112 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 466
GLPKFormulation: Number of values: 310
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int simpleLoopUnswitchOriginal(int)> SUCCESSFUL
      * Optimization took 7091
      * PEG2PEGTIME 7025
      * PBTIME 6904
      * ENGINETIME 112
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
+ Total optimization time = 149622 milliseconds
```

### Optimized
```java
10:01:34.085 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    for (int j = 0;; j = j)
    {
      j = 1 + j;
      if (500 <= j) {
        break;
      }
    }
    int i = j + j;
    if (paramInt == 0) {}
    for (i = i;; i = j + i) {
      return i;
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
    while (i < 3) {
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
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {}
    for (paramInt2 = 1 + paramInt1;; paramInt2 = paramInt1)
    {
      int i = 0;
      paramInt1 = 0;
      while (20 > i)
      {
        i = 1 + i;
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
      int i = 0;
      paramInt1 = 0;
      while (20 > paramInt1)
      {
        i = paramInt2 + i;
        System.out.println(i);
        paramInt1 += 1;
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
    int i;
    for (paramInt = 0;; paramInt = i)
    {
      return paramInt;
      i = 5;
      for (int j = 1; paramInt > j; j = 1 + j) {
        i = 5 + i;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (300 > i)
    {
      int j = i;
      i += 1;
      int tmp21_20 = (j + j);
      System.out.println(tmp21_20 + tmp21_20 + j);
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
    int i = 0;
    int k = 0;
    if (k < 300)
    {
      if (k == 150) {}
      for (int j = 200 + i;; j = i + 50)
      {
        i = j;
        k = 1 + k;
        System.out.println(i);
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
    int k = 0;
    if (paramInt < 0)
    {
      while (k < paramInt)
      {
        int i = k;
        j = 1 + 2;
        k += 1;
        System.out.println(i);
      }
      return j;
    }
    for (;;)
    {
      if (k < paramInt)
      {
        k += 1;
        System.out.println(k);
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
