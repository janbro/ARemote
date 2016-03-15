package com.example.alexa.objsense;

/**
 * Created by Alexa on 3/12/2016.
 */
public class HouseholeObject {

    private String UUID;
    private String menuOptions[];

    public HouseholeObject(String UUID, String... options){
        this.UUID = UUID;
        this.menuOptions = options;
    }
}
