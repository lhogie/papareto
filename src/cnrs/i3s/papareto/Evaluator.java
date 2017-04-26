package cnrs.i3s.papareto;

public abstract class Evaluator<E, R>
{
	public double weight = 1;
	
	public abstract double evaluate(E i, Population<E, R> p);
}
