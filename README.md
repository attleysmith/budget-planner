# budget-planner

Expense register and budget planner spring-boot application.

(Job interview project.)

## Registering expenses

Simple CRUD API to register single financial transactions.
Transactions contain sum, currency (HUF, EUR), an optional summary, the date of payment and a category chosen from several predefined values.
Querying transactions allows filtering by category and time interval.

## Planning the future

Taking historical data as basis of the calculation, there is a feature to predict the expenses of the next 30 days.

- The calculation considers expenses on a daily basis.
- There should be at least 20 days of history available.
- At most the last 365 days are taken as relevant historical source.
- The day of planning is in the predicted 30 days, and it is not considered in the calculation. 
