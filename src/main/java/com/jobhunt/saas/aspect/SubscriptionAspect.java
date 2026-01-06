package com.jobhunt.saas.aspect;

import com.jobhunt.saas.service.SubscriptionService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class SubscriptionAspect {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionAspect(SubscriptionService subscriptionService){
        this.subscriptionService=subscriptionService;
    }

    //This Is Advice
    @Before("@annotation(com.jobhunt.saas.annotations.RequiredActiveSubscription)")
    public void checkActiveSubscription() {
        subscriptionService.ensureActiveSubscription();
    }


}
