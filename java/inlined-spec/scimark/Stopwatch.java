/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

/**
 *
 * Provides a stopwatch to measure elapsed time.
 *
 * <P>
 * <DL>
 * <DT><B>Example of use:</B></DT>
 * <DD>
 * <p>
 * 
 * <pre>
 * Stopwatch Q = new Stopwatch;
 * <p>
 * Q.start();
 * 
 * 
 * 
 * Q.stop();
 * System.out.println("elapsed time was: " + Q.read() + " seconds.");
 * </pre>
 *
 * @author Roldan Pozo
 * @version 14 October 1997, revised 1999-04-24
 */
public class Stopwatch {
    boolean running;
    double last_time;
    double total;

    /**
     * Return system time (in seconds)
     *
     */
    public final static double seconds() {
        return (System.currentTimeMillis() * 0.001);
    }

    /**
     * Return system time (in seconds)
     *
     */
    public void reset() {
        running = false;
        last_time = 0.0;
        total = 0.0;
    }

    public Stopwatch() {
        running = false;
        last_time = 0.0;
        total = 0.0;
    }

    /**
     * Start (and reset) timer
     *
     */
    public void start() {
        if (!running) {
            running = true;
            total = 0.0;
            last_time = (System.currentTimeMillis() * 0.001);
        }
    }

    /**
     * Resume timing, after stopping. (Does not wipe out
     * accumulated times.)
     *
     */
    public void resume() {
        if (!running) {
            last_time = (System.currentTimeMillis() * 0.001);
            running = true;
        }
    }

    /**
     * Stop timer
     *
     */
    public double stop() {
        if (running) {
            total += (System.currentTimeMillis() * 0.001) - last_time;
            running = false;
        }
        return total;
    }

    /**
     * Display the elapsed time (in seconds)
     *
     */
    public double read() {
        if (running) {
            total += (System.currentTimeMillis() * 0.001) - last_time;
            last_time = (System.currentTimeMillis() * 0.001);
        }
        return total;
    }
}
