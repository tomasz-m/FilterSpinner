package info.tomaszminiach.libshow;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class UITest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void showPopup() {
        onView(withId(R.id.superSpinner))
                .perform(click());

        onView(withId(R.id.spinnerPopup))
                .check(matches(isDisplayed()));
    }

    @Test
    public void noItems() {
        onView(withId(R.id.superSpinner))
                .perform(click());
        onView(withId(R.id.editText))
                .perform(replaceText("Aa%#&*()!(#_.,;"), closeSoftKeyboard());

        onView(withId(R.id.emptyView))
                .check(matches(isDisplayed()));
    }

}