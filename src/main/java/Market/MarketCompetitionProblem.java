package Market;

import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;

import java.util.Arrays;

public class MarketCompetitionProblem extends AbstractProblem {

    public static final int Number_Of_Company = 4;

    public static final double Campaign = 0.1;
    public static final int No_Campaign = -5;
    public static final double Discount = 0.05;
    public static final double No_Discount = 0.1;

    // Total market size
    public static final int Market = 100;

    // Initial market share for each company (25% each at the start)
    public static final double Initial_Market_Share = 25.0;

    // Base marketing cost per percentage of market share
    public static final double Base_Marketing_Cost = 0.15;

    // Profit per unit of market share
    public static final int Profit_Percentage_Per_Market_Share = 10;


    public MarketCompetitionProblem() {
        super(Number_Of_Company, 2); // 4 decision variables (strategies for 4 companies), 2 objectives (maximize market share, balance profitability)
    }

    @Override
    public void evaluate(Solution solution) {
        // Get the strategies for each company
        int[] strategies = new int[Number_Of_Company];
        for (int i = 0; i < Number_Of_Company; i++) {
            strategies[i] = EncodingUtils.getInt(solution.getVariable(i));
        }

        // Calculate the market shares and profits for each company
        double[] marketShares = new double[Number_Of_Company];
        double[] profits = new double[Number_Of_Company];

        // Initialize market shares to 25% for each company
        Arrays.fill(marketShares, Initial_Market_Share);

        // Adjust market share based on strategies and competition
        for (int i = 0; i < Number_Of_Company; i++) {
            marketShares[i] = calculateMarketShare(strategies[i], marketShares, i);
            profits[i] = calculateProfit(marketShares[i],strategies[i]);
        }

        // Set objectives (negative because it's a minimization problem)
        for (int i = 0; i < Number_Of_Company; i++) {
            // Objective 1: maximize market share (convert to negative for minimization)
            solution.setObjective(i, -marketShares[i]);

            // Objective 2: maximize profit (convert to negative for minimization)
            solution.setObjective(i + Number_Of_Company, -profits[i]);
        }
    }

    private double calculateMarketShare(int strategy, double[] currentMarketShares, int companyIndex) {
        double adjustedMarketShare = currentMarketShares[companyIndex];

        // Adjust market share based on strategy
        if (strategy == 0) { // Campaign and discount
            adjustedMarketShare += Campaign * adjustedMarketShare + adjustedMarketShare * Discount + adjustedMarketShare;
        } else if (strategy == 1) { // No Campaign and discount
            adjustedMarketShare += No_Campaign + adjustedMarketShare * Discount;
        } else if (strategy == 2) { // Discount only
            adjustedMarketShare += adjustedMarketShare + No_Campaign;
        } else if (strategy == 3) { // No Discount
            adjustedMarketShare += -No_Discount * adjustedMarketShare + adjustedMarketShare * Campaign + adjustedMarketShare;
        }

        // Ensure market share doesn't exceed 100%
        double totalMarketShare = Arrays.stream(currentMarketShares).sum();
        double maxMarketShare = 100.0;

        // Calculate the new total market share if adjustedMarketShare is used
        double newTotalMarketShare = totalMarketShare - currentMarketShares[companyIndex] + adjustedMarketShare;

        // If the new total market share exceeds 100%, adjust the individual market shares
        if (newTotalMarketShare > maxMarketShare) {
            double excess = newTotalMarketShare - maxMarketShare;

            // Distribute the excess proportionally to other companies
            for (int i = 0; i < Number_Of_Company; i++) {
                if (i != companyIndex) {
                    currentMarketShares[i] -= (currentMarketShares[i] / totalMarketShare) * excess;
                }
            }

            // Recalculate adjusted market share to fit within the bounds
            adjustedMarketShare = currentMarketShares[companyIndex] + adjustedMarketShare;
        }

        return adjustedMarketShare;
    }


    private double calculateProfit(double marketShare, int strategy) {
        // Adjust profit based on marketing costs, which increase with market share
        double marketingCost = marketShare * Base_Marketing_Cost;
        double baseProfit = marketShare * Profit_Percentage_Per_Market_Share - marketingCost;

        // Modify baseProfit based on strategy if needed
        // For example, you might want to apply different strategies that affect profit
        if (strategy == 0) { // Campaign and discount
            baseProfit += (Campaign + Discount) * marketShare;
        } else if (strategy == 1) { // No Campaign and discount
            baseProfit -= No_Campaign;
        } else if (strategy == 2) { // Discount only
            baseProfit += Discount * marketShare;
        } else if (strategy == 3) { // No Discount
            baseProfit -= No_Discount;
        }

        // Ensure profit doesn't go below zero (or apply any other constraints if needed)
        if (baseProfit < 0) {
            baseProfit = 0;
        }

        return baseProfit;
    }


    @Override
    public Solution newSolution() {
        Solution solution = new Solution(Number_Of_Company, Number_Of_Company * 2);

        // Set decision variables (strategies) between 0 and 3 for each company
        for (int i = 0; i < Number_Of_Company; i++) {
            solution.setVariable(i, EncodingUtils.newInt(0, 3));
        }

        return solution;
    }
}
