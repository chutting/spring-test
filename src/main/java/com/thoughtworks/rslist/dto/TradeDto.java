package com.thoughtworks.rslist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trade")
public class TradeDto {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private int amount;

  private int rank;

  @ManyToOne
  @JoinColumn(name = "rsEventDto_id")
  private RsEventDto rsEventDto;

  public TradeDto(int amount, int rank, RsEventDto rsEventDto) {
    this.amount = amount;
    this.rank = rank;
    this.rsEventDto = rsEventDto;
  }
}
