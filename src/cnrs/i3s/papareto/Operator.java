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

import java.io.Serializable;
import java.util.List;

public class Operator implements Serializable
{
	public int nbSuccess = 0, nbFailure = 0;

	public double getSuccessRate()
	{
		return nbSuccess / (double) (nbSuccess + nbFailure);
	}

	@Override
	public String toString()
	{
		return getClass().getName() + " succes rate=" + getSuccessRate();
	}

	public String getFriendlyName()
	{
		return getClass().getName();
	}
	
	public static <O extends Operator> double[] getUsageProbabilities(List<O> operators)
	{
		double[] a = new double[operators.size()];

		for (int i = 0; i < a.length; ++i)
		{
			Operator o = operators.get(i);

			// if the operator was never used in the past
			if (o.nbFailure + o.nbSuccess == 0)
			{
				// give it max opportunity by assuming it would have top performance
				a[i] = 1;
			}
			else
			{
				a[i] = operators.get(i).getSuccessRate();
			}
		}

		return a;
	}
}
