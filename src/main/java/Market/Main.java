package Market;

import org.moeaframework.algorithm.NSGAIII;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.util.TypedProperties;

public class Main {
    public static void main(String[] args) {
        MarketCompetitionProblem problem = new MarketCompetitionProblem();

        TypedProperties properties = new TypedProperties();
        properties.setInt("divisionsOuter", 12);
        properties.setInt("divisionsInner", 0);

        NSGAIII algorithm = (NSGAIII) AlgorithmFactory.getInstance().getAlgorithm(
                "NSGAIII", properties, problem
        );

        algorithm.step();
        NondominatedPopulation population = algorithm.getResult();

        Solution bestSolution = null;
        double bestScore = 0;

        double weightMarketShare = 0.1;
        double weightProfit = 0.1;

        for (Solution solution : population) {
            double marketShare = -solution.getObjective(0);
            double profit = -solution.getObjective(1);
            double score = weightMarketShare * marketShare + weightProfit * profit;
            if (score > bestScore) {
                bestScore = score;
                bestSolution = solution;
            }
        }

        if (bestSolution != null) {
            System.out.println("Company:");
            for (int i = 0; i < problem.getNumberOfVariables(); i++) {
                System.out.print("Company " + (i + 1) + " strategies: " + EncodingUtils.getInt(bestSolution.getVariable(i)) + " ");
                System.out.println();
            }

            System.out.println("Market: " + -bestSolution.getObjective(0));
            System.out.println("Profit: " + (-bestSolution.getObjective(1)));
        }
    }
}