package SE250A4;

import java.util.*;

import static java.lang.System.lineSeparator;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A graph object, which contains methods that reveal various information about the graph. The graph is stored as two
 * adjacency lists. One with all in-going edges
 */
public class Graph {

    private final ArrayList<LinkedList<Integer>> _digraphOut;
    private final ArrayList<LinkedList<Integer>> _digraphIn;
    private final int _nodeCount;
    private final int _edgeCount;

    /**
     * Constructs the adjacency lists of the graph.
     *
     * @param size  the number of nodes in the graph
     * @param links list of edges [u v] where an edge goes from u to v.
     */
    public Graph(int size, List<String> links) {
        _digraphOut = new ArrayList<>(size);
        _digraphIn = new ArrayList<>(size);
        _nodeCount = size;
        _edgeCount = links.size();
        //add the linked lists to the adjacency lists.
        for (int i = 0; i < _nodeCount; i++) {
            _digraphIn.add(new LinkedList<>());
            _digraphOut.add(new LinkedList<>());
        }
        //fill the linked lists with their appropriate values.
        links.forEach(el -> {
            var nodes = el.split(" ");
            _digraphOut.get(Integer.parseInt(nodes[0])).add(Integer.parseInt(nodes[1]));
            _digraphIn.get(Integer.parseInt(nodes[1])).add(Integer.parseInt(nodes[0]));
        });
    }

    /**
     * Get the nodes with the same in and out degree.
     *
     * @return nodes where degree in = degree out.
     */
    public List<Integer> sameDegree() {
        List<Integer> a = new ArrayList<>();
        //if the list size of the in-degree list is the same as the out, add one to the counter
        for (int i = 0; i < _nodeCount; i++) {
            if (_digraphIn.get(i).size() == _digraphOut.get(i).size()) {
                a.add(i);
            }
        }
        return a;
    }

    /**
     * Gets the average degree of all nodes in the graph
     *
     * @return [average in , average out]
     */
    public List<Double> averageDegree() {
        return new ArrayList<>(2) {
            {
                add((double) _edgeCount / _nodeCount);
                add((double) _edgeCount / _nodeCount);
            }
        };
    }

    /**
     * Creates a CycleDetector object. If a cycle is found, then it will output the cycle that was found. It will then
     * create a CycleCounter object to count the number of cycles and say if there is at most 3.
     * <p>
     * If no cycle was found, then it will create a TopologicalOrderFinder object, which will then find the sorted
     * topological order of this directed acyclic graph.
     *
     * @return A string detailing information about cycles, or the sorted topological order if there are no cycles.
     */
    public String getCycles() {
        CycleDetector c = new CycleDetector();
        return c.findCycle();
    }

    /**
     * Gets the topological order of the graph. Only call this if the graph is known to be acyclic.
     *
     * @return the topological order of the digraph.
     */
    public String getTopological() {
        return new TopologicalOrderFinder().sortedTopological().toString();
    }

    /**
     * Detects cycle in the graph.
     */
    public class CycleDetector {
        //A stack is used here to keep track of the active nodes in the DFS search tree. We only ever need to add to the
        //top of the stack, or remove the top element, so the stack is ideal.
        private final Stack<Integer> _active = new Stack<>();
        //Explored is a set containing all the explored elements. This data must be unique, so using a set is ideal. We
        //also need to check if an element exists in the set, which is a constant time operation as well.
        private final Set<Integer> _explored = new HashSet<>(_nodeCount);

        /**
         * Apply DFS-like algorithm to each node of the graph. If the DFS finds any node that is currently 'active' (ie
         * currently in the stack), then there must be a cycle in the graph.
         *
         * @return if the graph contains cycles
         */
        public String findCycle() {
            for (int i = 0; i < _nodeCount; i++) {
                String result = cycleRecursive(i);
                if (!result.equals("false")) {

                    return "Cycle:" + lineSeparator()
                            + result + lineSeparator()
                            + (new CycleCounter().startCycleFinder() > 3 ? "No" : "Yes");
                }
            }
            return "Topological Order:" + lineSeparator() + getTopological() + lineSeparator() + "Yes";
        }

        /**
         * Traverse along edges, either until all paths are traversed, or a cycle is found.
         *
         * @param v the node to start at
         * @return String "false" if no cycle was found, or string detailing the first cycle that was found.
         */
        private String cycleRecursive(int v) {
            //first check if the active stack contains this node. If it does, then there's a cycle.
            if (_active.contains(v)) {
                StringBuilder str = new StringBuilder();
                //keep adding the nodes in the cycle from the stack, until the node that caused the cycle is added
                while (_active.peek() != v) {
                    str.append(_active.pop()).append(" ,");
                }
                str.append(_active.pop());
                return str.reverse().toString();
            }

            //if the node is explored (but not active), then there is no cycle here.
            if (_explored.contains(v)) return "false";

            //add this node to active and explored.
            _active.add(v);
            _explored.add(v);

            //for every outgoing edge
            for (int currentNode : _digraphOut.get(_active.peek())) {
                String result = cycleRecursive(currentNode);
                //if the result isn't false, then its a string with the cycle, so return this through recursion stack.
                if (!result.equals("false")) return result;
            }
            //if the for loop completes, it means no cycle was detected from that point, so remove it from active.
            _active.pop();
            return "false";
        }
    }

    /**
     * Find the sorted topological order of this graph.
     */
    public class TopologicalOrderFinder {
        //Here we store the size of the in-degree of all nodes. I use a map as this allows constant access time of
        // finding the in-degree of the given node.
        private final Map<Integer, Integer> _inDegreeQuantity = new HashMap<>(_nodeCount);
        //Here we store all 'candidates' - nodes which have in degree 0. The priority queue ensures we always remove
        // the smallest element first, ensuring that it is sorted.
        private final PriorityQueue<Integer> _candidates = new PriorityQueue<>();

        /**
         * Construct the TopologicalOrderFinder. We look at the size of the in-degree of every node, and store it in a
         * map for constant access time. If any have no in-degree, then we directly add it to the priority queue, since
         * those will be the start of the topological order.
         */
        public TopologicalOrderFinder() {
            for (int i = 0; i < _nodeCount; i++) {
                int size = _digraphIn.get(i).size();
                _inDegreeQuantity.put(i, size);
                if (size == 0) _candidates.add(i);
            }
        }

        /**
         * Find the topological order. We iterate until the ordered list contains all nodes. We look at every outgoing
         * edge from the node removed from the priority queue, and then reduce the in-degree of those connected edges by
         * 1. If a node ever gets reduced to in-degree 0, then it is added to the priority queue. The item removed from
         * the priority queue will be removed in O(log(n)) time, and is always the smallest number.
         *
         * @return the sorted topological order
         */
        private List<Integer> sortedTopological() {
            final List<Integer> _ordered = new ArrayList<>();
            while (_ordered.size() != _nodeCount) {
                int iterations = _candidates.size();
                for (int i = 0; i < iterations; i++) {
                    Integer node = _candidates.remove(); //get the lowest value node
                    _ordered.add(node); //add it to the ordered list
                    LinkedList<Integer> outLinks = _digraphOut.get(node); //get list of connections (outgoing edges)
                    outLinks.forEach(this::reduceEdges); //apply reduceEdge to each outgoing edge.
                }
            }
            return new ArrayList<>(_ordered);
        }

        /**
         * Reduces the edge's in-degree by 1 in the map. If this causes it to drop to in-degree
         *
         * @param link the edge to reduce
         */
        private void reduceEdges(Integer link) {
            _inDegreeQuantity.put(link, _inDegreeQuantity.get(link) - 1);
            if (_inDegreeQuantity.get(link).equals(0)) {
                _candidates.add(link);
            }
        }
    }

    /**
     * Counts the number of cycles in the graph. This works by applying a DFS style search to the graph, looping through
     * all possible start points. If node 'd' is the start point, then one iteration will show all cycles that start at
     * d and end at d. This will result in lots of duplicate cycles. If a cycle has length n, this will result in n
     * copies of the same cycle (since the cycle will be detected for each node in it). We can therefore take the sum of
     * the amount of cycles found of length n, divided by n, to find the total cycles.
     */
    public class CycleCounter {
        //This stack contains all nodes active in the recursion stack. We only want to add to the top of the stack,
        // or remove from the top, so we use a stack.
        private final Stack<Integer> _processing = new Stack<>();
        //An arraylist containing the size of each cycle found.
        private final ArrayList<Integer> _cycles = new ArrayList<>();

        /**
         * The start point of the algorithm. We call the dfsCycleFinder method on every single node.
         *
         * @return the number of cycles in the graph.
         */
        public int startCycleFinder() {
            //We iterate over every every node, and call dfsCycleFinder on it.
            for (int i = 0; i < _nodeCount; i++)
                dfsCycleFinder(i, i);

            //once we have all the lengths of the cycles, we create a frequency map detailing the number of times a
            // cycle of length n appeared (as a map -> key = the length, val = number of cycles of that length). We then
            // divide the values by the length, and sum it.
            return _cycles.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .mapToInt(e -> (int) (e.getValue() / e.getKey()))
                    .sum();
        }

        /**
         * The initial node is the very original node, called by {@link CycleCounter#startCycleFinder()}. We only count
         * a cycle if our algorithm discovers the initial nodes again. Other cycles can be found, but we ignore them at
         * this moment. They will be discovered once an initial node within the cycle is the initialNode.
         *
         * @param initialNode the original node at the start of the recursion stack
         * @param n           the current node we are at
         */
        public void dfsCycleFinder(int initialNode, int n) {
            // we add n to the processing stack. This helps us not enter infinite recursion loops by knowing when we
            // found a non-initialNode cycle, so we can then backtrace.
            _processing.add(n);
            // we iterate over the children of node n
            _digraphOut.get(n).forEach(child -> {
                if (child == initialNode) {
                    //if the child is the initialNode, then we found an interesting cycle. We add its size to the list.
                    _cycles.add(_processing.size());
                } else if (!_processing.contains(child)) {
                    //otherwise if the child isn't in the recursion stack currently, we call this method on the child.
                    dfsCycleFinder(initialNode, child);
                }
            });
            //If the recursive condition is not met, then we backtrace, removing the most recently added element from
            //the stack. Once a path is complete, then finished nodes are popped of the stack.
            _processing.pop();
        }
    }

}