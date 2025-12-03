/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.einfuehrungsprojekt;

/**
 *
 * @author Firma
 */
public class Patientendaten {
    
    String id;
    String vorname;
    String nachname;
    String svn;
    String geschlecht;
    String telefon;
    int alter;
    String erstKontakt;
    int gewicht;
    String DocDate;

    public Patientendaten() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVorname(String vorName) {
        this.vorname = vorName;
    }

    public void setNachname(String nachName) {
        this.nachname = nachName;
    }

    public void setSvn(String svn) {
        this.svn = svn;
    }

    public void setGeschlecht(String geschlecht) {
        this.geschlecht = geschlecht;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public void setAlter(int alter) {
        this.alter = alter;
    }

    public void setErstKontakt(String erstKontakt) {
        this.erstKontakt = erstKontakt;
    }

    public void setGewicht(int gewicht) {
        this.gewicht = gewicht;
    }

    public void setDocDate(String DocDate) {
        this.DocDate = DocDate;
    }
    
    
    
    public void writeAll(){
        System.out.println("ID: "+ id+ "\n" +
                            "Vorname: " + vorname+ "\n" +
                            "Nachname: "+ nachname + "\n" +
                            "svn: "+ svn + "\n" +
                            "Geschlecht: "+ geschlecht + "\n" +
                            "Telefon: "+ telefon + "\n" +
                            "Alter: "+ alter + "\n" +
                            "Kontakt: "+ erstKontakt + "\n" +
                            "Gewicht: "+gewicht);
    }

    public String getId() {
        return id;
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public String getSvn() {
        return svn;
    }

    public String getGeschlecht() {
        return geschlecht;
    }

    public String getTelefon() {
        return telefon;
    }

    public int getAlter() {
        return alter;
    }

    public String getErstKontakt() {
        return erstKontakt;
    }

    public int getGewicht() {
        return gewicht;
    }

    public String getDocDate() {
        return DocDate;
    }

    
    
    
    
    
}
