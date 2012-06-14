package com.esamir;

import java.util.Random;

/**
 * User: Samir Faci
 * Date: 6/13/12
 * Time: 4:35 PM
 */
public class Utility {
    //Singelton stuff
    private static Utility instance;

    public static Utility getInstance() {
        if (instance == null)
            instance = new Utility();

        return instance;
    }

    //non static

    Random gen = new Random(System.currentTimeMillis());
    ;
    int MIN_RANGE = 0;
    int MAX_RANGE = 10000;


    private Utility() {


    }


    public void setMaxRange(int value) {
        MAX_RANGE = value;
    }

    public Integer getMaxRange() {
        return MAX_RANGE;
    }

    public double getRandomDouble() {
        return MIN_RANGE + (MAX_RANGE - MIN_RANGE) * gen.nextDouble();

    }

    public boolean getRandomBoolean() {
        return (gen.nextInt(2) == 1) ? Boolean.TRUE : Boolean.FALSE;
    }

    public double getRandomPercent() {
        return (gen.nextDouble() * 100);
    }

    public int getRandomInt() {
        return MIN_RANGE + (MAX_RANGE - MIN_RANGE) * gen.nextInt();
    }


    public long getRandomLong() {
        return (long) getRandomInt();
    }

    public String getRandomString() {
        return "" + getRandomLong();
    }


}

