package basicgraphalgorithms;
/**
 * @file GraphDemo.java
 * @author Duncan, Jordan Morris
 * @date 10-21-2018
 * Description: A test bed for the implementations of various
 * weighted digraph algorithms
 */

import com.sun.javafx.geom.Edge;
import jdk.nashorn.internal.runtime.Undefined;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.Comparator;
import java.util.PriorityQueue;

public class GraphDemo {
    public static final Double INFINITY = Double.POSITIVE_INFINITY;

    public static void main(String[] args) throws GraphException {
        if (args.length != 1) {
            System.out.println("Usage: GraphDemo <filename>");
            System.exit(1);
        }
        City c1, c2;
        Scanner console;
        int menuReturnValue, i, j;
        Function<City, PrintStream> f = aCity -> System.out.printf("%-2d  %-30s%n", aCity.getKey(), aCity.getLabel().trim());
        Graph<City> g = readGraph(args[0]);
        Graph<City> gPrime;
        long s = g.size();
        menuReturnValue = -1;
        while (menuReturnValue != 0) {
            menuReturnValue = menu();
            switch (menuReturnValue) {
                case 1: //Transitive Closure Matrix of g'
                    //Generate the transitive closure matrix
                    gPrime = transpose(g);
                    System.out.println();
                    System.out.println("Transitive Closure Matrix for the Transpose of the Graph In " + args[0]);
                    System.out.println("=========================================================================================");
                    //Add code here to display the matrix with two-character spaces between columns
                    int[][] tMatrix = new int[(int) gPrime.size()][(int) gPrime.size()];
                    for (int k = 0; k < gPrime.size(); k++) {
                        City tmpCity1 = new City(k + 1);
                        for (int l = 0; l < gPrime.size(); l++) {
                            City tmpCity2 = new City(l + 1);
                            if (gPrime.isPath(tmpCity1, tmpCity2) || tmpCity1.compareTo(tmpCity2) == 0)
                                tMatrix[k][l] = 1;
                            else
                                tMatrix[k][l] = 0;
                        }
                    }
                    print2DArray(tMatrix);
                    //End add code here
                    System.out.println("=========================================================================================");
                    System.out.println();
                    System.out.println();
                    break;
                case 2: //number of edges
                    System.out.println();
                    System.out.println("The graph has " + g.countEdges() + ".");
                    System.out.println();
                    break;
                case 3://Shortest-path algorithm
                    console = new Scanner(System.in);
                    System.out.printf("Enter the source vertex: ");
                    int initial = console.nextInt();
                    System.out.printf("Enter the destination vertex: ");
                    int dest = console.nextInt();
                    if (g.isPath(new City(initial), new City(dest))) {
                        System.out.printf("Shortest route from %s to %s in G:%n", g.retrieveVertex(new City(initial)).getLabel().trim(), g.retrieveVertex(new City(dest)).getLabel().trim());
                        System.out.println("=========================================================================================");
                        //Add code here: A detail description of how the output should appear
                        //Add code here to print each leg of the trip from the source to the destination
                        //using the format below, where the columns are left-aligned and the distances
                        //are displayed to the nearest hundredths.
                        //For example:
                        //Baton Rouge            ->   Gonzales                  10.20 mi
                        //Gonzales               ->   Metaire                   32.00 mi
                        //Metaire                ->   New Orleans                7.25 mi
                        double[] dist = new double[(int) g.size()];
                        int[] pred = new int[(int) g.size()];
                        dijkstra(g, dist, pred, initial, dest);
                        //Begin Code
                        Stack<Integer> stack = new Stack<>();
                        stack.add(dest);
                        int curr = dest;

                        while (pred[curr - 1] != -1) {
                            curr = pred[curr - 1];
                            stack.add(curr);
                        }

                        while (!stack.isEmpty()) {
                            int temp = stack.pop();
                            City c = new City(temp);
                            if (!stack.isEmpty()) {
                                City topCity = new City(stack.peek());
                                System.out.printf("%-20s->%-20s%.02f%n", g.retrieveVertex(c).getLabel(), g.retrieveVertex(topCity).getLabel());
                            }
                        }
                        //End code
                        System.out.println("=========================================================================================");
                        System.out.printf("Total distance: %f miles.%n%n", dist[dest - 1]);
                    } else
                        System.out.printf("There is no path.%n%n");
                    break;
                case 4: //post-order depth-first-search traversal of g'
                    System.out.println();
                    System.out.println("PostOrder DFS Traversal For The Graph In " + args[0]);
                    System.out.println("==========================================================================");
                    //invoke the dfsTraverse function
                    // Output should be aligned in two-column format as illustrated below:
                    // 1     Charlottetown
                    // 4     Halifax
                    // 2     Edmonton
                    //Begin Code
                    gPrime = transpose(g);
                    gPrime.dfsTraverse(f);
                    //End Code
                    System.out.println("==========================================================================");
                    System.out.println();
                    System.out.println();
                    break;
                case 5: //breadth-first-search traversal
                    System.out.println();
                    System.out.println("BFS Traversal For The Graph In " + args[0]);
                    System.out.println("==========================================================================");
                    //invoke the bfsTraverse function
                    // Output should be aligned in two-column format as illustrated below:
                    // 1     Charlottetown
                    // 4     Halifax
                    // 2     Edmonton
                    //Begin Code
                    g.bfsTraverse(f);
                    //End Code
                    System.out.println("==========================================================================");
                    System.out.println();
                    System.out.println();
                    break;
                case 6: //Check whether g is connected
                    System.out.println();
                    if (isConnected(g))
                        System.out.println("The graph is connected.");
                    else
                        System.out.println("The graph is not connected.");
                    System.out.println();
                    break;
                case 7: //topoSort
                    System.out.println();
                    int[] top = new int[(int) g.size()];
                    if (topSortOutDeg(g, top)) {
                        System.out.println("Topological Sorting of The Graph In " + args[0]);
                        System.out.println("==========================================================================");
                        for (i = 1; i <= g.size(); i++) {
                            c1 = g.retrieveVertex(new City(top[i - 1]));
                            f.apply(c1);
                        }
                        System.out.println("==========================================================================");
                    } else
                        System.out.println("No topological ordering possible. The digraph in " + args[0] + " contains a directed cycle.");
                    System.out.printf("%n%n");
                    break;
                case 8: //kruskalMST;
                    int[] mst = new int[(int) g.size()];

                    int[] mstK = new int[(int) g.size()];
                    double totalWt = kruskalMST(g, mstK);
                    String cityNameA, cityNameB;
                    for (i = 1; i <= g.size(); i++) {
                        if (mstK[i - 1] < 1)
                            cityNameA = "NONE";
                        else
                            cityNameA = g.retrieveVertex(new City(mstK[i - 1])).getLabel().trim();
                        cityNameB = g.retrieveVertex(new City(i)).getLabel().trim();

                        System.out.printf("%d-%s parent[%d] <- %d (%s)%n", i, cityNameB, i, mstK[i - 1], cityNameA);
                    }
                    System.out.printf("The weight of the minimum spanning tree/forest is %.2f miles.%n%n", totalWt);
                    break;
                default:
                    ;
            } //end switch
        }//end while
    }//end main

    /**
     * This method reads a text file formatted as described in the project description.
     *
     * @param filename the name of the DIMACS formatted graph file.
     * @return an instance of a graph.
     */
    private static Graph<City> readGraph(String filename) {
        try {
            Graph<City> newGraph = new Graph();
            try (FileReader reader = new FileReader(filename)) {
                char temp;
                City c1, c2, aCity;
                String tmp;
                int k, m, v1, v2, j, size = 0, nEdges = 0;
                Integer key, v1Key, v2Key;
                Double weight;
                Scanner in = new Scanner(reader);
                while (in.hasNext()) {
                    tmp = in.next();
                    temp = tmp.charAt(0);
                    if (temp == 'p') {
                        size = in.nextInt();
                        nEdges = in.nextInt();
                    } else if (temp == 'c') {
                        in.nextLine();
                    } else if (temp == 'n') {
                        key = in.nextInt();
                        tmp = in.nextLine();
                        aCity = new City(key, tmp);
                        newGraph.insertVertex(aCity);
                    } else if (temp == 'e') {
                        v1Key = in.nextInt();
                        v2Key = in.nextInt();
                        weight = in.nextDouble();
                        c1 = new City(v1Key);
                        c2 = new City(v2Key);
                        newGraph.insertEdge(c1, c2, weight);
                    }
                }
            }
            return newGraph;
        } catch (IOException exception) {
            System.out.println("Error processing file: " + exception);
        }
        return null;
    }

    /**
     * Display the menu interface for the application.
     *
     * @return the menu option selected.
     */
    private static int menu() {
        Scanner console = new Scanner(System.in);
        String option;
        do {
            System.out.println("  BASIC WEIGHTED GRAPH APPLICATION   ");
            System.out.println("=======================================================");
            System.out.println("[1] Transitive Closure Matrix of G Transpose");
            System.out.println("[2] Number of Edges in G");
            System.out.println("[3] Single-source Shortest Path in G");
            System.out.println("[4] Postorder DFS Traversal of G Transpose");
            System.out.println("[5] BFS Traversal of G");
            System.out.println("[6] Check the Connectivity of G");
            System.out.println("[7] Topological Sort Labeling");
            System.out.println("[8] Kruskal's Minimum Spanning Tree in G");
            System.out.println("[0] Quit");
            System.out.println("=====================================");
            System.out.printf("Select an option: ");
            option = console.nextLine().trim();
            try {
                int choice = Integer.parseInt(option);
                if (choice < 0 || choice > 8) {
                    System.out.println("Invalid option...Try again");
                    System.out.println();
                } else
                    return choice;
            } catch (NumberFormatException e) {
                System.out.println("Invalid option...Try again");
            }
        } while (true);
    }

    /**
     * This method creates the transpose graph of the specified graph,
     * that is a digraph whose vertices are the same but whose edges are the
     * reverse of those of the specified graph; this method should preserve
     * the specified graph and not mutate it while creating its transpose.
     *
     * @param g a directed graph (without loops)
     * @return an instance of a graph representing the transpose of the
     * specified graph
     * @throws GraphException
     */
    private static Graph<City> transpose(Graph<City> g) throws GraphException {
        if (g.isEmpty())
            throw new GraphException("Non-existent vertex - transpose(Graph<City g).");
        Graph<City> newGraph = new Graph<>();
        double gWeight;
        int counter = 1;
        //Copies vertexes into newGraph
        for (int i = 1; i <= g.size(); i++)
            newGraph.insertVertex(g.retrieveVertex(new City(i)));
        while (counter <= g.size()) {
            City tmpCity = new City(counter);
            City tmpCityCycle = new City(1);
            for (int j = 1; j <= g.size(); j++) {
                if (g.isEdge(tmpCity, tmpCityCycle)) {
                    gWeight = g.retrieveEdge(tmpCity, tmpCityCycle);
                    newGraph.insertEdge(tmpCityCycle, tmpCity, gWeight);
                }
                tmpCityCycle = new City(j);
            }
            counter++;
        }
        return newGraph;
    }

    /**
     * This method computes the cost and path arrays using the
     * Dijkstra's single-source shortest path greedy algorithm.
     *
     * @param g    an instance of a weighted directed graph
     * @param dist an array containing shortest distances from a source vertex
     * @param pred an array containing predecessor vertices along the shortest path
     * @throws GraphException on call to retrieveEdge on non-existent edge
     */
    private static void dijkstra(Graph<City> g, double[] dist, int[] pred, int source, int destination) throws GraphException {
        //Implement this method
        /**
         * An auxiliary data structure to store the information
         * about a node for the Dijkstra SP algorithm
         *
         */
        class Node {
            public int id;
            public double key;

            public Node() {
            }

            public Node(int v, double k) {
                id = v;
                key = k;
            }
        }
       /* A Node comparator */
        Comparator<Node> cmp = (v1, v2) ->
        {
            double d = v1.key - v2.key;
            if (d < 0)
                return -1;
            if (d > 0)
                return 1;
            return v1.id - v2.id;
        };
        boolean[] visited = new boolean[dist.length];
        for (int i = 0; i < dist.length; i++) {
            dist[i] = INFINITY;
            pred[i] = -1;
            visited[i] = false;
        }
        dist[source - 1] = 0;
        visited[source - 1] = true;
        int x = source - 1;
        while (true) {
            for (int j = 0; j < dist.length; j++) {
                if (g.isEdge(g.retrieveVertex(new City(x + 1)), g.retrieveVertex(new City(j + 1))) && !visited[j]) {
                    double newDist = dist[x] + g.retrieveEdge(g.retrieveVertex(new City(x + 1)), g.retrieveVertex(new City(j + 1)));
                    if (newDist < dist[j]) {
                        dist[j] = newDist;
                        pred[j] = x;
                    }
                }
            }
            visited[x] = true;
            if (visited[destination - 1])
                return;
            double minDist = INFINITY;
            int minIndex = -1;
            for (int k = 0; k < dist.length; k++) {
                if (dist[k] < minDist && !visited[k]) {
                    minDist = dist[k];
                    minIndex = k;
                }
            }
            x = minIndex;
        }
    }

    /**
     * Determines whether or not the specified undirected graph is connected
     *
     * @return true if the specified graph is connected; otherwise, false
     * @throws GraphException
     */
    private static boolean isConnected(Graph<City> g) throws GraphException {
        if(g.isEmpty())
            throw new GraphException("Graph does not have any vertices - isConnected(Graph<City> g)");

        /* Sets all vertices to 'not visited' */
        boolean[] visited = new boolean[(int) g.size()];
        for(int i = 0; i < visited.length; i++)
            visited[i] = false;

        Stack<City> queue = new Stack<>();
        queue.push(new City(1));

        while(!queue.isEmpty()) {
            City tmpCity = queue.peek();
            queue.pop();
            if(!visited[tmpCity.getKey() - 1]) {
                visited[tmpCity.getKey()-1] = true;
                for(int j = 1; j <= g.countEdges(); j++) {
                    if(g.isEdge(tmpCity, new City(j))) {
                        if(!visited[(new City(j)).getKey() - 1])
                            queue.push(new City(j));
                    }
                }
            }
        }

        for(int k = 0; k < visited.length; k++) {
            if(!visited[k])
                return false;
        }
        return true;
    }

    /**
     * Generates a topological labeling of the specified digraph, in reverse order,
     * using the decrease-and-conquer algorithm that successively selects and
     * removes a vertex of out-degree 0 until all the vertices are selected.
     * The graph is explored in lexicographical order when adding a new vertex to the
     * topological ordering and the graph is not modified. Updates of the degrees
     * and vertices that are selected are tracked using auxiliary data structures.
     *
     * @param g           a digraph
     * @param linearOrder the topological ordering of the vertices
     * @return true if a topological ordering of the vertices of the specified digraph
     * exists; otherwise, false.
     */
    private static boolean topSortOutDeg(Graph<City> g, int linearOrder[]) throws GraphException {
        if(g.isEmpty())
            throw new GraphException("The graph does is (1) empty and/or (2) not connected - topSortOutDeg.");

        /* Creates an array that stores each vertex's out degree */
        int[] outDegree = new int[(int) g.size()];
        for(int i = 1; i <= g.size(); i++)
            outDegree[i-1] = (int) g.outDegree(new City(i));

        /* Used to keep track of current index when inserting keys into linearOrder array */
        int count = 0;

        /* Used to make sure that while loop is not infinite if no topo sort exist */
        int tracker = 1;

        while (tracker <= g.size()) {
            for (int j = 0; j < outDegree.length; j++) {
                if (outDegree[j] == 0) {
                    linearOrder[count] = j + 1;
                    count++;
                    /* Checks for adjacent vertices to the current vertex with no out degrees */
                    for (int k = 1; k <= g.size(); k++) {
                        if (g.isEdge(new City(k), new City(j + 1))) {
                            /* Decreases out degree of adjacent vertices ('deleting' edges) */
                            outDegree[k - 1]--;
                        }
                    }
                    /* Marked as -1 to signify that the vertex has been 'removed' */
                    outDegree[j] = -1;

                    /* Gets out of current loop */
                    j = outDegree.length + 1;

                    /* Resets tracker in order to stay inside while loop */
                    tracker = 1;
                }
                tracker++;
            }
        }

        /* If outDegree array does not contain all zeros, that means a topo sort does not exist */
        for(int p = 0; p < outDegree.length; p++) {
            if(outDegree[p] != -1)
                return false;
        }
        return true;
    }

    /**
     * Find the root vertex of the tree in which the specified is
     *
     * @param parent the parent implementation of a subtree of a graph
     * @param v      a vertex
     * @return the root of this subtree
     */
    private static int find(int[] parent, int v) {
        if(parent[v] != -1)
            v = find(parent, parent[v]);
        return v;
    }

    /**
     * This method generates a minimum spanning tree using Kruskal's
     * algorithm. If no such MST exists, then it generates a minimum spanning forest.
     *
     * @param g      a weighted directed graph
     * @param parent the parent implementation of the minimum spanning tree/forest
     * @return the weight of such a tree or forest.
     * @throws GraphException when this graph is empty
     *                        <pre>
     * {@code
     *  If a minimum spanning tree cannot be generated,
     *  the parent implementation of a minimum spanning tree or forest is
     *  determined. This implementation is based on the union-find strategy.
     *  }
     *  </pre>
     */
    private static double kruskalMST(Graph<City> g, int[] parent) throws GraphException {
        /**
         * An auxiliary data structure to store the information
         * about an edge for the MST algorithm
         *
         */
        class EdgeType {
            public double weight;
            public int source;
            public int destination;
            public boolean chosen;
        }
       /* An EdgeType comparator */
        Comparator<EdgeType> cmp = (t1, t2) ->
        {
            if (t1.weight < t2.weight)
                return -1;
            if (t1.weight > t2.weight)
                return 1;
            if (t1.source < t2.source)
                return -1;
            if (t1.source > t2.source)
                return 1;
            if (t1.destination < t2.destination)
                return -1;
            if (t1.destination > t2.destination)
                return 1;
            return 0;
        };
        /*Defining an instance of the PriorityQueue class that uses the comparator above
        and complete the implementation of the algorithm */
        PriorityQueue<EdgeType> pQueue = new PriorityQueue(cmp);
        boolean[] visited = new boolean[(int) g.size()];

        /* Inserts all edges into the priority queue */
        for (int i = 0; i < g.size(); i++) {
            for (int j = 0; j < g.size(); j++) {
                if (g.isEdge(new City(i + 1), new City(j + 1))) {
                    EdgeType edge = new EdgeType();
                    edge.source = i;
                    edge.destination = j;
                    edge.weight = g.retrieveEdge(new City(i + 1), new City(j + 1));
                    pQueue.add(edge);
                }
                if (g.isEdge(new City(j + 1), new City(i + 1))) {
                    EdgeType edge = new EdgeType();
                    edge.source = j;
                    edge.destination = i;
                    edge.weight = g.retrieveEdge(new City(j + 1), new City(i + 1));
                    pQueue.add(edge);
                }
            }
        }

        /* Initializes all indexes to -1 */
        for (int i = 0; i < parent.length; i++)
            parent[i] = -1;

        double totalWeight = 0;

        while (!pQueue.isEmpty()) {

            EdgeType e1 = pQueue.poll();
            if (!visited[e1.source]) {
                if (!visited[e1.destination]) {
                    totalWeight += e1.weight;
                    parent[e1.destination] = e1.source;
                    visited[e1.source] = true;
                    visited[e1.destination] = true;
                } else {
                    totalWeight += e1.weight;
                    parent[e1.source] = e1.destination;
                    visited[e1.source] = true;
                }
            }
            if (visited[e1.source]) {
                if (!visited[e1.destination]) {
                    totalWeight += e1.weight;
                    parent[e1.destination] = e1.source;
                    visited[e1.destination] = true;
                }
            }
            if (find(parent, e1.source) != find(parent, e1.destination)) {
                totalWeight += e1.weight;
                Stack<Integer> s = new Stack<>();
                int i = e1.destination;
                s.push(e1.source);

                while (i != -1) {
                    s.push(i);
                    i = parent[i];
                }
                while (s.size() > 1) {
                    int dest = s.pop();
                    int source = s.peek();
                    parent[dest] = source;
                }
            }
        }
        return totalWeight;
    }

    private static void print2DArray(int[][] array) {
        for (int i = 0; i <= array.length - 1; i++) {
            for (int j = 0; j <= array.length - 1; j++) {
                System.out.printf("%-2d", array[i][j]);
            }
            System.out.println();
        }
    }
}
