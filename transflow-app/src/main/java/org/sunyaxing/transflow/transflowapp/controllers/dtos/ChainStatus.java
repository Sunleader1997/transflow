package org.sunyaxing.transflow.transflowapp.controllers.dtos;

import lombok.Data;

@Data
public class ChainStatus {
    private Long remainNumb;
    private Long recNumb;
    private Long sendNumb;

    public ChainStatus() {
        this.remainNumb = 0L;
        this.recNumb = 0L;
        this.sendNumb = 0L;
    }
}
