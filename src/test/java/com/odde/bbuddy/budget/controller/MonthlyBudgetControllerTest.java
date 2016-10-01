package com.odde.bbuddy.budget.controller;

import com.nitorcreations.junit.runners.NestedRunner;
import com.odde.bbuddy.budget.domain.MonthlyBudget;
import com.odde.bbuddy.budget.domain.MonthlyBudgetPlanner;
import com.odde.bbuddy.budget.view.PresentableMonthlyBudgetAmount;
import com.odde.bbuddy.common.callback.PostActions;
import com.odde.bbuddy.common.view.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.validation.BindingResult;

import java.util.Date;

import static com.odde.bbuddy.common.Formats.parseDay;
import static com.odde.bbuddy.common.callback.PostActionsFactory.failed;
import static com.odde.bbuddy.common.callback.PostActionsFactory.success;
import static com.odde.bbuddy.common.controller.Urls.MONTHLYBUDGET_ADD;
import static com.odde.bbuddy.common.controller.Urls.MONTHLYBUDGET_TOTALAMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(NestedRunner.class)
public class MonthlyBudgetControllerTest {

    MonthlyBudgetPlanner mockPlanner = mock(MonthlyBudgetPlanner.class);
    PresentableMonthlyBudgetAmount mockPresentableMonthlyBudgetAmount = mock(PresentableMonthlyBudgetAmount.class);
    Message mockMessage = mock(Message.class);
    MonthlyBudgetController controller = new MonthlyBudgetController(mockPlanner, mockPresentableMonthlyBudgetAmount, mockMessage);
    BindingResult stubBindingResult = mock(BindingResult.class);

    @Before
    public void given_has_no_field_error() {
        given_has_field_error(false);
    }

    public class Add {

        @Test
        public void should_go_to_monthly_budget_add_page() {
            assertThat(controller.addMonthlyBudget()).isEqualTo(MONTHLYBUDGET_ADD);
        }

    }

    public class AddSubmitSuccess {

        MonthlyBudget monthlyBudget = new MonthlyBudget(parseDay("2016-07-01"), 100);

        @Before
        public void given_add_monthly_budget_will_success() {
            given_add_monthly_budget_will(success());
        }

        @Test
        public void should_go_to_add_monthly_budget_page() {
            assertThat(submitAddMonthlyBudget(monthlyBudget)).isEqualTo(MONTHLYBUDGET_ADD);
        }

        @Test
        public void should_add_monthly_budget() {
            submitAddMonthlyBudget(monthlyBudget);

            verify(mockPlanner).addMonthlyBudget(monthlyBudget);
        }

        @Test
        public void should_display_success_message() {
            controller.successMessage = "a success message";

            submitAddMonthlyBudget(monthlyBudget);

            verify(mockMessage).display("a success message");
        }
    }

    public class AddSubmitFailed {

        MonthlyBudget monthlyBudget = new MonthlyBudget(parseDay("2016-07-01"), 100);

        @Test
        public void should_display_failed_message() {
            given_add_monthly_budget_will(failed());
            controller.failedMessage = "a failed message";

            submitAddMonthlyBudget(monthlyBudget);

            verify(mockMessage).display("a failed message");
        }

    }

    public class Valid {

        MonthlyBudget invalidMonthlyBudget = new MonthlyBudget(null, null);

        @Before
        public void given_has_some_field_error() {
            given_has_field_error(true);
        }

        @Test
        public void should_not_add_monthly_budget() {
            submitAddMonthlyBudget(invalidMonthlyBudget);

            verify(mockPlanner, never()).addMonthlyBudget(invalidMonthlyBudget);
        }

        @Test
        public void should_go_to_add_monthly_budget_page() {
            assertThat(submitAddMonthlyBudget(invalidMonthlyBudget)).isEqualTo(MONTHLYBUDGET_ADD);
        }

    }

    public class GetAmount {

        Date startDate = parseDay("2016-07-01");
        Date endDate = parseDay("2016-07-10");

        @Test
        public void should_go_to_get_amount_page() {
            assertThat(getAmount()).isEqualTo(MONTHLYBUDGET_TOTALAMOUNT);
        }

        @Test
        public void should_get_amount_from_monthly_budget_planner() {
            getAmount();

            verify(mockPlanner).getAmount(startDate, endDate);
        }

        @Test
        public void should_pass_amount_to_page() {
            when(mockPlanner.getAmount(startDate, endDate)).thenReturn(100L);

            getAmount();

            verify(mockPresentableMonthlyBudgetAmount).display(100L);
        }

        private String getAmount() {
            return controller.totalAmountOfMonthlyBudget(startDate, endDate);
        }

    }

    private void given_has_field_error(boolean value) {
        when(stubBindingResult.hasFieldErrors()).thenReturn(value);
    }

    private void given_add_monthly_budget_will(PostActions postActions) {
        when(mockPlanner.addMonthlyBudget(any(MonthlyBudget.class))).thenReturn(postActions);
    }

    private String submitAddMonthlyBudget(MonthlyBudget monthlyBudget) {
        return controller.submitAddMonthlyBudget(monthlyBudget, stubBindingResult);
    }

}