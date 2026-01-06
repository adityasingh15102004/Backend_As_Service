package com.jobhunt.saas.serviceTest;

import com.jobhunt.saas.dto.SubscriptionResponse;
import com.jobhunt.saas.entity.Plan;
import com.jobhunt.saas.entity.Subscription;
import com.jobhunt.saas.entity.SubscriptionStatus;
import com.jobhunt.saas.repository.PlanRepo;
import com.jobhunt.saas.repository.SubscriptionRepo;
import com.jobhunt.saas.service.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepo subscriptionRepo;

    @Mock
    private PlanRepo planRepo;

    @InjectMocks
    private SubscriptionService subscriptionService;


    @Test
    public void getExceptionForInactiveSubscription() {

        Long  userId = 1L;

        when(subscriptionRepo.findByUserIdAndStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,() -> subscriptionService.getActiveSubscriptionForUser(1L));

    }

    @Test
    public void cancelSubscriptionTest(){

        Long  userId = 1L;

        SubscriptionService spyService= spy(subscriptionService);
        doReturn(userId).when(spyService).getCurrentUserId();

        when(subscriptionRepo.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE))
             .thenReturn(Optional.empty());

        assertThrows(
                IllegalStateException.class,
                () ->spyService.cancelSubscription()
        );

    }
    //Subscribe Test
    @Test
    public void subscribe_shouldThrowException_whenUserAlreadyActive(){

        Long userId = 1L;
        Long planId = 10L;

        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        SubscriptionService spyService= spy(subscriptionService);
        doReturn(userId).when(spyService).getCurrentUserId();
        doReturn(false).when(spyService).isExpired(subscription);

        when(subscriptionRepo.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));

        assertThrows(IllegalStateException.class,
                () -> spyService.subscribe(planId));

        verify(subscriptionRepo, never()).save(any());

    }

    //Check For Valid PlanId

    @Test
    public void subscribe_shouldThrowException_whenPlanNotFound(){
        Long userId = 1L;
        Long planId = 10L;

        SubscriptionService spyService= spy(subscriptionService);
        doReturn(userId).when(spyService).getCurrentUserId();

        when(subscriptionRepo.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE))
        .thenReturn(Optional.empty());

        when(planRepo.findById(planId)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> spyService.subscribe(planId));

        verify(subscriptionRepo, never()).save(any());

    }

    @Test
    public  void subscribe_shouldThrowException_whenPlanNotActive(){
        Long userId = 1L;
        Long planId = 10L;

        Plan plan = new Plan();
        plan.setId(planId);
        plan.setActive(false);

        SubscriptionService spyService= spy(subscriptionService);
        doReturn(userId).when(spyService).getCurrentUserId();

        when(subscriptionRepo.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE))
         .thenReturn(Optional.empty());

        when(planRepo.findById(planId)).thenReturn(Optional.of(plan));

        assertThrows(IllegalStateException.class,
                () -> spyService.subscribe(planId));

        verify(subscriptionRepo, never()).save(any());
    }

    @Test
    public void subscribe_shouldCreateSubscription_whenValid() {

        Long userId = 1L;
        Long planId = 10L;


        Plan plan = new Plan();
        plan.setId(planId);
        plan.setActive(true);
        plan.setDurationInDays(30);
        plan.setName("PRO");


        SubscriptionService spyService= spy(subscriptionService);
        doReturn(userId).when(spyService).getCurrentUserId();

        when(subscriptionRepo.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE))
        .thenReturn(Optional.empty());

        when(planRepo.findById(planId)).thenReturn(Optional.of(plan));

        SubscriptionResponse response = spyService.subscribe(planId);
        assertNotNull(response);
        assertEquals(planId, response.getPlanId());
        assertNotNull(response.getStartDate());
        assertNotNull(response.getEndDate());
        assertTrue(response.getEndDate().isAfter(response.getStartDate()));

        verify(subscriptionRepo).save(any(Subscription.class));

    }

 }
