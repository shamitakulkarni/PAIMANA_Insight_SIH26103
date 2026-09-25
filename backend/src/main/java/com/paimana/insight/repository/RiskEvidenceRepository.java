package com.paimana.insight.repository;
import com.paimana.insight.model.*; import java.util.List; import org.springframework.data.jpa.repository.JpaRepository;
public interface RiskEvidenceRepository extends JpaRepository<RiskEvidence,Long>{ List<RiskEvidence> findByProject(Project project); }
