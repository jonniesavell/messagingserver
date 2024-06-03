package com.indigententerprises.messagingserver.messagetransmission;

import com.indigententerprises.messagingartifacts.AppraisalRequest;
import com.indigententerprises.messagingartifacts.AppraisalResponse;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

import org.springframework.jms.support.converter.MessageConverter;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class RequestListener implements MessageListener {

    private final ResponseInitiator responseInitiator;
    private final MessageConverter messageConverter;

    public RequestListener(
            final ResponseInitiator responseInitiator,
            final MessageConverter messageConverter
    ) {
        this.responseInitiator = responseInitiator;
        this.messageConverter = messageConverter;
    }

    @Override
    public void onMessage(final Message message) {
        try {
            // simulated business logic
            final AppraisalRequest appraisalRequest = (AppraisalRequest) messageConverter.fromMessage(message);
            final String dateString = ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_DATE_TIME);
            final String itemName = appraisalRequest.getItemName();
            Long value = new Random().nextLong();

            if (value < 0) {
                value *= -1;
            }

            final AppraisalResponse appraisalResponse = new AppraisalResponse();
            appraisalResponse.setValue(value);
            appraisalResponse.setDateString(dateString);
            appraisalResponse.setItemName(itemName);
            responseInitiator.sendMessage(appraisalResponse, message.getJMSMessageID());
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
