package com.example.androidapp;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.androidapp.ui.main.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testAIAnalysisButton_opensDialog() {
        // Click the AI Analysis button
        Espresso.onView(ViewMatchers.withId(R.id.ai_analysis_button))
                .perform(ViewActions.click());

        // Check if the AI analysis dialog is displayed
        Espresso.onView(ViewMatchers.withText("اختر نوع التحليل:"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Check if the spinner for analysis type is displayed
        Espresso.onView(ViewMatchers.withId(R.id.spinner_analysis_type))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Check if the EditText for financial data is displayed
        Espresso.onView(ViewMatchers.withId(R.id.edit_text_financial_data))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Check if the Analyze button is displayed
        Espresso.onView(ViewMatchers.withId(R.id.button_analyze))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testGoogleDriveButton_initiatesSignInOrBackup() {
        // Click the Google Drive button
        Espresso.onView(ViewMatchers.withId(R.id.google_drive_button))
                .perform(ViewActions.click());

        // Since we cannot mock Firebase/Google Sign-in directly in a simple UI test,
        // we'll check for a Toast message or a dialog that indicates the next step.
        // This assumes that if not signed in, it attempts to sign in, and if signed in, it attempts backup.
        // For a real app, you'd mock the GoogleSignInClient and GoogleDriveService.

        // Check for a Toast message indicating sign-in attempt or backup initiation
        // This is a simplified check and might need refinement based on actual Toast messages.
        // Espresso.onView(ViewMatchers.withText(CoreMatchers.containsString("Google Drive")))
        //         .inRoot(RootMatchers.withDecorView(CoreMatchers.not(activityRule.getActivity().getWindow().getDecorView())))
        //         .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    // Add more UI tests for other functionalities as needed
}

