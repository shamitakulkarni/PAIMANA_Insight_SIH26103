package com.paimana.insight.repository;
import com.paimana.insight.model.Project; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository;
public interface ProjectRepository extends JpaRepository<Project,Long>{ Optional<Project> findByProjectCode(String projectCode); }
