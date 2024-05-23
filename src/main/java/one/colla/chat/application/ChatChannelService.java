package one.colla.chat.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.chat.application.dto.request.CreateChatChannelRequest;
import one.colla.chat.application.dto.response.CreateChatChannelResponse;
import one.colla.chat.domain.ChatChannel;
import one.colla.chat.domain.ChatChannelRepository;
import one.colla.chat.domain.UserChatChannel;
import one.colla.chat.domain.UserChatChannelRepository;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.Teamspace;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatChannelService {

	private final TeamspaceService teamspaceService;
	private final ChatChannelRepository chatChannelRepository;
	private final UserChatChannelRepository userChatChannelRepository;

	@Transactional
	public CreateChatChannelResponse createChatChannel(
		CustomUserDetails userDetails,
		Long teamspaceId,
		CreateChatChannelRequest request) {

		validateParticipationTeamspace(userDetails, teamspaceId);

		final Teamspace teamspace = teamspaceService.getTeamspace(teamspaceId);
		final ChatChannel createdChatChannel = chatChannelRepository.save(
			ChatChannel.of(teamspace, request.channelName()));
		final List<UserChatChannel> participants = createdChatChannel.participateAllTeamspaceUser(
			teamspace.getUserTeamspaces());

		userChatChannelRepository.saveAll(participants);

		log.info("채팅 채널 생성 - 팀스페이스 Id: {}, 생성한 사용자 Id: {}", createdChatChannel.getId(), userDetails.getUserId());
		return CreateChatChannelResponse.from(createdChatChannel);
	}

	private void validateParticipationTeamspace(CustomUserDetails userDetails, Long teamspaceId) {
		teamspaceService.getUserTeamspace(userDetails, teamspaceId);
	}
}
