import org.springframework.stereotype.Service;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class JmxQueueService {

    private static final String JMX_URL = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
    private static final String BROKER_NAME = "localhost"; // Change this to your broker name

    private MBeanServerConnection getConnection() throws Exception {
        JMXServiceURL url = new JMXServiceURL(JMX_URL);
        JMXConnector connector = JMXConnectorFactory.connect(url, null);
        return connector.getMBeanServerConnection();
    }

    public String deleteMessage(String queueName, String messageId) {
        try {
            MBeanServerConnection connection = getConnection();
            ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + BROKER_NAME + ",destinationType=Queue,destinationName=" + queueName);
            Set<ObjectName> queueSet = connection.queryNames(query, null);

            for (ObjectName queueObjectName : queueSet) {
                Object[] params = {};
                String[] signature = {};
                Object[] messages = (Object[]) connection.invoke(queueObjectName, "browse", params, signature);

                for (Object msg : messages) {
                    Message message = (Message) msg;
                    if (message != null && messageId.equals(message.getJMSMessageID())) {
                        connection.invoke(queueObjectName, "removeMessage", new Object[]{messageId}, new String[]{"java.lang.String"});
                        return "Message deleted: " + messageId;
                    }
                }
            }
            return "Message not found: " + messageId;

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to delete message from queue: " + queueName;
        }
    }

    public List<String> getAllQueueNames() {
        try {
            MBeanServerConnection connection = getConnection();
            ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + BROKER_NAME + ",destinationType=Queue,*");
            Set<ObjectName> queueSet = connection.queryNames(query, null);

            List<String> queueNames = new ArrayList<>();
            for (ObjectName queueObjectName : queueSet) {
                String queueName = queueObjectName.getKeyProperty("destinationName");
                queueNames.add(queueName);
            }
            return queueNames;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean checkIfMessageExistsInQueue(String queueName, String messageContent) {
        try {
            MBeanServerConnection connection = getConnection();
            ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + BROKER_NAME + ",destinationType=Queue,destinationName=" + queueName);
            Set<ObjectName> queueSet = connection.queryNames(query, null);

            for (ObjectName queueObjectName : queueSet) {
                Object[] params = {};
                String[] signature = {};
                Object[] messages = (Object[]) connection.invoke(queueObjectName, "browse", params, signature);

                for (Object msg : messages) {
                    if (msg instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) msg;
                        if (messageContent.equals(textMessage.getText())) {
                            return true;
                        }
                    }
                }
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getAllMessagesInQueue(String queueName) {
        try {
            MBeanServerConnection connection = getConnection();
            ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + BROKER_NAME + ",destinationType=Queue,destinationName=" + queueName);
            Set<ObjectName> queueSet = connection.queryNames(query, null);

            List<String> messagesList = new ArrayList<>();
            for (ObjectName queueObjectName : queueSet) {
                Object[] params = {};
                String[] signature = {};
                Object[] messages = (Object[]) connection.invoke(queueObjectName, "browse", params, signature);

                for (Object msg : messages) {
                    if (msg instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) msg;
                        messagesList.add(textMessage.getText());
                    }
                }
            }
            return messagesList;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

      public int getTotalQueueCount() {
        try {
            MBeanServerConnection connection = getConnection();
            ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + BROKER_NAME + ",destinationType=Queue,*");
            Set<ObjectName> queueSet = connection.queryNames(query, null);
            return queueSet.size();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getTotalTopicCount() {
        try {
            MBeanServerConnection connection = getConnection();
            ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + BROKER_NAME + ",destinationType=Topic,*");
            Set<ObjectName> topicSet = connection.queryNames(query, null);
            return topicSet.size();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getTotalConnectionCount() {
        try {
            MBeanServerConnection connection = getConnection();
            ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + BROKER_NAME + ",connector=clientConnectors,connectorName=*,connectionViewType=clientId,*");
            Set<ObjectName> connectionSet = connection.queryNames(query, null);
            return connectionSet.size();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getTotalSubscriberCount() {
        try {
            MBeanServerConnection connection = getConnection();
            ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + BROKER_NAME + ",destinationType=Topic,*");
            Set<ObjectName> topicSet = connection.queryNames(query, null);
            int subscriberCount = 0;

            for (ObjectName topicObjectName : topicSet) {
                subscriberCount += (Integer) connection.getAttribute(topicObjectName, "ConsumerCount");
            }

            return subscriberCount;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<QueueDetails> getAllQueueDetails() {
        try {
            MBeanServerConnection connection = getConnection();
            ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + BROKER_NAME + ",destinationType=Queue,*");
            Set<ObjectName> queueSet = connection.queryNames(query, null);

            List<QueueDetails> queueDetailsList = new ArrayList<>();
            for (ObjectName queueObjectName : queueSet) {
                QueueDetails details = new QueueDetails();
                details.setQueueName(queueObjectName.getKeyProperty("destinationName"));
                details.setTotalPendingMessages((Long) connection.getAttribute(queueObjectName, "QueueSize"));
                details.setNumberOfConsumers((Integer) connection.getAttribute(queueObjectName, "ConsumerCount"));
                details.setMessagesEnqueued((Long) connection.getAttribute(queueObjectName, "EnqueueCount"));
                details.setMessagesDequeued((Long) connection.getAttribute(queueObjectName, "DequeueCount"));
                queueDetailsList.add(details);
            }

            return queueDetailsList;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
