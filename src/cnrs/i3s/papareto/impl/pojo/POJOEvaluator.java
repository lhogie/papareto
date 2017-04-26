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

package cnrs.i3s.papareto.impl.pojo;

import cnrs.i3s.papareto.Evaluator;
import cnrs.i3s.papareto.Population;

public abstract class POJOEvaluator<E> extends Evaluator<E, E>
{

	@Override
	final public double evaluate(E i, Population<E, E> p)
	{
		return evaluate(i, (POJOPopulation<E>) p);
	}

	public abstract double evaluate(E i, POJOPopulation<E> p);
}
