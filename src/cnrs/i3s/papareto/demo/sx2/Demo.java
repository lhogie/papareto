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

package cnrs.i3s.papareto.demo.sx2;

import cnrs.i3s.papareto.Individual;
import cnrs.i3s.papareto.Population;

public class Demo
{
    public static void main(String[] args)
    {
	Population<ValueHolder> p = new Population<ValueHolder>(new ValueHolder(0)) {

	    double computeFitness(ValueHolder i)
	    {
		if (isEmpty())
		{
		    return 0;
		}
		else
		{
		    Individual<ValueHolder> best = get(0);

		    if (f(i.value) < f(best.object.value))
		    {
			return best.fitness[0] + 1;
		    }
		    else
		    {
			return best.fitness[0] - 1;
		    }
		}
	    }

	    double f(double x)
	    {
		return (x - 5) * (x - 5);
	    }
	};

	p.getCrossoverOperators().add(new Crossover());
	p.getMutationOperators().add(new Mutation());
	p.expansion(5);
	System.out.println("starting");

	// p.monitor();
	System.out.println("starting");

	while (true)
	{
	    Individual<ValueHolder> b = p.get(0);

	    if (p.makeNewGeneration())
	    {
		System.out.println(p.get(0));
	    }
	}
    }
}
