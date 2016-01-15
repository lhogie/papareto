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

import cnrs.i3s.papareto.MutationOperator;
import cnrs.i3s.papareto.Population;


public class Mutation extends MutationOperator<ValueHolder>
{
    public Mutation()
    {
	setNumberOfChanges(1);
	setProbability(0.1);
    }

    @Override
    protected void performOneSingleChange(ValueHolder h, Population<ValueHolder> p, Random r)
    {
	if (p.getFitnessHistory().size() == 0)
	{
	} else 	if (p.getFitnessHistory().size() == 1)
	{
	    
	}
	else
	{
	    double a = p.getFitnessHistory().getFitnessAtGeneration(p.getNumberOfGenerations())[0];
	    double b = p.getFitnessHistory().getFitnessAtGeneration(p.getNumberOfGenerations() - 1)[0];
	    double improvement = Math.abs(a - b);
	    h.value += r.nextDouble() * improvement - (improvement / 2);
	}
    }

}
