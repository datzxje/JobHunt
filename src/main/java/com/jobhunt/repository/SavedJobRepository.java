package com.jobhunt.repository;

import com.jobhunt.model.entity.Job;
import com.jobhunt.model.entity.SavedJob;
import com.jobhunt.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    boolean existsByUserAndJob(User user, Job job);
    
    Page<SavedJob> findByUser(User user, Pageable pageable);
    
    void deleteByUserAndJob(User user, Job job);
}