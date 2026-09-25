package com.paimana.insight.service;
import com.paimana.insight.dto.DTOs.*; import com.paimana.insight.model.*; import com.paimana.insight.repository.*; import org.springframework.stereotype.Service; import java.util.List;
@Service public class RiskService {
 ProjectRepository projects; RiskEvidenceRepository evidence; HistoricalComparisonRepository comparisons; MlClient ml;
 public RiskService(ProjectRepository p,RiskEvidenceRepository e,HistoricalComparisonRepository c,MlClient m){projects=p;evidence=e;comparisons=c;ml=m;}
 public Risk analyze(String code){Project p=projects.findByProjectCode(code).orElseThrow();MlClient.Prediction x=ml.predict(p);
  if(evidence.findByProject(p).isEmpty()){double inf=p.getOriginalCost()!=null&&p.getRevisedCost()!=null?p.getRevisedCost().subtract(p.getOriginalCost()).doubleValue()/p.getOriginalCost().doubleValue()*100:0;evidence.save(new RiskEvidence(p,"COST","Revised cost is approximately "+String.format("%.1f",inf)+"% above original cost.",Math.min(1,Math.max(0,inf/50)),"Cost revision history"));evidence.save(new RiskEvidence(p,"PROGRESS","Physical progress is "+p.getPhysicalProgress()+"%.",Math.max(0,1-(p.getPhysicalProgress()==null?0:p.getPhysicalProgress()/100)),"Current progress"));}
  List<Evidence> es=evidence.findByProject(p).stream().map(e->new Evidence(e.getFactorType(),e.getDescription(),e.getEvidenceWeight(),e.getHistoricalEvidenceRef())).toList();return new Risk(code,x.costRisk(),x.delayRisk(),x.overallRisk(),x.modelName(),x.modelVersion(),es);}
 public List<HistoricalComparison> comparisons(String code){return comparisons.findByProject(projects.findByProjectCode(code).orElseThrow());}
}
