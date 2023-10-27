package com.sprintform.interview.budgetplanner.interfaces;

import com.sprintform.interview.budgetplanner.application.BudgetPlannerService;
import com.sprintform.interview.budgetplanner.application.model.BudgetPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BudgetPlannerController {

    @Autowired
    private BudgetPlannerService budgetPlannerService;

    @PostMapping("/plan")
    public BudgetPlan plan() {
        return budgetPlannerService.plan();
    }
}
