Market Competition Problem



Overview
This project defines a Market Competition Problem modeled as a multi-objective optimization problem using the MOEA Framework. The problem simulates market competition between four companies, each applying different marketing strategies to maximize their market share and profits.

Objectives
The main objectives for each company are:

Maximize Market Share: Increase market dominance by selecting effective marketing strategies.
Maximize Profitability: Balance marketing costs with profits from market share.
Since this is a multi-objective optimization problem, both market share and profitability are minimized (represented as negative values).

Problem Definition
Constants

Number_Of_Company: The number of companies in the simulation (4 companies).

Campaign: The effect of running a marketing campaign.

No_Campaign: Penalty for not running a marketing campaign.

Discount: The effect of offering a discount.

No_Discount: Penalty for not offering a discount.

Initial_Market_Share: Initial market share for each company (set to 25%).

Base_Marketing_Cost: Cost incurred for increasing market share.

Profit_Percentage_Per_Market_Share: The profit obtained per unit percentage of market share.

Decision Variables

Each company has 4 potential strategies represented by integer values:

0 - Campaign + Discount

1 - No Campaign + Discount

2 - Discount Only

3 - No Discount

Objectives

The problem has 2 objectives per company:

Maximizing Market Share (minimization of the negative value).

Maximizing Profitability (minimization of the negative value).

Market Share Adjustment

Market share is adjusted based on the strategy chosen by the company. Additionally, if the total market share of all companies exceeds 100%, adjustments are made to bring the total back within limits.

Profit Calculation

Profit is determined based on the company’s market share and the cost of its chosen strategy. A strategy involving a campaign or discount will reduce profitability due to higher costs.

Code Explanation
