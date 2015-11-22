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
package io.astefanutti.metrics.aspectj;

import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.Timed;
import org.aspectj.lang.reflect.MethodSignature;

final aspect TimedAspect {

    pointcut timed(Profiled object) : execution(@Timed !static * (@Metrics Profiled+).*(..)) && this(object);

    Object around(Profiled object) : timed(object) {
        System.out.println("#################");
        System.out.println("thisJoinPoint = " + thisJoinPoint);
        System.out.println("object = " + object);
        String methodSignature = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod().toString();
        System.out.println("methodSignature = " + methodSignature);
        System.out.println("object.timers = " + object.timers);
        System.out.println("object.timers.get(methodSignature) = " + object.timers.get(methodSignature));
        System.out.println("object.timers.get(methodSignature).getMetric() = " + object.timers.get(methodSignature).getMetric());
        Timer timer = object.timers.get(methodSignature).getMetric();
        System.out.println("timer = " + timer);
        Timer.Context context = timer.time();
        try {
            return proceed(object);
        } finally {
            context.stop();
        }
    }
}
