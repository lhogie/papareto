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

import toools.text.TextUtilities;
import cnrs.i3s.papareto.MutationOperator;
import cnrs.i3s.papareto.Population;


public class CharAdditionMutation extends MutationOperator<StringBuilder>
{
    @Override
    public void performOneSingleChange(StringBuilder s, Population<StringBuilder> p, Random r)
    {
	if (s.length() == 0)
	{
	    s.append(TextUtilities.pickUpOneRandomChar(r));
	}
	else
	{
	    int randomPosition = r.nextInt(s.length());
	    s.insert(randomPosition, TextUtilities.pickUpOneRandomChar(r));
	}
    }

    @Override
    public String getFriendlyName()
    {
	return "char addition";
    }
}
