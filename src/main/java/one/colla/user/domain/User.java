package one.colla.user.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.chat.domain.ChatChannelMessage;
import one.colla.chat.domain.UserChatChannel;
import one.colla.common.domain.BaseEntity;
import one.colla.feed.collect.domain.CollectFeedResponse;
import one.colla.feed.common.domain.Comment;
import one.colla.feed.common.domain.Feed;
import one.colla.feed.scheduling.domain.SchedulingFeedAvailableTime;
import one.colla.feed.vote.domain.VoteFeedSelection;
import one.colla.file.domain.Attachment;
import one.colla.schedule.domain.CalendarEventSubtodo;
import one.colla.schedule.domain.UserCalendarEvent;
import one.colla.schedule.domain.UserCalendarEventMention;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.vo.Email;
import one.colla.user.domain.vo.Password;
import one.colla.user.domain.vo.ProfileImageUrl;
import one.colla.user.domain.vo.Username;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Embedded
	private Username username;

	@Embedded
	private Password password;

	@Embedded
	private Email email;

	@Column(name = "email_subscription", nullable = false)
	private boolean emailSubscription = true;

	@Embedded
	private ProfileImageUrl profileImageUrl;

	@Column(name = "comment_notification", nullable = false)
	@Enumerated(EnumType.STRING)
	private CommentNotification commentNotification;

	private User(Username username, Password password, Email email) {
		this.role = Role.USER;
		this.username = username;
		this.password = password;
		this.email = email;
		this.commentNotification = CommentNotification.ALL;
		this.profileImageUrl = new ProfileImageUrl();
	}

	public static User createGeneralUser(String createUsername, String createPassword, String createEmail) {
		Username username = Username.from(createUsername);
		Password password = Password.from(createPassword);
		Email email = Email.from(createEmail);
		return new User(username, password, email);
	}

	public static User createSocialUser(String createUsername, String createEmail) {
		Username username = Username.from(createUsername);
		Email email = Email.from(createEmail);
		return new User(username, null, email);
	}

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<OauthApproval> oauthApprovals = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<UserTeamspace> userTeamspaces = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<UserChatChannel> userChatChannels = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<ChatChannelMessage> chatChannelMessages = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<UserCalendarEventMention> userCalendarEventMentions = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<UserCalendarEvent> userCalendarEvents = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<CalendarEventSubtodo> calendarEventSubtodos = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<Attachment> attachments = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<Feed> feeds = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<Comment> comments = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<CollectFeedResponse> collectFeedResponses = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<VoteFeedSelection> voteFeedSelections = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<SchedulingFeedAvailableTime> schedulingFeedAvailableTimes = new ArrayList<>();

	public String getUsernameValue() {
		return username.getValue();
	}

	public String getEmailValue() {
		return email.getValue();
	}

	public String getPasswordValue() {
		return password.getValue();
	}

}