/*
 * Copyright 2015, Liberty Mutual Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lmig.forge.stash.ssh.scheduler;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.lmig.forge.stash.ssh.keys.EnterpriseSshKeyService;


public class KeyRotationScheduler implements DisposableBean, InitializingBean { 

     private static final JobId JOB_ID = JobId.of("com.edwardawebb.stash:stash-ssh-key-enforcer:KeyRotationJob"); 
     private static final long JOB_INTERVAL = TimeUnit.DAYS.toMillis(1L); //TODO runs daily, add config
     //private static final long JOB_INTERVAL = TimeUnit.MINUTES.toMillis(1L);
     private static final String JOB_RUNNER_KEY = "com.edwardawebb.stash:stash-ssh-key-enforcer:KeyRotationJobRunner"; 
     private static final Logger log = LoggerFactory.getLogger(KeyRotationScheduler.class);
     
     private final SchedulerService schedulerService;
     private final EnterpriseSshKeyService enterpriseKeyService; 
     
     public KeyRotationScheduler(SchedulerService schedulerService,EnterpriseSshKeyService enterpriseKeyService) { 
         this.schedulerService = schedulerService; 
         this.enterpriseKeyService = enterpriseKeyService;
     } 

     @Override 
     public void afterPropertiesSet() throws SchedulerServiceException { 
         //The JobRunner could be another component injected in the constructor, a 
         //private nested class, etc. It just needs to implement JobRunner 
         schedulerService.registerJobRunner(JobRunnerKey.of(JOB_RUNNER_KEY), new KeyRotationJobRunner(enterpriseKeyService)); 
         schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey(JobRunnerKey.of(JOB_RUNNER_KEY)) 
                 .withRunMode(RunMode.RUN_ONCE_PER_CLUSTER) 
                 .withSchedule(Schedule.forInterval(JOB_INTERVAL, new Date(System.currentTimeMillis() + JOB_INTERVAL)))); 
         log.warn("KEY Expiring Job Scheduled");
     } 

     @Override 
     public void destroy() { 
         schedulerService.unregisterJobRunner(JobRunnerKey.of(JOB_RUNNER_KEY)); 
     } 
 }