/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import {
    AnchorComputerRegistry,
    GConnectableElement,
    GLSPPolylineEdgeRouter,
    GParentElement,
    GRoutableElement,
    IAnchorComputer,
    Point,
    translatePoint
} from "@eclipse-glsp/client";
import { inject, injectable } from "inversify";

import { CENTERED_ANCHOR_KIND } from "./center-anchor-computer";

@injectable()
export class UVLPolylineEdgeRouter extends GLSPPolylineEdgeRouter {

    @inject(AnchorComputerRegistry) override anchorRegistry: AnchorComputerRegistry;

    override getTranslatedAnchor(
        connectable: GConnectableElement,
        refPoint: Point,
        refContainer: GParentElement,
        edge: GRoutableElement,
        anchorCorrection?: number | undefined
    ): Point {
        const translatedRefPoint = translatePoint(refPoint, refContainer, connectable.parent);

        // If the edge has the "centered-anchor" CSS class, use the centered anchor computer; otherwise, use the default one.
        let anchorComputer: IAnchorComputer;
        if (edge.cssClasses?.includes("centered-anchor")) {
            anchorComputer = this.anchorRegistry.get(this.kind, CENTERED_ANCHOR_KIND);
        } else {
            anchorComputer = this.getAnchorComputer(connectable);
        }

        const strokeCorrection = 0.5 * connectable.strokeWidth;
        const anchor = anchorComputer.getAnchor(connectable, translatedRefPoint, strokeCorrection + (anchorCorrection ?? 0));

        const translatedAnchor = translatePoint(anchor, connectable.parent, edge.parent);
        return Point.isValid(anchor) ? translatedAnchor : refPoint;
    }
}
