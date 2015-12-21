package com.example.jong.eyehelper;

//Same as before
/**
 * Created by jong on 11/19/15.
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//You guys leaned heavily on this code, be sure to talk about this if
//you guys make a readme for your github.

/*
 * So im just going to condense all my thoughts about your borrowed code
 * here so its easier to read and digest. First of all, I think its great
 * that you guys showed the initiative to go out and find code that not
 * only fixes your sensitivity problems but improves performance in code
 * rather than in hardware. Second, you cited your borrowed code and 
 * made sure that it was truelly redistributable. Good job on those points.
 * My only worry here, is that you may not have gotten as much out of this
 * portion of the project as the teaching team and you may have liked.
 * It is very important that every line of code you place in a program
 * you call yours is functionally understood by the team. In this case,
 * as significant portion(8/12 class files, ~1800/2600 lines) were not
 * written by you. As such, you really should, and are, expected to know
 * the intricacies of how your code works. That being said, a large portion
 * of creating novel and interesting projects is by working off the 
 * knowledge and effort of others. In any case, I really hope you read this
 * and understand where I am coming from. Expect questions from me regarding
 * the function of Kaleb's code and how you discovered it. Great work finding
 * this suite of tools, it fits your project superbly!
*/

/*
 * Gyroscope Explorer
 * Copyright (C) 2013-2015, Kaleb Kircher - Kircher Engineering, LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Implements a mean filter designed to smooth the data points based on a time
 * constant in units of seconds. The mean filter will average the samples that
 * occur over a period defined by the time constant... the number of samples
 * that are averaged is known as the filter window. The approach allows the
 * filter window to be defined over a period of time, instead of a fixed number
 * of samples. This is important on Android devices that are equipped with
 * different hardware sensors that output samples at different frequencies and
 * also allow the developer to generally specify the output frequency. Defining
 * the filter window in terms of the time constant allows the mean filter to
 * applied to all sensor outputs with the same relative filter window,
 * regardless of sensor frequency.
 *
 * @author Kaleb
 * @version %I%, %G%
 *
 */
public class MeanFilterSmoothing
{
    private static final String tag = MeanFilterSmoothing.class.getSimpleName();

    private float timeConstant = 1;
    private float startTime = 0;
    private float timestamp = 0;
    private float hz = 0;

    private int count = 0;
    // The size of the mean filters rolling window.
    private int filterWindow = 20;

    private boolean dataInit;

    private ArrayList<LinkedList<Number>> dataLists;

    /**
     * Initialize a new MeanFilter object.
     */
    public MeanFilterSmoothing()
    {
        dataLists = new ArrayList<LinkedList<Number>>();
        dataInit = false;
    }

    public void setTimeConstant(float timeConstant)
    {
        this.timeConstant = timeConstant;
    }

    public void reset()
    {
        startTime = 0;
        timestamp = 0;
        count = 0;
        hz = 0;
    }

    /**
     * Filter the data.
     *
     * @param iterator
     *            contains input the data.
     * @return the filtered output data.
     */
    public float[] addSamples(float[] data)
    {
        // Initialize the start time.
        if (startTime == 0)
        {
            startTime = System.nanoTime();
        }

        timestamp = System.nanoTime();

        // Find the sample period (between updates) and convert from
        // nanoseconds to seconds. Note that the sensor delivery rates can
        // individually vary by a relatively large time frame, so we use an
        // averaging technique with the number of sensor updates to
        // determine the delivery rate.
        hz = (count++ / ((timestamp - startTime) / 1000000000.0f));

        filterWindow = (int) (hz * timeConstant);

        for (int i = 0; i < data.length; i++)
        {
            // Initialize the data structures for the data set.
            if (!dataInit)
            {
                dataLists.add(new LinkedList<Number>());
            }

            dataLists.get(i).addLast(data[i]);

            if (dataLists.get(i).size() > filterWindow)
            {
                dataLists.get(i).removeFirst();
            }
        }

        dataInit = true;

        float[] means = new float[dataLists.size()];

        for (int i = 0; i < dataLists.size(); i++)
        {
            means[i] = (float) getMean(dataLists.get(i));
        }

        return means;
    }

    /**
     * Get the mean of the data set.
     *
     * @param data
     *            the data set.
     * @return the mean of the data set.
     */
    private float getMean(List<Number> data)
    {
        float m = 0;
        float count = 0;

        for (int i = 0; i < data.size(); i++)
        {
            m += data.get(i).floatValue();
            count++;
        }

        if (count != 0)
        {
            m = m / count;
        }

        return m;
    }

}
