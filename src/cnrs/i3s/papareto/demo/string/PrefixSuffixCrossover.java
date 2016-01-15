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

import java.util.Random;

import cnrs.i3s.papareto.CrossoverOperator;
import cnrs.i3s.papareto.Individual;
import cnrs.i3s.papareto.Population;


public class PrefixSuffixCrossover extends CrossoverOperator<StringBuilder>
{

    @Override
    public StringBuilder crossover(Individual<StringBuilder> a, Individual<StringBuilder> b, Population<StringBuilder> p, Random r)
    {
	if (r.nextDouble() < 0.5)
	{
	    Individual<StringBuilder> tmp = a;
	    a = b;
	    b = tmp;
	}

	StringBuilder s1 = a.object;
	StringBuilder s2 = b.object;
	
	// take a prefix from a
	String aPrefix = s1.length() > 0 ? s1.substring(0, r.nextInt(s1.length())) : "";
	
	// take a suffix from b
	String bSuffix = s2.length() > 0 ? s2.substring(r.nextInt(s2.length())) : "";
	
	// append them
	return new StringBuilder(aPrefix + bSuffix);
    }

    @Override
    public String getFriendlyName()
    {
	return "random crossover";
    }
}
