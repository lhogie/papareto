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

package cnrs.i3s.papareto.demo.string_morphing.operator;

import java.util.Random;

import cnrs.i3s.papareto.CrossoverOperator;

public class PrefixSuffixCrossover extends CrossoverOperator<StringBuilder>
{

	@Override
	public StringBuilder crossover(StringBuilder a, StringBuilder b, Random r)
	{
		if (r.nextDouble() < 0.5)
		{
			StringBuilder tmp = a;
			a = b;
			b = tmp;
		}

		// take a prefix from a
		String aPrefix = a.length() > 0 ? a.substring(0, r.nextInt(a.length())) : "";

		// take a suffix from b
		String bSuffix = b.length() > 0 ? b.substring(r.nextInt(b.length())) : "";

		// append them
		return new StringBuilder(aPrefix + bSuffix);
	}

	@Override
	public String getFriendlyName()
	{
		return "random crossover";
	}
}
