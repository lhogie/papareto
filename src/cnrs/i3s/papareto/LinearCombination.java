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

import java.util.Arrays;

public class LinearCombination implements Combiner
{
	public final double[] weights;

	public LinearCombination()
	{
		// no weight, so they are assumed to be always 1
		weights = null;
	}

	public LinearCombination(int nbElements)
	{
		weights = new double[nbElements];
		Arrays.fill(weights, 1);
	}

	public LinearCombination(double... weigths)
	{
		this.weights = Arrays.copyOf(weigths, weigths.length);
	}

	@Override
	public double combine(double[] a)
	{
		double r = 0;

		if (weights == null)
		{
			for (int i = 0; i < a.length; ++i)
			{
				r += a[i];
			}
		}
		else
		{
			if (weights.length != a.length)
				throw new IllegalStateException();

			for (int i = 0; i < a.length; ++i)
			{
				r += a[i] * weights[i];
			}
		}

		return r;
	}
}
