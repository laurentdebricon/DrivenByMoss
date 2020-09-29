// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchkey.view;

import de.mossgrabers.controller.launchkey.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.launchkey.controller.LaunchkeyMiniMk3ColorManager;
import de.mossgrabers.controller.launchkey.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.SessionColor;
import de.mossgrabers.framework.view.Views;


/**
 * The Session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionView extends AbstractSessionView<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration>
{
    /** Modes which can take over the first row of pads. */
    public static final Modes [] PAD_MODES =
    {
        Modes.REC_ARM,
        Modes.TRACK_SELECT,
        Modes.MUTE,
        Modes.SOLO,
        Modes.STOP_CLIP
    };

    private Modes                padMode   = null;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final LaunchkeyMiniMk3ControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 2, 8, true);

        final SessionColor isRecording = new SessionColor (LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED, false);
        final SessionColor isRecordingQueued = new SessionColor (LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_LO, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_LO, true);
        final SessionColor isPlaying = new SessionColor (LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN_SPRING, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN, false);
        final SessionColor isPlayingQueued = new SessionColor (LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN_SPRING, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN, true);
        final SessionColor hasContent = new SessionColor (LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER, -1, false);
        final SessionColor noContent = new SessionColor (LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK, -1, false);
        final SessionColor recArmed = new SessionColor (LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_LO, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ColorManager colorManager = this.model.getColorManager ();
        final int colorScene = colorManager.getColorIndex (AbstractSessionView.COLOR_SCENE);
        final int colorSceneSelected = colorManager.getColorIndex (AbstractSessionView.COLOR_SELECTED_SCENE);
        final int colorSceneOff = colorManager.getColorIndex (AbstractSessionView.COLOR_SCENE_OFF);

        final ISceneBank sceneBank = this.model.getSceneBank ();
        IScene s = sceneBank.getItem (0);

        if (buttonID == ButtonID.SCENE1)
            return s.doesExist () ? s.isSelected () ? colorSceneSelected : colorScene : colorSceneOff;

        // SCENE 2

        if (this.padMode == null)
        {
            s = sceneBank.getItem (1);
            return s.doesExist () ? s.isSelected () ? colorSceneSelected : colorScene : colorSceneOff;
        }

        switch (this.padMode)
        {
            case REC_ARM:
                return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_HI;
            case TRACK_SELECT:
                return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_WHITE;
            case MUTE:
                return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER_HI;
            case SOLO:
                return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_HI;
            case STOP_CLIP:
                return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_ROSE;
            default:
                return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final boolean controlModeIsOff = this.padMode == null;
        this.rows = controlModeIsOff ? 2 : 1;

        super.drawGrid ();

        if (controlModeIsOff)
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final IPadGrid pads = this.surface.getPadGrid ();
        for (int x = 0; x < this.columns; x++)
        {
            final ITrack track = tb.getItem (x);
            final boolean exists = track.doesExist ();
            switch (this.padMode)
            {
                case REC_ARM:
                    pads.lightEx (x, 1, exists ? track.isRecArm () ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_LO : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
                    break;
                case TRACK_SELECT:
                    pads.lightEx (x, 1, exists ? track.isSelected () ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_WHITE : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
                    break;
                case MUTE:
                    pads.lightEx (x, 1, exists ? track.isMute () ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER_LO : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
                    break;
                case SOLO:
                    pads.lightEx (x, 1, exists ? track.isSolo () ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_LO : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
                    break;
                case STOP_CLIP:
                    pads.lightEx (x, 1, exists ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_ROSE : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
                    break;
                default:
                    // Unused
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity != 0)
            return;

        final Pair<Integer, Integer> padPos = this.getPad (note);
        final int row = padPos.getValue ().intValue ();
        if (row == 0 || this.padMode == null)
            super.onGridNote (note, velocity);
        else
            this.handleFirstRowModes (padPos.getKey ().intValue ());
    }

    /** {@inheritDoc} */
	@Override
	public void onGridNoteLongPress (final int note)
	{
		final SessionView view = (SessionView) this.surface.getViewManager ().getView (Views.SESSION);
		final Modes padMode = view.getPadMode ();

		final Pair<Integer, Integer> padPos = this.getPad (note);
		final int column = padPos.getKey ().intValue ();
		final int row = padPos.getValue ().intValue ();
		
		if(padMode != null && row == -1){
			// we have a padmode selected and longpressing on a second row pad, do nothing
			return;
		}else{
			// long press stop the track, TODO do it with scene button also. How ??

			final ITrack track = this.model.getCurrentTrackBank ().getItem (column);
			final ISlot slot = track.getSlotBank ().getItem (row);

		

			if (slot.hasContent ())
			{
				track.stop ();
			}else{
				if (!slot.isRecording ()){
					// turn off recArms of every track, and recArm the selected one.
					
					final ITrackBank bank = this.model.getTrackBank ();

					int pageSize = bank.getPageSize();

					// TODO disabling for all tracks not just in the pageSize
					// int scrollPosition = bank.getScrollPosition();
					// bank.scrollTo(0);
					// for(int i=0; i< bank.getItemCount(); i++){
					// 	bank.getItem (i%pageSize).setRecArm(i == column);
					// 	if(!bank.canScrollForwards()){
					// 		bank.scrollForwards();
					// 	}
					// }
					// bank.scrollTo(scrollPosition);


					for(int i=0; i< pageSize; i++){
						bank.getItem (i).setRecArm(i == column);
					}

					track.setRecArm(true);
					slot.record ();

				}
			}
		}

			/* TODO : if scene empty, launch record
			final ISceneBank sceneBank = this.model.getCurrentTrackBank ().getSceneBank ();
			getSlotBank
	
			final ISlot selectedSlot = this.model.getSelectedSlot ();
			if (selectedSlot != null)
				selectedSlot.record ();
	
			if (slot.hasContent ())
			{
				slot.stop ();
				return;
			}else{
				slot.record ();
			}
	*/
	}


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID))
            return;

        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        final ISceneBank sceneBank = this.model.getCurrentTrackBank ().getSceneBank ();

        if (this.surface.isShiftPressed ())
        {
            if (event != ButtonEvent.UP)
                return;

            if (index == 0)
                sceneBank.selectPreviousPage ();
            else
                sceneBank.selectNextPage ();
            return;
        }

        if (event == ButtonEvent.DOWN)
        {
            if (index == 1)
                this.surface.getViewManager ().setActiveView (Views.CONTROL);
            return;
        }

        if (event == ButtonEvent.UP)
        {
            final IScene scene = sceneBank.getItem (index);
            scene.select ();
            scene.launch ();
        }
    }


    /** {@inheritDoc} */
    @Override
    protected Pair<Integer, Integer> getPad (final int note)
    {
        final Pair<Integer, Integer> pad = super.getPad (note);
        if (this.padMode != null)
            pad.setValue (Integer.valueOf (pad.getValue ().intValue () == 0 ? -1 : 0));
        return pad;
    }


    /**
     * Get the pad mode.
     *
     * @return The pad mode
     */
    public Modes getPadMode ()
    {
        return this.padMode;
    }


    /**
     * Set the pad mode.
     *
     * @param padMode The pad mode
     */
    public void setPadMode (final Modes padMode)
    {
        this.padMode = padMode;
    }


    /**
     * Execute the functions of row 1 if active.
     *
     * @param column The column of the pressed pad
     */
    private void handleFirstRowModes (final int column)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (column);
        switch (this.padMode)
        {
            case REC_ARM:
                track.toggleRecArm ();
                break;
            case TRACK_SELECT:
                this.selectTrack (column);
                break;
            case MUTE:
                track.toggleMute ();
                break;
            case SOLO:
                track.toggleSolo ();
                break;
            case STOP_CLIP:
                track.stop ();
                break;
            default:
                // Unused
                break;
        }
    }
}