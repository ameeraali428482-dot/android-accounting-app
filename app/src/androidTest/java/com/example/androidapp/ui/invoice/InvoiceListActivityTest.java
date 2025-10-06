package com.example.androidapp.ui.invoice;

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
public class InvoiceListActivityTest {

    @Rule
    public ActivityScenarioRule<InvoiceListActivity> activityRule = new ActivityScenarioRule<>(InvoiceListActivity.class);

    @Test
    public void testInvoiceListDisplay() {
        // Check if the RecyclerView for invoices is displayed
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_invoices))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Check if the FloatingActionButton for adding invoices is displayed
        Espresso.onView(ViewMatchers.withId(R.id.fab_add_invoice))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testAddInvoiceButton_opensInvoiceDetailActivity() {
        // Click the FloatingActionButton to add a new invoice
        Espresso.onView(ViewMatchers.withId(R.id.fab_add_invoice))
                .perform(ViewActions.click());

        // Check if InvoiceDetailActivity is launched (by checking for a view unique to it)
        Espresso.onView(ViewMatchers.withId(R.id.edit_text_invoice_customer))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
