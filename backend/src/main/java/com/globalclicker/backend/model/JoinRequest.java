package com.globalclicker.backend.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
// A class to wrap id and country of
// a new joining device (esp32)
public class JoinRequest {
    private String id;
    private String country;
}
