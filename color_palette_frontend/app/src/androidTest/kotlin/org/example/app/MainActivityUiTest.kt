package org.example.app

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityUiTest {

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun generateButton_exists_and_clickable() {
        onView(withId(R.id.fabGenerate)).check { view, _ ->
            assert(view.isClickable)
        }
        onView(withId(R.id.fabGenerate)).perform(click())
    }

    @Test
    fun tiles_exist() {
        onView(withId(R.id.tile1)).check { view, _ -> assert(view.isShown) }
        onView(withId(R.id.tile2)).check { view, _ -> assert(view.isShown) }
        onView(withId(R.id.tile3)).check { view, _ -> assert(view.isShown) }
        onView(withId(R.id.tile4)).check { view, _ -> assert(view.isShown) }
        onView(withId(R.id.tile5)).check { view, _ -> assert(view.isShown) }
    }
}
