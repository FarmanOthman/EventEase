package server.notification;

import java.util.Date;
import java.util.UUID;

/**
 * Represents a notification entity in the system.
 */
public class Notification {
  private String id;
  private String userId;
  private String message;
  private NotificationType type;
  private Date timestamp;
  private boolean read;
  private String relatedEntityId; // ID of related entity (event, booking, etc.)

  /**
   * Default constructor
   */
  public Notification() {
    this.id = UUID.randomUUID().toString();
    this.timestamp = new Date();
    this.read = false;
  }

  /**
   * Constructor with all required fields
   * 
   * @param userId  The ID of the user the notification is for
   * @param message The notification message
   * @param type    The type of notification
   */
  public Notification(String userId, String message, NotificationType type) {
    this();
    this.userId = userId;
    this.message = message;
    this.type = type;
  }

  /**
   * Constructor with all fields
   * 
   * @param userId          The ID of the user the notification is for
   * @param message         The notification message
   * @param type            The type of notification
   * @param relatedEntityId Optional ID of the related entity
   */
  public Notification(String userId, String message, NotificationType type, String relatedEntityId) {
    this(userId, message, type);
    this.relatedEntityId = relatedEntityId;
  }

  // Getters and setters

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public NotificationType getType() {
    return type;
  }

  public void setType(NotificationType type) {
    this.type = type;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
    this.read = read;
  }

  public String getRelatedEntityId() {
    return relatedEntityId;
  }

  public void setRelatedEntityId(String relatedEntityId) {
    this.relatedEntityId = relatedEntityId;
  }

  @Override
  public String toString() {
    return "Notification{" +
        "id='" + id + '\'' +
        ", userId='" + userId + '\'' +
        ", message='" + message + '\'' +
        ", type=" + type +
        ", timestamp=" + timestamp +
        ", read=" + read +
        ", relatedEntityId='" + relatedEntityId + '\'' +
        '}';
  }
}