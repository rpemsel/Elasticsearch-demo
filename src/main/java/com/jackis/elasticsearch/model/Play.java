package com.jackis.elasticsearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Play {

    private String speaker;
    @JsonProperty("play_name")
    private String playName;
    @JsonProperty("line_id")
    private Integer lineId;
    @JsonProperty("speech_number")
    private Integer speechNumber;
    @JsonProperty("line_number")
    private String lineNumber;
    @JsonProperty("text_entry")
    private String textEntry;

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(final String speaker) {
        this.speaker = speaker;
    }

    public String getPlayName() {
        return playName;
    }

    public void setPlayName(final String playName) {
        this.playName = playName;
    }

    public Integer getLineId() {
        return lineId;
    }

    public void setLineId(final Integer lineId) {
        this.lineId = lineId;
    }

    public Integer getSpeechNumber() {
        return speechNumber;
    }

    public void setSpeechNumber(final Integer speechNumber) {
        this.speechNumber = speechNumber;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(final String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getTextEntry() {
        return textEntry;
    }

    public void setTextEntry(final String textEntry) {
        this.textEntry = textEntry;
    }
}
