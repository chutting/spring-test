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
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RsService {
  final RsEventRepository rsEventRepository;
  final UserRepository userRepository;
  final VoteRepository voteRepository;
  final TradeRepository tradeRepository;

  public RsService(RsEventRepository rsEventRepository, UserRepository userRepository, VoteRepository voteRepository, TradeRepository tradeRepository) {
    this.rsEventRepository = rsEventRepository;
    this.userRepository = userRepository;
    this.voteRepository = voteRepository;
    this.tradeRepository = tradeRepository;
  }

  public void vote(Vote vote, int rsEventId) {
    Optional<RsEventDto> rsEventDto = rsEventRepository.findById(rsEventId);
    Optional<UserDto> userDto = userRepository.findById(vote.getUserId());
    if (!rsEventDto.isPresent()
        || !userDto.isPresent()
        || vote.getVoteNum() > userDto.get().getVoteNum()) {
      throw new RuntimeException();
    }
    VoteDto voteDto =
        VoteDto.builder()
            .localDateTime(vote.getTime())
            .num(vote.getVoteNum())
            .rsEvent(rsEventDto.get())
            .user(userDto.get())
            .build();
    voteRepository.save(voteDto);
    UserDto user = userDto.get();
    user.setVoteNum(user.getVoteNum() - vote.getVoteNum());
    userRepository.save(user);
    RsEventDto rsEvent = rsEventDto.get();
    rsEvent.setVoteNum(rsEvent.getVoteNum() + vote.getVoteNum());
    rsEventRepository.save(rsEvent);
  }

  public boolean buy(Trade trade, int id) {
    int rank = trade.getRank();
    int amount = trade.getAmount();

    List<TradeDto> allTradeByRank = tradeRepository.findAllByRank(rank);

    if (allTradeByRank.size() != 0) {
      TradeDto tradeDtoWithLargestAmount = allTradeByRank.stream()
          .collect(Collectors.maxBy(Comparator.comparingInt(TradeDto::getAmount)))
          .get();
      int largestAmount = tradeDtoWithLargestAmount.getAmount();

      if (largestAmount > amount) {
        return false;
      }
    }

    Optional<RsEventDto> rsEventDtoOptional = rsEventRepository.findById(id);
    if (!rsEventDtoOptional.isPresent()) {
      throw new RuntimeException();
    }

    RsEventDto rsEventDto = rsEventDtoOptional.get();

    TradeDto tradeDto = TradeDto
        .builder()
        .amount(amount)
        .rank(rank)
        .rsEventDto(rsEventDto)
        .build();

    tradeRepository.save(tradeDto);

    return true;
  }

  public RsEventDto findRsEventWithLargestAmountByRank(int rank) {
    List<TradeDto> allTrades = tradeRepository.findAllByRank(rank);
    if (allTrades.size() == 0) {
      throw new RuntimeException();
    }

    TradeDto tradeDtoWithLargestAmount = allTrades.stream()
        .collect(Collectors.maxBy(Comparator.comparingInt(TradeDto::getAmount)))
        .get();
    return tradeDtoWithLargestAmount.getRsEventDto();
  }
}
