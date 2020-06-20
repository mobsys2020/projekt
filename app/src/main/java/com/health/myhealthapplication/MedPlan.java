package com.health.myhealthapplication;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ole Hannemann
 * @author Sam Wolter
 */
public class MedPlan {
    public String patient = "";
    public String doctor = "";
    public int medcount = 0;
    public List<Meds> meds = new ArrayList<Meds>();

    public MedPlan() {
    }

    public MedPlan(String patient, String doctor, int medcount, ArrayList<Meds> meds) {
        this.patient = patient;
        this.doctor = doctor;
        this.medcount = medcount;
        this.meds = meds;
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

    public List<Meds> getMeds() {
        return meds;
    }

    public void setMeds(ArrayList<Meds> meds) {
        this.meds = meds;
    }
}
