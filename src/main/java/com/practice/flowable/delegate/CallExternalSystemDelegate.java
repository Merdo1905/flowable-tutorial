package com.practice.flowable.delegate;

import lombok.extern.java.Log;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

//Under Maintenance
@Log
public class CallExternalSystemDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Calling the external system for employee "
                + delegateExecution.getVariable("employee"));
    }
}
