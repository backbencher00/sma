package com.sma.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmaApiResponse {
	String success;
	String successCode;
	String message;
	Integer fare;
}
