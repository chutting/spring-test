package com.thoughtworks.rslist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "research")
public class RsEventDto {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private int id;
  private String eventName;
  private String keyword;
  private int voteNum;
  @ManyToOne private UserDto user;
}
