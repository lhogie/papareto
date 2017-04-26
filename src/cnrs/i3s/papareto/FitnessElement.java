package cnrs.i3s.papareto;

import java.io.Serializable;

public class FitnessElement implements Serializable
{
	public final double value;
	public final double weight;

	public FitnessElement(double value, double weight)
	{
		this.value = value;
		this.weight = weight;
	}

	public double computeWeightedValue()
	{
		return value * weight;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof FitnessElement && equals((FitnessElement) obj);
	}

	public boolean equals(FitnessElement m)
	{
		return value == m.value && weight == m.weight;
	}

	@Override
	public String toString()
	{
		if (weight == 1)
		{
			return String.valueOf(value);
		}
		else
		{
			return "(" + value + ", " + weight + ")";
		}
	}
}