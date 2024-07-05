import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.*;

@Service
public class QueueService {

    @Autowired
    private JmsTemplate jmsTemplate;

    public String deleteMessage(String queueName) {
        return jmsTemplate.execute(session -> {
            Queue queue = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(queue);
            Message message = consumer.receiveNoWait();
            
            if (message != null) {
                try {
                    String messageId = message.getJMSMessageID();
                    message.acknowledge(); // Acknowledge the message to remove it from the queue
                    return "Message deleted: " + messageId;
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return "No message available in the queue.";
            }
        }, true);
    }
}
