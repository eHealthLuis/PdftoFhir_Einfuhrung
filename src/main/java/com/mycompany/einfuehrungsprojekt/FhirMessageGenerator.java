/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.einfuehrungsprojekt;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class FhirMessageGenerator {

    private FhirContext ctx;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Patientendaten pat;
    Praxisdaten prax;
    ErhobeneDaten ed;

    public FhirMessageGenerator(Patientendaten pat, Praxisdaten prax, ErhobeneDaten ed) {
        this.ctx = FhirContext.forR4();
        this.pat = pat;
        this.prax = prax;
        this.ed = ed;
    }

    public Bundle createFhirBundle() {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.COLLECTION);
        bundle.setTimestamp(new Date());


        // 1. Patient erstellen
        Patient patient = createPatient();
        bundle.addEntry()
                .setFullUrl(generateFullUrl())
                .setResource(patient);

        // 2. Organization (Praxis) erstellen
        Organization organization = createOrganization();
        bundle.addEntry()
                .setFullUrl(generateFullUrl())
                .setResource(organization);

        // 3. Hauptdiagnose Vielleicht Mehr
        for (String diagnose : ed.hauptDiagnosen) {
            Condition hauptdiagnose = createCondition(patient, diagnose);
            bundle.addEntry()
                    .setFullUrl(generateFullUrl())
                    .setResource(hauptdiagnose);
        }

        /* 4. Nebendiagnose: Eisenmangel
        Condition nebendiagnose1 = createCondition(
                patient,
                ed.getNebenDiagnose()
        );
        bundle.addEntry()
                .setFullUrl("Condition/" + nebendiagnose1.getId())
                .setResource(nebendiagnose1);
         */
        // Mehr Diagnosen
        // 7. Observations - Gewichtsverlauf    JAHR:GEWICHT - JAHR:GEWICHT - JAHR:GEWICHT
        // 8. HbA1c-Wert
        Observation hba1c = createHbA1cObservation(patient, pat.getDocDate(), ed.getHbA1c());
        bundle.addEntry()
                .setFullUrl(generateFullUrl())
                .setResource(hba1c);

        // 9. BMI Observation
        // 10. DiagnosticReport für den gesamten Therapiebericht
        DiagnosticReport report = createDiagnosticReport(
                patient,
                organization,
                pat.getDocDate()
        );
        bundle.addEntry()
                .setFullUrl(generateFullUrl())
                .setResource(report);

        return bundle;
    }

    private String generateFullUrl() {
        return "urn:uuid:" + UUID.randomUUID().toString().toLowerCase();
    }

    private Patient createPatient() {
        Patient patient = new Patient();
        patient.setId("patient" + pat.getId());

        // Name
        HumanName name = new HumanName();
        name.setFamily(pat.getNachname());
        name.addGiven(pat.getVorname());
        name.setUse(HumanName.NameUse.OFFICIAL);
        patient.addName(name);

        // Klienten-ID
        Identifier klientenId = new Identifier();
        klientenId.setSystem("urn:oid:1.2.40.0.10.99.1");
        klientenId.setValue(pat.getId());
        klientenId.setType(new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                        .setCode("MR")
                        .setDisplay("Medical Record Number")));
        patient.addIdentifier(klientenId);

        // Österreichische Sozialversicherungsnummer
        Identifier svnr = new Identifier();
        svnr.setSystem("urn:oid:1.2.40.0.10.1.4.3.1");
        svnr.setValue(pat.getSvn());
        svnr.setType(new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                        .setCode("SB")
                        .setDisplay("Social Beneficiary Identifier")));
        patient.addIdentifier(svnr);

        // Telefonnummer
        ContactPoint phone = new ContactPoint();
        phone.setSystem(ContactPoint.ContactPointSystem.PHONE);

        phone.setValue(pat.getTelefon());
        phone.setUse(ContactPoint.ContactPointUse.HOME);
        patient.addTelecom(phone);

        // Geschlecht
        if (pat.getGeschlecht().equals("w")) {
            patient.setGender(Enumerations.AdministrativeGender.FEMALE);
        } else if (pat.geschlecht.equals("m")) {
            patient.setGender(Enumerations.AdministrativeGender.MALE);
        } else {
            patient.setGender(Enumerations.AdministrativeGender.OTHER);
        }

        // Alter: 45 Jahre (geschätztes Geburtsdatum: 1980)
        try {
            System.out.println("GebDatum Line 199 MEssageGenerator: " + pat.getAlter());
            patient.setBirthDate(sdf.parse("1980-01-01"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return patient;
    }

    private Organization createOrganization() {
        Organization org = new Organization();
        org.setId("org-praxis");

        // Österreichisches Organization-Profil
        Meta meta = new Meta();
        meta.addProfile("http://hl7.at/fhir/HL7Austria/r4/StructureDefinition/at-core-organization");
        org.setMeta(meta);

        org.setName(prax.getName());
        org.addType(new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/organization-type")
                        .setCode("prov")
                        .setDisplay("Healthcare Provider")));

        return org;
    }

    private Condition createCondition(Patient patient, String diagnosis) {
        Condition condition = new Condition();
        int conditionCounter = 1;
        condition.setId("condition-" + conditionCounter++);

        // Österreichisches Condition-Profil
        Meta meta = new Meta();
        meta.addProfile("http://hl7.at/fhir/HL7Austria/r4/StructureDefinition/at-core-condition");
        condition.setMeta(meta);

        condition.setSubject(new Reference("Patient/" + patient.getId()));

        // ICD-10 Code (Österreich)
        CodeableConcept code = new CodeableConcept();
        Coding coding = new Coding();
        coding.setSystem("http://hl7.at/fhir/HL7Austria/r4/CodeSystem/icd-10-at");
        coding.setDisplay(diagnosis);
        code.addCoding(coding);
        code.setText(diagnosis);
        condition.setCode(code);

        // Clinical Status
        condition.setClinicalStatus(new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/condition-clinical")
                        .setCode("active")
                        .setDisplay("Active")));

        // Verification Status
        condition.setVerificationStatus(new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/condition-ver-status")
                        .setCode("confirmed")
                        .setDisplay("Confirmed")));

        condition.setRecordedDate(new Date());

        return condition;
    }

    private Observation createWeightObservation(Patient patient, String date, double weight) {
        Observation obs = new Observation();
        obs.setId(IdType.newRandomUuid());

        // Österreichisches Observation-Profil
        Meta meta = new Meta();
        meta.addProfile("http://hl7.at/fhir/HL7Austria/r4/StructureDefinition/at-core-observation");
        obs.setMeta(meta);

        obs.setStatus(Observation.ObservationStatus.FINAL);
        obs.setSubject(new Reference("Patient/" + patient.getId()));

        // LOINC Code für Körpergewicht
        CodeableConcept code = new CodeableConcept();
        code.addCoding(new Coding()
                .setSystem("http://loinc.org")
                .setCode("29463-7")
                .setDisplay("Body weight"));
        code.setText("Gewicht");
        obs.setCode(code);

        // Kategorie
        obs.addCategory(new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                        .setCode("vital-signs")
                        .setDisplay("Vital Signs")));

        try {
            obs.setEffective(new DateTimeType(sdf.parse(date)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Quantity quantity = new Quantity();
        quantity.setValue(weight);
        quantity.setUnit("kg");
        quantity.setSystem("http://unitsofmeasure.org");
        quantity.setCode("kg");
        obs.setValue(quantity);

        return obs;
    }

    private Observation createHbA1cObservation(Patient patient, String date, double value) {
        Observation obs = new Observation();
        obs.setId(IdType.newRandomUuid());

        // Österreichisches Observation-Profil
        Meta meta = new Meta();
        meta.addProfile("http://hl7.at/fhir/HL7Austria/r4/StructureDefinition/at-core-observation");
        obs.setMeta(meta);

        obs.setStatus(Observation.ObservationStatus.FINAL);
        obs.setSubject(new Reference("Patient/" + patient.getId()));

        // LOINC Code für HbA1c
        CodeableConcept code = new CodeableConcept();
        code.addCoding(new Coding()
                .setSystem("http://loinc.org")
                .setCode("4548-4")
                .setDisplay("Hemoglobin A1c/Hemoglobin.total in Blood"));
        code.setText("HbA1c");
        obs.setCode(code);

        // Kategorie
        obs.addCategory(new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                        .setCode("laboratory")
                        .setDisplay("Laboratory")));

        try {
            obs.setEffective(new DateTimeType(sdf.parse(date)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Quantity quantity = new Quantity();
        quantity.setValue(value);
        quantity.setUnit("%");
        quantity.setSystem("http://unitsofmeasure.org");
        quantity.setCode("%");
        obs.setValue(quantity);

        return obs;
    }

    private Observation createBmiObservation(Patient patient, String date, double bmiValue) {
        Observation obs = new Observation();
        obs.setId(IdType.newRandomUuid());

        // Österreichisches Observation-Profil
        Meta meta = new Meta();
        meta.addProfile("http://hl7.at/fhir/HL7Austria/r4/StructureDefinition/at-core-observation");
        obs.setMeta(meta);

        obs.setStatus(Observation.ObservationStatus.FINAL);
        obs.setSubject(new Reference("Patient/" + patient.getId()));

        // LOINC Code für BMI
        CodeableConcept code = new CodeableConcept();
        code.addCoding(new Coding()
                .setSystem("http://loinc.org")
                .setCode("39156-5")
                .setDisplay("Body mass index (BMI) [Ratio]"));
        code.setText("BMI");
        obs.setCode(code);

        // Kategorie
        obs.addCategory(new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                        .setCode("vital-signs")
                        .setDisplay("Vital Signs")));

        try {
            obs.setEffective(new DateTimeType(sdf.parse(date)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Quantity quantity = new Quantity();
        quantity.setValue(bmiValue);
        quantity.setUnit("kg/m2");
        quantity.setSystem("http://unitsofmeasure.org");
        quantity.setCode("kg/m2");
        obs.setValue(quantity);

        return obs;
    }

    private DiagnosticReport createDiagnosticReport(Patient patient,
            Organization org,
            String date) {
        DiagnosticReport report = new DiagnosticReport();
        report.setId(IdType.newRandomUuid());

        // Österreichisches DiagnosticReport-Profil
        Meta meta = new Meta();
        meta.addProfile("http://hl7.at/fhir/HL7Austria/r4/StructureDefinition/at-core-diagnosticreport");
        report.setMeta(meta);

        report.setStatus(DiagnosticReport.DiagnosticReportStatus.FINAL);
        report.setSubject(new Reference("Patient/" + patient.getId()));

        CodeableConcept code = new CodeableConcept();
        code.setText("Therapiebericht - Ernährungsberatung");
        report.setCode(code);

        try {
            report.setEffective(new DateTimeType(sdf.parse(date)));
            report.setIssued(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Conclusion Text
        report.setConclusion("Therapiebericht vom " + date
                + " - Adipositas Grad II mit Begleitdiagnosen. "
                + "Gewichtsverlauf zeigt kontinuierliche Zunahme. "
                + "Ernährungsumstellung und Bewegungstherapie empfohlen.");

        return report;
    }

    /**
     * Gibt das FHIR Bundle als JSON String zurück
     */
    public String toJson() {
        Bundle bundle = createFhirBundle();
        IParser parser = ctx.newJsonParser().setPrettyPrint(true);
        return parser.encodeResourceToString(bundle);
    }

    /**
     * Gibt das FHIR Bundle als XML String zurück
     */
    public String toXml() {
        Bundle bundle = createFhirBundle();
        IParser parser = ctx.newXmlParser().setPrettyPrint(true);
        return parser.encodeResourceToString(bundle);
    }

    /**
     * Speichert das FHIR Bundle als JSON-Datei
     */
    public void saveToJsonFile(String filename) {
        try {
            java.nio.file.Files.write(
                    java.nio.file.Paths.get(filename),
                    toJson().getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );
            System.out.println("FHIR Bundle gespeichert: " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
