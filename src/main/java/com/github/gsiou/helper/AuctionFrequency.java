package com.github.gsiou.helper;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AuctionFrequency{
	private final int frequency;
	private final int auctionId;
}
