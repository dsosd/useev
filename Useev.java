import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

//TODO replace Pair with record, on upgrade to java 14
class Pair{
	public int k;
	public double v;

	Pair(int key, double val){
		k = key;
		v = val;
	}
}

//TODO change to a generic with int => T
//utterly stumped on how though because unable to instantiate a variable of type T
class Graph{
	public ArrayList<ArrayList<Pair>> edges;
	private int edge_count = 0;

	public void set_num_vertices(int num){
		edges = new ArrayList<>(num);

		for (int i = 0; i < num; ++i){
			edges.add(new ArrayList<>());
		}
		edge_count = 0;
	}

	public void add_edge(int src, int dest, double val){
		ArrayList<Pair> temp = edges.get(src);
		temp.add(new Pair(dest, val));

		edges.set(src, temp);
		++edge_count;
	}

	public int num_vertices(){
		return edges.size();
	}

	public int num_edges(){
		return edge_count;
	}

	//aux functions
	public double dfs(int curr, HashSet<Integer> target){
		return dfs_recurv(curr, target, new HashSet<>());
	}

	private double dfs_recurv(int curr, HashSet<Integer> target, HashSet<Integer> visited){
		visited.add(curr);

		for (Pair it: edges.get(curr)){
			//found a target vertex, multiply up the stack of calls
			if (target.contains(it.k)){
				return it.v;
			}

			//this check comes after target check to allow self loops to work
			if (visited.contains(it.k)){
				continue;
			}

			double ret = dfs_recurv(it.k, target, visited);
			//MAGIC 0.0 is sentinel for no path to a target vertex
			if (ret != 0.0){
				return it.v * ret;
			}
		}

		return 0.0;
	}
}

class Useev{
	public HashMap<String, Integer> get_units(){
		List<String> units = List.of(
			//length shorthand
			"m", "cm", "km", "au", "in", "ft", "mi",
			//mass shorthand
			"g", "kg", "lb",
			//time shorthand
			"s", "min", "hr",

			//special
			"smoot"
		);

		HashMap<String, Integer> ret = new HashMap<String, Integer>();
		for (int i = 0; i < units.size(); ++i){
			ret.put(units.get(i), i);
		}
		return ret;
	}

	public Graph get_graph(){
		HashMap<String, Integer> units = get_units();
		Graph ret = new Graph();
		ret.set_num_vertices(units.size());

		//TODO figure out a better way of doing this. maybe lambdas
		class add{
			add(String in, String out, double val){
				ret.add_edge(units.get(in), units.get(out), val);
				ret.add_edge(units.get(out), units.get(in), 1 / val);
			}
		}

		new add("m", "m", 1.0);
		new add("km", "m", 1000.0);
		new add("au", "m", 149597870700.0);
		new add("m", "cm", 100.0);
		new add("in", "cm", 2.54);
		new add("ft", "in", 12.0);
		new add("mi", "ft", 5280.0);

		new add("g", "g", 1.0);
		new add("kg", "g", 1000.0);
		new add("lb", "g", 453.59237);

		new add("s", "s", 1.0);
		new add("min", "s", 60.0);
		new add("hr", "min", 60.0);

		new add("smoot", "in", 67.0);//inexact

		return ret;
	}

	public static void main(String[] args) throws IOException {
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

		//input format:
		/*
		value : double
		input units : space-delimited units
		output units : space-delimited units
		*/
		//ex. 17.29 meters per second-squared to kilometers per day-hour
		/*
		17.29
		m /s /s
		km /day /hr
		*/
		double val = Double.parseDouble(stdin.readLine());
		String[] input_units = stdin.readLine().split(" ");
		String[] output_units = stdin.readLine().split(" ");

		Useev main = new Useev();

		HashMap<String, Integer> units = main.get_units();
		Graph graph = main.get_graph();

		//TODO add support for aggregate units
		//TODO add support for more than one input/output unit
		int in_unit = units.get(input_units[0]);
		int out_unit = units.get(output_units[0]);

		HashSet<Integer> base_units = new HashSet<>();
		base_units.add(units.get("m"));
		base_units.add(units.get("g"));
		base_units.add(units.get("s"));

		for (int it: base_units){
			HashSet<Integer> base = new HashSet<>();
			base.add(it);

			double in = graph.dfs(in_unit, base);
			double out = graph.dfs(out_unit, base);

			if (in != 0.0 && out != 0.0){
				System.out.print("dfs in: " + in + "\n");
				System.out.print("dfs out: " + out + "\n");
				System.out.print("best ans: " + in / out + "\n");
				return;
			}
		}

		System.out.print("wip: unsupported for now: {" + input_units[0] + "} {" + output_units[0] + "}\n");
	}
}
