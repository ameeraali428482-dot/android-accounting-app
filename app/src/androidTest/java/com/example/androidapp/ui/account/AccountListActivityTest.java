package com.example.androidapp.ui.account;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.androidapp.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AccountListActivityTest {

    @Rule
    public ActivityScenarioRule<AccountListActivity> activityRule = new ActivityScenarioRule<>(AccountListActivity.class);

    @Test
    public void testAccountListDisplay() {
        // Check if the RecyclerView for accounts is displayed
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_accounts))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Check if the FloatingActionButton for adding accounts is displayed
        Espresso.onView(ViewMatchers.withId(R.id.fab_add_account))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testAddAccountButton_opensAccountDetailActivity() {
        // Click the FloatingActionButton to add a new account
        Espresso.onView(ViewMatchers.withId(R.id.fab_add_account))
                .perform(ViewActions.click());

        // Check if AccountDetailActivity is launched (by checking for a view unique to it)
        Espresso.onView(ViewMatchers.withId(R.id.edit_text_account_name))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
