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

import cnrs.i3s.papareto.MutationOperator;

public class BitMutation extends MutationOperator<BitSet>
{

	@Override
	public String getFriendlyName()
	{
		return "Bit Mutation";
	}

	@Override
	protected void performOneSingleChange(BitSet bytes, Random r)
	{
		int index = r.nextInt(bytes.length());
		bytes.set(index, r.nextBoolean());
	}
}
