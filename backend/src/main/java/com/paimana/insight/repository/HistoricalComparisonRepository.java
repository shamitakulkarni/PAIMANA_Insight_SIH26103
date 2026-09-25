package com.paimana.insight.repository;
import com.paimana.insight.model.*; import java.util.List; import org.springframework.data.jpa.repository.JpaRepository;
public interface HistoricalComparisonRepository extends JpaRepository<HistoricalComparison,Long>{ List<HistoricalComparison> findByProject(Project project); }
