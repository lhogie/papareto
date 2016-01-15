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

package cnrs.i3s.papareto.demo.string;

import cnrs.i3s.papareto.Population;

public class Demo
{
    public static void main(String[] args)
    {
	StringBuilder initialInidividual = new StringBuilder("Salut ca va?");

	Population<StringBuilder> p = new Population<StringBuilder>(initialInidividual) {

	    protected double computeFitness1(StringBuilder i)
	    {
		return -Math.abs(10 - i.length());
	    }

	    protected double computeFitness2(StringBuilder i)
	    {
		return -Math.abs(5 - i.toString().split(" ").length);
	    }

	    protected double computeFitness3(StringBuilder i)
	    {
		return -i.indexOf("  ");
	    }
	};

	p.getCrossoverOperators().add(new PrefixSuffixCrossover());
	p.getCrossoverOperators().add(new HalfHalfCrossover());
	p.getMutationOperators().add(new CharAdditionMutation());
	p.getMutationOperators().add(new CharAlterationMutation());
	p.getMutationOperators().add(new CharDeletionMutation());

	while (true)
	{
	    if (p.makeNewGeneration(100))
	    {
		System.out.println("Generation: " + p.getNumberOfGenerations() + ": " + p.get(0));

		if (p.get(0).fitness[0] == 0)
		{
		    break;
		}
	    }
	}

    }
}
