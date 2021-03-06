package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.Trade;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.TradeDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.dto.VoteDto;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.TradeRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class RsServiceTest {
  RsService rsService;

  @Mock RsEventRepository rsEventRepository;
  @Mock UserRepository userRepository;
  @Mock VoteRepository voteRepository;
  @Mock TradeRepository tradeRepository;
  LocalDateTime localDateTime;
  Vote vote;

  @BeforeEach
  void setUp() {
    initMocks(this);
    rsService = new RsService(rsEventRepository, userRepository, voteRepository, tradeRepository);
    localDateTime = LocalDateTime.now();
    vote = Vote.builder().voteNum(2).rsEventId(1).time(localDateTime).userId(1).build();
  }

  @Test
  void shouldVoteSuccess() {
    // given

    UserDto userDto =
        UserDto.builder()
            .voteNum(5)
            .phone("18888888888")
            .gender("female")
            .email("a@b.com")
            .age(19)
            .userName("xiaoli")
            .id(2)
            .build();
    RsEventDto rsEventDto =
        RsEventDto.builder()
            .eventName("event name")
            .id(1)
            .keyword("keyword")
            .voteNum(2)
            .user(userDto)
            .build();

    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.of(rsEventDto));
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userDto));
    // when
    rsService.vote(vote, 1);
    // then
    verify(voteRepository)
        .save(
            VoteDto.builder()
                .num(2)
                .localDateTime(localDateTime)
                .user(userDto)
                .rsEvent(rsEventDto)
                .build());
    verify(userRepository).save(userDto);
    verify(rsEventRepository).save(rsEventDto);
  }

  @Test
  void shouldThrowExceptionWhenUserNotExist() {
    // given
    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.empty());
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
    //when&then
    assertThrows(
        RuntimeException.class,
        () -> {
          rsService.vote(vote, 1);
        });
  }

  @Test
  void shouldBuySuccessWhenNoTrade() {
    UserDto userDto =
        UserDto.builder()
            .voteNum(5)
            .phone("18888888888")
            .gender("female")
            .email("a@b.com")
            .age(19)
            .userName("xiaoli")
            .id(2)
            .build();
    RsEventDto rsEventDto =
        RsEventDto.builder()
            .eventName("event name")
            .id(1)
            .keyword("keyword")
            .voteNum(2)
            .user(userDto)
            .build();

    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.of(rsEventDto));
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userDto));
    when(tradeRepository.findAllByRank(anyInt())).thenReturn(new ArrayList<>());

    rsService.buy(new Trade(1, 100, 1), 1);

    verify(tradeRepository).save(TradeDto
        .builder()
        .rank(1)
        .amount(100)
        .rsEventDto(rsEventDto)
        .build());
  }

  @Test
  void shouldFailWhenAmountIsNotLargeEnough() {
    UserDto userDto =
        UserDto.builder()
            .voteNum(5)
            .phone("18888888888")
            .gender("female")
            .email("a@b.com")
            .age(19)
            .userName("xiaoli")
            .id(2)
            .build();
    RsEventDto rsEventDto =
        RsEventDto.builder()
            .eventName("event name")
            .id(1)
            .keyword("keyword")
            .voteNum(2)
            .user(userDto)
            .build();

    RsEventDto rsEventDtoClone =
        RsEventDto.builder()
            .eventName("event name clone")
            .id(2)
            .keyword("keyword clone")
            .voteNum(2)
            .user(userDto)
            .build();

    TradeDto tradeDto = TradeDto.builder()
        .rank(1)
        .amount(1000)
        .rsEventDto(rsEventDto)
        .build();

    when(rsEventRepository.findById(1)).thenReturn(Optional.of(rsEventDto));
    when(rsEventRepository.findById(2)).thenReturn(Optional.of(rsEventDtoClone));
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userDto));
    when(tradeRepository.findAllByRank(1)).thenReturn(Arrays.asList(tradeDto));

    rsService.buy(new Trade(100, 1), 2);

    verify(tradeRepository, times(0)).save(TradeDto
        .builder()
        .rank(1)
        .amount(100)
        .rsEventDto(rsEventDtoClone)
        .build());
  }

  @Test
  void shouldSuccessWhenAmountLargeEnough() {
    UserDto userDto =
        UserDto.builder()
            .voteNum(5)
            .phone("18888888888")
            .gender("female")
            .email("a@b.com")
            .age(19)
            .userName("xiaoli")
            .id(2)
            .build();

    RsEventDto rsEventDto =
        RsEventDto.builder()
            .eventName("event name")
            .id(1)
            .keyword("keyword")
            .voteNum(2)
            .user(userDto)
            .build();

    RsEventDto rsEventDtoClone =
        RsEventDto.builder()
            .eventName("event name clone")
            .id(2)
            .keyword("keyword clone")
            .voteNum(2)
            .user(userDto)
            .build();

    TradeDto tradeDto = TradeDto.builder()
        .rank(1)
        .amount(100)
        .id(1)
        .rsEventDto(rsEventDto)
        .build();

    when(rsEventRepository.findById(1)).thenReturn(Optional.of(rsEventDto));
    when(rsEventRepository.findById(2)).thenReturn(Optional.of(rsEventDtoClone));
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userDto));
    when(tradeRepository.findAllByRank(1)).thenReturn(Arrays.asList(tradeDto));

    rsService.buy(new Trade(1000, 1), 2);

    verify(tradeRepository).save(TradeDto
        .builder()
        .rank(1)
        .amount(1000)
        .rsEventDto(rsEventDtoClone)
        .build());
  }

  @Test
  void shouldBuyFailWhenRsEventNotExist() {
    UserDto userDto =
        UserDto.builder()
            .voteNum(5)
            .phone("18888888888")
            .gender("female")
            .email("a@b.com")
            .age(19)
            .userName("xiaoli")
            .id(2)
            .build();

    RsEventDto rsEventDto =
        RsEventDto.builder()
            .eventName("event name")
            .id(1)
            .keyword("keyword")
            .voteNum(2)
            .user(userDto)
            .build();

    TradeDto tradeDto = TradeDto.builder()
        .rank(1)
        .amount(1000)
        .rsEventDto(rsEventDto)
        .build();

    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.empty());
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userDto));

    rsService.buy(new Trade(1000, 1), 1);

    verify(tradeRepository, times(0)).save(tradeDto);
  }

  @Test
  void shouldGetRsEsEventSorted() {
    UserDto userDto =
        UserDto.builder()
            .voteNum(5)
            .phone("18888888888")
            .gender("female")
            .email("a@b.com")
            .age(19)
            .userName("xiaoli")
            .id(2)
            .build();

    RsEventDto rsEventDto =
        RsEventDto.builder()
            .eventName("event name")
            .id(1)
            .keyword("keyword")
            .voteNum(3)
            .user(userDto)
            .build();

    RsEventDto rsEventDtoClone1 =
        RsEventDto.builder()
            .eventName("event name clone 1")
            .id(2)
            .keyword("keyword")
            .voteNum(5)
            .user(userDto)
            .build();

    RsEventDto rsEventDtoClone2 =
        RsEventDto.builder()
            .eventName("event name clone 2")
            .id(3)
            .keyword("keyword")
            .voteNum(2)
            .user(userDto)
            .build();

    TradeDto tradeDto = TradeDto.builder()
        .rank(1)
        .amount(100)
        .rsEventDto(rsEventDto)
        .build();

    TradeDto tradeDtoClone2 = TradeDto.builder()
        .rank(1)
        .amount(1000)
        .rsEventDto(rsEventDtoClone2)
        .build();

    when(rsEventRepository.findAll()).thenReturn(Arrays.asList(rsEventDto, rsEventDtoClone1, rsEventDtoClone2));

    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userDto));
    when(tradeRepository.findAllByRank(1)).thenReturn(Arrays.asList(tradeDto, tradeDtoClone2));

    List<RsEventDto> rsEventListByRank = rsService.getRsEventListByRank();

    assertEquals("event name clone 2", rsEventListByRank.get(0).getEventName());
    assertEquals("event name", rsEventListByRank.get(1).getEventName());
  }
}
