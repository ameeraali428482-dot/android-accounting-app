package com.example.androidapp.ui.journalentry;

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
public class JournalEntryListActivityTest {

    @Rule
    public ActivityScenarioRule<JournalEntryListActivity> activityRule = new ActivityScenarioRule<>(JournalEntryListActivity.class);

    @Test
    public void testJournalEntryListDisplay() {
        // Check if the RecyclerView for journal entries is displayed
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_journal_entries))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Check if the FloatingActionButton for adding journal entries is displayed
        Espresso.onView(ViewMatchers.withId(R.id.fab_add_journal_entry))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testAddJournalEntryButton_opensJournalEntryDetailActivity() {
        // Click the FloatingActionButton to add a new journal entry
        Espresso.onView(ViewMatchers.withId(R.id.fab_add_journal_entry))
                .perform(ViewActions.click());

        // Check if JournalEntryDetailActivity is launched (by checking for a view unique to it)
        Espresso.onView(ViewMatchers.withId(R.id.edit_text_journal_entry_description))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
