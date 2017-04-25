package eg.edu.alexu.csd.control.sfg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solver {

	private int numOfNodes;
	private double transfareFunction;
	private double[][] graph;
	private ArrayList<ArrayList<Integer>> forwardPaths;
	private ArrayList<Double> forwardPathsGains;
	private ArrayList<List<Integer>> loops;
	private ArrayList<boolean[]> loopsMarker;
	private ArrayList<Double> loopsGains;
	private ArrayList<List<Integer>> nonTouchingLoops;
	private ArrayList<Double> nonTouchingLoopsGains;
	private ArrayList<boolean[]> forwardPathsMarker;
	private double delta;
	private double[] deltas;
	private boolean modified;
	
	public void solve(double[][] graph) {
		numOfNodes = graph.length;
		this.graph = graph;
		//modify output node to have zero out degree
		prepareGraph();
		beginSolver();
	}
	
	private void prepareGraph() {
		boolean flag = false;
		for (int i = 0; i < numOfNodes; i++) {
			if (graph[numOfNodes-1][i] != 0) {
				flag = true;
				break;
			}
		}
		if (flag == true) {
			numOfNodes++;
			double[][] newGraph = new double[numOfNodes][numOfNodes];
			for (int i = 0; i < numOfNodes-1; i++) {
				for (int j = 0; j < numOfNodes-1; j++) {
					newGraph[i][j] = graph[i][j];
				}
			}
			newGraph[numOfNodes-2][numOfNodes-1] = 1;
			graph = newGraph;
			modified = true;
		}
	}
	
	private void beginSolver() {
		forwardPaths = new ArrayList<ArrayList<Integer>>();
		loops = new ArrayList<List<Integer>>();
		forwardPathsGains = new ArrayList<Double>();
		loopsMarker = new ArrayList<boolean[]>();
		loopsGains = new ArrayList<Double>();
		nonTouchingLoops = new ArrayList<List<Integer>>();
		nonTouchingLoopsGains = new ArrayList<Double>();
		//generation of forward paths and loops
		dfs(new ArrayList<Integer>(), new boolean[numOfNodes], 0);
		//give every loop a label
		ArrayList<ArrayList<Integer>> labels = labelLoops();
		//generate non touching loops
		nonTouchingLoops(labels, 1);
		//calculate overall transfer function
		transfareFunction = calcOverallTF();
	}
	
	private void dfs (ArrayList<Integer> path, boolean[] visited, int pointer) {
		//add current node to bath and mark as visited
		path.add(pointer);
		visited[pointer] = true;
		//detecting end of path
		if (pointer == numOfNodes-1 && path.size() > 1) {
			//add this path to forward paths and calculate its gain
			forwardPaths.add(new ArrayList<Integer>(path));
			forwardPathsGains.add(calculateGains(path));
			return;
		}
		for (int i = 0; i < numOfNodes; i++) {
			//tracing path through graph
			if (graph[pointer][i] != 0) {
				//if node is not visited then mark it and apply dfs
				if (!visited[i]) {
					dfs(path, visited, i);
					//remove node when returning from recursion
					path.remove(path.size()-1);
					visited[i] = false;
				}
				else {
					//here the node has already been visited then it's a loop
					if (path.size() == 1) {continue;};
					List<Integer> tempLoop = new ArrayList<Integer>(path.subList(path.indexOf(i), path.size()));
					tempLoop.add(tempLoop.get(0));
					//add sub path to loops and calculate its gain
					if (!loops.contains(tempLoop)) {
						boolean[] loopMarker = markLoop(tempLoop);
						if (!isEquivalent(loopMarker)) {
							loops.add(tempLoop);
							loopsMarker.add(loopMarker);
							loopsGains.add(calculateGains(tempLoop));
						}
					}
				}
			}
		}
	}
	
	private Double calculateGains (List<Integer> list) {
		double gain = 1;
		if (list.size() > 1) {
			for (int i = 0; i < list.size()-1; i++) {
				gain *= graph[list.get(i)][list.get(i+1)];
			}
			return gain;
		}
		return graph[list.get(0)][list.get(0)];
	}
	
	private boolean[] markLoop(List<Integer> loop) {
		boolean[] result = new boolean[numOfNodes];
		for (int i = 0; i < loop.size(); i++) {
			result[loop.get(i)] = true;
		}
		return result;
	}
	
	private boolean isEquivalent(boolean[] array) {
		for (int i = 0; i < loopsMarker.size(); i++) {
			int flag = 0;
			for (int j = 0; j < array.length; j++) {
				if (loopsMarker.get(i)[j] == array[j]) {
					flag++;
				}
				if (flag == array.length) {
					return true;
				}
			}
		}
		return false;
	}
	
	private ArrayList<ArrayList<Integer>> labelLoops() {
		ArrayList<ArrayList<Integer>> labels = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < loops.size(); i++) {
			labels.add(new ArrayList<Integer>());
			labels.get(i).add(i);
		}
		return labels;
	}
	
	private void nonTouchingLoops(ArrayList<ArrayList<Integer>> labels, int level) {
		ArrayList<ArrayList<Integer>> nextLevelList = new ArrayList<ArrayList<Integer>>();
		boolean nextLevelAvailable = false;
		//first loop on elements of labels
		for (int i = 0; i < labels.size(); i++) {
			//second loop on all next elements to current element
			for (int j = i+1; j < labels.size(); j++) {
				//loop on inner elements to every array element
				for (int k = 0; k < labels.get(j).size(); k++) {
					ArrayList<Integer> testList = new ArrayList<Integer>();
					testList.addAll(labels.get(i));
					testList.add(labels.get(j).get(k));
					//test that list has non touching loops
					if (!isTouchingLoops(testList)) {
						nextLevelAvailable = true;
						nextLevelList.add(new ArrayList<Integer>(testList));
						nonTouchingLoops.add(new ArrayList<Integer>(testList));
						nonTouchingLoopsGains.add(calcNonLoopsGain(testList));
					}
				}
			}
		}
		//test to proceed to next level
		if (nextLevelAvailable) {
			level++;
			nonTouchingLoops(nextLevelList, level);
		}
	}
	
	private boolean isTouchingLoops(ArrayList<Integer> list) {
		//check the marker loops list if more than one element is equivalent
		for (int i = 0; i < numOfNodes; i++) {
			int touchFlag = 0;
			for (int j = 0; j < list.size(); j++) {
				if (loopsMarker.get(list.get(j))[i]) {
					touchFlag++;
				}
			}
			if (touchFlag >= 2) {
				return true;
			}
		}
		return false;
	}
	
	private double calcNonLoopsGain(ArrayList<Integer> list) {
		double gain = 1;
		for (int i = 0; i < list.size(); i++) {
			gain *= loopsGains.get(list.get(i));
		}
		return gain;
	}
	
	private double calcOverallTF() {
		forwardPathsMarker = markForwardPaths();
		int level = 2;
		int sign = -1;
		double delta = 0;
		double temp = 0;
		//calculate delta from loops gains
		for (int i = 0; i < nonTouchingLoops.size(); i++) {
			if (nonTouchingLoops.get(i).size() == level) {
				temp += nonTouchingLoopsGains.get(i);
			}
			else {
				delta += Math.pow(sign, level)*temp;
				temp = 0;
				level++;
			}
			if (i == nonTouchingLoops.size()-1) {
				delta += Math.pow(sign, level)*temp;
			}
		}
		temp = 0;
		for (int i = 0; i < loops.size(); i++) {
			temp += loopsGains.get(i);
		}
		delta = 1 - temp + delta;
		this.delta = delta;
		//calculate small deltas from path gains
		int smallDelta = 0;
		double[] smallPaths = new double[forwardPaths.size()];
		temp = 0;
		level = 2;
		for (int i = 0; i < forwardPaths.size(); i++) {
			for (int j = 0; j < nonTouchingLoops.size(); j++) {
				if (nonTouchingLoops.get(j).size() == level && !touchingForwardPath(nonTouchingLoops.get(j), i)) {
					temp += nonTouchingLoopsGains.get(j);
				}
				else {
					smallDelta += Math.pow(sign, level)*temp;
					temp = 0;
					level++;
				}
				if (i == nonTouchingLoops.size()-1) {
					smallDelta += Math.pow(sign, level)*temp;
				}
			}
			smallPaths[i] = smallDelta;
			smallDelta = 0;
			temp = 0;
			level = 2;
		}
		temp = 0;
		for (int i = 0; i < forwardPaths.size(); i++) {
			for (int j = 0; j < loops.size(); j++) {
				if (!isTouchingPath(i,j)) {
					temp += loopsGains.get(j);
				}
			}
			smallPaths[i] = 1 - temp + smallPaths[i];
			temp = 0;
		}
		this.deltas = new double[smallPaths.length];
		deltas = smallPaths.clone();
		double numerator = 0;
		for (int i = 0; i < smallPaths.length; i++) {
			smallPaths[i] *= forwardPathsGains.get(i);
			numerator += smallPaths[i];
		}
		return numerator/delta;
	}
	
	private ArrayList<boolean[]> markForwardPaths() {
		ArrayList<boolean[]> result = new ArrayList<boolean[]>();
		for (int i = 0; i < forwardPaths.size(); i++) {
			boolean[] temp = new boolean[numOfNodes];
			for (int j = 0; j < forwardPaths.get(i).size(); j++) {
				temp[forwardPaths.get(i).get(j)] = true;
			}
			result.add(temp);
		}
		return result;
	}
	
	private boolean touchingForwardPath(List<Integer> loop, int forwardPath) {
		for (int i = 0; i < numOfNodes; i++) {
			int touchFlag = 0;
			for (int j = 0; j < loop.size(); j++) {
				if (loopsMarker.get(loop.get(j))[i]) {
					touchFlag++;
				}
			}
			if (forwardPathsMarker.get(forwardPath)[i]) {
				touchFlag++;
			}
			if (touchFlag >= 2) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isTouchingPath(int pathIndex, int arrayIndex) {
		int touchFlag = 0;
		for (int i = 0; i < numOfNodes; i++) {
			if (loopsMarker.get(arrayIndex)[i]==true && forwardPathsMarker.get(pathIndex)[i]==true) {
				touchFlag++;
			}
		}
		if (touchFlag >= 1) {
			return true;
		}
		return false;
	}
	
	public String[] getForwardPaths() {
		String[] forwardPath = new String[forwardPaths.size()];
		int counter = 0;
		for (ArrayList<Integer> array : forwardPaths) {
			forwardPath[counter] = "";
			for (int num : array) {
				if (modified && num == numOfNodes-1) {
					continue;
				}
				forwardPath[counter] += num+1 + " ";
			}
			counter++;
		}
		return forwardPath;
	}
	
	public Double[] getForwardPathsGains() {
		Double[] gains = new Double[forwardPathsGains.size()];
		int counter = 0;
		for (Double d : forwardPathsGains) {
			gains[counter] = d;
			counter++;
		}
		return gains;
	}
	
	public String[] getLoops() {
		String[] loop = new String[loops.size()];
		int counter = 0;
		for (List<Integer> array : loops) {
			loop[counter] = "";
			for (int num : array) {
				loop[counter] += num+1 + " ";
			}
			counter++;
		}
		return loop;
	}
	
	public Double[] getLoopsGains() {
		Double[] gains = new Double[loopsGains.size()];
		int counter = 0;
		for (Double d : loopsGains) {
			gains[counter] = d;
			counter++;
		}
		return gains;
	}
	
	public String[] getNonTouchingLoops() {
		String[] loop = new String[nonTouchingLoops.size()];
		int counter = 0;
		for (List<Integer> array : nonTouchingLoops) {
			loop[counter] = "";
			for (int num : array) {
				int[] temp = new int[loops.get(num).size()];
				for (int i = 0; i < temp.length; i++) {
					temp[i] = loops.get(num).get(i)+1;
				}
				String tempString = Arrays.toString(temp);
				tempString = tempString.replace("[", "");
				tempString = tempString.replace("]", "");
				tempString = tempString.replace(",", "");
				loop[counter] += tempString + " ,";
			}
			int trim = loop[counter].lastIndexOf(',');
			loop[counter] = loop[counter].substring(0, trim);
			counter++;
		}
		return loop;
	}
	
	public Double[] getNonTouchingGains() {
		Double[] gains = new Double[nonTouchingLoopsGains.size()];
		int counter = 0;
		for (Double d : nonTouchingLoopsGains) {
			gains[counter] = d;
			counter++;
		}
		return gains;
	}
	
	public double[] getSmallDeltas() {
		return deltas;
	}
	
	public Double getTransfareFunction() {
		return this.transfareFunction;
	}
}
