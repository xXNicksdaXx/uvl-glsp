/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */

package de.tu_dresden.inf.st.uvl.bp.glsp.actions;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tu_dresden.inf.st.uvl.bp.glsp.service.ServerSentEventsService;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.glsp.server.actions.Action;
import org.eclipse.glsp.server.actions.ActionHandler;

@Singleton
public class SSEControlActionHandler implements ActionHandler {

  @Inject protected ServerSentEventsService serverSentEventsService;

  @Override
  public List<Action> execute(final Action action) {
    if (action instanceof SSEStartListeningAction) {
      serverSentEventsService.start();
    } else if (action instanceof SSEHighFrequencyPollingAction) {
      serverSentEventsService.triggerHighFrequencyPolling();
    } else if (action instanceof SSEStopListeningAction) {
      serverSentEventsService.stop();
    }

    return none();
  }

  @Override
  public List<Class<? extends Action>> getHandledActionTypes() {
    return new ArrayList<>(
        List.of(
            SSEStartListeningAction.class,
            SSEHighFrequencyPollingAction.class,
            SSEStopListeningAction.class));
  }
}
