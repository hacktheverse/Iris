
package statistics_distributions;

import statistics_analysis.DataSet;

/**
 *
 * @author alyacarina
 */
public class Binomial extends DiscreteDistribution {
    
    // This is the Binomial Distribution class
    
    public Binomial(int n, double p){
        super("Binomial", new double[]{n, p}, n);
        validate();
    }
    
    @Override
    public double getMean() {
        return getParameter(0)*getParameter(1);
    }

    @Override
    public double getVariance() {
        return getMean()*(1-getParameter(1));
    }
    
    @Override
    public double f_implementation(int x) {
        int n = (int) getParameter(0);
        double p = getParameter(1);
        return choose(n, x)*Math.pow(p, x)*Math.pow(1-p, n-x);
    }

    @Override
    protected double est_param_impl(int i, DataSet data) {
        if(i==0){
            return data.getTotalFrequency();
        } else {
            return data.getMean()/data.getTotalFrequency();
        }
    }

    @Override
    protected final void validate() throws IllegalArgumentException {
        if(getParameter(1)<0 || getParameter(1)>1 || getParameter(0)<0){
            throwBadArgs();
        }
    }
    
}
