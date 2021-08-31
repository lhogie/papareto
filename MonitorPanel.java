/* (C) Copyright 2009-2013 CNRS (Centre National de la Recherche Scientifique).

Licensed to the CNRS under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The CNRS licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

*/

/* Contributors:

Luc Hogie (CNRS, I3S laboratory, University of Nice-Sophia Antipolis) 

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
import xycharter.Figure;
import xycharter.Space.MODE;
import xycharter.SwingPlotter;
import xycharter.render.ConnectedLineFigureRenderer;

public class MonitorPanel<A> extends JPanel implements PopulationListener<A, A> {
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

	public MonitorPanel() {

		setLayout(new BorderLayout());

		JPanel labelPanel = new JPanel(new GridLayout(3, 2));
		labelPanel.add(sizeLabel);
		labelPanel.add(bestFitnessLabel);
		labelPanel.add(numberOfGenerationsLabel);
		labelPanel.add(numberOfThreadsLabel);
		add(labelPanel, BorderLayout.NORTH);

		f.addFigure(bestFitnessFigure);
		f.addFigure(worstFitnessFigure);
		ConnectedLineFigureRenderer br = new ConnectedLineFigureRenderer();
		f.setColor(Color.blue);
		ConnectedLineFigureRenderer wr = new ConnectedLineFigureRenderer();
		bestFitnessFigure.addRenderer(br);
		worstFitnessFigure.addRenderer(wr);
		fitnessPlotter.getPlot().getSpace().setMode(MODE.PHYSICS);
		fitnessPlotter.getPlot().setFigure(f);
		fitnessPlotter.getPlot().getSpace().getLegend().setText("Fitness'");
		fitnessPlotter.getPlot().getSpace().getXDimension().getLegend().setText("Number of generations");
		fitnessPlotter.getPlot().getSpace().getYDimension().getLegend().setText("Fitness value");
		fitnessPlotter.getPlot().getSpace().getXDimension().setMinimumIsAutomatic(false);
		tabbedPane.addTab("Fitness", fitnessPlotter);

		operatorPlotter.getPlot().setFigure(new Figure());
		operatorPlotter.getPlot().getSpace().getLegend().setText("Operator success rate");
		operatorPlotter.getPlot().getSpace().getXDimension().getLegend().setText("Number of generations");
		operatorPlotter.getPlot().getSpace().getYDimension().getLegend().setText("Operators success rate");
		operatorPlotter.getPlot().getSpace().setMode(MODE.PHYSICS);
		tabbedPane.addTab("Operators", operatorPlotter);

		perfPlotter.getPlot().addFigure(new Figure());
		ConnectedLineFigureRenderer r = new ConnectedLineFigureRenderer();
		perfPlotter.getPlot().getFigure().addRenderer(r);
		perfPlotter.getPlot().getSpace().getLegend().setText("Number of generations per second");
		perfPlotter.getPlot().getSpace().getXDimension().getLegend().setText("Number of generations");
		perfPlotter.getPlot().getSpace().getYDimension().getLegend().setText("Number of generations per second");
		perfPlotter.getPlot().getSpace().setMode(MODE.PHYSICS);
		tabbedPane.addTab("Profiling", perfPlotter);

		add(tabbedPane, BorderLayout.CENTER);

		setPreferredSize(new Dimension(800, 600));
	}

	private final Map<Operator, Figure> operator_figure = new HashMap();
	private long lastIteration = -1;

	@Override
	public void newIteration(Population<A, A> p, double improve) {
		sizeLabel.setText("size=" + p.size());
		numberOfGenerationsLabel.setText("number of generations=" + p.getNumberOfGenerations());
		bestFitnessLabel.setText("best distance=" + p.getIndividualAt(0).fitness);
		updateFitnessPlotter(p);
		updateOperatorsPlotter(p);
		updatePerfPlotter(p);
	}

	private void updatePerfPlotter(Population<A, A> p) {
		if (lastIteration != -1) {
			long duration = System.currentTimeMillis() - lastIteration;
			perfPlotter.getPlot().getFigure().addPoint(p.getNumberOfGenerations(), duration);
		}

		lastIteration = System.currentTimeMillis();
		perfPlotter.repaint();
	}

	Color[] colors = new Color[] { Color.red, Color.green, Color.blue, Color.yellow, Color.cyan, Color.magenta,
			Color.LIGHT_GRAY, Color.white };

	private void updateOperatorsPlotter(Population<A, A> p) {
		for (Operator o : p.getRepresentation().getRandomIndividualGenerators())
			updateOperatorFigure(o, p);

		for (Operator o : p.getRepresentation().getCrossoverOperators())
			updateOperatorFigure(o, p);

		for (Operator o : p.getRepresentation().getMutationOperators())
			updateOperatorFigure(o, p);

		operatorPlotter.repaint();
	}

	private void updateOperatorFigure(Operator o, Population<A, A> p) {
		Figure operatorFigure = operator_figure.get(o);

		if (operatorFigure == null) {
			operator_figure.put(o, operatorFigure = new Figure());
			// operatorFigure.setName(o.getClass().getName());
			operatorFigure.setName(o.getFriendlyName());

			ConnectedLineFigureRenderer r = new ConnectedLineFigureRenderer();
			operatorFigure.setColor(colors[operator_figure.size()]);
			operatorFigure.addRenderer(r);
			operatorPlotter.getPlot().getFigure().addFigure(operatorFigure);
		}

		operatorFigure.addPoint(p.getNumberOfGenerations(), (int) 1000 * o.getSuccessRate());
		operatorFigure.retainsOnlyLastPoints(100);
	}

	private void updateFitnessPlotter(Population<A, A> p) {
		double bestDistance = p.getIndividualAt(0).fitness.getCombinedFitnessValue();

		if (bestDistance != Double.MAX_VALUE) {
			bestFitnessFigure.addPoint(p.getNumberOfGenerations(), bestDistance);
			bestFitnessFigure.setName("best fitness=" + bestDistance);
		}

		double worstDistance = p.getIndividualAt(p.size() - 1).fitness.getCombinedFitnessValue();

		if (worstDistance != Double.MAX_VALUE) {
			worstFitnessFigure.addPoint(p.getNumberOfGenerations(), worstDistance);
			worstFitnessFigure.setName("worst fitness=" + worstDistance);
		}

		fitnessPlotter.repaint();
	}

	@Override
	public void completed(Population<A, A> p) {
		// TODO Auto-generated method stub

	}

}
