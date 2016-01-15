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

import java.util.Random;

import cnrs.i3s.papareto.CrossoverOperator;
import cnrs.i3s.papareto.Individual;
import cnrs.i3s.papareto.Population;


public class Crossover extends CrossoverOperator<ValueHolder>
{

    @Override
    public ValueHolder crossover(Individual<ValueHolder> i1, Individual<ValueHolder> i2, Population<ValueHolder> p, Random r)
    {
	double diff = Math.abs(i1.object.value - i2.object.value);
	double min = Math.abs(i1.object.value - i2.object.value);
	
	// go somewhere in between
	return new ValueHolder(min + diff * r.nextDouble());
    }

}
