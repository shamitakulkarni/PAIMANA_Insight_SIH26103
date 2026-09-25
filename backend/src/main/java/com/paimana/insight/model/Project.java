package com.paimana.insight.model;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.LocalDate;
@Entity @Table(name="projects")
public class Project {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @Column(unique=true,nullable=false) String projectCode; String projectName,ministry,sector,state,dataSource;
 BigDecimal originalCost,revisedCost,expenditure; LocalDate originalEndDate,revisedEndDate;
 Double physicalProgress; @Enumerated(EnumType.STRING) ProjectStatus status;
 public Project(){}
 public Long getId(){return id;} public String getProjectCode(){return projectCode;} public String getProjectName(){return projectName;}
 public String getMinistry(){return ministry;} public String getSector(){return sector;} public String getState(){return state;}
 public BigDecimal getOriginalCost(){return originalCost;} public BigDecimal getRevisedCost(){return revisedCost;}
 public BigDecimal getExpenditure(){return expenditure;} public LocalDate getOriginalEndDate(){return originalEndDate;}
 public LocalDate getRevisedEndDate(){return revisedEndDate;} public Double getPhysicalProgress(){return physicalProgress;}
 public ProjectStatus getStatus(){return status;} public String getDataSource(){return dataSource;}
 public void setProjectCode(String v){projectCode=v;} public void setProjectName(String v){projectName=v;}
 public void setMinistry(String v){ministry=v;} public void setSector(String v){sector=v;} public void setState(String v){state=v;}
 public void setOriginalCost(BigDecimal v){originalCost=v;} public void setRevisedCost(BigDecimal v){revisedCost=v;}
 public void setExpenditure(BigDecimal v){expenditure=v;} public void setOriginalEndDate(LocalDate v){originalEndDate=v;}
 public void setRevisedEndDate(LocalDate v){revisedEndDate=v;} public void setPhysicalProgress(Double v){physicalProgress=v;}
 public void setStatus(ProjectStatus v){status=v;} public void setDataSource(String v){dataSource=v;}
}
