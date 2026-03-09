package com.realstate.habitar.domain.dtos.token;

import java.util.Date;

public record TokenInfo(String email, Date issueAtDate, Date expDate){}