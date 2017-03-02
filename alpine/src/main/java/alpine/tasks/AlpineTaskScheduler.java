/*
 * This file is part of Alpine.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) Steve Springett. All Rights Reserved.
 */
package alpine.tasks;

import alpine.event.framework.Event;
import alpine.event.framework.EventService;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple framework for scheduling events to run periodicaly. Works in
 * conjunction with the {@link EventService} to process events.
 *
 * @author Steve Springett
 * @since 1.0.0
 */
public abstract class AlpineTaskScheduler {

    // Holds a list of all timers created during construction
    private List<Timer> timers = new ArrayList<>();

    /**
     * Schedules a repeating Event.
     * @param event the Event to schedule
     * @param delay delay in milliseconds before task is to be executed.
     * @param period time in milliseconds between successive task executions.
     */
    protected void scheduleEvent(Event event, long delay, long period) {
        final Timer timer = new Timer();
        timer.schedule(new ScheduleEvent().event(event), delay, period);
        timers.add(timer);
    }

    /**
     * Inner-class that when run() publishes an Event
     */
    private class ScheduleEvent extends TimerTask {
        private Event event;

        /**
         * The Event that will be published
         * @param event the Event to publish
         * @return a new ScheduleEvent instance
         */
        public ScheduleEvent event(Event event) {
            this.event = event;
            return this;
        }

        /**
         * Publishes the Event specified in the constructor.
         */
        public synchronized void run() {
            EventService.getInstance().publish(event);
        }
    }

    /**
     * Shuts town the TaskScheduler by canceling all scheduled events.
     */
    public void shutdown() {
        for (Timer timer: timers) {
            timer.cancel();
        }
    }

}
