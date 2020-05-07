package SE250A4;

import javax.swing.*;
import java.util.*;


public class Graph {

    private final ArrayList<LinkedList<Integer>> _digraphOut;
    private final ArrayList<LinkedList<Integer>> _digraphIn;
    private final int _nodeCount;
    private final int _edgeCount;

    public Graph(int size, List<String> links) {
        _digraphOut = new ArrayList<>(size);
        _digraphIn = new ArrayList<>(size);
        _nodeCount = size;
        _edgeCount = links.size();
        for (int i = 0; i < _nodeCount; i++) {
            _digraphIn.add(new LinkedList<>());
            _digraphOut.add(new LinkedList<>());
        }
        links.forEach(el -> {
            var nodes = el.split(" ");
            _digraphOut.get(Integer.parseInt(nodes[0])).add(Integer.valueOf(nodes[1]));
            _digraphIn.get(Integer.parseInt(nodes[1])).add(Integer.valueOf(nodes[0]));
        });
    }

    public List<Integer> sameDegree() {
        List<Integer> a = new ArrayList<>();
        for (int i = 0; i < _nodeCount; i++) {
            if (_digraphIn.get(i).size() == _digraphOut.get(i).size()) {
                a.add(i);
            }
        }
        return a;
    }

    public List<Double> averageDegree() {
        return new ArrayList<>(2) {
            {
                add((double) _edgeCount / _nodeCount);
                add((double) _edgeCount / _nodeCount);
            }
        };
    }

    public String getCycles() {
        CycleDetector c = new CycleDetector();
        return c.findCycle();
    }

    public String getTopological() {
        TopologicalOrderFinder t = new TopologicalOrderFinder();
        return t.sortedTopological().toString();
    }

    public class CycleDetector {
        private final Stack<Integer> _active = new Stack<>();
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
                    new CycleCounter().countCycles();
                    return "Cycle: " + result + "\r\n";
                }
            }
            return "Topological Order:" + getTopological();
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

    public class TopologicalOrderFinder {
        //Here we store the in degree of all nodes
        private final Map<Integer, Integer> _inDegreeQuantity = new HashMap<>(_nodeCount);
        //Here we store all 'candidates' - nodes which have in degree 0. The priority queue ensures we always remove
        // the smallest element first.
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

    public class CycleCounter {

        public class StrongComponents {
            ArrayList<LinkedList<Integer>> _workingGraph;
            Stack<Integer> _dfsStack = new Stack<>();
            Set<Integer> _visited = new HashSet<>();


        }

        public class CycleFinder {

        }

        Stack<Integer> _dfsStack = new Stack<>();
        Set<Integer> _visited = new HashSet<>();

        private void getStrongComponents() {

        }

        public void countCycles() {

            for (int i = 0; i < _nodeCount; i++) {
                if (!_visited.contains(i)) {
                    populateStackDfs(i);
                }
            }
            _visited.clear();
            while (!_dfsStack.isEmpty()) {
                int i = _dfsStack.pop();
                if (!_visited.contains(i)) {
                    processStackDfs(i);
                    System.out.println();
                }
            }
        }

        private void populateStackDfs(int node) {
            _visited.add(node);
            for (int currentNode : _digraphOut.get(node)) {
                if (!_visited.contains(currentNode)) {
                    populateStackDfs(currentNode);
                }
            }
            _dfsStack.add(node);
        }

        private void processStackDfs(int node) {
            _visited.add(node);
            System.out.print(node + " ");

            for (int currentNode : _digraphIn.get(node)) {
                if (!_visited.contains(currentNode)) {
                    processStackDfs(currentNode);
                }
            }
        }
    }

}