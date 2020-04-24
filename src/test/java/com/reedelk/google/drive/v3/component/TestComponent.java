package com.reedelk.google.drive.v3.component;

import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;

public class TestComponent implements ProcessorSync {

    @Override
    public Message apply(FlowContext flowContext, Message message) {
        throw new UnsupportedOperationException("Test only component");
    }
}
