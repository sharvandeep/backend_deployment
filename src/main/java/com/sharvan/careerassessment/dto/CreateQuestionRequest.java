package com.sharvan.careerassessment.dto;

public class CreateQuestionRequest {

    private String questionText;
    private String optiona;
    private String optionb;
    private String optionc;
    private String optiond;
    private String correctAnswer;

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getOptiona() { return optiona; }
    public void setOptiona(String optiona) { this.optiona = optiona; }

    public String getOptionb() { return optionb; }
    public void setOptionb(String optionb) { this.optionb = optionb; }

    public String getOptionc() { return optionc; }
    public void setOptionc(String optionc) { this.optionc = optionc; }

    public String getOptiond() { return optiond; }
    public void setOptiond(String optiond) { this.optiond = optiond; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
}
