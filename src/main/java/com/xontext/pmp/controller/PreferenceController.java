package com.xontext.pmp.controller;

import com.xontext.pmp.arc.Preference;
import com.xontext.pmp.arc.VestimentaryPreference;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PreferenceController {
    @GetMapping("/preferences/{email}")
    public Preference getPreferenceByEmail(@PathVariable String email){
        return findPreferenceByEmail(email);
    }
    @GetMapping("/")
    public Preference getPreference(){
        return findPreference();
    }

    private Preference findPreferenceByEmail(String email){
        return new VestimentaryPreference("Green shoes");
    }
    private Preference findPreference(){
        return new VestimentaryPreference("Green shoes");
    }
}
