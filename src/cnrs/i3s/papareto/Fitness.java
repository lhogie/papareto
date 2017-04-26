package cnrs.i3s.papareto;

import java.io.Serializable;
import java.util.Arrays;

public class Fitness implements Serializable
{
	public final FitnessElement[] elements;

	public Fitness(int nbElements)
	{
		if (nbElements < 1)
			throw new IllegalArgumentException(
					"the fitness must be composed of at least one element");

		elements = new FitnessElement[nbElements];
	}

	public double combine()
	{
		double r = 0;

		for (int i = 0; i < elements.length; ++i)
		{
			r += elements[i].computeWeightedValue();
		}

		return r;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Fitness && equals((Fitness) obj);
	}

	public boolean equals(Fitness m)
	{
		for (int i = 0; i < elements.length; ++i)
		{
			if ( ! m.elements[i].equals(elements[i]))
			{
				return false;
			}
		}

		return true;
	}

	public int compareTo(Fitness f)
	{
		return Double.compare(combine(), f.combine());
	}

	@Override
	public String toString()
	{
		if (elements.length == 1)
		{
			return elements[0].toString();
		}
		else
		{
			return Arrays.toString(elements) + " => " + combine();
		}
	}
}
