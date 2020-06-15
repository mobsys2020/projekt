package com.health.myhealthapplication;

/**
 * Created by User on 3/14/2017.
 */

public class Medicine {
    private String name;
    private String datetime;
    private String amount;

    public Medicine(String name, String datetime, String amount) {
        this.datetime = datetime;
        this.name = name;
        this.amount = amount;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}