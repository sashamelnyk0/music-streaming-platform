package com.music.user.dto;

import com.music.user.model.SubscriptionType;
import lombok.Data;

@Data
public class UpdateSubscriptionDto {
    private SubscriptionType subscription;
}