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
        double bestProfit = Double.NEGATIVE_INFINITY;
        int bestCompanyIndex = -1;

        for (Solution solution : population) {
            double[] profits = problem.getProfits(solution);
            double totalProfit = 0;
            for (int i = 0; i < profits.length; i++) {
                for (double profit : profits) {
                    totalProfit += profit;
                }

                if (profits[i] > bestProfit) {
                    bestProfit = profits[i];
                    bestSolution = solution;
                    bestCompanyIndex = i;
                }
            }
        }

        if (bestSolution != null) {
            System.out.println("Company strategies and profits:");
            double[] profits = problem.getProfits(bestSolution);
            for (int i = 0; i < problem.getNumberOfVariables(); i++) {
                System.out.println("Company " + (i + 1) + " strategy: " + EncodingUtils.getInt(bestSolution.getVariable(i)) + ", Profit: " + profits[i]);
            }

            System.out.println("Company with the highest profit:");
            System.out.println("Company " + (bestCompanyIndex + 1) + " strategy: " + EncodingUtils.getInt(bestSolution.getVariable(bestCompanyIndex)));
            System.out.println("Market Share: " + -bestSolution.getObjective(0));
            System.out.println("Profit: " + bestProfit);
        }
    }
}