package com.xontext.pmp.arc;

public class VestimentaryPreference implements Preference{
    private String preference;
    public VestimentaryPreference(String preference){
        setPreference(preference);
    }
    @Override
    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference){
        this.preference = preference;
    }
}
