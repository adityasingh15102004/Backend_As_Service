package com.jobhunt.saas.service;

import com.jobhunt.saas.annotations.RequireActiveSubscription;
import com.jobhunt.saas.entity.Project;
import com.jobhunt.saas.entity.ProjectStatus;
import com.jobhunt.saas.entity.Subscription;
import com.jobhunt.saas.repository.ProjectRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class ProjectService {

    @Autowired
   private  ProjectRepo projectRepo;

    @Autowired
    private SubscriptionService subscriptionService;


    @RequireActiveSubscription
    public void createProject(String projectName){

      Long ownerUserId=subscriptionService.getCurrentUserId();

       enforceProjectLimit(ownerUserId);

        Project project = new Project();
        project.setName(projectName);
        project.setOwnerUserId(ownerUserId);
        project.setCreatedDate(LocalDateTime.now());
        project.setStatus(ProjectStatus.ACTIVE);

        projectRepo.save(project);
    }

    public void enforceProjectLimit(Long ownerUserId)
    {

       //Then Got The Subscription
        Subscription subscription=subscriptionService.getActiveSubscriptionForUser(ownerUserId);

        String planName=subscription.getPlan().getName();

        Long currentCount=
                projectRepo.countByOwnerUserIdAndStatus(ownerUserId, ProjectStatus.ACTIVE);

        switch(planName) {

            case "FREE" -> {
                if(currentCount >=1){
                    throw new IllegalStateException("FREE plan allows only 1 project");
                }
            }
            case "BASIC" -> {
                if(currentCount >=5){
                    throw new IllegalStateException("Basic plan allows Maximum 5 project");
                }
            }

            case "PRO" ->{
                System.out.println("Unlimited");
            }

            default -> {
                throw new IllegalStateException("Unknown plan");
            }

        }

    }
}

