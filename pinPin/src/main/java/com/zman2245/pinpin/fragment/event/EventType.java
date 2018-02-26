package com.zman2245.pinpin.fragment.event;

/**
 * An enumeration of possible fragment events
 *
 * @author Zack
 */
public enum EventType
{
    QUIZ_LISTITEM_CLICK("quiz-listitem-click"),
    QUIZ_END("quiz-end"),
    QUIZ_CONTINUE("quiz-continue"),

    LEARN_END("learn-end"),
    STUDY_START("study-start"),

    INAPP_PURCHASED("inapp-purchased"),
    INAPP_PURCHASED_FAILED("inapp-purchased-failed"),
    INAPP_SHOW_STORE("inapp-show-store"),

    INAPP_INFO("inapp-info"),

    MARK_LEARN_PROGRESS("mark-learn-progress"),
    MARK_QUIZ_PROGRESS("mark-quiz-progress");

    public String name;

    private EventType(String name)
    {
        this.name = name;
    }
}
