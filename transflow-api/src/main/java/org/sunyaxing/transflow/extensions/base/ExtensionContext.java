package org.sunyaxing.transflow.extensions.base;

import java.util.concurrent.BlockingDeque;

public record ExtensionContext(BlockingDeque<String> inputQueue) {
}
