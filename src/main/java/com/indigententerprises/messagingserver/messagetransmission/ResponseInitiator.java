package com.indigententerprises.messagingserver.messagetransmission;

import com.indigententerprises.messagingartifacts.AppraisalResponse;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

public class ResponseInitiator {

    private final JmsTemplate jmsTemplate;

    public ResponseInitiator(final JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(
            final AppraisalResponse appraisalResponse,
            final String correlationId
    ) {
        jmsTemplate.convertAndSend(appraisalResponse, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws JMSException {
                message.setJMSCorrelationID(correlationId);
                return message;
            }
        });
    }
}
