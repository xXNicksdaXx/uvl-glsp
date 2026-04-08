/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */

package de.tu_dresden.inf.st.uvl.glsp.actions;

import org.eclipse.glsp.server.actions.ResponseAction;

public class HighlightElementResponseAction extends ResponseAction {

  public static final String KIND = "highlightElementResponse";

  private boolean ok;

  public HighlightElementResponseAction() {
    super(KIND);
  }

  public HighlightElementResponseAction(boolean ok) {
    super(KIND);
    this.ok = ok;
  }

  @Override
  public String getKind() {
    return KIND;
  }

  public boolean isOk() {
    return ok;
  }

  public void setOk(boolean ok) {
    this.ok = ok;
  }
}
