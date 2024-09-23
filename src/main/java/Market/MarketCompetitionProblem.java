package Market;

import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;

import java.util.Arrays;

public class MarketCompetitionProblem extends AbstractProblem {


    // number of companies
    public static final int Number_Of_Company = 4;

    // Increase market share, where do campaigns
    public static final double Campaign = 0.1;

    // market share drop, where do campaigns
    public static final int No_Campaign = -5;

    // % market share increase where to discount
    public static final double Discount = 0.15;

    // % market share falls where discounts are made
    public static final double No_Discount = 0.05;


    // Campaign strategy
    public static final int CAMPAIGN_DISCOUNT = 0;
    public static final int NO_CAMPAIGN_DISCOUNT = 1;
    public static final int NO_CAMPAIGN_NO_DISCOUNT = 2;
    public static final int CAMPAIGN_NO_DISCOUNT = 3;





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
        }

            // Cập nhật thị phần và lợi nhuận cho mỗi công ty sau mỗi lượt
            for (int i = 0; i < Number_Of_Company; i++) {
                marketShares[i] = calculateMarketShare(strategies[i], marketShares, i);
            }

            for (int i = 0; i < Number_Of_Company; i++) {
                profits[i] = calculateProfit(marketShares[i], strategies[i]);
            }

        // Set objectives (negative because it's a minimization problem)
        for (int i = 0; i < Number_Of_Company; i++) {
            profits[i] = calculateProfit(marketShares[i],strategies[i]);
            // Objective 1: maximize market share (convert to negative for minimization)
            solution.setObjective(i, -marketShares[i]);

            // Objective 2: maximize profit (convert to negative for minimization)
            solution.setObjective(i + Number_Of_Company, -profits[i]);
        }
    }

    private double calculateMarketShare(int strategy, double[] currentMarketShares, int companyIndex) {
        double adjustedMarketShare = currentMarketShares[companyIndex];

        // Adjust market share based on strategy
        switch (strategy) {
            case CAMPAIGN_DISCOUNT: // Campaign and discount
                adjustedMarketShare += Campaign * adjustedMarketShare + adjustedMarketShare * Discount;
                break;

            case NO_CAMPAIGN_DISCOUNT: // No Campaign and discount
                adjustedMarketShare += No_Campaign + adjustedMarketShare * Discount;
                break;

            case NO_CAMPAIGN_NO_DISCOUNT: // Discount only
                adjustedMarketShare += adjustedMarketShare + No_Campaign;
                break;

            case CAMPAIGN_NO_DISCOUNT: // No Discount
                adjustedMarketShare += -No_Discount * adjustedMarketShare + adjustedMarketShare * Campaign;
                break;

            default:
                throw new IllegalArgumentException("Invalid strategy: " + strategy);
        }


        // Ensure market share doesn't exceed 100%
        double totalMarketShare = Arrays.stream(currentMarketShares).sum();
        double maxMarketShare = 100.0;

        // Calculate the new total market share if adjustedMarketShare is used

        // If the new total market share exceeds 100%, adjust the individual market shares
        // If the new total market share exceeds 100%, adjust the individual market shares
        if (totalMarketShare > maxMarketShare) {
            double excess = totalMarketShare - maxMarketShare;

            // Distribute the excess proportionally to other companies
            for (int i = 0; i < Number_Of_Company; i++) {
                if (i != companyIndex) {
                    double reduction = (currentMarketShares[i] / (totalMarketShare - currentMarketShares[companyIndex])) * excess;
                    currentMarketShares[i] = Math.max(0, currentMarketShares[i] - reduction);
                }
            }

            // Adjust the current company's market share accordingly
            currentMarketShares[companyIndex] = maxMarketShare -
                    Arrays.stream(currentMarketShares).sum() + currentMarketShares[companyIndex];
        }


        return adjustedMarketShare;
    }


    private double calculateProfit(double marketShare, int strategy) {
        // Adjust profit based on marketing costs, which increase with market share
        double baseProfit = marketShare * Profit_Percentage_Per_Market_Share;

        // Use switch-case to handle strategy
        switch (strategy) {
            case CAMPAIGN_DISCOUNT: // Campaign and discount
                baseProfit -= Discount * marketShare * Base_Marketing_Cost;
                break;

            case NO_CAMPAIGN_DISCOUNT: // No Campaign and discount
                // No adjustments needed
                break;

            case NO_CAMPAIGN_NO_DISCOUNT: // Discount only
                baseProfit -= Discount * marketShare * Base_Marketing_Cost;
                break;

            case CAMPAIGN_NO_DISCOUNT: // No Discount
                baseProfit -= marketShare * Base_Marketing_Cost;
                break;

            default:
                throw new IllegalArgumentException("Invalid strategy: " + strategy);
        }

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
