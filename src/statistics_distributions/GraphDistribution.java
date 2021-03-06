
package statistics_distributions;

import graphs.DistributionManager;
import graphs.DistributionNode;
import graphs.MemoryNode;
import java.util.ArrayList;
import statistics_analysis.DataSet;

/**
 *
 * @author alyacarina
 */

/** <!-- NOTE TO DEV -->
 * Need to implement correctional features.
 *  A) Iris needs to be able to adjust her base graph when she is wrong.
 * i.e. LEARN from mistakes. Figure out a system to allow such changes,
 * that is not purely statistical in nature. 
 * <!-- IDEA --> input expected vs. input produced-
 * Iris can look at what she knows (or doesn't) to figure out if she should have taken a different path
 * to the response, or if she just needs to add this response to the end of this path (train of thought).
 *  B) Graphs need to reshuffle on sleep to be rooted at the most popular
 * node, or rather the node linked to the most 'popular' nodes- the most 
 * interconnected node, for faster traversal. Typically one & same.
**/

public class GraphDistribution extends Distribution {

    private final DistributionManager graph;
    private final static String BAD_METHOD = "Not computable.";
    private int total_frequency;
    private DistributionNode current_scope; // node of graph we're analyzing from
    private double track;
    private double last_datum;
    
    public GraphDistribution() {
        super("Graph Distribution", new double[]{});
        graph = new DistributionManager();
        current_scope = (DistributionNode) graph.root;
        total_frequency = graph.getTotalFrequency();
        track = -1;
        validate();
    }

    // Methods
    
    // Search for first node in graph with a high-enough p-value to not reject hypothesis
    private DistributionNode seekFirst(DistributionNode node, 
                           ArrayList<Integer> visited) {
        visited.add(node.getId());
        if(node.getDistribution() != null){ //if not root
            double pv = node.getDistribution().f(last_datum)
                    * node.getNumberOfCalls()/total_frequency;
            if(track*pv>0.05){
                node.addConfirmedDatum(last_datum);
                track*=pv;
                return node;
            }
        }
        for(MemoryNode d: node.getNeighbors()){
            if(visited.contains(d.getId())) continue;
            DistributionNode x = seekFirst((DistributionNode) d, visited);
            if(x!=graph.root){
                return x;
            }
        }
        return (DistributionNode) graph.root; // nothing was found
    }
    
    //Look for closest appropriate distribution in the current scope
    private DistributionNode huntForDistribution(double x){
        DistributionNode previous_scope = current_scope;
        if(track == -1){
            track = 1; // start tracking
            current_scope = (DistributionNode)graph.root;
        } 
        
        last_datum = x;
        
        current_scope = seekFirst(current_scope, new ArrayList());
        
        // link it to previous, if necessary
        //   note that checking for existing neighbors 
        //   with the same ID is done in the memorynode class
        if(previous_scope != current_scope){
            current_scope.addNeighbor(previous_scope);
        }
        
        return current_scope;
    }
    
    // Overrides
    
    @Override
    public double getMean() {
        // should never be called
        throw new UnsupportedOperationException(BAD_METHOD);
    }

    @Override
    public double getVariance() {
        // should never be called
        throw new UnsupportedOperationException(BAD_METHOD);
    }

    @Override
    public double f(double x) {
        track = -1;
        DistributionNode target = huntForDistribution(x);
        
        // graph has never met this kind of datum before
        if(target == graph.root) {
            return 0;
        }
        
        return track;
    }

    @Override
    protected double est_param_impl(int i, DataSet data) {
        // should never be called
        throw new UnsupportedOperationException(BAD_METHOD);
    }

    @Override
    protected final void validate() throws IllegalArgumentException {
        // nothing to do, no args
    }
    
    // hunt for, and update, DistributionNodes that need updating
    private void goodNight(DistributionNode current, ArrayList<Integer> visited){
        visited.add(current.getId());
        
        if(current.shouldUpdate()) {
            current.update(graph);
        }
        
        for(MemoryNode neighbor: current.getNeighbors()){
            if(!visited.contains(neighbor.getId())){
                goodNight((DistributionNode) neighbor, visited);
            }
        }
        
    }
    
    // Sends Iris to sleep- reorganizes data, reevaluates distributions
    public void goodNight(){
        goodNight((DistributionNode) graph.root, new ArrayList());
        total_frequency = graph.getTotalFrequency();
        graph.sleep();
    }
    
    public DistributionManager getGraph(){
        return graph;
    }

    public void addDistributionNode(Distribution distant) {
        graph.addDistributionNode(distant);
    }

    public void addDistributionNode(Distribution distant, int add_to) {
        graph.addDistributionNode(distant, add_to);
    }
    
    public DistributionNode getNewest(){
        return (DistributionNode) graph.getNewest();
    }
    
}
