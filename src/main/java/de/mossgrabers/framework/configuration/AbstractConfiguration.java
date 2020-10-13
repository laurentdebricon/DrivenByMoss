// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.EditCapability;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.observer.ISettingObserver;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.scale.ScaleLayout;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.Views;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Abstract base class for extension settings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractConfiguration implements Configuration
{
    /** ID for scale setting. */
    public static final Integer      SCALES_SCALE                      = Integer.valueOf (0);
    /** ID for scale base note setting. */
    public static final Integer      SCALES_BASE                       = Integer.valueOf (1);
    /** ID for scale in-key setting. */
    public static final Integer      SCALES_IN_KEY                     = Integer.valueOf (2);
    /** ID for scale layout setting. */
    public static final Integer      SCALES_LAYOUT                     = Integer.valueOf (3);
    /** ID for enabling VU meters setting. */
    public static final Integer      ENABLE_VU_METERS                  = Integer.valueOf (4);
    /** ID for behaviour on stop setting. */
    public static final Integer      BEHAVIOUR_ON_STOP                 = Integer.valueOf (5);
    /** ID for flipping the session grid setting. */
    public static final Integer      FLIP_SESSION                      = Integer.valueOf (6);
    /** ID for selecting the clip on launch setting. */
    public static final Integer      SELECT_CLIP_ON_LAUNCH             = Integer.valueOf (7);
    /** ID for drawing record stripes setting. */
    public static final Integer      DRAW_RECORD_STRIPE                = Integer.valueOf (8);
    /** ID for converting the aftertouch data setting. */
    public static final Integer      CONVERT_AFTERTOUCH                = Integer.valueOf (9);
    /** ID for activating the fixed accent setting. */
    public static final Integer      ACTIVATE_FIXED_ACCENT             = Integer.valueOf (10);
    /** ID for the value of the fixed accent setting. */
    public static final Integer      FIXED_ACCENT_VALUE                = Integer.valueOf (11);
    /** ID for the quantize amount setting. */
    public static final Integer      QUANTIZE_AMOUNT                   = Integer.valueOf (12);
    /** ID for the flip recording setting. */
    public static final Integer      FLIP_RECORD                       = Integer.valueOf (13);
    /** Setting for automatic selecting the drum channel. */
    public static final Integer      AUTO_SELECT_DRUM                  = Integer.valueOf (14);
    /** Setting for new clip length. */
    public static final Integer      NEW_CLIP_LENGTH                   = Integer.valueOf (15);
    /** Setting for turning off empty drum pads (otherwise orange). */
    public static final Integer      TURN_OFF_EMPTY_DRUM_PADS          = Integer.valueOf (16);
    /** Setting for action for rec armed pad. */
    public static final Integer      ACTION_FOR_REC_ARMED_PAD          = Integer.valueOf (17);
    /** Setting for the footswitch functionality. */
    public static final Integer      FOOTSWITCH_2                      = Integer.valueOf (18);
    /** Setting for displaying browser column 1. */
    public static final Integer      BROWSER_DISPLAY_FILTER1           = Integer.valueOf (19);
    /** Setting for displaying browser column 2. */
    public static final Integer      BROWSER_DISPLAY_FILTER2           = Integer.valueOf (20);
    /** Setting for displaying browser column 3. */
    public static final Integer      BROWSER_DISPLAY_FILTER3           = Integer.valueOf (21);
    /** Setting for displaying browser column 4. */
    public static final Integer      BROWSER_DISPLAY_FILTER4           = Integer.valueOf (22);
    /** Setting for displaying browser column 5. */
    public static final Integer      BROWSER_DISPLAY_FILTER5           = Integer.valueOf (23);
    /** Setting for displaying browser column 6. */
    public static final Integer      BROWSER_DISPLAY_FILTER6           = Integer.valueOf (24);
    /** Setting for displaying browser column 7. */
    public static final Integer      BROWSER_DISPLAY_FILTER7           = Integer.valueOf (25);
    /** Setting for displaying browser column 8. */
    public static final Integer      BROWSER_DISPLAY_FILTER8           = Integer.valueOf (26);
    /** The speed of a knob. */
    public static final Integer      KNOB_SENSITIVITY_DEFAULT          = Integer.valueOf (27);
    /** The speed of a knob in slow mode. */
    public static final Integer      KNOB_SENSITIVITY_SLOW             = Integer.valueOf (28);
    /** Turn noterepeat on/off. */
    public static final Integer      NOTEREPEAT_ACTIVE                 = Integer.valueOf (29);
    /** The note repeat period. */
    public static final Integer      NOTEREPEAT_PERIOD                 = Integer.valueOf (30);
    /** The note repeat length. */
    public static final Integer      NOTEREPEAT_LENGTH                 = Integer.valueOf (31);
    /** The note repeat mode. */
    public static final Integer      NOTEREPEAT_MODE                   = Integer.valueOf (32);
    /** The note repeat octave. */
    public static final Integer      NOTEREPEAT_OCTAVE                 = Integer.valueOf (33);
    /** The MIDI channel to use for editing sequencer notes. */
    public static final Integer      MIDI_EDIT_CHANNEL                 = Integer.valueOf (34);
    /** Setting for excluding deactivated tracks. */
    public static final Integer      EXCLUDE_DEACTIVATED_ITEMS         = Integer.valueOf (35);

    // Implementation IDs start at 50

    protected static final String    CATEGORY_DRUMS                    = "Drum Sequencer";
    protected static final String    CATEGORY_SCALES                   = "Scales";
    protected static final String    CATEGORY_SESSION                  = "Session";
    protected static final String    CATEGORY_TRANSPORT                = "Transport";
    protected static final String    CATEGORY_WORKFLOW                 = "Workflow";
    protected static final String    CATEGORY_PADS                     = "Pads";
    protected static final String    CATEGORY_PLAY_AND_SEQUENCE        = "Play and Sequence";
    protected static final String    CATEGORY_HARDWARE_SETUP           = "Hardware Setup";
    protected static final String    CATEGORY_DEBUG                    = "Debug";
    protected static final String    CATEGORY_NOTEREPEAT               = "Note Repeat";

    private static final String      SCALE_IN_KEY                      = "In Key";
    private static final String      SCALE_CHROMATIC                   = "Chromatic";

    /** Use footswitch 2 for toggling play. */
    public static final int          FOOTSWITCH_2_TOGGLE_PLAY          = 0;
    /** Use footswitch 2 for toggling record. */
    public static final int          FOOTSWITCH_2_TOGGLE_RECORD        = 1;
    /** Use footswitch 2 for stopping all clips. */
    public static final int          FOOTSWITCH_2_STOP_ALL_CLIPS       = 2;
    /** Use footswitch 2 for toggling clip overdub. */
    public static final int          FOOTSWITCH_2_TOGGLE_CLIP_OVERDUB  = 3;
    /** Use footswitch 2 for undo. */
    public static final int          FOOTSWITCH_2_UNDO                 = 4;
    /** Use footswitch 2 for tapping tempo. */
    public static final int          FOOTSWITCH_2_TAP_TEMPO            = 5;
    /** Use footswitch 2 as the new button. */
    public static final int          FOOTSWITCH_2_NEW_BUTTON           = 6;
    /** Use footswitch 2 as clip based looper. */
    public static final int          FOOTSWITCH_2_CLIP_BASED_LOOPER    = 7;
    /** Use footswitch 2 to trigger the arrange layout. */
    public static final int          FOOTSWITCH_2_PANEL_LAYOUT_ARRANGE = 8;
    /** Use footswitch 2 to trigger the mix layout. */
    public static final int          FOOTSWITCH_2_PANEL_LAYOUT_MIX     = 9;
    /** Use footswitch 2 to trigger the edit layout. */
    public static final int          FOOTSWITCH_2_PANEL_LAYOUT_EDIT    = 10;
    /** Use footswitch 2 to add a new instrument track. */
    public static final int          FOOTSWITCH_2_ADD_INSTRUMENT_TRACK = 11;
    /** Use footswitch 2 to add a new audio track. */
    public static final int          FOOTSWITCH_2_ADD_AUDIO_TRACK      = 12;
    /** Use footswitch 2 to add a new effect track. */
    public static final int          FOOTSWITCH_2_ADD_EFFECT_TRACK     = 13;
    /** Use footswitch 2 to quantize the selected clip. */
    public static final int          FOOTSWITCH_2_QUANTIZE             = 14;
    // Note: There are controllers who extend this list!
    public static final int          FOOTSWITCH_2_LOOPER_CUSTOM    	   = 15;

    protected static final String [] OPTIONS_MIDI_CHANNEL              = new String [16];
    protected static final String [] KNOB_SENSITIVITY                  = new String [201];
    static
    {
        for (int i = 0; i < OPTIONS_MIDI_CHANNEL.length; i++)
            OPTIONS_MIDI_CHANNEL[i] = Integer.toString (i + 1);

        for (int i = 0; i < 100; i++)
        {
            KNOB_SENSITIVITY[i] = "-" + (100 - i);
            KNOB_SENSITIVITY[101 + i] = "+" + (i + 1);
        }
        KNOB_SENSITIVITY[100] = "Normal";
    }

    protected static final ColorEx DEFAULT_COLOR_BACKGROUND         = ColorEx.fromRGB (83, 83, 83);
    protected static final ColorEx DEFAULT_COLOR_BORDER             = ColorEx.BLACK;
    protected static final ColorEx DEFAULT_COLOR_TEXT               = ColorEx.WHITE;
    protected static final ColorEx DEFAULT_COLOR_FADER              = ColorEx.fromRGB (69, 44, 19);
    protected static final ColorEx DEFAULT_COLOR_VU                 = ColorEx.GREEN;
    protected static final ColorEx DEFAULT_COLOR_EDIT               = ColorEx.fromRGB (240, 127, 17);
    protected static final ColorEx DEFAULT_COLOR_RECORD             = ColorEx.RED;
    protected static final ColorEx DEFAULT_COLOR_SOLO               = ColorEx.YELLOW;
    protected static final ColorEx DEFAULT_COLOR_MUTE               = ColorEx.fromRGB (245, 129, 17);
    protected static final ColorEx DEFAULT_COLOR_BACKGROUND_DARKER  = ColorEx.fromRGB (39, 39, 39);
    protected static final ColorEx DEFAULT_COLOR_BACKGROUND_LIGHTER = ColorEx.fromRGB (118, 118, 118);


    /** The behaviour when the stop button is pressed. */
    public enum BehaviourOnStop
    {
        /** Keep the play cursor at the current position on stop. */
        MOVE_PLAY_CURSOR,
        /** Move the cursor back to zero on stop. */
        RETURN_TO_ZERO,
        /** Only pause on stop. */
        PAUSE
    }


    private static final String [] AFTERTOUCH_CONVERSION_VALUES = new String [131];
    static
    {
        AFTERTOUCH_CONVERSION_VALUES[0] = "Off";
        AFTERTOUCH_CONVERSION_VALUES[1] = "Poly Aftertouch";
        AFTERTOUCH_CONVERSION_VALUES[2] = "Channel Aftertouch";
        for (int i = 0; i < 128; i++)
            AFTERTOUCH_CONVERSION_VALUES[3 + i] = "CC " + i;
    }

    /** The names for clip lengths. */
    protected static final String []                  NEW_CLIP_LENGTH_VALUES      =
    {
        "1 Beat",
        "2 Beat",
        "1 Bar",
        "2 Bars",
        "4 Bars",
        "8 Bars",
        "16 Bars",
        "32 Bars"
    };

    private static final String []                    BEHAVIOUR_ON_STOP_VALUES    =
    {
        "Move play cursor",
        "Return to Zero",
        "Pause"
    };

    private static final String []                    ACTIONS_REC_ARMED_PADS      =
    {
        "Start recording",
        "Create new clip",
        "Do nothing"
    };

    protected static final String []                  FOOTSWITCH_VALUES           =
    {
        "Toggle Play",
        "Toggle Record",
        "Stop All Clips",
        "Toggle Clip Overdub",
        "Undo",
        "Tap Tempo",
        "New Button",
        "Clip Based Looper",
        "Panel layout arrange",
        "Panel layout mix",
        "Panel layout edit",
        "Add instrument track",
        "Add audio track",
        "Add effect track",
        "Quantize",
        "Looper Custom"
    };

    private static final String []                    BROWSER_FILTER_COLUMN_NAMES =
    {
        "Collection",
        "Location",
        "File Type",
        "Category",
        "Tags",
        "Creator",
        "Device Type",
        "Device"
    };

    private static final String []                    COLUMN_VALUES               =
    {
        "Hide",
        "Show"
    };

    /** The Off/On option. */
    protected static final String []                  ON_OFF_OPTIONS              =
    {
        "Off",
        "On"
    };

    protected final IHost                             host;

    private IEnumSetting                              scaleBaseSetting;
    private IEnumSetting                              scaleInKeySetting;
    private IEnumSetting                              scaleLayoutSetting;
    private IEnumSetting                              scaleSetting;
    private IEnumSetting                              enableVUMetersSetting;
    private IEnumSetting                              flipSessionSetting;
    private IEnumSetting                              accentActiveSetting;
    private IIntegerSetting                           accentValueSetting;
    private IIntegerSetting                           quantizeAmountSetting;
    private IEnumSetting                              newClipLengthSetting;
    private IEnumSetting                              noteRepeatActiveSetting;
    private IEnumSetting                              noteRepeatPeriodSetting;
    private IEnumSetting                              noteRepeatLengthSetting;
    private IEnumSetting                              noteRepeatModeSetting;
    private IEnumSetting                              noteRepeatOctaveSetting;
    private IEnumSetting                              midiEditChannelSetting;

    private final Map<Integer, Set<ISettingObserver>> observers                   = new HashMap<> ();
    protected IValueChanger                           valueChanger;

    private String                                    scale                       = "Major";
    private String                                    scaleBase                   = "C";
    private boolean                                   scaleInKey                  = true;
    private String                                    scaleLayout                 = "4th ^";
    private boolean                                   enableVUMeters              = false;
    private BehaviourOnStop                           behaviourOnStop             = BehaviourOnStop.MOVE_PLAY_CURSOR;
    protected boolean                                 flipSession                 = false;
    private boolean                                   selectClipOnLaunch          = true;
    private boolean                                   drawRecordStripe            = true;
    private int                                       convertAftertouch           = 0;
    /** Accent button active. */
    private boolean                                   accentActive                = false;
    /** Fixed velocity value for accent. */
    private int                                       fixedAccentValue            = 127;
    private int                                       quantizeAmount              = 100;
    protected boolean                                 flipRecord                  = false;
    private int                                       newClipLength               = 2;
    private boolean                                   autoSelectDrum              = false;
    private boolean                                   turnOffEmptyDrumPads        = false;
    private int                                       actionForRecArmedPad        = 0;
    private int                                       footswitch2                 = FOOTSWITCH_2_NEW_BUTTON;
    private boolean []                                browserDisplayFilter        =
    {
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true
    };
    private int                                       knobSpeedDefault            = 0;
    private int                                       knobSpeedSlow               = -40;

    private boolean                                   noteRepeatActive            = false;
    private Resolution                                noteRepeatPeriod            = Resolution.RES_1_8;
    private Resolution                                noteRepeatLength            = Resolution.RES_1_8;
    private ArpeggiatorMode                           noteRepeatMode;
    private int                                       noteRepeatOctave            = 0;
    private int                                       midiEditChannel             = 0;
    private final ArpeggiatorMode []                  arpeggiatorModes;

    private boolean                                   includeMaster               = true;
    private boolean                                   excludeDeactivatedItems     = false;
    private final String []                           userPageNames               = new String [8];


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public AbstractConfiguration (final IHost host, final IValueChanger valueChanger, final ArpeggiatorMode [] arpeggiatorModes)
    {
        this.host = host;
        this.valueChanger = valueChanger;
        this.arpeggiatorModes = arpeggiatorModes;
        this.noteRepeatMode = arpeggiatorModes == null ? null : arpeggiatorModes[0];

        for (int i = 0; i < this.userPageNames.length; i++)
            this.userPageNames[i] = "Page " + (i + 1);

        Views.init ();
    }


    /** {@inheritDoc} */
    @Override
    public void addSettingObserver (final Integer settingID, final ISettingObserver observer)
    {
        this.observers.computeIfAbsent (settingID, id -> new HashSet<> ()).add (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void clearSettingObservers ()
    {
        this.observers.clear ();
    }


    /**
     * Set the scale by name.
     *
     * @param scale The name of a scale
     */
    public void setScale (final String scale)
    {
        this.scaleSetting.set (scale);
    }


    /** {@inheritDoc} */
    @Override
    public String getScale ()
    {
        return this.scale;
    }


    /**
     * Set the scale base note by name.
     *
     * @param scaleBase The name of a scale base note
     */
    public void setScaleBase (final String scaleBase)
    {
        this.scaleBaseSetting.set (scaleBase);
    }


    /** {@inheritDoc} */
    @Override
    public String getScaleBase ()
    {
        return this.scaleBase;
    }


    /**
     * Set the in-scale setting.
     *
     * @param inScale True if scale otherwise chromatic
     */
    public void setScaleInKey (final boolean inScale)
    {
        this.scaleInKeySetting.set (inScale ? SCALE_IN_KEY : SCALE_CHROMATIC);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isScaleInKey ()
    {
        return this.scaleInKey;
    }


    /**
     * Set the scale layout.
     *
     * @param scaleLayout The scale layout
     */
    public void setScaleLayout (final String scaleLayout)
    {
        this.scaleLayoutSetting.set (scaleLayout);
    }


    /** {@inheritDoc} */
    @Override
    public String getScaleLayout ()
    {
        return this.scaleLayout;
    }


    /** {@inheritDoc} */
    @Override
    public void setVUMetersEnabled (final boolean enabled)
    {
        this.setOnOffSetting (this.enableVUMetersSetting, enabled);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEnableVUMeters ()
    {
        return this.enableVUMeters;
    }


    /** {@inheritDoc} */
    @Override
    public void setFlipSession (final boolean enabled)
    {
        this.setOnOffSetting (this.flipSessionSetting, enabled);
    }


    /** {@inheritDoc} */
    @Override
    public void setAccentEnabled (final boolean enabled)
    {
        this.setOnOffSetting (this.accentActiveSetting, enabled);
    }


    /** {@inheritDoc} */
    @Override
    public void setFixedAccentValue (final int value)
    {
        this.accentValueSetting.set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void changeQuantizeAmount (final int control)
    {
        if (this.quantizeAmountSetting != null)
            this.quantizeAmountSetting.set (this.valueChanger.changeValue (control, this.quantizeAmount, -100, 101));
    }


    /** {@inheritDoc} */
    @Override
    public void setQuantizeAmount (final int value)
    {
        if (this.quantizeAmountSetting != null)
            this.quantizeAmountSetting.set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetQuantizeAmount ()
    {
        if (this.quantizeAmountSetting != null)
            this.quantizeAmountSetting.set (100);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelectClipOnLaunch ()
    {
        return this.selectClipOnLaunch;
    }


    /**
     * Sets an on/off setting.
     *
     * @param setting The setting
     * @param enabled On or off
     */
    protected void setOnOffSetting (final IEnumSetting setting, final boolean enabled)
    {
        if (setting != null)
            setting.set (enabled ? ON_OFF_OPTIONS[1] : ON_OFF_OPTIONS[0]);
    }


    /** {@inheritDoc} */
    @Override
    public BehaviourOnStop getBehaviourOnStop ()
    {
        return this.behaviourOnStop;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isFlipSession ()
    {
        return this.flipSession;
    }


    /** {@inheritDoc} */
    @Override
    public int getConvertAftertouch ()
    {
        return this.convertAftertouch;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isAccentActive ()
    {
        return this.accentActive;
    }


    /** {@inheritDoc} */
    @Override
    public int getFixedAccentValue ()
    {
        return this.fixedAccentValue;
    }


    /** {@inheritDoc} */
    @Override
    public int getQuantizeAmount ()
    {
        return this.quantizeAmount;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isFlipRecord ()
    {
        return this.flipRecord;
    }


    /** {@inheritDoc} */
    @Override
    public int getNewClipLength ()
    {
        return this.newClipLength;
    }


    /** {@inheritDoc} */
    @Override
    public int getNewClipLenghthInBeats (final int quartersPerMeasure)
    {
        return (int) (this.newClipLength < 2 ? Math.pow (2, this.newClipLength) : Math.pow (2, this.newClipLength - 2.0) * quartersPerMeasure);
    }


    /** {@inheritDoc} */
    @Override
    public void setNewClipLength (final int value)
    {
        this.newClipLengthSetting.set (NEW_CLIP_LENGTH_VALUES[value]);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isAutoSelectDrum ()
    {
        return this.autoSelectDrum;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTurnOffEmptyDrumPads ()
    {
        return this.turnOffEmptyDrumPads;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isDrawRecordStripe ()
    {
        return this.drawRecordStripe;
    }


    /** {@inheritDoc} */
    @Override
    public int getActionForRecArmedPad ()
    {
        return this.actionForRecArmedPad;
    }


    /** {@inheritDoc} */
    @Override
    public int getFootswitch2 ()
    {
        return this.footswitch2;
    }


    /**
     * Get the browser display filter.
     *
     * @return The array with states if a filter column should be displayed
     */
    public boolean [] getBrowserDisplayFilter ()
    {
        return this.browserDisplayFilter;
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobSensitivityDefault ()
    {
        return this.knobSpeedDefault;
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobSensitivitySlow ()
    {
        return this.knobSpeedSlow;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isNoteRepeatActive ()
    {
        return this.noteRepeatActive;
    }


    /** {@inheritDoc} */
    @Override
    public void setNoteRepeatActive (final boolean active)
    {
        this.setOnOffSetting (this.noteRepeatActiveSetting, active);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleNoteRepeatActive ()
    {
        this.setNoteRepeatActive (!this.isNoteRepeatActive ());
    }


    /** {@inheritDoc} */
    @Override
    public Resolution getNoteRepeatPeriod ()
    {
        return this.noteRepeatPeriod;
    }


    /** {@inheritDoc} */
    @Override
    public void setNoteRepeatPeriod (final Resolution noteRepeatPeriod)
    {
        this.noteRepeatPeriodSetting.set (noteRepeatPeriod.getName ());
    }


    /** {@inheritDoc} */
    @Override
    public Resolution getNoteRepeatLength ()
    {
        return this.noteRepeatLength;
    }


    /** {@inheritDoc} */
    @Override
    public void setNoteRepeatLength (final Resolution noteRepeatLength)
    {
        this.noteRepeatLengthSetting.set (noteRepeatLength.getName ());
    }


    /** {@inheritDoc} */
    @Override
    public ArpeggiatorMode getNoteRepeatMode ()
    {
        return this.noteRepeatMode;
    }


    /** {@inheritDoc} */
    @Override
    public void setNoteRepeatMode (final ArpeggiatorMode arpMode)
    {
        this.noteRepeatModeSetting.set (arpMode.getName ());
    }


    /** {@inheritDoc} */
    @Override
    public int getNoteRepeatOctave ()
    {
        return this.noteRepeatOctave;
    }


    /** {@inheritDoc} */
    @Override
    public void setNoteRepeatOctave (final int octave)
    {
        final int o = Math.max (0, Math.min (8, octave));
        this.noteRepeatOctaveSetting.set (Integer.toString (o));
    }


    /** {@inheritDoc} */
    @Override
    public int getMidiEditChannel ()
    {
        return this.midiEditChannel;
    }


    /** {@inheritDoc} */
    @Override
    public void setMidiEditChannel (final int midiChannel)
    {
        final int mc = Math.max (0, Math.min (midiChannel, 15));
        this.midiEditChannelSetting.set (OPTIONS_MIDI_CHANNEL[mc]);
    }


    /**
     * Activate the scale setting.
     *
     * @param settingsUI The settings
     */
    protected void activateScaleSetting (final ISettingsUI settingsUI)
    {
        final String [] scaleNames = Scale.getNames ();
        this.scaleSetting = settingsUI.getEnumSetting ("Scale", CATEGORY_SCALES, scaleNames, scaleNames[0]);
        this.scaleSetting.addValueObserver (value -> {
            this.scale = value;
            this.notifyObservers (AbstractConfiguration.SCALES_SCALE);
        });
    }


    /**
     * Activate the scale base note setting.
     *
     * @param settingsUI The settings
     */
    protected void activateScaleBaseSetting (final ISettingsUI settingsUI)
    {
        this.scaleBaseSetting = settingsUI.getEnumSetting ("Base", CATEGORY_SCALES, Scales.BASES, Scales.BASES[0]);
        this.scaleBaseSetting.addValueObserver (value -> {
            this.scaleBase = value;
            this.notifyObservers (SCALES_BASE);
        });
    }


    /**
     * Activate the scale in-scale setting.
     *
     * @param settingsUI The settings
     */
    protected void activateScaleInScaleSetting (final ISettingsUI settingsUI)
    {
        this.scaleInKeySetting = settingsUI.getEnumSetting (SCALE_IN_KEY, CATEGORY_SCALES, new String []
        {
            SCALE_IN_KEY,
            SCALE_CHROMATIC
        }, SCALE_IN_KEY);
        this.scaleInKeySetting.addValueObserver (value -> {
            this.scaleInKey = SCALE_IN_KEY.equals (value);
            this.notifyObservers (AbstractConfiguration.SCALES_IN_KEY);
        });
    }


    /**
     * Activate the scale layout setting.
     *
     * @param settingsUI The settings
     */
    protected void activateScaleLayoutSetting (final ISettingsUI settingsUI)
    {
        this.activateScaleLayoutSetting (settingsUI, ScaleLayout.FOURTH_UP.getName ());
    }


    /**
     * Activate the scale layout setting.
     *
     * @param settingsUI The settings
     * @param defaultScale The name of the default scale to set
     */
    protected void activateScaleLayoutSetting (final ISettingsUI settingsUI, final String defaultScale)
    {
        final String [] names = ScaleLayout.getNames ();
        this.scaleLayoutSetting = settingsUI.getEnumSetting ("Layout", CATEGORY_SCALES, names, defaultScale);
        this.scaleLayoutSetting.addValueObserver (value -> {
            this.scaleLayout = value;
            this.notifyObservers (AbstractConfiguration.SCALES_LAYOUT);
        });
    }


    /**
     * Activate the VU meters setting.
     *
     * @param settingsUI The settings
     */
    protected void activateEnableVUMetersSetting (final ISettingsUI settingsUI)
    {
        this.activateEnableVUMetersSetting (settingsUI, CATEGORY_WORKFLOW);
    }


    /**
     * Activate the VU meters setting.
     *
     * @param settingsUI The settings
     * @param category The name for the category
     */
    protected void activateEnableVUMetersSetting (final ISettingsUI settingsUI, final String category)
    {
        this.enableVUMetersSetting = settingsUI.getEnumSetting ("VU Meters", category, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.enableVUMetersSetting.addValueObserver (value -> {
            this.enableVUMeters = "On".equals (value);
            this.notifyObservers (AbstractConfiguration.ENABLE_VU_METERS);
        });
    }


    /**
     * Activate the behaviour on stop setting.
     *
     * @param settingsUI The settings
     */
    protected void activateBehaviourOnStopSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting behaviourOnStopSetting = settingsUI.getEnumSetting ("Behaviour on Stop", CATEGORY_TRANSPORT, BEHAVIOUR_ON_STOP_VALUES, BEHAVIOUR_ON_STOP_VALUES[0]);
        behaviourOnStopSetting.addValueObserver (value -> {
            this.behaviourOnStop = BehaviourOnStop.values ()[lookupIndex (BEHAVIOUR_ON_STOP_VALUES, value)];
            this.notifyObservers (BEHAVIOUR_ON_STOP);
        });
    }


    /**
     * Activate the flip session setting.
     *
     * @param settingsUI The settings
     */
    protected void activateFlipSessionSetting (final ISettingsUI settingsUI)
    {
        this.flipSessionSetting = settingsUI.getEnumSetting ("Flip Session", CATEGORY_SESSION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.flipSessionSetting.addValueObserver (value -> {
            this.flipSession = "On".equals (value);
            this.notifyObservers (AbstractConfiguration.FLIP_SESSION);
        });
    }


    /**
     * Activate the select clip on launch setting.
     *
     * @param settingsUI The settings
     */
    protected void activateSelectClipOnLaunchSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting selectClipOnLaunchSetting = settingsUI.getEnumSetting ("Select clip/scene on launch", CATEGORY_SESSION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        selectClipOnLaunchSetting.addValueObserver (value -> {
            this.selectClipOnLaunch = "On".equals (value);
            this.notifyObservers (AbstractConfiguration.SELECT_CLIP_ON_LAUNCH);
        });
    }


    /**
     * Activate the draw record stripe setting.
     *
     * @param settingsUI The settings
     */
    protected void activateDrawRecordStripeSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting drawRecordStripeSetting = settingsUI.getEnumSetting ("Display clips of record enabled tracks in red", CATEGORY_SESSION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        drawRecordStripeSetting.addValueObserver (value -> {
            this.drawRecordStripe = "On".equals (value);
            this.notifyObservers (AbstractConfiguration.DRAW_RECORD_STRIPE);
        });
    }


    /**
     * Activate action for rec armed pad setting.
     *
     * @param settingsUI The settings
     */
    protected void activateActionForRecArmedPad (final ISettingsUI settingsUI)
    {
        final IEnumSetting actionForRecArmedPadSetting = settingsUI.getEnumSetting ("Action for pressing rec armed empty clip", CATEGORY_SESSION, ACTIONS_REC_ARMED_PADS, ACTIONS_REC_ARMED_PADS[0]);
        actionForRecArmedPadSetting.addValueObserver (value -> {
            this.actionForRecArmedPad = lookupIndex (ACTIONS_REC_ARMED_PADS, value);
            this.notifyObservers (AbstractConfiguration.ACTION_FOR_REC_ARMED_PAD);
        });
    }


    /**
     * Activate the convert aftertouch setting.
     *
     * @param settingsUI The settings
     */
    protected void activateConvertAftertouchSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting convertAftertouchSetting = settingsUI.getEnumSetting ("Convert Poly Aftertouch to", CATEGORY_PADS, AbstractConfiguration.AFTERTOUCH_CONVERSION_VALUES, AbstractConfiguration.AFTERTOUCH_CONVERSION_VALUES[1]);
        convertAftertouchSetting.addValueObserver (value -> {
            this.convertAftertouch = lookupIndex (AbstractConfiguration.AFTERTOUCH_CONVERSION_VALUES, value) - 3;
            this.notifyObservers (AbstractConfiguration.CONVERT_AFTERTOUCH);
        });
    }


    /**
     * Activate the accent active setting.
     *
     * @param settingsUI The settings
     */
    protected void activateAccentActiveSetting (final ISettingsUI settingsUI)
    {
        this.accentActiveSetting = settingsUI.getEnumSetting ("Activate Fixed Accent", CATEGORY_PLAY_AND_SEQUENCE, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.accentActiveSetting.addValueObserver (value -> {
            this.accentActive = "On".equals (value);
            this.notifyObservers (AbstractConfiguration.ACTIVATE_FIXED_ACCENT);
        });
    }


    /**
     * Activate the accent value setting.
     *
     * @param settingsUI The settings
     */
    protected void activateAccentValueSetting (final ISettingsUI settingsUI)
    {
        this.accentValueSetting = settingsUI.getRangeSetting ("Fixed Accent Value", CATEGORY_PLAY_AND_SEQUENCE, 1, 127, 1, "", 127);
        this.accentValueSetting.addValueObserver (value -> {
            this.fixedAccentValue = value.intValue ();
            this.notifyObservers (AbstractConfiguration.FIXED_ACCENT_VALUE);
        });
    }


    /**
     * Activate the flip arranger and clip record setting.
     *
     * @param settingsUI The settings
     */
    protected void activateFlipRecordSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting flipRecordSetting = settingsUI.getEnumSetting ("Flip arranger and clip record / automation", CATEGORY_TRANSPORT, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        flipRecordSetting.addValueObserver (value -> {
            this.flipRecord = "On".equals (value);
            this.notifyObservers (FLIP_RECORD);
        });
    }


    /**
     * Activate the include master setting.
     *
     * @param settingsUI The settings
     */
    protected void activateIncludeMasterSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting includeMasterSetting = settingsUI.getEnumSetting ("Include (Group-)Mastertrack (requires restart)", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.includeMaster = "On".equals (includeMasterSetting.get ());
    }


    /**
     * Activate the exclude deactovated tracks setting.
     *
     * @param settingsUI The settings
     */
    protected void activateExcludeDeactivatedItemsSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting includeMasterSetting = settingsUI.getEnumSetting ("Exclude deactivated items (tracks, sends, devices, layers)", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        includeMasterSetting.addValueObserver (value -> {
            this.excludeDeactivatedItems = ON_OFF_OPTIONS[1].equals (value);
            this.notifyObservers (EXCLUDE_DEACTIVATED_ITEMS);
        });
    }


    /**
     * Activate the accent value setting.
     *
     * @param settingsUI The settings
     */
    protected void activateNewClipLengthSetting (final ISettingsUI settingsUI)
    {
        this.newClipLengthSetting = settingsUI.getEnumSetting ("New Clip Length", CATEGORY_WORKFLOW, NEW_CLIP_LENGTH_VALUES, NEW_CLIP_LENGTH_VALUES[2]);
        this.newClipLengthSetting.addValueObserver (value -> {
            this.newClipLength = lookupIndex (NEW_CLIP_LENGTH_VALUES, value);
            this.notifyObservers (NEW_CLIP_LENGTH);
        });
    }


    /**
     * Activate the quantize amount setting.
     *
     * @param settingsUI The settings
     */
    protected void activateQuantizeAmountSetting (final ISettingsUI settingsUI)
    {
        this.quantizeAmountSetting = settingsUI.getRangeSetting ("Quantize Amount", CATEGORY_PLAY_AND_SEQUENCE, 1, 100, 1, "%", 100);
        this.quantizeAmountSetting.addValueObserver (value -> {
            this.quantizeAmount = value.intValue ();
            this.notifyObservers (QUANTIZE_AMOUNT);
        });
    }


    /**
     * Activate the MIDI edit channel setting.
     *
     * @param settingsUI The settings
     */
    protected void activateMidiEditChannelSetting (final ISettingsUI settingsUI)
    {
        this.midiEditChannelSetting = settingsUI.getEnumSetting ("MIDI Edit/Insert note channel", CATEGORY_PLAY_AND_SEQUENCE, OPTIONS_MIDI_CHANNEL, OPTIONS_MIDI_CHANNEL[0]);
        this.midiEditChannelSetting.addValueObserver (value -> {
            this.midiEditChannel = Integer.parseInt (value) - 1;
            this.notifyObservers (MIDI_EDIT_CHANNEL);
        });
    }


    /**
     * Activate the auto select drum setting.
     *
     * @param settingsUI The settings
     */
    protected void activateAutoSelectDrumSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting autoSelectDrumSetting = settingsUI.getEnumSetting ("Auto-select drum settings", CATEGORY_DRUMS, new String []
        {
            "Off",
            "Channel"
        }, "Off");
        autoSelectDrumSetting.addValueObserver (value -> {
            this.autoSelectDrum = "Channel".equals (value);
            this.notifyObservers (AUTO_SELECT_DRUM);
        });
    }


    /**
     * Activate the turn off empty drum pads setting.
     *
     * @param settingsUI The settings
     */
    protected void activateTurnOffEmptyDrumPadsSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting turnOffEmptyDrumPadsSetting = settingsUI.getEnumSetting ("Turn off empty drum pads", CATEGORY_DRUMS, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        turnOffEmptyDrumPadsSetting.addValueObserver (value -> {
            this.turnOffEmptyDrumPads = "On".equals (value);
            this.notifyObservers (TURN_OFF_EMPTY_DRUM_PADS);
        });
    }


    /**
     * Activate the footswitch setting.
     *
     * @param settingsUI The settings
     */
    protected void activateFootswitchSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting footswitch2Setting = settingsUI.getEnumSetting ("Footswitch 2", CATEGORY_WORKFLOW, FOOTSWITCH_VALUES, FOOTSWITCH_VALUES[6]);
        footswitch2Setting.addValueObserver (value -> {
            this.footswitch2 = lookupIndex (FOOTSWITCH_VALUES, value);
            this.notifyObservers (FOOTSWITCH_2);
        });
    }


    /**
     * Activate the browser settings.
     *
     * @param settingsUI The settings
     */
    protected void activateBrowserSettings (final ISettingsUI settingsUI)
    {
        for (int i = 0; i < BROWSER_FILTER_COLUMN_NAMES.length; i++)
        {
            final IEnumSetting browserDisplayFilterSetting = settingsUI.getEnumSetting (BROWSER_FILTER_COLUMN_NAMES[i], "Browser", COLUMN_VALUES, COLUMN_VALUES[1]);
            final int index = i;
            browserDisplayFilterSetting.addValueObserver (value -> {
                this.browserDisplayFilter[index] = COLUMN_VALUES[1].equals (value);
                this.notifyObservers (Integer.valueOf (BROWSER_DISPLAY_FILTER1.intValue () + index));
            });
        }
    }


    /**
     * Activate the knob speed settings.
     *
     * @param settingsUI The settings
     */
    protected void activateKnobSpeedSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting knobSpeedNormalSetting = settingsUI.getEnumSetting ("Knob Sensitivity Default", CATEGORY_WORKFLOW, KNOB_SENSITIVITY, KNOB_SENSITIVITY[100]);
        knobSpeedNormalSetting.addValueObserver (value -> {
            this.knobSpeedDefault = lookupIndex (KNOB_SENSITIVITY, value) - 100;
            this.notifyObservers (AbstractConfiguration.KNOB_SENSITIVITY_DEFAULT);
        });
        final IEnumSetting knobSpeedSlowSetting = settingsUI.getEnumSetting ("Knob Sensitivity Slow", CATEGORY_WORKFLOW, KNOB_SENSITIVITY, KNOB_SENSITIVITY[60]);
        knobSpeedSlowSetting.addValueObserver (value -> {
            this.knobSpeedSlow = lookupIndex (KNOB_SENSITIVITY, value) - 100;
            this.notifyObservers (AbstractConfiguration.KNOB_SENSITIVITY_SLOW);
        });
    }


    /**
     * Activate the note repeat settings.
     *
     * @param settingsUI The settings
     */
    protected void activateNoteRepeatSetting (final ISettingsUI settingsUI)
    {
        this.noteRepeatActiveSetting = settingsUI.getEnumSetting ("Active", CATEGORY_NOTEREPEAT, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.noteRepeatActiveSetting.addValueObserver (value -> {
            this.noteRepeatActive = "On".equals (value);
            this.notifyObservers (AbstractConfiguration.NOTEREPEAT_ACTIVE);
        });

        final String [] names = Resolution.getNames ();

        this.noteRepeatPeriodSetting = settingsUI.getEnumSetting ("Period", CATEGORY_NOTEREPEAT, names, names[4]);
        this.noteRepeatPeriodSetting.addValueObserver (value -> {
            this.noteRepeatPeriod = Resolution.getByName (value);
            this.notifyObservers (AbstractConfiguration.NOTEREPEAT_PERIOD);
        });

        if (this.host.canEdit (EditCapability.NOTE_REPEAT_LENGTH))
        {
            this.noteRepeatLengthSetting = settingsUI.getEnumSetting ("Length", CATEGORY_NOTEREPEAT, names, names[4]);
            this.noteRepeatLengthSetting.addValueObserver (value -> {
                this.noteRepeatLength = Resolution.getByName (value);
                this.notifyObservers (AbstractConfiguration.NOTEREPEAT_LENGTH);
            });
        }

        if (this.host.canEdit (EditCapability.NOTE_REPEAT_MODE))
        {
            final String [] arpModeNames = new String [this.arpeggiatorModes.length];
            for (int i = 0; i < this.arpeggiatorModes.length; i++)
                arpModeNames[i] = this.arpeggiatorModes[i].getName ();

            this.noteRepeatModeSetting = settingsUI.getEnumSetting ("Mode", CATEGORY_NOTEREPEAT, arpModeNames, arpModeNames[1]);
            this.noteRepeatModeSetting.addValueObserver (value -> {
                this.noteRepeatMode = ArpeggiatorMode.lookupByName (value);
                this.notifyObservers (AbstractConfiguration.NOTEREPEAT_MODE);
            });
        }

        if (this.host.canEdit (EditCapability.NOTE_REPEAT_OCTAVES))
        {
            final String [] octaves =
            {
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8"
            };

            this.noteRepeatOctaveSetting = settingsUI.getEnumSetting ("Octave", CATEGORY_NOTEREPEAT, octaves, octaves[0]);
            this.noteRepeatOctaveSetting.addValueObserver (value -> {
                this.noteRepeatOctave = Integer.parseInt (value);
                this.notifyObservers (AbstractConfiguration.NOTEREPEAT_OCTAVE);
            });
        }
    }


    /**
     * Activate the settings for naming the user pages.
     *
     * @param settingsUI The settings
     */
    protected void activateUserPageNamesSetting (final ISettingsUI settingsUI)
    {
        for (int i = 0; i < 8; i++)
        {
            final int index = i;
            settingsUI.getStringSetting ("User Page " + (i + 1), CATEGORY_WORKFLOW, 10, "Page " + (i + 1)).addValueObserver (value -> this.userPageNames[index] = value);
        }
    }


    /**
     * Notify all observers about the change of a setting.
     *
     * @param settingID The ID of the setting, which has changed
     */
    protected void notifyObservers (final Integer settingID)
    {
        final Set<ISettingObserver> set = this.observers.get (settingID);
        if (set != null)
            set.forEach (ISettingObserver::hasChanged);
    }


    /**
     * Register a handler for the 'exclude deactivated items' setting.
     *
     * @param model The model for getting the banks to configure
     */
    public void registerDeactivatedItemsHandler (final IModel model)
    {
        this.addSettingObserver (AbstractConfiguration.EXCLUDE_DEACTIVATED_ITEMS, () -> {
            final boolean exclude = this.areDeactivatedItemsExcluded ();
            final ITrackBank trackBank = model.getTrackBank ();
            trackBank.setSkipDisabledItems (exclude);
            for (int i = 0; i < trackBank.getPageSize (); i++)
                trackBank.getItem (i).getSendBank ().setSkipDisabledItems (exclude);
            final ITrackBank effectTrackBank = model.getEffectTrackBank ();
            if (effectTrackBank != null)
                effectTrackBank.setSkipDisabledItems (exclude);
            final ICursorDevice cursorDevice = model.getCursorDevice ();
            final IDeviceBank deviceBank = cursorDevice.getDeviceBank ();
            deviceBank.setSkipDisabledItems (exclude);
            cursorDevice.getLayerBank ().setSkipDisabledItems (exclude);
            final IDrumPadBank drumPadBank = cursorDevice.getDrumPadBank ();
            if (drumPadBank != null)
                drumPadBank.setSkipDisabledItems (exclude);
        });
    }


    /**
     * Lookup the index of the value in the given options array.
     *
     * @param options The options in which to search for the value
     * @param value The value to search for
     * @return The index or 0 if not found
     */
    public static int lookupIndex (final String [] options, final String value)
    {
        for (int i = 0; i < options.length; i++)
        {
            if (options[i].equals (value))
                return i;
        }
        return 0;
    }


    /**
     * Get a new clip length value string.
     *
     * @param index The index
     * @return The text
     */
    public static String getNewClipLengthValue (final int index)
    {
        return NEW_CLIP_LENGTH_VALUES[index];
    }


    /** {@inheritDoc} */
    @Override
    public int lookupArpeggiatorModeIndex (final ArpeggiatorMode arpMode)
    {
        for (int i = 0; i < this.arpeggiatorModes.length; i++)
        {
            if (this.arpeggiatorModes[i] == arpMode)
                return i;
        }
        return 0;
    }


    /**
     * Get the next arpeggiator mode.
     *
     * @return The next
     */
    public ArpeggiatorMode nextArpeggiatorMode ()
    {
        final ArpeggiatorMode arpMode = this.getNoteRepeatMode ();
        int index = this.lookupArpeggiatorModeIndex (arpMode) + 1;
        if (index >= this.arpeggiatorModes.length)
            index = 0;
        return this.arpeggiatorModes[index];
    }


    /**
     * Get the previous arpeggiator mode.
     *
     * @return The previous
     */
    public ArpeggiatorMode prevArpeggiatorMode ()
    {
        final ArpeggiatorMode arpMode = this.getNoteRepeatMode ();
        int index = this.lookupArpeggiatorModeIndex (arpMode) - 1;
        if (index < 0)
            index = this.arpeggiatorModes.length - 1;
        return this.arpeggiatorModes[index];
    }


    /** {@inheritDoc} */
    @Override
    public ArpeggiatorMode [] getArpeggiatorModes ()
    {
        return this.arpeggiatorModes;
    }


    /**
     * Should the master track and group-master tracks be included in the track list?
     *
     * @return True if they should be included
     */
    public boolean areMasterTracksIncluded ()
    {
        return this.includeMaster;
    }


    /**
     * Should deactivated tracks be included in the track list?
     *
     * @return False if they should be included
     */
    public boolean areDeactivatedItemsExcluded ()
    {
        return this.excludeDeactivatedItems;
    }


    /**
     * Get the user page names.
     *
     * @return The user page names
     */
    public String [] getUserPageNames ()
    {
        return this.userPageNames;
    }
}
