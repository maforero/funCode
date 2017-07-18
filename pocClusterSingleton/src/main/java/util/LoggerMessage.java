package util;


public enum LoggerMessage {


    /**
     * DEBUG MESSAGES
     */
    FEC10000("DEBUG - "),

    /**
     * INFO MESSAGES
     */
    FEC20000("INFO - Work is ready"),
    FEC20001("INFO - There's no work to be done "),
    FEC20002("INFO - Got work  :{} "),
    FEC20003("INFO - Requesting work"),
    FEC20004("INFO - Working on :{} "),
    FEC20005("INFO - Work completed to: {} "),
    FEC20006("INFO - Topic consumer client will be created"),
    FEC20008("INFO - Topic consumer client will be started"),
    FEC20009("INFO - Message consumed '{}'"),
    FEC20010("INFO - Topic consumer client will be stopped"),
    FEC20011("INFO - Begin of the process TopicConsumer.modifyOffSet "),
    FEC20012("INFO - Loaded property flight-event-consumer.health-check.maximum-processing-time: {}"),
    FEC20013("INFO - Loaded property flight-event-consumer.health-check.number-of-events: {}"),
    FEC20014("INFO - Loaded property flight-event-consumer.health-check.minimum-successful-percentage: {}"),
    FEC20017("INFO - Topic consumer client will modify the offSet"),
    FEC20018("INFO - Offset updated into kafka consumer {}"),
    FEC20019("INFO - Changing offset Service..."),
    FEC20020("INFO - SASI_HOST: {}, AGENT_NAME: {}."),
    FEC20021("INFO - SASI connection successfully started. Status: {}."),
    FEC20022("INFO - SASI connection successfully finished. Status: {}."),

    /**
     * INITIALIZATION MESSAGES
     * */
    FEC20007("INFO - SpringProfileInitializer initialize"),
    FEC20015("INFO - Starting {} Service..."),
    FEC20016("INFO - Stopping {} Service..."),

    /**
     * WARNING MESSAGES
     */


    /**
     * ERROR MESSAGES
     */
    FEC40000("ERROR - Flight Transformation has failed. Event id # {}"),
    FEC40001("ERROR - Wrong Flight Regulation Type - {} "),
    FEC40002("ERROR - Unsupported Operation Code: {}"),
    FEC40003("ERROR - Unsupported Operation SubCode: {}"),
    FEC40004("ERROR - The current worker is busy"),
    FEC40005("ERROR - Failed to obtain moniker: {}"),
    FEC40006("ERROR - There was an error consuming from kafka {}"),
    FEC40007("ERROR - Message could not be transformed - {} {}"),
    FEC40008("ERROR - Flight Transformation has failed. Event id # {}"),
    FEC40009("ERROR - Loading the property flight-event-consumer.health-check.minimum-successful-percentage."),
    FEC40010("ERROR - Loading the property flight-event-consumer.health-check.maximum-processing-time."),
    FEC40011("ERROR - Loading the property flight-event-consumer.health-check.number-of-events."),
    FEC40012("ERROR - Modifying offset "),;

    /**
     * FATAL MESSAGES
     */

    private String message;

    LoggerMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new StringBuilder(this.name()).append(" - ").append(message).toString();
    }
}