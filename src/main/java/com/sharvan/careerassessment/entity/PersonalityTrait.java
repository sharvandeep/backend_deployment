package com.sharvan.careerassessment.entity;

/**
 * Big Five Personality Traits (OCEAN Model)
 * Used for personality assessment and career matching
 */
public enum PersonalityTrait {
    
    OPENNESS("Openness to Experience", 
             "Creative, curious, open to new ideas",
             "Prefer routine, practical, conventional"),
    
    CONSCIENTIOUSNESS("Conscientiousness", 
                      "Organized, dependable, disciplined",
                      "Flexible, spontaneous, careless"),
    
    EXTRAVERSION("Extraversion", 
                 "Outgoing, energetic, talkative",
                 "Reserved, introverted, reflective"),
    
    AGREEABLENESS("Agreeableness", 
                  "Friendly, compassionate, cooperative",
                  "Competitive, challenging, detached"),
    
    NEUROTICISM("Emotional Stability", 
                "Calm, confident, secure",
                "Anxious, sensitive, nervous");

    private final String displayName;
    private final String highDescription;
    private final String lowDescription;

    PersonalityTrait(String displayName, String highDescription, String lowDescription) {
        this.displayName = displayName;
        this.highDescription = highDescription;
        this.lowDescription = lowDescription;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHighDescription() {
        return highDescription;
    }

    public String getLowDescription() {
        return lowDescription;
    }
}
