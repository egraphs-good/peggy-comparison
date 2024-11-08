public class Raytracer {

    public static void main(double xpos, double ypos, double zpos, double width, double height) {
        int numTriangles = 100;
        double[] firstPoint = new double[] { 0.0, 0.0, 0.0 };
        double[][] firstTriangle = new double[][] { firstPoint, firstPoint, firstPoint };
        double[][][] triangles = new double[numTriangles][3][3];

        // Allocate triangles
        for (int i = 0; i < numTriangles; i++) {
            double[] point1 = new double[] { 0.0, 0.0, 0.0 };
            double[] point2 = new double[] { 0.0, 0.0, 0.0 };
            double[] point3 = new double[] { 0.0, 0.0, 0.0 };
            double[][] triangle = new double[][] { point1, point2, point3 };
            triangles[i] = triangle;
        }

        // Drop the first triangle
        firstPoint = null;
        firstTriangle = null;

        buildCube(xpos, ypos, zpos, width, height, triangles);

        // For each BVH, store the indices of the two children
        // Use -1 for no child
        long[] firstChild = new long[] { -1, -1 };
        long[][] bvhChildren = new long[100][2];
        double[] dummyPoint = new double[] { 0.0, 0.0, 0.0 };
        double[][] dummyBbox = new double[][] { dummyPoint, dummyPoint };
        // For each BVH, store the start position
        double[][][] bvhBbox = new double[100][2][3];
        // For each BVH, store the start index into triangles
        long[] bvhStart = new long[100];
        // For each BVH, store the size of the interval into triangles
        long[] bvhSize = new long[100];

        // Initialize bvhChildren
        for (int i = 1; i < 100; i++) {
            bvhChildren[i] = new long[] { -1, -1 };
        }

        // Initialize bvhBbox
        for (int i = 0; i < 100; i++) {
            bvhBbox[i] = new double[][] { new double[] { 0.0, 0.0, 0.0 }, new double[] { 0.0, 0.0, 0.0 } };
        }

        // Build the BVH
        buildBVH(triangles, bvhChildren, bvhBbox, bvhStart, bvhSize);

        // Define the output screen
        double[] dummyRow = new double[100];
        double[][] output = new double[100][100];
        for (int i = 0; i < 100; i++) {
            output[i] = dummyRow.clone();
        }

        double[] lightPos = new double[] { 0.0, 0.5, 2.0 };

        // Trace one ray per screen pixel
        for (int row = 0; row < 100; row++) {
            for (int col = 0; col < 100; col++) {
                double[][] ray = sampleRay(col * 0.01, row * 0.01);
                double[][] resRay = new double[2][3];
                boolean didIntersect = trace(triangles, ray, resRay, bvhChildren, bvhBbox, bvhStart, bvhSize);
                if (didIntersect) {
                    double[] pointToLight = vecSubtract(lightPos, resRay[0]);
                    double[] pointToLightNorm = vecNormalize(pointToLight);
                    double lightDist = vecLength(pointToLight);

                    double brightness = 1.0 / (lightDist * lightDist);
                    double lightDot = dot(pointToLightNorm, resRay[1]);
                    double absLightDot = abs(lightDot);

                    output[row][col] = brightness * absLightDot;
                }

                ray = null;
                resRay = null;
            }
        }

        // Print out the screen
        for (int row = 0; row < 100; row++) {
            for (int col = 0; col < 100; col++) {
                System.out.println(output[row][col]);
            }
        }

        double res = triangles[10][0][0];
        System.out.println(res);
    }

    // sample a ray from the camera pixel at (px, pz)
    // px and py are in the range [0, 1]
    private static double[][] sampleRay(double px, double pz) {
        double[] cameraPos = new double[] { 0.08, 1.7, 0.8 };
        double[] cameraForward = new double[] { 0.0, 1.0, -1.5 };
        double[] cameraForwardNorm = vecNormalize(cameraForward);

        double[] cameraUp = new double[] {
                cameraForwardNorm[0],
                -cameraForwardNorm[2],
                cameraForwardNorm[1]
        };

        double[] cameraRight = vecCross(cameraUp, cameraForwardNorm);

        double[] cameraForwardScaled = vecScale(cameraForwardNorm, 0.2);
        double[] cameraCenterPos = vecAdd(cameraPos, cameraForwardScaled);
        double[] cameraUpScaled = vecScale(cameraUp, 0.1);
        double[] cameraTopMiddlePos = vecAdd(cameraCenterPos, cameraUpScaled);
        double[] cameraRightScaled = vecScale(cameraRight, 0.1);
        double[] cameraTopLeftPos = vecSubtract(cameraTopMiddlePos, cameraRightScaled);
        double[] cameraTopRightPos = vecAdd(cameraTopMiddlePos, cameraRightScaled);
        double[] cameraBottomMiddlePos = vecSubtract(cameraCenterPos, cameraUpScaled);
        double[] cameraBottomLeftPos = vecSubtract(cameraBottomMiddlePos, cameraRightScaled);

        double[] cameraScreenAcross = vecSubtract(cameraTopRightPos, cameraTopLeftPos);
        double[] cameraScreenDown = vecSubtract(cameraBottomLeftPos, cameraTopLeftPos);

        double[] acrossScaled = vecScale(cameraScreenAcross, px);
        double[] rayEnd = vecAdd(cameraTopLeftPos, acrossScaled);
        double[] downScaled = vecScale(cameraScreenDown, pz);
        rayEnd = vecAdd(rayEnd, downScaled);

        double[] diff = vecSubtract(rayEnd, cameraPos);
        double[] rayDir = vecNormalize(diff);
        double[][] ray = new double[][] { cameraPos, rayDir };

        return ray;
    }

    private static boolean bboxRayIntersect(double[][] bbox, double[][] ray) {
        final double epsilon = 0.0000001;

        // Calculate intersection with x planes
        double t1 = (bbox[0][0] - ray[0][0]) / ray[1][0];
        double t2 = (bbox[1][0] - ray[0][0]) / ray[1][0];
        double tmin = Math.min(t1, t2);
        double tmax = Math.max(t1, t2);

        // Calculate intersection with y planes
        double t3 = (bbox[0][1] - ray[0][1]) / ray[1][1];
        double t4 = (bbox[1][1] - ray[0][1]) / ray[1][1];
        double tmin2 = Math.min(t3, t4);
        double tmax2 = Math.max(t3, t4);

        // Calculate intersection with z planes
        double t5 = (bbox[0][2] - ray[0][2]) / ray[1][2];
        double t6 = (bbox[1][2] - ray[0][2]) / ray[1][2];
        double tmin3 = Math.min(t5, t6);
        double tmax3 = Math.max(t5, t6);

        // Find final intersection intervals
        double tminFinal = Math.max(tmin, Math.max(tmin2, tmin3));
        double tmaxFinal = Math.min(tmax, Math.min(tmax2, tmax3));

        // Return true if there is a valid intersection
        return tmaxFinal >= tminFinal && tmaxFinal > epsilon;
    }

    /**
     * Subtracts two 3D vecs
     */
    public static double[] vecSubtract(double[] a, double[] b) {
        return new double[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
    }

    /**
     * Adds two 3D vecs
     */
    public static double[] vecAdd(double[] a, double[] b) {
        return new double[] { a[0] + b[0], a[1] + b[1], a[2] + b[2] };
    }

    /**
     * Scales a 3D vec by a scalar value
     */
    public static double[] vecScale(double[] a, double s) {
        return new double[] { a[0] * s, a[1] * s, a[2] * s };
    }

    public static double[] vecCross(double[] a, double[] b) {
        return new double[] {
                a[1] * b[2] - a[2] * b[1],
                a[2] * b[0] - a[0] * b[2],
                a[0] * b[1] - a[1] * b[0],
        };
    }

    /**
     * Returns absolute value of a number
     * Note: In practice, you should use Math.abs() instead
     */
    public static double abs(double x) {
        return x < 0.0 ? -x : x;
    }

    /**
     * Calculates square root using Newton's method
     * Note: In practice, you should use Math.sqrt() instead
     */
    public static double sqrt(double x) {
        double guess = x;
        final double tolerance = 1e-2; // Set precision level
        int numIters = 100;

        while (numIters > 0 && abs(guess * guess - x) > tolerance) {
            guess = (guess + x / guess) / 2.0;
            numIters--;
        }

        return guess;
    }

    /**
     * Calculates the length of a 3D vec
     */
    public static double vecLength(double[] a) {
        return sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
    }

    /**
     * Normalizes a 3D vec (makes it unit length)
     */
    public static double[] vecNormalize(double[] a) {
        double len = vecLength(a);
        return new double[] { a[0] / len, a[1] / len, a[2] / len };
    }

    /**
     * Calculates dot product of two 3D vecs
     */
    public static double dot(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    /**
     * Constructs BVH tree, re-arranging the order of triangles
     * so that the BVH can refer to intervals in the triangles array.
     */
    public static void buildBVH(
            double[][][] triangles,
            long[][] bvhChildren,
            double[][][] bvhBbox,
            long[] bvhStart,
            long[] bvhSize) {

        makeLeafNode(0, 100, triangles, bvhChildren, bvhBbox, bvhStart, bvhSize, 0);
        long bvhNextFree = 1;
        long[] nodeStack = new long[100];
        int stackSize = 1;

        // Define coordinate axis directions
        double[][] directions = new double[][] {
                { 1.0, 0.0, 0.0 }, // X axis
                { 0.0, 1.0, 0.0 }, // Y axis
                { 0.0, 0.0, 1.0 } // Z axis
        };

        final int numPartitionsTry = 32;
        final double largeNum = 9999999999999999.0;

        while (stackSize > 0) {
            long node = nodeStack[stackSize - 1];
            stackSize--;

            double[] bestPartition = new double[3];
            double bestCost = largeNum;

            // Try partitioning along each axis
            for (int directionIndex = 0; directionIndex < 3; directionIndex++) {
                for (int partitionI = 0; partitionI < numPartitionsTry; partitionI++) {
                    double partitionIFloat = partitionI;

                    // Calculate partition position
                    double[] scaledMax = vecScale(bvhBbox[(int) node][1], 1.0 / 32.0);
                    double[] scaledMin = vecScale(bvhBbox[(int) node][0], 1.0 / 32.0);
                    double[] subtracted = vecSubtract(scaledMax, scaledMin);
                    double dist = dot(subtracted, directions[directionIndex]) * partitionIFloat;

                    double[] partition = vecScale(directions[directionIndex], dist);
                    double cost = partitionCost(
                            node,
                            triangles,
                            bvhChildren,
                            bvhBbox,
                            bvhStart,
                            bvhSize,
                            partition);

                    if (cost < bestCost) {
                        bestCost = cost;
                        bestPartition = partition;
                    }
                }
            }

            // Partition the node using the best partition found
            long middle = partition(
                    node,
                    triangles,
                    bvhChildren,
                    bvhBbox,
                    bvhStart,
                    bvhSize,
                    bestPartition);

            // Create child nodes
            long leftChild = bvhNextFree;
            makeLeafNode(
                    bvhStart[(int) node],
                    middle,
                    triangles,
                    bvhChildren,
                    bvhBbox,
                    bvhStart,
                    bvhSize,
                    bvhNextFree);
            bvhNextFree++;

            long rightChild = bvhNextFree;
            makeLeafNode(
                    middle,
                    bvhStart[(int) node] + bvhSize[(int) node],
                    triangles,
                    bvhChildren,
                    bvhBbox,
                    bvhStart,
                    bvhSize,
                    bvhNextFree);
            bvhNextFree++;

            // Link children to parent
            bvhChildren[(int) node][0] = leftChild;
            bvhChildren[(int) node][1] = rightChild;

            // Add children to stack if they're large enough to split further
            if (bvhSize[(int) leftChild] > 4) {
                nodeStack[stackSize++] = leftChild;
            }

            if (bvhSize[(int) rightChild] > 4) {
                nodeStack[stackSize++] = rightChild;
            }
        }
    }

    // Note: These helper methods need to be implemented:
    private static void makeLeafNode(
            long start,
            long end,
            double[][][] triangles,
            long[][] bvhChildren,
            double[][][] bvhBbox,
            long[] bvhStart,
            long[] bvhSize,
            long nodeIndex) {
        // Implementation needed
    }

    // builds a cube with a bottom, front, left corner at (xpos, ypos, zpos)
    public static void buildCube(double xpos, double ypos, double zpos, double width, double height,
            double[][][] triangles) {
        int numRectangles = 25;
        double rectWidth = width / 25.0;

        int i = 0;
        int ti = 0;
        double cx = xpos;

        while (i < numRectangles) {
            // top left corner
            triangles[ti][0][0] = cx;
            triangles[ti][0][1] = ypos;
            triangles[ti][0][2] = zpos;

            // bottom left corner
            triangles[ti][1][0] = cx;
            triangles[ti][1][1] = ypos;
            triangles[ti][1][2] = zpos + height;

            // top right corner
            triangles[ti][2][0] = cx + rectWidth;
            triangles[ti][2][1] = ypos;
            triangles[ti][2][2] = zpos;

            ti++;

            // top right corner
            triangles[ti][0][0] = cx + rectWidth;
            triangles[ti][0][1] = ypos;
            triangles[ti][0][2] = zpos;

            // bottom left corner
            triangles[ti][1][0] = cx;
            triangles[ti][1][1] = ypos;
            triangles[ti][1][2] = zpos + height;

            // bottom right corner
            triangles[ti][2][0] = cx + rectWidth;
            triangles[ti][2][1] = ypos;
            triangles[ti][2][2] = zpos + height;

            ti++;
            cx += rectWidth;
            i++;
        }

        i = 0;
        cx = xpos;

        while (i < numRectangles) {
            // front left corner
            triangles[ti][0][0] = cx;
            triangles[ti][0][1] = ypos;
            triangles[ti][0][2] = zpos + height;

            // back right corner
            triangles[ti][1][0] = cx + rectWidth;
            triangles[ti][1][1] = ypos + height;
            triangles[ti][1][2] = zpos + height;

            // back left corner
            triangles[ti][2][0] = cx;
            triangles[ti][2][1] = ypos + height;
            triangles[ti][2][2] = zpos + height;

            ti++;

            // front left corner
            triangles[ti][0][0] = cx;
            triangles[ti][0][1] = ypos;
            triangles[ti][0][2] = zpos + height;

            // front right corner
            triangles[ti][1][0] = cx + rectWidth;
            triangles[ti][1][1] = ypos;
            triangles[ti][1][2] = zpos + height;

            // back right corner
            triangles[ti][2][0] = cx + rectWidth;
            triangles[ti][2][1] = ypos + height;
            triangles[ti][2][2] = zpos + height;

            ti++;
            cx += rectWidth;
            i++;
        }
    }

    private static double partitionCost(
            long node,
            double[][][] triangles,
            long[][] bvhChildren,
            double[][][] bvhBbox,
            long[] bvhStart,
            long[] bvhSize,
            double[] partition) {
        // Implementation needed
        return 0.0;
    }

    private static long partition(
            long node,
            double[][][] triangles,
            long[][] bvhChildren,
            double[][][] bvhBbox,
            long[] bvhStart,
            long[] bvhSize,
            double[] partition) {
        // Implementation needed
        return 0;
    }

    private static double bboxSize(double[][] bbox) {
        double[] diff = vecSubtract(bbox[1], bbox[0]);
        double size = vecLength(diff);
        return size;
    }

    // intersect a ray with a triangle
    // ray is a source point and normalized direction
    // triangle is 3 points, cc-wise
    // store the output intersection point in output
    // returns a bool indicating if the intersection is valid
    private static boolean intersect(double[][] tri, double[][] ray, double[] output) {
        final double epsilon = 0.0000001;
        double[] edge1 = vecSubtract(tri[1], tri[0]);
        double[] edge2 = vecSubtract(tri[2], tri[0]);
        double[] h = vecCross(ray[1], edge2);
        double a = dot(edge1, h);
        if (a > -epsilon && a < epsilon) {
            return false;
        }
        double f = 1.0 / a;
        double[] s = vecSubtract(ray[0], tri[0]);
        double u = f * dot(s, h);
        if (u < 0.0 || u > 1.0) {
            return false;
        }
        double[] q = vecCross(s, edge1);
        double v = f * dot(ray[1], q);
        if (v < 0.0 || u + v > 1.0) {
            return false;
        }
        double t = f * dot(edge2, q);
        if (t > epsilon) {
            output[0] = ray[0][0] + t * ray[1][0];
            output[1] = ray[0][1] + t * ray[1][1];
            output[2] = ray[0][2] + t * ray[1][2];
            return true;
        }
        return false;
    }

    // Trace a ray and return the new ray in the output
    // Return if the ray intersected with any triangle
    // a ray is a source point and normalized direction
    // Uses the bvh tree to speed up intersection
    private static boolean trace(
            double[][][] triangles,
            double[][] ray,
            double[][] resRay,
            long[][] bvhChildren,
            double[][][] bvhBbox,
            long[] bvhStart,
            long[] bvhSize) {
        double minDist = 9999999999999999.0;
        long[] bvhStack = new long[100];
        long stackSize = 1;

        boolean didIntersect = false;

        while (stackSize > 0) {
            long nodeIndex = bvhStack[(int) (stackSize - 1)];
            double[][] bbox = bvhBbox[(int) nodeIndex];
            stackSize--;
            if (bboxRayIntersect(bbox, ray)) {
                long child1 = bvhChildren[(int) nodeIndex][0];
                long child2 = bvhChildren[(int) nodeIndex][1];
                if (child1 != nodeIndex) {
                    bvhStack[(int) stackSize] = child1;
                    stackSize++;
                    bvhStack[(int) stackSize] = child2;
                    stackSize++;
                } else {
                    long i = bvhStart[(int) nodeIndex];
                    while (i < bvhStart[(int) nodeIndex] + bvhSize[(int) nodeIndex]) {
                        double[][] tri = triangles[(int) i];
                        double[] point = new double[3];
                        boolean res = intersect(tri, ray, point);
                        if (res) {
                            double[] subtracted = vecSubtract(point, ray[0]);
                            double dist = vecLength(subtracted);
                            if (dist < minDist) {
                                minDist = dist;
                                resRay[0][0] = point[0];
                                resRay[0][1] = point[1];
                                resRay[0][2] = point[2];

                                // new ray direction is the normal of the triangle
                                double[] edge1 = vecSubtract(tri[1], tri[0]);
                                double[] edge2 = vecSubtract(tri[2], tri[0]);
                                double[] normal = vecCross(edge1, edge2);
                                double[] normalizedNormal = vecNormalize(normal);
                                resRay[1][0] = normalizedNormal[0];
                                resRay[1][1] = normalizedNormal[1];
                                resRay[1][2] = normalizedNormal[2];
                                didIntersect = true;
                            }
                            point = null;
                            subtracted = null;
                        }
                        i++;
                    }
                }
            }
        }

        return didIntersect;
    }

}