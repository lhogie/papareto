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
import java.util.ArrayList;
import java.util.List;

public class FitnessMeasureHistory implements Serializable
{
	private final List<FitnessMeasure> l = new ArrayList<>();

	public int size()
	{
		return l.size();
	}

	public void add(FitnessMeasure d)
	{
		l.add(d);
	}

	public FitnessMeasure getFitnessAtGeneration(int g)
	{
		return l.get(g);
	}

	public int getNumberOfGenerationsDuringWhichTheBestFitnessHasNotChanged()
	{
		if (l.isEmpty())
			return 0;
		
		final FitnessMeasure lastFitness = l.get(l.size() - 1);

		for (int generation = l.size() - 2; generation >= 0; --generation)
		{
			if ( ! l.get(generation).equals(lastFitness))
			{
				return l.size() - generation;
			}
		}

		return l.size();
	}

	public int getNumberOfGenerationsDuringWhichTheBestFitnessHasNotChangedMoreThan(
			double max)
	{
		if (l.isEmpty())
			return 0;
		
		final double lastFitness = l.get(l.size() - 1).getCombinedFitnessValue();

		for (int generation = l.size() - 2; generation >= 0; --generation)
		{
			double fitness = l.get(generation).getCombinedFitnessValue();
			double diff = Math.abs(lastFitness - fitness);

			if (diff > max)
			{
				return l.size() - generation;
			}
		}

		return l.size();
	}
}
