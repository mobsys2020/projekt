package com.health.myhealthapplication;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class Medplaninfo extends SugarRecord<Medplaninfo> {
    public String patient = "";
    public String doctor = "";
    public int medcount = 0;

    public Medplaninfo() {
    }

    public Medplaninfo(String patient, String doctor, int medcount) {
        this.patient = patient;
        this.doctor = doctor;
        this.medcount = medcount;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public int getMedcount() {
        return medcount;
    }

    public void setMedcount(int medcount) {
        this.medcount = medcount;
    }
}
