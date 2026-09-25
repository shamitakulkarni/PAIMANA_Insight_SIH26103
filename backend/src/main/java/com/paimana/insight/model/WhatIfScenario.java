package com.paimana.insight.model;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.LocalDate;
@Entity @Table(name="what_if_scenarios")
public class WhatIfScenario {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @ManyToOne Project project;
 String scenarioName,predictedCostRisk,predictedDelayRisk; BigDecimal testCost; LocalDate testCompletionDate;
 public WhatIfScenario(){} public WhatIfScenario(Project p,String n,BigDecimal c,LocalDate d,String cr,String dr){project=p;scenarioName=n;testCost=c;testCompletionDate=d;predictedCostRisk=cr;predictedDelayRisk=dr;}
 public String getScenarioName(){return scenarioName;} public BigDecimal getTestCost(){return testCost;} public LocalDate getTestCompletionDate(){return testCompletionDate;}
 public String getPredictedCostRisk(){return predictedCostRisk;} public String getPredictedDelayRisk(){return predictedDelayRisk;}
}
