package com.alex_star.systemofsearch.dto.response;

public class FalseResultResponseDto implements ResultResponseDto {

    private final String error;

    public FalseResultResponseDto(String error) {
        this.error = error;
    }

    @Override
    public boolean getResult() {
        return false;
    }

    public String getError() {
        return error;
    }
}
