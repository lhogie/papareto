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
 
 
package cnrs.i3s.papareto.distributed_computing;

import java.util.List;

import cnrs.i3s.papareto.Individual;
import cnrs.i3s.papareto.Population;
import octojus.OctojusNode;

public interface IslandsListener<E, R>
{
	void sendingPopulations(Population<E, R> subPopulation, OctojusNode targetNode);

	void receivingIndividuals(OctojusNode senderNode, List<Individual<E>> individuals);

	void terminating(Population<E, R> result);
}
