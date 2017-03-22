package com.minivac.mini.flux

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class DispatcherTest : Spek({

    it("should add subscriptions") {
        val dispatcher = Dispatcher(verifyThreads = false)
        var called = false
        dispatcher.subscribe(DummyAction::class) {
            called = true
            assert(it.value == 3)
        }
        dispatcher.dispatch(DummyAction(3))
        assert(called)
    }

    it("subscriptions should be ordered") {
        val dispatcher = Dispatcher(verifyThreads = false)
        val callOrder = ArrayList<Int>()

        dispatcher.subscribe(30, DummyAction::class) { callOrder.add(2) }
        dispatcher.subscribe(30, DummyAction::class) { callOrder.add(3) }
        dispatcher.subscribe(0, DummyAction::class) { callOrder.add(1) }
        dispatcher.dispatch(DummyAction(3))

        assertThat(dispatcher.subscriptionCount, equalTo(3))
        assertThat(callOrder, equalTo(listOf(1, 2, 3)))
    }
})

data class DummyAction(val value: Int) : Action