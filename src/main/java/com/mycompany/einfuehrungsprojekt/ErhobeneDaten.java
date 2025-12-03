/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.einfuehrungsprojekt;

import java.util.ArrayList;

/**
 *
 * @author Firma
 */
public class ErhobeneDaten {
    String gewichtsVerlauf;
    String symptome;
    ArrayList<String> hauptDiagnosen= new ArrayList<>();
    String eigeneHauptDiagnose;
    String zusammenfassungHauptDiagnose;
    String nebenDiagnose;
    String eigeneNebenDiagnose;
    String zusammenfassungNebenDiagnose;
    String zuweisung;
    String betreuungsZeitraum;
    String klientenPerspektive;
    String serumProteinStatus;
    double hbA1c;
    String tagesProfil;
    String ernährungsProbleme;
    String diatZiele;
    int zielEnergie;
    String diatologischeDiagnose;
    String individuelErnahrung;
    String parenteraleErnahrung;
    String trinkNahrung;
    String inBehandlung;

    public ErhobeneDaten() {
    }

    public void setGewichtsVerlauf(String gewichtsVerlauf) {
        this.gewichtsVerlauf = gewichtsVerlauf;
    }

    public void setSymptome(String symptome) {
        this.symptome = symptome;
    }

    public void addHauptDiagnosen(String hauptDiagnose) {
        hauptDiagnosen.add(hauptDiagnose);
    }

    public void setEigeneHauptDiagnose(String eigeneHauptDiagnose) {
        this.eigeneHauptDiagnose = eigeneHauptDiagnose;
    }

    public void setZusammenfassungHauptDiagnose(String zusammenfassungHauptDiagnose) {
        this.zusammenfassungHauptDiagnose = zusammenfassungHauptDiagnose;
    }

    public void setNebenDiagnose(String nebenDiagnose) {
        this.nebenDiagnose = nebenDiagnose;
    }

    public void setEigeneNebenDiagnose(String eigeneNebenDiagnose) {
        this.eigeneNebenDiagnose = eigeneNebenDiagnose;
    }

    public void setZusammenfassungNebenDiagnose(String zusammenfassungNebenDiagnose) {
        this.zusammenfassungNebenDiagnose = zusammenfassungNebenDiagnose;
    }

    public void setZuweisung(String zuweisung) {
        this.zuweisung = zuweisung;
    }

    public void setBetreuungsZeitraum(String betreuungsZeitraum) {
        this.betreuungsZeitraum = betreuungsZeitraum;
    }

    public void setKlientenPerspektive(String klientenPerspektive) {
        this.klientenPerspektive = klientenPerspektive;
    }

    public void setSerumProteinStatus(String serumProteinStatus) {
        this.serumProteinStatus = serumProteinStatus;
    }

    public void setHbA1c(double hbA1c) {
        this.hbA1c = hbA1c;
    }

    public void setTagesProfil(String tagesProfil) {
        this.tagesProfil = tagesProfil;
    }

    public void setErnährungsProbleme(String ernährungsProbleme) {
        this.ernährungsProbleme = ernährungsProbleme;
    }

    public void setDiatZiele(String diatZiele) {
        this.diatZiele = diatZiele;
    }

    public void setZielEnergie(int zielEnergie) {
        this.zielEnergie = zielEnergie;
    }

    public void setDiatologischeDiagnose(String diatologischeDiagnose) {
        this.diatologischeDiagnose = diatologischeDiagnose;
    }

    public void setIndividuelErnahrung(String individuelErnahrung) {
        this.individuelErnahrung = individuelErnahrung;
    }

    public void setParenteraleErnahrung(String parenteraleErnahrung) {
        this.parenteraleErnahrung = parenteraleErnahrung;
    }

    public void setTrinkNahrung(String trinkNahrung) {
        this.trinkNahrung = trinkNahrung;
    }

    public void setInBehandlung(String inBehandlung) {
        this.inBehandlung = inBehandlung;
    }

    public String getGewichtsVerlauf() {
        return gewichtsVerlauf;
    }

    public String getSymptome() {
        return symptome;
    }

    public ArrayList<String> getHauptDiagnosen() {
        return hauptDiagnosen;
    }

    

    public String getEigeneHauptDiagnose() {
        return eigeneHauptDiagnose;
    }

    public String getZusammenfassungHauptDiagnose() {
        return zusammenfassungHauptDiagnose;
    }

    public String getNebenDiagnose() {
        return nebenDiagnose;
    }

    public String getEigeneNebenDiagnose() {
        return eigeneNebenDiagnose;
    }

    public String getZusammenfassungNebenDiagnose() {
        return zusammenfassungNebenDiagnose;
    }

    public String getZuweisung() {
        return zuweisung;
    }

    public String getBetreuungsZeitraum() {
        return betreuungsZeitraum;
    }

    public String getKlientenPerspektive() {
        return klientenPerspektive;
    }

    public String getSerumProteinStatus() {
        return serumProteinStatus;
    }

    public double getHbA1c() {
        return hbA1c;
    }

    public String getTagesProfil() {
        return tagesProfil;
    }

    public String getErnährungsProbleme() {
        return ernährungsProbleme;
    }

    public String getDiatZiele() {
        return diatZiele;
    }

    public int getZielEnergie() {
        return zielEnergie;
    }

    public String getDiatologischeDiagnose() {
        return diatologischeDiagnose;
    }

    public String getIndividuelErnahrung() {
        return individuelErnahrung;
    }

    public String getParenteraleErnahrung() {
        return parenteraleErnahrung;
    }

    public String getTrinkNahrung() {
        return trinkNahrung;
    }

    public String getInBehandlung() {
        return inBehandlung;
    }
    
    
    
    
}
