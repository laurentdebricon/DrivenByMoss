// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.continuous;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IClip;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.utils.ButtonEvent;
import java.time.*;


/**
 * Command for different functionalities of a foot switch.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FootswitchCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    
    private Instant instantUp;
    private Instant instantDown;
    
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public FootswitchCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.handleViewCommand (event))
            return;

        if (event != ButtonEvent.DOWN)
            return;

        switch (this.getSetting ())
        {
            case AbstractConfiguration.FOOTSWITCH_2_STOP_ALL_CLIPS:
                this.model.getCurrentTrackBank ().stop ();
                break;

            case AbstractConfiguration.FOOTSWITCH_2_TOGGLE_CLIP_OVERDUB:
                this.model.getTransport ().toggleLauncherOverdub ();
                break;

            case AbstractConfiguration.FOOTSWITCH_2_PANEL_LAYOUT_ARRANGE:
                this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_ARRANGE);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_PANEL_LAYOUT_MIX:
                this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_MIX);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_PANEL_LAYOUT_EDIT:
                this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_EDIT);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_ADD_INSTRUMENT_TRACK:
                this.model.getApplication ().addInstrumentTrack ();
                break;

            case AbstractConfiguration.FOOTSWITCH_2_ADD_AUDIO_TRACK:
                this.model.getApplication ().addAudioTrack ();
                break;

            case AbstractConfiguration.FOOTSWITCH_2_ADD_EFFECT_TRACK:
                this.model.getApplication ().addEffectTrack ();
                break;

            case AbstractConfiguration.FOOTSWITCH_2_QUANTIZE:
                final IClip clip = this.model.getCursorClip ();
                if (clip.doesExist ())
                    clip.quantize (this.surface.getConfiguration ().getQuantizeAmount () / 100.0);
                break;

            default:
                this.model.getHost ().error ("Unknown footswitch command called: " + this.getSetting ());
                break;
        }
    }


    /**
     * Get the configuration setting.
     *
     * @return The setting
     */
    protected int getSetting ()
    {
        return this.surface.getConfiguration ().getFootswitch2 ();
    }


    /**
     * Handles all view related commands.
     *
     * @param event The event
     * @return True if handled
     */
    private boolean handleViewCommand (final ButtonEvent event)
    {
        switch (this.getSetting ())
        {
            case AbstractConfiguration.FOOTSWITCH_2_TOGGLE_PLAY:
                this.surface.getButton (ButtonID.PLAY).trigger (event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_TOGGLE_RECORD:
                this.surface.getButton (ButtonID.RECORD).trigger (event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_UNDO:
                this.surface.getButton (ButtonID.UNDO).trigger (event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_TAP_TEMPO:
                this.surface.getButton (ButtonID.TAP_TEMPO).trigger (event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_NEW_BUTTON:
                this.surface.getButton (ButtonID.NEW).trigger (event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_CLIP_BASED_LOOPER:
                this.handleLooper (event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_LOOPER_CUSTOM:
                this.handleLooperCustom(event);
                break;

            default:
                return false;
        }
        return true;
    }


    /**
     * Handle clip looper.
     *
     * @param event The button event
     */
    private void handleLooper (final ButtonEvent event)
    {
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (!cursorTrack.doesExist ())
        {
            this.surface.getDisplay ().notify ("Please select an Instrument track first.");
            return;
        }

        final ISlotBank slotBank = cursorTrack.getSlotBank ();
        final ISlot selectedSlot = slotBank.getSelectedItem ();
        final ISlot slot = selectedSlot == null ? slotBank.getItem (0) : selectedSlot;
        if (event == ButtonEvent.DOWN)
        {
            if (slot.hasContent ())
            {
                // If there is a clip in the selected slot, enable (not toggle)
                // LauncherOverdub.
                this.model.getTransport ().setLauncherOverdub (true);
            }
            else
            {
                // If there is no clip in the selected slot, create a clip and begin record
                // mode. Releasing it ends record mode.
                this.surface.getButton (ButtonID.NEW).trigger (event);
                slot.select ();
                this.model.getTransport ().setLauncherOverdub (true);
            }
        }
        else
        {
            // Releasing it would turn off LauncherOverdub.
            this.model.getTransport ().setLauncherOverdub (false);
        }
        // Start transport if not already playing
        slot.launch ();
    }


    private void handleLooperCustom (final ButtonEvent event)
    {

        if (event == ButtonEvent.LONG)
            return;

        this.surface.println(event.toString());
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (!cursorTrack.doesExist ())
        {
            this.surface.getDisplay ().notify ("Please select an Instrument track first.");
            return;
        }

        if(!cursorTrack.isRecArm()){
            this.surface.getDisplay ().notify ("Rec Arm " + cursorTrack.getIndex() + " !");
        }

        final ISlotBank slotBank = cursorTrack.getSlotBank ();
        final ISlot selectedSlot = slotBank.getSelectedItem ();
        boolean noSelectedSlot = selectedSlot == null;
        ISlot slot = selectedSlot == null ? slotBank.getItem (0) : selectedSlot;
        if (event == ButtonEvent.DOWN)
        {
            instantDown = Instant.now();
        }else{
            long delta = -1;
            if(instantDown != null){ // safegard it could be null if pedal is plugged in pressed maybe
                instantUp = Instant.now();
                delta = Duration.between(instantDown, instantUp).toMillis(); 
                instantDown = null; // reset for next time
                this.surface.println(delta + " ms");
            }

            if(delta > 800 && delta < 2400){

                this.surface.println("long press");
                // oops my loop is not nice, stop it and delete
                if(slot.isRecording()){
                    slot.remove();
                    this.surface.getDisplay ().notify ("Stop this slot and Delete");
                }else{
                    // undo button
                    this.surface.getButton (ButtonID.NUDGE_MINUS).trigger (event);
                    // ISlot firstEmpty = slotBank.getEmptySlot(0); // not working properly :( maybe i save the previous recorded slot somewhere
                    // if(firstEmpty != null){
                    //     firstEmpty.select();
                    // }
                    this.surface.getDisplay ().notify ("Undo");
                }
            }else if(delta > 2400){
                this.surface.getDisplay ().notify ("Duplicate with no clip");
                this.surface.println("very long press");
                // disable record
                cursorTrack.setRecArm(false);
                cursorTrack.duplicate(); // pas sur de ça, normalement ça va sur la nouvelle
                int index = cursorTrack.getIndex();
                ITrack newTrack = this.model.getTrackBank().getItem(index +1);
                newTrack.select();
                // clear all clip of the cloned track
                var newTrackSlotBank = newTrack.getSlotBank();
                for (int i = 0; i < newTrackSlotBank.getPositionOfLastItem() + 1; i++) {
                    // how to remove the full line and go beyond the bank ?
                    newTrackSlotBank.getItem(i).remove();
                }

                newTrackSlotBank.getItem(0).select();
                newTrack.setRecArm(true);
                // enable record, you can now jam over your loop !
            }else{
                if(cursorTrack.canHoldNotes() && noSelectedSlot){                   
                    slot = slotBank.getEmptySlot(0);
                    if(slot == null){
                        this.surface.getDisplay ().notify ("No more empty slot in the bank");
                        return;
                    }
                    slot.select();
                }

                if (slot.hasContent ()){
                        if(cursorTrack.canHoldAudioData()){
                            // create new clip and record
                            this.surface.getButton (ButtonID.NEW).trigger (ButtonEvent.DOWN);
                        }else{
                            // If there is a clip in the selected slot, toggle LauncherOverdub.
                            this.model.getTransport ().toggleLauncherOverdub();
                        }

                    // If the clip is recording, launch it and thereby go into a loop
                    if(slot.isRecording()){
                        slot.launch();
                    }
                }
                else
                {
                    // If there is no clip in the selected slot, start recording into it
                    slot.record();
                    slot.launch();
                    this.model.getTransport ().setLauncherOverdub (false);
                }
            }
        }
	}
	
	/* inspired from https://www.kvraudio.com/forum/viewtopic.php?p=6638994
	The way the footswitch behaves now is that it starts recording into a selected empty clip in the selected track that you choose. If your clips are not playing yet they will start after the pre-roll and you'll record into the clip. If your clips are already playing, recording will start at the next measure or however you've set it up globally so it will be in time.

	Then when you press the footswitch again, recording will stop at the next measure and the clip will start looping.

    If you press the footswitch again and you have still selected a playing clip : 
     - if isInstrument : then overdub will be toggled so you can record additional midi notes if you want, 
     - if isAudio : then new clip for audio, record and select

    If you longPress 1sec
     - if clip recording, it gets stop and deleted
     - else undo

    If you very longPress 3sec
     - current track no more armed, clone current track, new clone is rec armed, but empty of clips, to jam over your previous loop
	*/


}
