package com.paimana.insight.model;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="risk_results")
public class RiskResult {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @ManyToOne(optional=false) Project project;
 String costRisk,delayRisk,overallRisk,modelName,modelVersion;
 LocalDateTime predictionDate=LocalDateTime.now();
 public RiskResult(){} public RiskResult(Project p,String c,String d,String o,String n,String v){project=p;costRisk=c;delayRisk=d;overallRisk=o;modelName=n;modelVersion=v;}
 public Long getId(){return id;} public Project getProject(){return project;} public String getCostRisk(){return costRisk;}
 public String getDelayRisk(){return delayRisk;} public String getOverallRisk(){return overallRisk;} public String getModelName(){return modelName;}
 public String getModelVersion(){return modelVersion;} public LocalDateTime getPredictionDate(){return predictionDate;}
}
