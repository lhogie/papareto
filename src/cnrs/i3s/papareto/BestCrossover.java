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

import java.util.Random;

public abstract class BestCrossover<E extends Cloneable> extends CrossoverOperator<E>
{
    @Override
    public String getFriendlyName()
    {
	return "elitist crossover";
    }

    @Override
    public E crossover(Individual<E> i1, Individual<E> i2, Population<E> p, Random r)
    {
	Individual<E> best = p.compareObjectiveValues(i1.fitness, i2.fitness) > 0 ? i1 : i2;
	return clone(best.object);
    }

    protected abstract E clone(E o);
}
