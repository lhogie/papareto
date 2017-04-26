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

import java.util.Random;

public abstract class MutationOperator<R> extends Operator
{
	private double occurenceProbability = 1;
	private int numberOfChanges = 1;

	public void mutate(R e, Random r)
	{
		for (int i = 0; i < numberOfChanges; ++i)
		{
			performOneSingleChange(e, r);
		}
	}

	protected abstract void performOneSingleChange(R individual, Random prng);

	public double getProbability()
	{
		return occurenceProbability;
	}

	public void setProbability(double occurenceProbability)
	{
		this.occurenceProbability = occurenceProbability;
	}

	public int getNumberOfChanges()
	{
		return numberOfChanges;
	}

	public void setNumberOfChanges(int numberOfChanges)
	{
		if (numberOfChanges < 0)
			throw new IllegalArgumentException("numberOfOccurences must be >= 0");

		this.numberOfChanges = numberOfChanges;
	}

}
