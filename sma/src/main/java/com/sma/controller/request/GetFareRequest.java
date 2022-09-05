package com.sma.controller.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetFareRequest {
	private String fromLine;
	private String toLine;
	private LocalDateTime travelDate;
}
