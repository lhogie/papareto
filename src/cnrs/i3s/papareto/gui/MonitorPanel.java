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

package cnrs.i3s.papareto.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import cnrs.i3s.papareto.Operator;
import cnrs.i3s.papareto.Population;
import cnrs.i3s.papareto.PopulationListener;
import oscilloscup.SwingPlotter;
import oscilloscup.data.Figure;
import oscilloscup.data.Point;
import oscilloscup.data.rendering.figure.ConnectedLineFigureRenderer;
import oscilloscup.system.Space.MODE;

public class MonitorPanel extends JPanel
{
    private final JLabel sizeLabel = new JLabel("Size");
    private final JLabel bestFitnessLabel = new JLabel("Best fitness");
    private final JLabel numberOfGenerationsLabel = new JLabel("Number of generations");
    private final JLabel numberOfThreadsLabel = new JLabel("Number of threads");
    private final SwingPlotter fitnessPlotter = new SwingPlotter();
    private final Figure bestFitnessFigure = new Figure();
    private final Figure worstFitnessFigure = new Figure();

    private final SwingPlotter operatorPlotter = new SwingPlotter();
    private final SwingPlotter perfPlotter = new SwingPlotter();

    private final JTabbedPane tabbedPane = new JTabbedPane();

    public MonitorPanel(Population p)
    {
	p.getPopulationListeners().add(new L());

	setLayout(new BorderLayout());

	JPanel labelPanel = new JPanel(new GridLayout(3, 2));
	labelPanel.add(sizeLabel);
	labelPanel.add(bestFitnessLabel);
	labelPanel.add(numberOfGenerationsLabel);
	labelPanel.add(numberOfThreadsLabel);
	add(labelPanel, BorderLayout.NORTH);

	Figure f = new Figure();
	f.addFigure(bestFitnessFigure);
	f.addFigure(worstFitnessFigure);
	ConnectedLineFigureRenderer br = new ConnectedLineFigureRenderer();
	br.setColor(Color.blue);
	ConnectedLineFigureRenderer wr = new ConnectedLineFigureRenderer();
	wr.setColor(Color.red);
	bestFitnessFigure.addRenderer(br);
	worstFitnessFigure.addRenderer(wr);
	fitnessPlotter.getGraphics2DPlotter().getSpace().setMode(MODE.PHYSICS);
	fitnessPlotter.getGraphics2DPlotter().setFigure(f);
	fitnessPlotter.getGraphics2DPlotter().getSpace().getLegend().setText("Fitness'");
	fitnessPlotter.getGraphics2DPlotter().getSpace().getXDimension().getLegend().setText("Number of generations");
	fitnessPlotter.getGraphics2DPlotter().getSpace().getYDimension().getLegend().setText("Fitness value");
	fitnessPlotter.getGraphics2DPlotter().getSpace().getXDimension().setMinimumIsAutomatic(false);
	tabbedPane.addTab("Fitness", fitnessPlotter);

	operatorPlotter.getGraphics2DPlotter().setFigure(new Figure());
	operatorPlotter.getGraphics2DPlotter().getSpace().getLegend().setText("Operator success rate");
	operatorPlotter.getGraphics2DPlotter().getSpace().getXDimension().getLegend().setText("Number of generations");
	operatorPlotter.getGraphics2DPlotter().getSpace().getYDimension().getLegend().setText("Operators success rate");
	operatorPlotter.getGraphics2DPlotter().getSpace().setMode(MODE.PHYSICS);
	tabbedPane.addTab("Operators", operatorPlotter);

	perfPlotter.getGraphics2DPlotter().setFigure(new Figure());
	ConnectedLineFigureRenderer r = new ConnectedLineFigureRenderer();
	perfPlotter.getGraphics2DPlotter().getFigure().addRenderer(r);
	perfPlotter.getGraphics2DPlotter().getSpace().getLegend().setText("Number of generations per second");
	perfPlotter.getGraphics2DPlotter().getSpace().getXDimension().getLegend().setText("Number of generations");
	perfPlotter.getGraphics2DPlotter().getSpace().getYDimension().getLegend()
		.setText("Number of generations per second");
	perfPlotter.getGraphics2DPlotter().getSpace().setMode(MODE.PHYSICS);
	tabbedPane.addTab("Profiling", perfPlotter);

	add(tabbedPane, BorderLayout.CENTER);

	setPreferredSize(new Dimension(800, 600));
    }

    private class L<A> implements PopulationListener<A>
    {
	private final Map<Operator, Figure> operator_figure = new HashMap();
	private long lastIteration = -1;

	@Override
	public void newIteration(Population<A> p, boolean improveSolution)
	{
	    sizeLabel.setText("size=" + p.getSize());
	    numberOfGenerationsLabel.setText("number of generations=" + p.getNumberOfGenerations());
	    bestFitnessLabel.setText("best distance=" + p.getIndividualAt(0).fitness);
	    updateFitnessPlotter(p);
	    updateOperatorsPlotter(p);
	    updatePerfPlotter(p);
	}

	private void updatePerfPlotter(Population<A> p)
	{
	    if (lastIteration != -1)
	    {
		long duration = System.currentTimeMillis() - lastIteration;
		perfPlotter.getGraphics2DPlotter().getFigure()
			.addPoint(new Point(p.getNumberOfGenerations(), duration));
	    }

	    lastIteration = System.currentTimeMillis();
	    perfPlotter.repaint();
	}

	Color[] colors = new Color[] { Color.red, Color.green, Color.blue, Color.yellow, Color.cyan, Color.magenta,
		Color.LIGHT_GRAY, Color.white };

	private void updateOperatorsPlotter(Population<A> p)
	{
	    for (Operator o : p.getCrossoverOperators())
	    {
		updateOperatorFigure(o, p);
	    }

	    for (Operator o : p.getMutationOperators())
	    {
		updateOperatorFigure(o, p);
	    }

	    operatorPlotter.repaint();
	}

	private void updateOperatorFigure(Operator o, Population<A> p)
	{
	    Figure operatorFigure = operator_figure.get(o);

	    if (operatorFigure == null)
	    {
		operator_figure.put(o, operatorFigure = new Figure());
		// operatorFigure.setName(o.getClass().getName());
		operatorFigure.setName(o.getFriendlyName());

		ConnectedLineFigureRenderer r = new ConnectedLineFigureRenderer();
		r.setColor(colors[operator_figure.size()]);
		operatorFigure.addRenderer(r);
		operatorPlotter.getGraphics2DPlotter().getFigure().addFigure(operatorFigure);
	    }

	    operatorFigure.addPoint(new Point(p.getNumberOfGenerations(), (int) 1000 * o.getSuccessRate()));
	    operatorFigure.retainsOnlyLastPoints(100);
	}

	private void updateFitnessPlotter(Population<A> p)
	{
	    double bestDistance = p.getIndividualAt(0).fitness[0];

	    if (bestDistance != Double.MAX_VALUE)
	    {
		bestFitnessFigure.addPoint(new Point(p.getNumberOfGenerations(), bestDistance));
		bestFitnessFigure.setName("best fitness=" + bestDistance);
	    }

	    double worstDistance = p.getIndividualAt(p.getSize() - 1).fitness[0];

	    if (worstDistance != Double.MAX_VALUE)
	    {
		worstFitnessFigure.addPoint(new Point(p.getNumberOfGenerations(), worstDistance));
		worstFitnessFigure.setName("worst fitness=" + worstDistance);
	    }

	    fitnessPlotter.repaint();
	}

	@Override
	public void completed(Population<A> p)
	{
	    // TODO Auto-generated method stub
	    
	}
    }
}
