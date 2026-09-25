package com.paimana.insight.model;
import jakarta.persistence.*;
@Entity @Table(name="historical_comparisons")
public class HistoricalComparison {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @ManyToOne Project project; @ManyToOne Project similarProject;
 Double similarityScore,costVariance,delayMonths;
 public HistoricalComparison(){} public HistoricalComparison(Project p,Project s,Double sim,Double cv,Double dm){project=p;similarProject=s;similarityScore=sim;costVariance=cv;delayMonths=dm;}
 public Project getSimilarProject(){return similarProject;} public Double getSimilarityScore(){return similarityScore;} public Double getCostVariance(){return costVariance;} public Double getDelayMonths(){return delayMonths;}
}
