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

package cnrs.i3s.papareto.impl.bytes;

import java.util.BitSet;
import java.util.Random;

import cnrs.i3s.papareto.CrossoverOperator;

public class ByteCrossover extends CrossoverOperator<BitSet>
{

	@Override
	public String getFriendlyName()
	{
		return "split crossover";
	}

	@Override
	public BitSet crossover(BitSet i1, BitSet i2, Random r)
	{
		BitSet b = new BitSet();
		int splitIndex = r.nextInt(Math.min(i1.length(), i2.length()));

		// copy the begining of i1
		System.arraycopy(i1, 0, b, 0, splitIndex);

		// copy the end of i2
		System.arraycopy(i2, splitIndex, b, splitIndex, i2.length() - splitIndex);

		return b;
	}

}
