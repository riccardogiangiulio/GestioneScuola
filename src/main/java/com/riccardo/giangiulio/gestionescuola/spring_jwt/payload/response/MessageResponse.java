package com.riccardo.giangiulio.gestionescuola.spring_jwt.payload.response;

public class MessageResponse {
  private String message;

  public MessageResponse() {

  }

  public MessageResponse(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  } 
}
