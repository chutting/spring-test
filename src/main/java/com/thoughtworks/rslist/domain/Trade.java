package com.thoughtworks.rslist.domain;

import com.thoughtworks.rslist.dto.RsEventDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Trade {
  private int id;
  private int amount;
  private int rank;
  private RsEventDto rsEventDto;

  public Trade(int amount, int rank, RsEventDto rsEventDto) {
    this.amount = amount;
    this.rank = rank;
    this.rsEventDto = rsEventDto;
  }
}
