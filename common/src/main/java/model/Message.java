package model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message implements Serializable {

    private String author;
    private String content;
    private long sendAt;


}