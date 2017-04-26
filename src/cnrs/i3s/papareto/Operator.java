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

public class Operator implements Serializable
{
	public int success = 0, numberOfFailure = 0;

	public double getSuccessRate()
	{
		return success / (double) (success + numberOfFailure);
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
}
