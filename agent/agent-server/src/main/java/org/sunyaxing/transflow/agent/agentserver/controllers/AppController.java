package org.sunyaxing.transflow.agent.agentserver.controllers;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/app")
public class AppController {
    private static final Logger logger = LoggerFactory.getLogger(AppController.class);

    @GetMapping("/list")
    public List<VirtualMachineDescriptor> list() {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        return list;
    }

    @GetMapping("/action/loadAgent")
    public String loadAgent() {
        String name = "org.sunyaxing.transflow.transflowapp.TransflowAppApplication";
        VirtualMachine.list()
                .stream()
                .filter(virtualMachineDescriptor -> virtualMachineDescriptor.displayName().equals(name))
                .findFirst()
                .ifPresent(virtualMachineDescriptor -> {
                    logger.info("attach to {} : {}", virtualMachineDescriptor.id(), virtualMachineDescriptor.displayName());
                    try {
                        VirtualMachine virtualMachine = VirtualMachine.attach(virtualMachineDescriptor.id());
                        //virtualMachine.loadAgent("C:\\Users\\syx19\\.m2\\repository\\net\\bytebuddy\\byte-buddy-agent\\1.11.22\\byte-buddy-agent-1.11.22.jar");
                        virtualMachine.loadAgent("D:\\workspace\\transflow\\agent\\agent-plugin\\target\\agent-plugin-1.0.jar");
                        virtualMachine.detach();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        return "ok";
    }
}
