/**
 * Copyright (C) 2013 Antonin Stefanutti (antonin.stefanutti@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.astefanutti.metrics.aspectj.se;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import io.astefanutti.metrics.aspectj.se.util.MetricsUtil;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.fest.reflect.core.Reflection.method;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InheritedTimedMethodWithVisibilityModifiersTest {

    private final static String REGISTRY_NAME = "visibilityTimerRegistry";

    private final static String[] TIMER_NAMES = {"publicTimedMethod", "packagePrivateTimedMethod", "protectedTimedMethod", "privateTimedMethod"};

    private InheritedTimedMethodWithVisibilityModifiers instance;

    private Set<String> absoluteMetricNames() {
        return MetricsUtil.absoluteMetricNames(TimedMethodWithVisibilityModifiers.class, TIMER_NAMES);
    }

    @Before
    public void createTimedInstance() {
        instance = new InheritedTimedMethodWithVisibilityModifiers();
    }

    @After
    public void clearSharedMetricRegistries() {
        SharedMetricRegistries.clear();
    }

    @Test
    public void timedMethodsNotCalledYet() {
        assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat("Timers are not registered correctly", registry.getTimers().keySet(), is(equalTo(absoluteMetricNames())));

        // Make sure that all the timers haven't been called yet
        assertThat("Timer counts are incorrect", registry.getTimers().values(), everyItem(Matchers.<Timer>hasProperty("count", equalTo(0L))));
    }

    @Test
    public void callTimedMethodsOnce() {
        assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat("Timers are not registered correctly", registry.getTimers().keySet(), is(equalTo(absoluteMetricNames())));

        // Call the timed methods and assert they've all been timed once
        instance.publicTimedMethod();
        instance.protectedTimedMethod();
        instance.packagePrivateTimedMethod();
        method("privateTimedMethod").in(instance).invoke();
        assertThat("Timer counts are incorrect", registry.getTimers().values(), everyItem(Matchers.<Timer>hasProperty("count", equalTo(1L))));
    }
}
