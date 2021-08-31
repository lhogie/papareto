/* (C) Copyright 2009-2013 CNRS (Centre National de la Recherche Scientifique).

Licensed to the CNRS under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The CNRS licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

*/

/* Contributors:

Luc Hogie (CNRS, I3S laboratory, University of Nice-Sophia Antipolis) 

*/
 
 
package cnrs.i3s.papareto;

import java.io.Serializable;
import java.util.Arrays;

public class FitnessMeasure implements Serializable
{
	public final double[] elements;
	private final double combination;

	public FitnessMeasure(double[] values, Combiner combiner)
	{
		if (combiner == null)
			throw new NullPointerException(
					"the fitness needs a combiner in order to be able to compute one single double value out of the set of its elements");

		elements = values;
		this.combination = combiner.combine(values);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof FitnessMeasure && equals((FitnessMeasure) obj);
	}

	public boolean equals(FitnessMeasure m)
	{
		for (int i = 0; i < elements.length; ++i)
		{
			if (m.elements[i] != elements[i])
			{
				return false;
			}
		}

		return true;
	}

	public int compareTo(FitnessMeasure f)
	{
		return Double.compare(combination, combination);
	}

	@Override
	public String toString()
	{
		if (elements.length == 1)
		{
			return elements[0] + "";
		}
		else
		{
			return Arrays.toString(elements) + " => " + combination;
		}
	}

	public double getCombinedFitnessValue()
	{
		return combination;
	}
}
