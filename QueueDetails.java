public class QueueDetails {
    private String queueName;
    private long totalPendingMessages;
    private int numberOfConsumers;
    private long messagesEnqueued;
    private long messagesDequeued;

    // Getters and Setters

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public long getTotalPendingMessages() {
        return totalPendingMessages;
    }

    public void setTotalPendingMessages(long totalPendingMessages) {
        this.totalPendingMessages = totalPendingMessages;
    }

    public int getNumberOfConsumers() {
        return numberOfConsumers;
    }

    public void setNumberOfConsumers(int numberOfConsumers) {
        this.numberOfConsumers = numberOfConsumers;
    }

    public long getMessagesEnqueued() {
        return messagesEnqueued;
    }

    public void setMessagesEnqueued(long messagesEnqueued) {
        this.messagesEnqueued = messagesEnqueued;
    }

    public long getMessagesDequeued() {
        return messagesDequeued;
    }

    public void setMessagesDequeued(long messagesDequeued) {
        this.messagesDequeued = messagesDequeued;
    }
}
