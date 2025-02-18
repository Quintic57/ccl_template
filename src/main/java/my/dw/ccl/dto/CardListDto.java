package my.dw.ccl.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class CardListDto {

    private String header;
    private String bodyText;

}
