/**
 *  This file is part of Papareto.
 *	
 *  Papareto is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Papareto is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Papareto.  If not, see <http://www.gnu.org/licenses/>. *
 */

package cnrs.i3s.papareto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FitnessHistory implements Serializable
{
	private final List<Fitness> l = new ArrayList<>();

	public int size()
	{
		return l.size();
	}

	public void add(Fitness d)
	{
		l.add(d);
	}

	public Fitness getFitnessAtGeneration(int g)
	{
		return l.get(g);
	}

	public int getNumberOfGenerationsDuringWhichTheBestFitnessHasNotChanged()
	{
		final Fitness lastFitness = l.get(l.size() - 1);

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
		final double lastFitness = l.get(l.size() - 1).combine();

		for (int generation = l.size() - 2; generation >= 0; --generation)
		{
			double fitness = l.get(generation).combine();
			double diff = Math.abs(lastFitness - fitness);

			if (diff > max)
			{
				return l.size() - generation;
			}
		}

		return l.size();
	}


}
