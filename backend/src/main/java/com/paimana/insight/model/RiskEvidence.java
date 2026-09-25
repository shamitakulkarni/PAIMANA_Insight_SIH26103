package com.paimana.insight.model;
import jakarta.persistence.*;
@Entity @Table(name="risk_evidence")
public class RiskEvidence {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @ManyToOne Project project;
 String factorType; @Column(length=2000) String description; Double evidenceWeight; String historicalEvidenceRef;
 public RiskEvidence(){} public RiskEvidence(Project p,String t,String d,Double w,String r){project=p;factorType=t;description=d;evidenceWeight=w;historicalEvidenceRef=r;}
 public String getFactorType(){return factorType;} public String getDescription(){return description;} public Double getEvidenceWeight(){return evidenceWeight;} public String getHistoricalEvidenceRef(){return historicalEvidenceRef;}
}
