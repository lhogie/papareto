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

import java.util.Arrays;
import java.util.List;

/**
 * A Bucket is an object with associate to an individual: - a fitness - the
 * operators utilized to generate it
 * 
 * @author lhogie
 * 
 * @param <E>
 */
public class Individual<E>
{
    public final E object;
    public final double[] fitness;
    
    // the operators used to created this individual
    public final List<Operator> operators;
  
    public Individual(E o, double[] fitness, List<Operator> operators)
    {
	if (o.getClass() == String.class)
	    throw new IllegalArgumentException("does not support immutable objects");

	this.object = o;
	this.fitness = fitness;
	this.operators = operators;

    }

    @Override
    public String toString()
    {
	return "['" + object + "', f=" + Arrays.toString(fitness) + "]";
    }

    @Override
    public boolean equals(Object i)
    {
	if (i == null)
	{
	    return false;
	}
	else
	{
	    Object o = ((Individual<E>) i).object;
	    return o.equals(object);
	}
    }
}