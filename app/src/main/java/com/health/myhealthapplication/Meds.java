package com.health.myhealthapplication;

import com.orm.SugarRecord;

/**
 * @author Ole Hannemann
 * @author Sam Wolter
 */
public class Meds extends SugarRecord<Meds> {
    public String name = "";
    public String quantity = "";
    public String time = "";
    public String days = "";


    //constructors
    public Meds() {
    }

    public Meds(String name, String quantity, String time, String days) {
        this.name = name;
        this.quantity = quantity;
        this.time = time;
        this.days = days;
    }

    //GETTERS AND SETTERS
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }


}