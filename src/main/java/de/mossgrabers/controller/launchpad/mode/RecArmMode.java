// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.mode;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.track.AbstractTrackMode;


/**
 * The rec arm track mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RecArmMode extends AbstractTrackMode<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public RecArmMode (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Rec Arm", surface, model, true);
    }
}
