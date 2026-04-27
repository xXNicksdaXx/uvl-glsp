/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */

package de.tu_dresden.inf.st.uvl.bp.glsp.actions;

import org.eclipse.glsp.server.actions.Action;

public class SSEStopListeningAction extends Action {

  public static final String KIND = "sseStopListening";

  public SSEStopListeningAction() {
    super(KIND);
  }

  @Override
  public String getKind() {
    return KIND;
  }
}
