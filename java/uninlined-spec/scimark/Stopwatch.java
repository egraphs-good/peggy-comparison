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
    public boolean running;
    public double last_time;
    public double total;

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
        reset();
    }

    /**
     * Start (and reset) timer
     *
     */
    public void start() {
        if (!running) {
            running = true;
            total = 0.0;
            last_time = seconds();
        }
    }

    /**
     * Resume timing, after stopping. (Does not wipe out
     * accumulated times.)
     *
     */
    public void resume() {
        if (!running) {
            last_time = seconds();
            running = true;
        }
    }

    /**
     * Stop timer
     *
     */
    public double stop() {
        if (running) {
            total += seconds() - last_time;
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
            total += seconds() - last_time;
            last_time = seconds();
        }
        return total;
    }
}
