package com.indigententerprises.messagingserver.configuration;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import com.indigententerprises.messagingserver.messagetransmission.RequestListener;
import com.indigententerprises.messagingserver.messagetransmission.ResponseInitiator;

import jakarta.jms.Queue;
import jakarta.jms.QueueConnectionFactory;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.rmi.Naming;

@Configuration
public class MessagingConfiguration {

    @Bean
    public InitialContext initialContext() throws NamingException {
        return new InitialContext();
    }

    @Bean
    public QueueConnectionFactory requesterConnectionFactory() throws NamingException {
        final Context context = initialContext();
        return (QueueConnectionFactory) context.lookup(
                "cn=requesterConnectionFactory,ou=JMSConnectionFactories,ou=Resources,dc=deliciouscottage,dc=com"
        );
    }

    @Bean
    public QueueConnectionFactory replierConnectionFactory() throws NamingException {
        final Context context = initialContext();
        return (QueueConnectionFactory) context.lookup(
                "cn=replierConnectionFactory,ou=JMSConnectionFactories,ou=Resources,dc=deliciouscottage,dc=com"
        );
    }

    @Bean
    public Queue requesterQueue() throws NamingException {
        final Context context = initialContext();
        return (Queue) context.lookup(
                "cn=requesterQueue,ou=JMSDestinations,ou=Resources,dc=deliciouscottage,dc=com"
        );
    }

    @Bean
    public Queue replierQueue() throws NamingException {
        final Context context = initialContext();
        return (Queue) context.lookup(
                "cn=replierQueue,ou=JMSDestinations,ou=Resources,dc=deliciouscottage,dc=com"
        );
    }

    @Bean
    public ResponseInitiator responseInitiator() throws NamingException {
        final ResponseInitiator result = new ResponseInitiator(jmsTemplate());
        return result;
    }

    @Bean
    public RequestListener messageListener() throws NamingException {
        return new RequestListener(responseInitiator(), messageConverter());
    }

    @Bean
    public DefaultMessageListenerContainer messageListenerContainer() throws NamingException {
        final DefaultMessageListenerContainer result = new DefaultMessageListenerContainer();
        result.setDestination(replierQueue());
        result.setConnectionFactory(replierConnectionFactory());
        result.setMessageListener(messageListener());
        return result;
    }

    @Bean
    public MessageConverter messageConverter() {
        final XmlMapper xmlMapper = new XmlMapper();
        final MappingJackson2MessageConverter result = new MappingJackson2MessageConverter();
        result.setTargetType(MessageType.TEXT);
        result.setTypeIdPropertyName("_type");
        result.setObjectMapper(xmlMapper);
        return result;
    }

    @Bean
    public JmsTemplate jmsTemplate() throws NamingException {
        JmsTemplate result = new JmsTemplate(requesterConnectionFactory());
        result.setMessageConverter(messageConverter());
        result.setDefaultDestination(requesterQueue());
        return result;
    }
}
